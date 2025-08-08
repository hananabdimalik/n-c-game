package com.example.nought_and_crosses_game.repository

import com.example.nought_and_crosses_game.model.GameCell
import com.example.nought_and_crosses_game.model.GameSession
import com.example.nought_and_crosses_game.model.JoinGameData
import com.example.nought_and_crosses_game.model.Player
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.apache.commons.io.IOUtils
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class GameRepository {
    val gson = Gson()
    suspend fun hostGameSession(player: Player): GameSession =
        makeGameSessionRequest("hostSession", player = player).await()

    suspend fun getBoardState(): List<GameCell> = makeRequest("gameBoard").await()

    suspend fun updateBoard(player: Player, position: Int): List<GameCell> =
        updateBoardPostRequest(player, "updateBoard/$position").await()

    suspend fun resetGameBoard(): List<GameCell> = makeRequest("resetGame").await()

    suspend fun joinGameSession(player: Player): JoinGameData = joinGameSession(player, "joinSession")
        .await()

    suspend fun getGameSession(): GameSession =
        makeGameSessionRequest("gameSession").await()

    suspend fun restartGame(): GameSession = makeGameSessionRequest("restartGame").await()

    private fun makeGameSessionRequest(path: String, player: Player? = null) =
        CoroutineScope(Dispatchers.Main).async {
            val deferred = async {

                val url = URL("http://10.0.2.2:8080/$path")
                val connection = url.openConnection() as HttpURLConnection

                withContext(Dispatchers.IO) {
                    try {
                        connection.requestMethod = "POST"
                        connection.setRequestProperty("Content-Type", "application/json")
                        connection.doOutput = true

                        val jsonBody = gson.toJson(player)
                        connection.outputStream.use {
                            it.write(jsonBody.toByteArray(Charset.forName("UTF-8")))
                        }

                        val inputStream = connection.getInputStream()
                        inputStream.bufferedReader(Charset.forName("UTF-8")).use { it.readText() }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        throw e
                    }
                }
            }
            val response = deferred.await()
            Gson().fromJson<GameSession>(
                response,
                object : TypeToken<GameSession>() {}.type
            )
        }

    private fun updateBoardPostRequest(player: Player, path: String) =
        CoroutineScope(Dispatchers.Main).async {
            val gson = Gson()
            val deferred = async {
                val url = URL("http://10.0.2.2:8080/$path")
                val connection = url.openConnection() as HttpURLConnection

                withContext(Dispatchers.IO) {
                    try {
                        connection.requestMethod = "POST"
                        connection.setRequestProperty("Content-Type", "application/json")
                        connection.doOutput = true

                        val jsonBody = gson.toJson(player)
                        connection.outputStream.use {
                            it.write(
                                jsonBody.toByteArray(
                                    Charset.forName(
                                        "UTF-8"
                                    )
                                )
                            )
                        }
                        connection.inputStream.bufferedReader().use { it.readText() }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        throw e
                    }
                }
            }

            val response = deferred.await()
            gson.fromJson<List<GameCell>>(response, object : TypeToken<List<GameCell>>() {}.type)
        }

    private fun makeRequest(path: String) = CoroutineScope(Dispatchers.Main).async {
        val deferred = async {
            val url = URL("http://10.0.2.2:8080/$path")

            withContext(Dispatchers.IO) {
                try {
                    val urlConnection = url.openConnection()
                    IOUtils.toString(urlConnection.getInputStream(), Charset.forName("UTF-8"))
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw e
                }
            }
        }
        val responseJson = deferred.await()

        Gson().fromJson<List<GameCell>>(responseJson, object : TypeToken<List<GameCell>>() {}.type)
    }

    private fun joinGameSession(player: Player, path: String) =
        CoroutineScope(Dispatchers.Main).async {
            val gson = Gson()
            val url = URL("http://10.0.2.2:8080/$path")
            val connection = url.openConnection() as HttpURLConnection

            val response = withContext(Dispatchers.IO) {
                try {
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.doOutput = true


                    val jsonBody = gson.toJson(player)
                    connection.outputStream.use { output ->
                        output.write(
                            jsonBody.toString()
                                .toByteArray(
                                    Charset.forName(
                                        "UTF-8"
                                    )
                                )
                        )
                    }

                    connection.inputStream.bufferedReader().use { it.readText() }
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw e
                }
            }

            gson.fromJson<JoinGameData>(response, object : TypeToken<JoinGameData>() {}.type)
        }
}
