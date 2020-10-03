package com.marchlab.haema.ui.category.book.edit

import android.content.Context
import android.content.Intent
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
import com.marchlab.haema.databinding.ActivityEditBookBinding
import com.marchlab.haema.ui.category.CategoryDatePickerFragment
import com.marchlab.haema.ui.main.ImagePickerFragment
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.successOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import org.threeten.bp.LocalDate
import timber.log.Timber
import timber.log.debug

@ExperimentalCoroutinesApi
class EditBookActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityEditBookBinding::inflate)

    private val viewModel by lifecycleScope.viewModel<EditBookViewModel>(this)

    private val mAdapter by lifecycleScope.inject<EditBookPagerAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory(lifecycleScope)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        intent
            .getLongExtra(EDIT_BOOK_ID, -1)
            .takeIf { it >= 0 }
            ?.let { viewModel.loadBook(it) }

        init()

        setupFragmentResultListener()

        setupObserver()
    }

    private fun init() {
        binding.editBookViewPager.adapter = mAdapter
        binding.editBookViewPager.isUserInputEnabled = false
        binding.editBookViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                binding.editBookNextPage.isVisible = position == PAGE_BOOK_INFO
                binding.editBookPreviousPage.isVisible = position == PAGE_BOOK_REVIEW
                binding.editBookDone.isVisible = position == PAGE_BOOK_REVIEW
                binding.pageIndicator1.isSelected = position == PAGE_BOOK_INFO
                binding.pageIndicator2.isSelected = position == PAGE_BOOK_REVIEW
            }
        })

        binding.editBookNextPage.setOnClickListener { viewModel.onPageChanged(PAGE_BOOK_REVIEW) }
        binding.editBookPreviousPage.setOnClickListener { viewModel.onPageChanged(PAGE_BOOK_INFO) }
        binding.editBookDone.setOnClickListener { viewModel.saveEditedBook() }
    }

    private fun setupFragmentResultListener() {
        supportFragmentManager.setFragmentResultListener(ImagePickerFragment.REQUEST_KEY, this) { _, bundle ->
            bundle
                .getParcelable<Uri>(ImagePickerFragment.URI_KEY)
                .let { viewModel.setImageUrl(it.toString()) }
        }

        supportFragmentManager.setFragmentResultListener(CategoryDatePickerFragment.REQUEST_KEY, this) { _, bundle ->
            bundle
                .getLong(CategoryDatePickerFragment.SELECTED_DATE)
                .takeIf { it > 0 }
                ?.let { viewModel.setDate(LocalDate.ofEpochDay(it)) }
        }
    }

    private fun setupObserver() {
        viewModel.page.observe(this) { page -> binding.editBookViewPager.currentItem = page }

        viewModel.book.observe(this) {
            it.successOrNull()?.let { book -> Timber.debug { "book succeeded" }; viewModel.setBook(book) }?: Timber.debug { "book failed" }
        }

        viewModel.title.observe(this) { title ->
            when {
                title.isNotBlank() -> {
                    binding.editBookNextPage.setColorFilter(ContextCompat.getColor(this, R.color.text_basic))
                    binding.editBookNextPage.setOnClickListener { viewModel.onPageChanged(PAGE_BOOK_REVIEW) }
                }
                else -> {
                    binding.editBookNextPage.setColorFilter(ContextCompat.getColor(this, R.color.text_hint_disable))
                    binding.editBookNextPage.setOnClickListener(null)
                }
            }
        }

        viewModel.canEdit.observe(this) { canEdit ->
            when {
                canEdit -> {
                    binding.editBookDone.setTextColor(ContextCompat.getColor(this, R.color.text_basic))
                    binding.editBookDone.setOnClickListener { viewModel.saveEditedBook() }
                }
                else -> {
                    binding.editBookDone.setTextColor(ContextCompat.getColor(this, R.color.text_hint_disable))
                    binding.editBookDone.setOnClickListener(null)
                }
            }
        }

        viewModel.book.observe(this) { /* trigger */ }

        viewModel.editBookResult.observe(this) { result -> result.successOrNull()?.let { finish() } }
    }

    override fun onBackPressed() {
        supportFragmentManager.findFragmentById(R.id.edit_book_root_layout)
            ?.let { supportFragmentManager.commit { remove(it) }; return }

        super.onBackPressed()
    }

    companion object {
        private const val PAGE_BOOK_INFO = 0
        private const val PAGE_BOOK_REVIEW = 1
        private const val EDIT_BOOK_ID = "EDIT_BOOK_ID"

        fun startActivity(context: Context, id: Long) {
            val i = Intent(context, EditBookActivity::class.java).putExtra(EDIT_BOOK_ID, id)
            context.startActivity(i)
        }
    }
}
