package com.mpieterse.fizznap.ui.adapters

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mpieterse.fizznap.core.utils.Clogger
import com.mpieterse.fizznap.databinding.ItemImageBinding

class GalleryViewHolder(
    private val binding: ItemImageBinding
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        private const val TAG = "GalleryViewHolder"
    }


// --- Internals


    /**
     * Set the image source of the view element to the specified URI.
     *
     * @param fileUri The path or URI string to the image file.
     *
     * @throws IllegalArgumentException If the provided path is incorrectly
     *         constructed or cannot be converted to a valid URI for any reason.
     */
    fun bind(fileUri: Uri) {
        Clogger.d(
            TAG, "<bind>: uri=[$fileUri]"
        )

        Glide.with(binding.ivImage.context).load(fileUri)
            .override(binding.ivImage.width, binding.ivImage.height).centerCrop()
            .into(binding.ivImage)
    }
}