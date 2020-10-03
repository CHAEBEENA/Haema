package com.marchlab.haema.ui.main.journal.emotion

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.marchlab.haema.databinding.ItemEmotionBinding
import com.marchlab.haema.domain.model.Emotion
import com.marchlab.haema.util.extensions.layoutInflater

class EmotionRecyclerViewAdapter(
    private val onClickAction: (emotion: Emotion) -> Unit
): ListAdapter<Emotion, EmotionViewHolder>(callback) {

    var selectedPosition = -1
        set(value) {
            val old = field
            field = value

            notifyItemChanged(old)
            notifyItemChanged(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmotionViewHolder {
        val mOnClickAction = { emotion: Emotion, position: Int -> onClickAction(emotion); selectedPosition = position }
        return EmotionViewHolder(ItemEmotionBinding.inflate(parent.layoutInflater, parent, false), mOnClickAction)
    }

    override fun onBindViewHolder(holder: EmotionViewHolder, position: Int) = holder.bind(getItem(position), selectedPosition)

    companion object {

        private val callback = object: DiffUtil.ItemCallback<Emotion>() {

            override fun areItemsTheSame(oldItem: Emotion, newItem: Emotion): Boolean = oldItem == newItem

            override fun areContentsTheSame(oldItem: Emotion, newItem: Emotion): Boolean = oldItem.state == newItem.state
        }
    }
}