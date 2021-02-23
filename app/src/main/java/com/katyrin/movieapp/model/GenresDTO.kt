package com.katyrin.movieapp.model

data class GenresDTO(
    val genres: Array<GenreDTO>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GenresDTO

        if (genres != null) {
            if (other.genres == null) return false
            if (!genres.contentEquals(other.genres)) return false
        } else if (other.genres != null) return false

        return true
    }

    override fun hashCode(): Int {
        return genres?.contentHashCode() ?: 0
    }
}

data class GenreDTO(
    val id: Int?,
    val name: String?
)
