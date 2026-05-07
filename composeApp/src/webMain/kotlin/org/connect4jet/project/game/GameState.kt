package org.connect4jet.project.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.Serializable
import org.connect4jet.project.storage.GameStateDTO

@Serializable
data class GameConfig(
    val cols: Int = 7,
    val rows: Int = 6,
    val winLength: Int = 4
)

@Serializable
enum class Player { FIRST, SECOND }

class CellState(val col: Int, val row: Int) {
    var player by mutableStateOf<Player?>(null)
    var isWinningCell by mutableStateOf(false)
    var isDimmed by mutableStateOf(false)

    var justDropped by mutableStateOf(false)
}

class GameState(val config: GameConfig) {
    val board: List<List<CellState>> = List(config.cols) { col ->
        List(config.rows) { row -> CellState(col, row) }
    }

    // Encapsulate state modification
    var currentPlayer by mutableStateOf(Player.FIRST)
        private set

    var winner by mutableStateOf<Player?>(null)
        private set

    var isDraw by mutableStateOf(false)
        private set

    var movesCount by mutableStateOf(0)
        private set

    private val maxMoves = config.cols * config.rows

    fun dropPiece(col: Int) {
        if (winner != null || isDraw) return

        val column = board[col]
        val row = column.indexOfFirst { it.player == null }

        if (row == -1) return // Column is full
        for (colList in board) {
            for (cell in colList) {
                cell.justDropped = false
            }
        }

        column[row].player = currentPlayer
        column[row].justDropped = true

        movesCount++

        val winCells = checkWinner(board, col, row, currentPlayer, config)

        if (winCells != null) {
            winner = currentPlayer

            // Replaced .flatten().forEach with optimal nested loops
            for (colList in board) {
                for (cell in colList) {
                    if (winCells.contains(cell.col to cell.row)) {
                        cell.isWinningCell = true
                    } else if (cell.player != null) {
                        cell.isDimmed = true
                    }
                }
            }
        } else if (movesCount == maxMoves) {
            // O(1) Draw check: If moves are exhausted and no winner, it's a draw
            isDraw = true
        } else {
            currentPlayer = if (currentPlayer == Player.FIRST) Player.SECOND else Player.FIRST
        }
    }

    fun reset() {
        for (col in board) {
            for (cell in col) {
                cell.player = null
                cell.isWinningCell = false
                cell.isDimmed = false
                cell.justDropped = false
            }
        }
        currentPlayer = Player.FIRST
        winner = null
        isDraw = false
        movesCount = 0
    }

    fun toDTO(): GameStateDTO {
        // Collect winning cells efficiently without storing them permanently in GameState
        val winningCoordinates = mutableListOf<Pair<Int, Int>>()
        if (winner != null) {
            for (col in board) {
                for (cell in col) {
                    if (cell.isWinningCell) {
                        winningCoordinates.add(cell.col to cell.row)
                    }
                }
            }
        }

        return GameStateDTO(
            config = config,
            board = board.map { col -> col.map { cell -> cell.player } },
            currentPlayer = currentPlayer,
            winner = winner,
            isDraw = isDraw,
            movesCount = movesCount,
            winCells = winningCoordinates // Now properly assigned!
        )
    }

    companion object {
        fun fromDTO(dto: GameStateDTO): GameState {
            val state = GameState(dto.config)
            state.currentPlayer = dto.currentPlayer
            state.winner = dto.winner
            state.isDraw = dto.isDraw
            state.movesCount = dto.movesCount

            // Restore board state
            for (c in 0 until dto.config.cols) {
                for (r in 0 until dto.config.rows) {
                    state.board[c][r].player = dto.board[c][r]
                }
            }

            // Restore winning highlights if the game was already won
            if (dto.winner != null) {
                // Replaced .flatten().forEach with optimal nested loops
                for (colList in state.board) {
                    for (cell in colList) {
                        if (dto.winCells.contains(cell.col to cell.row)) {
                            cell.isWinningCell = true
                        } else if (cell.player != null) {
                            cell.isDimmed = true
                        }
                    }
                }
            }
            return state
        }
    }
}

private val DIRECTIONS = intArrayOf(
    1, 0,  // horizontal
    0, 1,  // vertical
    1, 1,  // diagonal ↘
    1, -1  // diagonal ↗
)

fun checkWinner(
    board: List<List<CellState>>,
    lastCol: Int, lastRow: Int,
    player: Player,
    config: GameConfig
): List<Pair<Int, Int>>? {
    val cols = config.cols
    val rows = config.rows
    val winLen = config.winLength

    var i = 0
    while (i < 8) {
        val dc = DIRECTIONS[i]
        val dr = DIRECTIONS[i + 1]
        var count = 1

        // Forward
        var c1 = lastCol + dc
        var r1 = lastRow + dr
        while (c1 in 0..<cols && r1 >= 0 && r1 < rows && board[c1][r1].player == player) {
            count++
            c1 += dc
            r1 += dr
        }

        // Backward
        var c2 = lastCol - dc
        var r2 = lastRow - dr
        while (c2 in 0..<cols && r2 >= 0 && r2 < rows && board[c2][r2].player == player) {
            count++
            c2 -= dc
            r2 -= dr
        }

        if (count >= winLen) {
            val cells = ArrayList<Pair<Int, Int>>(count)
            var c = c2 + dc
            var r = r2 + dr
            for (step in 0 until count) {
                cells.add(c to r)
                c += dc
                r += dr
            }
            return cells
        }
        i += 2
    }
    return null
}