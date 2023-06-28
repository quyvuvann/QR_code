package com.example.example_qr_code.activity

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.example_qr_code.CreateViewModel
import com.example.example_qr_code.R
import com.example.example_qr_code.adapter.CreateAdapter
import com.example.example_qr_code.adapter.NavigationAdapter
import com.example.example_qr_code.base.BaseActivity
import com.example.example_qr_code.base.NavigationViewModel
import com.example.example_qr_code.databinding.ActivityCreateBinding

class CreateActivity : BaseActivity<ActivityCreateBinding>() {
    private var navigationAdapter =NavigationAdapter()
    protected val createViewModel by viewModels<CreateViewModel>()
    private var createAdapter = CreateAdapter()
    override fun getLayoutId(): Int = R.layout.activity_create

    override fun setUpView() {
        mBinding.xToolBar.setGoneFilter(View.GONE)
        val list = createViewModel.menuCreate()
        mBinding.rcvCreate.adapter = createAdapter
        createAdapter.submitList(list)
        setUpNavigation()
    }


    override fun listener() {
        navigationAdapter.listener = object : NavigationAdapter.IListener {
            override fun onClickNav(item: NavigationViewModel.NavigationItem) {
                when (item.toolId) {
                    NavigationViewModel.Tool.QR_CODE -> {
                        startActivity(Intent(this@CreateActivity, MainActivity::class.java))
                    }
                    NavigationViewModel.Tool.HISTORY -> {
                        startActivity(Intent(this@CreateActivity, HistoryActivity::class.java))
                    }
                    NavigationViewModel.Tool.MY_QR -> {

                    }
                    NavigationViewModel.Tool.CREATED_QR -> {

                    }
                    NavigationViewModel.Tool.SETTING -> {

                    }
                    NavigationViewModel.Tool.SHARE -> {

                    }
                    else -> {}
                }
            }
        }
        createAdapter.listener = object : CreateAdapter.IListener{
            override fun onClick(createModel: CreateViewModel.CreateItem) {

            }

        }
    }
    private fun setUpNavigation() {
        val listNav = viewModel.menuNav()
//        navigationAdapter = NavigationAdapter()
        mBinding.rcvNav.adapter = navigationAdapter
        navigationAdapter!!.submitList(listNav.toMutableList())

        val toggle = ActionBarDrawerToggle(this, mBinding.drawerLayout, 0, 0)
        mBinding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


    }

}