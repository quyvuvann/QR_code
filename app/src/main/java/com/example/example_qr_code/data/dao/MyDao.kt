package com.example.example_qr_code.data.dao

import androidx.room.*
import com.example.example_qr_code.model.MyQrModel


@Dao
interface MyDao {
    @Query("SELECT*FROM my_table ORDER BY id ASC")
    suspend fun getAllMyQr(): MutableList<MyQrModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMyQr(qr: MyQrModel) :Long

    @Query("DELETE FROM my_table WHERE imageBitmap LIKE '%' || :qrString || '%'")
    suspend fun deleteMyQr(qrString: String)

    @Query("DELETE FROM my_table")
    suspend fun deleteAllItems()

    @Query("SELECT * FROM my_table WHERE id = :itemId")
    fun getItemById(itemId: Int): MyQrModel?

    @Update
    fun updateItem(item: MyQrModel?)

}