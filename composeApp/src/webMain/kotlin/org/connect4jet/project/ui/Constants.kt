package org.connect4jet.project.ui

import org.connect4jet.project.game.Player

object GameConstants {
    const val MIN_COLS = 4
    const val MAX_COLS = 15
    const val DEFAULT_COLS = 7

    const val MIN_ROWS = 4
    const val MAX_ROWS = 15
    const val DEFAULT_ROWS = 6

    const val MIN_WIN_LENGTH = 3
    const val DEFAULT_WIN_LENGTH = 4
}

object GameStrings {
    fun title(winCondition: Int) = "Connect $winCondition"
    const val NEW_GAME = "Restart Game"

    const val COLUMNS_LABEL = "Cols: "
    const val ROWS_LABEL = "Rows: "
    const val WIN_COND_LABEL = "Win: "

    const val FIRST_PLAYER_NAME = "CYAN"
    const val SECOND_PLAYER_NAME = "MAGENTA"

    fun getPlayerName(player: Player) =
        if (player == Player.FIRST) FIRST_PLAYER_NAME else SECOND_PLAYER_NAME

    fun playerWins(player: Player) = "${getPlayerName(player)} WINS! //"
    const val DRAW = "SYSTEM DRAW"
    const val CURRENT_TURN = "Target: "
}

object Theme {
    // Неоновые цвета
    const val COLOR_CYAN = "#00f0ff"
    const val COLOR_MAGENTA = "#ff003c"
    const val FIRST_PLAYER_COLOR = COLOR_CYAN
    const val SECOND_PLAYER_COLOR = COLOR_MAGENTA

    fun getPlayerColorHex(player: Player) = if (player == Player.FIRST) FIRST_PLAYER_COLOR else SECOND_PLAYER_COLOR
    object Css {
        const val APP = "app"
        const val BOARD = "board"
        const val COLUMN = "column"
        const val CELL = "cell"

        const val PLAYER_RED = "red"
        const val PLAYER_YELLOW = "yellow"
        const val EMPTY = "empty"

        const val WIN_PULSE = "win-pulse"
        const val DIMMED = "dimmed"
        const val BTN_NEW_GAME = "btn-new-game"
        const val CONFIG_PANEL = "config-panel"
        const val FALLING = "falling"
        const val STATUS_BAR = "status-bar"
    }
}