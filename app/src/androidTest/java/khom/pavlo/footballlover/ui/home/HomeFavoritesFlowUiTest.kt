package khom.pavlo.footballlover.ui.home

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.os.LocaleListCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import khom.pavlo.footballlover.R
import khom.pavlo.footballlover.domain.model.Event
import khom.pavlo.footballlover.domain.model.FavoriteLeague
import khom.pavlo.footballlover.domain.model.League
import khom.pavlo.footballlover.domain.model.StandingRow
import khom.pavlo.footballlover.domain.model.Team
import khom.pavlo.footballlover.domain.repository.TheSportsRepository
import khom.pavlo.footballlover.domain.result.Result
import khom.pavlo.footballlover.domain.usecase.AddFavoriteLeagueUseCase
import khom.pavlo.footballlover.domain.usecase.GetEventsByDayUseCase
import khom.pavlo.footballlover.domain.usecase.GetFavoriteLeaguesUseCase
import khom.pavlo.footballlover.domain.usecase.GetLeagueBadgeUseCase
import khom.pavlo.footballlover.domain.usecase.GetLeaguesUseCase
import khom.pavlo.footballlover.domain.usecase.RemoveFavoriteLeagueUseCase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFavoritesFlowUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setUp() {
        composeRule.runOnUiThread {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
        }
    }

    @After
    fun tearDown() {
        composeRule.runOnUiThread {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
        }
    }

    @Test
    fun addLeagueToFavorites_fromAllMatches_appearsInFavoritesTab() {
        val fakeRepository = FakeHomeRepository()
        val viewModel = HomeViewModel(
            getEventsByDayUseCase = GetEventsByDayUseCase(fakeRepository),
            getLeaguesUseCase = GetLeaguesUseCase(fakeRepository),
            getFavoriteLeaguesUseCase = GetFavoriteLeaguesUseCase(fakeRepository),
            addFavoriteLeagueUseCase = AddFavoriteLeagueUseCase(fakeRepository),
            removeFavoriteLeagueUseCase = RemoveFavoriteLeagueUseCase(fakeRepository),
            getLeagueBadgeUseCase = GetLeagueBadgeUseCase(fakeRepository)
        )

        composeRule.setContent {
            HomeFavoritesTestHost(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Premier League").assertIsDisplayed()
        composeRule.onNodeWithContentDescription(string(R.string.action_add_favorite_league))
            .performClick()

        composeRule.onNodeWithText(string(R.string.nav_favorites)).performClick()

        composeRule.onNodeWithText("Premier League").assertIsDisplayed()
        composeRule.onNodeWithContentDescription(string(R.string.action_remove_favorite_league))
            .assertIsDisplayed()
    }

    @Composable
    private fun HomeFavoritesTestHost(viewModel: HomeViewModel) {
        var favoritesTab by remember { mutableStateOf(false) }

        androidx.compose.foundation.layout.Column {
            TextButton(onClick = { favoritesTab = false }) {
                Text(text = stringResource(R.string.nav_matches))
            }
            TextButton(onClick = { favoritesTab = true }) {
                Text(text = stringResource(R.string.nav_favorites))
            }

            if (favoritesTab) {
                FavoritesHomeScreen(
                    viewModel = viewModel,
                    onLeagueSelected = { _, _ -> },
                    contentPadding = PaddingValues()
                )
            } else {
                MatchesHomeScreen(
                    viewModel = viewModel,
                    onLeagueSelected = { _, _ -> },
                    contentPadding = PaddingValues()
                )
            }
        }
    }

    private fun string(resId: Int): String = composeRule.activity.getString(resId)
}

private class FakeHomeRepository : TheSportsRepository {
    private val favoriteLeagues = linkedMapOf<String, FavoriteLeague>()

    override suspend fun searchTeams(query: String, forceRefresh: Boolean): Result<List<Team>> {
        throw UnsupportedOperationException("Not used in this test")
    }

    override suspend fun teamsByLeague(leagueName: String, forceRefresh: Boolean): Result<List<Team>> {
        throw UnsupportedOperationException("Not used in this test")
    }

    override suspend fun nextEvents(teamId: String, forceRefresh: Boolean): Result<List<Event>> {
        throw UnsupportedOperationException("Not used in this test")
    }

    override suspend fun lastEvents(teamId: String, forceRefresh: Boolean): Result<List<Event>> {
        throw UnsupportedOperationException("Not used in this test")
    }

    override suspend fun eventDetails(eventId: String, forceRefresh: Boolean): Result<Event> {
        throw UnsupportedOperationException("Not used in this test")
    }

    override suspend fun leagues(sport: String, forceRefresh: Boolean): Result<List<League>> {
        return Result.Success(
            listOf(
                League(id = "4328", name = "Premier League", sport = "Soccer")
            )
        )
    }

    override suspend fun leagueBadge(leagueId: String): Result<String?> {
        return Result.Success(null)
    }

    override suspend fun favoriteLeagues(): List<FavoriteLeague> {
        return favoriteLeagues.values.toList()
    }

    override suspend fun addFavoriteLeague(leagueId: String, leagueName: String, region: String?) {
        favoriteLeagues[leagueId] = FavoriteLeague(leagueId = leagueId, name = leagueName, region = region)
    }

    override suspend fun removeFavoriteLeague(leagueId: String) {
        favoriteLeagues.remove(leagueId)
    }

    override suspend fun standings(
        leagueId: String,
        season: String,
        forceRefresh: Boolean
    ): Result<List<StandingRow>> {
        throw UnsupportedOperationException("Not used in this test")
    }

    override suspend fun eventsByDay(date: String, sport: String): Result<List<Event>> {
        return Result.Success(
            listOf(
                Event(
                    id = "event-1",
                    leagueId = "4328",
                    leagueName = "Premier League",
                    sport = "Soccer",
                    country = "England",
                    name = "Arsenal vs Chelsea",
                    date = date,
                    time = "18:30:00",
                    homeTeam = "Arsenal",
                    awayTeam = "Chelsea",
                    homeTeamBadgeUrl = null,
                    awayTeamBadgeUrl = null,
                    homeScore = null,
                    awayScore = null,
                    thumbUrl = null
                )
            )
        )
    }
}
