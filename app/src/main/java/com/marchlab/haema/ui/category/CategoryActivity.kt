package com.marchlab.haema.ui.category

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.observe
import androidx.viewpager2.widget.ViewPager2
import com.marchlab.haema.R
import com.marchlab.haema.databinding.ActivityCategoryBinding
import com.marchlab.haema.ui.category.book.add.AddBookActivity
import com.marchlab.haema.ui.category.book.list.BookFragment
import com.marchlab.haema.ui.category.movie.add.AddMovieActivity
import com.marchlab.haema.ui.category.movie.list.MovieFragment
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
class CategoryActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityCategoryBinding::inflate)

    private val viewModel: CategoryViewModel by lifecycleScope.viewModel(this)

    private val bookFragment: BookFragment by lifecycleScope.inject()

    private val movieFragment: MovieFragment by lifecycleScope.inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory(lifecycleScope)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()

        setupObserver()
    }

    private fun init() {
        binding.categoryViewPager.adapter = CategoryPagerAdapter(this, listOf(movieFragment, bookFragment))
        binding.categoryViewPager.isUserInputEnabled = false
        binding.categoryViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when(position) {
                    0 -> {
                        binding.categoryTabLayout.updateTransition {
                            connect(R.id.category_tab_indicator, ConstraintSet.END, R.id.category_tab_movie, ConstraintSet.END)
                            connect(R.id.category_tab_indicator, ConstraintSet.START, R.id.category_tab_movie, ConstraintSet.START)
                        }
                        binding.categoryAddBtn.setImageResource(R.drawable.img_add_movie_plus)
                    }
                    1 -> {
                        binding.categoryTabLayout.updateTransition {
                            connect(R.id.category_tab_indicator, ConstraintSet.END, R.id.category_tab_book, ConstraintSet.END)
                            connect(R.id.category_tab_indicator, ConstraintSet.START, R.id.category_tab_book, ConstraintSet.START)
                        }
                        binding.categoryAddBtn.setImageResource(R.drawable.img_add_book_plus)
                    }
                }
            }
        })

        binding.categoryTabMovie.setOnClickListener { binding.categoryViewPager.currentItem = 0 }
        binding.categoryTabBook.setOnClickListener { binding.categoryViewPager.currentItem = 1 }

        binding.categoryAddBtn.setOnClickListener(::onAddClick)

        binding.categoryBackBtn.setOnClickListener { onBackPressed() }
    }

    private fun setupObserver() {
        viewModel.isPurchased.observe(this) { result ->
            when(result) {
                is Result.Success -> {
                    if(!result.data && freeTrialPeriod() <= 0)
                        binding.categoryAddBtn.setOnClickListener { toast { "무료 사용기간이 만료되었습니다. 이용권을 등록해주세요!" } }
                    else
                        binding.categoryAddBtn.setOnClickListener(::onAddClick)
                }
            }
        }
    }

    private fun onAddClick(view: View) = when(binding.categoryViewPager.currentItem) {
        0 -> launchAddMovieActivity()
        1 -> launchAddBookActivity()
        else -> throw IllegalStateException("Unexpected item value ${binding.categoryViewPager.currentItem}")
    }

    private fun launchAddMovieActivity() = startActivity(Intent(this, AddMovieActivity::class.java))

    private fun launchAddBookActivity() = startActivity(Intent(this, AddBookActivity::class.java))
}
