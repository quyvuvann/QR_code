package com.example.example_qr_code.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.example_qr_code.CreateViewModel
import com.example.example_qr_code.R
import com.example.example_qr_code.base.BaseFragment
import com.example.example_qr_code.databinding.FragmentCreateContactBinding
import com.example.example_qr_code.databinding.FragmentCreateDocumentBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class CreatedContactFragment : BaseFragment<FragmentCreateContactBinding,CreateViewModel>() {
    override val viewModel: CreateViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.fragment_create_contact

    override fun setupView() {

    }

    override fun listener() {
        mBinding.xToolBar.setToolbarClickListener(clickLeft ={
            onBackPressed()
        }, clickRight = {
            initQrCode()
        })
    }
    private fun initQrCode(){
        val fullName = "Name: "+mBinding.edtFullname.text.toString().trim()
        val workplace = "Workplace: "+mBinding.edtWorkplace.text.toString().trim()
        val address = "Address: "+mBinding.edtAddress.text.toString().trim()
        val phoneNumber = "Phone number: "+mBinding.edtPhoneNumber.text.toString().trim()
        val email = "Email: "+mBinding.edtEmail.text.toString().trim()
        val note = "Note: "+mBinding.edtNote.text.toString().trim()
        val list = mutableListOf<String>()
        list.add(fullName)
        list.add(workplace)
        list.add(address)
        list.add(phoneNumber)
        list.add(email)
        list.add(note)
        val stringBuilder = StringBuilder()
        for (line in list){
            stringBuilder.append(line).append("\n")
        }
//
        val multiFormatWriter = MultiFormatWriter()
        try {
            val matrix = multiFormatWriter.encode(stringBuilder.toString(), BarcodeFormat.QR_CODE, 600, 600)
            val encode = BarcodeEncoder()
            val bitmap = encode.createBitmap(matrix)
            dataViewModel.setImageBitmap(bitmap)
            findNavController().navigate(R.id.action_createdContactFragment_to_showQrCodeFragment)
        }catch (e:Exception){

        }
    }
    override fun onBackPressed(): Boolean {
        findNavController().popBackStack()
        return true
    }
}