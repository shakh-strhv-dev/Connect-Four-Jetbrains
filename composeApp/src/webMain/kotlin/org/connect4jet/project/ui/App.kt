package org.connect4jet.project.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.connect4jet.project.storage.GameStorage
import org.connect4jet.project.game.GameConfig
import org.connect4jet.project.game.GameState
import org.connect4jet.project.game.Player
import org.connect4jet.project.game.CellState
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.flexDirection
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text

@Composable
fun App() {
    var config by remember { mutableStateOf(GameStorage.load()?.config ?: GameConfig()) }

    val state = remember(config) {
        val loaded = GameStorage.load()
        if (loaded != null && loaded.config == config) {
            loaded
        } else {
            GameState(config)
        }
    }

    LaunchedEffect(state.movesCount, state.winner, state.isDraw) {
        GameStorage.save(state)
    }

    Div({ classes(Theme.Css.APP) }) {
        H1 { Text(GameStrings.title(config.winLength)) }

        // Конфиг сверху
        ConfigPanel(config) { newConfig ->
            val maxAllowed = maxOf(newConfig.cols, newConfig.rows)
            val validatedWinLength = if (newConfig.winLength > maxAllowed) maxAllowed else newConfig.winLength
            val finalConfig = newConfig.copy(winLength = validatedWinLength)

            GameStorage.clear()
            config = finalConfig
        }

        // Доска посередине
        BoardGrid(state) { col ->
            state.dropPiece(col)
        }

        // Статус бар и кнопка внизу
        Div({
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                alignItems(AlignItems.Center)
                marginTop(20.px)
            }
        }) {
            StatusBar(state)

            Button({
                classes(Theme.Css.BTN_NEW_GAME)
                onClick { state.reset() }
            }) { Text(GameStrings.NEW_GAME) }
        }
    }
}

@Composable
fun BoardGrid(state: GameState, onColumnClick: (Int) -> Unit) {
    Div({ classes(Theme.Css.BOARD) }) {
        repeat(state.config.cols) { col ->
            Div({
                classes(Theme.Css.COLUMN)
                onClick { onColumnClick(col) }
            }) {
                (state.config.rows - 1 downTo 0).forEach { row ->
                    val cell = state.board[col][row]

                    CellView(cell)
                }
            }
        }
    }
}

@Composable
fun CellView(cell: CellState) {
    val player = cell.player
    val isWinning = cell.isWinningCell
    val isDimmed = cell.isDimmed
    val isJustDropped = cell.justDropped

    Div({
        var classNames = arrayOf(Theme.Css.CELL)

        when (player) {
            Player.FIRST -> classNames += Theme.Css.PLAYER_RED
            Player.SECOND -> classNames += Theme.Css.PLAYER_YELLOW
            null -> classNames += Theme.Css.EMPTY
        }

        if (isJustDropped && player != null) {
            classNames += Theme.Css.FALLING
        }

        if (isWinning) {
            classNames += Theme.Css.WIN_PULSE
        } else if (isDimmed) {
            classNames += Theme.Css.DIMMED
        }

        classes(*classNames)
    })
}