package com.example.example_qr_code

import android.annotation.SuppressLint
import android.content.Intent
import com.example.example_qr_code.activity.MainActivity
import com.example.example_qr_code.base.BaseActivity
import com.example.example_qr_code.databinding.ActivitySplashBinding


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_splash
    override fun setUpView() {
       mBinding.background.setBackgroundResource(R.drawable.background_qr)
    }

    override fun listener() {
        mBinding.btnStart.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))

        }
    }
}