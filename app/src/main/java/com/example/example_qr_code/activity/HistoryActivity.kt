package com.example.example_qr_code.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.example_qr_code.QrModel
import com.example.example_qr_code.QrRoomDatabase
import com.example.example_qr_code.R
import com.example.example_qr_code.adapter.HistoryAdapter
import com.example.example_qr_code.adapter.NavigationAdapter
import com.example.example_qr_code.base.BaseActivity
import com.example.example_qr_code.base.NavigationViewModel
import com.example.example_qr_code.databinding.ActivityHistoryBinding
import kotlinx.coroutines.launch

class HistoryActivity : BaseActivity<ActivityHistoryBinding>() {

    private var navigationAdapter = NavigationAdapter()
    private var historyAdapter = HistoryAdapter()
    private var listDataHistory = mutableListOf<QrModel>()
    override fun getLayoutId() = R.layout.activity_history

    override fun setUpView() {
        Glide.with(this).load(R.drawable.ic_qrcode).into(findViewById(R.id.img_nav))

        lifecycleScope.launch {
            val daoQr = QrRoomDatabase.getDataBase(this@HistoryActivity).qrDao()
            Log.d("TAG", "setUpViewlaunch: ${daoQr.getAllQr()}")
            listDataHistory = daoQr.getAllQr()

            mBinding.rcvView.adapter = historyAdapter
            historyAdapter.submitList(daoQr.getAllQr())
//            Log.d("TAG", "setUpView: ${daoQr.getAllQr()[0].timeString} | ${daoQr.getAllQr()[0].titleString} | ${daoQr.getAllQr()[0].linkString}")
        }
        Log.d("TAG", "setUpView: ${listDataHistory}")


        setUpNavigation()
        navigationAdapter.listener = object : NavigationAdapter.IListener {
            override fun onClickNav(item: NavigationViewModel.NavigationItem) {
                when (item.toolId) {
                    NavigationViewModel.Tool.QR_CODE -> {
                        startActivity(Intent(this@HistoryActivity, MainActivity::class.java))
                    }
                    NavigationViewModel.Tool.HISTORY -> {

                    }
                    NavigationViewModel.Tool.MY_QR -> {
                        showToast("My QR")
                    }
                    NavigationViewModel.Tool.CREATED_QR -> {
                        showToast("Create")
                    }
                    NavigationViewModel.Tool.SETTING -> {
                        showToast("Setting")
                    }
                    NavigationViewModel.Tool.SHARE -> {
                        showToast("Share")
                    }
                    else -> {}
                }
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

    override fun listener() {
        mBinding.xToolBar.setToolbarClickListener(clickRight = {

        }, clickLeft = {
            mBinding.drawerLayout.open()
        })

        historyAdapter.listener = object : HistoryAdapter.IListener {
            override fun onClick(item: QrModel) {
                Log.d("TAG", "onClick: {${item.id}")
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse(item.linkString)
                startActivity(intent)
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onClickDelete(item: QrModel, position: Int) {
                Log.d("TAG", "onClickDelete: ")
                lifecycleScope.launch {
                    val qrDao = QrRoomDatabase.getDataBase(this@HistoryActivity).qrDao()
                    qrDao.deleteQr(item.id)
                    Log.d("TAG", "onClickDelete: $position")
                    mBinding.rcvView.removeViewAt(position)
                    historyAdapter.submitList(qrDao.getAllQr())
                    historyAdapter.notifyDataSetChanged()

                }
            }

        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}