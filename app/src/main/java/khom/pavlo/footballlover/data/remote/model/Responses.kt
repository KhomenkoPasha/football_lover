package khom.pavlo.footballlover.data.remote.model

import com.squareup.moshi.Json

data class TeamsResponse(
    @Json(name = "teams") val teams: List<TeamDto>?
)

data class EventsResponse(
    @Json(name = "events") val events: List<EventDto>?
)

data class LeaguesResponse(
    @Json(name = "leagues") val leagues: List<LeagueDto>?
)

data class TableResponse(
    @Json(name = "table") val table: List<TableRowDto>?
)
