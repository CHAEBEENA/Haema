package com.marchlab.haema.ui.main.journal.date

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.marchlab.haema.databinding.ItemDateBinding
import com.marchlab.haema.util.extensions.layoutInflater
import org.threeten.bp.LocalDate

class JournalDatePickerRecyclerViewAdapter(
    private val onClickAction: (date: LocalDate) -> Unit
): ListAdapter<JournalDate, SelectDateViewHolder>(
    callback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectDateViewHolder
            = SelectDateViewHolder(
        ItemDateBinding.inflate(
            parent.layoutInflater,
            parent,
            false
        ), onClickAction
    )

    override fun onBindViewHolder(holder: SelectDateViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        private val callback = object: DiffUtil.ItemCallback<JournalDate>() {

            override fun areItemsTheSame(oldItem: JournalDate, newItem: JournalDate): Boolean = oldItem == newItem

            override fun areContentsTheSame(oldItem: JournalDate, newItem: JournalDate): Boolean
                    = oldItem.date == newItem.date && oldItem.enabled == newItem.enabled
        }
    }
}