package com.example.example_qr_code.fragment

import android.util.Log
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.example_qr_code.CreateViewModel
import com.example.example_qr_code.R
import com.example.example_qr_code.base.BaseFragment
import com.example.example_qr_code.databinding.FragmentCreateDocumentBinding
import com.example.example_qr_code.databinding.FragmentCreateWifiBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class CreatedWifiFragment : BaseFragment<FragmentCreateWifiBinding, CreateViewModel>() {

    val items = listOf("WPA/WPA2", "WEP", "No password")
    override val viewModel: CreateViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.fragment_create_wifi

    override fun setupView() {
        val adapter = ArrayAdapter(activityOwner, R.layout.layout_list_item, items)
        mBinding.dropdownMenu.setAdapter(adapter)

    }

    override fun listener() {


        mBinding.dropdownMenu.text
        Log.d("TAG", "listener: ${mBinding.dropdownMenu.text}")
        mBinding.dropdownMenu.setOnItemClickListener { adapterView, view, i, l ->
            Log.d("TAG", "listener: ${mBinding.dropdownMenu.text}")
        }

        mBinding.xToolBar.setToolbarClickListener(clickLeft = {
            onBackPressed()
        }, clickRight = {
            initQrCode()
        })
    }

    private fun initQrCode() {
        val netWorkName = mBinding.edtNameWifi.text.toString().trim()
        val password = mBinding.edtPassword.text.toString().trim()
        val typeWifi = mBinding.dropdownMenu.text.toString().trim()
        val stringBuilder = StringBuilder()
        stringBuilder.append("$netWorkName | $password | $typeWifi")
        val multiFormatWriter = MultiFormatWriter()
        try {
            val matrix =
                multiFormatWriter.encode(stringBuilder.toString(), BarcodeFormat.QR_CODE, 600, 600)
            val encode = BarcodeEncoder()
            val bitmap = encode.createBitmap(matrix)
            dataViewModel.setImageBitmap(bitmap)
            findNavController().navigate(R.id.action_createdWifiFragment_to_showQrCodeFragment)
        } catch (e: Exception) {

        }
    }

    override fun onBackPressed(): Boolean {
        findNavController().popBackStack()
        return true
    }
}