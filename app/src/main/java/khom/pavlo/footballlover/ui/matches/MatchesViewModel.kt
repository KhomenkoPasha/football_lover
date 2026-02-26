package khom.pavlo.footballlover.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import khom.pavlo.footballlover.domain.model.Event
import khom.pavlo.footballlover.domain.result.Result
import khom.pavlo.footballlover.domain.usecase.GetLastEventsUseCase
import khom.pavlo.footballlover.domain.usecase.GetNextEventsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MatchesTab { NEXT, LAST }

data class MatchesUiState(
    val teamId: String,
    val teamName: String,
    val isLoading: Boolean = false,
    val error: String? = null,
    val nextEvents: List<Event> = emptyList(),
    val lastEvents: List<Event> = emptyList(),
    val selectedTab: MatchesTab = MatchesTab.NEXT
)

class MatchesViewModel(
    private val getNextEventsUseCase: GetNextEventsUseCase,
    private val getLastEventsUseCase: GetLastEventsUseCase,
    teamId: String,
    teamName: String
) : ViewModel() {
    private val _state = MutableStateFlow(MatchesUiState(teamId = teamId, teamName = teamName))
    val state: StateFlow<MatchesUiState> = _state

    init {
        loadNext()
        loadLast()
    }

    fun selectTab(tab: MatchesTab) {
        _state.update { it.copy(selectedTab = tab) }
    }

    fun refresh() {
        loadNext(forceRefresh = true)
        loadLast(forceRefresh = true)
    }

    fun loadNext(forceRefresh: Boolean = false) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = getNextEventsUseCase(state.value.teamId, forceRefresh)) {
                is Result.Success -> _state.update {
                    it.copy(isLoading = false, nextEvents = result.data, error = null)
                }
                is Result.Error -> _state.update {
                    it.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun loadLast(forceRefresh: Boolean = false) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = getLastEventsUseCase(state.value.teamId, forceRefresh)) {
                is Result.Success -> _state.update {
                    it.copy(isLoading = false, lastEvents = result.data, error = null)
                }
                is Result.Error -> _state.update {
                    it.copy(isLoading = false, error = result.message)
                }
            }
        }
    }
}
