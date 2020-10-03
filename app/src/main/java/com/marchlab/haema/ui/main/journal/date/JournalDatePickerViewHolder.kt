package com.marchlab.haema.ui.main.journal.date

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.marchlab.haema.R
import com.marchlab.haema.databinding.ItemDateBinding
import com.marchlab.haema.util.extensions.toFormatString
import org.threeten.bp.LocalDate

class SelectDateViewHolder(
    private val binding: ItemDateBinding,
    private val onClickAction: (date: LocalDate) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(date: JournalDate) {
        binding.dateTextView.text = date.date.toFormatString()

        val colorString = if(date.enabled) R.color.text_display else R.color.text_hint_disable

        binding.dateTextView.setTextColor(ContextCompat.getColor(binding.root.context, colorString))

        if(date.enabled) binding.dateTextView.setOnClickListener { onClickAction(date.date) }
    }
}

