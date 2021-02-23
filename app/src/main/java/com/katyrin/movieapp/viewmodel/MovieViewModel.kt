package com.katyrin.movieapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katyrin.movieapp.model.Movie
import com.katyrin.movieapp.model.ResultsDTO

class MovieViewModel(
    private val liveDataToObserve: MutableLiveData<ResultsDTO> = MutableLiveData()
) : ViewModel() {

    fun getData() = liveDataToObserve

    fun setData(movie: ResultsDTO) {
        liveDataToObserve.value = movie
    }

}