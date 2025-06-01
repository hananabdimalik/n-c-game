package com.example.nought_and_crosses_game.repository

import com.example.nought_and_crosses_game.model.GameCell
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.apache.commons.io.IOUtils
import java.net.URL
import java.nio.charset.Charset

class GameRepository {
    suspend fun getBoardState(path: String) = makeRequest(path).await()

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

    fun updateBoard(position: Int) = makeRequest("updateBoard/$position")
}
