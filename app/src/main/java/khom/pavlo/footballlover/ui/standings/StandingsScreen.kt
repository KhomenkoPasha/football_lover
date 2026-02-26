package khom.pavlo.footballlover.ui.standings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import khom.pavlo.footballlover.domain.model.League
import khom.pavlo.footballlover.domain.model.StandingRow
import khom.pavlo.footballlover.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandingsScreen(
    viewModel: StandingsViewModel,
    initialLeagueId: String? = null,
    initialLeagueName: String? = null,
    onBack: (() -> Unit)? = null
) {
    val state by viewModel.state.collectAsState()
    val leagues = viewModel.filteredLeagues()
    var searchActive by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(initialLeagueId, initialLeagueName) {
        val id = initialLeagueId.orEmpty()
        if (id.isNotBlank()) {
            viewModel.preselectLeague(id, initialLeagueName.orEmpty())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_standings)) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.action_back)
                            )
                        }
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
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SearchBar(
                            modifier = Modifier.weight(1f),
                            query = state.leagueQuery,
                            onQueryChange = viewModel::onLeagueQueryChange,
                            onSearch = { searchActive = false },
                            active = searchActive,
                            onActiveChange = { searchActive = it },
                            placeholder = { Text(stringResource(R.string.hint_search_league)) }
                        ) {
                            Text(
                                text = stringResource(R.string.hint_search_league),
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        FilledTonalButton(onClick = { viewModel.loadLeagues(forceRefresh = true) }) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = null
                            )
                            Text(stringResource(R.string.action_refresh))
                        }
                    }
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.season,
                        onValueChange = viewModel::onSeasonChange,
                        placeholder = { Text(stringResource(R.string.hint_season)) },
                        singleLine = true
                    )
                }
            }

            state.error?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }

            if (state.isLoadingLeagues) {
                CircularProgressIndicator()
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(leagues) { league ->
                    LeagueRow(
                        league = league,
                        selected = state.selectedLeague?.id == league.id,
                        onClick = { viewModel.selectLeague(league) }
                    )
                }
            }

            if (state.isLoadingTable) {
                CircularProgressIndicator()
            }

        if (state.selectedLeague != null && state.table.isEmpty() && !state.isLoadingTable) {
            Text(text = stringResource(R.string.msg_no_table), style = MaterialTheme.typography.bodyMedium)
        }

        if (state.table.isNotEmpty()) {
            Text(
                text = stringResource(R.string.label_table_for, state.selectedLeague?.name ?: "-"),
                style = MaterialTheme.typography.titleMedium
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.table) { row ->
                        StandingRowItem(row = row)
                    }
                }
            }
        }
    }
}

@Composable
private fun LeagueRow(league: League, selected: Boolean, onClick: () -> Unit) {
    val colors = if (selected) {
        CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    } else {
        CardDefaults.elevatedCardColors()
    }
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = colors
    ) {
        ListItem(
            modifier = Modifier.heightIn(min = 56.dp),
            headlineContent = { Text(text = league.name) },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
private fun StandingRowItem(row: StandingRow) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors()
    ) {
        val primary = "${row.rank ?: "-"} • ${row.teamName}"
        val secondary = "P ${row.played ?: "-"}  W ${row.win ?: "-"}  D ${row.draw ?: "-"}  " +
            "L ${row.loss ?: "-"}  GD ${row.goalsDifference ?: "-"}"
        ListItem(
            modifier = Modifier.heightIn(min = 72.dp),
            headlineContent = { Text(text = primary) },
            supportingContent = { Text(text = secondary) },
            trailingContent = { Text(text = row.points ?: "-") },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}
