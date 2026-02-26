package khom.pavlo.footballlover.ui.league

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import khom.pavlo.footballlover.R
import khom.pavlo.footballlover.domain.model.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueTeamsScreen(
    viewModel: LeagueTeamsViewModel,
    onBack: () -> Unit,
    onOpenStandings: () -> Unit,
    onTeamSelected: (Team) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = state.leagueName.ifBlank { stringResource(R.string.title_league_teams) }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onOpenStandings) {
                        Text(text = stringResource(R.string.action_standings))
                    }
                    IconButton(onClick = { viewModel.load(forceRefresh = true) }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.action_refresh)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.msg_found_teams, state.teams.size),
                style = MaterialTheme.typography.bodySmall
            )

            state.error?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
                FilledTonalButton(onClick = { viewModel.load(forceRefresh = true) }) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                    Text(stringResource(R.string.action_retry))
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator()
            }

            if (state.teams.isEmpty() && !state.isLoading && state.error == null) {
                Text(
                    text = stringResource(R.string.msg_no_league_teams),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.teams) { team ->
                    LeagueTeamRow(team = team, onClick = { onTeamSelected(team) })
                }
            }
        }
    }
}

@Composable
private fun LeagueTeamRow(team: Team, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors()
    ) {
        ListItem(
            modifier = Modifier.heightIn(min = 72.dp),
            headlineContent = { Text(text = team.name) },
            supportingContent = {
                val subtitle = listOfNotNull(team.country, team.stadium).joinToString(" • ")
                if (subtitle.isNotBlank()) {
                    Text(text = subtitle)
                }
            },
            leadingContent = {
                AsyncImage(
                    model = team.badgeUrl,
                    contentDescription = team.name,
                    modifier = Modifier.size(48.dp)
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}
