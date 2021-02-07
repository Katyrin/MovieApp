package com.katyrin.movieapp.viewmodel

import com.katyrin.movieapp.model.MoviesData

sealed class AppState {
    data class Success(val movies: MoviesData): AppState()
    data class Error(val error: Throwable): AppState()
    object Loading: AppState()
}
