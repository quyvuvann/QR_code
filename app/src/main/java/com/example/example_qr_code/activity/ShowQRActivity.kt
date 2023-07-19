package com.example.example_qr_code.activity

import android.annotation.SuppressLint
import android.app.Activity
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
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.example_qr_code.*
import com.example.example_qr_code.adapter.NavigationAdapter
import com.example.example_qr_code.base.BaseActivity
import com.example.example_qr_code.base.NavigationViewModel
import com.example.example_qr_code.databinding.ActivityShowQrBinding
import com.example.example_qr_code.fragment.SelectStyleFragment
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

class ShowQRActivity : BaseActivity<ActivityShowQrBinding>() {

    private var navigationAdapter = NavigationAdapter()
    private var barCodeScanner: BarcodeScanner? = null
    private var barCodeScannerOptions: BarcodeScannerOptions? = null
    private val random = Random()
    val number = random.nextInt(1000000) + 1
    override fun getLayoutId(): Int = R.layout.activity_show_qr

    override fun setUpView() {
        lifecycleScope.launch {
            val myDao = QrRoomDatabase.getDataBase(this@ShowQRActivity).qrMyDao()
            for (i in 0 until myDao.getAllMyQr().size) {
                Glide.with(this@ShowQRActivity)
                    .load(stringToBitMap(myDao.getAllMyQr()[i].imageBitmap))
                    .into(mBinding.imgQrcode)
            }
        }
        val bitmap = intent.getParcelableExtra<Bitmap>("bitmapKey")
        Log.d("TAG", "setUpView: $bitmap")
        Glide.with(this).load(bitmap).into(mBinding.imgQrcode)
        Glide.with(this).load(R.drawable.ic_qrcode).into(mBinding.imgNav)
        barCodeScannerOptions =
            BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()

        barCodeScanner = BarcodeScanning.getClient(barCodeScannerOptions!!)
        setUpNavigation()
        mBinding.btnFavorite.setOnClickListener {
            detectResultFromImage()
        }
        setStatusBarTransparent(this, mBinding.root)
        Glide.with(this).load(R.drawable.ic_person).override(48).into(mBinding.imgView)
        mBinding.txtTitle.setText(R.string.contact)

        Handler(Looper.getMainLooper()).postDelayed({
            detectResultFromImage()
            mBinding.process.visibility = View.GONE
        }, 500)
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
                }
            }
        }
        mBinding.xToolBar.setToolbarClickListener(clickRight = {}, clickLeft = {
            mBinding.drawerLayout.open()
        })
        mBinding.btnNewCode.setOnClickListener {
            lifecycleScope.launch {
                val myDao = QrRoomDatabase.getDataBase(this@ShowQRActivity).qrMyDao()
                myDao.deleteAllItems()
                startActivity(Intent(this@ShowQRActivity, MyQRActivity::class.java))
                finish()
            }
        }
        mBinding.btnRename.setOnClickListener {
            showDialogFeedback(this)
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

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun extractBarcodeQrCodeInfo(barcodes: List<Barcode>) {
        for (barcode in barcodes) {
            val rawValue = barcode.rawValue
            when (barcode.valueType) {

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
        navigationAdapter.submitList(listNav.toMutableList())
        val toggle = ActionBarDrawerToggle(this, mBinding.drawerLayout, 0, 0)
        mBinding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

    }

    private fun showDialogFeedback(context: Context) {
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
                        val qrDao = QrRoomDatabase.getDataBase(this@ShowQRActivity).qrDao()
                        val myDao = QrRoomDatabase.getDataBase(this@ShowQRActivity).qrMyDao()
                        var idReceiver = intent.getIntExtra("idKey", 0)
                        Log.d("TAG", "showDialogFeedback: $idReceiver")
                        for (i in 0 until myDao.getAllMyQr().size) {
                            idReceiver = myDao.getAllMyQr()[0].idItem
                            Log.d("TAG", "showDialogFeedback: $idReceiver")
                        }
                        val qrModel = qrDao.getItemById(idReceiver)
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
}