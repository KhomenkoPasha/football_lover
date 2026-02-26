package khom.pavlo.footballlover.domain.usecase

import khom.pavlo.footballlover.domain.repository.TheSportsRepository

class GetNextEventsUseCase(
    private val repository: TheSportsRepository
) {
    suspend operator fun invoke(teamId: String, forceRefresh: Boolean = false) =
        repository.nextEvents(teamId, forceRefresh)
}
