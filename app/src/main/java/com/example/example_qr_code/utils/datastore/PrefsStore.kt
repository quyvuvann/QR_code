package com.example.example_qr_code.utils.datastore

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface PrefsStore {

    fun isOpenFirstTime(): Flow<Boolean>

    suspend fun openFirstTime(states: Boolean)
}