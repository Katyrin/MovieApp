package com.katyrin.movieapp.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoritesEntity(
    val id: Long,
    val title: String,
    val posterPath: String,
    val releaseDate: String,
    val voteAverage: String,
    val overview: String,
    val dateSearching: String,
    val filmNote: String,
    @PrimaryKey
    val idMovie: Long
)
