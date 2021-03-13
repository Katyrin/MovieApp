package com.katyrin.movieapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katyrin.movieapp.App
import com.katyrin.movieapp.model.Cinema
import com.katyrin.movieapp.repository.CinemasRepository
import com.katyrin.movieapp.repository.CinemasRepositoryImpl

class MapsViewModel(
    private val cinemasLiveData: MutableLiveData<List<Cinema>> = MutableLiveData(),
    private val cinemasRepository: CinemasRepository = CinemasRepositoryImpl(App.getCinemasDao())
): ViewModel() {

    fun getLiveData() = cinemasLiveData

    fun getAllCinemas() {
        Thread {
            cinemasLiveData.postValue(cinemasRepository.getAllCinemas())
        }.start()
    }

    fun saveCinemaToDB(cinema: Cinema) {
        Thread {
            cinemasRepository.saveEntity(cinema)
            getAllCinemas()
        }.start()
    }

    fun deleteCinemaToDB(placeName: String) {
        Thread {
            cinemasRepository.deleteEntity(placeName)
            getAllCinemas()
        }.start()
    }
}