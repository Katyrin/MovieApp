package com.katyrin.movieapp.repository

import com.google.gson.GsonBuilder
import com.katyrin.movieapp.model.BASE_URL
import com.katyrin.movieapp.model.GenresDTO
import com.katyrin.movieapp.model.MOVIE_API_KEY
import com.katyrin.movieapp.model.MoviesDTO
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {

    private val genresApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .client(createOkHttpClient(MovieApiInterceptor()))
        .build().create(MovieAPI::class.java)

    private fun createOkHttpClient(interceptor: Interceptor): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor(interceptor)
        okHttpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        return okHttpClient.build()
    }

    fun getGenresDetails(language: String, callback: Callback<GenresDTO>) {
        genresApi.getGenres(MOVIE_API_KEY, language).enqueue(callback)
    }

    fun getMoviesByGenreDetails(language: String, sortBy: String, withGenres: Int,
                                includeAdult: Boolean, voteAverage: Int, callback: Callback<MoviesDTO>) {
        genresApi.getMoviesByGenre(MOVIE_API_KEY, language, sortBy, withGenres,
                includeAdult, voteAverage).enqueue(callback)
    }

    fun getSearchMovies(language: String, includeAdult: Boolean, query: String,
                        callback: Callback<MoviesDTO>) {
        genresApi.getSearchMovies(MOVIE_API_KEY, language, includeAdult, query).enqueue(callback)
    }

    inner class MovieApiInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            return chain.proceed(chain.request())
        }
    }
}