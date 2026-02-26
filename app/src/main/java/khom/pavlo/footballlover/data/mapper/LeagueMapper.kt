package khom.pavlo.footballlover.data.mapper

import khom.pavlo.footballlover.data.local.LeagueEntity
import khom.pavlo.footballlover.data.remote.model.LeagueDto
import khom.pavlo.footballlover.domain.model.League

fun LeagueDto.toDomain(): League? {
    val id = idLeague ?: return null
    val name = strLeague ?: return null
    return League(id = id, name = name, sport = strSport)
}

fun League.toEntity(): LeagueEntity {
    return LeagueEntity(
        id = id,
        name = name,
        sport = sport
    )
}

fun LeagueEntity.toDomain(): League {
    return League(
        id = id,
        name = name,
        sport = sport
    )
}
