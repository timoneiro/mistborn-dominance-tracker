package app.mistborndominance.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.mistborndominance.data.GameLogEntry
import app.mistborndominance.data.GameLogRepository
import app.mistborndominance.data.GameState
import app.mistborndominance.data.Outcome
import app.mistborndominance.data.Track
import kotlinx.coroutines.flow.StateFlow

class DominanceTrackerViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val gameLog: GameLogRepository,
) : ViewModel() {

    /** The game in progress (or just finished and still on screen); null on the main menu. */
    val game: StateFlow<GameState?> = savedStateHandle.getStateFlow(KEY_GAME, null)

    val logEntries: StateFlow<List<GameLogEntry>> = gameLog.entries

    fun startGame(track: Track) {
        savedStateHandle[KEY_GAME] = GameState(track.id)
    }

    fun leaveGame() {
        savedStateHandle[KEY_GAME] = null
    }

    fun resolveEdict(dominanceUps: Int) = update { it.resolveEdict(dominanceUps) }

    fun undoLatestEdict() = update { it.undoLatestEdict() }

    fun undoLatestHeal() = update { it.undoLatestHeal() }

    fun adjustHealth(delta: Int) = update { it.adjustHealth(delta) }

    fun adjustTurn(delta: Int) = update { it.adjustTurn(delta) }

    fun toggleMission(index: Int) = update { it.toggleMission(index) }

    fun endGame(outcome: Outcome) = update { it.end(outcome) }

    fun deleteLogEntry(id: String) = gameLog.delete(id)

    /** Applies [transform] to a game still in progress, and logs the game the moment it ends. */
    private fun update(transform: (GameState) -> GameState) {
        val current = game.value?.takeUnless { it.isOver } ?: return
        val next = transform(current)
        savedStateHandle[KEY_GAME] = next
        if (next.isOver) gameLog.add(GameLogEntry.of(next, endedAtMillis = System.currentTimeMillis()))
    }

    companion object {
        private const val KEY_GAME = "game"

        val Factory = viewModelFactory {
            initializer {
                DominanceTrackerViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    gameLog = GameLogRepository(checkNotNull(this[APPLICATION_KEY])),
                )
            }
        }
    }
}
