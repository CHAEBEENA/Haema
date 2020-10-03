package com.marchlab.haema.ui.category.movie.edit

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.observe
import androidx.viewpager2.widget.ViewPager2
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentEditMovieBinding
import com.marchlab.haema.domain.model.Movie
import com.marchlab.haema.ui.category.CategoryDatePickerFragment
import com.marchlab.haema.ui.category.movie.detail.MovieDetailViewModel
import com.marchlab.haema.ui.main.ImagePickerFragment
import com.marchlab.haema.util.extensions.supportFragmentManager
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.Result
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.scope.viewModel
import org.threeten.bp.LocalDate


class EditMovieFragment: Fragment(R.layout.fragment_edit_movie) {

    private val binding by viewBinding(FragmentEditMovieBinding::bind)

    private val editMovieInfoFragment by lifecycleScope.inject<EditMovieInfoFragment>()

    private val editMoviePersonalFragment by lifecycleScope.inject<EditMoviePersonalFragment>()

    private val editMovieReviewFragment by lifecycleScope.inject<EditMovieReviewFragment>()

    private val parentViewModel by sharedViewModel<MovieDetailViewModel>()

    private val viewModel by lifecycleScope.viewModel<EditMovieViewModel>(this)

    private lateinit var mAdapter: EditMoviePagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupFragmentResultListener()

        setupObserver()

    }

    private fun init() {

        mAdapter = EditMoviePagerAdapter(this, listOf(editMovieInfoFragment, editMoviePersonalFragment, editMovieReviewFragment))
        binding.editMovieViewpager.adapter = mAdapter
        binding.editMovieViewpager.isUserInputEnabled = false
        binding.editMovieViewpager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when(position) {
                    0 -> {
                        binding.editMoviePreviousPage.isVisible = false
                        binding.editMovieDone.isVisible = false
                        binding.editMovieSkip.isVisible = false
                        binding.editMovieNextPage.isVisible = true

                        binding.pageIndicator1.background = ContextCompat.getDrawable(requireContext(), R.drawable.indicator_category_view_pager_selected)
                        binding.pageIndicator2.background = ContextCompat.getDrawable(requireContext(), R.drawable.indicator_category_view_pager_not_selected)
                        binding.pageIndicator3.background = ContextCompat.getDrawable(requireContext(), R.drawable.indicator_category_view_pager_not_selected)
                    }
                    1 -> {
                        binding.editMoviePreviousPage.isVisible = true
                        binding.editMovieDone.isVisible = false
                        binding.editMovieSkip.isVisible = viewModel.skipFilled.value != true
                        binding.editMovieNextPage.isVisible = viewModel.skipFilled.value == true

                        binding.pageIndicator1.background = ContextCompat.getDrawable(requireContext(), R.drawable.indicator_category_view_pager_not_selected)
                        binding.pageIndicator2.background = ContextCompat.getDrawable(requireContext(), R.drawable.indicator_category_view_pager_selected)
                        binding.pageIndicator3.background = ContextCompat.getDrawable(requireContext(), R.drawable.indicator_category_view_pager_not_selected)
                    }
                    2 -> {
                        binding.editMovieDone.isVisible = true
                        binding.editMovieNextPage.isVisible = false
                        binding.editMovieSkip.isVisible = false
                        viewModel.rating.value?.let {
                            binding.editMovieDone.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_basic))
                        } ?: binding.editMovieDone.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint_disable))

                        binding.pageIndicator1.background = ContextCompat.getDrawable(requireContext(), R.drawable.indicator_category_view_pager_not_selected)
                        binding.pageIndicator2.background = ContextCompat.getDrawable(requireContext(), R.drawable.indicator_category_view_pager_not_selected)
                        binding.pageIndicator3.background = ContextCompat.getDrawable(requireContext(), R.drawable.indicator_category_view_pager_selected)
                    }

                }
            }
        })

        binding.editMoviePreviousPage.setOnClickListener {
            if(binding.editMovieViewpager.currentItem != 0)
                binding.editMovieViewpager.currentItem--
        }

        binding.editMovieNextPage.setOnClickListener {
            if(binding.editMovieViewpager.currentItem != 2)
                binding.editMovieViewpager.currentItem++
        }

        binding.editMovieSkip.setOnClickListener {
            binding.editMovieViewpager.currentItem = 2
        }

        binding.editMovieDone.setOnClickListener {
            parentViewModel.editMovie(createMovieInstance(
                viewModel.id.value,
                viewModel.imageUrl.value,
                viewModel.title.value,
                viewModel.release.value,
                viewModel.director.value,
                viewModel.actor.value,
                viewModel.date.value,
                viewModel.place.value,
                viewModel.people.value,
                viewModel.rating.value,
                viewModel.review.value
            ))
        }
    }

    private fun setupFragmentResultListener() {
        supportFragmentManager.setFragmentResultListener(ImagePickerFragment.REQUEST_KEY, viewLifecycleOwner, ::onImageSelected)

        supportFragmentManager.setFragmentResultListener(CategoryDatePickerFragment.REQUEST_KEY, viewLifecycleOwner, ::onDateSelected)
    }

    private fun setupObserver() {

        when(val result = parentViewModel.movie.value) {
            Result.Loading -> { Toast.makeText(requireContext(), "비정상적인 접근", Toast.LENGTH_SHORT).show() }
            is Result.Success -> { viewModel.setMovie(result.data) }
            is Result.Error -> { Toast.makeText(requireContext(), "비정상적인 접근", Toast.LENGTH_SHORT).show() }
        }

        viewModel.title.observe(viewLifecycleOwner) {
            val color = if(it.isNotBlank()) R.color.text_basic else R.color.text_hint_disable
            binding.editMovieNextPage.isClickable = it.isNotBlank()
            binding.editMovieNextPage.setColorFilter(ContextCompat.getColor(requireContext(), color))
        }

        viewModel.skipFilled.observe(viewLifecycleOwner) {
            binding.editMovieSkip.isVisible = !it
            binding.editMovieNextPage.isVisible = it
        }

    }

    private fun onImageSelected(resultKey: String, bundle: Bundle) {
        val uri = bundle.getParcelable<Uri>(ImagePickerFragment.URI_KEY)

        viewModel.setImageUrl(uri?.toString())

        supportFragmentManager.findFragmentByTag(ImagePickerFragment::class.java.simpleName)
            ?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
    }

    private fun onDateSelected(resultKey: String, bundle: Bundle) {
        val epochDay = bundle.getLong(CategoryDatePickerFragment.SELECTED_DATE)
        if(epochDay > 0) viewModel.setDate(LocalDate.ofEpochDay(epochDay))

    }

    private fun createMovieInstance(id: Long?, imageUrl: String?, title: String?, release: String?, director: String?, actor: String?, date: LocalDate?, place: String?, people: String?, rating: Int?, review: String?): Movie {
        return Movie(id ?: 0L,
            imageUrl ?: "",
            title ?: "",
            release ?: "",
            director ?: "",
            actor ?: "",
            date ?: LocalDate.now(),
            place ?: "",
            people ?: "",
            rating ?: 0,
            review ?: ""
        )
    }
}

