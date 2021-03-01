package com.katyrin.movieapp.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val idMovie: Long,
    val note: String
)
