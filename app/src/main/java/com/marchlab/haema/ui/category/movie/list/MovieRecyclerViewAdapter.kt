package com.marchlab.haema.ui.category.movie.list

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.marchlab.haema.databinding.ItemMovieBinding
import com.marchlab.haema.domain.model.Movie
import com.marchlab.haema.util.extensions.layoutInflater

class MovieRecyclerViewAdapter(
    private val onClickAction: (id: Long) -> Unit
): RecyclerView.Adapter<MovieViewHolder>() {

    var items = listOf<Movie>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(parent.layoutInflater, parent, false)

        binding.movieItemPoster.updateLayoutParams<ConstraintLayout.LayoutParams> {
            val dW = parent.resources.displayMetrics.widthPixels
            val margins = (binding.movieItemPoster.marginStart + binding.movieItemPoster.marginEnd) * 3
            val w = (dW - margins) / 3
            height = w * 4 / 3
        }

        return MovieViewHolder(
            binding,
            onClickAction
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(items[position])
    }
}