package com.example.example_qr_code.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.example_qr_code.R
import com.example.example_qr_code.base.BaseViewModel

class ClickViewModel : BaseViewModel() {
    private val _imageResId = MutableLiveData<Int>()
    val imageResId: LiveData<Int> = _imageResId

    private var isImage1 = true

    fun onItemClick() {
        val newImageResId = if (isImage1) {
            R.drawable.ic_star_fill
        } else {
            R.drawable.ic_star
        }
        _imageResId.value = newImageResId
        isImage1 = !isImage1
    }
}