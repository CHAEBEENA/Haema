package com.marchlab.haema.ui.imagepicker

import android.net.Uri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.marchlab.haema.databinding.ItemImagePickerBinding

class ImagePickerViewHolder(
    private val binding: ItemImagePickerBinding,
    private val onClickAction: (position: Int, uri: Uri) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(uri: Uri, selectedPosition: Int) {
        Glide.with(binding.imageView)
            .load(uri)
            .transform(CenterCrop())
            .into(binding.imageView)

        binding.imageView.setOnClickListener { onClickAction(adapterPosition, uri) }

        binding.mask.isVisible = selectedPosition == adapterPosition
        binding.selectedImageView.isVisible = selectedPosition == adapterPosition
    }
}