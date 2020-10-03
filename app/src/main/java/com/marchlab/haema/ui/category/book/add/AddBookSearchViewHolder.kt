package com.marchlab.haema.ui.category.book.add

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.marchlab.haema.R
import com.marchlab.haema.databinding.ItemSearchBookBinding
import com.marchlab.haema.domain.model.BookSearchResult
import com.marchlab.haema.util.extensions.convertDpToPx
import com.marchlab.haema.util.extensions.loadBookImage

class AddBookSearchViewHolder(
    private val binding: ItemSearchBookBinding,
    private val onClickAction: (book: BookSearchResult) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(book: BookSearchResult) {
        binding.bookSearchResultRootLayout.setOnClickListener { onClickAction(book) }

        binding.searchBookImageView.loadBookImage(book.imageUrl)

        binding.searchBookTitleTextView.text = book.title

        binding.searchBookAuthorTextView.text = book.authors
    }
}