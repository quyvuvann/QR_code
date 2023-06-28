package com.example.example_qr_code.adapter

import com.example.example_qr_code.CreateViewModel
import com.example.example_qr_code.R
import com.example.example_qr_code.base.BaseAdapter
import com.example.example_qr_code.base.BaseListener
import com.example.example_qr_code.databinding.ActivityCreateBinding
import com.example.example_qr_code.model.CreateModel

class CreateAdapter :BaseAdapter<CreateViewModel.CreateItem>(R.layout.layout_item_create) {
    interface IListener:BaseListener{
        fun onClick(createModel: CreateViewModel.CreateItem)
    }
}