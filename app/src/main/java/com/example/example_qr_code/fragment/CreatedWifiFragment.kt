package com.example.example_qr_code.fragment

import android.text.InputType
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.example_qr_code.*
import com.example.example_qr_code.base.BaseFragment
import com.example.example_qr_code.databinding.FragmentCreateDocumentBinding
import com.example.example_qr_code.databinding.FragmentCreateWifiBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreatedWifiFragment : BaseFragment<FragmentCreateWifiBinding, CreateViewModel>() {

    val items = listOf("WPA/WPA2", "WEP", "No password")
    override val viewModel: CreateViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.fragment_create_wifi

    override fun setupView() {
        val adapter = ArrayAdapter(activityOwner, R.layout.layout_list_item, items)
        mBinding.dropdownMenu.setAdapter(adapter)

    }

    override fun listener() {

        Log.d("TAG", "listener: ${mBinding.dropdownMenu.text}")
        mBinding.dropdownMenu.setOnItemClickListener { adapterView, view, i, l ->
            Log.d("TAG", "listener: ${mBinding.dropdownMenu.text}")
        }
        mBinding.chkHint.setOnCheckedChangeListener { compoundButton, b ->
            if (!b) {
                mBinding.edtPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                mBinding.edtPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            mBinding.edtPassword.setSelection(mBinding.edtPassword.text.length)
        }
        mBinding.xToolBar.setToolbarClickListener(clickLeft = {
            onBackPressed()
        }, clickRight = {
            initQrCode()
        })
    }

    private fun initQrCode() {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val strDate: String = sdf.format(c.time)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date: String = dateFormat.format(c.time)
        val netWorkName = mBinding.edtNameWifi.text.toString().trim()
        val password = mBinding.edtPassword.text.toString().trim()
        val typeWifi = mBinding.dropdownMenu.text.toString().trim()
        val data = "WIFI:S:$netWorkName;T:$typeWifi;P:$password"
        val bitmap = generateQRCode(data)
        dataViewModel.setImageBitmap(bitmap!!)
        dataViewModel.setDataQrcode(R.drawable.ic_wifi, R.string.wifi)
        lifecycleScope.launch {
            val daoQr = QrRoomDatabase.getDataBase(activityOwner).qrDao()
            daoQr.insertQr(
                QrModel(
                    imageString = R.drawable.ic_wifi,
                    imageBitmap = bitmapTOString(bitmap),
                    titleTimeString = date,
                    titleString = "Wifi",
                    timeString = strDate,
                    linkString = "",
                    phone = "",
                    message = "",
                    email = "",
                    topic = "",
                    content = "",
                    document = "",
                    fullName = "",
                    workPlace = "",
                    address = "",
                    note = "",
                    networkName = "netWorkName",
                    password = password,
                    typeWifi = typeWifi,
                    latitude = "",
                    longitude = "",
                    query = ""
                )
            )
        }
        if (netWorkName.isEmpty() || typeWifi.isEmpty()) {
            Toast.makeText(activityOwner, "cannot be left blank", Toast.LENGTH_SHORT).show()
        } else {
            findNavController().navigate(R.id.action_createdWifiFragment_to_showQrCodeFragment)
        }

    }

    override fun onBackPressed(): Boolean {
        findNavController().popBackStack()
        return true
    }
}