package app.mistborndominance.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import app.mistborndominance.R

// Cinzel: engraved Roman capitals for titles, labels and numbers.
val Cinzel = FontFamily(
    Font(R.font.cinzel_semibold, FontWeight.SemiBold),
    Font(R.font.cinzel_bold, FontWeight.Bold),
)

// Spectral: a book serif for the effect text and everything read at length.
val Spectral = FontFamily(
    Font(R.font.spectral_regular, FontWeight.Normal),
    Font(R.font.spectral_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.spectral_medium, FontWeight.Medium),
    Font(R.font.spectral_semibold, FontWeight.SemiBold),
)

// Reference sizes, tuned for a ~6.8" phone. MistbornDominanceTheme scales these up for
// larger screens (see rememberTextScale in Theme.kt) rather than using them as-is everywhere.
val BaseTypography = Typography(
    // The BUMP button and the menu's title.
    headlineMedium = TextStyle(fontFamily = Cinzel, fontWeight = FontWeight.Bold, fontSize = 24.sp, letterSpacing = 0.16.em),
    // Dialog titles.
    headlineSmall = TextStyle(fontFamily = Cinzel, fontWeight = FontWeight.Bold, fontSize = 21.sp),
    // Screen title (the track's name).
    titleLarge = TextStyle(fontFamily = Cinzel, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, letterSpacing = 0.18.em),
    // Names in lists: difficulties, log entries.
    titleMedium = TextStyle(fontFamily = Cinzel, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    // Numerals on buttons: health steps, ×2/×3, mission numbers.
    titleSmall = TextStyle(fontFamily = Cinzel, fontWeight = FontWeight.Bold, fontSize = 21.sp),
    // Effect text of the latest Edict.
    bodyLarge = TextStyle(fontFamily = Spectral, fontSize = 17.sp, lineHeight = 23.sp),
    // Captions, trail rows, dialog text.
    bodyMedium = TextStyle(fontFamily = Spectral, fontSize = 15.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = Spectral, fontSize = 14.sp, lineHeight = 19.sp),
    // Text buttons (Quit, Undo…) — Material buttons read labelLarge.
    labelLarge = TextStyle(fontFamily = Spectral, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    // Engraved action labels (NEXT TURN) and X= tags.
    labelMedium = TextStyle(fontFamily = Cinzel, fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 0.14.em),
    // Section labels (DOMINANCE, TURN, GAME LOG…).
    labelSmall = TextStyle(fontFamily = Cinzel, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, letterSpacing = 0.2.em),
)

fun TextStyle.scaled(scale: Float): TextStyle = copy(
    fontSize = fontSize * scale,
    lineHeight = if (lineHeight.isSp) lineHeight * scale else lineHeight,
)

fun Typography.scaled(scale: Float): Typography = Typography(
    headlineMedium = headlineMedium.scaled(scale),
    headlineSmall = headlineSmall.scaled(scale),
    titleLarge = titleLarge.scaled(scale),
    titleMedium = titleMedium.scaled(scale),
    titleSmall = titleSmall.scaled(scale),
    bodyLarge = bodyLarge.scaled(scale),
    bodyMedium = bodyMedium.scaled(scale),
    bodySmall = bodySmall.scaled(scale),
    labelLarge = labelLarge.scaled(scale),
    labelMedium = labelMedium.scaled(scale),
    labelSmall = labelSmall.scaled(scale),
)
