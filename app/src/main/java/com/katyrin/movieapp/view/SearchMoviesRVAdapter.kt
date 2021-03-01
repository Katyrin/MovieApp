package com.katyrin.movieapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.katyrin.movieapp.databinding.RvItemBinding
import com.katyrin.movieapp.model.IMAGE_BASE_URL
import com.katyrin.movieapp.model.Movie
import com.squareup.picasso.Picasso

class SearchMoviesRVAdapter(private val movies: List<Movie>,
                            private val onClickListener: FilmOnClickListener):
        RecyclerView.Adapter<SearchMoviesRVAdapter.SearchMoviesViewHolder>() {

    inner class SearchMoviesViewHolder(private val itemBinding: RvItemBinding):
            RecyclerView.ViewHolder(itemBinding.root) {
                fun bind(movie: Movie) {
                    itemBinding.movieName.text = movie.title
                    itemBinding.ratingTextView.text = movie.voteAverage
                    itemBinding.yearTextView.text = movie.releaseDate

                    Picasso.get()
                            .load(IMAGE_BASE_URL + movie.posterPath)
                            .into(itemBinding.itemImageView)

                    itemBinding.cardView.setOnClickListener {
                        onClickListener.onFilmClicked(movie)
                    }
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMoviesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = RvItemBinding.inflate(layoutInflater, parent, false)
        return SearchMoviesViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: SearchMoviesViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

}