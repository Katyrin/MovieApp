package com.katyrin.movieapp.model.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HistoryEntity::class, NoteEntity::class], version = 1, exportSchema = false)
abstract class MainDataBase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun noteDao(): NoteDao
}