package com.katyrin.movieapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.katyrin.movieapp.R
import com.katyrin.movieapp.databinding.RvItemBinding
import com.katyrin.movieapp.model.IMAGE_BASE_URL
import com.katyrin.movieapp.model.Movie
import com.squareup.picasso.Picasso

class SearchMoviesRVAdapter(private val movies: List<Movie>,
                            private val favoriteListMovie: List<Movie>,
                            private val onLikeListener: FavoriteFilmOnClickListener,
                            private val onClickListener: FilmOnClickListener
                            ): RecyclerView.Adapter<SearchMoviesRVAdapter.SearchMoviesViewHolder>() {

    inner class SearchMoviesViewHolder(private val itemBinding: RvItemBinding):
            RecyclerView.ViewHolder(itemBinding.root) {
                fun bind(movie: Movie) {
                    setLike(movie, itemBinding.favoriteImageView)
                    itemBinding.movieName.text = movie.title
                    itemBinding.ratingTextView.text = movie.voteAverage
                    itemBinding.yearTextView.text = movie.releaseDate

                    Picasso.get()
                            .load(IMAGE_BASE_URL + movie.posterPath)
                            .into(itemBinding.itemImageView)

                    itemBinding.cardView.setOnClickListener {
                        onClickListener.onFilmClicked(movie)
                    }
                    itemBinding.favoriteImageView.setOnClickListener {
                        onLikeListener.onFilmLiked(movie, itemBinding.favoriteImageView)
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

    private fun setLike(movie: Movie, favoriteImageView: ImageView) {
        favoriteListMovie.map {
            when (it.idMovie == movie.idMovie) {
                true -> {
                    favoriteImageView.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            favoriteImageView.resources, R.drawable.ic_baseline_favorite_24, null
                        )
                    )
                }
            }
        }
    }
}