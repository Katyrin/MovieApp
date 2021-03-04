package com.katyrin.movieapp.repository

import com.katyrin.movieapp.model.Movie
import com.katyrin.movieapp.model.room.NoteDao
import com.katyrin.movieapp.model.room.NoteEntity

class LocalRepositoryNoteImpl(private val localDataSource: NoteDao): LocalRepository {

    override fun getAllMovies(): List<Movie> {
        return convertNoteEntityToMovie(localDataSource.all())
    }

    override fun saveEntity(movie: Movie) {
        localDataSource.insert(convertMovieToEntity(movie))
    }

    override fun deleteEntity(idMovie: Long) {
        TODO("Not yet implemented")
    }

    private fun convertNoteEntityToMovie(entityList: List<NoteEntity>): List<Movie> {
        return entityList.map { Movie(idMovie = it.idMovie, filmNote = it.note) }
    }

    private fun convertMovieToEntity(movie: Movie): NoteEntity {
        return NoteEntity(0, movie.idMovie, movie.filmNote)
    }
}