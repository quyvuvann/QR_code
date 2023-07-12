package com.example.example_qr_code.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.example_qr_code.CreateViewModel
import com.example.example_qr_code.R
import com.example.example_qr_code.adapter.NavigationAdapter
import com.example.example_qr_code.base.BaseActivity
import com.example.example_qr_code.base.NavigationViewModel
import com.example.example_qr_code.createBitmap
import com.example.example_qr_code.databinding.ActivityShowQrBinding
import com.example.example_qr_code.fragment.SelectStyleFragment
import com.example.example_qr_code.model.DataActivityViewModel
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ShowQRActivity : BaseActivity<ActivityShowQrBinding>() {

    private var navigationAdapter = NavigationAdapter()
    private var barCodeScanner: BarcodeScanner? = null
    private var barCodeScannerOptions: BarcodeScannerOptions? = null
    val random = Random()
    val number = random.nextInt(1000000) + 1
    override fun getLayoutId(): Int = R.layout.activity_show_qr

    override fun setUpView() {

        Log.d("TAG", "setupView: ${dataActivityViewModel.getBitmap()}")
        val bitmapReceiver = dataActivityViewModel.bitmapQr?.copy(Bitmap.Config.ARGB_8888, true)
        Glide.with(this).load(bitmapReceiver).into(mBinding.imgQrcode)
        Glide.with(this).load(R.drawable.ic_qrcode).into(mBinding.imgNav)
        barCodeScannerOptions =
            BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()

        barCodeScanner = BarcodeScanning.getClient(barCodeScannerOptions!!)
        setUpNavigation()
        mBinding.btnFavorite.setOnClickListener {
            detectResultFromImage()
        }
        setStatusBarTransparent(this,mBinding.root)
//        Glide.with(activityOwner).load(dataViewModel.image).override(48).into(mBinding.imgView)
//        mBinding.txtTitle.setText(dataViewModel.titleCr!!)

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

    override fun listener() {

        mBinding.btnSave.setOnClickListener {
            saveImage(createBitmap(mBinding.cardView, this), number.toString())
        }
        mBinding.btnShare.setOnClickListener {
            shareImage(createBitmap(mBinding.cardView, this))
        }
        Handler(Looper.getMainLooper()).postDelayed({
            detectResultFromImage()
        }, 300)
        navigationAdapter.listener = object : NavigationAdapter.IListener {
            override fun onClickNav(item: NavigationViewModel.NavigationItem) {
                when (item.toolId) {
                    NavigationViewModel.Tool.QR_CODE -> {
                        startActivity(Intent(this@ShowQRActivity, MainActivity::class.java))
                    }
                    NavigationViewModel.Tool.FAVORITE -> {
                        startActivity(Intent(this@ShowQRActivity, FavoriteActivity::class.java))
                    }
                    NavigationViewModel.Tool.HISTORY -> {
                        startActivity(Intent(this@ShowQRActivity, HistoryActivity::class.java))
                    }
                    NavigationViewModel.Tool.MY_QR -> {
                        startActivity(Intent(this@ShowQRActivity, MyQRActivity::class.java))
                    }
                    NavigationViewModel.Tool.CREATED_QR -> {
                        startActivity(Intent(this@ShowQRActivity, SelectStyleFragment::class.java))
                    }
                    NavigationViewModel.Tool.SETTING -> {

                    }
                    NavigationViewModel.Tool.SHARE -> {

                    }
                    else -> {}
                }
            }
        }
    }

    private fun saveImage(bitmap: Bitmap, title: String) {
        val filename = "$title.png"
        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val imageFile = File(directory, filename)
        val outputStream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(imageFile)
        mediaScanIntent.data = contentUri
//        dataViewModel.setUriReceiver(contentUri)
        Log.d("TAG", "saveImage: $contentUri")
        sendBroadcast(mediaScanIntent)

    }


    private fun shareImage(bitmap: Bitmap) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"
        val cachePath = File(externalCacheDir, "shared_image.jpg")
        val outputStream = FileOutputStream(cachePath)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        val imageUri =
            FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                cachePath
            )
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ hình ảnh qua"))
    }

    private fun detectResultFromImage() {
        try {

            val inputImage = InputImage.fromFilePath(
                this,
                getImageUri(
                    this,
                    createBitmap(mBinding.cardView, this),
                    number.toString()
                )!!
            )
            val barCodeResult =
                barCodeScanner!!.process(inputImage).addOnSuccessListener { barcodes ->
                    extractBarcodeQrCodeInfo(barcodes)
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "errorListener", Toast.LENGTH_SHORT).show()
                }
        } catch (e: java.lang.Exception) {
            Log.e("TAG", "detectResultFromImage: ${e.message}")
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun extractBarcodeQrCodeInfo(barcodes: List<Barcode>) {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val strDate: String = sdf.format(c.time)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date: String = dateFormat.format(c.time)
        for (barcode in barcodes) {
            val bound = barcode.boundingBox
            val corners = barcode.cornerPoints
            val rawValue = barcode.rawValue
            val valueType = barcode.valueType
            when (valueType) {
                Barcode.TYPE_WIFI -> {

                    val typeWifi = barcode.wifi
                    val ssid = typeWifi?.ssid
                    val password = typeWifi?.password!!
                    var encryptionType = typeWifi.encryptionType
                    encryptionType = when (encryptionType) {
                        1 -> R.string.open
                        2 -> R.string.wpa
                        3 -> R.string.wep
                        else -> R.string.un_know
                    }
                    Log.d(
                        "TAG",
                        "extractBarcodeQrCodeInfo:  \"TYPE_WIFI \\nssid:$ssid \\npassword: $password \\nencryptionType: $encryptionType "
                    )
                    mBinding.txtResult.text =
                        "TYPE_WIFI \nssid:$ssid \npassword: $password \nencryptionType: $encryptionType"


                }
                Barcode.TYPE_URL -> {

                    val url = barcode.url

                    val title = url?.title
                    val urlValue = url?.url

                    mBinding.txtResult.text =
                        "TYPE_URL \ntitle: $title \nurl: $urlValue \n"


//                    uri = Uri.parse(url)
                }
                Barcode.TYPE_EMAIL -> {
                    val typeEmail = barcode.email
                    val address = "${typeEmail?.address}"
                    val body = "${typeEmail?.body}"
                    val subject = "${typeEmail?.subject}"
                    mBinding.txtResult.text =
                        "TYPE_EMAIL \nEmail: $address \nbody: $body \nsubject: $subject \n"
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
                        "TYPE_CONTACT_INFO \nfullName: $fullName \nworkPlace: $workPlace \n" +
                                "Address: ${address} \n" +
                                "Phone: $phoneNumber \n" +
                                "Email: $email \n" +
                                "Note: $note"
                }
                Barcode.TYPE_SMS -> {
                    val typeContact = barcode.sms
                    val phone = typeContact?.phoneNumber
                    val mess = typeContact?.message
                    mBinding.txtResult.text =
                        "TYPE_SMS \nphone: $phone \nmess: $mess"
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
                        "TYPE_GEO \n Latitude: $latitude \n " + "Longitude: $longitude"
                }

                else -> {
                    mBinding.txtResult.text = "rawValue: $rawValue"
                    Log.d("TAG", "extractBarcodeQrCodeInfo: ${rawValue}")
                }
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
//        mBinding.navView.getHeaderView(0)
        toggle.syncState()
//        val menuItem = mBinding.navView.menu.findItem(NavigationViewModel.Tool.CREATED_QR.ordinal)
//        menuItem.isChecked = true

    }

    fun getImageUri(inContext: Context, inImage: Bitmap, title: String): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, title, null)
        return Uri.parse(path)
    }


}