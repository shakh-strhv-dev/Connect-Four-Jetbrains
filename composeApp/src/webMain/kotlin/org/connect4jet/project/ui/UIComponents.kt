package org.connect4jet.project.ui

import androidx.compose.runtime.Composable
import org.connect4jet.project.game.*
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.dom.*

@Composable
fun ConfigPanel(config: GameConfig, onConfigChanged: (GameConfig) -> Unit) {
    val maxPossibleWin = maxOf(config.cols, config.rows)

    Div({ classes(Theme.Css.CONFIG_PANEL) }) {
        // Columns config
        Label {
            Text("${GameStrings.COLUMNS_LABEL}${config.cols} ")
            Input(InputType.Range) {
                min(GameConstants.MIN_COLS.toString())
                max(GameConstants.MAX_COLS.toString())
                value(config.cols)
                onInput { event ->
                    onConfigChanged(config.copy(cols = event.value?.toInt() ?: GameConstants.DEFAULT_COLS))
                }
            }
        }

        // Rows config
        Label {
            Text("${GameStrings.ROWS_LABEL}${config.rows} ")
            Input(InputType.Range) {
                min(GameConstants.MIN_ROWS.toString())
                max(GameConstants.MAX_ROWS.toString())
                value(config.rows)
                onInput { event ->
                    onConfigChanged(config.copy(rows = event.value?.toInt() ?: GameConstants.DEFAULT_ROWS))
                }
            }
        }

        // Win condition config
        Label {
            Text("${GameStrings.WIN_COND_LABEL}${config.winLength} ")
            Input(InputType.Range) {
                min(GameConstants.MIN_WIN_LENGTH.toString())
                max(maxPossibleWin.toString())
                value(config.winLength)
                onInput { event ->
                    onConfigChanged(config.copy(winLength = event.value?.toInt() ?: GameConstants.DEFAULT_WIN_LENGTH))
                }
            }
        }
    }
}

@Composable
fun StatusBar(state: GameState) {
    Div({ classes(Theme.Css.STATUS_BAR) }) {
        when {
            state.winner != null -> {
                Span({ style { property("color", Theme.getPlayerColorHex(state.winner!!)) } }) {
                    Text(GameStrings.playerWins(state.winner!!))
                }
            }
            state.isDraw -> Text(GameStrings.DRAW)
            else -> {
                Text(GameStrings.CURRENT_TURN)
                Span({ style { property("color", Theme.getPlayerColorHex(state.currentPlayer)) } }) {
                    Text(GameStrings.getPlayerName(state.currentPlayer))
                }
            }
        }
    }
}