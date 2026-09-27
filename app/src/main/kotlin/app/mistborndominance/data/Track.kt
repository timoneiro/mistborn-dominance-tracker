package app.mistborndominance.data

/**
 * One row of a Dominance Track, in the order it's revealed by successive "Dominance Up" bumps.
 * The physical card's front/back split and its "flip this card" instruction row are not modeled
 * here — they're a print-medium artifact, not a game effect, so the track is stored as one
 * continuous sequence of real effect rows.
 */
data class TrackRow(
    val effectText: String,
    val revealedDominance: Int? = null,
    val isOverflow: Boolean = false,
)

data class Track(
    val id: String,
    val displayName: String,
    val maxDominance: Int,
    val rows: List<TrackRow>,
) {
    init {
        require(rows.first().revealedDominance != null) {
            "Row 0 of $id must set the starting Dominance value"
        }
        require(rows.last().isOverflow) {
            "Last row of $id must be marked isOverflow"
        }
    }

    /**
     * Dominance value showing after [index] bumps (-1 = setup, before the first bump —
     * only the track's starting value is showing, no row has been revealed yet).
     */
    fun dominanceAt(index: Int): Int =
        rows.take(index.coerceAtLeast(0) + 1).mapNotNull { it.revealedDominance }.last()
}
