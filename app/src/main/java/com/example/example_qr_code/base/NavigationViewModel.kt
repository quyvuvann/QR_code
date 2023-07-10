package com.example.example_qr_code.base

import androidx.annotation.DrawableRes
import com.example.example_qr_code.R

class NavigationViewModel : BaseViewModel() {
    enum class Tool {
        QR_CODE,FAVORITE,HISTORY, MY_QR, CREATED_QR, SETTING, SHARE,

    }

    data class NavigationItem(
        val toolId: Tool, @DrawableRes val img: Int, val title: Int
    )

    fun menuNav() = listOf(
        NavigationItem(Tool.QR_CODE, R.drawable.ic_qrcode, R.string.qr_code),
        NavigationItem(Tool.FAVORITE, R.drawable.ic_star, R.string.Favorite),
        NavigationItem(Tool.HISTORY, R.drawable.ic_history, R.string.History),
        NavigationItem(Tool.MY_QR, R.drawable.ic_baseline_account_box_24, R.string.My_qr),
        NavigationItem(Tool.CREATED_QR, R.drawable.ic_baseline_create_24, R.string.Create),
        NavigationItem(Tool.SETTING, R.drawable.ic_baseline_settings_24, R.string.Setting),
        NavigationItem(Tool.SHARE, R.drawable.ic_baseline_share_24, R.string.Share)
    )


}