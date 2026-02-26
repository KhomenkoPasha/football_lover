package khom.pavlo.footballlover.ui.standings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import khom.pavlo.footballlover.domain.model.League
import khom.pavlo.footballlover.domain.model.StandingRow
import khom.pavlo.footballlover.domain.result.Result
import khom.pavlo.footballlover.domain.usecase.GetLeaguesUseCase
import khom.pavlo.footballlover.domain.usecase.GetStandingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StandingsUiState(
    val leagueQuery: String = "",
    val season: String = "2024-2025",
    val leagues: List<League> = emptyList(),
    val selectedLeague: League? = null,
    val table: List<StandingRow> = emptyList(),
    val isLoadingLeagues: Boolean = false,
    val isLoadingTable: Boolean = false,
    val error: String? = null
)

class StandingsViewModel(
    private val getLeaguesUseCase: GetLeaguesUseCase,
    private val getStandingsUseCase: GetStandingsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(StandingsUiState())
    val state: StateFlow<StandingsUiState> = _state

    init {
        loadLeagues()
    }

    fun onLeagueQueryChange(value: String) {
        _state.update { it.copy(leagueQuery = value) }
    }

    fun onSeasonChange(value: String) {
        _state.update { it.copy(season = value) }
    }

    fun loadLeagues(forceRefresh: Boolean = false) {
        _state.update { it.copy(isLoadingLeagues = true, error = null) }
        viewModelScope.launch {
            when (val result = getLeaguesUseCase("Soccer", forceRefresh)) {
                is Result.Success -> _state.update {
                    it.copy(isLoadingLeagues = false, leagues = result.data, error = null)
                }
                is Result.Error -> _state.update {
                    it.copy(isLoadingLeagues = false, error = result.message)
                }
            }
        }
    }

    fun selectLeague(league: League) {
        _state.update { it.copy(selectedLeague = league) }
        loadStandings(forceRefresh = true)
    }

    fun preselectLeague(leagueId: String, leagueName: String) {
        if (leagueId.isBlank()) return
        val existing = state.value.leagues.firstOrNull { it.id == leagueId }
        val league = existing ?: League(id = leagueId, name = leagueName, sport = null)
        _state.update { it.copy(selectedLeague = league) }
        loadStandings(forceRefresh = true)
    }

    fun loadStandings(forceRefresh: Boolean = false) {
        val league = state.value.selectedLeague ?: return
        val season = state.value.season.trim()
        if (season.isEmpty()) {
            _state.update { it.copy(error = "Enter season, e.g. 2024-2025") }
            return
        }
        _state.update { it.copy(isLoadingTable = true, error = null) }
        viewModelScope.launch {
            when (val result = getStandingsUseCase(league.id, season, forceRefresh)) {
                is Result.Success -> _state.update {
                    it.copy(isLoadingTable = false, table = result.data, error = null)
                }
                is Result.Error -> _state.update {
                    it.copy(isLoadingTable = false, error = result.message, table = emptyList())
                }
            }
        }
    }

    fun filteredLeagues(): List<League> {
        val query = state.value.leagueQuery.trim().lowercase()
        if (query.isEmpty()) return state.value.leagues
        return state.value.leagues.filter { it.name.lowercase().contains(query) }
    }
}
