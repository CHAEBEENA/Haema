package com.marchlab.haema.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.observe
import androidx.viewpager2.widget.ViewPager2
import com.marchlab.haema.R
import com.marchlab.haema.databinding.ActivityMainBinding
import com.marchlab.haema.ui.category.CategoryActivity
import com.marchlab.haema.ui.main.calendar.CalendarFragment
import com.marchlab.haema.ui.main.calendar.YearMonthPickerDialog
import com.marchlab.haema.ui.main.billing.CheckBillingStateFragment
import com.marchlab.haema.ui.main.journal.add.AddJournalActivity
import com.marchlab.haema.ui.main.journal.list.JournalFragment
import com.marchlab.haema.ui.settings.BillingFragment
import com.marchlab.haema.ui.settings.SettingsActivity
import com.marchlab.haema.util.extensions.freeTrialPeriod
import com.marchlab.haema.util.extensions.toast
import com.marchlab.haema.util.extensions.updateTransition
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    private val viewModel: MainViewModel by lifecycleScope.viewModel(this)

    private val calendarFragment: CalendarFragment by lifecycleScope.inject()

    private val journalFragment: JournalFragment by lifecycleScope.inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory(lifecycleScope)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()

        setupObserver()
    }

    private fun init() {
        binding.viewPager.adapter = MainPagerAdapter(this, listOf(calendarFragment, journalFragment))
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when(position) {
                    PAGE_CALENDAR -> binding.tabLayout.updateTransition {
                        connect(R.id.tab_indicator, ConstraintSet.START, R.id.tab_calendar, ConstraintSet.START)
                        connect(R.id.tab_indicator, ConstraintSet.END, R.id.tab_calendar, ConstraintSet.END)
                    }
                    PAGE_JOURNAL -> binding.tabLayout.updateTransition {
                        connect(R.id.tab_indicator, ConstraintSet.START, R.id.tab_journal, ConstraintSet.START)
                        connect(R.id.tab_indicator, ConstraintSet.END, R.id.tab_journal, ConstraintSet.END)
                    }
                }
            }
        })

        binding.tabCalendar.setOnClickListener { binding.viewPager.currentItem = PAGE_CALENDAR }
        binding.tabJournal.setOnClickListener { binding.viewPager.currentItem = PAGE_JOURNAL }

        binding.navigateSetting.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }

        binding.navigateCategory.setOnClickListener { startActivity(Intent(this, CategoryActivity::class.java)) }

        supportFragmentManager.commit {
            replace(R.id.main_root_layout, CheckBillingStateFragment::class.java, null, CheckBillingStateFragment::class.java.simpleName)
        }
    }

    private fun setupObserver() {
        supportFragmentManager.setFragmentResultListener("onMonthClicked", this) {_, result ->
            val date = result.getIntegerArrayList("dialog_date")

            supportFragmentManager.beginTransaction()
                .add(R.id.main_root_layout, YearMonthPickerDialog(date!![0], date[1]))
                .addToBackStack(null)
                .commit()
        }

        supportFragmentManager.setFragmentResultListener("onDaySelected", this) { _, _ -> binding.viewPager.currentItem = 1 }

        viewModel.isPurchased.observe(this) { result ->
            when(result) {
                is Result.Success -> if(!result.data) {
                    val left = freeTrialPeriod()
                    if(left <= 15) toast { "무료 사용기간이 ${left}일 남았어요" }
                }
            }
        }
    }

    override fun onBackPressed() {
        if(binding.viewPager.currentItem == PAGE_JOURNAL) {
            binding.viewPager.currentItem = PAGE_CALENDAR
            return
        }

        super.onBackPressed()
    }

    companion object {
        private const val PAGE_CALENDAR = 0
        private const val PAGE_JOURNAL = 1

    }
}


