package com.marchlab.haema.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.marchlab.haema.databinding.ActivityPhotoViewBinding
import com.marchlab.haema.util.extensions.viewBinding

class PhotoViewActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityPhotoViewBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val uri = intent.getParcelableExtra<Uri>(IMAGE_URI_NAME)

        uri?.let {
            Glide.with(binding.photoView)
                .load(it)
                .into(binding.photoView)
        } ?: finish()

        binding.photoViewClose.setOnClickListener { finish() }
    }

    companion object {

        private const val IMAGE_URI_NAME = "imageUri"

        fun start(context: Context, uri: Uri) = context.startActivity(Intent(context, PhotoViewActivity::class.java).putExtra(
            IMAGE_URI_NAME, uri))
    }
}
