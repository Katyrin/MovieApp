package com.katyrin.movieapp.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.katyrin.movieapp.R
import com.katyrin.movieapp.model.*
import java.util.*

class VerticalRVAdapter(
    private val genres: Array<GenreDTO?>
): RecyclerView.Adapter<VerticalRVAdapter.VerticalViewHolder>() {
    private lateinit var context: Context

    inner class VerticalViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val movies: RecyclerView = view.findViewById(R.id.horizontalRV)
        val genre: TextView = view.findViewById(R.id.genre)
    }

    override fun getItemCount(): Int = genres.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_vertical, parent, false)
        return VerticalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VerticalViewHolder, position: Int) {
        val str: String = genres[position]?.name ?: "null"
        holder.genre.text = str.capitalize(Locale.ROOT)
        context = holder.genre.context
        getResults(genres[position]?.id)

        val loadResultsReceiver2: BroadcastReceiver by lazy {
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    when(intent?.getStringExtra(DETAILS_LOAD_RESULT_MOVIES_EXTRA)) {
                        DETAILS_INTENT_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                        DETAILS_DATA_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                        DETAILS_RESPONSE_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                        DETAILS_REQUEST_ERROR_EXTRA -> TODO(PROCESS_ERROR)
                        DETAILS_REQUEST_ERROR_MESSAGE_EXTRA -> TODO(PROCESS_ERROR)
                        DETAILS_URL_MALFORMED_EXTRA -> TODO(PROCESS_ERROR)
                        DETAILS_RESULT_RESPONSE_SUCCESS_EXTRA -> {
                            createRV(
                                intent.getParcelableArrayExtra(DETAILS_RESULTS_LIST_EXTRA)
                                        as Array<ResultsDTO?>, holder.movies
                            )
                        }
                        else -> TODO(PROCESS_ERROR)
                    }
                }
            }
        }
        context.let {
            LocalBroadcastManager.getInstance(it)
                .registerReceiver(loadResultsReceiver2, IntentFilter(DETAILS_INTENT_FILTER_RESULT))
        }
    }

    private fun getResults(id: Int?) {
        context.let {
            it.startService(Intent(it, MoviesService::class.java).apply {
                putExtra(DETAILS_GENRE_ID_EXTRA, id)
            })
        }
    }

    private fun createRV(results: Array<ResultsDTO?>, horizontalRV: RecyclerView) {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        horizontalRV.layoutManager = layoutManager
        horizontalRV.adapter = MainRVAdapter(results, object : FilmOnClickListener {
            override fun onFilmClicked(movie: ResultsDTO) {
                val bundle = Bundle()
                bundle.putParcelable(MovieFragment.MOVIE_BUNDLE_EXTRA, movie)

                val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                val movieFragment = MovieFragment.newInstance(bundle)
                transaction.replace(R.id.container, movieFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        })

    }
}