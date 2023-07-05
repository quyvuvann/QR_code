package com.example.example_qr_code

import androidx.annotation.LongDef
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_table")
class QrModel (
    @PrimaryKey(autoGenerate = true) val id :Int=0,
    @ColumnInfo(name = "imageString") val imageString:Int,
    @ColumnInfo(name = "imageBitmap") val imageBitmap:String,
    @ColumnInfo(name = "titleTimeString") val titleTimeString: String,
    @ColumnInfo(name = "titleString") val titleString:String,
    @ColumnInfo(name = "timeString") val timeString:String,
    @ColumnInfo(name = "linkString") val linkString:String,
    @ColumnInfo(name = "phone") val phone:String,
    @ColumnInfo(name = "message") val message:String,
    @ColumnInfo(name = "email") val email:String,
    @ColumnInfo(name = "topic") val topic:String,
    @ColumnInfo(name = "content") val content:String,
    @ColumnInfo(name = "document") val document: String,
    @ColumnInfo(name = "fullName") val fullName:String,
    @ColumnInfo(name = "workPlace") val workPlace:String,
    @ColumnInfo(name = "address") val address:String,
    @ColumnInfo(name = "note") val note :String,
    @ColumnInfo(name = "networkName") val networkName:String,
    @ColumnInfo(name = "password") val password:String,
    @ColumnInfo(name = "typeWifi") val typeWifi:String,
    @ColumnInfo(name = "latitude") val latitude:String,
    @ColumnInfo(name = "longitude") val longitude:String,
    @ColumnInfo(name = "query") val query:String

){}