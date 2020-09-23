package com.petter.myuploadapplication.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.widget.ImageView
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.request.LoadRequest
import com.petter.myuploadapplication.R
import java.io.File
import java.util.*

fun Context.findIntentPackageToChoose(intent: Intent, list: MutableList<Intent>) {
    val resInfo = packageManager.queryIntentActivities(intent, 0)
    for (resolveInfo in resInfo) {
        val packageName = resolveInfo.activityInfo.packageName
        val targetedIntent = Intent(intent)
        targetedIntent.setPackage(packageName)
        list.add(targetedIntent)
    }
}

fun Context.configChooserToImagePicker(imageUrl: File): Intent? {
    var chooserIntent: Intent? = null

    val intentList: MutableList<Intent> = ArrayList()
    val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    pickIntent.apply {
        type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
    }

    val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    imageUrl.also {
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileProvider",
            it
        )
        takePhotoIntent.apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }
    }


    findIntentPackageToChoose(takePhotoIntent, intentList)
    findIntentPackageToChoose(pickIntent, intentList)

    if (intentList.size > 0) {
        chooserIntent = Intent.createChooser(
            intentList.removeAt(intentList.size - 1),
            getString(R.string.select_capture_image)
        )
        chooserIntent?.putExtra(
            Intent.EXTRA_INITIAL_INTENTS,
            intentList.toTypedArray<Parcelable>()
        )
    }

    return chooserIntent
}

fun ImageView.load(url: String) {
    val imageLoader = ImageLoader.Builder(context)
        .bitmapPoolPercentage(0.5)
        .crossfade(true)
        .build()
    val request = LoadRequest.Builder(context)
        .data(url)
        .target(this)
        .build()
    imageLoader.execute(request)
}