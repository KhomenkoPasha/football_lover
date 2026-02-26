package khom.pavlo.footballlover.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import khom.pavlo.footballlover.di.AppContainer
import khom.pavlo.footballlover.ui.common.EventDetailViewModelFactory
import khom.pavlo.footballlover.ui.common.MatchesViewModelFactory
import khom.pavlo.footballlover.ui.common.SearchViewModelFactory
import khom.pavlo.footballlover.ui.common.LeagueTeamsViewModelFactory
import khom.pavlo.footballlover.ui.home.MainHomeScreen
import khom.pavlo.footballlover.ui.details.EventDetailScreen
import khom.pavlo.footballlover.ui.details.EventDetailViewModel
import khom.pavlo.footballlover.ui.league.LeagueTeamsScreen
import khom.pavlo.footballlover.ui.league.LeagueTeamsViewModel
import khom.pavlo.footballlover.ui.matches.MatchesScreen
import khom.pavlo.footballlover.ui.matches.MatchesViewModel
import khom.pavlo.footballlover.ui.search.SearchScreen
import khom.pavlo.footballlover.ui.search.SearchViewModel
import khom.pavlo.footballlover.ui.common.StandingsViewModelFactory
import khom.pavlo.footballlover.ui.standings.StandingsScreen
import khom.pavlo.footballlover.ui.standings.StandingsViewModel
import khom.pavlo.footballlover.ui.settings.SettingsScreen
import khom.pavlo.footballlover.ui.theme.FootballLoverTheme
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppContainer.init(applicationContext)
        ensureDefaultLanguage()
        enableEdgeToEdge()
        setContent {
            FootballLoverTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    FootballLoverApp()
                }
            }
        }
    }
}

@Composable
fun FootballLoverApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            MainHomeScreen(
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onOpenLeague = { leagueId, leagueName ->
                    val encoded = URLEncoder.encode(leagueName, StandardCharsets.UTF_8.toString())
                    navController.navigate("${Routes.LEAGUE_TEAMS}/$leagueId/$encoded")
                },
                onOpenEvent = { eventId ->
                    navController.navigate("${Routes.EVENT}/$eventId")
                }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = "${Routes.LEAGUE_TEAMS}/{leagueId}/{leagueName}",
            arguments = listOf(
                navArgument("leagueId") { type = NavType.StringType },
                navArgument("leagueName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val leagueId = backStackEntry.arguments?.getString("leagueId").orEmpty()
            val leagueName = URLDecoder.decode(
                backStackEntry.arguments?.getString("leagueName").orEmpty(),
                StandardCharsets.UTF_8.toString()
            )
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<LeagueTeamsViewModel>(
                factory = LeagueTeamsViewModelFactory(
                    AppContainer.getTeamsByLeagueUseCase,
                    leagueName
                )
            )
            LeagueTeamsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onOpenStandings = {
                    val encoded = URLEncoder.encode(leagueName, StandardCharsets.UTF_8.toString())
                    navController.navigate("${Routes.STANDINGS}/$leagueId/$encoded")
                },
                onTeamSelected = { team ->
                    val encoded = URLEncoder.encode(team.name, StandardCharsets.UTF_8.toString())
                    navController.navigate("${Routes.TEAM}/${team.id}/$encoded")
                }
            )
        }
        composable(
            route = "${Routes.STANDINGS}/{leagueId}/{leagueName}",
            arguments = listOf(
                navArgument("leagueId") { type = NavType.StringType },
                navArgument("leagueName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val leagueId = backStackEntry.arguments?.getString("leagueId").orEmpty()
            val leagueName = URLDecoder.decode(
                backStackEntry.arguments?.getString("leagueName").orEmpty(),
                StandardCharsets.UTF_8.toString()
            )
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<StandingsViewModel>(
                factory = StandingsViewModelFactory(
                    AppContainer.getLeaguesUseCase,
                    AppContainer.getStandingsUseCase
                )
            )
            StandingsScreen(
                viewModel = viewModel,
                initialLeagueId = leagueId,
                initialLeagueName = leagueName,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SEARCH) {
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<SearchViewModel>(
                factory = SearchViewModelFactory(AppContainer.searchTeamsUseCase)
            )
            SearchScreen(
                viewModel = viewModel,
                onTeamSelected = { team ->
                    val encoded = URLEncoder.encode(team.name, StandardCharsets.UTF_8.toString())
                    navController.navigate("${Routes.TEAM}/${team.id}/$encoded")
                },
                onOpenStandings = { navController.navigate(Routes.STANDINGS) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.STANDINGS) {
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<StandingsViewModel>(
                factory = StandingsViewModelFactory(
                    AppContainer.getLeaguesUseCase,
                    AppContainer.getStandingsUseCase
                )
            )
            StandingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "${Routes.TEAM}/{teamId}/{teamName}",
            arguments = listOf(
                navArgument("teamId") { type = NavType.StringType },
                navArgument("teamName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getString("teamId").orEmpty()
            val teamName = URLDecoder.decode(
                backStackEntry.arguments?.getString("teamName").orEmpty(),
                StandardCharsets.UTF_8.toString()
            )
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<MatchesViewModel>(
                factory = MatchesViewModelFactory(
                    AppContainer.getNextEventsUseCase,
                    AppContainer.getLastEventsUseCase,
                    teamId,
                    teamName
                )
            )
            MatchesScreen(
                viewModel = viewModel,
                onEventSelected = { event ->
                    navController.navigate("${Routes.EVENT}/${event.id}")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "${Routes.EVENT}/{eventId}",
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId").orEmpty()
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<EventDetailViewModel>(
                factory = EventDetailViewModelFactory(AppContainer.getEventDetailsUseCase, eventId)
            )
            EventDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FootballLoverTheme {
        FootballLoverApp()
    }
}

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val SEARCH = "search"
    const val TEAM = "team"
    const val EVENT = "event"
    const val STANDINGS = "standings"
    const val LEAGUE_TEAMS = "league_teams"
}

private fun ensureDefaultLanguage() {
    val current = AppCompatDelegate.getApplicationLocales()
    if (current.isEmpty) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
    }
}
