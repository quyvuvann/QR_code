package com.example.example_qr_code.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.example_qr_code.CreateViewModel
import com.example.example_qr_code.QrModel
import com.example.example_qr_code.QrRoomDatabase
import com.example.example_qr_code.R
import com.example.example_qr_code.adapter.NavigationAdapter
import com.example.example_qr_code.base.BaseFragment
import com.example.example_qr_code.base.NavigationViewModel
import com.example.example_qr_code.databinding.FragmentShowQrcodeBinding
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class ShowQrCodeFragment : BaseFragment<FragmentShowQrcodeBinding, CreateViewModel>() {
    override val viewModel: CreateViewModel by viewModels()
    private val navigationModel by activityViewModels<NavigationViewModel>()
    private var navigationAdapter = NavigationAdapter()
    private var barCodeScanner: BarcodeScanner? = null
    private var barCodeScannerOptions: BarcodeScannerOptions? = null
    override fun getLayoutId(): Int = R.layout.fragment_show_qrcode
    val random = Random()
    val number = random.nextInt(1000000) + 1

    override fun setupView() {

        val bitmapReceiver = dataViewModel.bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        Log.d("TAG", "setupView: ${viewModel.bitmap}")
        Log.d("TAG", "setupView: ${bitmapReceiver}")
        Glide.with(activityOwner).load(bitmapReceiver).into(mBinding.imgQrcode)
        Glide.with(this).load(R.drawable.ic_qrcode).into(mBinding.imgNav)
        barCodeScannerOptions =
            BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()

        barCodeScanner = BarcodeScanning.getClient(barCodeScannerOptions!!)
        setUpNavigation()
        mBinding.btnFavorite.setOnClickListener {
            detectResultFromImage()
        }


    }

    override fun listener() {

        mBinding.btnSave.setOnClickListener {
            saveImage(createBitmap(mBinding.cardView, activityOwner), number.toString())
        }
        mBinding.btnShare.setOnClickListener {
            shareImage()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            detectResultFromImage()
        }, 300)

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
        dataViewModel.setUriReceiver(contentUri)
        Log.d("TAG", "saveImage: $contentUri")
        activityOwner.sendBroadcast(mediaScanIntent)

    }

    fun createBitmap(view: View, context: Context): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        val file = File.createTempFile("temp", ".png", context.cacheDir)
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
        return bitmap
    }

    private fun shareImage() {
        val file = File(Uri.parse(dataViewModel.uri.toString()).path!!)
        val shareIntent = Intent(Intent.ACTION_SEND)
        Log.d("TAG,", "shareImage: $file")
        shareIntent.type = "image/*"
        val imageUri =
            file.let {
                FileProvider.getUriForFile(
                    activityOwner, activityOwner.packageName + ".provider",
                    it
                )
            }
        Log.d("TAG", "shareImage: $imageUri")
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(shareIntent, "Share using"))
    }

    private fun detectResultFromImage() {
        try {

            val inputImage = InputImage.fromFilePath(
                activityOwner,
                getImageUri(requireContext(), createBitmap(mBinding.cardView, activityOwner),number.toString())!!
            )
            val barCodeResult = barCodeScanner!!.process(inputImage).addOnSuccessListener { barcodes ->
                extractBarcodeQrCodeInfo(barcodes)
            }.addOnFailureListener { e ->
                Toast.makeText(activityOwner, "errorListener", Toast.LENGTH_SHORT).show()
            }
        } catch (e: java.lang.Exception) {
            Log.e("TAG", "detectResultFromImage: ${e.message}")
            Toast.makeText(activityOwner, "error", Toast.LENGTH_SHORT).show()
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
                    lifecycleScope.launch {
                        val daoQr = QrRoomDatabase.getDataBase(activityOwner).qrDao()
                        daoQr.insertQr(
                            QrModel(
                                titleTimeString = date,
                                titleString = "Wifi",
                                timeString = strDate,
                                linkString = password
                            )
                        )
                    }

                }
                Barcode.TYPE_URL -> {

                    val typeUrl = barcode.url
                    val title = "${typeUrl?.title}"
                    val url = "${typeUrl?.url}"
                    mBinding.txtResult.text =
                        "TYPE_URL \ntitle: $title \nurl: $url \n\nrawValue: $rawValue"
                    Log.d(
                        "TAG",
                        "extractBarcodeQrCodeURL:  \"TYPE_URL \\ntypeUrl:$typeUrl \\ntitle: $title \\nurl: $url"
                    )
                    lifecycleScope.launch {
                        val daoQr = QrRoomDatabase.getDataBase(activityOwner).qrDao()
                        daoQr.insertQr(
                            QrModel(
                                titleTimeString = date,
                                titleString = "Liên kết web",
                                timeString = strDate,
                                linkString = url
                            )
                        )
                    }
//                    uri = Uri.parse(url)
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
                    Log.d("TAG", "extractBarcodeQrCodeInfo: ${rawValue}")
                }
            }
        }
    }

    private fun setUpNavigation() {
        val listNav = navigationModel.menuNav()
        navigationAdapter = NavigationAdapter()
        mBinding.rcvNav.adapter = navigationAdapter
        navigationAdapter!!.submitList(listNav.toMutableList())

        val toggle = ActionBarDrawerToggle(activityOwner, mBinding.drawerLayout, 0, 0)
        mBinding.drawerLayout.addDrawerListener(toggle)
//        mBinding.navView.getHeaderView(0)
        toggle.syncState()

    }

    fun getImageUri(inContext: Context, inImage: Bitmap,title: String): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, title, null)
        return Uri.parse(path)
    }

}