package com.example.example_qr_code.fragment

import android.graphics.Bitmap
import android.graphics.Color
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.example_qr_code.CreateViewModel
import com.example.example_qr_code.R
import com.example.example_qr_code.base.BaseFragment
import com.example.example_qr_code.databinding.FragmentCreateDocumentBinding
import com.example.example_qr_code.databinding.FragmentCreateEmailBinding
import com.example.example_qr_code.generateQRCode
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.net.URLEncoder
import java.util.*

class CreatedEmailFragment : BaseFragment<FragmentCreateEmailBinding, CreateViewModel>() {
    override val viewModel: CreateViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.fragment_create_email

    override fun setupView() {

    }

    override fun listener() {
        mBinding.xToolBar.setToolbarClickListener(clickLeft = {
            onBackPressed()
        }, clickRight = {
            initQrCode()
        })
    }

    private fun initQrCode() {
        val email = mBinding.edtEmail.text.toString().trim()
        val topic = mBinding.edtTopic.text.toString().trim()
        val content = mBinding.edtContent.text.toString().trim()
        val data = "mailto:$email?subject=${encodeContent(topic)}&body=${encodeSubject(content)}"
        val bitmap = generateQRCode(data)
        dataViewModel.setImageBitmap(bitmap!!)
        dataViewModel.setDataQrcode(R.drawable.ic_email, R.string.email)
        if (email.isEmpty()) {
            Toast.makeText(activityOwner, "cannot be left blank", Toast.LENGTH_SHORT).show()
        } else {
            findNavController().navigate(R.id.action_createdEmailFragment_to_showQrCodeFragment)
        }

    }

    override fun onBackPressed(): Boolean {
        findNavController().popBackStack()
        return true
    }

    private fun encodeSubject(subject: String): String {
        return URLEncoder.encode(subject,"UTF-8")

    }

    private fun encodeContent(content: String): String {
        return URLEncoder.encode(content,"UTF-8")
    }
}