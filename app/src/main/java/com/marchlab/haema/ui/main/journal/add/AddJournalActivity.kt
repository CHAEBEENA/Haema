package com.marchlab.haema.ui.main.journal.add

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.observe
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.marchlab.haema.R
import com.marchlab.haema.databinding.ActivityAddJournalBinding
import com.marchlab.haema.domain.model.Emotion
import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.ui.main.ImagePickerFragment
import com.marchlab.haema.ui.main.journal.date.JournalDatePickerFragment
import com.marchlab.haema.ui.main.journal.emotion.EmotionFragment
import com.marchlab.haema.util.extensions.*
import com.marchlab.haema.util.result.successOrNull
import kotlinx.coroutines.*
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import org.threeten.bp.LocalDate
import timber.log.Timber
import timber.log.debug
import java.io.File
import java.io.FileOutputStream
import kotlin.properties.Delegates

@ExperimentalCoroutinesApi
class AddJournalActivity: AppCompatActivity() {

    private val binding by viewBinding(ActivityAddJournalBinding::inflate)

    private val viewModel: AddJournalViewModel by lifecycleScope.viewModel(this)

    private val today = LocalDate.now()

    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
        if(isGranted)
            supportFragmentManager.commit {
                replace(R.id.add_journal_root_layout, ImagePickerFragment::class.java, null, ImagePickerFragment::class.java.simpleName)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory(lifecycleScope)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        intent
            .getLongExtra("date", today.toEpochDay())
            .let { viewModel.setDate(LocalDate.ofEpochDay(it)) }

        init()

        setupFragmentResultListener()

        setupObserver()
    }

    private fun init() {
        binding.addJournalDateTextView.setOnClickListener {
            lifecycle.coroutineScope.launch {

                binding.addJournalEditText.clearFocus()

                val args = bundleOf(JournalDatePickerFragment.IS_FORCED to false)

                supportFragmentManager.commitDelayed(250L) {
                    replace(R.id.add_journal_root_layout, JournalDatePickerFragment::class.java, args, JournalDatePickerFragment::class.java.simpleName)
                }
            }

            if(binding.addJournalDeleteImageView.isVisible) binding.addJournalDeleteImageView.isVisible = false
        }

        binding.addJournalEmotionView.setOnClickListener {
            lifecycle.coroutineScope.launch {
                binding.addJournalEmotionView.playAnimation()
                binding.addJournalEditText.clearFocus()

                supportFragmentManager.findFragmentById(R.id.add_journal_root_layout)
                    ?.let { supportFragmentManager.commit { remove(it) } }

                startExitTransition()


                val args = viewModel.emotion.value?.let { bundleOf(EmotionFragment.PREVIOUSLY_SELECTED_EMOTION_KEY to it) }

                supportFragmentManager.commitDelayed(250L) {
                    replace(R.id.add_journal_fragment_container, EmotionFragment::class.java, args, EmotionFragment::class.java.simpleName)
                        .runOnCommit { startEnterTransition() }
                }
            }

            if(binding.addJournalDeleteImageView.isVisible) binding.addJournalDeleteImageView.isVisible = false
        }

        binding.addJournalImageView.setOnClickListener { binding.addJournalDeleteImageView.isVisible = true }

        binding.addJournalDeleteImageView.setOnClickListener { viewModel.setUri(null) }

        binding.addJournalEditText.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) binding.addJournalEditText.showKeyboard()
            else binding.addJournalEditText.hideKeyboard()

            binding.addJournalEditText.isCursorVisible = hasFocus

