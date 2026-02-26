package khom.pavlo.footballlover.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import khom.pavlo.footballlover.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    viewModel: EventDetailViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_match_details)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
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
            if (state.isLoading) {
                CircularProgressIndicator()
            }

            state.error?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
                FilledTonalButton(onClick = viewModel::load) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                    Text(stringResource(R.string.action_retry))
                }
            }

            state.event?.let { event ->
                Text(text = event.name, style = MaterialTheme.typography.headlineMedium)
                val dateTime = listOfNotNull(event.date, event.time).joinToString(" ")
                if (dateTime.isNotBlank()) {
                    Text(text = dateTime, style = MaterialTheme.typography.bodySmall)
                }
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "${event.homeTeam ?: "-"} vs ${event.awayTeam ?: "-"}")
                    val score = if (event.homeScore != null && event.awayScore != null) {
                        "${event.homeScore} : ${event.awayScore}"
                    } else {
                        stringResource(R.string.msg_no_score)
                    }
                        Text(text = score, style = MaterialTheme.typography.titleSmall)
                    }
                }
                if (!event.thumbUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = event.thumbUrl,
                        contentDescription = event.name,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
