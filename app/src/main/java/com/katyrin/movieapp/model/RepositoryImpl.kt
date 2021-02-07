package com.katyrin.movieapp.model

class RepositoryImpl : Repository {
    override fun getMovieFromServer(): MoviesData {
        return MoviesData
    }

    override fun getMovieFromLocalStorage(): MoviesData {
        return MoviesData
    }
}