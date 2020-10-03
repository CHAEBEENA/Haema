package com.marchlab.haema.ui.settings

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.marchlab.haema.databinding.ItemExportBinding
import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.util.extensions.layoutInflater

class ExportJournalRecyclerViewAdapter: ListAdapter<Journal, ExportJournalViewHolder>(callback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExportJournalViewHolder {
        return ExportJournalViewHolder(ItemExportBinding.inflate(parent.layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ExportJournalViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {

        private val callback = object: DiffUtil.ItemCallback<Journal>() {

            override fun areItemsTheSame(oldItem: Journal, newItem: Journal): Boolean = oldItem == newItem

            override fun areContentsTheSame(oldItem: Journal, newItem: Journal): Boolean = oldItem.id == newItem.id
        }
    }
}