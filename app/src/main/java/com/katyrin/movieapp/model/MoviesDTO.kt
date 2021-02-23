package com.katyrin.movieapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class MoviesDTO(
    val results: Array<ResultsDTO?>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MoviesDTO

        if (!results.contentEquals(other.results)) return false

        return true
    }

    override fun hashCode(): Int {
        return results.contentHashCode()
    }
}

@Parcelize
data class ResultsDTO(
    val title: String,
    val poster_path: String,
    val release_date: String,
    val vote_average: String,
    val overview: String,
    val genre_ids: Array<Int>
): Parcelable