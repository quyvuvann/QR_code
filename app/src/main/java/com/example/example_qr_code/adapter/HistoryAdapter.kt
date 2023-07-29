package com.example.example_qr_code.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.example_qr_code.QrModel
import com.example.example_qr_code.R
import com.example.example_qr_code.base.BaseAdapter
import com.example.example_qr_code.base.BaseListener
import com.example.example_qr_code.base.BaseViewHolder
import com.example.example_qr_code.databinding.LayoutItemHistoryBinding
import kotlin.math.log

typealias ModelCallback = (qrModel:  QrModel,position:Int,isFavorite:Boolean) -> Unit

class HistoryAdapter() : BaseAdapter<QrModel>(R.layout.layout_item_history) {
     var isFavorite = false
    var modelCallback :ModelCallback ?= null

//    init {
//        setSingleSelectEnabled(true)
//    }

    interface IListener : BaseListener {
        fun onClick(item: QrModel)
        fun onClickDelete(item: QrModel, position: Int)
        fun onClickFavorite(item: QrModel, position: Int)

    }
    interface ICallBack{
        fun clickFavorite(model: QrModel)

    }



    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder.binding is LayoutItemHistoryBinding) {
            val list = data as MutableList<QrModel>
            val qr = list[position]
            if (qr.isFavorite){
                Glide.with(holder.binding.imgFavorite).load(R.drawable.ic_star_fill).override(48,48).into(holder.binding.imgFavorite)
            }
            Log.d("TAG", "onBindViewHolder: ${qr.isFavorite}")
            holder.binding.imgFavorite.setOnClickListener {

                qr.isFavorite = !qr.isFavorite

                if (qr.isFavorite){
                    holder.binding.imgFavorite.setImageResource(R.drawable.ic_star_fill)
                    Log.d("TAG", "onBindViewHold :${qr.imageBitmap}")
                    Log.d("TAG", "onBindViewHold :${qr.id}")
                    Log.d("TAG", "onBindViewHold :${qr.titleString}")
                    modelCallback?.invoke(qr,position,true)
                }else{
                    holder.binding.imgFavorite.setImageResource(R.drawable.ic_star)
                    modelCallback?.invoke(qr,position,false)
                    Log.d("TAG", "onBindViewHolder: $isFavorite")
                }
                Log.d("TAG", "onBindViewHolder: ${qr.isFavorite}")
                Log.d("TAG", "onBindViewHolder: clicklcsxvjkxkshgvkjsd")

            }
        }
    }


}