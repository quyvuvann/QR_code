package com.example.example_qr_code

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide


@BindingAdapter("app:loadImg")
fun ImageView.loadImg(str: Any) = Glide.with(context).load(str).into(this)

