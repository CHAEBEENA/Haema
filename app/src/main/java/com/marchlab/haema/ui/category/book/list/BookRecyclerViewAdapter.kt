package com.marchlab.haema.ui.category.book.list

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.marchlab.haema.databinding.ItemBookBinding
import com.marchlab.haema.domain.model.Book
import com.marchlab.haema.util.extensions.layoutInflater

class BookRecyclerViewAdapter(
    private val onClickAction: (id: Long) -> Unit
): ListAdapter<Book, BookViewHolder>(callback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(parent.layoutInflater, parent, false)

        binding.bookImageView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            val dW = parent.resources.displayMetrics.widthPixels
            val margins = parent.marginStart + parent.marginEnd + (binding.bookImageView.marginStart + binding.bookImageView.marginEnd) * 3
            val w = (dW - margins) / 3
            height = w * 4 / 3
        }

        return BookViewHolder(binding, onClickAction)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        val callback = object: DiffUtil.ItemCallback<Book>() {

            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean = oldItem == newItem

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean = oldItem.id == newItem.id
        }
    }
}