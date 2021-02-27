package com.katyrin.movieapp.repository

import com.katyrin.movieapp.model.GenresDTO
import com.katyrin.movieapp.model.MoviesDTO
import retrofit2.Callback

class MoviesRepositoryImpl(private val remoteDataSource: RemoteDataSource): MoviesRepository {
    override fun getGenresFromServer(language: String, callback: Callback<GenresDTO>) {
        remoteDataSource.getGenresDetails(language, callback)
    }

    override fun getMoviesByGenreFromServer(
        language: String,
        sortBy: String,
        withGenres: Int,
        includeAdult: Boolean,
        voteAverage: Int,
        callback: Callback<MoviesDTO>
    ) {
        remoteDataSource.getMoviesByGenreDetails(language, sortBy, withGenres,
                includeAdult, voteAverage, callback)
    }
}