package khom.pavlo.footballlover.domain.usecase

import khom.pavlo.footballlover.domain.repository.TheSportsRepository

class GetFavoriteLeaguesUseCase(
    private val repository: TheSportsRepository
) {
    suspend operator fun invoke() = repository.favoriteLeagues()
}
