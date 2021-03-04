package com.katyrin.movieapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katyrin.movieapp.App
import com.katyrin.movieapp.App.Companion.getHistoryDao
import com.katyrin.movieapp.repository.LocalRepository
import com.katyrin.movieapp.repository.LocalRepositoryImpl
import com.katyrin.movieapp.repository.LocalRepositoryNoteImpl

class HistoryViewModel(
    private val noteLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val historyLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val historyRepository: LocalRepository = LocalRepositoryImpl(getHistoryDao()),
    private var noteRepository: LocalRepository = LocalRepositoryNoteImpl(App.getNoteDao())
) : ViewModel() {

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