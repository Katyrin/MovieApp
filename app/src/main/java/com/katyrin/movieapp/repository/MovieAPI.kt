package com.katyrin.movieapp.repository

import com.katyrin.movieapp.model.GenresDTO
import com.katyrin.movieapp.model.MoviesDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieAPI {
    @GET("3/genre/movie/list")
    fun getGenres(
        @Query("api_key") token: String,
        @Query("language") language: String
    ): Call<GenresDTO>

    @GET("3/discover/movie")
    fun getMoviesByGenre(
        @Query("api_key") token: String,
        @Query("language") language: String,
        @Query("sort_by") sortBy: String,
        @Query("with_genres") withGenres: Int,
        @Query("include_adult") includeAdult: Boolean,
        @Query("vote_average.gte") voteAverage: Int,
        @Query("primary_release_date.gte") minReleaseDate: String
    ): Call<MoviesDTO>

    @GET("3/search/movie")
    fun getSearchMovies(
            @Query("api_key") token: String,
            @Query("language") language: String,
            @Query("include_adult") includeAdult: Boolean,
            @Query("query") query: String,
            @Query("primary_release_date.gte") minReleaseDate: String
    ): Call<MoviesDTO>
}