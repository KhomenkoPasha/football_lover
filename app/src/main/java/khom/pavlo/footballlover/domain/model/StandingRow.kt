package khom.pavlo.footballlover.domain.model

data class StandingRow(
    val rank: String?,
    val teamName: String,
    val played: String?,
    val win: String?,
    val draw: String?,
    val loss: String?,
    val goalsFor: String?,
    val goalsAgainst: String?,
    val goalsDifference: String?,
    val points: String?
)
