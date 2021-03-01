package com.katyrin.movieapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katyrin.movieapp.model.CORRUPTED_DATA
import com.katyrin.movieapp.model.MoviesDTO
import com.katyrin.movieapp.model.REQUEST_ERROR
import com.katyrin.movieapp.model.SERVER_ERROR
import com.katyrin.movieapp.repository.MoviesRepository
import com.katyrin.movieapp.repository.MoviesRepositoryImpl
import com.katyrin.movieapp.repository.RemoteDataSource
import com.katyrin.movieapp.utils.convertMoviesDtoToModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchMoviesViewModel(
        val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData(),
        private val moviesRepositoryImpl: MoviesRepository = MoviesRepositoryImpl(RemoteDataSource())
) : ViewModel() {

    fun getSearchMoviesRemoteSource(language: String, includeAdult: Boolean, query: String) {
        moviesRepositoryImpl.getSearchMoviesFromServer(
                language, includeAdult, query, callBackSearchMovies)
    }

    private val callBackSearchMovies = object : Callback<MoviesDTO> {
        override fun onResponse(call: Call<MoviesDTO>, response: Response<MoviesDTO>) {
            val serverResponse: MoviesDTO? = response.body()
            liveDataToObserve.postValue(
                    if (response.isSuccessful && serverResponse != null) {
                        checkResponse(serverResponse)
                    } else {
                        AppState.Error(Throwable(SERVER_ERROR))
                    }
            )
        }
        override fun onFailure(call: Call<MoviesDTO>, t: Throwable) {
            liveDataToObserve.postValue(AppState.Error(Throwable(t.message ?: REQUEST_ERROR)))
        }
        private fun checkResponse(serverResponse: MoviesDTO): AppState {
            val results = serverResponse.results
            return if (results == null) {
                AppState.Error(Throwable(CORRUPTED_DATA))
            } else {
                AppState.SuccessSearch(convertMoviesDtoToModel(serverResponse))
            }
        }
    }
}


