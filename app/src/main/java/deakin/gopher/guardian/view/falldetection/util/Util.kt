package deakin.gopher.guardian.view.falldetection.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.mlkit.vision.common.InputImage
import java.nio.ByteBuffer

fun Context.openActivity(openThis: Class<out AppCompatActivity>) {
    startActivity(Intent(this, openThis))
}

fun ImageView.load(imageUrl: String) {
    // Set the minimum width and height
    val requestOptions =
        RequestOptions().override(480, 360)
            // Crop the image if needed to fit the dimensions
            .centerCrop()

    // Use the context of the ImageView
    Glide.with(this.context)
        .load(imageUrl)
        .apply(requestOptions)
        // Cache the image
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

fun ImageView.load(
    @DrawableRes drawableId: Int,
    onLoad: (Drawable) -> Unit = {},
) {
    val requestOptions =
        RequestOptions().centerCrop() // Crop the image if needed to fit the dimensions

    Glide.with(this.context) // Use the context of the ImageView
        .load(drawableId)
        .apply(requestOptions)
        .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
        .listener(
            object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean,
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean,
                ): Boolean {
                    onLoad(resource)
                    return false
                }
            },
        )
        .into(this)
}

fun getInputImageFrom(
    bitmap: Bitmap,
    rotationDegrees: Int = 0,
): InputImage {
    return InputImage.fromBitmap(bitmap, rotationDegrees)
}

fun getInputImageFrom(
    context: Context,
    uri: Uri,
): InputImage {
    return InputImage.fromFilePath(context, uri)
}

fun getInputImageFrom(
    byteBuffer: ByteBuffer,
    bitmap: Bitmap,
    rotationDegrees: Int,
): InputImage {
    return InputImage.fromByteBuffer(
        byteBuffer,
        bitmap.width,
        bitmap.height,
        rotationDegrees,
        // IMAGE_FORMAT_NV21 or IMAGE_FORMAT_YV12
        InputImage.IMAGE_FORMAT_NV21,
    )
}

fun Context.toast(
    message: String,
    duration: Int = android.widget.Toast.LENGTH_SHORT,
) {
    android.widget.Toast.makeText(this, message, duration).show()
}

fun extractNumericValue(input: String): Int? {
    val regex = Regex("\\d+")
    val matchResult = regex.find(input)
    return matchResult?.value?.toInt()
}
