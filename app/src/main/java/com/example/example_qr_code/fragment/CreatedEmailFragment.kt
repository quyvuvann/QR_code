package com.example.example_qr_code.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.example_qr_code.CreateViewModel
import com.example.example_qr_code.R
import com.example.example_qr_code.base.BaseFragment
import com.example.example_qr_code.databinding.FragmentCreateDocumentBinding
import com.example.example_qr_code.databinding.FragmentCreateEmailBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class CreatedEmailFragment : BaseFragment<FragmentCreateEmailBinding,CreateViewModel>() {
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
    private fun initQrCode(){
        val email = mBinding.edtEmail.text.toString().trim()
        val topic = mBinding.edtTopic.text.toString().trim()
        val content = mBinding.edtContent.text.toString().trim()
        val stringBuilder = StringBuilder()
        stringBuilder.append("$email | $topic | $content")
        val multiFormatWriter = MultiFormatWriter()
        try {
            val matrix = multiFormatWriter.encode(stringBuilder.toString(), BarcodeFormat.QR_CODE, 600, 600)
            val encode = BarcodeEncoder()
            val bitmap = encode.createBitmap(matrix)
            dataViewModel.setImageBitmap(bitmap)
            findNavController().navigate(R.id.action_createdEmailFragment_to_showQrCodeFragment)
        }catch (e:Exception){

        }
    }
    override fun onBackPressed(): Boolean {
        findNavController().popBackStack()
        return true
    }
}