package app.mistborndominance.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import app.mistborndominance.data.Outcome
import app.mistborndominance.ui.theme.Ash
import app.mistborndominance.ui.theme.AshPanel
import app.mistborndominance.ui.theme.Atium
import app.mistborndominance.ui.theme.AtiumDim
import app.mistborndominance.ui.theme.AtiumRing
import app.mistborndominance.ui.theme.Bone
import app.mistborndominance.ui.theme.Cinzel
import app.mistborndominance.ui.theme.Ember
import app.mistborndominance.ui.theme.EmberDeep
import app.mistborndominance.ui.theme.EmberRing
import app.mistborndominance.ui.theme.EmberText
import app.mistborndominance.ui.theme.Hairline
import app.mistborndominance.ui.theme.HairlineFaint
import app.mistborndominance.ui.theme.HairlineStrong
import app.mistborndominance.ui.theme.Mist
import app.mistborndominance.ui.theme.MistFaded

/** Engraved section label: small Cinzel capitals. */
@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Mist,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        textAlign = textAlign,
        modifier = modifier,
    )
}

/** A small atium diamond — the ornament on dividers and the control deck's edge. */
@Composable
fun Diamond(size: Dp, modifier: Modifier = Modifier) {
    Box(modifier.size(size).rotate(45f).background(Atium))
}

/** A hairline with a diamond at its centre. */
@Composable
fun OrnamentDivider(modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(Modifier.weight(1f).height(1.dp).background(Hairline))
        Diamond(7.dp)
        Box(Modifier.weight(1f).height(1.dp).background(Hairline))
    }
}

enum class CoinStyle { Atium, Ember, Plain }

/** A stamped coin: Dominance (atium), the Lord Ruler (ember) or a plain one (the turn). */
@Composable
fun Coin(text: String, style: CoinStyle, size: Dp, modifier: Modifier = Modifier) {
    // The numeral scales with the coin rather than the font scale, so it always fits the rim.
    val fontSize = with(LocalDensity.current) {
        (size * when (text.length) { 1, 2 -> 0.41f; 3 -> 0.32f; else -> 0.26f }).toSp()
    }
    Box(
        modifier = modifier.size(size).drawBehind {
            val radius = this.size.minDimension / 2
            val diameter = this.size.minDimension
            when (style) {
                CoinStyle.Atium -> {
                    drawCircle(Atium, radius)
                    val ring = diameter * 0.019f
                    drawCircle(AtiumRing, radius - diameter * 0.052f - ring / 2, style = Stroke(ring))
                }
                CoinStyle.Ember -> {
                    drawCircle(EmberDeep, radius)
                    val rim = diameter * 0.026f
                    drawCircle(Ember, radius - rim / 2, style = Stroke(rim))
                    val ring = diameter * 0.013f
                    drawCircle(EmberRing, radius - diameter * 0.064f - ring / 2, style = Stroke(ring))
                }
                CoinStyle.Plain -> {
                    drawCircle(AshPanel, radius)
                    val rim = diameter * 0.019f
                    drawCircle(HairlineStrong, radius - rim / 2, style = Stroke(rim))
                }
            }
        },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(fontFamily = Cinzel, fontWeight = FontWeight.Bold, fontSize = fontSize),
            color = if (style == CoinStyle.Atium) Ash else Bone,
        )
    }
}

/** An "X=3" tag marking a track row that raises Dominance; [filled] for the latest Edict's rows. */
@Composable
fun DominanceTag(value: Int, filled: Boolean, modifier: Modifier = Modifier) {
    val shape = MaterialTheme.shapes.extraSmall
    Text(
        text = "X=$value",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.04.em),
        color = if (filled) Ash else Atium,
        modifier = modifier
            .background(if (filled) Atium else Color.Transparent, shape)
            .border(1.dp, if (filled) Atium else AtiumDim, shape)
            .padding(horizontal = 6.dp, vertical = 1.dp),
    )
}

/** The primary action: solid atium. */
@Composable
fun AtiumButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = Atium, contentColor = Ash),
        contentPadding = PaddingValues(horizontal = 8.dp),
        content = content,
    )
}

/** A secondary action on the same footing as the primary one: atium outline. */
@Composable
fun AtiumOutlinedButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, Atium),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Atium),
        contentPadding = PaddingValues(horizontal = 8.dp),
        content = content,
    )
}

/** A quiet bordered action (End game, previous turn). */
@Composable
fun HairlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp),
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, if (enabled) HairlineStrong else HairlineFaint),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Bone, disabledContentColor = MistFaded.copy(alpha = 0.5f)),
        contentPadding = contentPadding,
        content = content,
    )
}

val Outcome.label: String
    get() = when (this) {
        Outcome.VICTORY -> "Victory"
        Outcome.DEFEAT -> "Defeat"
    }

val Outcome.color: Color
    get() = when (this) {
        Outcome.VICTORY -> Atium
        Outcome.DEFEAT -> EmberText
    }

fun turnsLabel(turns: Int): String = if (turns == 1) "1 turn" else "$turns turns"
