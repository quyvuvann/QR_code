package com.example.example_qr_code.model

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.example_qr_code.base.BaseViewModel
import com.example.example_qr_code.utils.datastore.PrefsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val prefsSTore: PrefsStore) : BaseViewModel() {
    val isFirstOpenApp = this.prefsSTore.isOpenFirstTime().asLiveData()

    fun openFirstTime(states: Boolean) {
        viewModelScope.launch { prefsSTore.openFirstTime(states) }
    }

    fun init(context: AppCompatActivity) {
        prefsSTore.isOpenFirstTime()
    }
}