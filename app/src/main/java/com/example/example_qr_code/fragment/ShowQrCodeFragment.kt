package com.example.example_qr_code.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.example_qr_code.*
import com.example.example_qr_code.activity.*
import com.example.example_qr_code.adapter.NavigationAdapter
import com.example.example_qr_code.base.BaseFragment
import com.example.example_qr_code.base.NavigationViewModel
import com.example.example_qr_code.databinding.FragmentShowQrcodeBinding
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*


class ShowQrCodeFragment : BaseFragment<FragmentShowQrcodeBinding, CreateViewModel>() {
    override val viewModel: CreateViewModel by viewModels()
    private val navigationModel by activityViewModels<NavigationViewModel>()
    private var navigationAdapter = NavigationAdapter()
    private var barCodeScanner: BarcodeScanner? = null
    private var barCodeScannerOptions: BarcodeScannerOptions? = null
    private var barcodeDefault: Barcode? = null
    private var isFavorite = false
    override fun getLayoutId(): Int = R.layout.fragment_show_qrcode
    private val random = Random()
    val number = random.nextInt(1000000) + 1

    override fun setupView() {
        val bitmapReceiver = dataViewModel.bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        Glide.with(activityOwner).load(bitmapReceiver).into(mBinding.imgQrcode)
        Glide.with(this).load(R.drawable.ic_qrcode).into(mBinding.imgNav)
        barCodeScannerOptions =
            BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()

        barCodeScanner = BarcodeScanning.getClient(barCodeScannerOptions!!)
        setUpNavigation()
        mBinding.btnFavorite.setOnClickListener {
            detectResultFromImage()
        }
        Glide.with(activityOwner).load(dataViewModel.image).override(48).into(mBinding.imgView)
        mBinding.txtTitle.setText(dataViewModel.titleCr!!)

    }

