package com.marchlab.haema.ui.main.journal.list

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentJournalBinding
import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.ui.main.MainViewModel
import com.marchlab.haema.ui.main.journal.delete.DeleteJournalDialogFragment
import com.marchlab.haema.ui.main.journal.edit.EditJournalActivity
import com.marchlab.haema.util.extensions.convertDpToPx
import com.marchlab.haema.util.extensions.isGranted
import com.marchlab.haema.util.extensions.smoothScrollTo
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.*
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.scope.viewModel
import org.threeten.bp.LocalDate
import timber.log.Timber
import timber.log.debug
import java.io.File
import java.io.FileOutputStream

@ExperimentalCoroutinesApi
class JournalFragment : Fragment(R.layout.fragment_journal) {

    private val binding by viewBinding(FragmentJournalBinding::bind)

    private val viewModel by lifecycleScope.viewModel<JournalViewModel>(this)

    private val parentViewModel by sharedViewModel<MainViewModel>()

    private lateinit var mAdapter: JournalRecyclerViewAdapter

    private var mJournal: Journal? = null

    private var mAdapterPosition: Int = Int.MIN_VALUE

    private val shareLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        when(result.resultCode) {
            Activity.RESULT_OK -> Toast.makeText(requireContext(), "공유에 성공하였습니다.", Toast.LENGTH_SHORT).show()
            Activity.RESULT_CANCELED -> Toast.makeText(requireContext(), "공유가 취소되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private val permissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
        if(isGranted) shareJournal()
        else Timber.debug { "User denied to grant permission" }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()
    }

    private fun init() {
        mAdapter =
            JournalRecyclerViewAdapter(::onJournalLongClick)
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                (binding.recyclerView.layoutManager as? LinearLayoutManager)?.apply {
                    val first = findFirstVisibleItemPosition()
                    val last = itemCount + first
                    mAdapter.mVisibleRange = first until last
                }
            }
        })
    }

    private fun onJournalLongClick(journal: Journal, adapterPosition: Int) {
        mJournal = journal
        mAdapterPosition = adapterPosition

        parentFragmentManager.beginTransaction()
            .replace(R.id.main_root_layout, JournalDialogFragment::class.java, null, JournalDialogFragment::class.java.simpleName)
            .commit()
    }

    private fun setupObserver() {
        parentViewModel.journals.observe(viewLifecycleOwner) { result ->
            when(result) {
                Result.Loading -> Timber.debug { "journals loading" }
                is Result.Success -> {
                    Timber.debug { "journals success" }

                    binding.journalEmptyLayout.isVisible = result.data.isEmpty()

                    mAdapter.submitList(result.data)
                }
                is Result.Error -> Timber.debug(result.exception) { "journals error" }
            }
        }

        /* TODO(const) */
        parentFragmentManager.setFragmentResultListener("date", viewLifecycleOwner) { _, result ->
            val epochDay = result.getLong("selected_date")

            lifecycle.coroutineScope.launch {
                val index = withContext(Dispatchers.Default) { mAdapter.currentList.indexOfFirst { it.date.toEpochDay() == epochDay } }

                when {
                    index < 0 -> Timber.debug { "journal at ${LocalDate.ofEpochDay(epochDay)} not found" }
                    else -> {
                        delay(250L)
                        binding.recyclerView.smoothScrollTo(index)
                    }
                }
            }
        }

        parentFragmentManager.setFragmentResultListener(JournalDialogFragment.REQUEST_KEY, viewLifecycleOwner) { _, result ->
            when(result.getString(JournalDialogFragment.ACTION_KEY)) {
                JournalDialogFragment.EDIT -> {
                    mJournal?.let { EditJournalActivity.startActivity(requireContext(), it.id) }
                }
                JournalDialogFragment.DELETE -> {
                    val args = bundleOf(DeleteJournalDialogFragment.DELETE_JOURNAL_ID to requireNotNull(mJournal).id)

                    parentFragmentManager.commit {
                        replace(R.id.main_root_layout, DeleteJournalDialogFragment::class.java, args, DeleteJournalDialogFragment::class.java.simpleName)
                    }
                }
                JournalDialogFragment.SHARE -> {
                    if(isGranted(WRITE_EXTERNAL_STORAGE)) shareJournal()
                    else permissionLauncher.launch(WRITE_EXTERNAL_STORAGE)
                }
                else -> { /* IllegalState */ }
            }
        }
    }

    private fun shareJournal() {
        lifecycle.coroutineScope.launch {
            val holder = binding.recyclerView.findViewHolderForAdapterPosition(mAdapterPosition)

            val bitmap = withContext(Dispatchers.Default) { holder?.let { createBitmap(it.itemView) } }

            val uri = withContext(Dispatchers.IO) { bitmap?.let { cacheShareImage(it) } }

            uri?.let { launchShareChooser(it) }
        }
    }

    private fun createBitmap(view: View): Bitmap? {
        val w = view.width
        val h = view.height + requireContext().convertDpToPx(20).toInt()

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)

        view.background
            ?.draw(canvas)
            ?: canvas.drawColor(ContextCompat.getColor(requireContext(), R.color.main_background))

        view.draw(canvas)

        return bitmap
    }

    private suspend fun cacheShareImage(bitmap: Bitmap): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val folder = File(requireContext().cacheDir, "/share")

                folder.mkdirs()

                val file = File(folder, "/${System.currentTimeMillis()}.jpg")

                val stream = FileOutputStream(file)

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

                FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)

            } catch (exception: Exception) {
                Timber.debug(exception) { "saveBitmap(Bitmap) throw exception" }

                null
            }
        }
    }

    private fun launchShareChooser(uri: Uri) {
        val i = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(uri, requireContext().contentResolver.getType(uri))
            putExtra(Intent.EXTRA_STREAM, uri)
        }

        shareLauncher.launch(Intent.createChooser(i, "공유할 앱을 선택해주세요"))
    }
}