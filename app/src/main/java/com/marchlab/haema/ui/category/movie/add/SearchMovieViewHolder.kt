package com.marchlab.haema.ui.category.movie.add

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.marchlab.haema.R
import com.marchlab.haema.databinding.ItemSearchMovieBinding
import com.marchlab.haema.domain.model.MovieSearchResult

class SearchMovieViewHolder(
    private val binding: ItemSearchMovieBinding,
    private val onClickAction: (movie: MovieSearchResult) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: MovieSearchResult){
        val title = item.title.replace("<b>","").replace("</b>","")
        val director = if(item.director.isNotBlank()) { item.director.replace("|",", ") } else ""
        val actors = if(item.actor.isNotBlank()) { item.actor.split("|")} else emptyList()
        var actor = ""
        if(actors.isNotEmpty()) {
            actors.forEach {
                if (actor.length + it.length <= 40 && it.isNotBlank()) actor += "$it, "
                else return@forEach
            }
        }

        binding.searchMovieItemRootLayout.setOnClickListener {
            onClickAction(MovieSearchResult(
                title,
                item.image,
                item.subtitle,
                item.pubDate,
                director.takeIf { it.isNotBlank() }?.substring(0, director.length-2) ?: "",
                actor.takeIf { it.isNotBlank() }?.substring(0, actor.length-2) ?: "")
            )
        }

        Glide.with(binding.searchMovieItemPoster)
            .load(item.image.takeIf { it.isNotBlank() })
            .fallback(R.drawable.img_photo)
            .transform(CenterCrop())
            .into(binding.searchMovieItemPoster)

        binding.searchMovieItemTitle.text = title

        binding.searchMovieItemYear.text = item.pubDate
    }

}