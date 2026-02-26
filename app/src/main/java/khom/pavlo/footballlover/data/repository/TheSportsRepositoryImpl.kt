package khom.pavlo.footballlover.data.repository

import khom.pavlo.footballlover.data.local.AppDao
import khom.pavlo.footballlover.data.local.FavoriteLeagueEntity
import khom.pavlo.footballlover.data.local.SearchQueryEntity
import khom.pavlo.footballlover.data.mapper.toDomain as teamDtoToDomain
import khom.pavlo.footballlover.data.mapper.toEntity as teamToEntity
import khom.pavlo.footballlover.data.mapper.toDomain as teamEntityToDomain
import khom.pavlo.footballlover.data.mapper.toDomain as eventDtoToDomain
import khom.pavlo.footballlover.data.mapper.toEntity as eventToEntity
import khom.pavlo.footballlover.data.mapper.toDomain as eventEntityToDomain
import khom.pavlo.footballlover.data.mapper.toDomain as leagueDtoToDomain
import khom.pavlo.footballlover.data.mapper.toEntity as leagueToEntity
import khom.pavlo.footballlover.data.mapper.toDomain as leagueEntityToDomain
import khom.pavlo.footballlover.data.mapper.toDomain as standingDtoToDomain
import khom.pavlo.footballlover.data.mapper.toEntity as standingToEntity
import khom.pavlo.footballlover.data.mapper.toDomain as standingEntityToDomain
import khom.pavlo.footballlover.data.remote.TheSportsDbApi
import khom.pavlo.footballlover.domain.model.Event
import khom.pavlo.footballlover.domain.model.FavoriteLeague
import khom.pavlo.footballlover.domain.model.League
import khom.pavlo.footballlover.domain.model.StandingRow
import khom.pavlo.footballlover.domain.model.Team
import khom.pavlo.footballlover.domain.repository.TheSportsRepository
import khom.pavlo.footballlover.domain.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class TheSportsRepositoryImpl(
    private val api: TheSportsDbApi,
    private val dao: AppDao
) : TheSportsRepository {
    override suspend fun searchTeams(query: String, forceRefresh: Boolean): Result<List<Team>> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val key = query.trim().lowercase()
                if (!forceRefresh) {
                    val cached = getTeamsFromCache(key)
                    if (cached != null) return@safeCall Result.Success(cached)
                }
                val response = api.searchTeams(query)
                val teams = response.teams.orEmpty()
                    .mapNotNull { it.teamDtoToDomain() }
                cacheTeams(key, teams)
                Result.Success(teams)
            }
        }
    }

    override suspend fun teamsByLeague(leagueName: String, forceRefresh: Boolean): Result<List<Team>> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val normalizedLeague = leagueName.trim()
                val key = "league:${normalizedLeague.lowercase()}"
                if (!forceRefresh) {
                    val cached = getTeamsFromCache(key)
                    if (cached != null) {
                        val shouldUseCache = cached.isEmpty() || cached.any { !it.badgeUrl.isNullOrBlank() }
                        if (shouldUseCache) return@safeCall Result.Success(cached)
                    }
                }
                val response = api.teamsByLeague(normalizedLeague)
                val teams = response.teams.orEmpty()
                    .mapNotNull { it.teamDtoToDomain() }
                cacheTeams(key, teams)
                Result.Success(teams)
            }
        }
    }

    override suspend fun nextEvents(teamId: String, forceRefresh: Boolean): Result<List<Event>> {
        return eventsByType(teamId, EventType.NEXT, forceRefresh)
    }

    override suspend fun lastEvents(teamId: String, forceRefresh: Boolean): Result<List<Event>> {
        return eventsByType(teamId, EventType.LAST, forceRefresh)
    }

    override suspend fun eventDetails(eventId: String, forceRefresh: Boolean): Result<Event> {
        return withContext(Dispatchers.IO) {
            safeCall {
                if (!forceRefresh) {
                    val cached = dao.getEventById(eventId)
                    if (cached != null && !isExpired(cached.updatedAt, CACHE_DETAIL_TTL_MS)) {
                        return@safeCall Result.Success(cached.eventEntityToDomain())
                    }
                }
                val response = api.lookupEvent(eventId)
                val event = response.events.orEmpty().mapNotNull { it.eventDtoToDomain() }.firstOrNull()
                    ?: return@safeCall Result.Error("Event not found.")
                dao.upsertEvents(listOf(event.eventToEntity(teamId = "", type = EventType.DETAIL.name, updatedAt = System.currentTimeMillis())))
                Result.Success(event)
            }
        }
    }

    override suspend fun leagues(sport: String, forceRefresh: Boolean): Result<List<League>> {
        return withContext(Dispatchers.IO) {
            safeCall {
                if (!forceRefresh) {
                    val cached = dao.getLeaguesBySport(sport)
                    if (cached.isNotEmpty()) return@safeCall Result.Success(cached.map { it.leagueEntityToDomain() })
                }
                val response = api.allLeagues()
                val leagues = response.leagues.orEmpty()
                    .filter { it.strSport.equals(sport, ignoreCase = true) }
                    .mapNotNull { it.leagueDtoToDomain() }
                dao.upsertLeagues(leagues.map { it.leagueToEntity() })
                Result.Success(leagues)
            }
        }
    }

    override suspend fun leagueBadge(leagueId: String): Result<String?> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val badgeUrl = api.lookupLeague(leagueId)
                    .leagues
                    .orEmpty()
                    .firstOrNull()
                    ?.strBadge
                Result.Success(badgeUrl)
            }
        }
    }

    override suspend fun favoriteLeagues(): List<FavoriteLeague> {
        return withContext(Dispatchers.IO) {
            dao.getFavoriteLeagues().map {
                FavoriteLeague(
                    leagueId = it.leagueId,
                    name = it.name,
                    region = it.region
                )
            }
        }
    }

    override suspend fun addFavoriteLeague(leagueId: String, leagueName: String, region: String?) {
        withContext(Dispatchers.IO) {
            dao.upsertFavoriteLeague(
                FavoriteLeagueEntity(
                    leagueId = leagueId,
                    name = leagueName,
                    region = region,
                    addedAt = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun removeFavoriteLeague(leagueId: String) {
        withContext(Dispatchers.IO) {
            dao.deleteFavoriteLeague(leagueId)
        }
    }

    override suspend fun standings(
        leagueId: String,
        season: String,
        forceRefresh: Boolean
    ): Result<List<StandingRow>> {
        return withContext(Dispatchers.IO) {
            safeCall {
                if (!forceRefresh) {
                    val cached = dao.getStandings(leagueId, season)
                    if (cached.isNotEmpty() && !isExpired(cached.first().updatedAt, CACHE_STANDINGS_TTL_MS)) {
                        return@safeCall Result.Success(cached.map { it.standingEntityToDomain() })
                    }
                }
                val response = api.lookupTable(leagueId = leagueId, season = season)
                val rows = response.table.orEmpty().mapNotNull { it.standingDtoToDomain() }
                val now = System.currentTimeMillis()
                dao.upsertStandings(rows.map { it.standingToEntity(leagueId, season, now) })
                Result.Success(rows)
            }
        }
    }

    override suspend fun eventsByDay(date: String, sport: String): Result<List<Event>> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val response = api.eventsDay(date = date, sport = sport)
                val events = response.events.orEmpty().mapNotNull { it.eventDtoToDomain() }
                Result.Success(events)
            }
        }
    }

    private suspend fun eventsByType(
        teamId: String,
        type: EventType,
        forceRefresh: Boolean
    ): Result<List<Event>> {
        return withContext(Dispatchers.IO) {
            safeCall {
                if (!forceRefresh) {
                    val cached = dao.getEvents(teamId, type.name)
                    if (cached.isNotEmpty() && !isExpired(cached.first().updatedAt, CACHE_EVENTS_TTL_MS)) {
                        return@safeCall Result.Success(cached.map { it.eventEntityToDomain() })
                    }
                }
                val response = when (type) {
                    EventType.NEXT -> api.eventsNext(teamId)
                    EventType.LAST -> api.eventsLast(teamId)
                    EventType.DETAIL -> throw IllegalStateException("Detail events should be loaded via eventDetails()")
                }
                val events = response.events.orEmpty().mapNotNull { it.eventDtoToDomain() }
                val now = System.currentTimeMillis()
                val entities = events.map { it.eventToEntity(teamId, type.name, now) }
                dao.upsertEvents(entities)
                Result.Success(events)
            }
        }
    }

    private suspend fun getTeamsFromCache(queryKey: String): List<Team>? {
        val cachedQuery = dao.getSearchQuery(queryKey) ?: return null
        if (isExpired(cachedQuery.updatedAt, CACHE_TEAMS_TTL_MS)) return null
        val ids = cachedQuery.teamIdsCsv.split(",").filter { it.isNotBlank() }
        if (ids.isEmpty()) return emptyList()
        return dao.getTeamsByIds(ids).map { it.teamEntityToDomain() }
    }

    private suspend fun cacheTeams(queryKey: String, teams: List<Team>) {
        val now = System.currentTimeMillis()
        val entities = teams.map { it.teamToEntity() }
        dao.upsertTeams(entities)
        val ids = entities.joinToString(",") { it.id }
        dao.upsertSearchQuery(SearchQueryEntity(query = queryKey, teamIdsCsv = ids, updatedAt = now))
    }

    private inline fun <T> safeCall(block: () -> Result<T>): Result<T> {
        return try {
            block()
        } catch (e: IOException) {
            Result.Error("Network error. Check your connection.")
        } catch (e: HttpException) {
            Result.Error("Server error: ${e.code()}.")
        } catch (e: Exception) {
            Result.Error("Unexpected error.")
        }
    }

    private fun isExpired(updatedAt: Long, ttlMs: Long): Boolean {
        return System.currentTimeMillis() - updatedAt > ttlMs
    }
}

private enum class EventType { NEXT, LAST, DETAIL }

private const val CACHE_TEAMS_TTL_MS = 6 * 60 * 60 * 1000L
private const val CACHE_EVENTS_TTL_MS = 30 * 60 * 1000L
private const val CACHE_DETAIL_TTL_MS = 24 * 60 * 60 * 1000L
private const val CACHE_STANDINGS_TTL_MS = 60 * 60 * 1000L
