package com.petter.myuploadapplication.model

class MyFile(
    var name: String,
    var path: String,
    var size: Long,
    var uploadType: UploadType,
    var documentType: String
) {
    fun isCorrectSize(): Boolean {
        val mb = size.times(0.000001)
        return mb <= 5
    }
}