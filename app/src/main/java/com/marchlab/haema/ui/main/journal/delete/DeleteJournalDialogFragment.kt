package com.marchlab.haema.ui.main.journal.delete

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.observe
import com.marchlab.haema.R
import com.marchlab.haema.databinding.DialogDeleteJournalBinding
import com.marchlab.haema.util.extensions.toast
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.Result
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel

class DeleteJournalDialogFragment: DialogFragment(R.layout.dialog_delete_journal) {

    private val binding by viewBinding(DialogDeleteJournalBinding::bind)

    private val viewModel by lifecycleScope.viewModel<DeleteJournalDialogViewModel>(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()
    }

    private fun init() {
        binding.deleteJournalDialogRootLayout.setOnClickListener { dismiss() }

        binding.deleteJournalDialogInnerLayout.setOnClickListener { /* block user input */ }

        binding.deleteJournalDialogCancel.setOnClickListener { dismiss() }

        val id = requireArguments().getLong(DELETE_JOURNAL_ID, -1L)

        binding.deleteJournalDialogDelete.setOnClickListener {
            if(id >0) viewModel.delete(id)
            else toast { "일기 삭제 도중 오류가 발생하였습니다." }
        }
    }

    private fun setupObserver() {
        viewModel.deleteJournalResult.observe(viewLifecycleOwner) { result -> if(result is Result.Success) dismiss() }
    }

    companion object {
        const val DELETE_JOURNAL_ID = "deleteJournalId"
    }
}
