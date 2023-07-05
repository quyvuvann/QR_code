package com.example.example_qr_code.fragment

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.example_qr_code.*
import com.example.example_qr_code.base.BaseFragment
import com.example.example_qr_code.databinding.FragmentCreateContactBinding
import com.example.example_qr_code.databinding.FragmentCreateDocumentBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class CreatedContactFragment : BaseFragment<FragmentCreateContactBinding, CreateViewModel>() {
    override val viewModel: CreateViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.fragment_create_contact

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
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val strDate: String = sdf.format(c.time)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date: String = dateFormat.format(c.time)
        val fullName = mBinding.edtFullname.text.toString().trim()
        val workplace = mBinding.edtWorkplace.text.toString().trim()
        val address = mBinding.edtAddress.text.toString().trim()
        val phoneNumber = mBinding.edtPhoneNumber.text.toString().trim()
        val email = mBinding.edtEmail.text.toString().trim()
        val note = mBinding.edtNote.text.toString().trim()

        val data = "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "N;CHARSET=UTF-8:${encodeVCardValue(fullName)}\n" +
                "ORG;CHARSET=UTF-8:${encodeVCardValue(workplace)}\n" +
                "ADR;CHARSET=UTF-8:${encodeVCardValue(address)}\n" +
                "TEL;TYPE=CELL:${encodeVCardValue(phoneNumber)}\n" +
                "EMAIL:${encodeVCardValue(email)}\n" +
                "TITLE;CHARSET=UTF-8:${encodeVCardValue(note)}\n" +
                "END:VCARD"
        val bitmap = generateQRCode(data)
        dataViewModel.setImageBitmap(bitmap!!)
        dataViewModel.setDataQrcode(R.drawable.ic_person, R.string.contact)
        if (fullName.isEmpty() || workplace.isEmpty() || address.isEmpty()
            || phoneNumber.isEmpty() || email.isEmpty() || note.isEmpty()
        ) {
            Toast.makeText(activityOwner, "cannot be left blank", Toast.LENGTH_SHORT).show()
        } else {
            lifecycleScope.launch {
                val daoQr = QrRoomDatabase.getDataBase(activityOwner).qrDao()
                daoQr.insertQr(
                    QrModel(
                        imageString = R.drawable.ic_sms,
                        imageBitmap = bitmapTOString(bitmap),
                        titleTimeString = date,
                        titleString = "SMS",
                        timeString = strDate,
                        linkString = "",
                        phone = phoneNumber,
                        message = "",
                        email = email,
                        topic = "",
                        content = "",
                        document = "",
                        fullName = fullName,
                        workPlace = workplace,
                        address = address,
                        note = note,
                        networkName = "",
                        password = "",
                        typeWifi = "",
                        latitude = "",
                        longitude = "",
                        query = ""
                    )
                )
            }
            findNavController().navigate(R.id.action_createdContactFragment_to_showQrCodeFragment)
        }

    }

    override fun onBackPressed(): Boolean {
        findNavController().popBackStack()
        return true
    }

    private fun encodeVCardValue(value: String): String {
        return value.replace("\n", "\\n")
            .replace(";", "\\;")
            .replace(":", "\\:")
            .replace(",", "\\,")
            .replace("\\", "\\\\")
    }
}