package app.mistborndominance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import app.mistborndominance.R
import app.mistborndominance.data.GameLogEntry
import app.mistborndominance.data.MISSION_COUNT
import app.mistborndominance.data.Outcome
import app.mistborndominance.data.Track
import app.mistborndominance.data.Tracks
import app.mistborndominance.ui.theme.Ash
import app.mistborndominance.ui.theme.Atium
import app.mistborndominance.ui.theme.BoneDim
import app.mistborndominance.ui.theme.EmberText
import app.mistborndominance.ui.theme.Hairline
import app.mistborndominance.ui.theme.HairlineFaint
import app.mistborndominance.ui.theme.Mist
import app.mistborndominance.ui.theme.MistFaded
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val LogDateFormat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)

// Past this the menu reads as a column down the middle rather than stretching edge to edge.
private val MenuMaxWidth = 640.dp

@Composable
fun MainMenuScreen(
    logEntries: List<GameLogEntry>,
    onStartGame: (Track) -> Unit,
    onDeleteLogEntry: (id: String) -> Unit,
) {
    var pendingDeleteId by rememberSaveable { mutableStateOf<String?>(null) }
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(Modifier.fillMaxSize().background(Ash), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = MenuMaxWidth)
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 20.dp + bottomInset),
        ) {
            item {
                Text("DOMINANCE TRACKER", style = MaterialTheme.typography.headlineMedium, color = Atium)
                Text(
                    text = "Mistborn · Solo & Co-op",
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    color = Mist,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }

            item { MenuSection("New game", Modifier.padding(top = 26.dp)) }
            itemsIndexed(Tracks.all, key = { _, track -> track.id }) { index, track ->
                DifficultyRow(track, showDivider = index < Tracks.all.lastIndex, onClick = { onStartGame(track) })
            }

            item {
                val won = logEntries.count { it.outcome == Outcome.VICTORY }
                MenuSection(
                    title = "Game log",
                    trailing = if (logEntries.isEmpty()) null else "$won won · ${logEntries.size - won} lost",
                    modifier = Modifier.padding(top = 26.dp),
                )
            }
            if (logEntries.isEmpty()) {
                item {
                    Text(
                        text = "Finished games will appear here.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        color = Mist,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                }
            }
            itemsIndexed(logEntries, key = { _, entry -> entry.id }) { index, entry ->
                LogEntryRow(entry, showDivider = index < logEntries.lastIndex, onDelete = { pendingDeleteId = entry.id })
            }

            // The statement Brotherwise and Dragonsteel ask fan-made game content to carry.
            item {
                Text(
                    text = "This is unofficial fan content, created and shared for non-commercial use. " +
                        "It has not been reviewed by Dragonsteel Entertainment, LLC or Brotherwise Games, LLC.",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = MistFaded,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                )
            }
        }
    }

    logEntries.find { it.id == pendingDeleteId }?.let { entry ->
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("Delete this game?") },
            text = { Text("${entry.trackName} · ${entry.outcome.label} · ${entry.endedAtText}\n\nThis can't be undone.") },
            confirmButton = {
                TextButton(
                    onClick = { pendingDeleteId = null; onDeleteLogEntry(entry.id) },
                    colors = ButtonDefaults.textButtonColors(contentColor = EmberText),
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) { Text("Cancel") }
            },
        )
    }
}

/** A section title followed by a hairline running to the edge, with optional text at the end. */
@Composable
private fun MenuSection(title: String, modifier: Modifier = Modifier, trailing: String? = null) {
    Row(
        modifier = modifier.fillMaxWidth().padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SectionLabel(title)
        HorizontalDivider(Modifier.weight(1f), color = Hairline)
        trailing?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Mist) }
    }
}

@Composable
private fun DifficultyRow(track: Track, showDivider: Boolean, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = track.displayName,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = MaterialTheme.typography.titleMedium.fontSize * 1.05f),
                // Extreme stands apart from the rest, so it takes the Lord Ruler's colour.
                color = if (track.id == "extreme") EmberText else MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f),
            )
            Text("Max X=${track.maxDominance}", style = MaterialTheme.typography.bodyMedium, color = Mist)
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Mist.copy(alpha = 0.55f),
                modifier = Modifier.size(18.dp),
            )
        }
        if (showDivider) HorizontalDivider(color = HairlineFaint)
    }
}

@Composable
private fun LogEntryRow(entry: GameLogEntry, showDivider: Boolean, onDelete: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(entry.trackName, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = entry.outcome.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.18.em),
                        color = entry.outcome.color,
                    )
                }
                Text(
                    text = "${turnsLabel(entry.turns)} · Dominance ${entry.dominance} · " +
                        "${entry.missionsCompleted}/$MISSION_COUNT missions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BoneDim,
                )
                Text(
                    text = "Lord Ruler at ${entry.lordRulerHealth} · ${entry.endedAtText}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Mist,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = "Delete this game",
                    tint = Mist,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        if (showDivider) HorizontalDivider(color = HairlineFaint)
    }
}

private val GameLogEntry.trackName: String
    get() = Tracks.byId(trackId)?.displayName ?: trackId

private val GameLogEntry.endedAtText: String
    get() = Instant.ofEpochMilli(endedAtMillis).atZone(ZoneId.systemDefault()).format(LogDateFormat)
