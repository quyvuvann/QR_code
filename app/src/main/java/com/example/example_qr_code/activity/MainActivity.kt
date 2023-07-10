package com.example.example_qr_code.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.example_qr_code.QrModel
import com.example.example_qr_code.QrRoomDatabase
import com.example.example_qr_code.R
import com.example.example_qr_code.adapter.NavigationAdapter
import com.example.example_qr_code.base.BaseActivity
import com.example.example_qr_code.base.NavigationViewModel
import com.example.example_qr_code.databinding.ActivityMainBinding
import com.example.example_qr_code.utils.permission.PermissionUtils
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var imageUri: Uri? = null

    private var barCodeScannerOptions: BarcodeScannerOptions? = null
    private var barCodeScanner: BarcodeScanner? = null
    private var navigationAdapter: NavigationAdapter? = null
    private var uri: Uri? = null

    override fun getLayoutId() = R.layout.activity_main

    override fun setUpView() {
        PermissionUtils.requestPermission()
        Glide.with(this).load(R.drawable.ic_qrcode).into(findViewById(R.id.img_viewQR))
        Glide.with(this).load(R.drawable.ic_qrcode).into(findViewById(R.id.img_nav))
        setUpNavigation()

        barCodeScannerOptions =
            BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()

        barCodeScanner = BarcodeScanning.getClient(barCodeScannerOptions!!)

    }

    override fun listener() {
        mBinding.btnCamera.setOnClickListener {
            if (PermissionUtils.checkCameraPermission(this)) {
                pickImageCamera()
            } else {
                PermissionUtils.requestCameraPermission(this)
            }
        }
        mBinding.btnGallery.setOnClickListener {
            if (PermissionUtils.checkStoragePermission(this)) {
                selectImageGallery()
            } else {
                PermissionUtils.requestStoragePermission(this)
            }
        }
        mBinding.btnScan.setOnClickListener {
            if (imageUri == null) {
                showToast("Pick image first")
            } else {
                detectResultFromImage()
            }
        }
        mBinding.toolBar.setNavigationOnClickListener {
            mBinding.drawerLayout.open()
        }
        navigationAdapter?.listener = object : NavigationAdapter.IListener {
            override fun onClickNav(item: NavigationViewModel.NavigationItem) {
                when (item.toolId) {
                    NavigationViewModel.Tool.QR_CODE ->{
//                        startActivity(Intent(this@MainActivity, MainActivity::class.java))
                        mBinding.drawerLayout.close()
                    }
                    NavigationViewModel.Tool.FAVORITE -> {
                        startActivity(Intent(this@MainActivity, FavoriteActivity::class.java))
                    }
                    NavigationViewModel.Tool.HISTORY -> {
                        startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
                    }
                    NavigationViewModel.Tool.MY_QR -> {
                        showToast("My QR")
                    }
                    NavigationViewModel.Tool.CREATED_QR -> {
                        startActivity(Intent(this@MainActivity, Create2Activity::class.java))
                    }
                    NavigationViewModel.Tool.SETTING -> {
                        showToast("Setting")
                    }
                    NavigationViewModel.Tool.SHARE -> {
                        showToast("Share")
                    }
                    else -> {}
                }
            }
        }
        mBinding.txtResult.setOnClickListener {
            if (mBinding.txtResult.text.isNotEmpty()) {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = uri
                startActivity(intent)
            }
        }
    }

    private fun setUpNavigation() {
        val listNav = viewModel.menuNav()
        navigationAdapter = NavigationAdapter()
        mBinding.rcvNav.adapter = navigationAdapter
        navigationAdapter!!.submitList(listNav.toMutableList())

        val toggle = ActionBarDrawerToggle(this, mBinding.drawerLayout, 0, 0)
        mBinding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

    }

    private fun detectResultFromImage() {
        try {
            val inputImage = InputImage.fromFilePath(this, imageUri!!)
            Log.d("TAG", "detectResultFromImagemain: $imageUri")
            Log.d("TAG", "detectResultFromImagemain: $inputImage")
            val barCodeResult =
                barCodeScanner!!.process(inputImage).addOnSuccessListener { barcodes ->
                    extractBarcodeQrCodeInfo(barcodes)
                }.addOnFailureListener { e ->
                    showToast("Failed scanning due to ${e.message}")
                }
        } catch (e: java.lang.Exception) {
            showToast("Failed due to ${e.message}")
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun extractBarcodeQrCodeInfo(barcodes: List<Barcode>) {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val strDate: String = sdf.format(c.time)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date: String = dateFormat.format(c.time)
//        val currentTime = Calendar.getInstance().calendarType
        for (barcode in barcodes) {
            val bound = barcode.boundingBox
            val corners = barcode.cornerPoints
            val rawValue = barcode.rawValue
            val valueType = barcode.valueType
            when (valueType) {
                Barcode.TYPE_WIFI -> {

                    val typeWifi = barcode.wifi
                    val ssid = "${typeWifi?.ssid}"
                    val password = "${typeWifi?.password}"
                    var encryptionType = "${typeWifi?.encryptionType}"
                    if (encryptionType == "1") {
                        encryptionType = "OPEN"
                    } else if (encryptionType == "2") {
                        encryptionType = "WPA"
                    } else if (encryptionType == "3") {
                        encryptionType = "WEP"
                    }
                    Log.d(
                        "TAG",
                        "extractBarcodeQrCodeInfo:  \"TYPE_WIFI \\nssid:$ssid \\npassword: $password \\nencryptionType: $encryptionType \\n\\nrawValue:$rawValue\""
                    )
                    mBinding.txtResult.text =
                        "TYPE_WIFI \nssid:$ssid \npassword: $password \nencryptionType: $encryptionType \n\nrawValue:$rawValue"
//                    lifecycleScope.launch {
//                        val daoQr = QrRoomDatabase.getDataBase(this@MainActivity).qrDao()
//                        daoQr.insertQr(
//                            QrModel(
//                                imageString = R.drawable.ic_wifi,
//                                titleTimeString = date,
//                                titleString = "Wifi",
//                                timeString = strDate,
//                                linkString = password
//
//                            )
//                        )
//                    }

                }
                Barcode.TYPE_URL -> {
                    val typeUrl = barcode.url
                    val title = "${typeUrl?.title}"
                    val url = "${typeUrl?.url}"
                    mBinding.txtResult.text =
                        "TYPE_URL \ntitle: $title \nurl: $url \n\nrawValue: $rawValue"
                    lifecycleScope.launch {
                        val daoQr = QrRoomDatabase.getDataBase(this@MainActivity).qrDao()
//                        daoQr.insertQr(
//                            QrModel(
//                                imageString = R.drawable.ic_link,
//                                titleTimeString = date,
//                                titleString = "Liên kết web",
//                                timeString = strDate,
//                                linkString = url
//                            )
//                        )
                    }
                    uri = Uri.parse(url)
                }
                Barcode.TYPE_EMAIL -> {
                    val typeEmail = barcode.email
                    val address = "${typeEmail?.address}"
                    val body = "${typeEmail?.body}"
                    val subject = "${typeEmail?.subject}"
                    mBinding.txtResult.text =
                        "TYPE_EMAIL \nEmail: $address \nbody: $body \nsubject: $subject \n\n"
                }
                Barcode.TYPE_CONTACT_INFO -> {
                    val typeContact = barcode.contactInfo

                    val title = "${typeContact?.title}"
                    val organization = "${typeContact?.organization}"
                    val name = "${typeContact?.name?.first} ${typeContact?.name?.last}"
                    val phone = "${typeContact?.name?.first} ${typeContact?.phones?.get(0)?.number}"
                    mBinding.txtResult.text =
                        "TYPE_CONTACT_INFO \ntitle: $title \norganization: $organization \nname: $name \nphone: $phone \n\nrawValue: $rawValue"
                }
                else -> {
                    mBinding.txtResult.text = "rawValue: $rawValue"
                }
            }
        }
    }


    private fun selectImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imageUri = data?.data
                Log.d("TAG", "image: $imageUri")
//                mBinding.imgViewQR.setImageURI(imageUri)
                Glide.with(this).load(imageUri).into(mBinding.imgViewQR)
            } else {
                showToast("Cancelled...")
            }
        }

    private fun pickImageCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Sample image")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Sample image description")
        imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private var cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data

                Log.d("TAG", " cameraActivityResultLauncher:$imageUri ")
//                mBinding.imgViewQR.setImageURI(imageUri)
                Glide.with(this).load(imageUri).into(mBinding.imgViewQR)
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted) {
                        pickImageCamera()
                    } else {
                        showToast("Camera & storage permission are required")
                    }
                }
            }

            STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (storageAccepted) {
                        selectImageGallery()
                    } else {
                        showToast("Storage permission is required...")
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    companion object {
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST_CODE = 101
    }




}