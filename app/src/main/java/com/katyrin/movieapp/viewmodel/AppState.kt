package com.katyrin.movieapp.viewmodel

import com.katyrin.movieapp.model.Genre
import com.katyrin.movieapp.model.Movie
import java.util.*

sealed class AppState {
    data class SuccessMainQuery(val movies: SortedMap<Genre, List<Movie>>): AppState()
    data class SuccessSearch(val movies: List<Movie>): AppState()
    data class SuccessFavorites(val movies: List<Movie>): AppState()
    data class SuccessHistory(val movies: List<Movie>): AppState()
    data class SuccessNote(val movies: List<Movie>): AppState()
    data class Error(val error: Throwable): AppState()
    object Loading: AppState()
}
