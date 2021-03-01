package com.katyrin.movieapp.viewmodel

import com.katyrin.movieapp.model.Genre
import com.katyrin.movieapp.model.Movie
import java.util.*

sealed class AppState {
    data class Success(val movies: SortedMap<Genre, List<Movie>>): AppState()
    data class SuccessSearch(val movies: List<Movie>): AppState()
    data class Error(val error: Throwable): AppState()
    object Loading: AppState()
    object LoadingSecondQuery: AppState()
}
