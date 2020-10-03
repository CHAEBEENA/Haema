package com.marchlab.haema.ui.main.journal.list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.marchlab.haema.databinding.ItemJournalBinding
import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.util.extensions.layoutInflater
import timber.log.Timber
import timber.log.debug

class JournalRecyclerViewAdapter(
    private val onLongClickAction: (journal: Journal, adapterPosition: Int) -> Unit
): ListAdapter<Journal, JournalViewHolder>(callback) {

    /* positionStart to itemCount */
    var mVisibleRange = 0 until 0
        set(value) {
            field = value
            try { notifyItemRangeChanged(value.first, value.last - value.first, true) }
            catch (exception: Exception) {}
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        return JournalViewHolder(
            ItemJournalBinding.inflate(parent.layoutInflater, parent, false),
            onLongClickAction
        )
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) = holder.bind(getItem(position))

    override fun onBindViewHolder(
        holder: JournalViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if(!payloads.isNullOrEmpty()) {
            Timber.debug { "position at $position played Animation" }
            holder.playAnimationIfVisible(payloads[0] as Boolean)
        } else
            super.onBindViewHolder(holder, position, payloads)
    }

    companion object {

        private val callback = object: DiffUtil.ItemCallback<Journal>() {

            override fun areItemsTheSame(oldItem: Journal, newItem: Journal): Boolean = oldItem == newItem

            override fun areContentsTheSame(oldItem: Journal, newItem: Journal): Boolean = oldItem.id == newItem.id
        }
    }
}