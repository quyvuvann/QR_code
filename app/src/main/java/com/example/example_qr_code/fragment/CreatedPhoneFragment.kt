package com.example.example_qr_code.fragment

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.example_qr_code.*
import com.example.example_qr_code.base.BaseFragment
import com.example.example_qr_code.databinding.FragmentCreateDocumentBinding
import com.example.example_qr_code.databinding.FragmentCreatePhoneBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreatedPhoneFragment : BaseFragment<FragmentCreatePhoneBinding, CreateViewModel>() {
    override val viewModel: CreateViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.fragment_create_phone

    override fun setupView() {

    }

    override fun listener() {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val strDate: String = sdf.format(c.time)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date: String = dateFormat.format(c.time)
        mBinding.xToolBar.setToolbarClickListener(
            clickRight = {
                val phone = mBinding.edtPhoneNumber.text.toString().trim()
                val data = "tel:$phone"
                if (phone.isEmpty()) {
                    Toast.makeText(activityOwner, "cannot be left blank", Toast.LENGTH_SHORT).show()
                } else {

                    val bitmap = generateQRCode(data)
                    lifecycleScope.launch {
                        val daoQr = QrRoomDatabase.getDataBase(activityOwner).qrDao()
                        daoQr.insertQr(
                            QrModel(
                                imageString = R.drawable.ic_phone,
                                imageBitmap = bitmapTOString(bitmap!!),
                                titleTimeString = date,
                                titleString = "Phone",
                                timeString = strDate,
                                linkString = "" ,
                                phone = phone,
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
                                latitude = "",
                                longitude = "",
                                query = ""
                            )
                        )
                    }
                    Log.d("TAG", "setupVieww: $bitmap")
                    dataViewModel.setImageBitmap(bitmap!!.copy(Bitmap.Config.ARGB_8888, true))
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