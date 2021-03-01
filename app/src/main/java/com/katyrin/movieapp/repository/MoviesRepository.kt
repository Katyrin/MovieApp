package com.katyrin.movieapp.repository

import com.katyrin.movieapp.model.GenresDTO
import com.katyrin.movieapp.model.MoviesDTO
import retrofit2.Callback

interface MoviesRepository {
    fun getGenresFromServer(
        language: String,
        callback: Callback<GenresDTO>
    )

    fun getMoviesByGenreFromServer(
        language: String,
        sortBy: String,
        withGenres: Int,
        includeAdult: Boolean,
        voteAverage: Int,
        callback: Callback<MoviesDTO>
    )

    fun getSearchMoviesFromServer(
            language: String,
            includeAdult: Boolean,
            query: String,
            callback: Callback<MoviesDTO>
    )
}