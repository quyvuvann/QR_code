package com.example.example_qr_code.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.example_qr_code.*
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
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var imageUri: Uri? = null

    private var barCodeScannerOptions: BarcodeScannerOptions? = null
    private var barCodeScanner: BarcodeScanner? = null
    private var navigationAdapter: NavigationAdapter? = null
    private var uri: Uri? = null
    private var uriReceiverCustom: Uri? = null
    private var uriQr: Uri? = null
    private val random = Random()
    val number = random.nextInt(1000000) + 1


    override fun getLayoutId() = R.layout.activity_main

    override fun setUpView() {
        PermissionUtils.requestPermission()
        PermissionUtils.requestCameraPermission(this)
//        if (PermissionUtils.checkCameraPermission(this)) {
//            scanCode()
//        } else {
//            PermissionUtils.requestCameraPermission(this)
//        }
        Glide.with(this).load(R.drawable.ic_qrcode).into(findViewById(R.id.img_viewQR))
        Glide.with(this).load(R.drawable.ic_qrcode).into(findViewById(R.id.img_nav))
        setUpNavigation()

        barCodeScannerOptions =
            BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()

        barCodeScanner = BarcodeScanning.getClient(barCodeScannerOptions!!)
        uriReceiverCustom = intent.getParcelableExtra<Uri>("uriKey")
        if (uriReceiverCustom != null) {
            imageUri = uriReceiverCustom
            Glide.with(this).load(imageUri).into(mBinding.imgViewQR)
            Log.d("TAG", "setUpView: $uriReceiverCustom")
            detectResultFromImage()
        }
    }

    override fun listener() {
        mBinding.btnCamera.setOnClickListener {
            if (PermissionUtils.checkCameraPermission(this)) {
                scanCode()
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
                    NavigationViewModel.Tool.QR_CODE -> {
//                        startActivity(Intent(this@MainActivity, MainActivity::class.java))
                        mBinding.drawerLayout.close()
                    }
                    NavigationViewModel.Tool.FAVORITE -> {
                        startActivity(Intent(this@MainActivity, FavoriteActivity::class.java))
                        finish()
                    }
                    NavigationViewModel.Tool.HISTORY -> {
                        startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
                        finish()
                    }
                    NavigationViewModel.Tool.MY_QR -> {
                        lifecycleScope.launch {
                            val myDao = QrRoomDatabase.getDataBase(this@MainActivity).qrMyDao()
                            if (myDao.getAllMyQr().size > 0) {
                                startActivity(
                                    Intent(this@MainActivity, ShowQRActivity::class.java)
                                )
                                finish()
                            } else {
                                startActivity(Intent(this@MainActivity, MyQRActivity::class.java))
                                finish()
                            }
                        }

                    }
                    NavigationViewModel.Tool.CREATED_QR -> {
                        startActivity(Intent(this@MainActivity, Create2Activity::class.java))
                        finish()
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

        for (barcode in barcodes) {

            val rawValue = barcode.rawValue
            when (barcode.valueType) {
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
                    mBinding.txtResult.text =
                        "TYPE_WIFI \nssid:$ssid \npassword: $password \nencryptionType: $encryptionType\n"

                }
                Barcode.TYPE_URL -> {
                    val typeUrl = barcode.url
                    val title = "${typeUrl?.title}"
                    val url = "${typeUrl?.url}"
                    mBinding.txtResult.text =
                        "TYPE_URL \ntitle: $title \nurl: $url \n"

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
                    val fullName = typeContact?.name?.formattedName
                    val workPlace = typeContact?.organization
                    val address = typeContact?.addresses?.joinToString("\n") { address ->
                        address.addressLines.joinToString(", ")
                    }

                    val phoneNumber = typeContact?.phones?.firstOrNull()?.number
                    val email = typeContact?.emails?.map { email ->
                        email.address
                    }?.joinToString("\n")
                    val note = typeContact?.title
                    mBinding.txtResult.text =
                        "TYPE_CONTACT_INFO \nfullName: $fullName \nworkPlace: $workPlace \n" + "Address: $address \n" + "Phone: $phoneNumber \n" + "Email: $email \n" + "Note: $note"
                }
                Barcode.TYPE_SMS -> {
                    val typeContact = barcode.sms
                    val phone = typeContact?.phoneNumber
                    val mess = typeContact?.message
                    mBinding.txtResult.text = "TYPE_SMS \nphone: $phone \nmess: $mess"
                }
                Barcode.TYPE_PHONE -> {
                    val typeContact = barcode.phone
                    val phone = typeContact?.number
                    mBinding.txtResult.text = "TYPE_PHONE \nPhone number: $phone"
                }
                Barcode.TYPE_GEO -> {
                    val typeContact = barcode.geoPoint
                    val latitude = typeContact?.lat
                    val longitude = typeContact?.lng
                    mBinding.txtResult.text =
                        "TYPE_GEO \n Latitude: $latitude \n Longitude: $longitude"
                }
                else -> {
                    mBinding.txtResult.text = "rawValue: $rawValue"
                }
            }
        }
    }


    fun selectImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                uriReceiverCustom = null
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
//        scanCode()

//        cameraActivityResultLauncher.launch(intent)
    }

    private var cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                uriReceiverCustom = null
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

    private fun scanCode() {
        val options = ScanOptions().setOrientationLocked(false).setCaptureActivity(
            CustomScannerActivity::class.java
        )
        barcodeLauncher.launch(options)
    }

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            val originalIntent = result.originalIntent
            if (originalIntent == null) {
                Log.d("MainActivity", "Cancelled scan")
                Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_LONG).show()
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Log.d(
                    "MainActivity",
                    "Cancelled scan due to missing camera permission"
                )
                Toast.makeText(
                    this@MainActivity,
                    "Cancelled due to missing camera permission",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Log.d("MainActivity", "Scanned")
            Toast.makeText(
                this@MainActivity,
                "Scanned: " + result.contents,
                Toast.LENGTH_LONG
            ).show()
            Log.d(
                "TAG",
                ": barcodeImagePath :${result.barcodeImagePath},orientation :${result.orientation},originalIntent:${result.originalIntent},contents: ${result.contents},formatName: ${result.formatName},rawBytes:${result.rawBytes}"
            )
            val barcodeImage =
                BitmapFactory.decodeByteArray(result.rawBytes, 0, result.rawBytes.size)
            Log.d("TAG", "barcodeImage: $barcodeImage")
            mBinding.txtResult.text = result.contents
            imageUri = getImageUri(this, generateQRCode(result.contents)!!, number.toString())
            Glide.with(this).load(generateQRCode(result.contents)).into(mBinding.imgViewQR)
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