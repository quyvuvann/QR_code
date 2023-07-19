package com.example.example_qr_code.base

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.example.example_qr_code.R
import com.example.example_qr_code.databinding.LayoutXToolbarBinding

class XToolbar :FrameLayout{
    private var title: String? = null
    private var colorTitle = Color.WHITE
    private var visibilityTitle = View.VISIBLE

    private var visibilityRight = View.VISIBLE
    private var visibilityLeft = View.VISIBLE

    private var iconRight: Drawable? = null
    private var iconLeft: Drawable? = null

    private var colorBackground: Int? = null

    private var _binding =
        LayoutXToolbarBinding.inflate(LayoutInflater.from(context), this, true)


    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        initAttrs(context, attributeSet)
        setupView()
    }

    private fun initAttrs(context: Context, attributeSet: AttributeSet?) {

        context.theme.obtainStyledAttributes(attributeSet, R.styleable.XToolbar, 0, 0).apply {
            try {
                title = getString(R.styleable.XToolbar_xtb_title)
                colorTitle = getColor(R.styleable.XToolbar_xtb_colorTitle, 0)
                visibilityTitle = getInt(R.styleable.XToolbar_xtb_visibilityTitle, visibilityTitle)

                visibilityRight = getInt(R.styleable.XToolbar_xtb_visibilityRight, visibilityRight)
                visibilityLeft = getInt(R.styleable.XToolbar_xtb_visibilityLeft, visibilityLeft)

                iconRight = getDrawable(R.styleable.XToolbar_xtb_iconRight)
                iconLeft = getDrawable(R.styleable.XToolbar_xtb_iconLeft)

                colorBackground = getColor(R.styleable.XToolbar_xtb_background, 0)


            } finally {
                recycle()
            }
        }
    }

    private fun getStatusBarHeight(): Int {
        var statusBarHeight = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }


    private fun marginTopWithHeight() {
        setMargins(_binding.root, 0, getStatusBarHeight(), 0, 0)
    }


    private fun setMargins(v: View, l: Int, t: Int, r: Int, b: Int) {
        if (v.layoutParams is MarginLayoutParams) {
            val p = v.layoutParams as MarginLayoutParams
            p.setMargins(l, t, r, b)
            v.requestLayout()
        }
    }


    private fun setupView() {
        _binding.txtTitle.apply {
            setTitleText(title ?: "Unknown")
            setTitleColor(colorTitle)
            setTitleTextVisible(visibilityTitle)
        }

        _binding.btnLeft.apply {
            setImageDrawable(iconLeft)
            setLeftButtonVisible(visibilityLeft)
        }

        _binding.btnRight.apply {
            setImageDrawable(iconRight)
            setRightButtonVisible(visibilityRight)
        }

        _binding.root.setBackgroundColor(colorBackground ?: 0)

    }


    fun setToolbarClickListener(clickLeft: (View) -> Unit, clickRight: (View) -> Unit) {
        _binding.apply {
            btnLeft.setOnClickListener {
                clickLeft(it)
            }

            btnRight.setOnClickListener {
                clickRight(it)
            }

        }
    }
    fun setToolbarClickListener2(clickFlash: (View) -> Unit, clickRotate: (View) -> Unit) {
        _binding.apply {
            btnFlash.setOnClickListener {
                clickFlash(it)
            }

            btnRotateCamera.setOnClickListener {
                clickRotate(it)
            }

        }
    }

    fun setLeftButtonResource(resId: Int) {
        Glide.with(context).load(resId).into(_binding.btnLeft)
    }

    fun setLeftButtonVisible(visible: Int) {
        _binding.btnLeft.visibility = visible
    }


    fun setRightButtonResource(resId: Int) {
        Glide.with(context).load(resId).into(_binding.btnRight)
    }

    fun setRightButtonVisible(visible: Int) {
        _binding.btnRight.visibility = visible
    }

    fun setTitleTextVisible(visible: Int) {
        _binding.txtTitle.visibility = visible
    }

    fun setTitleTextResource(titleResId: Int) {
        _binding.txtTitle.text = context.getString(titleResId)
    }

    fun setTitleText(title: String) {
        _binding.txtTitle.text = title
    }


    fun setTitleColor(titleColorId: Int) {
        _binding.txtTitle.setTextColor(titleColorId)
    }

    fun setBackground(color: Int) {
        _binding.root.setBackgroundColor(color)
    }
    fun setGoneFilter(visible: Int){
        _binding.btnFilter.visibility = visible
    }
    fun setGoneFlash(visible: Int){
        _binding.btnFlash.visibility = visible
    }
    fun setGoneRotateCamera(visible: Int){
        _binding.btnRotateCamera.visibility = visible
    }
    fun setImageFlash(image:Int){
        _binding.btnFlash.setImageResource(image)
    }
}