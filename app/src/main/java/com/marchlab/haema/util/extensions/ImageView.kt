package com.marchlab.haema.util.extensions

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.TypedValue
import android.widget.ImageView
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.marchlab.haema.R
import kotlin.math.roundToInt

fun ImageView.load(url: String)
        = Glide.with(context).load(url).transform(CenterCrop(), RoundedCorners(context.convertDpToPx(8).roundToInt())).into(this)

fun ImageView.loadJournalImage(uri: Uri) { Glide.with(context).load(uri).transform(CenterCrop()).into(this) }

fun ImageView.loadBookImage(url: String?) {
    Glide.with(context)
        .load(url?.takeIf { it.isNotBlank() })
        .fallback(R.drawable.img_photo)
        .transition(DrawableTransitionOptions.withCrossFade(500))
        .transform(
            CenterCrop(),
            GranularRoundedCorners(0F, context.convertDpToPx(8), context.convertDpToPx(8),0F)
        )
        .into(this)
}

fun ImageView.loadMovieImage(url: String?) {

}

fun rotateImageIfRequired(context: Context, image: Bitmap,  uri: Uri): Bitmap? {
    return context.contentResolver.openInputStream(uri)
        ?.let {
            val ei = ExifInterface(it)

            when(ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(image, 90);
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(image, 180);
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(image, 270);
                else -> image
            }
        }
}