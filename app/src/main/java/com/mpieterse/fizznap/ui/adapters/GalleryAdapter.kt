package com.mpieterse.fizznap.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.ListAdapter
import com.mpieterse.fizznap.core.utils.Clogger
import com.mpieterse.fizznap.databinding.ItemImageBinding

class GalleryAdapter : ListAdapter<String, GalleryViewHolder>(GalleryDiffCallback()) {
    companion object {
        private const val TAG = "GalleryAdapter"
    }


// --- Contracts


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): GalleryViewHolder {
        Clogger.d(
            TAG, "Constructing the ViewHolder"
        )

        return GalleryViewHolder(
            ItemImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(
        holder: GalleryViewHolder, position: Int
    ) = holder.bind(getItem(position).toUri()).also {
        Clogger.d(
            TAG, "<onBindViewHolder>: position=[$position]"
        )
    }


// --- Internals


    fun insert(directory: String) {
        Clogger.d(
            TAG, "Updating the source collection"
        )

        val collection = currentList.toMutableList().apply {
            add(0, directory)
        }
        submitList(collection)
    }
}