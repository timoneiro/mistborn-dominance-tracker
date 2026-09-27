package app.mistborndominance

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import app.mistborndominance.ui.GameActions
import app.mistborndominance.ui.GameScreen
import app.mistborndominance.ui.MainMenuScreen
import app.mistborndominance.ui.theme.MistbornDominanceTheme
import app.mistborndominance.viewmodel.DominanceTrackerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // The app is always dark, so the system bars need light icons regardless of the
        // device's own light/dark setting (the default follows the device).
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        setContent {
            MistbornDominanceTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DominanceTrackerApp()
                }
            }
        }
    }
}

@Composable
private fun DominanceTrackerApp(
    viewModel: DominanceTrackerViewModel = viewModel(factory = DominanceTrackerViewModel.Factory),
) {
    val game by viewModel.game.collectAsState()
    val logEntries by viewModel.logEntries.collectAsState()
    val gameActions = remember(viewModel) {
        GameActions(
            onEdict = viewModel::resolveEdict,
            onUndoEdict = viewModel::undoLatestEdict,
            onUndoHeal = viewModel::undoLatestHeal,
            onAdjustHealth = viewModel::adjustHealth,
            onAdjustTurn = viewModel::adjustTurn,
            onToggleMission = viewModel::toggleMission,
            onEndGame = viewModel::endGame,
            onLeave = viewModel::leaveGame,
        )
    }

    when (val current = game) {
        null -> MainMenuScreen(
            logEntries = logEntries,
            onStartGame = viewModel::startGame,
            onDeleteLogEntry = viewModel::deleteLogEntry,
        )
        else -> GameScreen(game = current, actions = gameActions)
    }
}
