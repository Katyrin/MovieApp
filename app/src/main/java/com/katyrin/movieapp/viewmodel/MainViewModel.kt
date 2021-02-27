package com.katyrin.movieapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katyrin.movieapp.model.Genre
import com.katyrin.movieapp.model.GenresDTO
import com.katyrin.movieapp.model.Movie
import com.katyrin.movieapp.model.MoviesDTO
import com.katyrin.movieapp.repository.MoviesRepository
import com.katyrin.movieapp.repository.MoviesRepositoryImpl
import com.katyrin.movieapp.repository.RemoteDataSource
import com.katyrin.movieapp.utils.convertGenresDtoToModel
import com.katyrin.movieapp.utils.convertMoviesDtoToModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val SERVER_ERROR = "Ошибка сервера"
private const val REQUEST_ERROR = "Ошибка запроса на сервер"
private const val CORRUPTED_DATA = "Неполные данные"

class MainViewModel(
    val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData(),
    private val moviesRepositoryImpl: MoviesRepository = MoviesRepositoryImpl(RemoteDataSource())
) : ViewModel() {

    private val mutableMap: LinkedHashMap<Genre, List<Movie>> = linkedMapOf()
    private lateinit var language: String
    private var isDataShowAdult = false
    private var voteAverage = 0

    fun getGenresFromRemoteSource(language: String, isDataShowAdult: Boolean, voteAverage: Int) {
        this.language = language
        this.isDataShowAdult = isDataShowAdult
        this.voteAverage = voteAverage
        liveDataToObserve.value = AppState.Loading
        moviesRepositoryImpl.getGenresFromServer(language, callBackGenres)
    }

    fun getMoviesFromRemoteSource(language: String,  sortBy: String, genre: Genre, size: Int,
                                  includeAdult: Boolean, voteAverage: Int) {
        moviesRepositoryImpl.getMoviesByGenreFromServer(
                language,
                sortBy,
                genre.id,
                includeAdult,
                voteAverage,
                object : Callback<MoviesDTO> {
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
                        return if (results == null ||
                                results[0].title == null ||
                                results[0].posterPath == null ||
                                results[0].releaseDate == null ||
                                results[0].voteAverage == null ||
                                results[0].overview == null ||
                                results[0].genreIds == null ) {
                                    AppState.Error(Throwable(CORRUPTED_DATA))
                        } else {
                            mutableMap[genre] = convertMoviesDtoToModel(serverResponse)
                            if (size <= 0) {
                                AppState.Success(mutableMap.toSortedMap(compareBy { it.name }))
                            }
                            else
                                AppState.LoadingSecondQuery
                        }
                    }
                }
        )
    }

    private val callBackGenres = object : Callback<GenresDTO> {
        override fun onResponse(call: Call<GenresDTO>, response: Response<GenresDTO>) {
            val serverResponse: GenresDTO? = response.body()
            liveDataToObserve.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    checkResponse(serverResponse)
                } else {
                    AppState.Error(Throwable(SERVER_ERROR))
                }
            )
        }
        override fun onFailure(call: Call<GenresDTO>, t: Throwable) {
            liveDataToObserve.postValue(AppState.Error(Throwable(t.message ?: REQUEST_ERROR)))
        }
        private fun checkResponse(serverResponse: GenresDTO): AppState {
            val genres = serverResponse.genres
            return if (genres == null || genres[0].id == null || genres[0].name == null) {
                AppState.Error(Throwable(CORRUPTED_DATA))
            } else {
                val listGenre = convertGenresDtoToModel(serverResponse)
                var size = listGenre.size
                listGenre.forEach {
                    getMoviesFromRemoteSource(language, "popularity.desc",
                        it, --size, isDataShowAdult, voteAverage)
                }
                AppState.LoadingSecondQuery
            }
        }
    }
}