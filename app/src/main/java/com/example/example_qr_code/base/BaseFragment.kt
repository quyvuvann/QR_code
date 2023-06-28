package com.example.example_qr_code.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.example_qr_code.CreateViewModel
import com.example.example_qr_code.activity.Create2Activity
import com.example.example_qr_code.model.DataViewModel

abstract class BaseFragment<VB : ViewDataBinding, VM : BaseViewModel> : Fragment() {
    private lateinit var myInflater: LayoutInflater
    protected val createViewModel by activityViewModels<CreateViewModel>()
    protected val dataViewModel by activityViewModels<DataViewModel>()
    private var binding: VB? = null
    abstract val viewModel: VM
    protected val mBinding: VB
        get() = binding!!
    protected val activityOwner by lazy { requireActivity() as Create2Activity }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (!::myInflater.isInitialized) {
            myInflater = LayoutInflater.from(requireActivity())
        }
        binding = DataBindingUtil.inflate(myInflater, getLayoutId(), container, false)
        init()
        setupView()
        listener()

        return mBinding.root
    }

    abstract fun getLayoutId(): Int
    open fun init() {}
    abstract fun setupView()
    abstract fun listener()
    open fun onBackPressed() = false

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}