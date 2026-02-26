package khom.pavlo.footballlover.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import khom.pavlo.footballlover.R

@Composable
fun FavoritesHomeScreen(
    viewModel: HomeViewModel,
    onLeagueSelected: (leagueId: String, leagueName: String) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val state by viewModel.state.collectAsState()

    if (state.favoriteLeagues.isEmpty()) {
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.msg_no_favorite_leagues),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.padding(contentPadding),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(state.favoriteLeagues) { row ->
            LeagueRowItem(
                row = row,
                onClick = { onLeagueSelected(row.leagueId, row.name) },
                onToggleFavorite = { viewModel.toggleFavoriteLeague(row) },
                onBadgeRequested = { viewModel.ensureLeagueBadge(row.leagueId) }
            )
        }
    }
}
