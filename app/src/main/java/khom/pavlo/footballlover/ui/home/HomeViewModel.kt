package khom.pavlo.footballlover.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import khom.pavlo.footballlover.domain.model.Event
import khom.pavlo.footballlover.domain.model.FavoriteLeague
import khom.pavlo.footballlover.domain.model.League
import khom.pavlo.footballlover.domain.result.Result
import khom.pavlo.footballlover.domain.usecase.AddFavoriteLeagueUseCase
import khom.pavlo.footballlover.domain.usecase.GetEventsByDayUseCase
import khom.pavlo.footballlover.domain.usecase.GetFavoriteLeaguesUseCase
import khom.pavlo.footballlover.domain.usecase.GetLeagueBadgeUseCase
import khom.pavlo.footballlover.domain.usecase.GetLeaguesUseCase
import khom.pavlo.footballlover.domain.usecase.RemoveFavoriteLeagueUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class HomeUiState(
    val days: List<DayItem> = emptyList(),
    val selectedDayIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalMatches: Int = 0,
    val favoriteLeagues: List<LeagueRow> = emptyList(),
    val otherLeagues: List<LeagueRow> = emptyList()
)

class HomeViewModel(
    private val getEventsByDayUseCase: GetEventsByDayUseCase,
    private val getLeaguesUseCase: GetLeaguesUseCase,
    private val getFavoriteLeaguesUseCase: GetFavoriteLeaguesUseCase,
    private val addFavoriteLeagueUseCase: AddFavoriteLeagueUseCase,
    private val removeFavoriteLeagueUseCase: RemoveFavoriteLeagueUseCase,
    private val getLeagueBadgeUseCase: GetLeagueBadgeUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    private val apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)
    private var latestEvents: List<Event> = emptyList()
    private var allSoccerLeagues: List<League> = emptyList()
    private var favoriteLeagueSnapshots: List<FavoriteLeague> = emptyList()
    private var favoriteLeagueIds: Set<String> = emptySet()
    private val leagueBadgesById = mutableMapOf<String, String?>()
    private val badgeRequestsInFlight = mutableSetOf<String>()

    init {
        val days = buildDays(LocalDate.now())
        _state.update { it.copy(days = days, selectedDayIndex = 2) }
        loadFavorites()
        loadLeagueCatalog()
        loadDay(days[2].apiDate)
    }

    fun selectDay(index: Int) {
        val days = buildDays(LocalDate.now())
        val safeIndex = index.coerceIn(days.indices)
        val day = days[safeIndex]
        _state.update { it.copy(days = days, selectedDayIndex = safeIndex) }
        loadDay(day.apiDate)
    }

    private fun buildDays(today: LocalDate): List<DayItem> {
        return (-2..4).map { offset ->
            val date = today.plusDays(offset.toLong())
            DayItem(
                epochDay = date.toEpochDay(),
                dayOfWeek = date.dayOfWeek.value,
                isToday = date == today,
                apiDate = date.format(apiDateFormatter)
            )
        }
    }

    fun toggleFavoriteLeague(row: LeagueRow) {
        if (row.leagueId.isBlank()) return

        val wasFavorite = favoriteLeagueIds.contains(row.leagueId)
        favoriteLeagueSnapshots = if (wasFavorite) {
            favoriteLeagueSnapshots.filterNot { it.leagueId == row.leagueId }
        } else {
            favoriteLeagueSnapshots.filterNot { it.leagueId == row.leagueId } + FavoriteLeague(
                leagueId = row.leagueId,
                name = row.name,
                region = row.region
            )
        }
        favoriteLeagueIds = favoriteLeagueSnapshots.map { it.leagueId }.toSet()
        refreshLeagueLists()

        viewModelScope.launch {
            try {
                if (wasFavorite) {
                    removeFavoriteLeagueUseCase(row.leagueId)
                } else {
                    addFavoriteLeagueUseCase(row.leagueId, row.name, row.region)
                }
            } catch (_: Exception) {
                loadFavorites()
            }
        }
    }

    fun ensureLeagueBadge(leagueId: String) {
        if (leagueId.isBlank()) return
        if (leagueBadgesById.containsKey(leagueId)) return
        if (!badgeRequestsInFlight.add(leagueId)) return

        viewModelScope.launch {
            when (val result = getLeagueBadgeUseCase(leagueId)) {
                is Result.Success -> {
                    leagueBadgesById[leagueId] = result.data
                    refreshLeagueLists()
                }
                is Result.Error -> {
                    leagueBadgesById[leagueId] = null
                }
            }
            badgeRequestsInFlight.remove(leagueId)
        }
    }

    private fun loadDay(apiDate: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = getEventsByDayUseCase(apiDate)) {
                is Result.Success -> {
                    val events = result.data
                    latestEvents = events
                    val grouped = groupByLeague(events, allSoccerLeagues, favoriteLeagueSnapshots)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            totalMatches = events.size,
                            favoriteLeagues = grouped.favorites,
                            otherLeagues = grouped.others,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            favoriteLeagueSnapshots = getFavoriteLeaguesUseCase()
            favoriteLeagueIds = favoriteLeagueSnapshots.map { it.leagueId }.toSet()
            refreshLeagueLists()
        }
    }

    private fun loadLeagueCatalog() {
        viewModelScope.launch {
            when (val result = getLeaguesUseCase("Soccer")) {
                is Result.Success -> {
                    allSoccerLeagues = result.data
                    refreshLeagueLists()
                }
                is Result.Error -> Unit
            }
        }
    }

    private fun refreshLeagueLists() {
        val grouped = groupByLeague(latestEvents, allSoccerLeagues, favoriteLeagueSnapshots)
        _state.update {
            it.copy(
                favoriteLeagues = grouped.favorites,
                otherLeagues = grouped.others
            )
        }
    }

    private fun groupByLeague(
        events: List<Event>,
        catalogLeagues: List<League>,
        favoriteSnapshots: List<FavoriteLeague>
    ): LeagueGroups {
        val eventLeagueRows = events
            .filter { it.leagueId != null && it.leagueName != null }
            .groupBy { it.leagueId to it.leagueName }
            .map { (key, items) ->
                LeagueRow(
                    leagueId = key.first ?: "",
                    region = items.firstOrNull()?.country ?: "—",
                    name = key.second ?: "—",
                    count = items.size,
                    isFavorite = false,
                    badgeUrl = leagueBadgesById[key.first.orEmpty()]
                )
            }
        val eventRowsById = eventLeagueRows.associateBy { it.leagueId }
        val favoriteById = favoriteSnapshots.associateBy { it.leagueId }
        val favoriteIds = favoriteById.keys

        val baseRows = if (catalogLeagues.isEmpty()) {
            eventLeagueRows
        } else {
            val catalogIds = catalogLeagues.map { it.id }.toSet()
            val catalogRows = catalogLeagues
                .map { league ->
                    val fromEvents = eventRowsById[league.id]
                    val fromFavorites = favoriteById[league.id]
                    LeagueRow(
                        leagueId = league.id,
                        region = fromEvents?.region ?: fromFavorites?.region ?: "—",
                        name = league.name,
                        count = fromEvents?.count ?: 0,
                        isFavorite = false,
                        badgeUrl = leagueBadgesById[league.id]
                    )
                }
            val extraEventRows = eventLeagueRows.filterNot { it.leagueId in catalogIds }
            catalogRows + extraEventRows
        }

        val baseIds = baseRows.map { it.leagueId }.toSet()
        val extraFavoriteRows = favoriteSnapshots
            .filterNot { it.leagueId in baseIds }
            .map { favorite ->
                val fromEvents = eventRowsById[favorite.leagueId]
                LeagueRow(
                    leagueId = favorite.leagueId,
                    region = fromEvents?.region ?: favorite.region ?: "—",
                    name = favorite.name,
                    count = fromEvents?.count ?: 0,
                    isFavorite = true,
                    badgeUrl = leagueBadgesById[favorite.leagueId]
                )
            }

        val leagueCounts = (baseRows + extraFavoriteRows)
            .map { row -> row.copy(isFavorite = row.leagueId in favoriteIds) }
            .sortedWith(compareByDescending<LeagueRow> { it.count }.thenBy { it.name })

        return LeagueGroups(
            favorites = leagueCounts.filter { it.isFavorite },
            others = leagueCounts.filterNot { it.isFavorite }
        )
    }

}

data class DayItem(
    val epochDay: Long,
    val dayOfWeek: Int,
    val isToday: Boolean,
    val apiDate: String
)

data class LeagueRow(
    val leagueId: String,
    val region: String,
    val name: String,
    val count: Int,
    val isFavorite: Boolean,
    val badgeUrl: String?
)

private data class LeagueGroups(
    val favorites: List<LeagueRow>,
    val others: List<LeagueRow>
)
