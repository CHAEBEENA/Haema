package com.marchlab.haema.ui.category.movie.list

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.marchlab.haema.R
import com.marchlab.haema.databinding.ItemMovieBinding
import com.marchlab.haema.domain.model.Movie

class MovieViewHolder(
    private val binding: ItemMovieBinding,
    private val onClickAction: (id: Long) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Movie) {
        binding.movieItemRootLayout.setOnClickListener { onClickAction(item.id) }
        Glide.with(binding.root)
            .load(item.posterUrl.takeIf { it.isNotBlank() })
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .transform(CenterCrop())
            .fallback(R.drawable.img_photo)
            .into(binding.movieItemPoster)

        binding.movieItemTitle.text = item.title
        binding.movieItemRatingBar.rating = item.rate.toFloat()
    }
}