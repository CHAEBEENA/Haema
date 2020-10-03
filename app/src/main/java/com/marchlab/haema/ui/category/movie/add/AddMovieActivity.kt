package com.marchlab.haema.ui.category.movie.add

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.observe
import androidx.viewpager2.widget.ViewPager2
import com.marchlab.haema.R
import com.marchlab.haema.databinding.ActivityAddMovieBinding
import com.marchlab.haema.ui.category.CategoryDatePickerFragment
import com.marchlab.haema.ui.main.ImagePickerFragment
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.Result
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import org.threeten.bp.LocalDate
import timber.log.Timber
import timber.log.debug

class AddMovieActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityAddMovieBinding::inflate)

    private val viewModel by lifecycleScope.viewModel<AddMovieViewModel>(this)

    private val searchBookFragment: SearchMovieFragment by lifecycleScope.inject()

    private val addMovieInfoFragment: AddMovieInfoFragment by lifecycleScope.inject()

    private val addMoviePersonalFragment: AddMoviePersonalFragment by lifecycleScope.inject()

    private val addMovieReviewFragment: AddMovieReviewFragment by lifecycleScope.inject()

    private lateinit var mAdapter : AddMoviePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()

        setupObserver()

        setupFragmentResultListener()
    }

    private fun init() {

        mAdapter = AddMoviePagerAdapter(this, listOf(searchBookFragment, addMovieInfoFragment, addMoviePersonalFragment, addMovieReviewFragment))

        binding.addMovieViewpager.adapter = mAdapter

        binding.addMovieViewpager.isUserInputEnabled = false
        binding.addMovieViewpager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                when(position) {
                    0 -> {
                        binding.addMoviePageNavigator.isVisible = false
                        viewModel.clear()
                    }
                    1 -> {
                        binding.addMoviePageNavigator.isVisible = true
                        binding.addMoviePreviousPage.isVisible = false
                        binding.addMovieDone.isVisible = false
                        binding.addMovieSkip.isVisible = false
                        binding.addMovieNextPage.isVisible = true

                        binding.addMoviePageIndicator1.background = ContextCompat.getDrawable(this@AddMovieActivity, R.drawable.indicator_category_view_pager_selected)
                        binding.addMoviePageIndicator2.background = ContextCompat.getDrawable(this@AddMovieActivity, R.drawable.indicator_category_view_pager_not_selected)
                        binding.addMoviePageIndicator3.background = ContextCompat.getDrawable(this@AddMovieActivity, R.drawable.indicator_category_view_pager_not_selected)
                    }
                    2 -> {
                        binding.addMoviePreviousPage.isVisible = true
                        binding.addMovieDone.isVisible = false
                        binding.addMovieSkip.isVisible = viewModel.skipFilled.value != true
                        binding.addMovieNextPage.isVisible = viewModel.skipFilled.value == true

                        binding.addMoviePageIndicator1.background = ContextCompat.getDrawable(this@AddMovieActivity, R.drawable.indicator_category_view_pager_not_selected)
                        binding.addMoviePageIndicator2.background = ContextCompat.getDrawable(this@AddMovieActivity, R.drawable.indicator_category_view_pager_selected)
                        binding.addMoviePageIndicator3.background = ContextCompat.getDrawable(this@AddMovieActivity, R.drawable.indicator_category_view_pager_not_selected)
                    }
                    3 -> {
                        binding.addMovieDone.isVisible = true
                        binding.addMovieNextPage.isVisible = false
                        binding.addMovieSkip.isVisible = false

                        binding.addMoviePageIndicator1.background = ContextCompat.getDrawable(this@AddMovieActivity, R.drawable.indicator_category_view_pager_not_selected)
                        binding.addMoviePageIndicator2.background = ContextCompat.getDrawable(this@AddMovieActivity, R.drawable.indicator_category_view_pager_not_selected)
                        binding.addMoviePageIndicator3.background = ContextCompat.getDrawable(this@AddMovieActivity, R.drawable.indicator_category_view_pager_selected)
                    }

                }

            }
        })

        binding.addMoviePreviousPage.setOnClickListener {
            if(binding.addMovieViewpager.currentItem != 0) {
                var page = binding.addMovieViewpager.currentItem
                viewModel.onPageChange(--page)
            }
        }

        binding.addMovieNextPage.setOnClickListener {
            if(binding.addMovieViewpager.currentItem != 3) {
                var page = binding.addMovieViewpager.currentItem
                viewModel.onPageChange(++page)
            }
        }

        binding.addMovieSkip.setOnClickListener {
            viewModel.onPageChange(3)
        }

        binding.addMovieDone.setOnClickListener { viewModel.onComplete() }

    }

    private fun setupObserver() {
        viewModel.page.observe(this) {
            binding.addMovieViewpager.currentItem = it
        }

        viewModel.title.observe(this) {
            val color = if(it.isNotBlank()) R.color.text_basic else R.color.text_hint_disable
            binding.addMovieNextPage.isClickable = it.isNotBlank()
            binding.addMovieNextPage.setColorFilter(ContextCompat.getColor(this, color))
        }

        viewModel.skipFilled.observe(this) {
            binding.addMovieSkip.isVisible = !it
            binding.addMovieNextPage.isVisible = it
        }

        viewModel.addMovieResult.observe(this) { result ->
            when(result) {
                Result.Loading -> Unit
                is Result.Success -> finish()
                is Result.Error -> Timber.debug { "Add Movie throw except${result.exception.message}" }
            }
        }
    }

    private fun setupFragmentResultListener() {
        supportFragmentManager.setFragmentResultListener(ImagePickerFragment.REQUEST_KEY, this, ::onImageSelected)
    }

    private fun onImageSelected(resultKey: String, bundle: Bundle) {
        supportFragmentManager.findFragmentByTag(ImagePickerFragment::class.java.simpleName)
            ?.takeIf { it.isAdded && !it.isHidden }
            ?.let {
                supportFragmentManager.beginTransaction()
                    .hide(it)
                    .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                    .commit()
            }

        val uri = bundle.getParcelable<Uri>(ImagePickerFragment.URI_KEY)

        viewModel.setImageUrl(uri.toString())
    }

    override fun onBackPressed() {
        supportFragmentManager.findFragmentByTag(ImagePickerFragment::class.java.simpleName)
            ?.let {
                supportFragmentManager.beginTransaction().remove(it).commit()
                return
            }

        supportFragmentManager.findFragmentByTag(CategoryDatePickerFragment::class.java.simpleName)
            ?.let {
                supportFragmentManager.beginTransaction().remove(it).commit()
                return
            }

        if(binding.addMovieViewpager.currentItem > 0) {
            binding.addMovieViewpager.currentItem -= 1
            return
        }
        super.onBackPressed()
    }
}
