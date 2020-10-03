package com.marchlab.haema.ui.category.book.add

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentAddBookInfoBinding
import com.marchlab.haema.ui.main.ImagePickerFragment
import com.marchlab.haema.util.extensions.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddBookInfoFragment : Fragment(R.layout.fragment_add_book_info) {

    private val binding by viewBinding(FragmentAddBookInfoBinding::bind)

    private val viewModel: AddBookViewModel by sharedViewModel()

    private val permissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
        if(isGranted)
            parentFragmentManager.commit {
                replace(R.id.add_book_root_layout, ImagePickerFragment::class.java, null, ImagePickerFragment::class.java.simpleName)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupFragmentResultListener()

        setupObserver()
    }

    private fun init() {

        binding.addBookTitleEditText.doAfterTextChanged { title -> title?.let { viewModel.setTitle(it.toString()) } }

        binding.addBookAuthorEditText.doAfterTextChanged { author -> author?.let { viewModel.setAuthor(it.toString()) } }

        binding.addBookPublisherEditText.doAfterTextChanged { publisher -> publisher?.let { viewModel.setPublisher(it.toString()) } }

        binding.addBookImageView.setOnClickListener {
            lifecycle.coroutineScope.launch {
                binding.addBookTitleEditText.clearFocus()
                binding.addBookAuthorEditText.clearFocus()
                binding.addBookPublisherEditText.clearFocus()

                binding.addBookDeleteImageView.isVisible = !viewModel.imageUrl.value.isNullOrBlank()
                if(binding.addBookDeleteImageView.isVisible) return@launch

                when {
                    isGranted(READ_EXTERNAL_STORAGE) -> parentFragmentManager.commitDelayed(250L) {
                        replace(R.id.add_book_root_layout, ImagePickerFragment::class.java, null, ImagePickerFragment::class.java.simpleName)
                    }
                    else -> permissionLauncher.launch(READ_EXTERNAL_STORAGE)
                }
            }
        }

        binding.addBookDeleteImageView.setOnClickListener {
            viewModel.setImageUrl(null)
            binding.addBookDeleteImageView.isVisible = false
        }

        binding.addBookTitleEditText.setOnFocusChangeListener { _, hasFocus ->
            when {
                hasFocus -> {
                    binding.addBookDeleteImageView.isVisible = false
                    binding.addBookTitleEditText.showKeyboard()
                }
                else -> binding.addBookTitleEditText.hideKeyboard()
            }
        }
        binding.addBookAuthorEditText.setOnFocusChangeListener { _, hasFocus ->
            when {
                hasFocus -> {
                    binding.addBookDeleteImageView.isVisible = false
                    binding.addBookAuthorEditText.showKeyboard()
                }
                else -> binding.addBookAuthorEditText.hideKeyboard()
            }
        }
        binding.addBookPublisherEditText.setOnFocusChangeListener { _, hasFocus ->
            when {
                hasFocus -> {
                    binding.addBookDeleteImageView.isVisible = false
                    binding.addBookPublisherEditText.showKeyboard()
                }
                else -> binding.addBookPublisherEditText.hideKeyboard()
            }
        }
    }

    private fun setupFragmentResultListener() {
        parentFragmentManager.setFragmentResultListener(ImagePickerFragment.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            bundle.getParcelable<Uri>(ImagePickerFragment.URI_KEY)
                ?.let { viewModel.setImageUrl(it.toString()) }
        }
    }

    private fun setupObserver() {
        viewModel.page.observe(viewLifecycleOwner) { page ->
            if(page == 1) {
                when {
                    viewModel.title.value.isNullOrBlank() -> binding.addBookTitleEditText.post { binding.addBookTitleEditText.requestFocus() }

                    else -> binding.addBookPublisherEditText.post {
                        binding.addBookPublisherEditText.requestFocus()
                        binding.addBookPublisherEditText.setSelection(binding.addBookPublisherEditText.text.length) /* place cursor to the end */
                    }
                }
            }
        }

        viewModel.imageUrl.observe(viewLifecycleOwner) { binding.addBookImageView.loadBookImage(it) }

        viewModel.title.observe(viewLifecycleOwner) { if(binding.addBookTitleEditText.text.toString() != it) binding.addBookTitleEditText.setText(it) }

        viewModel.author.observe(viewLifecycleOwner) { if(binding.addBookAuthorEditText.text.toString() != it) binding.addBookAuthorEditText.setText(it) }

        viewModel.publisher.observe(viewLifecycleOwner) { if(binding.addBookPublisherEditText.text.toString() != it) binding.addBookPublisherEditText.setText(it) }
    }
}