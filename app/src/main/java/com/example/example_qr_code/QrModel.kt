package com.example.example_qr_code

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_table")
class QrModel (
    @PrimaryKey(autoGenerate = true) val id :Int=0,
    @ColumnInfo(name = "titleTimeString") val titleTimeString: String,
    @ColumnInfo(name = "titleString") val titleString:String,
    @ColumnInfo(name = "timeString") val timeString:String,
    @ColumnInfo(name = "linkString") val linkString:String,
){}