package com.example.example_qr_code


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.*
import java.util.*

fun <T> Fragment.observer(liveData: LiveData<T>, onChange: (T) -> Unit) {
    liveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer(onChange))
}


fun <T> LiveData<T>.observeOnce(
    lifecycleOwner: LifecycleOwner,
    observer: androidx.lifecycle.Observer<T>
) {
    observe(lifecycleOwner, object : androidx.lifecycle.Observer<T> {
        override fun onChanged(value: T) {
            observer.onChanged(value)
            removeObserver(this)
        }

    })
}

fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>

@BindingAdapter("app:loadImg")
fun ImageView.loadImg(str: Any) = Glide.with(context).load(str).into(this)

fun generateQRCode(data: String): Bitmap? {

    val hints = Hashtable<EncodeHintType, Any>()
    hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.Q

    try {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix: BitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 500, 500, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}

fun createBitmap(view: View, context: Context): Bitmap {
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)

    val file = File.createTempFile("temp", ".png", context.cacheDir)
    val fos = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
    fos.close()
    return bitmap
}

fun bitmapTOString(bitmap: Bitmap): String {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    val byteArray: ByteArray = stream.toByteArray()
    val result: String = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
    return result
}

fun stringToBitMap(image: String?): Bitmap? {
    return try {
        val encodeByte: ByteArray = Base64.decode(image, Base64.DEFAULT)
        val inputStream: InputStream = ByteArrayInputStream(encodeByte)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.message
        null
    }
}

fun getImageUri(inContext: Context, inImage: Bitmap, title: String): Uri? {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path =
        MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, title, null)
    return Uri.parse(path)
}