package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CandidateProfileEntity::class,
        ApplicationRecordEntity::class,
        SavedJobEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class JobPilotDatabase : RoomDatabase() {
    abstract fun jobPilotDao(): JobPilotDao

    companion object {
        @Volatile
        private var INSTANCE: JobPilotDatabase? = null

        fun getInstance(context: Context): JobPilotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JobPilotDatabase::class.java,
                    "jobpilot_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
