package com.example.example_qr_code.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.example_qr_code.R
import com.example.example_qr_code.base.BaseActivity
import com.example.example_qr_code.databinding.ActivityCustomScannerBinding
import com.example.example_qr_code.utils.permission.PermissionUtils
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.ViewfinderView
import java.util.*

class CustomScannerActivity : BaseActivity<ActivityCustomScannerBinding>() {

    private var imageUri: Uri? = null
    private lateinit var capture: CaptureManager
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    private lateinit var switchFlashlightButton: Button
    private lateinit var viewfinderView: ViewfinderView
    private var isFlashlightOn = false

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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    private fun hasFlash(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    fun switchFlashlight(view: View) {
//        if (getString(R.string.turn_on_flashlight) == switchFlashlightButton.text) {
        barcodeScannerView.setTorchOn()
//        } else {
//            barcodeScannerView.setTorchOff()
//        }
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
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun getLayoutId(): Int = R.layout.activity_custom_scanner

    override fun setUpView() {
        PermissionUtils.requestPermission()
        capture = CaptureManager(this, mBinding.barcodeScanner)
        mBinding.xToolBar.setGoneFlash(View.VISIBLE)
        mBinding.xToolBar.setGoneRotateCamera(View.VISIBLE)
    }

    override fun listener() {
        mBinding.xToolBar.setToolbarClickListener2(clickFlash = {
            toggleFlashlight()
        }, clickRotate = {
            mBinding.barcodeScanner.setTorchOff()
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
    }

    fun selectImageGallery() {
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
                val intent = Intent(this@CustomScannerActivity, MainActivity::class.java)
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

}