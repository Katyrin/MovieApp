package com.katyrin.movieapp.repository

import com.katyrin.movieapp.model.Cinema
import com.katyrin.movieapp.model.room.CinemasDao
import com.katyrin.movieapp.model.room.CinemasEntity

class CinemasRepositoryImpl(private val localDataSource: CinemasDao): CinemasRepository {
    override fun getAllCinemas(): List<Cinema> {
        return convertCinemasEntityToCinema(localDataSource.all())
    }

    override fun saveEntity(cinema: Cinema) {
        localDataSource.insert(convertCinemaToEntity(cinema))
    }

    override fun deleteEntity(placeName: String) {
        localDataSource.deleteByPlaceName(placeName)
    }

    private fun convertCinemasEntityToCinema(entityList: List<CinemasEntity>): List<Cinema> {
        return entityList.map {
            Cinema(it.placeName, it.lat, it.lng)
        }
    }

    private fun convertCinemaToEntity(cinema: Cinema): CinemasEntity {
        return CinemasEntity(0, cinema.placeName, cinema.lat, cinema.lng)
    }
}