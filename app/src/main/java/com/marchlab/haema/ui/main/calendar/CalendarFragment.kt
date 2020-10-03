package com.marchlab.haema.ui.main.calendar

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentCalendarBinding
import com.marchlab.haema.ui.main.MainViewModel
import com.marchlab.haema.ui.main.journal.add.AddJournalActivity
import com.marchlab.haema.util.extensions.freeTrialPeriod
import com.marchlab.haema.util.extensions.toast
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.Result
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.scope.viewModel
import org.threeten.bp.LocalDate
import java.util.*

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private val binding by viewBinding(FragmentCalendarBinding::bind)

    private val viewModel by lifecycleScope.viewModel<CalendarViewModel>(this)

    private val parentViewModel by sharedViewModel<MainViewModel>()

    private lateinit var calendarAdapter : CalendarViewPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupFragmentResultListener()

        setupObserver()
    }

    private fun init() {
        setDateHeader(Calendar.getInstance())

        calendarAdapter = CalendarViewPagerAdapter(requireContext(),::onDaySelected)

        binding.calendarViewPager.apply {
            adapter = calendarAdapter
            onCalendarChangeListener = { viewModel.setDate(it) }
            onYearMonthChangeListener = { viewModel.setDate(it) }
        }

        binding.calendarMonthLayout.setOnClickListener { onMonthClicked() }
    }

    private fun setupFragmentResultListener() {
        parentFragmentManager.setFragmentResultListener(YearMonthPickerDialog.REQUEST_KEY,viewLifecycleOwner) {_, result->
            val date = result.getIntegerArrayList("change_month")
            binding.calendarViewPager.onYearMonthChanged(date!![0], date[1])
        }
    }

    private fun setupObserver() {
        parentViewModel.journals.observe(viewLifecycleOwner, Observer { result ->
            when(result) {
                Result.Loading -> {}
                is Result.Success -> {
                    calendarAdapter.journals = result.data
                }
                is Result.Error -> {}
            }
        })

        viewModel.isPurchased.observe(viewLifecycleOwner) { result ->
            when(result) {
                is Result.Success -> {
                    if(!result.data && requireContext().freeTrialPeriod() <= 0)
                        binding.calendarNavigateAddJournal.setOnClickListener { toast { "무료 사용기간이 만료되었습니다. 이용권을 등록해주세요!" } }
                    else
                        binding.calendarNavigateAddJournal.setOnClickListener { navigateAddJournal() }
                }
            }
        }

        viewModel.date.observe(viewLifecycleOwner) {
            setDateHeader(it)
        }

    }

    private fun navigateAddJournal() {
        startActivity(Intent(requireContext(), AddJournalActivity::class.java))
    }

    private fun setDateHeader(calendar: Calendar) {
        binding.calendarYear.text = calendar.get(Calendar.YEAR).toString()
        binding.calendarMonth.text = resources.getString(R.string.calendar_month, calendar.get(Calendar.MONTH)+1)
    }

    private fun onMonthClicked() {
        val year = viewModel.date.value?.get(Calendar.YEAR)
        val month = viewModel.date.value?.get(Calendar.MONTH)
        parentFragmentManager.setFragmentResult("onMonthClicked", bundleOf("dialog_date" to arrayListOf(year, month)))
    }

    private fun onDaySelected(date: LocalDate, isJournal: Boolean) {
        if(isJournal) {
            parentFragmentManager.setFragmentResult("onDaySelected", bundleOf())
            parentFragmentManager.setFragmentResult("date", bundleOf("selected_date" to date.toEpochDay()))
        }
        else {
            val intent = Intent(requireContext(), AddJournalActivity::class.java)
            intent.putExtra("date", date.toEpochDay())
            startActivity(intent)
        }
    }

}

