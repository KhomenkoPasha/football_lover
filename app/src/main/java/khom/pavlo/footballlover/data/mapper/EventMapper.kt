package khom.pavlo.footballlover.data.mapper

import khom.pavlo.footballlover.data.local.EventEntity
import khom.pavlo.footballlover.data.remote.model.EventDto
import khom.pavlo.footballlover.domain.model.Event

fun EventDto.toDomain(): Event? {
    val id = idEvent ?: return null
    val name = strEvent ?: return null
    return Event(
        id = id,
        leagueId = idLeague,
        leagueName = strLeague,
        sport = strSport,
        country = strCountry,
        name = name,
        date = dateEvent,
        time = strTime,
        homeTeam = strHomeTeam,
        awayTeam = strAwayTeam,
        homeTeamBadgeUrl = strHomeTeamBadge,
        awayTeamBadgeUrl = strAwayTeamBadge,
        homeScore = intHomeScore,
        awayScore = intAwayScore,
        thumbUrl = strThumb
    )
}

fun Event.toEntity(teamId: String, type: String, updatedAt: Long): EventEntity {
    return EventEntity(
        id = id,
        teamId = teamId,
        type = type,
        name = name,
        date = date,
        time = time,
        homeTeam = homeTeam,
        awayTeam = awayTeam,
        homeTeamBadgeUrl = homeTeamBadgeUrl,
        awayTeamBadgeUrl = awayTeamBadgeUrl,
        homeScore = homeScore,
        awayScore = awayScore,
        thumbUrl = thumbUrl,
        updatedAt = updatedAt
    )
}

fun EventEntity.toDomain(): Event {
    return Event(
        id = id,
        leagueId = null,
        leagueName = null,
        sport = null,
        country = null,
        name = name,
        date = date,
        time = time,
        homeTeam = homeTeam,
        awayTeam = awayTeam,
        homeTeamBadgeUrl = homeTeamBadgeUrl,
        awayTeamBadgeUrl = awayTeamBadgeUrl,
        homeScore = homeScore,
        awayScore = awayScore,
        thumbUrl = thumbUrl
    )
}
