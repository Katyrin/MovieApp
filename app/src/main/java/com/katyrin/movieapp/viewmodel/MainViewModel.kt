package com.katyrin.movieapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katyrin.movieapp.model.Repository
import com.katyrin.movieapp.model.RepositoryImpl
import java.lang.Thread.sleep

class MainViewModel(
    private val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData(),
    private val repositoryImpl: Repository = RepositoryImpl()
) : ViewModel() {

    fun getMoviesFromLocalSource() = getDataFromLocalSource()

    fun getMoviesFromRemoteSource() = getDataFromLocalSource()

    fun getLiveData() = liveDataToObserve

    private fun getDataFromLocalSource() {
        liveDataToObserve.value = AppState.Loading
        Thread {
            sleep(500)
            liveDataToObserve.postValue(AppState.Success(repositoryImpl.getMovieFromLocalStorage()))
        }.start()
    }
}