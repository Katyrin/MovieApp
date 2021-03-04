package com.katyrin.movieapp.view

import android.widget.ImageView
import com.katyrin.movieapp.model.Movie

interface FavoriteFilmOnClickListener {
    fun onFilmLiked(movie: Movie, favoriteImageView: ImageView)
}