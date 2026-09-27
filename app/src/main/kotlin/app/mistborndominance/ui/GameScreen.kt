package app.mistborndominance.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import app.mistborndominance.data.GameState
import app.mistborndominance.data.MISSION_COUNT
import app.mistborndominance.data.Outcome
import app.mistborndominance.ui.theme.Ash
import app.mistborndominance.ui.theme.AshPanel
import app.mistborndominance.ui.theme.AshRaised
import app.mistborndominance.ui.theme.AshTrack
import app.mistborndominance.ui.theme.Hairline
import app.mistborndominance.ui.theme.LocalDimens

// Wide and tall enough (a tablet in landscape): the track and the controls sit side by side.
private val TwoPaneMinWidth = 840.dp
private val TwoPaneMinHeight = 700.dp

// Shorter than this (a phone on its side), the screen scrolls and the track gets a fixed height.
private val CompactHeight = 640.dp
private val CompactTrackHeight = 320.dp

/** Everything the game screen can ask for; the ViewModel implements each. */
class GameActions(
    val onEdict: (dominanceUps: Int) -> Unit,
    val onUndoEdict: () -> Unit,
    val onUndoHeal: () -> Unit,
    val onAdjustHealth: (delta: Int) -> Unit,
    val onAdjustTurn: (delta: Int) -> Unit,
    val onToggleMission: (index: Int) -> Unit,
    val onEndGame: (Outcome) -> Unit,
    val onLeave: () -> Unit,
)

@Composable
fun GameScreen(game: GameState, actions: GameActions) {
    var confirmQuit by rememberSaveable { mutableStateOf(false) }
    var choosingOutcome by rememberSaveable { mutableStateOf(false) }
    // The result pops up once when the game ends; closing it leaves the final state on screen.
    var resultClosed by rememberSaveable { mutableStateOf(false) }

    val requestLeave = { if (game.isOver || game.isUntouched) actions.onLeave() else confirmQuit = true }
    val requestEnd = { choosingOutcome = true }
    BackHandler(onBack = requestLeave)

    BoxWithConstraints(Modifier.fillMaxSize().background(Ash)) {
        if (maxWidth >= TwoPaneMinWidth && maxHeight >= TwoPaneMinHeight) {
            TwoPaneGame(game, actions, onQuit = requestLeave, onEndGame = requestEnd)
        } else {
            SinglePaneGame(game, actions, compact = maxHeight < CompactHeight, onQuit = requestLeave, onEndGame = requestEnd)
        }
    }

    if (confirmQuit) {
        AlertDialog(
            onDismissRequest = { confirmQuit = false },
            title = { Text("Quit game?") },
            text = { Text("Progress on ${game.track.displayName} will be lost and the game won't be added to the log.") },
            confirmButton = {
                TextButton(onClick = { confirmQuit = false; actions.onLeave() }) { Text("Quit") }
            },
            dismissButton = {
                TextButton(onClick = { confirmQuit = false }) { Text("Cancel") }
            },
        )
    }

    if (choosingOutcome) {
        AlertDialog(
            onDismissRequest = { choosingOutcome = false },
            title = { Text("End game?") },
            text = { Text("How did it end? The result is saved to the game log.") },
            confirmButton = {
                Row {
                    for (outcome in listOf(Outcome.DEFEAT, Outcome.VICTORY)) {
                        TextButton(
                            onClick = { choosingOutcome = false; actions.onEndGame(outcome) },
                            colors = ButtonDefaults.textButtonColors(contentColor = outcome.color),
                        ) { Text(outcome.label) }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { choosingOutcome = false }) { Text("Cancel") }
            },
        )
    }

    val outcome = game.outcome
    if (outcome != null && !resultClosed) {
        AlertDialog(
            onDismissRequest = { resultClosed = true },
            title = { Text(if (outcome == Outcome.VICTORY) "Congratulations!" else "Game Over") },
            text = {
                Text(
                    (if (outcome == Outcome.VICTORY) "The Lord Ruler has fallen." else "The Lord Ruler's reign endures.") +
                        "\n\nTurns: ${game.turn}" +
                        "\nDominance: ${game.dominance}" +
                        "\nMissions completed: ${game.completedMissions.size} of $MISSION_COUNT" +
                        "\nLord Ruler health: ${game.lordRulerHealth}"
                )
            },
            confirmButton = {
                TextButton(onClick = actions.onLeave) { Text("Main Menu") }
            },
            dismissButton = {
                TextButton(onClick = { resultClosed = true }) { Text("Close") }
            },
        )
    }
}

/** Phones: the track fills the middle, between the coins and the controls docked at the bottom. */
@Composable
private fun SinglePaneGame(game: GameState, actions: GameActions, compact: Boolean, onQuit: () -> Unit, onEndGame: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
            .then(if (compact) Modifier.verticalScroll(rememberScrollState()) else Modifier),
    ) {
        GameHeader(game, onQuit, onEndGame, Modifier.padding(start = 20.dp, end = 12.dp, top = 4.dp))
        Medallions(game, Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
        OrnamentDivider(Modifier.padding(start = 20.dp, end = 20.dp, top = 4.dp))
        DominanceTrack(
            game = game,
            latestEdictColor = AshPanel,
            onUndoEdict = actions.onUndoEdict,
            onUndoHeal = actions.onUndoHeal,
            modifier = (if (compact) Modifier.height(CompactTrackHeight) else Modifier.weight(1f)).fillMaxWidth(),
            horizontalPadding = 16.dp,
            verticalPadding = 8.dp,
        )
        DockedDeck {
            if (game.isOver) ResultDeck(game, actions.onLeave, fillHeight = false) else ControlDeck(game, actions, fillHeight = false)
        }
    }
}

/** Tablets in landscape: the full track on the left, coins and controls on the right. */
@Composable
private fun TwoPaneGame(game: GameState, actions: GameActions, onQuit: () -> Unit, onEndGame: () -> Unit) {
    val shape = MaterialTheme.shapes.medium
    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 32.dp, vertical = 20.dp),
    ) {
        GameHeader(game, onQuit, onEndGame)
        Row(Modifier.weight(1f).padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(LocalDimens.current.gutter)) {
            DominanceTrack(
                game = game,
                latestEdictColor = AshRaised,
                onUndoEdict = actions.onUndoEdict,
                onUndoHeal = actions.onUndoHeal,
                modifier = Modifier
                    .weight(0.85f)
                    .fillMaxHeight()
                    .clip(shape)
                    .background(AshTrack)
                    .border(1.dp, Hairline, shape),
                horizontalPadding = 18.dp,
                verticalPadding = 14.dp,
            )
            Column(Modifier.weight(1f).fillMaxHeight()) {
                Medallions(game)
                OrnamentDivider(Modifier.padding(vertical = 18.dp))
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(shape)
                        .background(AshPanel)
                        .border(1.dp, Hairline, shape)
                        .padding(20.dp),
                ) {
                    if (game.isOver) ResultDeck(game, actions.onLeave, fillHeight = true) else ControlDeck(game, actions, fillHeight = true)
                }
            }
        }
    }
}

/** A panel docked to the bottom edge, under the navigation bar, with a diamond on its rim. */
@Composable
private fun DockedDeck(content: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AshPanel)
                .drawBehind { drawLine(Hairline, Offset.Zero, Offset(size.width, 0f), strokeWidth = 1.dp.toPx()) }
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            content = content,
        )
        Diamond(9.dp, Modifier.align(Alignment.TopCenter).offset(y = (-4.5).dp))
    }
}
