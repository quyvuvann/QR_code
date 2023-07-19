package com.example.example_qr_code.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.example_qr_code.*
import com.example.example_qr_code.adapter.NavigationAdapter
import com.example.example_qr_code.base.BaseActivity
import com.example.example_qr_code.base.NavigationViewModel
import com.example.example_qr_code.databinding.ActivityMyQrBinding
import com.example.example_qr_code.fragment.SelectStyleFragment
import com.example.example_qr_code.model.DataActivityViewModel
import com.example.example_qr_code.model.MyQrModel

import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class MyQRActivity : BaseActivity<ActivityMyQrBinding>() {
    private var navigationAdapter = NavigationAdapter()

    override fun getLayoutId(): Int = R.layout.activity_my_qr
    override fun setUpView() {

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setStatusBarTransparent(this, mBinding.root)
        mBinding.edtFullname.requestLayout()
        mBinding.edtWorkplace.requestLayout()
        mBinding.edtAddress.requestLayout()
        mBinding.edtPhoneNumber.requestLayout()
        mBinding.edtEmail.requestLayout()
        mBinding.edtNote.requestLayout()

    }

    override fun listener() {
        setUpNavigation()
        navigationAdapter.listener = object : NavigationAdapter.IListener {
            override fun onClickNav(item: NavigationViewModel.NavigationItem) {
                when (item.toolId) {
                    NavigationViewModel.Tool.QR_CODE -> {
                        startActivity(Intent(this@MyQRActivity, MainActivity::class.java))
                    }
                    NavigationViewModel.Tool.FAVORITE -> {
                        startActivity(Intent(this@MyQRActivity, FavoriteActivity::class.java))
                    }
                    NavigationViewModel.Tool.HISTORY -> {
                        startActivity(Intent(this@MyQRActivity, HistoryActivity::class.java))
                    }
                    NavigationViewModel.Tool.MY_QR -> {

                    }
                    NavigationViewModel.Tool.CREATED_QR -> {
                        startActivity(Intent(this@MyQRActivity, SelectStyleFragment::class.java))
                    }
                    NavigationViewModel.Tool.SETTING -> {

                    }
                    NavigationViewModel.Tool.SHARE -> {

                    }
                    else -> {}
                }
            }

        }
        mBinding.xToolBar.setToolbarClickListener(clickLeft = {
            mBinding.drawerLayout.open()
        }, clickRight = {
            initQrCode()
        })
    }

    private fun setUpNavigation() {
        val listNav = viewModel.menuNav()
        mBinding.rcvNav.adapter = navigationAdapter
        navigationAdapter.submitList(listNav.toMutableList())
        val toggle = ActionBarDrawerToggle(this, mBinding.drawerLayout, 0, 0)
        mBinding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setStatusBarTransparent(activity: Activity, view: View) {
        activity.apply {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
            WindowCompat.setDecorFitsSystemWindows(window, false)
            ViewCompat.setOnApplyWindowInsetsListener(view) { root, windowInset ->
                val inset = windowInset.getInsets(WindowInsetsCompat.Type.systemBars())
                root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    leftMargin = inset.left
                    bottomMargin = inset.bottom
                    rightMargin = inset.right
                    topMargin = 0
                }
                WindowInsetsCompat.CONSUMED
            }
        }
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

        if (fullName.isEmpty() || workplace.isEmpty() || address.isEmpty()
            || phoneNumber.isEmpty() || email.isEmpty() || note.isEmpty()
        ) {
            Toast.makeText(this, "cannot be left blank", Toast.LENGTH_SHORT).show()
        } else {
            lifecycleScope.launch {
                val daoQr = QrRoomDatabase.getDataBase(this@MyQRActivity).qrDao()
                val id  = daoQr.insertQr(
                    QrModel(
                        imageString = R.drawable.ic_person,
                        imageBitmap = bitmapTOString(bitmap!!),
                        titleTimeString = date,
                        titleString = "Contact",
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
                val myDao = QrRoomDatabase.getDataBase(this@MyQRActivity).qrMyDao()
//                myDao.insertMyQr(MyQrModel(isCheck = true, imageBitmap = bitmapTOString(bitmap!!)))
                myDao.insertMyQr(MyQrModel(imageBitmap = bitmapTOString(bitmap), titleString = "My QR", idItem = id.toInt()))
                val intent = Intent(this@MyQRActivity, ShowQRActivity::class.java)
                intent.putExtra("bitmapKey", bitmap)
                Log.d("TAG", "initQrCode: $id")
                intent.putExtra("idKey", id.toInt())
                startActivity(intent)
            }
            lifecycleScope.launch {

//                val intent = Intent(this@MyQRActivity, ShowQRActivity::class.java)
//                intent.putExtra("bitmapKey", bitmap)
//                Log.d("TAG", "initQrCode: $id")
//                intent.putExtra("idKey", id.toInt())
//                startActivity(intent)
            }


        }

    }


    private fun encodeVCardValue(value: String): String {
        return value.replace("\n", "\\n")
            .replace(";", "\\;")
            .replace(":", "\\:")
            .replace(",", "\\,")
            .replace("\\", "\\\\")
    }


}