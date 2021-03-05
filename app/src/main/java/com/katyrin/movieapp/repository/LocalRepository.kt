package com.katyrin.movieapp.repository

import com.katyrin.movieapp.model.Movie

interface LocalRepository {
    fun getAllMovies(): List<Movie>
    fun saveEntity(movie: Movie)
    fun deleteEntity(idMovie: Long)
}