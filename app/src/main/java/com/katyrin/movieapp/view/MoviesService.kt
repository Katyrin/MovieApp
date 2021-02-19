package com.katyrin.movieapp.view

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.app.JobIntentService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.katyrin.movieapp.model.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MoviesService: JobIntentService() {
    private val broadcastIntent = Intent(DETAILS_INTENT_FILTER_RESULT)

    override fun onHandleWork(intent: Intent) {
        loadGenres(intent.getIntExtra(DETAILS_GENRE_ID_EXTRA, 0))
    }

    private fun loadGenres(id: Int) {
        try {
            val handler = Handler(Looper.getMainLooper())
            val uri = URL(
                "https://api.themoviedb.org/3/discover/movie?api_key=" +
                        "$MOVIE_API_KEY&language=ru&sort_by=popularity.desc&with_genres=$id"
            )
            lateinit var urlConnection: HttpsURLConnection

            Thread {
                try {
                    urlConnection = uri.openConnection() as HttpsURLConnection
                    urlConnection.apply {
                        requestMethod = REQUEST_GET
                        readTimeout = REQUEST_TIMEOUT
                    }
                    val input = BufferedReader(InputStreamReader(urlConnection.inputStream))
                    val result = getLines(input)
                    val moviesDTO = Gson().fromJson(result, MoviesDTO::class.java)
                    handler.post {
                        onResponse(moviesDTO)
                    }
                } catch (e: Exception) {
                    onErrorRequest(e.message ?: "")
                } finally {
                    urlConnection.disconnect()
                }
            }.start()
        } catch (e: MalformedURLException) {
            onMalformedURL()
        }
    }

    private fun getLines(reader: BufferedReader): String {
        val rawData = StringBuilder(1024)

        while (true) {
            try {
                reader.readLine()?.let {
                    rawData.append(it).append("\n")
                } ?: break
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        try {
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return rawData.toString()
    }

    private fun onResponse(moviesDTO: MoviesDTO) {
        val results = moviesDTO.results
        onSuccessResponse(results)
    }

    private fun onSuccessResponse(results: Array<ResultsDTO?>) {
        putLoadResult(DETAILS_RESULT_RESPONSE_SUCCESS_EXTRA)
        broadcastIntent.putExtra(DETAILS_RESULTS_LIST_EXTRA, results)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    private fun onMalformedURL() {
        putLoadResult(DETAILS_URL_MALFORMED_EXTRA)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    private fun onErrorRequest(error: String) {
        putLoadResult(DETAILS_REQUEST_ERROR_EXTRA)
        broadcastIntent.putExtra(DETAILS_REQUEST_ERROR_MESSAGE_EXTRA, error)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    private fun putLoadResult(result: String) {
        broadcastIntent.putExtra(DETAILS_LOAD_RESULT_MOVIES_EXTRA, result)
    }
}