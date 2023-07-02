package com.example.example_qr_code.fragment

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.example_qr_code.CreateViewModel
import com.example.example_qr_code.R
import com.example.example_qr_code.base.BaseFragment
import com.example.example_qr_code.databinding.FragmentCreateDocumentBinding
import com.example.example_qr_code.databinding.FragmentCreatePhoneBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class CreatedPhoneFragment : BaseFragment<FragmentCreatePhoneBinding, CreateViewModel>() {
    override val viewModel: CreateViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.fragment_create_phone

    override fun setupView() {

    }

    override fun listener() {
        mBinding.xToolBar.setToolbarClickListener(
            clickRight = {
                val text = mBinding.edtPhoneNumber.text.toString().trim()
                val writer = MultiFormatWriter()


                if (text.isEmpty()) {
                    Toast.makeText(activityOwner, "cannot be left blank", Toast.LENGTH_SHORT).show()
                } else {
                    val matrix = writer.encode(text, BarcodeFormat.QR_CODE, 600, 600)
                    val encode = BarcodeEncoder()
                    val bitmap = encode.createBitmap(matrix)
                    Log.d("TAG", "setupVieww: $bitmap")
                    dataViewModel.setImageBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true))
                    dataViewModel.setDataQrcode(R.drawable.ic_phone, R.string.phone)
                    findNavController().navigate(R.id.action_createdPhoneFragment_to_showQrCodeFragment)
                }

            }, clickLeft = {
                onBackPressed()
            })
    }

    override fun onBackPressed(): Boolean {
        findNavController().popBackStack()
        return true
    }
}