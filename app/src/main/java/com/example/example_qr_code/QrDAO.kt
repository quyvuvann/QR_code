package com.example.example_qr_code

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface QrDAO {
    @Query("SELECT*FROM qr_table ORDER BY id ASC")
    suspend fun getAllQr(): MutableList<QrModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQr(qr: QrModel)

    @Query("DELETE FROM qr_table WHERE id = :qrString")
    suspend fun deleteQr(qrString: Int)
}