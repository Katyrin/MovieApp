package com.katyrin.movieapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val title: String = "",
    val posterPath: String = "",
    val releaseDate: String = "",
    val voteAverage: String = "",
    val overview: String = "",
    var dateSearching: String = "",
    var filmNote: String = "",
    val idMovie: Long = 0,
): Parcelable
