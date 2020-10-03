package com.marchlab.haema.ui.main.journal.edit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.observe
import com.marchlab.haema.R
import com.marchlab.haema.databinding.ActivityEditJournalBinding
import com.marchlab.haema.domain.model.Emotion
import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.ui.main.ImagePickerFragment
import com.marchlab.haema.ui.main.journal.date.JournalDatePickerFragment
import com.marchlab.haema.ui.main.journal.emotion.EmotionFragment
import com.marchlab.haema.util.extensions.*
import com.marchlab.haema.util.result.Result
import com.marchlab.haema.util.result.successOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import org.threeten.bp.LocalDate

@ExperimentalCoroutinesApi
class EditJournalActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityEditJournalBinding::inflate)

    private val viewModel by lifecycleScope.viewModel<EditJournalViewModel>(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory(lifecycleScope)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        intent
            .getLongExtra(JOURNAL_ID, -1L)
            .takeIf { it >= 0 }
            ?.let { viewModel.loadJournal(it) }

        init()

        setupFragmentResultListener()

        setupObserver()
    }

    private fun init() {
        binding.editJournalDateTextView.setOnClickListener {
            lifecycle.coroutineScope.launch {
                binding.editJournalEditText.clearFocus()

                val args = bundleOf(JournalDatePickerFragment.IS_FORCED to false)

                supportFragmentManager.commitDelayed(250L) {
                    replace(R.id.edit_journal_root_layout, JournalDatePickerFragment::class.java, args, JournalDatePickerFragment::class.java.simpleName)
                }
            }
        }

        binding.editJournalEmotionView.setOnClickListener {
            lifecycle.coroutineScope.launch {
                binding.editJournalEditText.clearFocus()

                val args = bundleOf(EmotionFragment.PREVIOUSLY_SELECTED_EMOTION_KEY to viewModel.emotion.value)

                supportFragmentManager.commitDelayed(250L) {
                    replace(R.id.edit_journal_fragment_container, EmotionFragment::class.java, args, EmotionFragment::class.java.simpleName)
                        .runOnCommit {
                            binding.editJournalRootLayout.updateTransition {
                                setGuidelinePercent(R.id.edit_journal_fragment_container_top_guideline, 0.0F)
                                setGuidelinePercent(R.id.edit_journal_fragment_container_bottom_guideline, 1.0F)
                            }
                        }
                }
            }
        }

        binding.editJournalImageView.setOnClickListener {
            binding.editJournalEditText.clearFocus()
            binding.editJournalDeleteImageView.isVisible = true
        }

        binding.editJournalDeleteImageView.setOnClickListener { viewModel.setImageUri(null) }

        binding.editJournalEditText.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus) binding.editJournalEditText.hideKeyboard()
            else binding.editJournalEditText.showKeyboard()
        }
        binding.editJournalEditText.requestFocus()

        binding.editJournalGallery.setOnClickListener {
            lifecycle.coroutineScope.launch {
                binding.editJournalEditText.clearFocus()

                supportFragmentManager.commitDelayed(250L) {
                    replace(R.id.edit_journal_fragment_container, ImagePickerFragment::class.java, null, ImagePickerFragment::class.java.simpleName)
                        .runOnCommit {
                            binding.editJournalRootLayout.updateTransition {
                                setGuidelinePercent(R.id.edit_journal_fragment_container_top_guideline, 0.0F)
                                setGuidelinePercent(R.id.edit_journal_fragment_container_bottom_guideline, 1.0F)
                            }
                        }
                }
            }
        }
    }

    private fun setupFragmentResultListener() {
        supportFragmentManager.setFragmentResultListener(JournalDatePickerFragment.REQUEST_KEY, this) { _, bundle ->
            val epochDay = bundle.getLong(JournalDatePickerFragment.DATE_KEY)

            if(epochDay > 0) viewModel.setDate(LocalDate.ofEpochDay(epochDay))
        }

        supportFragmentManager.setFragmentResultListener(EmotionFragment.REQUEST_KEY, this) { _, bundle ->
            (bundle.get(EmotionFragment.DISPLAY_EMOTION_KEY) as? Emotion)?.let { viewModel.setEmotion(it) }

            (bundle.get(EmotionFragment.SELECTED_EMOTION_KEY) as? Emotion)
                ?.let { viewModel.setEmotion(it) }
                ?.also { removeEmotionFragment() }
        }

        supportFragmentManager.setFragmentResultListener(ImagePickerFragment.REQUEST_KEY, this) { _, bundle ->
            val uri = bundle.getParcelable<Uri>(ImagePickerFragment.URI_KEY)

            viewModel.setImageUri(uri)
        }
    }

    private fun setupObserver() {
        viewModel.journal.observe(this) { result -> result.successOrNull()?.let { onBind(it) } }

        viewModel.date.observe(this) { date -> binding.editJournalDateTextView.text = date.toFormatString() }

        viewModel.emotion.observe(this) { emotion ->
            binding.editJournalEmotionView.setAnimation(emotion.rawResId)
            binding.editJournalEmotionView.playAnimation()
        }

        viewModel.uri.observe(this) { uri ->
            binding.editJournalMaskingTape.isVisible = uri != null
            binding.editJournalImageView.isVisible = uri != null
            uri?.let { binding.editJournalImageView.loadJournalImage(it) }
        }

        viewModel.editJournalResult.observe(this) { result ->
            binding.editJournalLoadingView.isVisible = result == Result.Loading
            result.successOrNull()?.run { finish() }
        }

        viewModel.editedJournal.observe(this) { editedJournal ->
            binding.editJournalComplete.setOnClickListener { viewModel.onComplete(editedJournal) }
        }
    }

    private fun onBind(journal: Journal) = binding.apply {
        editJournalDateTextView.text = journal.date.toFormatString()

        binding.editJournalEmotionView.setAnimation(journal.emotion.rawResId)
        binding.editJournalEmotionView.playAnimation()

        binding.editJournalMaskingTape.isVisible = journal.imageUri != null
        binding.editJournalImageView.isVisible = journal.imageUri != null
        journal.imageUri?.let { binding.editJournalImageView.loadJournalImage(it) }

        binding.editJournalEditText.setText(journal.content)
        binding.editJournalEditText.doAfterTextChanged { text -> viewModel.setContent(text.toString()) }
    }

    private fun removeEmotionFragment() {
        lifecycle.coroutineScope.launch {
            binding.editJournalRootLayout.updateTransition {
                setGuidelinePercent(R.id.edit_journal_fragment_container_top_guideline, 1.0F)
                setGuidelinePercent(R.id.edit_journal_fragment_container_bottom_guideline, 2.0F)
            }

            supportFragmentManager.findFragmentById(R.id.edit_journal_root_layout)
                ?.let { supportFragmentManager.commitDelayed(250L) { remove(it).runOnCommit { binding.editJournalEditText.requestFocus() } } }
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.findFragmentById(R.id.edit_journal_root_layout)
            ?.let { supportFragmentManager.commit { remove(it) }; return }

        supportFragmentManager.findFragmentById(R.id.edit_journal_fragment_container)
            ?.let {
                lifecycle.coroutineScope.launch {
                    binding.editJournalRootLayout.updateTransition {
                        setGuidelinePercent(R.id.edit_journal_fragment_container_top_guideline, 1.0F)
                        setGuidelinePercent(R.id.edit_journal_fragment_container_bottom_guideline, 2.0F)
                    }

                    supportFragmentManager.commitDelayed(250L) { remove(it).runOnCommit { binding.editJournalEditText.requestFocus() } }
                }
                return
            }

        super.onBackPressed()
    }

    companion object {

        private const val JOURNAL_ID = "JOURNAL_ID"

        fun startActivity(context: Context, id: Long)
                = context.startActivity(Intent(context, EditJournalActivity::class.java).putExtra(JOURNAL_ID, id))
    }
}
