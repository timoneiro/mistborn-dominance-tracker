package app.mistborndominance.data

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class GameLogEntry(
    val id: String,
    val trackId: String,
    val outcome: Outcome,
    val turns: Int,
    val dominance: Int,
    val missionsCompleted: Int,
    val lordRulerHealth: Int,
    val endedAtMillis: Long,
) {
    companion object {
        fun of(game: GameState, endedAtMillis: Long) = GameLogEntry(
            id = UUID.randomUUID().toString(),
            trackId = game.trackId,
            outcome = checkNotNull(game.outcome) { "Only finished games are logged" },
            turns = game.turn,
            dominance = game.dominance,
            missionsCompleted = game.completedMissions.size,
            lordRulerHealth = game.lordRulerHealth,
            endedAtMillis = endedAtMillis,
        )
    }
}

/** Finished games, newest first, kept in SharedPreferences as a JSON array. */
class GameLogRepository(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _entries = MutableStateFlow(decode(prefs.getString(KEY_ENTRIES, null)))
    val entries: StateFlow<List<GameLogEntry>> = _entries.asStateFlow()

    fun add(entry: GameLogEntry) = save(listOf(entry) + _entries.value)

    fun delete(id: String) = save(_entries.value.filterNot { it.id == id })

    private fun save(entries: List<GameLogEntry>) {
        _entries.value = entries
        prefs.edit { putString(KEY_ENTRIES, encode(entries)) }
    }

    private companion object {
        const val PREFS_NAME = "game_log"
        const val KEY_ENTRIES = "entries"

        fun encode(entries: List<GameLogEntry>): String =
            JSONArray(entries.map { it.toJson() }).toString()

        fun decode(json: String?): List<GameLogEntry> {
            val array = JSONArray(json ?: return emptyList())
            return List(array.length()) { array.getJSONObject(it).toGameLogEntry() }
        }

        fun GameLogEntry.toJson(): JSONObject = JSONObject()
            .put("id", id)
            .put("trackId", trackId)
            .put("outcome", outcome.name)
            .put("turns", turns)
            .put("dominance", dominance)
            .put("missionsCompleted", missionsCompleted)
            .put("lordRulerHealth", lordRulerHealth)
            .put("endedAt", endedAtMillis)

        fun JSONObject.toGameLogEntry() = GameLogEntry(
            id = getString("id"),
            trackId = getString("trackId"),
            outcome = Outcome.valueOf(getString("outcome")),
            turns = getInt("turns"),
            dominance = getInt("dominance"),
            missionsCompleted = getInt("missionsCompleted"),
            lordRulerHealth = getInt("lordRulerHealth"),
            endedAtMillis = getLong("endedAt"),
        )
    }
}
