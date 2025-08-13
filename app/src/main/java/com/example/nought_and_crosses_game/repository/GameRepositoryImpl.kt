package com.example.nought_and_crosses_game.repository

import com.example.nought_and_crosses_game.model.GameCell
import com.example.nought_and_crosses_game.model.GameSession
import com.example.nought_and_crosses_game.model.Player
import com.example.nought_and_crosses_game.model.RestartGame
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

class GameRepositoryImpl : GameRepository {
    val gson = Gson()
    override suspend fun hostGameSession(
        path: String,
        player: Player
    ): GameSession {
        val output = CoroutineScope(Dispatchers.Main).async {
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
        return output.await()
    }

    override suspend fun joinGameSession(
        path: String,
        player: Player
    ): GameSession {
        val output = CoroutineScope(Dispatchers.Main).async {
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

            gson.fromJson<GameSession>(response, object : TypeToken<GameSession>() {}.type)
        }
        return output.await()
    }

    override suspend fun loadGameState(path: String): GameSession {
        val output = CoroutineScope(Dispatchers.Main).async {
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

            Gson().fromJson<GameSession>(
                responseJson,
                object : TypeToken<GameSession>() {}.type
            )
        }
        return output.await()
    }

    override suspend fun getGameBoard(path: String): List<GameCell> {
        val output = CoroutineScope(Dispatchers.Main).async {
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

            Gson().fromJson<List<GameCell>>(
                responseJson,
                object : TypeToken<List<GameCell>>() {}.type
            )
        }
        return output.await()
    }

    override suspend fun updateBoard(
        path: String,
        player: Player,
        position: Int,
        sessionId: String
    ): List<GameCell> {
        val output = CoroutineScope(Dispatchers.Main).async {
            val gson = Gson()
            val deferred = async {
                val url = URL("http://10.0.2.2:8080/$path/$position/$sessionId")
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
        return output.await()
    }

    override suspend fun getGameState(path: String): GameSession {
        val output = CoroutineScope(Dispatchers.Main).async {
            val deferred = async {
                val url = URL("http://10.0.2.2:8080/$path")

                withContext(Dispatchers.IO) {
                    try {
                        val urlConnection = url.openConnection()
                        IOUtils.toString(urlConnection.inputStream, Charset.forName("UTF-8"))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        throw e
                    }
                }
            }
            val responseJson = deferred.await()
            Gson().fromJson<GameSession>(responseJson, object : TypeToken<GameSession>() {}.type)
        }
        return output.await()
    }

    override suspend fun resetGameBoard(path: String): List<GameCell> { // requires gameSessionId
        val output = CoroutineScope(Dispatchers.Main).async {
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

            Gson().fromJson<List<GameCell>>(
                responseJson,
                object : TypeToken<List<GameCell>>() {}.type
            )
        }
        return output.await()
    }

    override suspend fun restartGameSession(path: String): RestartGame { // requires gameSessionId
        val output = CoroutineScope(Dispatchers.Main).async {
            val deferred = async {

                val url = URL("http://10.0.2.2:8080/$path")
                val connection = url.openConnection() as HttpURLConnection

                withContext(Dispatchers.IO) {
                    try {
//                        connection.requestMethod = "POST"
//                        connection.setRequestProperty("Content-Type", "application/json")
//                        connection.doOutput = true

//                        val jsonBody = gson.toJson(player)
//                        connection.outputStream.use {
//                            it.write(jsonBody.toByteArray(Charset.forName("UTF-8")))
//                        }

                        val inputStream = connection.getInputStream()
                        inputStream.bufferedReader(Charset.forName("UTF-8")).use { it.readText() }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        throw e
                    }
                }
            }
            val response = deferred.await()
            Gson().fromJson<RestartGame>(
                response,
                object : TypeToken<RestartGame>() {}.type
            )
        }
        return output.await()
    }
}
