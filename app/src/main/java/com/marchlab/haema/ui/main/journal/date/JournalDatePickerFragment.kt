package com.marchlab.haema.ui.main.journal.date

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.marchlab.haema.R
import com.marchlab.haema.databinding.DialogJournalDatePickerBinding
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import org.threeten.bp.LocalDate
import timber.log.Timber
import timber.log.debug

@ExperimentalCoroutinesApi
class JournalDatePickerFragment: Fragment(R.layout.dialog_journal_date_picker) {

    private val binding by viewBinding(DialogJournalDatePickerBinding::bind)

    private val viewModel by lifecycleScope.viewModel<JournalDatePickerViewModel>(this)

    private lateinit var mAdapter: JournalDatePickerRecyclerViewAdapter

    private var mIsForced = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mIsForced = requireArguments().getBoolean(IS_FORCED)

        init()

        setupObserver()
    }

    private fun init() {
        mAdapter =
            JournalDatePickerRecyclerViewAdapter(
                ::onSelect
            )
        binding.datePickerRecyclerView.adapter = mAdapter
        binding.datePickerRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)

        binding.datePickerInnerLayout.setOnClickListener { /* block user input */ }
        binding.datePickerRootLayout.setOnClickListener {
            if(mIsForced) {
                mAdapter.currentList.find { it.enabled }
                    ?.let {
                        onSelect(it.date)
                        parentFragmentManager.commit { remove(this@JournalDatePickerFragment) }
                    }
            } else {
                parentFragmentManager.commit { remove(this@JournalDatePickerFragment) }
            }
        }
    }

    private fun onSelect(date: LocalDate) {
        parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(DATE_KEY to date.toEpochDay(), IS_FORCED to mIsForced))
        parentFragmentManager.commit { remove(this@JournalDatePickerFragment) }
    }

    private fun setupObserver() {
        viewModel.unavailableDates.observe(viewLifecycleOwner) { result ->
            when(result) {
                Result.Loading -> Unit
                is Result.Success -> {
                    val today = LocalDate.now()

                    /* 3 years */
                    val dates = (0L..1095L).map {
                        val date = today.minusDays(it)
                        val enable = !result.data.contains(date)
                        JournalDate(date, enable)
                    }

                    mAdapter.submitList(dates)

                    val defaultIndex = dates.indexOfFirst { it.enabled }

                    if(defaultIndex > 2) {
                        binding.datePickerRecyclerView.scrollToPosition(defaultIndex - 2)
                    }
                }
                is Result.Error -> Timber.debug(result.exception) { "observe unavailableDates throw Exception" }
            }
        }
    }

    companion object {
        const val REQUEST_KEY = "DATE_PICKER_REQUEST_KEY"

        const val DATE_KEY = "DATE_KEY"

        const val IS_FORCED = "IS_FORCED"
    }
}