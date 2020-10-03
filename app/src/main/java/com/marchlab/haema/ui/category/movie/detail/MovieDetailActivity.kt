package com.marchlab.haema.ui.category.movie.detail

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.marchlab.haema.R
import com.marchlab.haema.databinding.ActivityMovieDetailBinding
import com.marchlab.haema.domain.model.Movie
import com.marchlab.haema.ui.category.CategoryDatePickerFragment
import com.marchlab.haema.ui.category.movie.delete.DeleteMovieDialog
import com.marchlab.haema.ui.category.movie.edit.EditMovieFragment
import com.marchlab.haema.ui.main.ImagePickerFragment
import com.marchlab.haema.util.extensions.toFormatString
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.*
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import timber.log.Timber
import timber.log.debug
import java.io.File
import java.io.FileOutputStream

@ExperimentalCoroutinesApi
class MovieDetailActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMovieDetailBinding::inflate)

    private val viewModel: MovieDetailViewModel by lifecycleScope.viewModel(this)

    private var mUri: Uri? = null

    private val shareLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        binding.shareLayout.isVisible = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory(lifecycleScope)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        intent.extras
            ?.getLong("movie_id")
            ?.let {
                viewModel.setMovieId(it)
                viewModel.loadMovie(it)
            }
            ?: Snackbar.make(binding.root, "일시적인 오류가 발생하였습니다.", Snackbar.LENGTH_INDEFINITE)
                .setAction("닫기") { finish() }
                .show()


        init()
        setupObserver()

    }

    private fun init(){
        binding.movieDetailNavigateBack.setOnClickListener { finish() }

        binding.movieDetailDelete.setOnClickListener {
            /* dialog */
            supportFragmentManager.beginTransaction()
                .replace(R.id.movie_detail_root_layout,
                    DeleteMovieDialog(), DeleteMovieDialog::class.java.simpleName)
                .commit()
        }

        binding.movieDetailEdit.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(R.id.movie_detail_root_layout, EditMovieFragment::class.java, null, EditMovieFragment::class.java.simpleName)
                .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                .commit()
        }

        binding.movieDetailShare.setOnClickListener {
            /* dialog ? */
            lifecycle.coroutineScope.launch {
                mUri?.let {
                    binding.shareLayout.isVisible = true
                    binding.shareImageView.setImageURI(it)

                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "image/*"
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        setDataAndType(it, contentResolver.getType(it))
                        putExtra(Intent.EXTRA_STREAM, it)
                    }

                    shareLauncher.launch(Intent.createChooser(shareIntent, "공유할 앱을 선택해주세요"))

                    return@launch
                }

                createBitmapFromView(binding.movieDetailReviewLayout)
                    ?.let { saveBitmap(it) }
                    ?.let { uri ->
                        mUri = uri

                        binding.shareLayout.isVisible = true
                        binding.shareImageView.setImageURI(uri)

                        delay(500L) /* pause */

                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "image/*"
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            setDataAndType(uri, contentResolver.getType(uri))
                            putExtra(Intent.EXTRA_STREAM, uri)
                        }

                        shareLauncher.launch(Intent.createChooser(shareIntent, "공유할 앱을 선택해주세요"))
                    }
                    ?: Toast.makeText(this@MovieDetailActivity, "일시적인 오류 발생", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObserver() {
        supportFragmentManager.setFragmentResultListener(DeleteMovieDialog.REQUEST_KEY, this, ::onDelete)

        viewModel.movie.observe(this) { result ->
            when (result) {
                Result.Loading -> { }
                is Result.Success -> bind(result.data)
                is Result.Error -> { }
            }
        }

        viewModel.deleteMovieResult.observe(this) {result ->
            when(result) {
                Result.Loading -> { }
                is Result.Success -> { finish() }
                is Result.Error -> { }
            }
        }

        viewModel.editMovieResult.observe(this) {result ->
            when(result) {
                Result.Loading -> { }
                is Result.Success -> {
                    supportFragmentManager.findFragmentByTag(EditMovieFragment::class.java.simpleName)
                        ?.let {
                            supportFragmentManager.beginTransaction()
                                .remove(it)
                                .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                                .commit()
                        }
                }
                is Result.Error -> { }
            }

        }
    }

    private fun bind(movie: Movie) {

        binding.movieDetailRatingBar.rating = movie.rate.toFloat()

        binding.movieDetailTitleTextView.text = movie.title

        Glide.with(this)
            .load(movie.posterUrl)
            .centerCrop()
            .into(binding.movieDetailImageView)

        binding.movieDetailImageView.isVisible = movie.posterUrl.isNotBlank()
        binding.movieDetailImageTape.isVisible = movie.posterUrl.isNotBlank()
        binding.movieDetailImageShadow.isVisible = movie.posterUrl.isNotBlank()

        binding.movieDetailReleaseTextView.text = resources.getString(R.string.movie_release_year, movie.release)
        binding.movieDetailRelease.isVisible = movie.release.isNotBlank()
        binding.movieDetailReleaseTextView.isVisible = movie.release.isNotBlank()

        binding.movieDetailDirectorTextView.text = movie.directors
        binding.movieDetailDirector.isVisible = movie.directors.isNotBlank()
        binding.movieDetailDirectorTextView.isVisible = movie.directors.isNotBlank()

        binding.movieDetailActorTextView.text = movie.actors
        binding.movieDetailActor.isVisible = movie.actors.isNotBlank()
        binding.movieDetailActorTextView.isVisible = movie.actors.isNotBlank()

        binding.movieDetailDateTextView.text = movie.date.toFormatString()

        binding.movieDetailPlaceTextView.text = movie.place
        binding.movieDetailPlace.isVisible = movie.place.isNotBlank()
        binding.movieDetailPlaceTextView.isVisible = movie.place.isNotBlank()

        binding.movieDetailPeopleTextView.text = movie.people
        binding.movieDetailPeople.isVisible = movie.people.isNotBlank()
        binding.movieDetailPeopleTextView.isVisible = movie.people.isNotBlank()

        binding.movieDetailReviewTextView.text = movie.review
        binding.movieDetailReviewTextView.isVisible = movie.review.isNotBlank()

    }

    private fun onDelete(resultKey: String, bundle: Bundle) = viewModel.deleteMovie()

    /* 공유 */
    private suspend fun createBitmapFromView(view: View, width: Int = view.width, height: Int = view.height): Bitmap {
        return withContext(Dispatchers.Unconfined) {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(bitmap)

            view.background
                ?.draw(canvas)
                ?: canvas.drawColor(ContextCompat.getColor(this@MovieDetailActivity, R.color.main_background))

            view.draw(canvas)

            bitmap
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun saveBitmap(bitmap: Bitmap): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val folder = File(cacheDir, "/share")

                folder.mkdirs()

                val file = File(folder, "/${System.currentTimeMillis()}.jpg")

                val stream = FileOutputStream(file)

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

                stream.flush()

                stream.close()

                FileProvider.getUriForFile(this@MovieDetailActivity, "$packageName.fileprovider", file)
            } catch (exception: Exception) {
                Timber.debug(exception) { "saveBitmap(Bitmap) throw exception" }

                null
            }
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.findFragmentByTag(DeleteMovieDialog::class.java.simpleName)
            ?.let {
                Timber.debug { "onBackPressed(); remove DeleteMovieDialog" }
                supportFragmentManager.beginTransaction().remove(it).commit()
                return
            }

        supportFragmentManager.findFragmentByTag(ImagePickerFragment::class.java.simpleName)
            ?.let {
                Timber.debug { "onBackPressed(); remove ImagePickerFragment" }
                supportFragmentManager.beginTransaction().remove(it).commit()
                return
            }

        supportFragmentManager.findFragmentByTag(CategoryDatePickerFragment::class.java.simpleName)
            ?.let {
                Timber.debug { "onBackPressed(); remove CategoryDatePickerFragment" }
                supportFragmentManager.beginTransaction().remove(it).commit()
                return
            }

        supportFragmentManager.findFragmentByTag(EditMovieFragment::class.java.simpleName)
            ?.let {
                Timber.debug { "onBackPressed(); remove EditMovieFragment" }
                supportFragmentManager.beginTransaction().remove(it).commit()
                return
            }

        super.onBackPressed()
    }


}

