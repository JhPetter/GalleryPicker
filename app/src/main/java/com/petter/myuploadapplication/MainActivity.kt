package com.petter.myuploadapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.petter.myuploadapplication.manager.PermissionManager
import com.petter.myuploadapplication.model.MyFile
import com.petter.myuploadapplication.model.UploadType
import com.petter.myuploadapplication.utils.StartPickerContract
import com.petter.myuploadapplication.utils.configChooserToImagePicker
import com.petter.myuploadapplication.utils.load
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val permissionManager: PermissionManager by lazy {
        PermissionManager(
            this,
            onDeny = {
                Toast.makeText(this, "Permission is required", Toast.LENGTH_SHORT).show()
            })
    }

    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainChoose.setOnClickListener {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        permissionManager.requestPermission(android.Manifest.permission.CAMERA) {
            chooserOptions()
        }
    }

    private fun chooserOptions() {
        configChooserToImagePicker(configImageUri())?.let {
            registerForActivityResult(StartPickerContract(it)) { response ->
                configImage(response)
            }.launch(null)
        }
    }

    private fun configImage(response: Intent?) {
        if (response != null) {
            response.data.let { uri ->
                uri?.let {
                    configDocumentToUpload(it, UploadType.GALLERY)
                }
            }
        } else {
            configDocumentToUpload(currentPhotoPath.toUri(), UploadType.CAMERA)
        }
    }

    private fun configDocumentToUpload(path: Uri, uploadType: UploadType) {
        prepareFile(path.toString(), uploadType)
    }


    private fun prepareFile(path: String, uploadType: UploadType) {
        if (uploadType == UploadType.GALLERY || uploadType == UploadType.DOCUMENT)
            configGalleryDocuments(path, uploadType)
        else
            configCamera(path, uploadType)
    }

    private fun configGalleryDocuments(path: String, uploadType: UploadType) {
        val fileType = getFileType(path)
        contentResolver.query(path.toUri(), null, null, null, null).use { cursor ->
            cursor?.let {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                cursor.moveToFirst()
                if (!cursor.isNull(sizeIndex)) {
                    val rimacFile = MyFile(
                        cursor.getString(nameIndex),
                        path,
                        cursor.getLong(sizeIndex),
                        uploadType, fileType
                    )
                    if (rimacFile.isCorrectSize())
                        openUpload(rimacFile)
                    else
                        showErrorSizeFile()
                }
                cursor.close()
            }
        }
    }

    private fun getFileType(path: String): String {
        val fileType = contentResolver?.getType(path.toUri())?.split("/") ?: arrayListOf()
        return if (fileType.size > 1) fileType[fileType.size.minus(1)] else ""
    }

    private fun configCamera(path: String, uploadType: UploadType) {
        val file = File(path)
        val rimacFile =
            MyFile(
                file.name,
                "file:///$path",
                file.length(),
                uploadType,
                file.extension
            )
        if (rimacFile.isCorrectSize())
            openUpload(rimacFile)
        else
            showErrorSizeFile()
    }

    private fun openUpload(file: MyFile) {
        Toast.makeText(this, "make actions with: ${file.name}", Toast.LENGTH_SHORT).show()
        println("Here is path: ${file.path}")
        imgUpload.load(file.path)
    }

    private fun showErrorSizeFile() {
        Toast.makeText(this, "Max file file is 5MB", Toast.LENGTH_SHORT).show()
    }

    private fun configImageUri(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? =
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "PHOTO_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }
}