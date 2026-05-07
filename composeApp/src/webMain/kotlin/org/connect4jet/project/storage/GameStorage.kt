package org.connect4jet.project.storage

import kotlinx.browser.localStorage
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.connect4jet.project.game.GameConfig
import org.connect4jet.project.game.GameState
import org.connect4jet.project.game.Player

object GameStorage {
    private const val KEY = "connect4_state"

    fun save(state: GameState) {
        localStorage.setItem(KEY, Json.encodeToString(state.toDTO()))
    }

    fun load(): GameState? = try {
        val jsonString = localStorage.getItem(KEY)
        if (jsonString != null) {
            val dto = Json.Default.decodeFromString<GameStateDTO>(jsonString)
            GameState.fromDTO(dto)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }

    fun clear() {
        localStorage.removeItem(KEY)
    }
}

@Serializable
data class GameStateDTO(
    val config: GameConfig,
    val board: List<List<Player?>>,
    val currentPlayer: Player,
    val winner: Player?,
    val isDraw: Boolean,
    val movesCount: Int,
    val winCells: List<Pair<Int, Int>> = emptyList()
)