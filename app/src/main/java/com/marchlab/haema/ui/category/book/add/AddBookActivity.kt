package com.marchlab.haema.ui.category.book.add

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.observe
import androidx.viewpager2.widget.ViewPager2
import com.marchlab.haema.R
import com.marchlab.haema.databinding.ActivityAddBookBinding
import com.marchlab.haema.ui.category.*
import com.marchlab.haema.ui.main.ImagePickerFragment
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.Result
import com.marchlab.haema.util.result.successOrNull
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import org.threeten.bp.LocalDate
import timber.log.Timber
import timber.log.debug

class AddBookActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityAddBookBinding::inflate)

    private val viewModel by lifecycleScope.viewModel<AddBookViewModel>(this)

    private val mAdapter: AddBookPagerAdapter by lifecycleScope.inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory(lifecycleScope)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()

        setupObserver()
    }

    private fun init() {
        binding.addBookViewPager.adapter = mAdapter
        binding.addBookViewPager.isUserInputEnabled = false
        binding.addBookViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                binding.addBookPageNavigator.isVisible = position != PAGE_BOOK_SEARCH
                binding.addBookPreviousPage.isVisible = position == PAGE_BOOK_REVIEW
                binding.addBookNextPage.isVisible = position == PAGE_BOOK_INFO
                binding.addBookDone.isVisible = position == PAGE_BOOK_REVIEW
                binding.addBookPageIndicator1.isSelected = position == PAGE_BOOK_INFO
                binding.addBookPageIndicator2.isSelected = position == PAGE_BOOK_REVIEW

                if(position == PAGE_BOOK_SEARCH) viewModel.clear()
            }
        })

        binding.addBookNextPage.setOnClickListener { viewModel.onPageChange(PAGE_BOOK_REVIEW) }

        binding.addBookPreviousPage.setOnClickListener { viewModel.onPageChange(PAGE_BOOK_INFO) }
    }

    private fun setupObserver() {
        viewModel.page.observe(this) { currentPage -> binding.addBookViewPager.currentItem = currentPage }

        viewModel.title.observe(this) { title ->
            binding.addBookNextPage.isEnabled = title.isNotBlank()
            binding.addBookNextPage.setOnClickListener { if(it.isEnabled) viewModel.onPageChange(2) }
        }

        viewModel.canAdd.observe(this) { requirementsFilled ->
            when {
                requirementsFilled -> {
                    binding.addBookDone.setTextColor(ContextCompat.getColor(this, R.color.text_basic))
                    binding.addBookDone.setOnClickListener {
                        viewModel.onComplete()
                        binding.addBookDone.setOnClickListener(null)
                    }
                }
                else -> {
                    binding.addBookDone.setTextColor(ContextCompat.getColor(this, R.color.text_hint_disable))
                    binding.addBookDone.setOnClickListener(null)
                }
            }
        }

        viewModel.addBookResult.observe(this) { result -> result.successOrNull()?.let { finish() } }
    }

    override fun onBackPressed() {
        supportFragmentManager.findFragmentById(R.id.add_book_root_layout)
            ?.let { supportFragmentManager.commit { remove(it) }; return }

        when {
            binding.addBookViewPager.currentItem > 0 -> viewModel.onPageChange(--binding.addBookViewPager.currentItem)
            else -> super.onBackPressed()
        }
    }

    companion object {
        const val PAGE_BOOK_SEARCH = 0
        const val PAGE_BOOK_INFO = 1
        const val PAGE_BOOK_REVIEW = 2
    }
}
