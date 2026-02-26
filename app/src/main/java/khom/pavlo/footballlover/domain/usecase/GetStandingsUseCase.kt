package khom.pavlo.footballlover.domain.usecase

import khom.pavlo.footballlover.domain.repository.TheSportsRepository

class GetStandingsUseCase(
    private val repository: TheSportsRepository
) {
    suspend operator fun invoke(leagueId: String, season: String, forceRefresh: Boolean = false) =
        repository.standings(leagueId, season, forceRefresh)
}
