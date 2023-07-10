package com.example.example_qr_code.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.example_qr_code.QrFavoriteModel

@Dao
interface QrFavoriteDao {
    @Query("SELECT*FROM qr_table_favorite ORDER BY id ASC")
    suspend fun getAllFavoriteQr(): MutableList<QrFavoriteModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteQr(qr: QrFavoriteModel)

    @Query("DELETE FROM qr_table_favorite WHERE timeString LIKE '%' || :qrString || '%'")
    suspend fun deleteFavoriteQr(qrString: String)
}