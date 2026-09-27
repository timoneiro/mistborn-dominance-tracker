package app.mistborndominance.data

import java.io.Serializable

const val LORD_RULER_MAX_HEALTH = 48
const val MISSION_COUNT = 3
private const val HEAL_PER_INCOMPLETE_MISSION = 10

enum class Outcome { VICTORY, DEFEAT }

/**
 * One resolved Edict: how many times it raised Dominance, and how much it actually healed the
 * Lord Ruler (after the health cap) — what undoing it has to take back.
 */
data class Edict(val dominanceUps: Int, val healed: Int) : Serializable

/**
 * A Solo/Co-op game against the Lord Ruler, as an immutable value. Every rule lives in the
 * transition functions below, so callers only ever store the state those return.
 *
 * [Serializable] so a SavedStateHandle can hold it across process death.
 */
data class GameState(
    val trackId: String,
    /** Every Edict resolved so far, oldest first. */
    val edicts: List<Edict> = emptyList(),
    val lordRulerHealth: Int = LORD_RULER_MAX_HEALTH,
    val turn: Int = 1,
    /** Which missions (0 until [MISSION_COUNT]) have reached the end of their tracker. */
    val completedMissions: Set<Int> = emptySet(),
    val outcome: Outcome? = null,
) : Serializable {

    val track: Track get() = checkNotNull(Tracks.byId(trackId)) { "Unknown track $trackId" }

    val isOver: Boolean get() = outcome != null

    /** Nothing has happened yet, so leaving this game loses nothing. */
    val isUntouched: Boolean get() = this == GameState(trackId)

    val latestEdict: Edict? get() = edicts.lastOrNull()

    /**
     * Total Dominance Ups so far. Can run past the track's last row: each Up beyond it is
     * "additional Dominance", as the track's final (overflow) row describes.
     */
    val dominanceUps: Int get() = edicts.sumOf { it.dominanceUps }

    private val dominanceUpsBeforeLatestEdict: Int get() = dominanceUps - (latestEdict?.dominanceUps ?: 0)

    /** Row the track is showing, or -1 during setup (before the first Dominance Up). */
    val rowIndex: Int get() = (dominanceUps - 1).coerceAtMost(track.rows.lastIndex)

    val dominance: Int get() = track.dominanceAt(rowIndex)

    /** Rows revealed by Edicts before the latest one. */
    val trailRows: List<TrackRow> get() = track.rows.subList(0, revealedRowCount(dominanceUpsBeforeLatestEdict))

    /**
     * Rows the latest Edict revealed, in order. Empty if the track was already on its final row,
     * in which case the Edict only added [latestEdictAdditionalDominance].
     */
    val latestEdictRows: List<TrackRow>
        get() = track.rows.subList(revealedRowCount(dominanceUpsBeforeLatestEdict), revealedRowCount(dominanceUps))

    /** Rows not revealed yet. */
    val rowsToGo: Int get() = track.rows.size - revealedRowCount(dominanceUps)

    /** Dominance Ups from the latest Edict that went past the final row. */
    val latestEdictAdditionalDominance: Int
        get() = additionalDominance(dominanceUps) - additionalDominance(dominanceUpsBeforeLatestEdict)

    val healPerEdict: Int get() = HEAL_PER_INCOMPLETE_MISSION * (MISSION_COUNT - completedMissions.size)

    /** An Edict raises Dominance [dominanceUps] times, then heals the Lord Ruler once. */
    fun resolveEdict(dominanceUps: Int): GameState {
        require(dominanceUps > 0) { "An Edict raises Dominance at least once" }
        val health = (lordRulerHealth + healPerEdict).coerceAtMost(LORD_RULER_MAX_HEALTH)
        return copy(
            edicts = edicts + Edict(dominanceUps, healed = health - lordRulerHealth),
            lordRulerHealth = health,
        )
    }

    /**
     * Takes back the latest Edict — its Dominance Ups and whatever it healed. Damage dealt since
     * stands, so if that damage would have finished the Lord Ruler without the heal, it now does.
     */
    fun undoLatestEdict(): GameState {
        val latest = latestEdict ?: return this
        return copy(edicts = edicts.dropLast(1)).adjustHealth(-latest.healed)
    }

    /** For an Edict that doesn't heal: takes back the latest Edict's heal but keeps its Dominance Ups. */
    fun undoLatestHeal(): GameState {
        val latest = latestEdict ?: return this
        return copy(edicts = edicts.dropLast(1) + latest.copy(healed = 0)).adjustHealth(-latest.healed)
    }

    /** Damages (negative [delta]) or heals the Lord Ruler. Bringing him to 0 wins the game. */
    fun adjustHealth(delta: Int): GameState {
        val health = (lordRulerHealth + delta).coerceIn(0, LORD_RULER_MAX_HEALTH)
        return copy(lordRulerHealth = health, outcome = if (health == 0) Outcome.VICTORY else outcome)
    }

    fun adjustTurn(delta: Int): GameState = copy(turn = (turn + delta).coerceAtLeast(1))

    fun toggleMission(index: Int): GameState {
        require(index in 0 until MISSION_COUNT) { "No mission $index" }
        val missions = if (index in completedMissions) completedMissions - index else completedMissions + index
        return copy(completedMissions = missions)
    }

    fun end(outcome: Outcome): GameState = copy(outcome = outcome)

    private fun revealedRowCount(ups: Int) = ups.coerceAtMost(track.rows.size)

    private fun additionalDominance(ups: Int) = (ups - track.rows.size).coerceAtLeast(0)
}
