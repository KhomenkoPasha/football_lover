package khom.pavlo.footballlover.domain.usecase

import khom.pavlo.footballlover.domain.repository.TheSportsRepository

class AddFavoriteLeagueUseCase(
    private val repository: TheSportsRepository
) {
    suspend operator fun invoke(leagueId: String, leagueName: String, region: String?) =
        repository.addFavoriteLeague(leagueId, leagueName, region)
}
