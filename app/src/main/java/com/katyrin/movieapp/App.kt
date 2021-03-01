package com.katyrin.movieapp

import android.app.Application
import androidx.room.Room
import com.katyrin.movieapp.model.room.HistoryDao
import com.katyrin.movieapp.model.room.MainDataBase
import com.katyrin.movieapp.model.room.NoteDao
import java.lang.IllegalStateException

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object {
        private var appInstance: App? = null
        private var db: MainDataBase? = null
        private const val DB_NAME = "MainDataBase.db"

        private fun getMainDB(): MainDataBase? {
            if (db == null) {
                synchronized(MainDataBase::class.java) {
                    if (db == null) {
                        if (appInstance == null)
                            throw IllegalStateException("Application is null while creating Database")
                        db = Room.databaseBuilder(appInstance!!.applicationContext,
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
    }
}