package com.example.project_uas_todo2

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class RegistrasiActivity : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 1
    private val CAMERA_REQUEST_CODE = 3
    private val GALLERY_REQUEST_CODE = 4

    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var spinProdi: Spinner
    private lateinit var imUpload: ImageView
    private lateinit var btnInsert: Button
    private lateinit var btn_back: Button
    private lateinit var adapterProdi: ArrayAdapter<String>
    private var photoUrl: String? = null


    private lateinit var prodiList: MutableList<Prodi>

    private var selectedImage: Bitmap? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        editTextName = findViewById(R.id.editTextName)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        spinProdi = findViewById(R.id.spinProdi)
        imUpload = findViewById(R.id.imUpload)
        btnInsert = findViewById(R.id.btnInsert)
        btn_back = findViewById(R.id.btn_back)

        prodiList = mutableListOf()

        adapterProdi = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        adapterProdi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinProdi.adapter = adapterProdi

        imUpload.setOnClickListener {
            showImagePickerDialog()
        }

        btn_back.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnInsert.setOnClickListener {
            registerUser()
        }
        loadProdiList()
    }

    private fun showImagePickerDialog() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Choose your profile picture")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        openCamera()
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.CAMERA),
                            CAMERA_PERMISSION_CODE
                        )
                    }
                }
                options[item] == "Choose from Gallery" -> {
                    openGallery()
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    selectedImage = imageBitmap
                    imUpload.setImageBitmap(imageBitmap)
                    photoUrl = saveImageToStorage(imageBitmap)
                }
                GALLERY_REQUEST_CODE -> {
                    val imageUri: Uri? = data?.data
                    val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    selectedImage = imageBitmap
                    imUpload.setImageBitmap(imageBitmap)
                    photoUrl = imageUri?.toString()
                }
            }
        }
    }
    private fun saveImageToStorage(bitmap: Bitmap): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        val encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        val fileName = "${System.currentTimeMillis()}.jpg" // Menggunakan timestamp sebagai nama file
        val url = "http://192.168.60.64/project_uas_todo/public/storage/images/$fileName" // URL dengan nama file

        // Mengirimkan encoded image dalam permintaan POST
        val requestData = JSONObject()
        requestData.put("photo", encodedImage)

        val request = JsonObjectRequest(Request.Method.POST, url, requestData,
            { response ->
                // Gambar berhasil disimpan ke penyimpanan server
                Log.d("SAVE_IMAGE", "Image saved successfully")
            },
            { error ->
                // Error saat menyimpan gambar ke penyimpanan server
                Log.e("SAVE_IMAGE", "Failed to save image: ${error.message}")
            })

        val queue = Volley.newRequestQueue(this)
        queue.add(request)

        return url
    }




    private fun registerUser() {
        val name = editTextName.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val prodiId = prodiList[spinProdi.selectedItemPosition].id

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || selectedImage == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val base64Image = encodeImage(selectedImage!!)

        val user = JSONObject()
        user.put("name", name)
        user.put("email", email)
        user.put("password", password)
        user.put("prodi_id", prodiId)
        user.put("photo", base64Image)

        val queue = Volley.newRequestQueue(this)
        val url ="http://192.168.60.64/project_uas_todo/public/api/users"
        val request = JsonObjectRequest(Request.Method.POST, url, user,
            { response ->
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                // Do something with the response
                // You can navigate to the next activity here
            },
            { error ->
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                // Handle error
            })
        queue.add(request)
    }

    private fun loadProdiList() {
        Log.d("LOAD_PRODI_LIST", "Start loading prodi list")

        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.60.64/project_uas_todo/public/api/prodi"
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                Log.d("LOAD_PRODI_LIST", "Prodi list response: $response")

                for (i in 0 until response.length()) {
                    val prodiObject = response.getJSONObject(i)
                    val prodiId = prodiObject.getInt("id")
                    val prodiName = prodiObject.getString("name")

                    prodiList.add(Prodi(prodiId, prodiName))
                    adapterProdi.add(prodiName)
                }

                Log.d("LOAD_PRODI_LIST", "Prodi list loaded successfully")
            },
            { error ->
                Log.e("LOAD_PRODI_LIST", "Failed to load prodi list: ${error.message}")
                Toast.makeText(this, "Failed to load prodi list", Toast.LENGTH_SHORT).show()
            })
        queue.add(request)
    }

    private fun encodeImage(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }
}
