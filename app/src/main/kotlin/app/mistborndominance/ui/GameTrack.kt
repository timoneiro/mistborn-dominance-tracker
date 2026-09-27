package app.mistborndominance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.mistborndominance.data.Edict
import app.mistborndominance.data.GameState
import app.mistborndominance.data.TrackRow
import app.mistborndominance.ui.theme.Atium
import app.mistborndominance.ui.theme.Hairline
import app.mistborndominance.ui.theme.HairlineFaint
import app.mistborndominance.ui.theme.Mist
import app.mistborndominance.ui.theme.MistFaded
import app.mistborndominance.ui.theme.Steel

// Unrevealed rows are drawn as blank bars of varied length, so the track still reads as text.
private val HiddenRowWidths = listOf(0.82f, 0.64f, 0.9f, 0.58f, 0.76f, 0.88f, 0.62f, 0.8f, 0.7f)
private val HiddenRowColor = Color(0xFF221F28)

/**
 * The whole Dominance Track, as the physical card lays it out: rows already passed, the rows
 * the latest Edict revealed (framed, with its undo controls), then the rows still to come.
 */
@Composable
fun DominanceTrack(
    game: GameState,
    latestEdictColor: Color,
    onUndoEdict: () -> Unit,
    onUndoHeal: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 0.dp,
    verticalPadding: Dp = 0.dp,
) {
    val trail = game.trailRows
    val latestEdict = game.latestEdict
    val listState = rememberLazyListState()

    // Keep the latest Edict in view, with the row before it as context.
    LaunchedEffect(game.edicts.size) {
        if (latestEdict != null) listState.animateScrollToItem((trail.size - 1).coerceAtLeast(0))
    }

    Column(modifier) {
        // Pinned above the list, so scrolling to the latest Edict never hides it.
        TrackHeader(game.rowsToGo, Modifier.padding(start = horizontalPadding, end = horizontalPadding, top = verticalPadding))
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = horizontalPadding, end = horizontalPadding, bottom = verticalPadding),
        ) {
            trackItems(game, latestEdictColor, onUndoEdict, onUndoHeal)
        }
    }
}

private fun LazyListScope.trackItems(
    game: GameState,
    latestEdictColor: Color,
    onUndoEdict: () -> Unit,
    onUndoHeal: () -> Unit,
) {
    val trail = game.trailRows
    val latestEdict = game.latestEdict
    if (latestEdict == null) {
        item(key = "hint") { SetupHint() }
    }
    items(trail.size, key = { "row$it" }) { index -> PastRow(trail[index]) }
    if (latestEdict != null) {
        item(key = "latest") {
            LatestEdictBlock(
                edict = latestEdict,
                rows = game.latestEdictRows,
                additionalDominance = game.latestEdictAdditionalDominance,
                finalRow = game.track.rows.last(),
                containerColor = latestEdictColor,
                showUndo = !game.isOver,
                onUndoEdict = onUndoEdict,
                onUndoHeal = onUndoHeal,
            )
        }
    }
    items(game.rowsToGo, key = { "hidden$it" }) { index -> HiddenRow(HiddenRowWidths[index % HiddenRowWidths.size]) }
}

@Composable
private fun TrackHeader(rowsToGo: Int, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        SectionLabel("Dominance track", Modifier.weight(1f))
        Text(
            text = when (rowsToGo) {
                0 -> "Final row reached"
                1 -> "1 row to go"
                else -> "$rowsToGo rows to go"
            },
            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
            color = Mist,
        )
    }
}

@Composable
private fun SetupHint() {
    Text(
        text = "Tap Bump when an Edict raises Dominance — ×2 or ×3 for Edicts that raise it more " +
            "than once. Each Edict also heals the Lord Ruler.",
        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
        color = Mist,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Hairline, MaterialTheme.shapes.medium)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    )
}

@Composable
private fun PastRow(row: TrackRow) {
    Row(
        modifier = Modifier.fillMaxWidth().bottomHairline(HairlineFaint).padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = row.effectText,
            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
            color = MistFaded,
            modifier = Modifier.weight(1f),
        )
        row.revealedDominance?.let { DominanceTag(it, filled = false) }
    }
}

@Composable
private fun HiddenRow(widthFraction: Float) {
    Box(Modifier.fillMaxWidth().bottomHairline(HairlineFaint).padding(horizontal = 4.dp, vertical = 9.dp)) {
        Box(
            Modifier
                .fillMaxWidth(widthFraction)
                .height(8.dp)
                .background(HiddenRowColor, MaterialTheme.shapes.extraSmall),
        )
    }
}

/**
 * Every row the latest Edict revealed, in order, plus any Dominance past the final row — and
 * the controls to take that Edict (or just its heal) back.
 */
@Composable
private fun LatestEdictBlock(
    edict: Edict,
    rows: List<TrackRow>,
    additionalDominance: Int,
    finalRow: TrackRow,
    containerColor: Color,
    showUndo: Boolean,
    onUndoEdict: () -> Unit,
    onUndoHeal: () -> Unit,
) {
    val shape = MaterialTheme.shapes.medium
    Column(
        Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(containerColor)
            .border(1.dp, Atium, shape),
    ) {
        Row(Modifier.padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
            SectionLabel(
                text = if (edict.dominanceUps > 1) "Latest Edict · ×${edict.dominanceUps}" else "Latest Edict",
                color = Atium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = if (edict.healed > 0) "Healed +${edict.healed}" else "No heal",
                style = MaterialTheme.typography.bodySmall,
                color = Mist,
            )
        }
        rows.forEach { row ->
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(row.effectText, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                row.revealedDominance?.let { DominanceTag(it, filled = true) }
            }
        }
        if (additionalDominance > 0) {
            Text(
                text = "+$additionalDominance additional Dominance",
                style = MaterialTheme.typography.titleMedium,
                color = Atium,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            )
            // The track was already on its final row, so this Edict revealed nothing new —
            // show that row, muted, as a reminder of what additional Dominance does.
            if (rows.isEmpty()) {
                Text(
                    text = finalRow.effectText,
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    color = MistFaded,
                    modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 6.dp),
                )
            }
        }
        if (showUndo) {
            HorizontalDivider(Modifier.padding(top = 4.dp), color = Hairline)
            Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.End) {
                val undoColors = ButtonDefaults.textButtonColors(contentColor = Steel, disabledContentColor = MistFaded.copy(alpha = 0.5f))
                TextButton(onClick = onUndoHeal, enabled = edict.healed > 0, colors = undoColors) { Text("Undo heal") }
                TextButton(onClick = onUndoEdict, colors = undoColors) { Text("Undo bump") }
            }
        } else {
            Box(Modifier.height(8.dp))
        }
    }
}

private fun Modifier.bottomHairline(color: Color): Modifier = drawBehind {
    val y = size.height - 0.5.dp.toPx()
    drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
}
