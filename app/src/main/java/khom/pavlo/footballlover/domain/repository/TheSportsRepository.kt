package khom.pavlo.footballlover.domain.repository

import khom.pavlo.footballlover.domain.model.Event
import khom.pavlo.footballlover.domain.model.FavoriteLeague
import khom.pavlo.footballlover.domain.model.League
import khom.pavlo.footballlover.domain.model.StandingRow
import khom.pavlo.footballlover.domain.model.Team
import khom.pavlo.footballlover.domain.result.Result

interface TheSportsRepository {
    suspend fun searchTeams(query: String, forceRefresh: Boolean = false): Result<List<Team>>
    suspend fun teamsByLeague(leagueName: String, forceRefresh: Boolean = false): Result<List<Team>>
    suspend fun nextEvents(teamId: String, forceRefresh: Boolean = false): Result<List<Event>>
    suspend fun lastEvents(teamId: String, forceRefresh: Boolean = false): Result<List<Event>>
    suspend fun eventDetails(eventId: String, forceRefresh: Boolean = false): Result<Event>
    suspend fun leagues(sport: String, forceRefresh: Boolean = false): Result<List<League>>
    suspend fun leagueBadge(leagueId: String): Result<String?>
    suspend fun favoriteLeagues(): List<FavoriteLeague>
    suspend fun addFavoriteLeague(leagueId: String, leagueName: String, region: String?)
    suspend fun removeFavoriteLeague(leagueId: String)
    suspend fun standings(leagueId: String, season: String, forceRefresh: Boolean = false): Result<List<StandingRow>>
    suspend fun eventsByDay(date: String, sport: String = "Soccer"): Result<List<Event>>
}
