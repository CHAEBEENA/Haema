package com.marchlab.haema.ui.category.movie.add

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.marchlab.haema.databinding.ItemSearchMovieBinding
import com.marchlab.haema.domain.model.MovieSearchResult
import com.marchlab.haema.util.extensions.layoutInflater

class SearchMovieRecyclerViewAdapter(
    private val onClickAction: (movie: MovieSearchResult) -> Unit
): RecyclerView.Adapter<SearchMovieViewHolder>(){

    var items = listOf<MovieSearchResult>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMovieViewHolder {
        val binding = ItemSearchMovieBinding.inflate(parent.layoutInflater, parent, false)

        binding.searchMovieItemPoster.updateLayoutParams<ConstraintLayout.LayoutParams> {
            val dW = parent.resources.displayMetrics.widthPixels
            val margins = (binding.searchMovieItemPoster.marginStart + binding.searchMovieItemPoster.marginEnd) * 3
            val w = (dW - margins) / 3
            height = w * 4 / 3
        }

        return SearchMovieViewHolder(binding, onClickAction)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SearchMovieViewHolder, position: Int) = holder.bind(items[position])

}