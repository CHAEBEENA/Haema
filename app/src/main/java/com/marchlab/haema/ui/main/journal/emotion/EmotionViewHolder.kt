package com.marchlab.haema.ui.main.journal.emotion

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.marchlab.haema.databinding.ItemEmotionBinding
import com.marchlab.haema.domain.model.Emotion
import com.marchlab.haema.util.extensions.drawableResId
import com.marchlab.haema.util.extensions.stateName

class EmotionViewHolder(
    private val binding: ItemEmotionBinding,
    private val onClickAction: (emotion: Emotion, position: Int) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(emotion: Emotion, selectedPosition: Int) {
        binding.emotionItemRootLayout.setOnClickListener { onClickAction(emotion, adapterPosition) }

        Glide.with(binding.emotionImageView)
            .load(emotion.drawableResId)
            .into(binding.emotionImageView)

        binding.emotionTextView.text = emotion.stateName

        binding.emotionSelectedBackground.isVisible = selectedPosition == adapterPosition
    }
}