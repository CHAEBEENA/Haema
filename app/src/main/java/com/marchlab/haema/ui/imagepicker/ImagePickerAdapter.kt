package com.marchlab.haema.ui.imagepicker

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.marchlab.haema.databinding.ItemImagePickerBinding
import com.marchlab.haema.util.extensions.layoutInflater

class ImagePickerAdapter(
    private val onClickAction: (uri: Uri) -> Unit
): ListAdapter<Uri, ImagePickerViewHolder>(
    callback
) {

    var selectedPosition = -1
        set(value) {
            val old = field
            field = value

            notifyItemChanged(old)
            notifyItemChanged(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePickerViewHolder {

        val binding = ItemImagePickerBinding.inflate(parent.layoutInflater, parent, false)

        /* 정방형 */
        binding.imageView.updateLayoutParams<FrameLayout.LayoutParams> {
            val w = parent.resources.displayMetrics.widthPixels
            val margins = (binding.imageView.marginStart + binding.imageView.marginEnd) * 3
            val h = (w - margins) / 3
            height = h
        }

        return ImagePickerViewHolder(binding) { position, uri ->
            selectedPosition = position; onClickAction(uri)
        }
    }

    override fun onBindViewHolder(holder: ImagePickerViewHolder, position: Int) {
        holder.bind(getItem(position), selectedPosition)
    }

    companion object {
        private val callback = object : DiffUtil.ItemCallback<Uri>() {

            override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean = oldItem == newItem

            override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean = oldItem == newItem
        }
    }
}