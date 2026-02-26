package khom.pavlo.footballlover.data.mapper

import khom.pavlo.footballlover.data.local.LeagueEntity
import khom.pavlo.footballlover.data.remote.model.LeagueDto
import khom.pavlo.footballlover.domain.model.League
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LeagueMapperTest {

    @Test
    fun leagueDto_toDomain_returnsNull_whenIdOrNameMissing() {
        val missingId = LeagueDto(
            idLeague = null,
            strLeague = "Premier League",
            strSport = "Soccer",
            strBadge = null
        )
        val missingName = LeagueDto(
            idLeague = "4328",
            strLeague = null,
            strSport = "Soccer",
            strBadge = null
        )

        assertNull(missingId.toDomain())
        assertNull(missingName.toDomain())
    }

    @Test
    fun league_toEntity_and_back_preservesFields() {
        val league = League(
            id = "4328",
            name = "Premier League",
            sport = "Soccer"
        )

        val entity = league.toEntity()
        val mappedBack = entity.toDomain()

        assertEquals(
            LeagueEntity(id = "4328", name = "Premier League", sport = "Soccer"),
            entity
        )
        assertEquals(league, mappedBack)
    }
}
