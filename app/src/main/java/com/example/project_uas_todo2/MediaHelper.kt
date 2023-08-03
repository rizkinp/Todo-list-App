package com.example.project_uas_todo2

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MediaHelper(private val activity: Activity) {
    private var namaFile = ""
    private var fileUri: Uri = Uri.parse("")
    private val RC_CAMERA = 100
    private val RC_GALLERY = 200
    private var captureImagePath: String = ""

    fun setCaptureImagePath(path: String) {
        captureImagePath = path
    }

    fun getCaptureImagePath(): String {
        return captureImagePath
    }

    fun getMyFileName(): String {
        return this.namaFile
    }

    fun getRcCamera(): Int {
        return this.RC_CAMERA
    }

    fun getRcGallery(): Int {
        return this.RC_GALLERY
    }

    // Buat direktori jika belum ada
    private fun getOutputMediaFile(): File {
        val mediaStorageDir = File(
            activity.getExternalFilesDir(null),
            "registrasi"
        )
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.e("mkdir", "Gagal membuat direktori")
        }
        val mediaFile = File(
            mediaStorageDir.path + File.separator + this.namaFile
        )
        return mediaFile
    }

    // Siapkan nama file
    fun getOutputMediaFileUri(): Uri {
        val timeStamp =
            SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        this.namaFile = "DC_$timeStamp.jpg"
        this.fileUri = Uri.fromFile(getOutputMediaFile())
        Log.i("fileUri", this.fileUri.path.toString())
        return this.fileUri
    }

    fun bitmapToString(bmp: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun getBitmapToString(uri: Uri, imV: ImageView): String {
        val inputStream = activity.contentResolver.openInputStream(uri)
        val bmp = BitmapFactory.decodeStream(inputStream)
        val dim = 720
        val scaledBitmap = Bitmap.createScaledBitmap(
            bmp,
            dim,
            (bmp.height * dim) / bmp.width,
            true
        )
        imV.setImageBitmap(scaledBitmap)
        return bitmapToString(scaledBitmap)
    }

    fun checkCameraPermission(): Boolean {
        val permission = Manifest.permission.CAMERA
        val result = ContextCompat.checkSelfPermission(activity, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission() {
        val permission = Manifest.permission.CAMERA
        ActivityCompat.requestPermissions(activity, arrayOf(permission), RC_CAMERA)
    }

    fun checkGalleryPermission(): Boolean {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val result = ContextCompat.checkSelfPermission(activity, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun requestGalleryPermission() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        ActivityCompat.requestPermissions(activity, arrayOf(permission), RC_GALLERY)
    }
}