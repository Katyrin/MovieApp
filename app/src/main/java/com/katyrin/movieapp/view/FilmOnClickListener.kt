package com.katyrin.movieapp.view

import com.katyrin.movieapp.model.Movie
import com.katyrin.movieapp.model.ResultsDTO

interface FilmOnClickListener {
    fun onFilmClicked(movie: ResultsDTO)
}