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
        callback: Callback<MoviesDTO>
    ) {
        remoteDataSource.getMoviesByGenreDetails(language, sortBy, withGenres, callback)
    }
}