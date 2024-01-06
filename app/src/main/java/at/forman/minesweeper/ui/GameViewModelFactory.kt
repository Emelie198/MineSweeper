package at.forman.minesweeper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameViewModelFactory(
    private val initialCols: Int,
    private val initialRows: Int,
    private val initialBombs: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(initialCols, initialRows, initialBombs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}