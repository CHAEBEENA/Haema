package com.marchlab.haema.ui.settings

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentExportBinding
import com.marchlab.haema.util.extensions.convertDpToPx
import com.marchlab.haema.util.extensions.isGranted
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.*
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import timber.log.debug
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

/**
 * export journals to pdf
 */
class ExportFragment : Fragment(R.layout.fragment_export) {

    private val binding by viewBinding(FragmentExportBinding::bind)

    private val viewModel by lifecycleScope.viewModel<ExportViewModel>(this)

    private lateinit var mAdapter: ExportJournalRecyclerViewAdapter

    private var mSnackbar: Snackbar? = null

    private val permissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
        if(isGranted) {
            binding.exportLoading.isVisible = true

            export()
        }
        else
            Timber.debug { "User denied to grant WRITE_EXTERNAL_PERMISSION" }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if(binding.exportLoading.isVisible) {

                mSnackbar?.dismiss()
                mSnackbar = null

                try {
                    lifecycle.coroutineScope.cancel()
                } catch (exception: Exception) {
                    Timber.debug(exception) { "${exception.message}" }
                }
            }

            parentFragmentManager.commit { remove(this@ExportFragment) }

            this.remove()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()
    }

    private fun init() {
        mAdapter = ExportJournalRecyclerViewAdapter()

        binding.exportBackButton.setOnClickListener { parentFragmentManager.beginTransaction().remove(this).commit() }

        binding.exportSaveButton.setOnClickListener {
            if(!isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            else {
                binding.exportLoading.isVisible = true

                export()
            }
        }

        binding.exportLoading.setOnClickListener { /* block user input */ }
    }

    private fun setupObserver() {
        viewModel.snapshotJournals.observe(viewLifecycleOwner) { result -> if(result is Result.Success) mAdapter.submitList(result.data) }
    }

    private fun export() {
        lifecycle.coroutineScope.launch {

            val uri = withContext(Dispatchers.IO) {
                createBitmaps()
                    ?.let { writeToPdf(it) }
                    ?.let { savePdf(it) }
            }

            uri?.let {
                mSnackbar = Snackbar.make(binding.exportRootLayout, "PDF 가 저장되었습니다.", Snackbar.LENGTH_INDEFINITE).apply {
                    setTextColor(Color.WHITE)
                    setActionTextColor(Color.WHITE)
                    setAction("지금보기") { openFile(uri) }
                    show()
                }

                binding.exportLoading.isVisible = false
            }
        }
    }

    private fun openFile(uri: Uri) {
        startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        )
    }

    private fun savePdf(pdf: PdfDocument): Uri? {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            val relativePath = "${Environment.DIRECTORY_DOWNLOADS}/해마"

            val values = ContentValues().apply {
                put(MediaStore.DownloadColumns.DISPLAY_NAME, "${LocalDateTime.now().toFormatString()}.pdf")
                put(MediaStore.DownloadColumns.RELATIVE_PATH, relativePath)
                put(MediaStore.DownloadColumns.IS_PENDING, 1)
            }

            val contentUri = requireContext().contentResolver.insert(collection, values)

            contentUri
                ?.let {requireContext().contentResolver.openOutputStream(it) }
                ?.let { pdf.writeTo(it) }
                ?.also {
                    values.clear()
                    values.put(MediaStore.DownloadColumns.IS_PENDING, 0)
                    requireContext().contentResolver.update(contentUri, values, null, null)
                }

            contentUri
        } else {
            @Suppress("DEPRECATION")
            val pdfsDirectory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path, "해마")

            val pdfToExport = File(pdfsDirectory, "${LocalDateTime.now().toFormatString()}.pdf")

            if(!pdfsDirectory.exists()) {
                pdfsDirectory.mkdirs()
            }

            if(pdfToExport.createNewFile()) {
                pdf.writeTo(FileOutputStream(pdfToExport))

                FileProvider.getUriForFile(requireContext(), requireContext().packageName + ".fileprovider", pdfToExport)
            } else
                null
        }
    }

    private fun LocalDateTime.toFormatString() = "$year-$monthValue-$dayOfMonth-$hour:$minute:$second"

    private fun writeToPdf(bitmaps: List<Bitmap>): PdfDocument {

        val document = PdfDocument()

        val w = requireContext().resources.displayMetrics.widthPixels
        val h = requireContext().resources.displayMetrics.heightPixels

        bitmaps.forEachIndexed { index, bitmap ->
            /* center alignment */
            val left = (w - bitmap.width) / 2f

            val top = (h - bitmap.height) / 2f

            val pageInfo = PdfDocument.PageInfo.Builder(w, h, index).create()

            val page = document.startPage(pageInfo)

            page.canvas.drawBitmap(bitmap, left, top, null)

            document.finishPage(page)
        }

        return document
    }

    @Suppress("DEPRECATION")
    private fun createBitmaps(): List<Bitmap>? {
        return mAdapter.let {

            (0 until it.itemCount).map { position ->

                val holder = it.createViewHolder(binding.exportRootLayout, it.getItemViewType(position))

                it.onBindViewHolder(holder, position)

                holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(binding.exportRootLayout.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )

                holder.itemView.layout(0, 0, holder.itemView.measuredWidth, holder.itemView.measuredHeight)

                val bitmap = Bitmap.createBitmap(
                    holder.itemView.measuredWidth,
                    holder.itemView.measuredHeight + requireContext().convertDpToPx(20).roundToInt(),
                    Bitmap.Config.ARGB_8888
                )

                val canvas = Canvas(bitmap)

                holder.itemView.draw(canvas)

                if(bitmap.height > resources.displayMetrics.heightPixels) {
                    val w = (resources.displayMetrics.heightPixels / bitmap.height.toFloat()) * bitmap.width
                    val h = resources.displayMetrics.heightPixels

                    Bitmap.createScaledBitmap(bitmap, w.toInt(), h - requireContext().convertDpToPx(20).roundToInt(), false)
                } else
                    bitmap
            }
        }
    }
}
