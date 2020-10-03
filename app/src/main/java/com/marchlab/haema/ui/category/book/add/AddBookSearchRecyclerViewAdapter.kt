package com.marchlab.haema.ui.category.book.add

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.marchlab.haema.databinding.ItemSearchBookBinding
import com.marchlab.haema.domain.model.BookSearchResult
import com.marchlab.haema.util.extensions.layoutInflater

class AddBookSearchRecyclerViewAdapter(
    private val onClickAction: (book: BookSearchResult) -> Unit
): ListAdapter<BookSearchResult, AddBookSearchViewHolder>(callback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddBookSearchViewHolder {
        val binding = ItemSearchBookBinding.inflate(parent.layoutInflater, parent, false)

        binding.searchBookImageView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            val dW = parent.resources.displayMetrics.widthPixels
            val margins = (binding.searchBookImageView.marginStart + binding.searchBookImageView.marginEnd) * 3
            val w = (dW - margins) / 3
            height = w * 4 / 3
        }

        return AddBookSearchViewHolder(binding, onClickAction)
    }

    override fun onBindViewHolder(holder: AddBookSearchViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {

        private val callback = object: DiffUtil.ItemCallback<BookSearchResult>() {

            override fun areItemsTheSame(oldItem: BookSearchResult, newItem: BookSearchResult): Boolean = oldItem == newItem

            override fun areContentsTheSame(oldItem: BookSearchResult, newItem: BookSearchResult): Boolean
                    = oldItem.title == newItem.title && oldItem.authors == newItem.authors && oldItem.imageUrl == newItem.imageUrl
        }
    }
}