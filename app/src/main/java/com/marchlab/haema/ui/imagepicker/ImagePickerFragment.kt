package com.marchlab.haema.ui.main

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.observe
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentImagePickerBinding
import com.marchlab.haema.ui.imagepicker.ImagePickerAdapter
import com.marchlab.haema.ui.imagepicker.ImagePickerViewModel
import com.marchlab.haema.util.extensions.*
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import kotlin.math.max
import kotlin.math.min

class ImagePickerFragment: Fragment(R.layout.fragment_image_picker), LoaderManager.LoaderCallbacks<Cursor> {

    private val binding by viewBinding(FragmentImagePickerBinding::bind)

    private val viewModel by lifecycleScope.viewModel<ImagePickerViewModel>(this)

    private lateinit var mAdapter: ImagePickerAdapter

    private var selectedUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()
    }

    private fun init() {
        binding.navigateBack.setOnClickListener {
            parentFragmentManager.commit { remove(this@ImagePickerFragment) }
        }

        binding.upload.setOnClickListener {
            selectedUri
                ?.let {
                    parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(URI_KEY to it))
                    parentFragmentManager.commit { remove(this@ImagePickerFragment) }
                    return@setOnClickListener
                }
                ?: toast { "사진을 선택해주세요." }
        }

        mAdapter = ImagePickerAdapter { uri -> selectedUri = uri }
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView.onActionMove = ::onActionMove
        binding.recyclerView.onActionUp = ::onActionUp

        binding.imagePickerRootLayout.setOnClickListener { /* block user input */ }
    }

    private fun setupObserver() {
        viewModel.imageUris.observe(viewLifecycleOwner) { uris -> mAdapter.submitList(uris) }
    }

    private fun onActionMove(percent: Float) {
        val guidePercent = min(0.5F, max(0.0F, binding.topGuideline.guidePercent + percent))

        binding.recyclerView.guidePercent = guidePercent

        binding.imagePickerRootLayout.update {
            binding.topGuideline.setGuidelinePercent(guidePercent)
            binding.bottomGuideline.setGuidelinePercent(guidePercent + 1.0F)
        }
    }

    private fun onActionUp(percent: Float) {
        val direction = if(percent < 0) 0.0F else 0.5F

        binding.recyclerView.guidePercent = direction

        binding.imagePickerRootLayout.updateTransition {
            setGuidelinePercent(R.id.top_guideline, direction)
            setGuidelinePercent(R.id.bottom_guideline, direction + 1.0F)
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        if(id != LOADER_ID) throw IllegalStateException("unexpected loader id: $id")

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(MediaStore.Images.Media._ID)

        val sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"

        return CursorLoader(requireContext(), uri, projection, null, null, sortOrder)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        data ?: return

        val columnIndex = data.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

        val uris = mutableListOf<Uri>()

        while(data.moveToNext()) {
            val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, data.getLong(columnIndex))
            uris.add(contentUri)
        }

        if(uris.isNotEmpty()) onUrisLoaded(uris)
    }

    private fun onUrisLoaded(uris: List<Uri>) { viewModel.onUriLoaded(uris) }

    override fun onLoaderReset(loader: Loader<Cursor>) {}

    companion object {

        private const val LOADER_ID = 3

        const val REQUEST_KEY = "IMAGE_PICKER_REQUEST_KEY"

        const val URI_KEY = "URI_KEY"
    }
}