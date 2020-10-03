package com.marchlab.haema.ui.category.book.detail

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.marchlab.haema.R
import com.marchlab.haema.databinding.ActivityBookDetailBinding
import com.marchlab.haema.domain.model.Book
import com.marchlab.haema.ui.category.book.delete.DeleteBookDialogFragment
import com.marchlab.haema.ui.category.book.edit.EditBookActivity
import com.marchlab.haema.util.extensions.loadBookImage
import com.marchlab.haema.util.extensions.toFormatString
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.successOrNull
import kotlinx.coroutines.*
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import timber.log.Timber
import timber.log.debug
import java.io.File
import java.io.FileOutputStream

@ExperimentalCoroutinesApi
class BookDetailActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityBookDetailBinding::inflate)

    private val viewModel: BookDetailViewModel by lifecycleScope.viewModel(this)

    private val shareIntentLauncher
            = registerForActivityResult(StartActivityForResult()) { binding.bookDetailSharePreviewLayout.isVisible = false }

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory(lifecycleScope)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        intent.getLongExtra(BOOK_ID, -1L)
            .takeIf { it >= 0 }
            ?.let { viewModel.loadBook(it) }
            ?: Snackbar.make(binding.root, "예기치못한 오류가 발생하였습니다.", Snackbar.LENGTH_INDEFINITE)
                .setAction("닫기") { finish() }
                .show()

        init()

        setupFragmentResultListener()

        setupObserver()
    }

    private fun init() {
        binding.bookDetailNavigateBack.setOnClickListener { finish() }

        binding.bookDetailDelete.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.book_detail_root_layout, DeleteBookDialogFragment::class.java, null, DeleteBookDialogFragment::class.java.simpleName)
            }
        }

        binding.bookDetailShare.setOnClickListener { onShare() }
    }

    private fun setupFragmentResultListener() {
        supportFragmentManager.setFragmentResultListener(DeleteBookDialogFragment.REQUEST_KEY, this) { _, _ -> viewModel.deleteBook() }
    }

    private fun setupObserver() {
        viewModel.book.observe(this) {
            it.successOrNull()?.let { book -> onBind(book) }
        }

        viewModel.deleteBookResult.observe(this) { result -> result.successOrNull()?.run { finish() } }
    }

    private fun onBind(book: Book) {
        binding.bookDetailEdit.setOnClickListener { EditBookActivity.startActivity(this, book.id) }


        binding.bookDetailImageView.isVisible = book.imageUrl.isNotBlank()
        binding.bookDetailImageShadow.isVisible = book.imageUrl.isNotBlank()
        if(binding.bookDetailImageView.isVisible) binding.bookDetailImageView.loadBookImage(book.imageUrl)

        binding.bookDetailTitleTextView.text = book.title

        binding.bookDetailRatingBar.rating = book.rating.toFloat()

        binding.bookDetailAuthor.isVisible = book.author.isNotBlank()
        binding.bookDetailAuthorTextView.isVisible = book.author.isNotBlank()
        if(binding.bookDetailAuthorTextView.isVisible) binding.bookDetailAuthorTextView.text = book.author

        binding.bookDetailPublisher.isVisible = book.publisher.isNotBlank()
        binding.bookDetailPublisherTextView.isVisible = book.publisher.isNotBlank()
        if(binding.bookDetailPublisherTextView.isVisible) binding.bookDetailPublisherTextView.text = book.publisher

        binding.bookDetailDateTextView.text = book.date.toFormatString()

        binding.bookDetailReviewTextView.isVisible = book.review.isNotBlank()
        if(binding.bookDetailReviewTextView.isVisible) binding.bookDetailReviewTextView.text = book.review
        binding.bookDetailSpacing.isVisible = book.review.isBlank()
    }

    private fun onShare() {
        lifecycle.coroutineScope.launch {
            val bitmap = withContext(Dispatchers.Default) { createBitmap(binding.bookDetailContentsLayout) }

            val uri = withContext(Dispatchers.IO) { saveBitmap(bitmap) }

            when {
                uri != null -> {
                    /*
                    binding.bookDetailSharePreviewLayout.isVisible = true
                    binding.bookDetailShareImageView.setImageURI(uri)
                    delay(500L)
                    */

                    Intent()
                        .apply {
                            action = Intent.ACTION_SEND
                            type = "image/*"
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            setDataAndType(uri, contentResolver.getType(uri))
                            putExtra(Intent.EXTRA_STREAM, uri)
                        }
                        .let { Intent.createChooser(it, "공유") }
                        .let { shareIntentLauncher.launch(it) }
                }
                else -> {
                    Toast.makeText(this@BookDetailActivity, "예기치못한 오류 발생가 발생하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /* 공유 */
    private fun createBitmap(view: View, width: Int = view.width, height: Int = view.height): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)

        view.background
            ?.draw(canvas)
            ?:canvas.drawColor(ContextCompat.getColor(this@BookDetailActivity, R.color.main_background))

        view.draw(canvas)

        return bitmap
    }

    private fun saveBitmap(bitmap: Bitmap): Uri? {
        return try {
            val folder = File(cacheDir, "/share")

            folder.mkdirs()

            val file = File(folder, "/${System.currentTimeMillis()}.jpg")

            val stream = FileOutputStream(file)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            FileProvider.getUriForFile(this@BookDetailActivity, "$packageName.fileprovider", file)

        } catch (exception: Exception) {
            Timber.debug(exception) { "saveBitmap(Bitmap) throw exception" }
            null
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.findFragmentById(R.id.book_detail_root_layout)
            ?.let {
                supportFragmentManager.commit { remove(it) }
                return
            }

        super.onBackPressed()
    }

    companion object {
        private const val BOOK_ID = "BOOK_ID"

        fun startActivity(context: Context, id: Long)
                = context.startActivity(Intent(context, BookDetailActivity::class.java).putExtra(BOOK_ID, id))
    }
}