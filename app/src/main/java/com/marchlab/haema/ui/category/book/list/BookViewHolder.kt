package com.marchlab.haema.ui.category.book.list

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.marchlab.haema.R
import com.marchlab.haema.databinding.ItemBookBinding
import com.marchlab.haema.domain.model.Book
import com.marchlab.haema.util.extensions.convertDpToPx
import com.marchlab.haema.util.extensions.loadBookImage


class BookViewHolder(
    private val binding: ItemBookBinding,
    private val onClickAction: (id: Long) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(book: Book) {
        binding.bookImageView.loadBookImage(book.imageUrl?.takeIf { it.isNotBlank() })

        binding.bookTitleTextView.text = book.title

        binding.bookRatingBar.rating = book.rating.toFloat()

        binding.root.setOnClickListener { onClickAction(book.id) }
    }
}