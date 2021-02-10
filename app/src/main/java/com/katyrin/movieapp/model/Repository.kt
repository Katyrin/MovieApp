package com.katyrin.movieapp.model

interface Repository {
    fun getMovieFromServer(): MoviesData
    fun getMovieFromLocalStorage(): MoviesData
}