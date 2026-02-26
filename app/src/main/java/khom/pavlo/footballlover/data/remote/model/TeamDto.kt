package khom.pavlo.footballlover.data.remote.model

import com.squareup.moshi.Json

data class TeamDto(
    @Json(name = "idTeam") val idTeam: String?,
    @Json(name = "strTeam") val strTeam: String?,
    @Json(name = "strLeague") val strLeague: String?,
    @Json(name = "strTeamBadge") val strTeamBadge: String?,
    @Json(name = "strBadge") val strBadge: String?,
    @Json(name = "strStadium") val strStadium: String?,
    @Json(name = "strCountry") val strCountry: String?
)
