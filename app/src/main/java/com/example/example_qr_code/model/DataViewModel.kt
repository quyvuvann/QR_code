package com.example.example_qr_code.model

import android.graphics.Bitmap
import android.net.Uri
import com.example.example_qr_code.base.BaseViewModel
import java.net.URI

class DataViewModel : BaseViewModel(){
    var bitmap:Bitmap ?= null
    var uri : Uri ?= null
    var uriShow :Uri ?= null
    var image: Int? = null
    var titleCr: Int? = null

    fun setImageBitmap(bitmap: Bitmap){
        this.bitmap =bitmap
    }
    fun setUriReceiver(uri: Uri){
        this.uri = uri
    }
    fun setUriShoww(uri: Uri){
        this.uriShow = uri
    }
    fun setDataQrcode(image:Int,title:Int){
        this.image = image
        this.titleCr = title
    }
}