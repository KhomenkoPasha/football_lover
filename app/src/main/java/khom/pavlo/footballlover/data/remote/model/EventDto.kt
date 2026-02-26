package khom.pavlo.footballlover.data.remote.model

import com.squareup.moshi.Json

data class EventDto(
    @Json(name = "idEvent") val idEvent: String?,
    @Json(name = "idLeague") val idLeague: String?,
    @Json(name = "strLeague") val strLeague: String?,
    @Json(name = "strSport") val strSport: String?,
    @Json(name = "strCountry") val strCountry: String?,
    @Json(name = "strEvent") val strEvent: String?,
    @Json(name = "dateEvent") val dateEvent: String?,
    @Json(name = "strTime") val strTime: String?,
    @Json(name = "strStatus") val strStatus: String?,
    @Json(name = "strHomeTeam") val strHomeTeam: String?,
    @Json(name = "strAwayTeam") val strAwayTeam: String?,
    @Json(name = "strHomeTeamBadge") val strHomeTeamBadge: String?,
    @Json(name = "strAwayTeamBadge") val strAwayTeamBadge: String?,
    @Json(name = "intHomeScore") val intHomeScore: String?,
    @Json(name = "intAwayScore") val intAwayScore: String?,
    @Json(name = "strThumb") val strThumb: String?
)
