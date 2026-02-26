package khom.pavlo.footballlover.data.mapper

import khom.pavlo.footballlover.data.local.EventEntity
import khom.pavlo.footballlover.data.remote.model.EventDto
import khom.pavlo.footballlover.domain.model.Event
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EventMapperTest {

    @Test
    fun eventDto_toDomain_mapsFieldsIncludingTeamBadges() {
        val dto = EventDto(
            idEvent = "100",
            idLeague = "4328",
            strLeague = "Premier League",
            strSport = "Soccer",
            strCountry = "England",
            strEvent = "Arsenal vs Chelsea",
            dateEvent = "2026-02-26",
            strTime = "18:30:00",
            strHomeTeam = "Arsenal",
            strAwayTeam = "Chelsea",
            strHomeTeamBadge = "https://img/home.png",
            strAwayTeamBadge = "https://img/away.png",
            intHomeScore = "2",
            intAwayScore = "1",
            strThumb = "https://img/thumb.png"
        )

        val event = dto.toDomain()

        assertEquals("100", event?.id)
        assertEquals("Arsenal", event?.homeTeam)
        assertEquals("Chelsea", event?.awayTeam)
        assertEquals("https://img/home.png", event?.homeTeamBadgeUrl)
        assertEquals("https://img/away.png", event?.awayTeamBadgeUrl)
        assertEquals("https://img/thumb.png", event?.thumbUrl)
    }

    @Test
    fun eventDto_toDomain_returnsNull_whenRequiredFieldsMissing() {
        val missingId = EventDto(
            idEvent = null,
            idLeague = null,
            strLeague = null,
            strSport = null,
            strCountry = null,
            strEvent = "Name",
            dateEvent = null,
            strTime = null,
            strHomeTeam = null,
            strAwayTeam = null,
            strHomeTeamBadge = null,
            strAwayTeamBadge = null,
            intHomeScore = null,
            intAwayScore = null,
            strThumb = null
        )
        val missingName = missingId.copy(idEvent = "1", strEvent = null)

        assertNull(missingId.toDomain())
        assertNull(missingName.toDomain())
    }

    @Test
    fun event_toEntity_and_entity_toDomain_preservesCachedFields() {
        val event = Event(
            id = "e1",
            leagueId = "4328",
            leagueName = "Premier League",
            sport = "Soccer",
            country = "England",
            name = "Arsenal vs Chelsea",
            date = "2026-02-26",
            time = "19:00:00",
            homeTeam = "Arsenal",
            awayTeam = "Chelsea",
            homeTeamBadgeUrl = "https://img/home.png",
            awayTeamBadgeUrl = "https://img/away.png",
            homeScore = "0",
            awayScore = "0",
            thumbUrl = "https://img/thumb.png"
        )

        val entity = event.toEntity(teamId = "team1", type = "NEXT", updatedAt = 1L)
        val mappedBack = entity.toDomain()

        assertEquals("https://img/home.png", entity.homeTeamBadgeUrl)
        assertEquals("https://img/away.png", entity.awayTeamBadgeUrl)
        assertEquals("https://img/home.png", mappedBack.homeTeamBadgeUrl)
        assertEquals("https://img/away.png", mappedBack.awayTeamBadgeUrl)
        assertEquals("Arsenal vs Chelsea", mappedBack.name)
    }
}
