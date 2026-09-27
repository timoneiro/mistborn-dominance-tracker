package app.mistborndominance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.mistborndominance.data.GameState
import app.mistborndominance.data.MISSION_COUNT
import app.mistborndominance.ui.theme.Ash
import app.mistborndominance.ui.theme.Atium
import app.mistborndominance.ui.theme.Bone
import app.mistborndominance.ui.theme.EmberDeep
import app.mistborndominance.ui.theme.EmberText
import app.mistborndominance.ui.theme.HairlineStrong
import app.mistborndominance.ui.theme.LocalDimens
import app.mistborndominance.ui.theme.Mist

private val MissionNumerals = listOf("I", "II", "III")

/** The track's name, and — while the game runs — the ways out of it. */
@Composable
fun GameHeader(game: GameState, onQuit: () -> Unit, onEndGame: () -> Unit, modifier: Modifier = Modifier) {
    // Keeps its height when the buttons go (game over), so nothing below it shifts.
    Row(
        modifier.fillMaxWidth().heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = game.track.displayName.uppercase(),
            style = MaterialTheme.typography.titleLarge,
            color = Atium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (!game.isOver) {
            TextButton(onClick = onQuit) { Text("Quit", color = Mist) }
            HairlineButton(onClick = onEndGame) { Text("End game") }
        }
    }
}

/** Dominance, the Lord Ruler's health and the turn, as three coins. */
@Composable
fun Medallions(game: GameState, modifier: Modifier = Modifier) {
    val coin = LocalDimens.current.coin
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        Medallion(
            label = "Dominance",
            caption = if (game.rowIndex < 0) "Not yet started" else "Row ${game.rowIndex + 1} of ${game.track.rows.size}",
        ) {
            Coin("X=${game.dominance}", CoinStyle.Atium, coin)
        }
        Medallion(label = "Lord Ruler", caption = "Edicts heal +${game.healPerEdict}") {
            Coin("${game.lordRulerHealth}", CoinStyle.Ember, coin)
        }
        Medallion(label = "Turn", caption = null) {
            Coin("${game.turn}", CoinStyle.Plain, coin)
        }
    }
}

@Composable
private fun Medallion(label: String, caption: String?, coin: @Composable () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionLabel(label)
        coin()
        caption?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Mist) }
    }
}

/** Health, missions, turn and Edicts — everything tapped during play, in thumb reach. */
@Composable
fun ColumnScope.ControlDeck(game: GameState, actions: GameActions, fillHeight: Boolean) {
    HealthBar(actions.onAdjustHealth)
    Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionLabel("Missions done")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (index in 0 until MISSION_COUNT) {
                    MissionToggle(index, done = index in game.completedMissions, onToggle = { actions.onToggleMission(index) })
                }
            }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionLabel("Turn")
            TurnControls(canGoBack = game.turn > 1, onAdjustTurn = actions.onAdjustTurn)
        }
    }
    if (fillHeight) Spacer(Modifier.weight(1f))
    EdictButtons(actions.onEdict, Modifier.padding(top = 14.dp))
}

