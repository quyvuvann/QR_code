package com.example.example_qr_code.fragment

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.example_qr_code.CreateViewModel
import com.example.example_qr_code.R
import com.example.example_qr_code.base.BaseFragment
import com.example.example_qr_code.databinding.FragmentCreateDocumentBinding
import com.example.example_qr_code.databinding.FragmentCreateSmsBinding
import com.example.example_qr_code.generateQRCode
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class CreatedSMSFragment : BaseFragment<FragmentCreateSmsBinding, CreateViewModel>() {
    override val viewModel: CreateViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.fragment_create_sms

    override fun setupView() {

    }

    override fun listener() {
        mBinding.xToolBar.setToolbarClickListener(
            clickRight = {
                initQrCode()
            }, clickLeft = {
                onBackPressed()
            }
        )
    }

    private fun initQrCode() {
        val phone = mBinding.edtPhoneNumber.text.toString().trim()
        val mess = mBinding.edtMess.text.toString().trim()

        val data = "SMSTO:$phone:$mess"
        val bitmap = generateQRCode(data)
        dataViewModel.setImageBitmap(bitmap!!)
        dataViewModel.setDataQrcode(R.drawable.ic_sms, R.string.sms)
        if (phone.isEmpty()) {
            Toast.makeText(activityOwner, "cannot be left blank", Toast.LENGTH_SHORT).show()
        } else {
            findNavController().navigate(R.id.action_createdSMSFragment_to_showQrCodeFragment)
        }

    }

    override fun onBackPressed(): Boolean {
        findNavController().popBackStack()
        return true
    }
}