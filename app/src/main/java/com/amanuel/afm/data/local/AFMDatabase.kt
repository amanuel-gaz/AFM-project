package com.amanuel.afm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.amanuel.afm.data.model.TransactionEntity

@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AFMDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AFMDatabase? = null

        fun getDatabase(context: Context): AFMDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AFMDatabase::class.java,
                    "afm_finance_database.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
