package com.katyrin.movieapp.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CinemasEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val placeName: String,
    val lat: Double,
    val lng: Double
)