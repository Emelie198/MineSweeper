package at.forman.minesweeper.ui

import androidx.lifecycle.ViewModel
import at.forman.minesweeper.model.FieldState
import at.forman.minesweeper.model.Game
import at.forman.minesweeper.model.Game.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel(
    initialCols: Int,
    initialRows: Int,
    initialBombs: Int
) : ViewModel() {
    private val model: Game = Game(initialCols, initialRows, initialBombs)
    private val _uiState = MutableStateFlow(
        GameUiState(
            initialCols,
            initialRows,
            "Welcome!",
            convertFrontStateArray(model.frontState),
            model.frontState
        )
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    @Suppress("unused")
    @JvmOverloads
    constructor() : this(0, 0, 0)

    init {
        resetGame()
    }

    fun resetGame() {
        _uiState.value = GameUiState(frontState = model.frontState)
        model.reset()
        updateUiState()
    }

    private fun convertBackStateToFrontState(backState: Game.BackState): FieldState {
        return backState.toFieldState()
    }

    private fun convertFrontStateToFieldState(frontState: Game.FrontState): FieldState {
        return when (frontState) {
            Game.FrontState.HIDDEN -> FieldState.HIDDEN
            Game.FrontState.VISIBLE -> FieldState.VISIBLE
            Game.FrontState.FLAGGED -> FieldState.FLAGGED
        }
    }

    private fun convertFrontStateArray(frontStateArray: Array<Array<Game.FrontState>>): Array<Array<FieldState>> {
        return frontStateArray.map { row ->
            row.map { convertFrontStateToFieldState(it) }.toTypedArray()
        }.toTypedArray()
    }

    fun longClicked(row: Int, col: Int) {
        if (isValidIndex(row, col)) {
            when (model.flag(col, row)) {
                Result.WIN -> _uiState.value = GameUiState(
                    model.cols,
                    model.rows,
                    "You Win!",
                    convertFrontStateArray(model.frontState),
                    model.frontState
                )
                Result.VALID -> updateUiState()
                else -> {}
            }
        }
    }

    fun clicked(row: Int, col: Int) {
        if (isValidIndex(row, col)) {
            when (model.uncover(col, row)) {
                Result.WIN -> _uiState.value = GameUiState(
                    model.cols,
                    model.rows,
                    "You Win!",
                    convertFrontStateArray(model.frontState),
                    model.frontState
                )
                Result.HIT -> _uiState.value = GameUiState(
                    model.cols,
                    model.rows,
                    "Game Over!",
                    convertFrontStateArray(model.frontState),
                    model.frontState
                )
                Result.VALID -> updateUiState()
                else -> {}
            }
        }
    }

    private fun isValidIndex(row: Int, col: Int): Boolean {
        return row in 0 until model.rows && col in 0 until model.cols
    }

    private fun updateUiState() {
        _uiState.value = GameUiState(
            model.cols,
            model.rows,
            "Keep Going!",
            convertFrontStateArray(model.frontState),
            model.frontState
        )
    }
}
