package com.katyrin.movieapp

import android.app.Application
import androidx.room.Room
import com.katyrin.movieapp.model.room.*

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object {
        private lateinit var appInstance: App
        private var db: MainDataBase? = null
        private const val DB_NAME = "MainDataBase.db"

        private fun getMainDB(): MainDataBase? {
            if (db == null) {
                synchronized(MainDataBase::class.java) {
                    if (db == null) {
                        db = Room.databaseBuilder(
                            appInstance.applicationContext,
                            MainDataBase::class.java, DB_NAME).build()
                    }
                }
            }
            return db
        }

        fun getHistoryDao(): HistoryDao {
            return getMainDB()!!.historyDao()
        }

        fun getNoteDao(): NoteDao {
            return getMainDB()!!.noteDao()
        }

        fun getFavoritesDao(): FavoritesDao {
            return getMainDB()!!.favoritesDao()
        }

        fun getCinemasDao(): CinemasDao {
            return getMainDB()!!.cinemasDao()
        }
    }
}