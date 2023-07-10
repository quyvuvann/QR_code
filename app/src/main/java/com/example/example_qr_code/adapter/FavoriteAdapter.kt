package com.example.example_qr_code.adapter

import com.example.example_qr_code.QrFavoriteModel
import com.example.example_qr_code.R
import com.example.example_qr_code.base.BaseAdapter
import com.example.example_qr_code.base.BaseListener
import com.example.example_qr_code.QrModel

class FavoriteAdapter:BaseAdapter<QrFavoriteModel>(R.layout.layout_item_favorite) {

    interface IListener : BaseListener {
        fun onClick(item: QrFavoriteModel)
        fun onClickDelete(item: QrFavoriteModel,position:Int)
        fun onClickFavorite(item: QrFavoriteModel,position: Int)

    }

}