            if(binding.addJournalDeleteImageView.isVisible && hasFocus) binding.addJournalDeleteImageView.isVisible = false
        }

        binding.addJournalGallery.setOnClickListener {
            lifecycle.coroutineScope.launch {
                binding.addJournalEditText.clearFocus()

                when {
                    binding.addJournalImageView.isVisible -> {
                        supportFragmentManager.commitDelayed(250L) {
                            replace(R.id.add_journal_root_layout, AddJournalImageLimitDialogFragment::class.java, null, AddJournalImageLimitDialogFragment::class.java.simpleName)
                        }
                    }
                    isGranted(READ_EXTERNAL_STORAGE) -> {
                        supportFragmentManager.commitDelayed(250L) {
                            replace(R.id.add_journal_fragment_container, ImagePickerFragment::class.java, null, ImagePickerFragment::class.java.simpleName)
                                .runOnCommit { startEnterTransition() }
                        }
                    }
                    else -> requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
                }
            }

            if(binding.addJournalDeleteImageView.isVisible) binding.addJournalDeleteImageView.isVisible = false
        }

        binding.addJournalComplete.setOnClickListener {
            if(binding.addJournalDeleteImageView.isVisible) binding.addJournalDeleteImageView.isVisible = false

            lifecycle.coroutineScope.launch {

                binding.addJournalEditText.clearFocus()

                binding.addJournalLoadingView.isVisible = true

                val bitmap = withContext(Dispatchers.Default) { viewModel.uri.value?.let { createBitmap(it) } }

                val imageUri = withContext(Dispatchers.IO) { bitmap?.let { saveBitmap(it) } }

                val emotion = requireNotNull(viewModel.emotion.value)

                val content = binding.addJournalEditText.text.toString()

                val date = requireNotNull(viewModel.date.value)

                viewModel.addJournal(Journal(0L, emotion, imageUri, content, date, System.currentTimeMillis()))
            }
        }
    }

    private fun setupFragmentResultListener() {
        supportFragmentManager.setFragmentResultListener(EmotionFragment.REQUEST_KEY, this) { _, bundle ->
            (bundle.get(EmotionFragment.DISPLAY_EMOTION_KEY) as? Emotion)?.let { viewModel.setEmotion(it) }

            (bundle.get(EmotionFragment.SELECTED_EMOTION_KEY) as? Emotion)
                ?.let { viewModel.setEmotion(it) }
                ?.also {
                    lifecycle.coroutineScope.launch {
                        startExitTransition()

                        delay(250L)

                        supportFragmentManager.findFragmentByTag(EmotionFragment::class.java.simpleName)
                            ?.let { supportFragmentManager.commit { remove(it) } }

                        binding.addJournalEditText.requestFocus()
                    }
                }
        }

        supportFragmentManager.setFragmentResultListener(ImagePickerFragment.REQUEST_KEY, this) { _, bundle ->
            bundle.getParcelable<Uri>(ImagePickerFragment.URI_KEY)
                ?.let { viewModel.setUri(it) }

            lifecycle.coroutineScope.launch {
                startExitTransition()

                delay(250L)

                supportFragmentManager.findFragmentByTag(ImagePickerFragment::class.java.simpleName)
                    ?.let { supportFragmentManager.commit { remove(it) } }

                binding.addJournalEditText.requestFocus()
            }
        }

        supportFragmentManager.setFragmentResultListener(JournalDatePickerFragment.REQUEST_KEY, this) { _, bundle ->
            bundle.getLong(JournalDatePickerFragment.DATE_KEY)
                .takeIf { it > 0 }
                ?.let { viewModel.setDate(LocalDate.ofEpochDay(it)) }

            val isForced = bundle.getBoolean(JournalDatePickerFragment.IS_FORCED)

            when {
                isForced -> {
                    supportFragmentManager.commit {
                        replace(R.id.add_journal_fragment_container, EmotionFragment::class.java, null, EmotionFragment::class.java.simpleName)
                            .runOnCommit { startEnterTransition() }
                    }
                }
                else -> binding.addJournalEditText.requestFocus()
            }
        }
    }

    private fun setupObserver() {
        viewModel.addJournalResult.observe(this) { result -> result.successOrNull()?.run { finish() } }

        viewModel.unavailableDates.observe(this) { result ->
            result.successOrNull()?.let { notAvailableDates ->
                when {
                    notAvailableDates.contains(today) -> {
                        val args = bundleOf(JournalDatePickerFragment.IS_FORCED to true)

                        supportFragmentManager.commit {
                            replace(R.id.add_journal_root_layout, JournalDatePickerFragment::class.java, args, JournalDatePickerFragment::class.java.simpleName)
                        }
                    }
                    else -> {
                        lifecycle.coroutineScope.launch {
                            supportFragmentManager.commit {
                                replace(R.id.add_journal_root_layout, EmotionFragment::class.java, null, EmotionFragment::class.java.simpleName)
                                    .runOnCommit { startEnterTransition() }
                            }
                        }
                    }
                }
            }
        }

        viewModel.date.observe(this) { date -> binding.addJournalDateTextView.text = date.toFormatString() }

        viewModel.emotion.observe(this) { emotion ->
            binding.addJournalEmotionView.setAnimation(emotion.rawResId)
            binding.addJournalEmotionView.playAnimation()
        }

        viewModel.uri.observe(this) { uri ->
            binding.addJournalImageView.isVisible = uri != null
            binding.addJournalMaskingTape.isVisible = uri != null
            when (uri) {
                null -> binding.addJournalDeleteImageView.isVisible = false
                else -> binding.addJournalImageView.loadJournalImage(uri)
            }
        }
    }

    private fun startEnterTransition() = binding.addJournalRootLayout.updateTransition {
        setGuidelinePercent(R.id.add_journal_fragment_container_top_guideline, 0.0F)
        setGuidelinePercent(R.id.add_journal_fragment_container_bottom_guideline, 1.0F)
    }

    private fun startExitTransition() = binding.addJournalRootLayout.updateTransition {
        setGuidelinePercent(R.id.add_journal_fragment_container_top_guideline, 1.0F)
        setGuidelinePercent(R.id.add_journal_fragment_container_bottom_guideline, 2.0F)
    }

    private fun createBitmap(uri: Uri): Bitmap? {
        return contentResolver.openFileDescriptor(uri, "r")
            ?.fileDescriptor
            ?.let { BitmapFactory.decodeFileDescriptor(it) }
            ?.let { rotateIfRequired(it, uri) }
    }

    private fun rotateIfRequired(bitmap: Bitmap, uri: Uri): Bitmap? {
        return contentResolver.openInputStream(uri)
            ?.let { ExifInterface(it) }
            ?.let {
                val orientation = it.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

                when(orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90);
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180);
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270);
                    else -> bitmap
                }
            }
    }

    private fun saveBitmap(bitmap: Bitmap): Uri? {
        return try {
            val directory = File(getExternalFilesDir(null), "images")

            val imageFile = File(directory, "${requireNotNull(viewModel.date.value).toEpochDay()}.jpg")

            if(!directory.exists()) directory.mkdirs()

            val stream = FileOutputStream(imageFile)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            FileProvider.getUriForFile(this, "$packageName.fileprovider", imageFile)
        } catch (exception: Exception) {
            Timber.debug(exception) { "saveBitmap() throw exception" }
            null
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.findFragmentById(R.id.add_journal_root_layout)
            ?.let {
                when(viewModel.emotion.value) {
                    null -> { finish(); return }
                    else -> {
                        supportFragmentManager.commit { remove(it) }
                        binding.addJournalEditText.requestFocus()
                        return
                    }
                }
            }

        supportFragmentManager.findFragmentById(R.id.add_journal_fragment_container)
            ?.let {
                lifecycle.coroutineScope.launch {
                    startExitTransition()

                    supportFragmentManager.commitDelayed(250L) {
                        remove(it).runOnCommit { binding.addJournalEditText.requestFocus() }
                    }
                }
                return
            }

        supportFragmentManager.commit {
            replace(R.id.add_journal_root_layout, AddJournalExitDialogFragment::class.java, null, AddJournalExitDialogFragment::class.java.simpleName)
        }
    }
}