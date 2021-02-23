package com.katyrin.movieapp.utils

import com.katyrin.movieapp.model.*

fun convertGenresDtoToModel(genresDTO: GenresDTO): List<Genre> {
    val genres: Array<GenreDTO> = genresDTO.genres!!
    val list: MutableList<Genre> = mutableListOf()
    genres.forEach {
        list.add(Genre(it.id!!, it.name!!))
    }
    return list
}

fun convertMoviesDtoToModel(moviesDTO: MoviesDTO): List<Movie> {
    val movies: Array<ResultsDTO> = moviesDTO.results!!
    val list: MutableList<Movie> = mutableListOf()
    movies.forEach {
        list.add(Movie(it.title!!, it.posterPath!!, it.releaseDate!!, it.voteAverage!!,
            it.overview!!, it.genreIds!!))
    }
    return list
}