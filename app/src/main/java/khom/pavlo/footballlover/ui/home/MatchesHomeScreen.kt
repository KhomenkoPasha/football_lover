package khom.pavlo.footballlover.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import coil.compose.AsyncImage
import khom.pavlo.footballlover.R

@Composable
fun MatchesHomeScreen(
    viewModel: HomeViewModel,
    onLeagueSelected: (leagueId: String, leagueName: String) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val locale: Locale = context.resources.configuration.locales.get(0) ?: Locale.getDefault()
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM", locale)

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .padding(top = 12.dp)
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.days.indices.toList()) { index ->
                val day = state.days[index]
                val dateText = LocalDate.ofEpochDay(day.epochDay).format(dateFormatter)
                val labelText = if (day.isToday) {
                    stringResource(R.string.label_today)
                } else {
                    DayOfWeek.of(day.dayOfWeek).getDisplayName(TextStyle.SHORT, locale).uppercase(locale)
                }
                DayChip(
                    label = labelText,
                    date = dateText,
                    selected = index == state.selectedDayIndex,
                    onClick = { viewModel.selectDay(index) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = true,
                    onClick = {},
                    label = { Text(stringResource(R.string.label_all_matches)) }
                )
                FilterChip(
                    selected = false,
                    onClick = {},
                    label = { Text(stringResource(R.string.label_live)) }
                )
            }
            BadgedBox(
                badge = {
                    Badge { Text(state.totalMatches.toString()) }
                }
            ) {
                TextButton(onClick = {}) {
                    Text(stringResource(R.string.label_total))
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (state.favoriteLeagues.isNotEmpty()) {
                item { SectionHeader(title = stringResource(R.string.section_favorites)) }
                items(state.favoriteLeagues) { row ->
                    LeagueRowItem(
                        row = row,
                        onClick = { onLeagueSelected(row.leagueId, row.name) },
                        onToggleFavorite = { viewModel.toggleFavoriteLeague(row) },
                        onBadgeRequested = { viewModel.ensureLeagueBadge(row.leagueId) }
                    )
                }
            }
            item { SectionHeader(title = stringResource(R.string.section_other)) }
            items(state.otherLeagues) { row ->
                LeagueRowItem(
                    row = row,
                    onClick = { onLeagueSelected(row.leagueId, row.name) },
                    onToggleFavorite = { viewModel.toggleFavoriteLeague(row) },
                    onBadgeRequested = { viewModel.ensureLeagueBadge(row.leagueId) }
                )
            }
        }
    }
}

@Composable
private fun DayChip(label: String, date: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AssistChip(
            onClick = onClick,
            label = {
                Column(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = label, style = MaterialTheme.typography.labelLarge)
                    Text(text = date, style = MaterialTheme.typography.labelMedium)
                }
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
            )
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .height(3.dp)
                    .fillMaxWidth(0.6f)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun LeagueRowItem(
    row: LeagueRow,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onBadgeRequested: () -> Unit
) {
    LaunchedEffect(row.leagueId, row.badgeUrl) {
        if (row.badgeUrl.isNullOrBlank()) onBadgeRequested()
    }

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        colors = CardDefaults.elevatedCardColors()
    ) {
        ListItem(
            headlineContent = { Text(text = row.name) },
            overlineContent = { Text(text = row.region) },
            leadingContent = {
                if (row.badgeUrl.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFFE0E0E0))
                    )
                } else {
                    AsyncImage(
                        model = row.badgeUrl,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = row.count.toString())
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (row.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = if (row.isFavorite) {
                                stringResource(R.string.action_remove_favorite_league)
                            } else {
                                stringResource(R.string.action_add_favorite_league)
                            }
                        )
                    }
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

 
