package app.mistborndominance.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Every role the app's Material components read is set explicitly — anything left out falls
// back to Material's baseline purple, which clashes with the palette.
private val AshColors = darkColorScheme(
    primary = Atium,
    onPrimary = Ash,
    primaryContainer = AtiumDim,
    onPrimaryContainer = Bone,
    secondary = Steel,
    onSecondary = Ash,
    secondaryContainer = AshRaised,
    onSecondaryContainer = Bone,
    error = EmberText,
    onError = Ash,
    errorContainer = EmberDeep,
    onErrorContainer = EmberText,
    background = Ash,
    onBackground = Bone,
    surface = AshPanel,
    onSurface = Bone,
    surfaceVariant = AshPanel,
    onSurfaceVariant = Mist,
    surfaceContainerLowest = Ash,
    surfaceContainerLow = AshTrack,
    surfaceContainer = AshPanel,
    surfaceContainerHigh = AshRaised,
    surfaceContainerHighest = AshRaised,
    outline = HairlineStrong,
    outlineVariant = Hairline,
)

// Engraved, not bubbly: small radii everywhere, dialogs included.
private val AshShapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(6.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(10.dp),
)

/** Component sizes that grow with the device (text grows separately, via the typography). */
@Immutable
data class Dimens(
    val gutter: Dp,
    val coin: Dp,
    val control: Dp,
    val healthBar: Dp,
    val bumpHeight: Dp,
    val bumpSideWidth: Dp,
)

private val PhoneDimens = Dimens(gutter = 16.dp, coin = 78.dp, control = 44.dp, healthBar = 54.dp, bumpHeight = 62.dp, bumpSideWidth = 72.dp)
private val TabletDimens = Dimens(gutter = 28.dp, coin = 150.dp, control = 56.dp, healthBar = 66.dp, bumpHeight = 76.dp, bumpSideWidth = 104.dp)

val LocalDimens = staticCompositionLocalOf { PhoneDimens }

/**
 * Text scale relative to a ~6.8" phone (the reference the base typography sizes were tuned
 * for). Keyed off smallest-width so it reflects device size, not orientation — a phone in
 * landscape shouldn't scale up just because its rotated width crossed a breakpoint, but a
 * tablet should scale up in either orientation. Breakpoints match Material's own
 * compact/medium/expanded window size classes (600dp / 840dp).
 */
@Composable
fun rememberTextScale(): Float {
    val smallestWidthDp = LocalConfiguration.current.smallestScreenWidthDp
    return when {
        smallestWidthDp >= 840 -> 1.3f
        smallestWidthDp >= 600 -> 1.15f
        else -> 1f
    }
}

@Composable
fun MistbornDominanceTheme(content: @Composable () -> Unit) {
    val scale = rememberTextScale()
    val typography = remember(scale) { BaseTypography.scaled(scale) }
    val dimens = if (scale > 1f) TabletDimens else PhoneDimens
    CompositionLocalProvider(LocalDimens provides dimens) {
        MaterialTheme(
            colorScheme = AshColors,
            typography = typography,
            shapes = AshShapes,
            content = content,
        )
    }
}
