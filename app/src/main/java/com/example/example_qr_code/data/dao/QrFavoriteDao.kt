package com.example.example_qr_code.data.dao

import androidx.room.*
import com.example.example_qr_code.QrFavoriteModel
import com.example.example_qr_code.QrModel

@Dao
interface QrFavoriteDao {
    @Query("SELECT*FROM qr_table_favorite ORDER BY id ASC")
    suspend fun getAllFavoriteQr(): MutableList<QrFavoriteModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteQr(qr: QrFavoriteModel)

    @Query("DELETE FROM qr_table_favorite WHERE timeString LIKE '%' || :qrString || '%'")
    suspend fun deleteFavoriteQr(qrString: String)

    @Query("SELECT * FROM qr_table_favorite WHERE id = :itemId")
    fun getItemById(itemId: Int): QrFavoriteModel?

    @Query("SELECT id FROM qr_table_favorite ORDER BY id DESC LIMIT 1")
    fun getLastIdFavorite(): Long

    @Update
    fun updateItem(item: QrFavoriteModel?)
}