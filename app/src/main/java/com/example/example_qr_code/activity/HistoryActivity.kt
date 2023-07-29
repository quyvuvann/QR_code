package com.example.example_qr_code.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.example_qr_code.*
import com.example.example_qr_code.adapter.HistoryAdapter
import com.example.example_qr_code.adapter.NavigationAdapter
import com.example.example_qr_code.base.BaseActivity
import com.example.example_qr_code.base.NavigationViewModel
import com.example.example_qr_code.databinding.ActivityHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.util.*


class HistoryActivity : BaseActivity<ActivityHistoryBinding>() {

    private var navigationAdapter = NavigationAdapter()
    private var historyAdapter = HistoryAdapter()
    private var listDataHistory = mutableListOf<QrModel>()
    val list = mutableListOf<QrModel>()
    private val random = Random()
    private val number = random.nextInt(1000000) + 1
    override fun getLayoutId() = R.layout.activity_history

    override fun setUpView() {
        Glide.with(this).load(R.drawable.ic_qrcode).into(findViewById(R.id.img_nav))
        Glide.with(this).load(R.drawable.ic_empty).override(200).into(mBinding.imgEmpty)
        lifecycleScope.launch {
            val daoQr = QrRoomDatabase.getDataBase(this@HistoryActivity).qrDao()
            Log.d("TAG", "setUpViewlaunch: ${daoQr.getAllQr()}")
            listDataHistory = daoQr.getAllQr()
            list.addAll(daoQr.getAllQr().sortedByDescending { it.timeString })
            mBinding.rcvView.adapter = historyAdapter
            historyAdapter.submitList(list)
            if (daoQr.getAllQr().sortedByDescending { it.timeString }.isEmpty()) {
                mBinding.txtEmpty.visibility = View.VISIBLE
                mBinding.imgEmpty.visibility = View.VISIBLE
            } else {
                mBinding.imgEmpty.visibility = View.GONE
                mBinding.txtEmpty.visibility = View.GONE
            }
//            Log.d("TAG", "setUpView: ${daoQr.getAllQr()[0].timeString} | ${daoQr.getAllQr()[0].titleString} | ${daoQr.getAllQr()[0].linkString}")
        }
        Log.d("TAG", "setUpView: ${listDataHistory}")


        setUpNavigation()
        navigationAdapter.listener = object : NavigationAdapter.IListener {
            override fun onClickNav(item: NavigationViewModel.NavigationItem) {
                when (item.toolId) {
                    NavigationViewModel.Tool.QR_CODE -> {
                        startActivity(Intent(this@HistoryActivity, MainActivity::class.java))
                    }
                    NavigationViewModel.Tool.FAVORITE -> {
                        startActivity(Intent(this@HistoryActivity, FavoriteActivity::class.java))
                    }
                    NavigationViewModel.Tool.HISTORY -> {

                    }
                    NavigationViewModel.Tool.MY_QR -> {
                        lifecycleScope.launch() {
                            val myDao = QrRoomDatabase.getDataBase(this@HistoryActivity).qrMyDao()
                            if (myDao.getAllMyQr().size > 0) {
                                startActivity(
                                    Intent(this@HistoryActivity, ShowQRActivity::class.java)
                                )
                                finish()
                            } else {
                                startActivity(
                                    Intent(
                                        this@HistoryActivity, MyQRActivity::class.java
                                    )
                                )
                                finish()
                            }
                        }
                    }
                    NavigationViewModel.Tool.CREATED_QR -> {
                        startActivity(Intent(this@HistoryActivity, Create2Activity::class.java))
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

    }

    private fun setUpNavigation() {
        val listNav = viewModel.menuNav()
        mBinding.rcvNav.adapter = navigationAdapter
        navigationAdapter.submitList(listNav.toMutableList())
        val toggle = ActionBarDrawerToggle(this, mBinding.drawerLayout, 0, 0)
        mBinding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


    }

    override fun listener() {
        clickFavorite()
        mBinding.xToolBar.setToolbarClickListener(clickRight = {

        }, clickLeft = {
            mBinding.drawerLayout.open()
        })

        historyAdapter.listener = object : HistoryAdapter.IListener {
            override fun onClick(item: QrModel) {
                Log.d("TAG", "onClick: {${item.id}")
                Log.d("TAG", "onClickQR: ${item.id}")
                Log.d("TAG", "onClickQR: ${item.titleString}")
                Log.d("TAG", "onClickQR: ${item.linkString}")
                Log.d("TAG", "onClickQRTme: ${item.timeString}")
                Log.d("TAG", "onClickQR: ${item.content}")
                Log.d("TAG", "onClickQR: ${item.address}")
                Log.d("TAG", "onClickQR: ${item.document}")
                Log.d("TAG", "onClickQR: ${item.email}")
                Log.d("TAG", "onClickQR: ${item.fullName}")
                Log.d("TAG", "onClickQR: ${item.workPlace}")
                Log.d("TAG", "onClickQR: ${item.phone}")
                Log.d("TAG", "onClickQR: ${item.note}")
                Log.d("TAG", "onClickQR: ${item.message}")
                Log.d("TAG", "onClickQR: ${item.networkName}")
                Log.d("TAG", "onClickQR: ${item.password}")
                Log.d("TAG", "onClickQR: ${item.typeWifi}")
                Log.d("TAG", "onClickQR: ${item.latitude}")
                Log.d("TAG", "onClickQR: ${item.longitude}")
                Log.d("TAG", "onClickQR: ${item.query}")
                Log.d("TAG", "onClickQR: ${item.imageBitmap}")

                showDialogQR(stringToBitMap(item.imageBitmap)!!)
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onClickDelete(item: QrModel, position: Int) {
                Log.d("TAG", "onClickDelete: ")
                lifecycleScope.launch {
                    val qrDao = QrRoomDatabase.getDataBase(this@HistoryActivity).qrDao()
                    qrDao.deleteQr(item.timeString)
                    Log.d("TAG", "onClickDelete: $position")
                    historyAdapter.submitList(qrDao.getAllQr())
                    if (qrDao.getAllQr().sortedByDescending { it.timeString }.isEmpty()) {

                        mBinding.txtEmpty.visibility = View.VISIBLE
                        mBinding.imgEmpty.visibility = View.VISIBLE
                    } else {
                        mBinding.imgEmpty.visibility = View.GONE
                        mBinding.txtEmpty.visibility = View.GONE
                    }
                }
                mBinding.rcvView.removeViewAt(position)

                historyAdapter.notifyDataSetChanged()

            }

            override fun onClickFavorite(item: QrModel, position: Int) {
                historyAdapter.setSelectedItem(position)
                Log.d("TAG", "onClickFavorite: ${historyAdapter.isFavorite}")
                if (historyAdapter.isFavorite) {
                    lifecycleScope.launch {
                        val qrFavoriteDao =
                            QrRoomDatabase.getDataBase(this@HistoryActivity).qrFavoriteDao()
                        qrFavoriteDao.insertFavoriteQr(
                            QrFavoriteModel(
                                id = item.id,
                                imageString = item.imageString,
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
                    lifecycleScope.launch {
                        val qrDao = QrRoomDatabase.getDataBase(this@HistoryActivity).qrDao()
                        qrDao.deleteQr(item.timeString)
                        Log.d("TAG", "onClickDelete: $position")
                        historyAdapter.submitList(qrDao.getAllQr())
                        if (qrDao.getAllQr().sortedByDescending { it.timeString }.isEmpty()) {

                            mBinding.txtEmpty.visibility = View.VISIBLE
                            mBinding.imgEmpty.visibility = View.VISIBLE
                        } else {
                            mBinding.imgEmpty.visibility = View.GONE
                            mBinding.txtEmpty.visibility = View.GONE
                        }
                    }
                }

            }


        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showDialogQR(imgViewQR: Bitmap) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_show_qr)
        val btnSave = dialog.findViewById<TextView>(R.id.btn_save)
        val btnShare = dialog.findViewById<TextView>(R.id.btn_share)
        val imgQR = dialog.findViewById<ImageView>(R.id.img_viewShow)
        Glide.with(this).load(imgViewQR).into(imgQR)
        btnSave.setOnClickListener {
            saveImage(createBitmap(imgQR, this), number.toString())
            Toast.makeText(this, "Save successfully", Toast.LENGTH_SHORT).show()
        }

        btnShare.setOnClickListener {
//            getImageUri(this, createBitmap(imgQR, this), number.toString())
            Log.d("TAG", "showDialogQR: ${shareImage(createBitmap(imgQR, this))}")
            shareImage(createBitmap(imgQR, this))
        }
        dialog.show()

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
        sendBroadcast(mediaScanIntent)

    }


    private fun shareImage(bitmap: Bitmap) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"
        val cachePath = File(externalCacheDir, "shared_image.jpg")
        val outputStream = FileOutputStream(cachePath)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        val imageUri = FileProvider.getUriForFile(this, "$packageName.provider", cachePath)
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ hình ảnh qua"))
    }

    private fun clickFavorite() {
        historyAdapter.modelCallback = { item, position, isFavorite ->
            Log.d("TAG", "listener: $item")
            Log.d("TAG", "listener: $isFavorite")
            Log.d("TAG", "listener: ${item.isFavorite}")
            if (item.isFavorite) {
                lifecycleScope.launch {
                    val qrFavoriteDao =
                        QrRoomDatabase.getDataBase(this@HistoryActivity).qrFavoriteDao()
                    qrFavoriteDao.insertFavoriteQr(
                        QrFavoriteModel(
                            id = item.id,
                            imageString = item.imageString,
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
                lifecycleScope.launch {
                    val qrDao = QrRoomDatabase.getDataBase(this@HistoryActivity).qrFavoriteDao()
                    qrDao.deleteFavoriteQr(item.timeString)
                    Log.d("TAG", "onClickDelete: $position")
                    if (qrDao.getAllFavoriteQr().sortedByDescending { it.timeString }.isEmpty()) {

                        mBinding.txtEmpty.visibility = View.VISIBLE
                        mBinding.imgEmpty.visibility = View.VISIBLE
                    } else {
                        mBinding.imgEmpty.visibility = View.GONE
                        mBinding.txtEmpty.visibility = View.GONE
                    }
                }
            }
            lifecycleScope.launch(Dispatchers.IO) {
                val qrDao = QrRoomDatabase.getDataBase(this@HistoryActivity).qrDao()
                val qrModel = qrDao.getItemById(item.id)
                if (qrModel != null) {
                    qrModel.isFavorite = item.isFavorite
                    qrDao.updateItem(qrModel)
                }
            }


        }


    }
}