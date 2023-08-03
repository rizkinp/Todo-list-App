package com.example.project_uas_todo2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONException

class MainActivity : AppCompatActivity() {

    private lateinit var tvToken: TextView
    private lateinit var tvUserId: TextView
    private lateinit var tvName: TextView
    private lateinit var tvProdiName: TextView

    private lateinit var bottomNavigationView: BottomNavigationView
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        val token = intent.getStringExtra("token")
        val userId = intent.getStringExtra("userId")
        val name = intent.getStringExtra("name")
//        val prodiName = intent.getStringExtra("prodiName")
        val email = intent.getStringExtra("email")
        val prodiId = intent.getIntExtra("prodiId", 0).toString()
        val password = intent.getStringExtra("password")

        // Memuat HomeFragment
        val homeFragment = userId?.let {
            if (prodiId != null && name != null) {
                HomeFragment.newInstance(it, prodiId, name)
            } else {
                null
            }
        }


        super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main)
        // Memuat HomeFragment
        if (homeFragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment)
                .commit()
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
                bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_home -> {
                            val homeFragment = userId?.let {
                                if (prodiId != null && name != null) {
                                    HomeFragment.newInstance(it, prodiId, name)
                                } else {
                                    null
                                }
                            }

                            if (homeFragment != null) {
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainer, homeFragment)
                                    .commit()
                            }

                            return@setOnNavigationItemSelectedListener true
                        }

                        R.id.menu_history -> {
                            val historyFragment =
                                userId?.let { HistoryFragment.newInstance(it, prodiId) }
                            if (historyFragment != null) {
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainer, historyFragment)
                                    .commit()
                            }
                            return@setOnNavigationItemSelectedListener true
                        }
                        R.id.menu_about -> {
                            val aboutFragment = AboutFragment()

                            supportFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainer, aboutFragment)
                                .commit()

                            return@setOnNavigationItemSelectedListener true
                        }

                        R.id.menu_akun -> {
                            val akunFragment =
                                token?.let {
                                    if (userId != null) {
                                        if (name != null) {
                                            if (email != null) {
                                                if (password != null ) {
                                                    AkunFragment.newInstance(it, userId, name, email, prodiId, password)
                                                } else {
                                                    null
                                                }
                                            } else {
                                                null
                                            }
                                        } else {
                                            null
                                        }
                                    } else {
                                        null
                                    }
                                }

                            if (akunFragment != null) {
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainer, akunFragment)
                                    .commit()
                            }

                            return@setOnNavigationItemSelectedListener true
                        }
                    }
                    false
                }

                // Memuat AkunFragment
//                val akunFragment = token?.let {
//                    if (userId != null) {
//                        if (name != null) {
//                            if (email != null) {
//                                if (prodiName != null) {
//                                    AkunFragment.newInstance(it, userId, name, email, prodiName)
//                                }
//                            }
//                        }
//                    }
//                }

                // Tampilkan fragment default saat aplikasi pertama kali dijalankan


    }

}

