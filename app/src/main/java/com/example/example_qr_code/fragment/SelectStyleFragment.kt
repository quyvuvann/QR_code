package com.example.example_qr_code.fragment

import android.content.Intent
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.example_qr_code.CreateViewModel
import com.example.example_qr_code.QrRoomDatabase
import com.example.example_qr_code.R
import com.example.example_qr_code.activity.*
import com.example.example_qr_code.adapter.CreateAdapter
import com.example.example_qr_code.adapter.NavigationAdapter
import com.example.example_qr_code.base.BaseFragment
import com.example.example_qr_code.base.NavigationViewModel
import com.example.example_qr_code.databinding.FragmentSelectStyleBinding
import kotlinx.coroutines.launch

class SelectStyleFragment : BaseFragment<FragmentSelectStyleBinding, NavigationViewModel>() {
    private val createAdapter = CreateAdapter()
    private val navigationAdapter = NavigationAdapter()
    override val viewModel: NavigationViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.fragment_select_style

    override fun setupView() {
        Glide.with(activityOwner).load(R.drawable.ic_qrcode).into(mBinding.imgNav)
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
                        startActivity(Intent(activityOwner, MainActivity::class.java))
                    }
                    NavigationViewModel.Tool.FAVORITE -> {
                        startActivity(Intent(activityOwner, FavoriteActivity::class.java))
                    }
                    NavigationViewModel.Tool.HISTORY -> {
                        startActivity(Intent(activityOwner, HistoryActivity::class.java))
                    }
                    NavigationViewModel.Tool.MY_QR -> {
                        lifecycleScope.launch {
                            val myDao = QrRoomDatabase.getDataBase(activityOwner).qrMyDao()
                            if (myDao.getAllMyQr().size > 0) {
                                startActivity(
                                    Intent(activityOwner, ShowQRActivity::class.java)
                                )
                                destroyCategoryFragment()

                            } else {
                                startActivity(Intent(activityOwner, MyQRActivity::class.java))
                                destroyCategoryFragment()
                            }
                        }
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
        createAdapter.listener = object : CreateAdapter.IListener {
            override fun onClick(createModel: CreateViewModel.CreateItem) {
                when (createModel.toolId) {
                    CreateViewModel.Tool.WEB -> {
                        findNavController().navigate(R.id.action_selectStyleFragment_to_createdWebFragment)
                    }
                    CreateViewModel.Tool.DOCUMENT -> {
                        findNavController().navigate(R.id.action_selectStyleFragment_to_createdDocumentFragment)
                    }
                    CreateViewModel.Tool.CONTACT -> {
                        findNavController().navigate(R.id.action_selectStyleFragment_to_createdContactFragment)
                    }
                    CreateViewModel.Tool.EMAIL -> {
                        findNavController().navigate(R.id.action_selectStyleFragment_to_createdEmailFragment)
                    }
                    CreateViewModel.Tool.SMS -> {
                        findNavController().navigate(R.id.action_selectStyleFragment_to_createdSMSFragment)
                    }
                    CreateViewModel.Tool.PHONE -> {
                        findNavController().navigate(R.id.action_selectStyleFragment_to_createdPhoneFragment)
                    }
                    CreateViewModel.Tool.WIFI -> {
                        findNavController().navigate(R.id.action_selectStyleFragment_to_createdWifiFragment)
                    }
                    CreateViewModel.Tool.LOCATION -> {
                        findNavController().navigate(R.id.action_selectStyleFragment_to_createdGeographyFragment)
                    }
                    else -> {}
                }
            }
        }
        mBinding.xToolBar.setToolbarClickListener(
            clickLeft = {
                mBinding.drawerLayout.open()
            },
            clickRight = {}
        )
    }

    private fun setUpNavigation() {
        val listNav = viewModel.menuNav()
//        navigationAdapter = NavigationAdapter()
        mBinding.rcvNav.adapter = navigationAdapter
        navigationAdapter!!.submitList(listNav.toMutableList())

        val toggle = ActionBarDrawerToggle(activityOwner, mBinding.drawerLayout, 0, 0)
        mBinding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun destroyCategoryFragment() {
        val fragmentManager = activityOwner.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val currentFragment = fragmentManager.findFragmentById(R.id.selectStyleFragment)
        if (currentFragment != null) {
            transaction.detach(currentFragment).commit()
        }

    }


}