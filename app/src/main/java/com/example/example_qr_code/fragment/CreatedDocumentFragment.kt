package com.example.example_qr_code.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.example_qr_code.*
import com.example.example_qr_code.base.BaseFragment
import com.example.example_qr_code.databinding.FragmentCreateDocumentBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CreatedDocumentFragment : BaseFragment<FragmentCreateDocumentBinding, CreateViewModel>() {
    override val viewModel: CreateViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.fragment_create_document

    override fun setupView() {

    }

    override fun listener() {
        mBinding.xToolBar.setToolbarClickListener(
            clickRight = {
                initQrCode()
            }, clickLeft = {
                onBackPressed()
            })


    }

    private fun initQrCode() {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val strDate: String = sdf.format(c.time)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date: String = dateFormat.format(c.time)
        val text = mBinding.editText.text.toString().trim()
        val writer = MultiFormatWriter()


        if (text.isEmpty()) {
            Toast.makeText(activityOwner, "cannot be left blank", Toast.LENGTH_SHORT).show()
        } else {
            val matrix = writer.encode(text, BarcodeFormat.QR_CODE, 600, 600)
            val encode = BarcodeEncoder()
            val bitmap = encode.createBitmap(matrix)
            lifecycleScope.launch {
                val daoQr = QrRoomDatabase.getDataBase(activityOwner).qrDao()
                daoQr.insertQr(
                    QrModel(
                        imageString = R.drawable.ic_document,
                        imageBitmap = bitmapTOString(bitmap),
                        titleTimeString = date,
                        titleString = "Document",
                        timeString = strDate,
                        linkString = "",
                        phone = "",
                        message = "",
                        email = "",
                        topic = "",
                        content = "",
                        document = text,
                        fullName = "",
                        workPlace = "",
                        address = "",
                        note = "",
                        networkName = "",
                        password = "",
                        typeWifi = "",
                        latitude = "",
                        longitude = "",
                        query = ""
                    )
                )
            }
            Log.d("TAG", "setupVieww: $bitmap")
            dataViewModel.setImageBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true))
            dataViewModel.setDataQrcode(R.drawable.ic_document, R.string.document)
            findNavController().navigate(R.id.action_createdDocumentFragment_to_showQrCodeFragment)
        }
    }

    override fun onBackPressed(): Boolean {
        findNavController().popBackStack()
        return true
    }

}