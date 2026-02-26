package khom.pavlo.footballlover.data.mapper

import khom.pavlo.footballlover.data.local.StandingEntity
import khom.pavlo.footballlover.data.remote.model.TableRowDto
import khom.pavlo.footballlover.domain.model.StandingRow

fun TableRowDto.toDomain(): StandingRow? {
    val name = name ?: return null
    return StandingRow(
        rank = rank,
        teamName = name,
        played = played,
        win = win,
        draw = draw,
        loss = loss,
        goalsFor = goalsFor,
        goalsAgainst = goalsAgainst,
        goalsDifference = goalsDifference,
        points = total
    )
}

fun StandingRow.toEntity(leagueId: String, season: String, updatedAt: Long): StandingEntity {
    val rowId = "$leagueId|$season|${teamName.lowercase()}"
    return StandingEntity(
        id = rowId,
        leagueId = leagueId,
        season = season,
        rank = rank,
        teamName = teamName,
        played = played,
        win = win,
        draw = draw,
        loss = loss,
        goalsFor = goalsFor,
        goalsAgainst = goalsAgainst,
        goalsDifference = goalsDifference,
        points = points,
        updatedAt = updatedAt
    )
}

fun StandingEntity.toDomain(): StandingRow {
    return StandingRow(
        rank = rank,
        teamName = teamName,
        played = played,
        win = win,
        draw = draw,
        loss = loss,
        goalsFor = goalsFor,
        goalsAgainst = goalsAgainst,
        goalsDifference = goalsDifference,
        points = points
    )
}
