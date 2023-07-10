package com.example.example_qr_code

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.example_qr_code.data.dao.QrFavoriteDao

@Database(entities = [QrModel::class, QrFavoriteModel::class], version = 1)
abstract class QrRoomDatabase : RoomDatabase() {
    abstract fun qrDao(): QrDAO
    abstract fun qrFavoriteDao(): QrFavoriteDao

    companion object {
        private var INSTANCE: QrRoomDatabase? = null
        private const val DB_NAME = "qr_db"
        fun getDataBase(context: Context): QrRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QrRoomDatabase::class.java,
                    DB_NAME
                ).build()
                return instance
            }
        }
    }
}