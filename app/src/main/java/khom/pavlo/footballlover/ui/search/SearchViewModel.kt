package khom.pavlo.footballlover.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import khom.pavlo.footballlover.domain.model.Team
import khom.pavlo.footballlover.domain.result.Result
import khom.pavlo.footballlover.domain.usecase.SearchTeamsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val teams: List<Team> = emptyList()
)

class SearchViewModel(
    private val searchTeamsUseCase: SearchTeamsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state

    fun onQueryChange(value: String) {
        _state.update { it.copy(query = value) }
    }

    fun search(forceRefresh: Boolean = false) {
        val query = state.value.query.trim()
        if (query.isEmpty()) {
            _state.update { it.copy(error = "Enter a team name.", teams = emptyList()) }
            return
        }
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = searchTeamsUseCase(query, forceRefresh)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, teams = result.data, error = null) }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message, teams = emptyList()) }
                }
            }
        }
    }
}
