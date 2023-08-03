package com.example.project_uas_todo2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btn_back: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btn_back = findViewById(R.id.btn_back)

        btn_back.setOnClickListener {
            val intent = Intent(this, RegistrasiActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            login(username, password)
        }
    }
    private fun login(username: String, password: String) {
        val url = "http://192.168.60.64/project_uas_todo/public/api/users"
        val request = JSONObject()
        request.put("name", username)
        request.put("password", password)

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    var isSuccess = false
                    var userId = ""
                    var name = ""
                    var prodiName = ""

                    for (i in 0 until response.length()) {
                        val user = response.getJSONObject(i)
                        val userEmail = user.getString("email")
                        val userPassword = user.getString("password")
                        val prodiId = user.getInt("prodi_id")
                        val email = user.getString("email")
                        if (username == userEmail && password == userPassword) {
                            isSuccess = true
                            userId = user.getString("id")
                            name = user.getString("name")

                            // Menggunakan Coroutine untuk mendapatkan prodiName secara asinkron

//                                prodiName = getProdiName(prodiId)

                                // Melanjutkan langkah-langkah selanjutnya setelah mendapatkan prodiName

                                    // Login berhasil
                                    val token = "dummy-token" // Ganti dengan token yang diberikan oleh server
                                    // Simpan informasi login di sini (misalnya, di SharedPreferences)

                                    // Navigasi ke halaman berikutnya setelah login berhasil
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.putExtra("token", token)
                                    intent.putExtra("prodiId", prodiId)
                                    intent.putExtra("userId", userId)
                                    intent.putExtra("name", name)
                                    intent.putExtra("password", userPassword)
//                                    intent.putExtra("prodiName", prodiName) // Menambahkan prodiName ke intent
                                    intent.putExtra("email", email)

                                    startActivity(intent)
                                    finish()



                            break
                        }
                    }

                    if (!isSuccess) {
                        // Login gagal
                        Toast.makeText(this@LoginActivity, "Invalid username or password", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("LoginActivity", "Error: ${error.message}")
                Toast.makeText(this@LoginActivity, "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
            })

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }

    private suspend fun getProdiName(prodiId: Int): String = withContext(Dispatchers.IO) {
        val prodiUrl = "http://192.168.60.64/project_uas_todo/public/api/prodi/$prodiId"
        var prodiName = ""

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, prodiUrl, null,
            { response ->
                try {
                    prodiName = response.getString("name")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("LoginActivity", "Error: ${error.message}")
            })

        val requestQueue = Volley.newRequestQueue(this@LoginActivity)
        val responseFuture = RequestFuture.newFuture<JSONObject>()
        requestQueue.add(jsonObjectRequest)

        try {
            val response = responseFuture.get(10, TimeUnit.SECONDS)
            prodiName = response.getString("name")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        prodiName
    }




    private fun login2(username: String, password: String) {
        val url = "http://192.168.60.64/project_uas_todo/public/api/users"

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                var isSuccess = false
                var userId = ""
                var name = ""

                for (i in 0 until response.length()) {
                    val user = response.getJSONObject(i)
                    val userEmail = user.getString("email")
                    val userPassword = user.getString("password")

                    if (username == userEmail && password == userPassword) {
                        isSuccess = true
                        userId = user.getString("id")
                        name = user.getString("name")
                        break
                    }
                }

                if (isSuccess) {
                    // Login berhasil
                    val token = "dummy-token" // Ganti dengan token yang diberikan oleh server
                    // Simpan informasi login di sini (misalnya, di SharedPreferences)

                    // Navigasi ke halaman berikutnya setelah login berhasil
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("token", token)
                    intent.putExtra("userId", userId)
                    intent.putExtra("name", name)
                    startActivity(intent)
                    finish()
                } else {
                    // Login gagal
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("LoginActivity", "Error: ${error.message}")
                Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
            })

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }






}