package com.example.nought_and_crosses_game.ui.gameBoard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nought_and_crosses_game.R
import com.example.nought_and_crosses_game.model.GameCell
import com.example.nought_and_crosses_game.model.GamePieces
import com.example.nought_and_crosses_game.model.GameState
import com.example.nought_and_crosses_game.model.Player
import com.example.nought_and_crosses_game.ui.theme.NoughtandcrossesgameTheme

@Composable
fun GameView(
    viewModel: GameViewModel = viewModel(),
    playerNames: List<String>
) {

    val state = viewModel.state.collectAsStateWithLifecycle()

    Column(verticalArrangement = Arrangement.spacedBy(30.dp)) {
        PlayerNamesView(playerNames)

        GameGrid(
            viewModel,
            hasGameBegan = state.value.gameSession.hasGameBegan,
            gameState = state.value.gameState,
            gameCells = state.value.gameCells
        )

        GameOutcomeTextView(
            hasGameBegan = state.value.gameSession.hasGameBegan,
            players = state.value.gameSession.players,
            gameState = state.value.gameState,
            currentPlayer = state.value.gameSession.currentPlayer
        )
    }
}

@Composable
private fun GameGrid(
    viewModel: GameViewModel = viewModel(),
    hasGameBegan: Boolean,
    gameState: GameState,
    gameCells: List<GameCell>

) {
    val gridSize = 9

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Box(
            modifier = Modifier
                .padding(start = 40.dp, end = 60.dp)
                .size(height = 300.dp, width = 600.dp)
                .background(if (!hasGameBegan && gameState != GameState.None) Color.LightGray else Color.Unspecified)
        ) {
            VerticalDivider(
                modifier = Modifier
                    .size(300.dp)
                    .padding(start = 90.dp)
            )
            VerticalDivider(
                modifier = Modifier
                    .size(300.dp)
                    .padding(start = 200.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(top = 90.dp))
            HorizontalDivider(modifier = Modifier.padding(top = 200.dp))

            var x = 0
            var y = 0
            for (i in 0 until gridSize) {
                GridCell(
                    x = x,
                    y = y,
                    gamePiece = gameCells[i].piece,
                    onCellTapped = { viewModel.updateGrid(i) },
                    hasGameBegan = !hasGameBegan && gameState != GameState.None
                )
                val mod = i % 3
                if (mod == 2) {
                    x = 0
                    y += 100
                } else {
                    x += 100
                }
            }
        }

        Button(
            onClick = viewModel::onResetTapped,
            modifier = Modifier
                .padding(start = 130.dp)
                .padding(top = 20.dp),
            enabled = !hasGameBegan
        ) {
            Text("Reset Game")
        }
    }
}

@Composable
private fun GridCell(
    x: Int,
    y: Int,
    gamePiece: GamePieces,
    onCellTapped: () -> Unit,
    hasGameBegan: Boolean
) {
    val density = LocalDensity.current
    val xInDp = with(density) { x.dp }
    val yInDp = with(density) { y.dp }
    val image = when (gamePiece) {
        GamePieces.Nought -> R.drawable.ic_nought
        GamePieces.Cross -> R.drawable.ic_cross
        else -> null
    }
    Box(
        modifier = Modifier
            .size(90.dp)
            .offset(x = xInDp, y = yInDp)
            .clickable(enabled = !hasGameBegan) {
                onCellTapped()
            }
    ) {
        image?.let {
            Image(
                painter = painterResource(it),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun GameOutcomeTextView(
    hasGameBegan: Boolean,
    players: List<Player>,
    gameState: GameState,
    currentPlayer: Player?
) {
    if (!hasGameBegan && players.isNotEmpty() && gameState != GameState.None) {
        if (gameState == GameState.Win) {
            Text(
                text = "${currentPlayer?.name} is the winner",
                modifier = Modifier.padding(start = 120.dp)
            )
        } else {
            Text(
                text = "$gameState - Reset to play again",
                modifier = Modifier.padding(start = 150.dp)
            )
        }
    }
}

@Composable
private fun PlayerNamesView(playerNames: List<String>) {
    if (playerNames.isNotEmpty()) {
        Row(modifier = Modifier.padding(start = 140.dp)) {
            Text(
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(top = 20.dp),
                text = playerNames.first()
            )

            Text(
                style = TextStyle(fontSize = 14.sp),
                text = " VS ",
                modifier = Modifier
                    .padding(top = 20.dp),
            )
            Text(
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(top = 20.dp),

                text = playerNames.last()
            )
        }
    }
}

@Preview
@Composable
fun GameGridPreview() {
    NoughtandcrossesgameTheme {
        GameGrid(
            hasGameBegan = true,
            gameState = GameState.Win,
            gameCells = List(9) { GameCell(GamePieces.Cross, it) })
    }
}

@Preview
@Composable
fun PreviewGridCell() {
    NoughtandcrossesgameTheme {
        GridCell(0, 0, GamePieces.Nought, {}, false)
    }
}

@Preview
@Composable
fun PlayerNamesPreview() {
    NoughtandcrossesgameTheme {
        PlayerNamesView(listOf())
    }
}

@Preview
@Composable
fun GameOutcomePreview() {
    NoughtandcrossesgameTheme {
        GameOutcomeTextView(
            hasGameBegan = false,
            players = listOf(
                Player("Mark", "id", GamePieces.Nought), Player(
                    "Lily", "id",
                    GamePieces.Cross
                )
            ),
            gameState = GameState.Win,
            currentPlayer = Player(name = "Mark", id = "", gamePiece = GamePieces.Nought)
        )
    }
}

@Preview
@Composable
fun NoughtAndCrossGamePreview(){
    GameView(playerNames = listOf("Mark, Lily"))
}
