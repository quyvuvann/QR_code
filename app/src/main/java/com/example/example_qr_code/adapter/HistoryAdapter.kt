package com.example.example_qr_code.adapter

import com.example.example_qr_code.R
import com.example.example_qr_code.base.BaseAdapter
import com.example.example_qr_code.base.BaseListener
import com.example.example_qr_code.QrModel

class HistoryAdapter:BaseAdapter<QrModel>(R.layout.layout_item_history) {
    interface IListener : BaseListener {
        fun onClick(item: QrModel)
        fun onClickDelete(item: QrModel,position:Int)
    }
}