package com.example.example_qr_code.model

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.example_qr_code.base.BaseViewModel
import java.net.URI

class DataActivityViewModel : BaseViewModel(){
    var bitmapQr:Bitmap ?= null
    var uri : Uri ?= null
    var uriShow :Uri ?= null
    var image: Int? = null
    var titleCr: Int? = null
    private val _bitmapQRCode = MutableLiveData<Bitmap>()
    val bitmapQrCode: LiveData<Bitmap> = _bitmapQRCode

    fun initQrcode(bitmap: Bitmap){
        _bitmapQRCode.value = bitmap
    }

    fun setImageBitmap(bitmap: Bitmap){
        this.bitmapQr =bitmap
        Log.d("TAG", "setImageBitmap: $bitmap")
    }
    fun getBitmap() = bitmapQr
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