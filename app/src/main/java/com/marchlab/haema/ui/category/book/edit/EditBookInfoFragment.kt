package com.marchlab.haema.ui.category.book.edit

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.observe
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentEditBookInfoBinding
import com.marchlab.haema.ui.main.ImagePickerFragment
import com.marchlab.haema.util.extensions.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@ExperimentalCoroutinesApi
class EditBookInfoFragment: Fragment(R.layout.fragment_edit_book_info) {

    private val binding by viewBinding(FragmentEditBookInfoBinding::bind)

    private val viewModel by sharedViewModel<EditBookViewModel>()

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if(isGranted)
            supportFragmentManager.commit {
                replace(R.id.edit_book_root_layout, ImagePickerFragment::class.java, null, ImagePickerFragment::class.java.simpleName)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()
    }

    private fun init() {
        binding.editBookImageView.setOnClickListener {
            lifecycle.coroutineScope.launch {
                binding.editBookTitleEditText.clearFocus()
                binding.editBookAuthorEditText.clearFocus()
                binding.editBookPublisherEditText.clearFocus()

                binding.editBookDeleteImageView.isVisible = !viewModel.imageUrl.value.isNullOrBlank()
                if(binding.editBookDeleteImageView.isVisible) return@launch

                when {
                    isGranted(READ_EXTERNAL_STORAGE) -> {
                        parentFragmentManager.commitDelayed(250L) {
                            replace(R.id.edit_book_root_layout, ImagePickerFragment::class.java, null, ImagePickerFragment::class.java.simpleName)
                        }
                    }
                    else -> requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
                }
            }
        }

        binding.editBookDeleteImageView.setOnClickListener {
            viewModel.setImageUrl(null)
            binding.editBookDeleteImageView.isVisible = false
        }

        binding.editBookTitleEditText.doAfterTextChanged { title ->
            title?.let { viewModel.setTitle(it.toString()) }
        }

        binding.editBookAuthorEditText.doAfterTextChanged { author ->
            author?.let { viewModel.setAuthor(it.toString()) }
        }

        binding.editBookPublisherEditText.doAfterTextChanged { publisher ->
            publisher?.let { viewModel.setPublisher(it.toString()) }
        }

        binding.editBookPublisherEditText.postDelayed(250L) {
            binding.editBookPublisherEditText.requestFocus()
            binding.editBookPublisherEditText.setSelection(binding.editBookPublisherEditText.text.length)
            binding.editBookPublisherEditText.showKeyboard()
        }

        binding.editBookTitleEditText.setOnFocusChangeListener { _, hasFocus ->
            when {
                hasFocus -> {
                    binding.editBookDeleteImageView.isVisible = false
                    binding.editBookTitleEditText.showKeyboard()
                }
                else -> binding.editBookTitleEditText.hideKeyboard()
            }
        }

        binding.editBookAuthorEditText.setOnFocusChangeListener { _, hasFocus ->
            when {
                hasFocus -> {
                    binding.editBookDeleteImageView.isVisible = false
                    binding.editBookAuthorEditText.showKeyboard()
                }
                else -> binding.editBookAuthorEditText.hideKeyboard()
            }
        }

        binding.editBookPublisherEditText.setOnFocusChangeListener { _, hasFocus ->
            when {
                hasFocus -> {
                    binding.editBookDeleteImageView.isVisible = false
                    binding.editBookPublisherEditText.showKeyboard()
                }
                else -> binding.editBookPublisherEditText.hideKeyboard()
            }
        }
    }

    private fun setupObserver() {
        viewModel.imageUrl.observe(viewLifecycleOwner) { imageUrl -> binding.editBookImageView.loadBookImage(imageUrl) }

        viewModel.title.observe(viewLifecycleOwner) {
            if(binding.editBookTitleEditText.text.toString() != it) binding.editBookTitleEditText.setText(it)
        }

        viewModel.author.observe(viewLifecycleOwner) {
            if(binding.editBookAuthorEditText.text.toString() != it) binding.editBookAuthorEditText.setText(it)
        }

        viewModel.publisher.observe(viewLifecycleOwner) {
            if(binding.editBookPublisherEditText.text.toString() != it) binding.editBookPublisherEditText.setText(it)
        }
    }
}
