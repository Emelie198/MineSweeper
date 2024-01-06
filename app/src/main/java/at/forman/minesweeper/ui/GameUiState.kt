package at.forman.minesweeper.ui

import at.forman.minesweeper.model.FieldState
import at.forman.minesweeper.model.Game

data class GameUiState(
    val rowNum: Int = 7,
    val colNum: Int = 5,
    val display: String = "xxx",
    val board: Array<Array<FieldState>> = Array(rowNum) { Array(colNum) { FieldState.HIDDEN } },
    val frontState: Array<Array<Game.FrontState>>
)
    {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameUiState

        if (rowNum != other.rowNum) return false
        if (colNum != other.colNum) return false
        if (display != other.display) return false
        return board.contentDeepEquals(other.board)
    }

    override fun hashCode(): Int {
        var result = rowNum
        result = 31 * result + colNum
        result = 31 * result + display.hashCode()
        result = 31 * result + board.contentDeepHashCode()
        return result
    }
}
