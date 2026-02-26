package khom.pavlo.footballlover.ui.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import khom.pavlo.footballlover.domain.model.Event
import androidx.compose.material3.Switch
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import khom.pavlo.footballlover.R
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(
    viewModel: MatchesViewModel,
    onEventSelected: (Event) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var onlyScored by rememberSaveable { mutableStateOf(false) }
    val events = if (state.selectedTab == MatchesTab.NEXT) state.nextEvents else state.lastEvents
    val filteredEvents = if (onlyScored) {
        events.filter { it.homeScore != null && it.awayScore != null }
    } else {
        events
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.teamName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
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
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SegmentedButton(
                            selected = state.selectedTab == MatchesTab.NEXT,
                            onClick = { viewModel.selectTab(MatchesTab.NEXT) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            label = { Text(stringResource(R.string.tab_next)) }
                        )
                        SegmentedButton(
                            selected = state.selectedTab == MatchesTab.LAST,
                            onClick = { viewModel.selectTab(MatchesTab.LAST) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            label = { Text(stringResource(R.string.tab_last)) }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(R.string.filter_finished_matches))
                        Switch(checked = onlyScored, onCheckedChange = { onlyScored = it })
                    }
                }
            }

            state.error?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }

            if (state.isLoading) {
                CircularProgressIndicator()
            }

            if (filteredEvents.isEmpty() && !state.isLoading) {
                Text(text = stringResource(R.string.msg_no_matches), style = MaterialTheme.typography.bodyMedium)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredEvents) { event ->
                    EventRow(event = event, onClick = { onEventSelected(event) })
                }
            }
        }
    }
}

@Composable
private fun EventRow(event: Event, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors()
    ) {
        val dateTime = listOfNotNull(event.date, event.time).joinToString(" ")
        val score = if (event.homeScore != null && event.awayScore != null) {
            "${event.homeScore} : ${event.awayScore}"
        } else {
            "vs"
        }
        val teams = "${event.homeTeam ?: "-"} • ${event.awayTeam ?: "-"}"
        ListItem(
            modifier = Modifier.heightIn(min = 88.dp),
            headlineContent = { Text(text = event.name) },
            supportingContent = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (dateTime.isNotBlank()) {
                        Text(text = dateTime)
                    }
                    Text(text = teams)
                }
            },
            leadingContent = {
                TeamBadge(
                    badgeUrl = event.homeTeamBadgeUrl,
                    contentDescription = event.homeTeam
                )
            },
            trailingContent = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = score, style = MaterialTheme.typography.titleSmall)
                    TeamBadge(
                        badgeUrl = event.awayTeamBadgeUrl,
                        contentDescription = event.awayTeam
                    )
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
private fun TeamBadge(
    badgeUrl: String?,
    contentDescription: String?
) {
    if (badgeUrl.isNullOrBlank()) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFE0E0E0))
        )
        return
    }

    AsyncImage(
        model = badgeUrl,
        contentDescription = contentDescription,
        modifier = Modifier.size(32.dp)
    )
}
