package com.example.example_qr_code.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Looper
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.example_qr_code.CreateViewModel
import com.example.example_qr_code.QrModel
import com.example.example_qr_code.QrRoomDatabase
import com.example.example_qr_code.R
import com.example.example_qr_code.base.BaseFragment
import com.example.example_qr_code.databinding.FragmentCreateWebBinding
import com.example.example_qr_code.databinding.FragmentSelectStyleBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Handler

class CreatedWebFragment : BaseFragment<FragmentCreateWebBinding, CreateViewModel>() {
    override val viewModel: CreateViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.fragment_create_web

    override fun setupView() {

    }

    @SuppressLint("SimpleDateFormat")
    override fun listener() {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val strDate: String = sdf.format(c.time)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date: String = dateFormat.format(c.time)
        mBinding.xToolBar.setToolbarClickListener(clickLeft = {
            onBackPressed()
        }, clickRight = {
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                val text = mBinding.editText.text.toString().trim()
                val writer = MultiFormatWriter()

                val matrix = writer.encode(text, BarcodeFormat.QR_CODE, 500, 500)
                val encode = BarcodeEncoder()
                val bitmap = encode.createBitmap(matrix)
                Log.d("TAG", "setupVieww: $bitmap")
                dataViewModel.setImageBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true))
                lifecycleScope.launch{
                    val daoQr = QrRoomDatabase.getDataBase(activityOwner).qrDao()
                    daoQr.insertQr(
                        QrModel(
                            titleTimeString = date,
                            titleString = "Wifi",
                            timeString = strDate,
                            linkString = text
                        )
                    )
                }
                dataViewModel.setDataQrcode(R.drawable.ic_link,R.string.lienKet)

                findNavController().navigate(R.id.action_createdWebFragment_to_showQrCodeFragment)
            }, 200)


        })
    }

    override fun onBackPressed(): Boolean {
        findNavController().popBackStack()
        return true

    }
}