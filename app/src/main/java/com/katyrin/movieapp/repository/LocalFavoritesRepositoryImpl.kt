package com.katyrin.movieapp.repository

import com.katyrin.movieapp.model.Movie
import com.katyrin.movieapp.model.room.FavoritesDao
import com.katyrin.movieapp.model.room.FavoritesEntity

class LocalFavoritesRepositoryImpl(private val localDataSource: FavoritesDao): LocalRepository {

    override fun getAllMovies(): List<Movie> {
        return convertHistoryEntityToMovie(localDataSource.all())
    }

    override fun saveEntity(movie: Movie) {
        localDataSource.insert(convertMovieToEntity(movie))
    }

    override fun deleteEntity(idMovie: Long) {
        localDataSource.deleteByIdMovie(idMovie)
    }

    private fun convertHistoryEntityToMovie(entityList: List<FavoritesEntity>): List<Movie> {
        return entityList.map {
            Movie(it.title, it.posterPath, it.releaseDate, it.voteAverage, it.overview,
                it.dateSearching, it.filmNote, it.idMovie)
        }
    }

    private fun convertMovieToEntity(movie: Movie): FavoritesEntity {
        return FavoritesEntity(0, movie.title, movie.posterPath, movie.releaseDate,
            movie.voteAverage, movie.overview, movie.dateSearching, movie.filmNote, movie.idMovie)
    }
}