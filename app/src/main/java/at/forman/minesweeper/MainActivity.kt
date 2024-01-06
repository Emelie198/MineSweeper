package at.forman.minesweeper

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import at.forman.minesweeper.model.FieldState
import at.forman.minesweeper.ui.GameViewModel
import at.forman.minesweeper.ui.GameViewModelFactory
import at.forman.minesweeper.ui.theme.GameTheme

private const val TAG = "Game"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContent {
            GameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameApp()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    @Composable
    fun GameApp(modifier: Modifier = Modifier) {
        val initialCols = 5
        val initialRows = 7
        val initialBombs = 6
        val vm: GameViewModel = viewModel(
            factory = GameViewModelFactory(initialCols, initialRows, initialBombs)
        )
        val gameUiState by vm.uiState.collectAsState()
        Text(
            text = "MineSweeper",
            fontSize = 40.sp,
            fontWeight = FontWeight(900),
            modifier = Modifier
                .wrapContentWidth(align = Alignment.CenterHorizontally)
                .offset(y = 30.dp)
        )
        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(gameUiState.colNum),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(25.dp)
                    .offset(y = 90.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                items(gameUiState.board.flatten().indices.toList()) { index ->
                    val fieldState = gameUiState.board.flatten()[index]
                    MinesweeperField(
                        state = fieldState,
                        onClick = {
                            vm.clicked(
                                index / gameUiState.colNum,
                                index % gameUiState.colNum
                            )
                        },
                        onLongClick = {
                            vm.longClicked(
                                index / gameUiState.colNum,
                                index % gameUiState.colNum
                            )
                        }
                    )
                }
            }
        }
        Button(
            onClick = { vm.resetGame() },
            modifier = Modifier
                .padding(70.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .wrapContentWidth(align = Alignment.CenterHorizontally)
                .size(180.dp, 80.dp)
                .offset(y = 230.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Reset Game",
                fontSize = 20.sp,
                modifier = Modifier
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
            )
        }
    }

    private fun getNumberDrawable(number: Int): Int {
        return when (number) {
            1 -> return R.drawable.icon1
            2 -> return R.drawable.icon2
            3 -> return R.drawable.icon3
            4 -> return R.drawable.icon4
            5 -> return R.drawable.icon5
            6 -> return R.drawable.icon6
            7 -> return R.drawable.icon7
            8 -> return R.drawable.icon8
            else -> {
                Log.e(TAG, "Invalid number: $number")
                throw IllegalArgumentException("Invalid number")
            }
        }
    }

    private fun getResourceIdForState(state: FieldState, number: Int): Int {
        Log.d(TAG, "State: $state, Number: $number")
        return when (state) {
            FieldState.HIDDEN -> R.drawable.hidden
            FieldState.FLAGGED -> R.drawable.flag
            FieldState.BOMB -> R.drawable.bomb
            FieldState.ZERO -> R.drawable.empty
            FieldState.VISIBLE -> getNumberDrawable(number)
            else -> {
                Log.e(TAG, "Invalid FieldState: $state")
                throw IllegalArgumentException("Invalid FieldState")
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun MinesweeperField(state: FieldState, onClick: () -> Unit, onLongClick: () -> Unit) {
        Log.d("minesweepie", state.toString())
        val number: Int = if (state.number in 1..8) state.number else 1
        Log.d("minesweepie2", number.toString())
        Image(
            painter = painterResource(id = getResourceIdForState(state, number)),
            contentDescription = null,
            modifier = Modifier
                .combinedClickable(
                    onClick = { onClick() },
                    onLongClick = { onLongClick() }
                )
                .padding(7.dp)
                .size(50.dp)
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GamePreview() {
        GameTheme {
            GameApp()
        }
    }
}





