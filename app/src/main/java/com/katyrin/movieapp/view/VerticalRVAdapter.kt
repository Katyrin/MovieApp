package com.katyrin.movieapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.katyrin.movieapp.R
import com.katyrin.movieapp.model.Movie
import com.katyrin.movieapp.view.MovieFragment.Companion.BUNDLE_EXTRA

class VerticalRVAdapter(private val genres: Map<String, List<Movie>>
): RecyclerView.Adapter<VerticalRVAdapter.VerticalViewHolder>() {

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
        val str: String = genres.keys.elementAt(position)
        holder.genre.text = str
        val moviesList: List<Movie> = genres[str]!!
        createRV(holder.movies, moviesList)
    }

    private fun createRV(moviesRV: RecyclerView, moviesList: List<Movie>) {
        val context = moviesRV.context
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        moviesRV.layoutManager = layoutManager
        moviesRV.adapter = MainRVAdapter(moviesList, object : FilmOnClickListener {
            override fun onFilmClicked(movie: Movie) {
                val bundle = Bundle()
                bundle.putParcelable(BUNDLE_EXTRA, movie)

                val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                val movieFragment = MovieFragment.newInstance(bundle)
                transaction.replace(R.id.container, movieFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        })
    }
}