package khom.pavlo.footballlover.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import khom.pavlo.footballlover.di.AppContainer
import khom.pavlo.footballlover.ui.common.HomeViewModelFactory
import androidx.compose.ui.res.stringResource
import khom.pavlo.footballlover.R
import androidx.compose.material.icons.filled.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHomeScreen(
    onOpenSettings: () -> Unit,
    onOpenLeague: (leagueId: String, leagueName: String) -> Unit,
    onOpenEvent: (eventId: String) -> Unit
) {
    var selected by remember { mutableStateOf(HomeTab.MATCHES) }
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            AppContainer.getEventsByDayUseCase,
            AppContainer.getLeaguesUseCase,
            AppContainer.getFavoriteLeaguesUseCase,
            AppContainer.addFavoriteLeagueUseCase,
            AppContainer.removeFavoriteLeagueUseCase,
            AppContainer.getLeagueBadgeUseCase,
            AppContainer.getLiveEventsUseCase
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(selected.labelRes)) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.title_settings))
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                HomeTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selected == tab,
                        onClick = { selected = tab },
                        icon = { Icon(tab.icon, contentDescription = stringResource(tab.labelRes)) },
                        label = { Text(stringResource(tab.labelRes)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selected) {
            HomeTab.MATCHES -> {
                MatchesHomeScreen(
                    viewModel = homeViewModel,
                    onLeagueSelected = onOpenLeague,
                    contentPadding = innerPadding
                )
            }
            HomeTab.LIVE -> LiveMatchesHomeScreen(
                viewModel = homeViewModel,
                onEventSelected = { event -> onOpenEvent(event.id) },
                contentPadding = innerPadding
            )
            HomeTab.FAVORITES -> FavoritesHomeScreen(
                viewModel = homeViewModel,
                onLeagueSelected = onOpenLeague,
                contentPadding = innerPadding
            )
            HomeTab.LEAGUES -> FavoriteMatchesHomeScreen(
                viewModel = homeViewModel,
                onEventSelected = { event -> onOpenEvent(event.id) },
                contentPadding = innerPadding
            )
        }
    }
}

private enum class HomeTab(
    val labelRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    MATCHES(R.string.nav_matches, Icons.Filled.Home),
    LIVE(R.string.nav_live, Icons.Filled.LiveTv),
    FAVORITES(R.string.nav_favorites, Icons.Filled.Star),
    LEAGUES(R.string.nav_leagues, Icons.Filled.EmojiEvents)
}

@Composable
private fun PlaceholderScreen(label: String, contentPadding: PaddingValues) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label)
    }
}
