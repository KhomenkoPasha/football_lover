package khom.pavlo.footballlover.ui.search

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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import khom.pavlo.footballlover.domain.model.Team
import khom.pavlo.footballlover.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onTeamSelected: (Team) -> Unit,
    onOpenStandings: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var searchActive by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_teams)) },
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
                        Text(stringResource(R.string.action_standings))
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
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = stringResource(R.string.title_find_team), style = MaterialTheme.typography.titleLarge)
                    SearchBar(
                        modifier = Modifier.fillMaxWidth(),
                        query = state.query,
                        onQueryChange = viewModel::onQueryChange,
                        onSearch = {
                            searchActive = false
                            viewModel.search(forceRefresh = true)
                        },
                        active = searchActive,
                        onActiveChange = { searchActive = it },
                        placeholder = { Text(stringResource(R.string.hint_team_search)) },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    searchActive = false
                                    viewModel.search(forceRefresh = true)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = stringResource(R.string.action_search)
                                )
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.hint_press_search),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    }
                }
            }

            state.error?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }

            if (state.teams.isEmpty() && !state.isLoading) {
                Text(text = stringResource(R.string.msg_no_teams), style = MaterialTheme.typography.bodyMedium)
            }
            if (state.teams.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.msg_found_teams, state.teams.size),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.teams) { team ->
                    TeamRow(team = team, onClick = { onTeamSelected(team) })
                }
            }
        }
    }
}

@Composable
private fun TeamRow(team: Team, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors()
    ) {
        ListItem(
            modifier = Modifier.heightIn(min = 72.dp),
            headlineContent = { Text(text = team.name) },
            supportingContent = {
                val subtitle = listOfNotNull(team.league, team.country, team.stadium).joinToString(" • ")
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