/** Replaces the controls once the game is over: the result, and the way back to the menu. */
@Composable
fun ColumnScope.ResultDeck(game: GameState, onMainMenu: () -> Unit, fillHeight: Boolean) {
    val outcome = game.outcome ?: return
    Text(
        text = outcome.label.uppercase(),
        style = MaterialTheme.typography.headlineMedium,
        color = outcome.color,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Text(
        text = "${turnsLabel(game.turn)} · Dominance ${game.dominance} · " +
            "${game.completedMissions.size}/$MISSION_COUNT missions · Lord Ruler at ${game.lordRulerHealth}",
        style = MaterialTheme.typography.bodyMedium,
        color = Mist,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
    )
    if (fillHeight) Spacer(Modifier.weight(1f))
    AtiumButton(
        onClick = onMainMenu,
        modifier = Modifier.fillMaxWidth().padding(top = 14.dp).height(LocalDimens.current.bumpHeight),
    ) {
        Text("MAIN MENU", style = MaterialTheme.typography.headlineMedium)
    }
}

/** Damage on the left, healing on the right, as one engraved bar. */
@Composable
private fun HealthBar(onAdjustHealth: (delta: Int) -> Unit) {
    val shape = MaterialTheme.shapes.medium
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth()) {
            SectionLabel("Damage", Modifier.weight(1f), textAlign = TextAlign.Center)
            SectionLabel("Heal", Modifier.weight(1f), textAlign = TextAlign.Center)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(LocalDimens.current.healthBar)
                .clip(shape)
                .border(1.dp, HairlineStrong, shape),
        ) {
            HealthStep(-5, EmberDeep, EmberText, onAdjustHealth)
            VerticalDivider(color = HairlineStrong)
            HealthStep(-1, EmberDeep, EmberText, onAdjustHealth)
            VerticalDivider(color = Atium)
            HealthStep(1, Ash, Bone, onAdjustHealth)
            VerticalDivider(color = HairlineStrong)
            HealthStep(5, Ash, Bone, onAdjustHealth)
        }
    }
}

@Composable
private fun RowScope.HealthStep(delta: Int, background: Color, color: Color, onAdjustHealth: (delta: Int) -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(background)
            .clickable(
                role = Role.Button,
                onClickLabel = if (delta < 0) "Damage the Lord Ruler by ${-delta}" else "Heal the Lord Ruler by $delta",
                onClick = { onAdjustHealth(delta) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(if (delta < 0) "−${-delta}" else "+$delta", style = MaterialTheme.typography.titleSmall, color = color)
    }
}

@Composable
private fun MissionToggle(index: Int, done: Boolean, onToggle: () -> Unit) {
    val shape = MaterialTheme.shapes.small
    Box(
        modifier = Modifier
            .size(LocalDimens.current.control)
            .clip(shape)
            .background(if (done) Atium else Color.Transparent)
            .border(1.dp, if (done) Atium else HairlineStrong, shape)
            .toggleable(value = done, role = Role.Checkbox, onValueChange = { onToggle() })
            .semantics { contentDescription = "Mission ${index + 1}" },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = MissionNumerals[index],
            style = MaterialTheme.typography.titleSmall.copy(fontSize = MaterialTheme.typography.titleSmall.fontSize * 0.78f),
            color = if (done) Ash else Mist,
        )
    }
}

@Composable
private fun TurnControls(canGoBack: Boolean, onAdjustTurn: (delta: Int) -> Unit) {
    val control = LocalDimens.current.control
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        HairlineButton(
            onClick = { onAdjustTurn(-1) },
            enabled = canGoBack,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(control).semantics { contentDescription = "Previous turn" },
        ) {
            Text("−", style = MaterialTheme.typography.titleSmall)
        }
        AtiumOutlinedButton(onClick = { onAdjustTurn(1) }, modifier = Modifier.weight(1f).height(control)) {
            Text("NEXT TURN", style = MaterialTheme.typography.labelMedium, maxLines = 1)
        }
    }
}

@Composable
private fun EdictButtons(onEdict: (dominanceUps: Int) -> Unit, modifier: Modifier = Modifier) {
    val dimens = LocalDimens.current
    Row(modifier.fillMaxWidth().height(dimens.bumpHeight), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        AtiumButton(onClick = { onEdict(1) }, modifier = Modifier.weight(1f).fillMaxHeight()) {
            Text("BUMP", style = MaterialTheme.typography.headlineMedium)
        }
        for (dominanceUps in 2..3) {
            AtiumOutlinedButton(
                onClick = { onEdict(dominanceUps) },
                modifier = Modifier
                    .width(dimens.bumpSideWidth)
                    .fillMaxHeight()
                    .semantics { contentDescription = "Bump $dominanceUps times" },
            ) {
                Text("×$dominanceUps", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}
