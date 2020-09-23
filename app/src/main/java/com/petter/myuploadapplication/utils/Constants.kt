package com.petter.myuploadapplication.utils

import android.Manifest

val REQUIRED_UPLOAD_FILES_PERMISSION = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)
