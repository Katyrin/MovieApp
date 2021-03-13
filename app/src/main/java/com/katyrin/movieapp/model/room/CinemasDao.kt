package com.katyrin.movieapp.model.room

import androidx.room.*

@Dao
interface CinemasDao {
    @Query("SELECT * FROM CinemasEntity")
    fun all(): List<CinemasEntity>

    @Query("SELECT * FROM CinemasEntity WHERE placeName LIKE :placeName")
    fun getDataByPlaceName(placeName: String): List<CinemasEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: CinemasEntity)

    @Update
    fun update(entity: CinemasEntity)

    @Delete
    fun delete(entity: CinemasEntity)

    @Query("DELETE FROM CinemasEntity WHERE placeName LIKE :placeName ")
    fun deleteByPlaceName(placeName: String)
}