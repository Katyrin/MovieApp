package com.katyrin.movieapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katyrin.movieapp.App
import com.katyrin.movieapp.model.*
import com.katyrin.movieapp.repository.*
import com.katyrin.movieapp.utils.convertGenresDtoToModel
import com.katyrin.movieapp.utils.convertMoviesDtoToModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(
    val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData(),
    private val moviesRepositoryImpl: MoviesRepository = MoviesRepositoryImpl(RemoteDataSource()),
    val favoritesLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val favoritesRepository: LocalRepository = LocalFavoritesRepositoryImpl(App.getFavoritesDao())
) : ViewModel() {

    private val mutableMap: LinkedHashMap<Genre, List<Movie>> = linkedMapOf()
    private lateinit var language: String
    private var isDataShowAdult = false
    private var voteAverage = 0
    private lateinit var minReleaseDate: String

    fun saveFavoriteMovieToDB(movie: Movie) {
        Thread {
            favoritesRepository.saveEntity(movie)
        }.start()
    }

    fun deleteFavoriteMovieToDB(idMovie: Long) {
        Thread {
            favoritesRepository.deleteEntity(idMovie)
        }.start()
    }

    fun getAllFavorites() {
        favoritesLiveData.value = AppState.Loading
        Thread {
            favoritesLiveData.postValue(AppState.SuccessSearch(favoritesRepository.getAllMovies()))
        }.start()
    }

    fun getGenresFromRemoteSource(language: String, isDataShowAdult: Boolean,
                                  voteAverage: Int, minReleaseDate: String) {
        this.language = language
        this.isDataShowAdult = isDataShowAdult
        this.voteAverage = voteAverage
        this.minReleaseDate = minReleaseDate
        liveDataToObserve.value = AppState.Loading
        moviesRepositoryImpl.getGenresFromServer(language, callBackGenres)
    }

    fun getMoviesFromRemoteSource(language: String,  sortBy: String, genre: Genre, size: Int,
                                  includeAdult: Boolean, voteAverage: Int, minReleaseDate: String) {
        moviesRepositoryImpl.getMoviesByGenreFromServer(language, sortBy, genre.id, includeAdult,
                voteAverage, minReleaseDate, object : Callback<MoviesDTO> {
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
                                results[0].overview == null ) {
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
                        it, --size, isDataShowAdult, voteAverage, minReleaseDate)
                }
                AppState.LoadingSecondQuery
            }
        }
    }
}