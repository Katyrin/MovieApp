package com.katyrin.movieapp.view

import com.katyrin.movieapp.model.Movie

interface FilmOnClickListener {
    fun onFilmClicked(movie: Movie)
}