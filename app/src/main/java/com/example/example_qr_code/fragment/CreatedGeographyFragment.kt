package com.example.example_qr_code.fragment

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.example_qr_code.*
import com.example.example_qr_code.base.BaseFragment
import com.example.example_qr_code.databinding.FragmentCreateGeographyBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreatedGeographyFragment: BaseFragment<FragmentCreateGeographyBinding, CreateViewModel>() {
    override val viewModel: CreateViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.fragment_create_geography

    override fun setupView() {

    }

    override fun listener() {
        mBinding.xToolBar.setToolbarClickListener(clickLeft = {
            onBackPressed()
        }, clickRight = {
            initQrCode()
        })
    }
    @SuppressLint("SimpleDateFormat")
    private fun initQrCode() {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val strDate: String = sdf.format(c.time)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date: String = dateFormat.format(c.time)
        val latitude = mBinding.edtLatitude.text.toString().trim()
        val longitude = mBinding.edtLongitude.text.toString().trim()
        val query = mBinding.editQuery.text.toString().trim()

//        val geoUrl = "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude&query_place_id=$query"
        val data = "GEO:$latitude,$longitude"
        val bitmap = generateQRCode(data)
        dataViewModel.setImageBitmap(bitmap!!)
        dataViewModel.setDataQrcode(R.drawable.ic_person, R.string.contact)
        if (latitude.isEmpty() || longitude.isEmpty()) {
            Toast.makeText(activityOwner, "cannot be left blank", Toast.LENGTH_SHORT).show()
        } else {
            lifecycleScope.launch {
                val daoQr = QrRoomDatabase.getDataBase(activityOwner).qrDao()
                daoQr.insertQr(
                    QrModel(
                        imageString = R.drawable.ic_location,
                        imageBitmap = bitmapTOString(bitmap),
                        titleTimeString = date,
                        titleString = "Geography",
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
                        networkName = "",
                        password = "",
                        typeWifi = "",
                        latitude = latitude,
                        longitude = longitude,
                        query = query
                    )
                )
            }
            findNavController().navigate(R.id.action_createdGeographyFragment_to_showQrCodeFragment)
        }

    }

    override fun onBackPressed(): Boolean {
        findNavController().popBackStack()
        return true
    }


}