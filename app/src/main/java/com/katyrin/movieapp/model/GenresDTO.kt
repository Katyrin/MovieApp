package com.katyrin.movieapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class GenresDTO(
    val genres: Array<GenreDTO?>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GenresDTO

        if (!genres.contentEquals(other.genres)) return false

        return true
    }

    override fun hashCode(): Int {
        return genres.contentHashCode()
    }
}

@Parcelize
data class GenreDTO(
    val id: Int,
    val name: String
): Parcelable
