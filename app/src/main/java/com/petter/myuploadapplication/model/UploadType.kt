package com.petter.myuploadapplication.model

enum class UploadType(var code: Int) {
    CAMERA(1), GALLERY(2), DOCUMENT(3);

    companion object {
        fun valueOfCode(code: Int) = values().find { it.code == code } ?: CAMERA
    }

}