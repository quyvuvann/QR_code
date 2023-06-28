package com.example.example_qr_code.base

interface SelectItemTouchListener<T> {
    fun setSelectedItem(position: Int) {}
    fun getSelectedItem() : T?

    fun multiSelection(position: Int, isMaximum: Boolean) {}
    fun clearSelections() {}
    fun unselectItem(position: Int) {}
    fun getSelectedItems() : List<T>
    fun isMaximum() :Boolean
}

interface DiffUtilListener<T> {
    fun submitList(newData: List<T>) {}
}