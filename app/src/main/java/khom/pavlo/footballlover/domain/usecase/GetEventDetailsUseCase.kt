package khom.pavlo.footballlover.domain.usecase

import khom.pavlo.footballlover.domain.repository.TheSportsRepository

class GetEventDetailsUseCase(
    private val repository: TheSportsRepository
) {
    suspend operator fun invoke(eventId: String, forceRefresh: Boolean = false) =
        repository.eventDetails(eventId, forceRefresh)
}
