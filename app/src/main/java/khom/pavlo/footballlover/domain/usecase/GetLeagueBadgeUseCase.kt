package khom.pavlo.footballlover.domain.usecase

import khom.pavlo.footballlover.domain.repository.TheSportsRepository

class GetLeagueBadgeUseCase(
    private val repository: TheSportsRepository
) {
    suspend operator fun invoke(leagueId: String) = repository.leagueBadge(leagueId)
}
