package com.example.example_qr_code.activity

import androidx.appcompat.app.ActionBarDrawerToggle

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.example_qr_code.QrRoomDatabase
import com.example.example_qr_code.R
import com.example.example_qr_code.adapter.NavigationAdapter
import com.example.example_qr_code.base.BaseActivity
import com.example.example_qr_code.base.NavigationViewModel
import com.example.example_qr_code.databinding.ActivityCustomScannerBinding
import com.example.example_qr_code.utils.permission.PermissionUtils
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.ViewfinderView
import kotlinx.coroutines.launch
import java.util.*

class CustomScannerActivity : BaseActivity<ActivityCustomScannerBinding>() {

    private var imageUri: Uri? = null
    private lateinit var capture: CaptureManager
    private lateinit var viewfinderView: ViewfinderView
    private var isFlashlightOn = false
    private var navigationAdapter: NavigationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        capture.initializeFromIntent(intent, savedInstanceState);
        capture.setShowMissingCameraPermissionDialog(false);
        capture.decode();

    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }


    fun changeMaskColor(view: View) {
        val rnd = Random()
        val color = Color.argb(100, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        viewfinderView.setMaskColor(color)
    }

    fun changeLaserVisibility(visible: Boolean) {
        viewfinderView.setLaserVisibility(visible)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun getLayoutId(): Int = R.layout.activity_custom_scanner

    override fun setUpView() {
        PermissionUtils.requestPermission()
        Glide.with(this).load(R.drawable.ic_qrcode).into(findViewById(R.id.img_nav))
        Glide.with(this).load(R.drawable.ic_zoom_out).into(findViewById(R.id.btn_zoom_out))
        Glide.with(this).load(R.drawable.ic_zoom_in).into(findViewById(R.id.btn_zoom_in))
        capture = CaptureManager(this, mBinding.barcodeScanner)
        mBinding.xToolBar.setGoneFlash(View.VISIBLE)
        mBinding.xToolBar.setGoneRotateCamera(View.VISIBLE)
        setUpNavigation()
        setStatusBarTransparent(this,mBinding.root)
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

    override fun listener() {
        mBinding.xToolBar.setToolbarClickListener2(clickFlash = {
            toggleFlashlight()
        }, clickRotate = {
            mBinding.barcodeScanner.switchCamera()
        })
        mBinding.xToolBar.setToolbarClickListener(clickLeft = {
            Log.d("TAG", "listener: ")
            mBinding.drawerLayout.open()
        }, clickRight = {
            if (PermissionUtils.checkStoragePermission(this)) {
                selectImageGallery()
            } else {
                PermissionUtils.requestStoragePermission2(this)
            }
        })

        mBinding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                mBinding.barcodeScanner.zoomCamera(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        mBinding.btnZoomIn.setOnClickListener {
            mBinding.barcodeScanner.zoomin()

        }
        mBinding.btnZoomOut.setOnClickListener {
            mBinding.barcodeScanner.zoomOut()
        }

        navigationAdapter?.listener = object : NavigationAdapter.IListener {
            override fun onClickNav(item: NavigationViewModel.NavigationItem) {
                when (item.toolId) {
                    NavigationViewModel.Tool.QR_CODE -> {
//                        startActivity(Intent(this@MainActivity, MainActivity::class.java))
//                        mBinding.drawerLayout.close()
                    }
                    NavigationViewModel.Tool.FAVORITE -> {
                        startActivity(Intent(this@CustomScannerActivity, FavoriteActivity::class.java))
                    }
                    NavigationViewModel.Tool.HISTORY -> {
                        startActivity(Intent(this@CustomScannerActivity, HistoryActivity::class.java))

                    }
                    NavigationViewModel.Tool.MY_QR -> {
                        lifecycleScope.launch {
                            val myDao = QrRoomDatabase.getDataBase(this@CustomScannerActivity).qrMyDao()
                            if (myDao.getAllMyQr().size > 0) {
                                startActivity(
                                    Intent(this@CustomScannerActivity, ShowQRActivity::class.java)
                                )
                            } else {
                                startActivity(Intent(this@CustomScannerActivity, MyQRActivity::class.java))
                            }
                        }

                    }
                    NavigationViewModel.Tool.CREATED_QR -> {
                        startActivity(Intent(this@CustomScannerActivity, Create2Activity::class.java))

                    }
                    NavigationViewModel.Tool.SETTING -> {
//                        showToast("Setting")
                    }
                    NavigationViewModel.Tool.SHARE -> {
//                        showToast("Share")
                    }
                    else -> {}
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
                Log.d("TAG", "imageCustom: $imageUri")
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("uriKey", imageUri)
                startActivity(intent)
                finish()
//                mBinding.imgViewQR.setImageURI(imageUri)
//                Glide.with(this).load(imageUri).into(mBinding.imgViewQR)
            } else {
//                showToast("Cancelled...")
            }
        }

    private fun toggleFlashlight() {
        isFlashlightOn = !isFlashlightOn
        if (isFlashlightOn) {
            mBinding.barcodeScanner.setTorchOn()
            mBinding.xToolBar.setImageFlash(R.drawable.ic_flash_on)
        } else {
            mBinding.barcodeScanner.setTorchOff()
            mBinding.xToolBar.setImageFlash(R.drawable.ic_flash_off)
        }
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


}