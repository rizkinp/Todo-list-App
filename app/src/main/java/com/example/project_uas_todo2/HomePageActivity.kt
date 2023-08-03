package com.example.project_uas_todo2

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton

class HomePageActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        val loginButton: Button = findViewById(R.id.loginButton)
        val registerButton: Button = findViewById(R.id.registerButton)
//        val websiteButton: ImageButton = findViewById(R.id.website)
//        val mapsButton: ImageButton = findViewById(R.id.maps)

        loginButton.setOnClickListener {
            // Aksi yang akan dijalankan saat tombol Login diklik
            // Implementasikan sesuai kebutuhan Anda
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        registerButton.setOnClickListener {
            // Aksi yang akan dijalankan saat tombol Register diklik
            // Implementasikan sesuai kebutuhan Anda
            val intent = Intent(this, RegistrasiActivity::class.java)
            startActivity(intent)
        }

//        websiteButton.setOnClickListener {
//            // Aksi yang akan dijalankan saat ikon website diklik
//            val websiteUrl = "https://www.youtube.com" // Ganti dengan URL website yang diinginkan
//            openUrlInBrowser(websiteUrl)
//        }
//
//        mapsButton.setOnClickListener {
//            // Aksi yang akan dijalankan saat ikon Maps diklik
//            val location = "latitude,longitude" // Ganti dengan koordinat latitude dan longitude yang diinginkan
//            openMaps(location)
//        }
    }

    private fun openUrlInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun openMaps(location: String) {
        val gmmIntentUri = Uri.parse("geo:$location")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }
}
