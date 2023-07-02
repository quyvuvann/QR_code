package com.example.example_qr_code

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import com.example.example_qr_code.base.BaseViewModel

class CreateViewModel : BaseViewModel() {

    var bitmap: Bitmap? = null


    enum class Tool {
        WEB, DOCUMENT, CONTACT, EMAIL, SMS, PHONE, WIFI

    }

    data class CreateItem(
        val toolId: Tool, @DrawableRes val img: Int, val title: Int
    )

    fun menuCreate() = listOf(
        CreateItem(Tool.WEB, R.drawable.ic_link, R.string.web_link),
        CreateItem(Tool.DOCUMENT, R.drawable.ic_document, R.string.document),
        CreateItem(Tool.CONTACT, R.drawable.ic_person, R.string.contact),
        CreateItem(Tool.EMAIL, R.drawable.ic_email, R.string.email),
        CreateItem(Tool.SMS, R.drawable.ic_sms, R.string.sms),
        CreateItem(Tool.PHONE, R.drawable.ic_phone, R.string.phone),
        CreateItem(Tool.WIFI, R.drawable.ic_wifi, R.string.wifi)
    )

    fun setBitmapCode(bitmap: Bitmap) {
        this.bitmap = bitmap
    }


}