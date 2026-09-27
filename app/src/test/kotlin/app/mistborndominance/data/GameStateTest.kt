package app.mistborndominance.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameStateTest {

    // Intro: 15 rows, the last being the overflow row.
    private val intro = Tracks.byId("intro")!!
    private val newGame = GameState(trackId = "intro")

    @Test
    fun `a new game starts in setup at full health on turn 1`() {
        assertEquals(LORD_RULER_MAX_HEALTH, newGame.lordRulerHealth)
        assertEquals(1, newGame.turn)
        assertEquals(-1, newGame.rowIndex)
        assertEquals(1, newGame.dominance)
        assertTrue(newGame.latestEdictRows.isEmpty())
        assertTrue(newGame.trailRows.isEmpty())
        assertTrue(newGame.isUntouched)
        assertFalse(newGame.isOver)
    }

    @Test
    fun `each Edict heals 10 per incomplete mission`() {
        val damaged = newGame.adjustHealth(-40)
        val healsByMissionsDone = (0..MISSION_COUNT).map { done ->
            val game = (0 until done).fold(damaged) { game, mission -> game.toggleMission(mission) }
            game.resolveEdict(1).lordRulerHealth - damaged.lordRulerHealth
        }
        assertEquals(listOf(30, 20, 10, 0), healsByMissionsDone)
    }

    @Test
    fun `healing never takes the Lord Ruler above his starting health`() {
        assertEquals(LORD_RULER_MAX_HEALTH, newGame.adjustHealth(-5).resolveEdict(1).lordRulerHealth)
        assertEquals(LORD_RULER_MAX_HEALTH, newGame.adjustHealth(10).lordRulerHealth)
    }

    @Test
    fun `a multi-bump Edict reveals every row it passes but heals only once`() {
        val game = newGame.adjustHealth(-40).resolveEdict(3)
        assertEquals(8 + 30, game.lordRulerHealth)
        assertEquals(2, game.rowIndex)
        assertEquals(intro.rows.subList(0, 3), game.latestEdictRows)
        assertTrue(game.trailRows.isEmpty())
    }

    @Test
    fun `rows to go counts down to 0 and stays there past the final row`() {
        assertEquals(intro.rows.size, newGame.rowsToGo)
        assertEquals(intro.rows.size - 3, newGame.resolveEdict(3).rowsToGo)
        assertEquals(0, newGame.resolveEdict(intro.rows.size + 2).rowsToGo)
    }

    @Test
    fun `rows from earlier Edicts move to the trail`() {
        val game = newGame.resolveEdict(2).resolveEdict(3)
        assertEquals(intro.rows.subList(0, 2), game.trailRows)
        assertEquals(intro.rows.subList(2, 5), game.latestEdictRows)
        assertEquals(2, game.dominance) // Intro's 4th row raises Dominance to 2.
    }

    @Test
    fun `Dominance Ups past the final row count as additional Dominance`() {
        val nearEnd = newGame.resolveEdict(14)
        assertEquals(13, nearEnd.rowIndex)

        // Crossing the end: the final row is revealed, and the rest is additional.
        val crossing = nearEnd.resolveEdict(3)
        assertEquals(intro.rows.lastIndex, crossing.rowIndex)
        assertEquals(listOf(intro.rows.last()), crossing.latestEdictRows)
        assertEquals(2, crossing.latestEdictAdditionalDominance)

        // Already at the end: nothing new is revealed, everything is additional.
        val past = crossing.resolveEdict(2)
        assertEquals(intro.rows.lastIndex, past.rowIndex)
        assertTrue(past.latestEdictRows.isEmpty())
        assertEquals(2, past.latestEdictAdditionalDominance)
        assertEquals(intro.rows, past.trailRows)
        assertEquals(intro.maxDominance, past.dominance)
    }

    @Test
    fun `an Edict records only what it actually healed after the cap`() {
        assertEquals(Edict(dominanceUps = 2, healed = 5), newGame.adjustHealth(-5).resolveEdict(2).latestEdict)
    }

    @Test
    fun `undoing an Edict takes back its Dominance and heal but keeps damage dealt since`() {
        val before = newGame.adjustHealth(-40).resolveEdict(1)
        val game = before.resolveEdict(3).adjustHealth(-4).undoLatestEdict()
        assertEquals(before.dominanceUps, game.dominanceUps)
        assertEquals(before.lordRulerHealth - 4, game.lordRulerHealth)
        assertEquals(before.latestEdictRows, game.latestEdictRows)
        assertEquals(before.trailRows, game.trailRows)
    }

    @Test
    fun `undoing every Edict returns to setup`() {
        val game = newGame.adjustHealth(-10).resolveEdict(2).resolveEdict(1)
            .undoLatestEdict().undoLatestEdict().undoLatestEdict()
        assertEquals(newGame.adjustHealth(-10), game)
        assertEquals(-1, game.rowIndex)
    }

    @Test
    fun `undoing a heal keeps the Edict's Dominance and can't be repeated`() {
        val game = newGame.adjustHealth(-40).resolveEdict(2).undoLatestHeal()
        assertEquals(8, game.lordRulerHealth)
        assertEquals(2, game.dominanceUps)
        assertEquals(Edict(dominanceUps = 2, healed = 0), game.latestEdict)
        assertEquals(game, game.undoLatestHeal())
        // And undoing the whole Edict afterwards doesn't take the heal back twice.
        assertEquals(8, game.undoLatestEdict().lordRulerHealth)
    }

    @Test
    fun `undoing a heal the Lord Ruler needed to survive wins the game`() {
        val game = newGame.adjustHealth(-40).resolveEdict(1).adjustHealth(-35)
        assertEquals(3, game.lordRulerHealth)
        assertEquals(Outcome.VICTORY, game.undoLatestHeal().outcome)
    }

    @Test
    fun `undo does nothing before the first Edict`() {
        assertEquals(newGame, newGame.undoLatestEdict())
        assertEquals(newGame, newGame.undoLatestHeal())
    }

    @Test
    fun `reducing the Lord Ruler to 0 health wins, and health never goes negative`() {
        val game = newGame.adjustHealth(-45).adjustHealth(-5)
        assertEquals(0, game.lordRulerHealth)
        assertEquals(Outcome.VICTORY, game.outcome)
        assertNull(newGame.adjustHealth(-47).outcome)
    }

    @Test
    fun `the turn count never drops below 1`() {
        assertEquals(1, newGame.adjustTurn(-1).turn)
        assertEquals(3, newGame.adjustTurn(1).adjustTurn(1).turn)
    }

    @Test
    fun `missions toggle on and off`() {
        val game = newGame.toggleMission(1)
        assertEquals(setOf(1), game.completedMissions)
        assertEquals(emptySet<Int>(), game.toggleMission(1).completedMissions)
        assertEquals(20, game.healPerEdict)
    }

    @Test
    fun `any change means the game is no longer untouched`() {
        assertFalse(newGame.adjustTurn(1).isUntouched)
        assertFalse(newGame.toggleMission(0).isUntouched)
        assertFalse(newGame.resolveEdict(1).isUntouched)
    }

    @Test
    fun `ending the game records the outcome`() {
        assertEquals(Outcome.DEFEAT, newGame.resolveEdict(2).end(Outcome.DEFEAT).outcome)
    }
}
