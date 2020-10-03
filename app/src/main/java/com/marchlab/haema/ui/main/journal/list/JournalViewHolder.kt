package com.marchlab.haema.ui.main.journal.list

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.marchlab.haema.databinding.ItemJournalBinding
import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.ui.main.PhotoViewActivity
import com.marchlab.haema.util.extensions.rawResId
import com.marchlab.haema.util.extensions.toFormatString

class JournalViewHolder(
    private val binding: ItemJournalBinding,
    private val onLongClickAction: (journal: Journal, adapterPosition: Int) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun playAnimationIfVisible(isVisible: Boolean) {
        if(isVisible) {
            binding.lottieAnimationView.post { binding.lottieAnimationView.playAnimation() }
        }
    }

    fun bind(journal: Journal) = binding.bind(journal)

    private fun ItemJournalBinding.bind(journal: Journal) {

        rootLayout.setOnClickListener { lottieAnimationView.playAnimation() }

        rootLayout.setOnLongClickListener {
            onLongClickAction(journal, adapterPosition)
            true
        }

        /* 감정 */
        lottieAnimationView.setAnimation(journal.emotion.rawResId)
        lottieAnimationView.setOnClickListener { lottieAnimationView.playAnimation() }

        /* 날짜 */ dateTextView.text = journal.date.toFormatString()

        /* 사진 */
        maskingTape.isVisible = journal.imageUri != null
        imageView.isVisible = journal.imageUri != null
        journal.imageUri?.let { uri ->
            Glide.with(imageView)
                .load(uri)
                .transform(CenterCrop())
                .into(imageView)

            imageView.setOnClickListener { PhotoViewActivity.start(imageView.context, uri) }
        }

        /* 일기 */
        if(journal.content.isEmpty()) {
            contentTextView.isVisible = false
            journalItemSpacing.isVisible = true
        } else {
            contentTextView.isVisible = true
            journalItemSpacing.isVisible = false
            contentTextView.text = journal.content
        }
    }
}