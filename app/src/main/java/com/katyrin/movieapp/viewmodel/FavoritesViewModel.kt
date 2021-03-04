package com.katyrin.movieapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katyrin.movieapp.App
import com.katyrin.movieapp.model.Movie
import com.katyrin.movieapp.repository.LocalFavoritesRepositoryImpl
import com.katyrin.movieapp.repository.LocalRepository
import com.katyrin.movieapp.repository.LocalRepositoryNoteImpl

class FavoritesViewModel(
    private val noteLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val favoritesLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val favoritesRepository: LocalRepository = LocalFavoritesRepositoryImpl(App.getFavoritesDao()),
    private var noteRepository: LocalRepository = LocalRepositoryNoteImpl(App.getNoteDao())
) : ViewModel() {

    fun getFavoritesData() = favoritesLiveData
    fun getNoteData() = noteLiveData

    fun getAllFavorites() {
        favoritesLiveData.value = AppState.Loading
        Thread {
            favoritesLiveData.postValue(AppState.SuccessSearch(favoritesRepository.getAllMovies()))
        }.start()
    }

    fun getAllNotes() {
        noteLiveData.value = AppState.Loading
        Thread {
            noteLiveData.postValue(AppState.SuccessSearch(noteRepository.getAllMovies()))
        }.start()
    }

    fun saveFavoriteMovieToDB(movie: Movie) {
        Thread {
            favoritesRepository.saveEntity(movie)
        }.start()
    }

    fun deleteFavoriteMovieToDB(idMovie: Long) {
        Thread {
            favoritesRepository.deleteEntity(idMovie)
        }.start()
    }

}