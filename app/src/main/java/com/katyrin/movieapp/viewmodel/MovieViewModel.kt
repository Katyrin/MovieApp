package com.katyrin.movieapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katyrin.movieapp.model.Movie

class MovieViewModel(
    private val liveDataToObserve: MutableLiveData<Movie> = MutableLiveData()
) : ViewModel() {

    fun getData() = liveDataToObserve

    fun setData(movie: Movie) {
        liveDataToObserve.value = movie
    }

}