package com.example.example_qr_code.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_table")
class MyQrModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "imageBitmap") val imageBitmap: String,
    @ColumnInfo(name = "titleString") var titleString:String,
    @ColumnInfo(name = "idItem") var idItem:Int
){}