package com.katyrin.movieapp.repository

import com.katyrin.movieapp.model.Cinema

interface CinemasRepository {
    fun getAllCinemas(): List<Cinema>
    fun saveEntity(cinema: Cinema)
    fun deleteEntity(placeName: String)
}