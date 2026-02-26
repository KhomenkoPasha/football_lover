package khom.pavlo.footballlover.domain.usecase

import khom.pavlo.footballlover.domain.repository.TheSportsRepository

class GetTeamsByLeagueUseCase(
    private val repository: TheSportsRepository
) {
    suspend operator fun invoke(leagueName: String, forceRefresh: Boolean = false) =
        repository.teamsByLeague(leagueName, forceRefresh)
}
