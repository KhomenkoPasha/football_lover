package khom.pavlo.footballlover.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import khom.pavlo.footballlover.domain.model.Event
import khom.pavlo.footballlover.domain.result.Result
import khom.pavlo.footballlover.domain.usecase.GetEventDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val event: Event? = null
)

class EventDetailViewModel(
    private val getEventDetailsUseCase: GetEventDetailsUseCase,
    private val eventId: String
) : ViewModel() {
    private val _state = MutableStateFlow(EventDetailUiState(isLoading = true))
    val state: StateFlow<EventDetailUiState> = _state

    init {
        load()
    }

    fun load() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = getEventDetailsUseCase(eventId)) {
                is Result.Success -> _state.update { it.copy(isLoading = false, event = result.data) }
                is Result.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }
}
