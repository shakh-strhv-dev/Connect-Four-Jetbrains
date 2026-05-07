package org.connect4jet.project

import org.connect4jet.project.game.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class GameLogicTest {

    private val defaultConfig = GameConfig(cols = 7, rows = 6, winLength = 4)

    /**
     * Helper function to count the number of cells marked as part of the winning line.
     */
    private fun getWinningCellsCount(state: GameState): Int {
        return state.board.flatten().count { it.isWinningCell }
    }

    @Test
    fun testHorizontalWin() {
        val state = GameState(defaultConfig)

        state.dropPiece(0) // Red
        state.dropPiece(0) // Yellow
        state.dropPiece(1) // Red
        state.dropPiece(1) // Yellow
        state.dropPiece(2) // Red
        state.dropPiece(2) // Yellow

        assertNull(state.winner, "The winner should be null before the winning move")

        state.dropPiece(3) // Red wins

        assertNotNull(state.winner, "The winner should be determined")
        assertEquals(Player.FIRST, state.winner, "FIRST (Red) should be the winner")
        assertEquals(4, getWinningCellsCount(state), "There should be exactly 4 winning cells")
    }

    @Test
    fun testVerticalWin() {
        val state = GameState(defaultConfig)

        state.dropPiece(0) // R
        state.dropPiece(1) // Y
        state.dropPiece(0) // R
        state.dropPiece(1) // Y
        state.dropPiece(0) // R
        state.dropPiece(1) // Y

        state.dropPiece(0) // R wins vertically

        assertEquals(Player.FIRST, state.winner, "FIRST (Red) should win vertically")
    }

    @Test
    fun testDiagonalWin() {
        val state = GameState(defaultConfig)

        state.dropPiece(0) // R
        state.dropPiece(1); state.dropPiece(1) // Y, R
        state.dropPiece(2); state.dropPiece(2); state.dropPiece(0); state.dropPiece(2) // Y, R, Y, R
        state.dropPiece(3); state.dropPiece(3); state.dropPiece(3); state.dropPiece(3) // Y, R, Y, R wins diagonally

        assertEquals(Player.FIRST, state.winner, "FIRST (Red) should win diagonally")
        assertEquals(4, getWinningCellsCount(state), "There should be exactly 4 winning cells")
    }

    @Test
    fun testFullColumnDoesNotCrash() {
        val state = GameState(defaultConfig)

        // Fill the first column (6 rows)
        for (i in 0 until 6) {
            state.dropPiece(0)
        }

        val playerBeforeFullDrop = state.currentPlayer
        val movesBefore = state.movesCount

        // Attempt to drop into a full column
        state.dropPiece(0)

        assertEquals(playerBeforeFullDrop, state.currentPlayer, "Turn should not pass on invalid move")
        assertEquals(movesBefore, state.movesCount, "Move count should not increase on invalid move")
        assertNull(state.winner, "There should be no winner")
    }

    @Test
    fun testWinByFillingGap() {
        val state = GameState(defaultConfig)

        state.dropPiece(0); state.dropPiece(0) // R, Y
        state.dropPiece(1); state.dropPiece(1) // R, Y
        state.dropPiece(3); state.dropPiece(3) // R, Y
        state.dropPiece(4); state.dropPiece(4) // R, Y

        state.dropPiece(2) // R fills the gap!

        assertEquals(Player.FIRST, state.winner, "RED should win by connecting the line through the gap")
    }

    @Test
    fun testOvershootWinReturnsAllCells() {
        val state = GameState(defaultConfig)

        // Setup situation: R, R, _, R, R
        state.dropPiece(0); state.dropPiece(0) // R, Y
        state.dropPiece(1); state.dropPiece(1) // R, Y
        state.dropPiece(3); state.dropPiece(3) // R, Y
        state.dropPiece(4); state.dropPiece(4) // R, Y

        // Drop in the center (2), resulting in 5 in a row
        state.dropPiece(2)

        assertEquals(Player.FIRST, state.winner, "The winner should be determined")
        assertEquals(5, getWinningCellsCount(state), "The algorithm should highlight all 5 cells, not just 4")
    }

    @Test
    fun testDrawConditionO1() {
        // Check draw condition on a tiny board
        val tinyConfig = GameConfig(cols = 2, rows = 2, winLength = 3)
        val state = GameState(tinyConfig)

        state.dropPiece(0) // R
        state.dropPiece(1) // Y
        state.dropPiece(1) // R
        state.dropPiece(0) // Y

        assertNull(state.winner, "There should be no winner")
        assertTrue(state.isDraw, "The game should register a draw state (isDraw = true)")
        assertEquals(4, state.movesCount, "Moves count should be equal to total board size")
    }

    @Test
    fun testCustomConfigConnectFive() {
        val customConfig = GameConfig(cols = 10, rows = 10, winLength = 5)
        val state = GameState(customConfig)

        state.dropPiece(0); state.dropPiece(0)
        state.dropPiece(1); state.dropPiece(1)
        state.dropPiece(2); state.dropPiece(2)
        state.dropPiece(3)

        assertNull(state.winner, "4 pieces should not be enough to win when winLength=5")

        state.dropPiece(5) // Y
        state.dropPiece(4) // R wins (5 in a row)

        assertEquals(Player.FIRST, state.winner, "RED should win only after the 5th piece")
        assertEquals(5, getWinningCellsCount(state))
    }

    // --- Advanced Tests ---

    @Test
    fun testNoMovesAllowedAfterWin() {
        val state = GameState(defaultConfig)

        // RED wins horizontally
        state.dropPiece(0); state.dropPiece(0)
        state.dropPiece(1); state.dropPiece(1)
        state.dropPiece(2); state.dropPiece(2)
        state.dropPiece(3)

        val movesAfterWin = state.movesCount

        // Attempt to make a move after game is won
        state.dropPiece(4)

        assertEquals(Player.FIRST, state.winner, "Winner should still be RED")
        assertEquals(movesAfterWin, state.movesCount, "Moves count should not increase after game over")
        assertNull(state.board[4][0].player, "Piece should not be placed after game is won")
    }

    @Test
    fun testResetRestoresInitialState() {
        val state = GameState(defaultConfig)

        // Make several moves
        state.dropPiece(0)
        state.dropPiece(1)
        state.dropPiece(2)

        // Reset the game
        state.reset()

        assertEquals(0, state.movesCount, "Moves count should be 0 after reset")
        assertNull(state.winner, "Winner should be null after reset")
        assertFalse(state.isDraw, "isDraw should be false after reset")
        assertEquals(Player.FIRST, state.currentPlayer, "First player should start after reset")

        // Ensure all cells are fully cleared (no pieces, no highlights)
        val allCellsEmpty = state.board.flatten().all {
            it.player == null && !it.isWinningCell && !it.isDimmed
        }
        assertTrue(allCellsEmpty, "All cells must be fully cleared after reset")
    }

    @Test
    fun testDimmedCellsOnWin() {
        val state = GameState(defaultConfig)

        // Make 7 moves: 4 winning for RED, 3 distracting for YELLOW
        state.dropPiece(0) // R (win part)
        state.dropPiece(0) // Y (dimmed later)
        state.dropPiece(1) // R (win part)
        state.dropPiece(1) // Y (dimmed later)
        state.dropPiece(2) // R (win part)
        state.dropPiece(2) // Y (dimmed later)
        state.dropPiece(3) // R (wins!)

        val winningCells = state.board.flatten().count { it.isWinningCell }
        val dimmedCells = state.board.flatten().count { it.isDimmed }
        val emptyCells = state.board.flatten().count { it.player == null }

        assertEquals(4, winningCells, "Exactly 4 cells should pulse as winning")
        assertEquals(3, dimmedCells, "Exactly 3 non-winning pieces should be dimmed")
        // Total cells: 42. Occupied: 7. Empty: 35. Empty cells should not be dimmed or winning.
        assertEquals(42 - 7, emptyCells, "Remaining empty cells should be left untouched")
    }
}