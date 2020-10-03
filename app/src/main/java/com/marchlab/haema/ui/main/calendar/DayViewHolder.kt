package com.marchlab.haema.ui.main.calendar

import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.marchlab.haema.R
import com.marchlab.haema.databinding.ItemCalendarBinding
import org.threeten.bp.LocalDate
import java.util.*

class DayViewHolder(
    private val binding: ItemCalendarBinding,
    private val onClickAction: (date: LocalDate, isJournal: Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(day: Day){
        val date = LocalDate.now()
            .withMonth(day.calendar.get(Calendar.MONTH)+1)
            .withDayOfMonth(day.calendar.get(Calendar.DAY_OF_MONTH))
            .withYear(day.calendar.get(Calendar.YEAR))

        val isJournal = day.emotion != null

        binding.calendarItemRootLayout.setOnClickListener { onClickAction(date, isJournal) }

        if(day.state == DayState.ThisMonth) {
            binding.calendarItemRootLayout.isVisible = true
            binding.calendarItemTodayIndicator.visibility = if(day.isToday) View.VISIBLE else View.GONE
            binding.calendarItemEmotion.visibility = if(day.emotion != null) View.VISIBLE else View.GONE
            binding.calendarItemDayText.text = day.calendar.get(Calendar.DAY_OF_MONTH).toString()
            val textColor = if(day.isFuture) ContextCompat.getColor(itemView.context, R.color.text_hint_disable) else ContextCompat.getColor(itemView.context, R.color.text_display)
            binding.calendarItemDayText.setTextColor(textColor)
            binding.calendarItemRootLayout.isClickable = !day.isFuture
            day.emotion.let {
                when(it?.state){
                    1 -> binding.calendarItemEmotion.setImageResource(R.drawable.img_calendar_emo_joy)
                    2 -> binding.calendarItemEmotion.setImageResource(R.drawable.img_calendar_emo_happiness)
                    3 -> binding.calendarItemEmotion.setImageResource(R.drawable.img_calendar_emo_proud)
                    4 -> binding.calendarItemEmotion.setImageResource(R.drawable.img_calendar_emo_tired)
                    5 -> binding.calendarItemEmotion.setImageResource(R.drawable.img_calendar_emo_sadness)
                    6 -> binding.calendarItemEmotion.setImageResource(R.drawable.img_calendar_emo_anger)
                    7 -> binding.calendarItemEmotion.setImageResource(R.drawable.img_calendar_emo_fear)
                    8 -> binding.calendarItemEmotion.setImageResource(R.drawable.img_calendar_emo_dispressed)
                    9 -> binding.calendarItemEmotion.setImageResource(R.drawable.img_calendar_emo_calmness)
                }
            }
        } else {
            binding.calendarItemRootLayout.visibility = View.INVISIBLE
        }
    }

}