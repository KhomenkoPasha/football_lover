package khom.pavlo.footballlover.data.mapper

import khom.pavlo.footballlover.data.local.TeamEntity
import khom.pavlo.footballlover.data.remote.model.TeamDto
import khom.pavlo.footballlover.domain.model.Team

fun TeamDto.toDomain(): Team? {
    val id = idTeam ?: return null
    val name = strTeam ?: return null
    return Team(
        id = id,
        name = name,
        league = strLeague,
        badgeUrl = strTeamBadge ?: strBadge,
        stadium = strStadium,
        country = strCountry
    )
}

fun Team.toEntity(): TeamEntity {
    return TeamEntity(
        id = id,
        name = name,
        league = league,
        badgeUrl = badgeUrl,
        stadium = stadium,
        country = country
    )
}

fun TeamEntity.toDomain(): Team {
    return Team(
        id = id,
        name = name,
        league = league,
        badgeUrl = badgeUrl,
        stadium = stadium,
        country = country
    )
}
