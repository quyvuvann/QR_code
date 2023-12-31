package com.example.example_qr_code.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel


abstract class BaseViewModel : ViewModel() {

    val messageString: MutableLiveData<String> = MutableLiveData()
    val messageStringId: MutableLiveData<Int> = MutableLiveData()

}