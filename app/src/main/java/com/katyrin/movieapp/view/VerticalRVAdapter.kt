package com.katyrin.movieapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.katyrin.movieapp.databinding.RvVerticalBinding
import com.katyrin.movieapp.model.Genre
import com.katyrin.movieapp.model.Movie
import java.util.*

class VerticalRVAdapter(
    private val genres: SortedMap<Genre, List<Movie>>,
    private val favoriteListMovie: List<Movie>,
    private val onClickListener: FilmOnClickListener,
    private val onLikeListener: FavoriteFilmOnClickListener
): RecyclerView.Adapter<VerticalRVAdapter.VerticalViewHolder>() {

    inner class VerticalViewHolder(private val itemBinding: RvVerticalBinding):
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(genre: Genre) {
            itemBinding.genre.text = genre.name.capitalize(Locale.ROOT)
        }
        fun bind(moviesList: List<Movie>) {
            val context = itemBinding.horizontalRV.context
            val layoutManager = LinearLayoutManager(context)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            itemBinding.horizontalRV.layoutManager = layoutManager
            itemBinding.horizontalRV.adapter =
                MainRVAdapter(moviesList, favoriteListMovie, onClickListener, onLikeListener)
        }
    }

    override fun getItemCount(): Int = genres.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = RvVerticalBinding.inflate(layoutInflater, parent, false)
        return VerticalViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: VerticalViewHolder, position: Int) {
        val genre: Genre = genres.keys.elementAt(position)
        holder.bind(genre)
        val moviesList: List<Movie> = genres[genre]!!
        holder.bind(moviesList)
    }
}