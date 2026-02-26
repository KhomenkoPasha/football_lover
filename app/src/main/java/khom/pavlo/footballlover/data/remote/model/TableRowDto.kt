package khom.pavlo.footballlover.data.remote.model

import com.squareup.moshi.Json

data class TableRowDto(
    @Json(name = "name") val name: String?,
    @Json(name = "teamid") val teamId: String?,
    @Json(name = "played") val played: String?,
    @Json(name = "win") val win: String?,
    @Json(name = "draw") val draw: String?,
    @Json(name = "loss") val loss: String?,
    @Json(name = "goalsfor") val goalsFor: String?,
    @Json(name = "goalsagainst") val goalsAgainst: String?,
    @Json(name = "goalsdifference") val goalsDifference: String?,
    @Json(name = "total") val total: String?,
    @Json(name = "rank") val rank: String?
)
