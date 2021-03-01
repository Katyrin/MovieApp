package com.katyrin.movieapp.repository

import com.katyrin.movieapp.model.Movie
import com.katyrin.movieapp.model.room.HistoryDao
import com.katyrin.movieapp.model.room.HistoryEntity

class LocalRepositoryImpl(private val localDataSource: HistoryDao): LocalRepository {

    override fun getAllMovies(): List<Movie> {
        return convertHistoryEntityToMovie(localDataSource.all())
    }

    override fun saveEntity(movie: Movie) {
        localDataSource.insert(convertMovieToEntity(movie))
    }

    private fun convertHistoryEntityToMovie(entityList: List<HistoryEntity>): List<Movie> {
        return entityList.map {
            Movie(it.title, it.posterPath, it.releaseDate, it.voteAverage, it.overview,
            dateSearching = it.dateSearching, filmNote = it.filmNote, idMovie = it.idMovie)
        }
    }

    private fun convertMovieToEntity(movie: Movie): HistoryEntity {
        return HistoryEntity(0, movie.title, movie.posterPath, movie.releaseDate,
        movie.voteAverage, movie.overview, movie.dateSearching, movie.filmNote, movie.idMovie)
    }
}