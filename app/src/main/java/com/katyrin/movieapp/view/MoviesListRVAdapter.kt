package com.katyrin.movieapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.katyrin.movieapp.R
import com.katyrin.movieapp.databinding.RvHistoryItemBinding
import com.katyrin.movieapp.model.IMAGE_BASE_URL
import com.katyrin.movieapp.model.Movie
import com.squareup.picasso.Picasso

class MoviesListRVAdapter(
    private val onLikeListener: FavoriteFilmOnClickListener
): RecyclerView.Adapter<MoviesListRVAdapter.HistoryViewHolder>() {

    private var data: List<Movie> = arrayListOf()
    private lateinit var favoriteListMovie: List<Movie>

    fun setData(data: List<Movie>, favoriteListMovie: List<Movie>) {
        this.data = data
        this.favoriteListMovie = favoriteListMovie
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = RvHistoryItemBinding.inflate(layoutInflater, parent, false)
        return HistoryViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class HistoryViewHolder(private val itemBinding: RvHistoryItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(movie: Movie) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                setLike(movie, itemBinding.favoriteImageView)
                itemBinding.movieName.text = movie.title
                itemBinding.ratingTextView.text = movie.voteAverage
                itemBinding.yearTextView.text = movie.releaseDate
                itemBinding.description.text = movie.overview
                val date =
                    "${itemBinding.root.context.getString(R.string.date_viewed)} ${movie.dateSearching}"
                itemBinding.dateSearching.text = date
                val note = "${itemBinding.root.context.getString(R.string.note)} ${movie.filmNote}"
                itemBinding.myNote.text = note

                Picasso.get()
                    .load(IMAGE_BASE_URL + movie.posterPath)
                    .into(itemBinding.itemImageView)

                itemBinding.favoriteImageView.setOnClickListener {
                    onLikeListener.onFilmLiked(movie, itemBinding.favoriteImageView)
                }
            }
        }
    }

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