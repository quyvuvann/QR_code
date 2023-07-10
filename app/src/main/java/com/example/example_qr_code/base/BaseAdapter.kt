package com.example.example_qr_code.base

import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.example_qr_code.BR


abstract class BaseAdapter<T : Any>(
    private val layout: Int,
) : RecyclerView.Adapter<BaseViewHolder>(), DiffUtilListener<T>, SelectItemTouchListener<T> {

    private lateinit var inflater: LayoutInflater

    /**
     * List items
     */
    val data: MutableList<T> = mutableListOf()


    /**
     * Listener action of item list
     */
    var listener: BaseListener? = null

    /**
     * Single select mode
     */
    private var singleSelect = false
    private var selectedItemPosition = -1

    private val TYPE_ITEM = 1
    private val TYPE_LOADING = 2
    private var isLoadingAdd = true

    /**
     * Multi select mode
     */
    private var multiSelect = false
    private val selectedPositions = SparseBooleanArray()
    private var hiddenList = listOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        if (!::inflater.isInitialized) {
            inflater = LayoutInflater.from(parent.context)
        }
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater, layout, parent, false
        )
        return BaseViewHolder(binding)

    }

override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
    holder.binding.apply {
        setVariable(BR.item, data[position])
        setVariable(BR.position, position)
        setVariable(BR.itemListener, listener)
        setVariable(BR.itemSelected,hiddenList)
//        if (singleSelect) {
//            setVariable(BR.itemSelected, selectedItemPosition == position)
//            val context = root.context as LifecycleOwner
//            lifecycleOwner = context
//            executePendingBindings()
//        }

    }
}


    override fun getItemCount() = data.size


    override fun submitList(newData: List<T>) {
        Log.d("TAG", "submitList: $newData")
        val diffResult = DiffUtil.calculateDiff(BaseDiffUtilCallback(data, newData))
        data.clear()
        data.addAll(newData)
        diffResult.dispatchUpdatesTo(this)


    }


    /**
     * Multi select mode
     */

    override fun clearSelections() {
        val positions = selectedPositions.clone()
        selectedPositions.clear()
        for (i in 0 until positions.size()) {
            val position = positions.keyAt(i)
            notifyItemChanged(position)
        }
    }


    override fun getSelectedItem(): T? {
        return if (selectedItemPosition != -1) {
            data[selectedItemPosition]
        } else {
            null
        }
    }


    override fun getSelectedItems(): List<T> {
        val selectedItemsT = mutableListOf<T>()
        for (i in 0 until selectedPositions.size()) {
            val position = selectedPositions.keyAt(i)
            selectedItemsT.add(data[position])
        }

        return selectedItemsT
    }

    override fun isMaximum() = selectedPositions.size() >= 20

}


