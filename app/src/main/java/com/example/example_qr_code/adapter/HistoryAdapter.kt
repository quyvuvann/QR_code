package com.example.example_qr_code.adapter

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.example.example_qr_code.R
import com.example.example_qr_code.base.BaseAdapter
import com.example.example_qr_code.base.BaseListener
import com.example.example_qr_code.QrModel
import com.example.example_qr_code.base.BaseViewHolder
import com.example.example_qr_code.databinding.LayoutItemHistoryBinding

class HistoryAdapter : BaseAdapter<QrModel>(R.layout.layout_item_history) {

    interface IListener : BaseListener {
        fun onClick(item: QrModel)
        fun onClickDelete(item: QrModel, position: Int)
        fun onClickFavorite(item: QrModel, position: Int)

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
//        val item = yourItemList[position]
//        holder.bind(item)
    }

    inner class ViewHolder(private val binding: LayoutItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.imgFavorite) {

        private var isSelected: Boolean = false

        init {
            binding.imgFavorite.setOnClickListener {
                isSelected = !isSelected
                updateItemColor()
            }
        }

        private fun updateItemColor() {
            if (isSelected) {
                binding.imgFavorite.setBackgroundColor(Color.RED)
            } else {
                // Trở về màu cũ khi isSelected là false
                binding.imgFavorite.setBackgroundColor(Color.WHITE)
            }
        }
    }


}