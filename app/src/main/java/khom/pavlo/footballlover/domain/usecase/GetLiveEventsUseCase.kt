package khom.pavlo.footballlover.domain.usecase

import khom.pavlo.footballlover.domain.repository.TheSportsRepository

class GetLiveEventsUseCase(
    private val repository: TheSportsRepository
) {
    suspend operator fun invoke(sport: String = "Soccer") =
        repository.liveEvents(sport)
}
