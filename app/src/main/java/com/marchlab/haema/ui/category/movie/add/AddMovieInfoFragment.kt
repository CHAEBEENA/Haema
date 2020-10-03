package com.marchlab.haema.ui.category.movie.add

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentAddMovieInfoBinding
import com.marchlab.haema.ui.main.ImagePickerFragment
import com.marchlab.haema.util.extensions.hideKeyboard
import com.marchlab.haema.util.extensions.isGranted
import com.marchlab.haema.util.extensions.showKeyboard
import com.marchlab.haema.util.extensions.viewBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import timber.log.debug

class AddMovieInfoFragment : Fragment(R.layout.fragment_add_movie_info) {

    private val binding by viewBinding(FragmentAddMovieInfoBinding::bind)

    private val viewModel: AddMovieViewModel by sharedViewModel()

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if(isGranted) {
            parentFragmentManager.beginTransaction()
                .add(R.id.add_book_root_layout, ImagePickerFragment::class.java, null, ImagePickerFragment::class.java.simpleName)
                .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                .commit()
        }
        else Timber.debug { "READ_EXTERNAL_PERMISSION denied." }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()
    }

    private fun init() {

        binding.addMovieTitleEditText.doAfterTextChanged { title ->
            title?.let { viewModel.setTitle(it.toString()) }
        }

        binding.addMovieReleaseEditText.doAfterTextChanged { release ->
            release?.let { viewModel.setRelease(it.toString()) }
        }

        binding.addMovieDirectorEditText.doAfterTextChanged { director ->
            director?.let { viewModel.setDirector(it.toString()) }
        }

        binding.addMovieActorEditText.doAfterTextChanged { actor ->
            actor?.let { viewModel.setActor(it.toString()) }
        }

        binding.addMoviePosterImage.setOnClickListener {
            clearFocusAll()

            if(viewModel.imageUrl.value.isNullOrBlank()) {
                if(isGranted(Manifest.permission.READ_EXTERNAL_STORAGE))
                    lifecycleScope.launch {
                        delay(250L)

                        parentFragmentManager.beginTransaction()
                            .add(R.id.add_movie_root_layout, ImagePickerFragment::class.java, null, ImagePickerFragment::class.java.simpleName)
                            .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                            .commit()
                    }
                else
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                binding.addMovieDeleteImageView.isVisible = true
            }
        }

        binding.addMovieDeleteImageView.setOnClickListener {
            viewModel.setImageUrl(null)
            binding.addMovieDeleteImageView.isVisible = false
        }
    }

    private fun clearFocusAll() {
        binding.addMovieTitleEditText.clearFocus()
        binding.addMovieTitleEditText.hideKeyboard()

        binding.addMovieReleaseEditText.clearFocus()
        binding.addMovieReleaseEditText.hideKeyboard()

        binding.addMovieDirectorEditText.clearFocus()
        binding.addMovieDirectorEditText.hideKeyboard()

        binding.addMovieActorEditText.clearFocus()
        binding.addMovieActorEditText.hideKeyboard()
    }

    private fun setupObserver(){
        viewModel.page.observe(viewLifecycleOwner) { page ->
            if(page == 1) {
                if(viewModel.title.value.isNullOrBlank()) {
                    binding.addMovieTitleEditText.post {
                        binding.addMovieTitleEditText.requestFocus()
                        binding.addMovieTitleEditText.showKeyboard()
                    }
                } else {
                    binding.addMovieActorEditText.post {
                        binding.addMovieActorEditText.requestFocus()
                        binding.addMovieActorEditText.setSelection(binding.addMovieActorEditText.text.length) /* place cursor to the end */
                        binding.addMovieActorEditText.showKeyboard()
                    }
                }
            }
        }

        viewModel.imageUrl.observe(viewLifecycleOwner) {
            Glide.with(requireContext())
                .load(it?.takeIf { it.isNotBlank() })
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .fallback(R.drawable.img_photo)
                .transform(CenterCrop())
                .into(binding.addMoviePosterImage)
        }
        viewModel.title.observe(viewLifecycleOwner) { if(binding.addMovieTitleEditText.text.toString() != it) binding.addMovieTitleEditText.setText(it) }

        viewModel.release.observe(viewLifecycleOwner) { if(binding.addMovieReleaseEditText.text.toString() != it) binding.addMovieReleaseEditText.setText(it) }

        viewModel.director.observe(viewLifecycleOwner) { if(binding.addMovieDirectorEditText.text.toString() != it) binding.addMovieDirectorEditText.setText(it) }

        viewModel.actor.observe(viewLifecycleOwner) { if(binding.addMovieActorEditText.text.toString() != it) binding.addMovieActorEditText.setText(it) }
    }


}
