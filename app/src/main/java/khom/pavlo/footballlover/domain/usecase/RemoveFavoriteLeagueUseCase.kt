package khom.pavlo.footballlover.domain.usecase

import khom.pavlo.footballlover.domain.repository.TheSportsRepository

class RemoveFavoriteLeagueUseCase(
    private val repository: TheSportsRepository
) {
    suspend operator fun invoke(leagueId: String) = repository.removeFavoriteLeague(leagueId)
}
