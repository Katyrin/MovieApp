package com.katyrin.movieapp.model.room

import androidx.room.*

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM FavoritesEntity")
    fun all(): List<FavoritesEntity>

    @Query("SELECT * FROM FavoritesEntity WHERE title LIKE :title")
    fun getDataByWord(title: String): List<FavoritesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: FavoritesEntity)

    @Update
    fun update(entity: FavoritesEntity)

    @Delete
    fun delete(entity: FavoritesEntity)

    @Query("DELETE FROM FavoritesEntity WHERE idMovie LIKE :idMovie ")
    fun deleteByIdMovie(idMovie: Long)
}