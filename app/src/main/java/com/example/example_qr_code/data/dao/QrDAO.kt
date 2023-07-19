package com.example.example_qr_code.data.dao

import androidx.room.*
import com.example.example_qr_code.QrModel
import com.example.example_qr_code.model.MyQrModel


@Dao
interface QrDAO {
    @Query("SELECT*FROM qr_table ORDER BY id ASC")
    suspend fun getAllQr(): MutableList<QrModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQr(qr: QrModel) :Long

//    @Query("DELETE FROM qr_table WHERE id = :qrString")
   @Query("DELETE FROM qr_table WHERE timeString LIKE '%' || :qrString || '%'")
    suspend fun deleteQr(qrString: String)


    @Query("SELECT * FROM qr_table WHERE id = :itemId")
    fun getItemById(itemId: Int): QrModel?

    @Update
    fun updateItem(item: QrModel?)
}