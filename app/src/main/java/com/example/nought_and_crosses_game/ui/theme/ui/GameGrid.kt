package com.example.nought_and_crosses_game.ui.theme.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nought_and_crosses_game.R
import com.example.nought_and_crosses_game.model.GameCell
import com.example.nought_and_crosses_game.model.GamePieces
import com.example.nought_and_crosses_game.model.GameState
import com.example.nought_and_crosses_game.ui.theme.NoughtandcrossesgameTheme

@Composable
fun GameGrid(
    board: List<GameCell>,
    onCellTapped: (Int) -> Unit,
    hasGameEnded: Boolean,
    onResetTapped: () -> Unit,
    gameState: GameState,
) {
    val gridSize = 9

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Box(
            modifier = Modifier
                .padding(top = 100.dp, start = 40.dp, end = 60.dp)
                .size(height = 300.dp, width = 600.dp)
                .background(if (hasGameEnded) Color.LightGray else Color.Unspecified)
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
                    gamePiece = board[i].piece,
                    onCellTapped = { onCellTapped(i) },
                    hasGameEnded = hasGameEnded
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

        if (hasGameEnded) {
            Text("Game over: $gameState", modifier = Modifier.padding(start = 150.dp))
        }

        Button(onResetTapped, modifier = Modifier.padding(start = 130.dp), enabled = hasGameEnded) {
            Text("Reset Game")
        }
    }
}

@Composable
fun GridCell(
    x: Int,
    y: Int,
    gamePiece: GamePieces,
    onCellTapped: () -> Unit,
    hasGameEnded: Boolean
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
            .clickable(enabled = !hasGameEnded) {
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

@Preview
@Composable
fun GameGridPreview() {
    NoughtandcrossesgameTheme {
        GameGrid(List(9) { GameCell(GamePieces.Cross, it) }, {}, true, {}, GameState.None)
    }
}

@Preview
@Composable
fun PreviewGridCell() {
    NoughtandcrossesgameTheme {
        GridCell(0, 0, GamePieces.Nought, {}, false)
    }
}
