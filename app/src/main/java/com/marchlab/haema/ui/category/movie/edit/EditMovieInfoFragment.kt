package com.marchlab.haema.ui.category.movie.edit

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentEditMovieInfoBinding
import com.marchlab.haema.ui.main.ImagePickerFragment
import com.marchlab.haema.util.extensions.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber
import timber.log.debug

class EditMovieInfoFragment: Fragment(R.layout.fragment_edit_movie_info) {

    private val binding by viewBinding(FragmentEditMovieInfoBinding::bind)

    private val viewModel by lazy { requireParentFragment().getViewModel<EditMovieViewModel>() }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if(isGranted)
            supportFragmentManager.beginTransaction()
                .add(R.id.movie_detail_root_layout, ImagePickerFragment::class.java, null, ImagePickerFragment::class.java.simpleName)
                .commit()
        else
            Timber.debug { "User denied to grant READ_EXTERNAL_STORAGE permission." }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()
    }

    private fun init() {
       /* binding.editMoviePosterImage.updateLayoutParams<ConstraintLayout.LayoutParams> {
            val w = (resources.displayMetrics.widthPixels - requireContext().convertDpToPx(64)) / 3
            val h = (resources.displayMetrics.widthPixels - requireContext().convertDpToPx(24)) / 3
            width = w.roundToInt()
            height = (h * 4 / 3).roundToInt()
        }*/
        binding.editMoviePosterImage.setOnClickListener {
            clearFocusAll()

            if(viewModel.imageUrl.value.isNullOrBlank()) {
                if(isGranted(Manifest.permission.READ_EXTERNAL_STORAGE))
                    lifecycle.coroutineScope.launch {
                        delay(250L)

                        Timber.debug { "parentFragmentManager; $parentFragmentManager in EditBookInfoFragment" }

                        supportFragmentManager.beginTransaction()
                            .add(R.id.movie_detail_root_layout, ImagePickerFragment::class.java, null, ImagePickerFragment::class.java.simpleName)
                            .commit()
                    }
                else
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                binding.editMovieDeleteImageView.isVisible = true
            }
        }

        binding.editMovieDeleteImageView.setOnClickListener {
            viewModel.setImageUrl(null)
            binding.editMovieDeleteImageView.isVisible = false
        }

        binding.editMovieTitleEditText.doAfterTextChanged { title ->
            title?.let { viewModel.setTitle(it.toString()) }
        }

        binding.editMovieReleaseEditText.doAfterTextChanged { release ->
            release?.let { viewModel.setRelease(it.toString()) }
        }

        binding.editMovieDirectorEditText.doAfterTextChanged { director ->
            director?.let { viewModel.setDirector(it.toString()) }
        }

        binding.editMovieActorEditText.doAfterTextChanged { actor ->
            actor?.let { viewModel.setActor(it.toString()) }
        }

        binding.editMovieActorEditText.postDelayed(250L) {
            binding.editMovieActorEditText.requestFocus()
            binding.editMovieActorEditText.setSelection(binding.editMovieActorEditText.text.length) /* place cursor to the end */
            binding.editMovieActorEditText.showKeyboard()
        }

        binding.editMovieTitleEditText.setOnFocusChangeListener(::onFocusChanged)
        binding.editMovieReleaseEditText.setOnFocusChangeListener(::onFocusChanged)
        binding.editMovieDirectorEditText.setOnFocusChangeListener(::onFocusChanged)
        binding.editMovieActorEditText.setOnFocusChangeListener(::onFocusChanged)
    }

    private fun onFocusChanged(view: View, hasFocus: Boolean) {
        if(hasFocus) binding.editMovieDeleteImageView.isVisible = false
    }

    private fun clearFocusAll() {
        binding.editMovieTitleEditText.clearFocus()
        binding.editMovieTitleEditText.hideKeyboard()

        binding.editMovieReleaseEditText.clearFocus()
        binding.editMovieReleaseEditText.hideKeyboard()

        binding.editMovieDirectorEditText.clearFocus()
        binding.editMovieDirectorEditText.hideKeyboard()

        binding.editMovieActorEditText.clearFocus()
        binding.editMovieActorEditText.hideKeyboard()
    }

    private fun setupObserver(){
        viewModel.imageUrl.observe(viewLifecycleOwner) {
            Glide.with(requireContext())
                .load(it?.takeIf { it.isNotBlank() })
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .fallback(R.drawable.img_photo)
                .transform(CenterCrop())
                .into(binding.editMoviePosterImage)
        }
        viewModel.title.observe(viewLifecycleOwner) { if(binding.editMovieTitleEditText.text.toString() != it) binding.editMovieTitleEditText.setText(it) }

        viewModel.release.observe(viewLifecycleOwner) { if(binding.editMovieReleaseEditText.text.toString() != it) binding.editMovieReleaseEditText.setText(it) }

        viewModel.director.observe(viewLifecycleOwner) { if(binding.editMovieDirectorEditText.text.toString() != it) binding.editMovieDirectorEditText.setText(it) }

        viewModel.actor.observe(viewLifecycleOwner) { if(binding.editMovieActorEditText.text.toString() != it) binding.editMovieActorEditText.setText(it) }
    }

}