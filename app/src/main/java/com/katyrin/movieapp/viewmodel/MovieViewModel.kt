package com.katyrin.movieapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katyrin.movieapp.App
import com.katyrin.movieapp.model.Movie
import com.katyrin.movieapp.repository.LocalRepository
import com.katyrin.movieapp.repository.LocalRepositoryImpl
import com.katyrin.movieapp.repository.LocalRepositoryNoteImpl

class MovieViewModel(
    private val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData(),
    private var historyRepository: LocalRepository = LocalRepositoryImpl(App.getHistoryDao()),
    private var noteRepository: LocalRepository = LocalRepositoryNoteImpl(App.getNoteDao())
) : ViewModel() {

    fun getData() = liveDataToObserve

    fun saveMovieToDB(movie: Movie) {
        Thread {
            historyRepository.saveEntity(movie)
        }.start()
    }

    fun saveNoteToDB(movie: Movie) {
        Thread {
            noteRepository.saveEntity(movie)
        }.start()
    }

    fun getAllNotes() {
        liveDataToObserve.value = AppState.Loading
        Thread {
            liveDataToObserve.postValue(AppState.SuccessNote(noteRepository.getAllMovies()))
        }.start()
    }

}