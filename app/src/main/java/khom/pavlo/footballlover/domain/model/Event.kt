package khom.pavlo.footballlover.domain.model

data class Event(
    val id: String,
    val leagueId: String?,
    val leagueName: String?,
    val sport: String?,
    val country: String?,
    val name: String,
    val date: String?,
    val time: String?,
    val homeTeam: String?,
    val awayTeam: String?,
    val homeTeamBadgeUrl: String?,
    val awayTeamBadgeUrl: String?,
    val homeScore: String?,
    val awayScore: String?,
    val thumbUrl: String?
)
