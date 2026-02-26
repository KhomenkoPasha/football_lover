package khom.pavlo.footballlover.domain.usecase

import khom.pavlo.footballlover.domain.repository.TheSportsRepository

class GetEventsByDayUseCase(
    private val repository: TheSportsRepository
) {
    suspend operator fun invoke(date: String, sport: String = "Soccer") =
        repository.eventsByDay(date, sport)
}
