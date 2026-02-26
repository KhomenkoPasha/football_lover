package khom.pavlo.footballlover.data.remote.model

import com.squareup.moshi.Json

data class LeagueDto(
    @Json(name = "idLeague") val idLeague: String?,
    @Json(name = "strLeague") val strLeague: String?,
    @Json(name = "strSport") val strSport: String?,
    @Json(name = "strBadge") val strBadge: String?
)
