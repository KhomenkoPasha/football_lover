package khom.pavlo.footballlover.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import khom.pavlo.footballlover.R
import khom.pavlo.footballlover.domain.model.Event

@Composable
fun FavoriteMatchesHomeScreen(
    viewModel: HomeViewModel,
    onEventSelected: (Event) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (state.isLoadingTodayFavoriteMatches) {
            CircularProgressIndicator()
        }

        state.todayFavoriteMatchesError?.let { error ->
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }

        if (!state.isLoadingTodayFavoriteMatches && state.todayFavoriteMatches.isEmpty()) {
            Text(
                text = if (state.favoriteLeagues.isEmpty()) {
                    stringResource(R.string.msg_no_favorite_leagues)
                } else {
                    stringResource(R.string.msg_no_favorite_matches_today)
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.todayFavoriteMatches) { event ->
                FavoriteMatchRow(
                    event = event,
                    onClick = { onEventSelected(event) }
                )
            }
        }
    }
}

@Composable
private fun FavoriteMatchRow(
    event: Event,
    onClick: () -> Unit
) {
    val dateTime = listOfNotNull(event.date, event.time).joinToString(" ")
    val score = if (event.homeScore != null && event.awayScore != null) {
        "${event.homeScore} : ${event.awayScore}"
    } else {
        "vs"
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = event.id.isNotBlank(), onClick = onClick),
        colors = CardDefaults.elevatedCardColors()
    ) {
        ListItem(
            modifier = Modifier.heightIn(min = 92.dp),
            overlineContent = {
                Text(text = event.leagueName ?: "—")
            },
            headlineContent = { Text(text = event.name) },
            supportingContent = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (dateTime.isNotBlank()) {
                        Text(text = dateTime)
                    }
                    Text(text = "${event.homeTeam ?: "-"} • ${event.awayTeam ?: "-"}")
                }
            },
            leadingContent = {
                MatchTeamBadge(badgeUrl = event.homeTeamBadgeUrl, description = event.homeTeam)
            },
            trailingContent = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = score, style = MaterialTheme.typography.titleSmall)
                    MatchTeamBadge(badgeUrl = event.awayTeamBadgeUrl, description = event.awayTeam)
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
private fun MatchTeamBadge(badgeUrl: String?, description: String?) {
    if (badgeUrl.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFE0E0E0))
        )
        return
    }

    AsyncImage(
        model = badgeUrl,
        contentDescription = description,
        modifier = Modifier.size(32.dp)
    )
}
