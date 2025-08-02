package com.mpieterse.fizznap.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.mpieterse.fizznap.core.utils.Clogger

class GalleryDiffCallback : DiffUtil.ItemCallback<String>() {
    companion object {
        private const val TAG = "GalleryDiffCallback"
    }


// --- Contracts


    override fun areItemsTheSame(
        oldItem: String, newItem: String
    ): Boolean {
        val result = (oldItem == newItem)
        Clogger.d(
            TAG, "<areItemsTheSame>: oldItem=[${oldItem}], newItem=[${newItem}], result=[${result}]"
        )

        return result
    }


    override fun areContentsTheSame(
        oldItem: String, newItem: String
    ): Boolean {
        val result = (oldItem == newItem)
        Clogger.d(
            TAG,
            "<areContentsTheSame>: oldItem=[${oldItem}], newItem=[${newItem}], result=[${result}]"
        )

        return result
    }
}