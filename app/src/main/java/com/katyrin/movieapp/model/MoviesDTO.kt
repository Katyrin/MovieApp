package com.katyrin.movieapp.model

import com.google.gson.annotations.SerializedName

data class MoviesDTO(
    val results: Array<ResultsDTO>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MoviesDTO

        if (results != null) {
            if (other.results == null) return false
            if (!results.contentEquals(other.results)) return false
        } else if (other.results != null) return false

        return true
    }

    override fun hashCode(): Int {
        return results?.contentHashCode() ?: 0
    }
}

data class ResultsDTO(
    val title: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("vote_average")
    val voteAverage: String?,
    val overview: String?,
    @SerializedName("id")
    val idMovie: Long?
)