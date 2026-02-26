package khom.pavlo.footballlover.ui.league

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import khom.pavlo.footballlover.domain.model.Team
import khom.pavlo.footballlover.domain.result.Result
import khom.pavlo.footballlover.domain.usecase.GetTeamsByLeagueUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LeagueTeamsUiState(
    val leagueName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val teams: List<Team> = emptyList()
)

class LeagueTeamsViewModel(
    private val getTeamsByLeagueUseCase: GetTeamsByLeagueUseCase,
    leagueName: String
) : ViewModel() {
    private val _state = MutableStateFlow(LeagueTeamsUiState(leagueName = leagueName))
    val state: StateFlow<LeagueTeamsUiState> = _state

    init {
        load()
    }

    fun load(forceRefresh: Boolean = false) {
        val leagueName = state.value.leagueName.trim()
        if (leagueName.isBlank()) {
            _state.update { it.copy(error = "League name is empty.", teams = emptyList()) }
            return
        }
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = getTeamsByLeagueUseCase(leagueName, forceRefresh)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            teams = result.data.sortedBy { team -> team.name },
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message, teams = emptyList()) }
                }
            }
        }
    }
}
