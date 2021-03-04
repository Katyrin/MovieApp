package com.katyrin.movieapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katyrin.movieapp.App
import com.katyrin.movieapp.App.Companion.getHistoryDao
import com.katyrin.movieapp.model.Movie
import com.katyrin.movieapp.repository.LocalFavoritesRepositoryImpl
import com.katyrin.movieapp.repository.LocalRepository
import com.katyrin.movieapp.repository.LocalRepositoryImpl
import com.katyrin.movieapp.repository.LocalRepositoryNoteImpl

class HistoryViewModel(
    private val noteLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val historyLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val historyRepository: LocalRepository = LocalRepositoryImpl(getHistoryDao()),
    private var noteRepository: LocalRepository = LocalRepositoryNoteImpl(App.getNoteDao()),
    private val favoritesLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val favoritesRepository: LocalRepository = LocalFavoritesRepositoryImpl(App.getFavoritesDao())
) : ViewModel() {

    fun getFavoritesData() = favoritesLiveData

    fun getAllFavorites() {
        favoritesLiveData.value = AppState.Loading
        Thread {
            favoritesLiveData.postValue(AppState.SuccessSearch(favoritesRepository.getAllMovies()))
        }.start()
    }

    fun deleteFavoriteMovieToDB(idMovie: Long) {
        Thread {
            favoritesRepository.deleteEntity(idMovie)
        }.start()
    }

    fun saveFavoriteMovieToDB(movie: Movie) {
        Thread {
            favoritesRepository.saveEntity(movie)
        }.start()
    }

    fun getHistoryData() = historyLiveData
    fun getNoteData() = noteLiveData

    fun getAllHistory() {
        historyLiveData.value = AppState.Loading
        Thread {
            historyLiveData.postValue(AppState.SuccessSearch(historyRepository.getAllMovies()))
        }.start()
    }

    fun getAllNotes() {
        noteLiveData.value = AppState.Loading
        Thread {
            noteLiveData.postValue(AppState.SuccessSearch(noteRepository.getAllMovies()))
        }.start()
    }
}