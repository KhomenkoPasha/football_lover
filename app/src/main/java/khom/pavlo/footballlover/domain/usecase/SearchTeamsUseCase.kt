package khom.pavlo.footballlover.domain.usecase

import khom.pavlo.footballlover.domain.repository.TheSportsRepository

class SearchTeamsUseCase(
    private val repository: TheSportsRepository
) {
    suspend operator fun invoke(query: String, forceRefresh: Boolean = false) =
        repository.searchTeams(query, forceRefresh)
}
