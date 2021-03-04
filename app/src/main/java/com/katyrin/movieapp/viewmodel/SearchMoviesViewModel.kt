package com.katyrin.movieapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katyrin.movieapp.App
import com.katyrin.movieapp.model.*
import com.katyrin.movieapp.repository.*
import com.katyrin.movieapp.utils.convertMoviesDtoToModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchMoviesViewModel(
        val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData(),
        private val moviesRepositoryImpl: MoviesRepository = MoviesRepositoryImpl(RemoteDataSource()),
        private val favoritesLiveData: MutableLiveData<AppState> = MutableLiveData(),
        private val favoritesRepository: LocalRepository = LocalFavoritesRepositoryImpl(App.getFavoritesDao())
) : ViewModel() {

    fun getSearchMoviesRemoteSource(language: String, includeAdult: Boolean,
                                    query: String, minReleaseDate: String) {
        moviesRepositoryImpl.getSearchMoviesFromServer(
                language, includeAdult, query, minReleaseDate, callBackSearchMovies)
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
}


