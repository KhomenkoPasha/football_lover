package khom.pavlo.footballlover.domain.usecase

import khom.pavlo.footballlover.domain.repository.TheSportsRepository

class GetLeaguesUseCase(
    private val repository: TheSportsRepository
) {
    suspend operator fun invoke(sport: String, forceRefresh: Boolean = false) =
        repository.leagues(sport, forceRefresh)
}
