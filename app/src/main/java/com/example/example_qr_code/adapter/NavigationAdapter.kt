package com.example.example_qr_code.adapter

import com.example.example_qr_code.R
import com.example.example_qr_code.base.BaseAdapter
import com.example.example_qr_code.base.BaseListener
import com.example.example_qr_code.base.NavigationViewModel

class NavigationAdapter :BaseAdapter<NavigationViewModel.NavigationItem>(R.layout.layout_item_navigation){
    interface IListener : BaseListener {
        fun onClickNav(item: NavigationViewModel.NavigationItem)
    }

}