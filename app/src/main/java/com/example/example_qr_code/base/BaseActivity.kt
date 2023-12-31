package com.example.example_qr_code.base

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.example_qr_code.model.DataActivityViewModel
import com.example.example_qr_code.model.MainViewModel
import io.github.inflationx.viewpump.ViewPumpContextWrapper

abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var mBinding: VB
    protected val viewModel by viewModels<NavigationViewModel>()
    protected val dataActivityViewModel by viewModels<DataActivityViewModel>()


    @LayoutRes
    abstract fun getLayoutId(): Int
    open fun init() {}

    abstract fun setUpView()
    abstract fun listener()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        init()
        setUpView()
        listener()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }


}