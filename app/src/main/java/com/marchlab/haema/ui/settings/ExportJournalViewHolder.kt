package com.marchlab.haema.ui.settings

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.marchlab.haema.databinding.ItemExportBinding
import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.util.extensions.drawableResId
import com.marchlab.haema.util.extensions.toFormatString

class ExportJournalViewHolder(
    private val binding: ItemExportBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(journal: Journal) = binding.bind(journal)

    private fun ItemExportBinding.bind(journal: Journal) {

        /* 감정 */
        emotionImageView.setImageDrawable(ContextCompat.getDrawable(emotionImageView.context, journal.emotion.drawableResId))

        /* 날짜 */ dateTextView.text = journal.date.toFormatString()

        /* 사진 */
        maskingTape.isVisible = journal.imageUri != null
        imageView.isVisible = journal.imageUri != null
        imageView.setImageURI(journal.imageUri)

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