    override fun listener() {

        mBinding.btnSave.setOnClickListener {
            saveImage(createBitmap(mBinding.cardView, activityOwner), number.toString())
        }
        mBinding.btnShare.setOnClickListener {
            shareImage(createBitmap(mBinding.cardView, activityOwner))
        }
        mBinding.xToolBar.setToolbarClickListener(clickLeft = {
            mBinding.drawerLayout.open()
        }, clickRight = {})
        Handler(Looper.getMainLooper()).postDelayed({
            detectResultFromImage()
        }, 300)
        navigationAdapter.listener = object : NavigationAdapter.IListener {
            override fun onClickNav(item: NavigationViewModel.NavigationItem) {
                when (item.toolId) {
                    NavigationViewModel.Tool.QR_CODE -> {
                        startActivity(Intent(activityOwner, MainActivity::class.java))
                    }
                    NavigationViewModel.Tool.FAVORITE -> {
                        startActivity(Intent(activityOwner, FavoriteActivity::class.java))
                    }
                    NavigationViewModel.Tool.HISTORY -> {
                        startActivity(Intent(activityOwner, HistoryActivity::class.java))
                    }
                    NavigationViewModel.Tool.MY_QR -> {
                        lifecycleScope.launch() {
                            val myDao = QrRoomDatabase.getDataBase(activityOwner).qrMyDao()
                            if (myDao.getAllMyQr().size > 0) {
                                startActivity(
                                    Intent(activityOwner, ShowQRActivity::class.java)
                                )

                            } else {
                                startActivity(
                                    Intent(
                                        activityOwner, MyQRActivity::class.java
                                    )
                                )

                            }
                        }
                    }
                    NavigationViewModel.Tool.CREATED_QR -> {
                        startActivity(Intent(activityOwner, SelectStyleFragment::class.java))
                    }
                    NavigationViewModel.Tool.SETTING -> {

                    }
                    NavigationViewModel.Tool.SHARE -> {

                    }
                }
            }
        }
        mBinding.btnFavorite.setOnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) {
                mBinding.btnFavorite.setBackgroundResource(R.drawable.ic_star_fill)
                lifecycleScope.launch(Dispatchers.IO) {
                    val qrFavoriteDao = QrRoomDatabase.getDataBase(activityOwner).qrFavoriteDao()
                    val qrDao = QrRoomDatabase.getDataBase(activityOwner).qrDao()
                    val id = qrDao.getLastId()
                    val item = qrDao.getItemById(id.toInt())
                    qrFavoriteDao.insertFavoriteQr(
                        QrFavoriteModel(
                            imageString = item!!.imageString,
                            imageBitmap = item.imageBitmap,
                            titleTimeString = item.titleTimeString,
                            titleString = item.titleString,
                            timeString = item.timeString,
                            linkString = item.linkString,
                            phone = item.phone,
                            message = item.message,
                            email = item.email,
                            topic = item.topic,
                            content = item.content,
                            document = item.document,
                            fullName = item.fullName,
                            workPlace = item.workPlace,
                            address = item.address,
                            note = item.note,
                            networkName = item.networkName,
                            password = item.password,
                            typeWifi = item.typeWifi,
                            latitude = item.latitude,
                            longitude = item.longitude,
                            query = item.query
                        )
                    )
                }
            } else {

                mBinding.btnFavorite.setBackgroundResource(R.drawable.ic_star)
                lifecycleScope.launch(Dispatchers.IO) {
                    val qrFavoriteDao = QrRoomDatabase.getDataBase(activityOwner).qrFavoriteDao()
                    val qrDao = QrRoomDatabase.getDataBase(activityOwner).qrDao()
                    val id = qrDao.getLastId()
                    val item = qrDao.getItemById(id.toInt())
                    qrFavoriteDao.deleteFavoriteQr(item!!.timeString)
                    Log.d("TAG", "listener: ${item.timeString}")
                }
            }
        }
        mBinding.btnEditText.setOnClickListener {
            showDialogEditTittle(activityOwner)
        }
    }

    private fun saveImage(bitmap: Bitmap, title: String) {
        val filename = "$title.png"
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
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


    private fun shareImage(bitmap: Bitmap) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"
        val cachePath = File(activityOwner.externalCacheDir, "shared_image.jpg")
        val outputStream = FileOutputStream(cachePath)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        val imageUri = FileProvider.getUriForFile(
            activityOwner, "${activityOwner.packageName}.provider", cachePath
        )
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ hình ảnh qua"))
    }

    private fun detectResultFromImage() {
        try {

            val inputImage = InputImage.fromFilePath(
                activityOwner, getImageUri(
                    requireContext(),
                    createBitmap(mBinding.cardView, activityOwner),
                    number.toString()
                )!!
            )
            barCodeScanner!!.process(inputImage).addOnSuccessListener { barcodes ->
                extractBarcodeQrCodeInfo(barcodes)
            }.addOnFailureListener {
                Toast.makeText(activityOwner, "errorListener", Toast.LENGTH_SHORT).show()
            }
        } catch (e: java.lang.Exception) {
            Log.e("TAG", "detectResultFromImage: ${e.message}")
            Toast.makeText(activityOwner, "error", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun extractBarcodeQrCodeInfo(barcodes: List<Barcode>) {
        for (barcode in barcodes) {
            barcodeDefault = barcode
            val rawValue = barcode.rawValue
            when (barcode.valueType) {
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
                    mBinding.txtResult.text =
                        "TYPE_WIFI \nssid:$ssid \npassword: $password \nencryptionType: $encryptionType"

                }
                Barcode.TYPE_URL -> {

                    val url = barcode.url

                    val title = url?.title
                    val urlValue = url?.url

                    mBinding.txtResult.text = "TYPE_URL \ntitle: $title \nurl: $urlValue \n"


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
                    Log.d("TAG", "extractBarcodeQrCodeInfo: $rawValue")
                }
            }
        }
    }

    private fun showDialogEditTittle(context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.layout_dialog_rename)
        val btnCancel = dialog.findViewById<TextView>(R.id.btn_cancel)
        val btnConfirm = dialog.findViewById<TextView>(R.id.btn_confirm)
        val editText = dialog.findViewById<EditText>(R.id.edt_feedback)
        btnCancel.alpha = 0.3f
        editText.hint = mBinding.txtTitle.text
        editText.addTextChangedListener {
            if (editText.text.isNotEmpty() || editText.text.isNotBlank()) {
                btnConfirm.isEnabled = true
                btnConfirm.setOnClickListener {
                    mBinding.txtTitle.text = editText.text.toString()
                    lifecycleScope.launch(Dispatchers.IO) {
                        val qrFavoriteDao =
                            QrRoomDatabase.getDataBase(activityOwner).qrFavoriteDao()
                        val qrDao = QrRoomDatabase.getDataBase(activityOwner).qrDao()
                        val idHistory = qrDao.getLastId()
                        val idFavorite = qrFavoriteDao.getLastIdFavorite()
                        if (isFavorite) {
                            val qrModelFavorite = qrFavoriteDao.getItemById(idFavorite.toInt())
                            if (qrModelFavorite != null) {
                                qrModelFavorite.titleString = editText.text.toString()
                                qrFavoriteDao.updateItem(qrModelFavorite)
                            }
                        }
                        val qrModel = qrDao.getItemById(idHistory.toInt())
                        if (qrModel != null) {
                            qrModel.titleString = editText.text.toString()
                            qrDao.updateItem(qrModel)
                        }
                    }
                    dialog.dismiss()
                }
            } else {
                btnConfirm.isEnabled = false
            }
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun setUpNavigation() {
        val listNav = navigationModel.menuNav()
        navigationAdapter = NavigationAdapter()
        mBinding.rcvNav.adapter = navigationAdapter
        navigationAdapter.submitList(listNav.toMutableList())
        val toggle = ActionBarDrawerToggle(activityOwner, mBinding.drawerLayout, 0, 0)
        mBinding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }
}