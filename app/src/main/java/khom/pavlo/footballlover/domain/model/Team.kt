package khom.pavlo.footballlover.domain.model

data class Team(
    val id: String,
    val name: String,
    val league: String?,
    val badgeUrl: String?,
    val stadium: String?,
    val country: String?
)
