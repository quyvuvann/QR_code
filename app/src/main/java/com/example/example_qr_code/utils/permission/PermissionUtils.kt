package com.example.example_qr_code.utils.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.example_qr_code.activity.CustomScannerActivity
import com.example.example_qr_code.activity.MainActivity

object PermissionUtils {
    private const val CAMERA_REQUEST_CODE = 100
    private const val STORAGE_REQUEST_CODE = 101

    private lateinit var cameraPermission: Array<String>
    private lateinit var storagePermission: Array<String>

    fun requestPermission() {
        cameraPermission = arrayOf(
            android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    }

    fun checkStoragePermission(context: Context): Boolean {
        val result = (ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
        return result
    }

    fun requestStoragePermission(activity: MainActivity) {
        ActivityCompat.requestPermissions(
            activity, storagePermission,
            STORAGE_REQUEST_CODE
        )

    }
    fun requestStoragePermission2(activity: CustomScannerActivity) {
        ActivityCompat.requestPermissions(
            activity, storagePermission,
            STORAGE_REQUEST_CODE
        )

    }

    fun checkCameraPermission(context: Context): Boolean {
        val resultCamera = (ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED)
        val resultStorage = (ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
        return resultCamera
    }

    fun requestCameraPermission(activity: MainActivity) {
        ActivityCompat.requestPermissions(activity, cameraPermission, CAMERA_REQUEST_CODE)
    }
    fun requestCameraPermission2(activity: CustomScannerActivity) {
        ActivityCompat.requestPermissions(activity, cameraPermission, CAMERA_REQUEST_CODE)
    }
}