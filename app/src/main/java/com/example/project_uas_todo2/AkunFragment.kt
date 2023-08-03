package com.example.project_uas_todo2

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class AkunFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvProdiName: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var btnlogOut: Button
    private lateinit var email: String
    private lateinit var password: String
    private var updatedPassword: String = ""
    private var isPasswordVerified: Boolean = false


    private lateinit var ivPhoto: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_akun, container, false)

        tvName = view.findViewById(R.id.tvName)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvProdiName = view.findViewById(R.id.tvProdiName)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnlogOut = view.findViewById(R.id.btnLogout)
        ivPhoto = view.findViewById(R.id.ivPhoto)

        // Mendapatkan data dari arguments
        val token = arguments?.getString("token")
        val userId = arguments?.getString("userId")
        val name = arguments?.getString("name")
        email = arguments?.getString("email") ?: ""
        val prodiId = arguments?.getString("prodiId")
        password = arguments?.getString("password").toString()

        // Menampilkan data di TextView
        tvName.text = "Name: $name"
        tvEmail.text = "Email: $email"

        if (prodiId != null) {
            getProdiName(prodiId)
        }

        btnEditProfile.setOnClickListener {
            verifyPassword()
        }

        if (userId != null) {
            getUserData(userId)
        }

        btnlogOut.setOnClickListener {
            val intent = Intent(requireContext(), HomePageActivity::class.java)
            startActivity(intent)
        }


        return view
    }

    private fun getProdiName(prodiId: String) {
        val url = "http://192.168.60.64/project_uas_todo/public/api/prodi/$prodiId"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val prodiName = response.getString("name")
                    tvProdiName.text = "Prodi Name: $prodiName"
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("AkunFragment", "Error: ${error.message}")
            })

        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest)
    }

    private fun verifyPassword() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_verify_password, null)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPassword)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Verify Password")
            .setView(dialogView)
            .setPositiveButton("Verify") { _, _ ->
                val inputPassword = etPassword.text.toString()

                if (password == inputPassword) {
                    isPasswordVerified = true
                    showEditProfileDialog()
                } else {
                    showAlert("Incorrect password!")
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPassword)

        etEmail.setText(email) // Menetapkan email saat ini ke EditText email

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newEmail = etEmail.text.toString()
                val newPassword = etPassword.text.toString()

                // Menyimpan password yang baru saja diubah
                updatedPassword = newPassword

                updateProfile(newEmail, newPassword)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }


    @SuppressLint("SetTextI18n")
    private fun updateProfile(email: String, password: String) {
        val userId = arguments?.getString("userId")
        val url = "http://192.168.60.64/project_uas_todo/public/api/users/$userId"

        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password // Menggunakan password yang baru

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.PUT, url, JSONObject(params as Map<*, *>?),
            { response ->
                try {
                    val updatedUser = response.getJSONObject("user")
                    val updatedEmail = updatedUser.getString("email")
                    val updatedPassword = updatedUser.getString("password")

                    // Mengupdate variabel email dan password dengan data yang telah diperbarui
                    this.email = updatedEmail
                    this.password = updatedPassword

                    activity?.runOnUiThread {
                        tvEmail.text = "Email: $updatedEmail"
                    }

                    showAlert("Profile updated successfully!")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("AkunFragment", "Error: ${error.message}")
            })

        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest)
    }


    private fun showAlert(message: String) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Alert")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create()

        dialog.show()
    }

    private fun getUserData(userId: String) {
        val url = "http://192.168.60.64/project_uas_todo/public/api/users/$userId"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val name = response.getString("name")
                    val email = response.getString("email")
                    val prodiId = response.getString("prodi_id")
                    val photo = response.getString("photo")

                    // Menampilkan data pengguna di TextView
                    tvName.text = "Name: $name"
                    tvEmail.text = "Email: $email"

                    // Memuat gambar menggunakan Picasso
                    Picasso.get().load(photo).into(ivPhoto)

                    // Mendapatkan nama prodi jika prodiId tidak kosong
                    if (prodiId.isNotEmpty()) {
                        getProdiName(prodiId)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("AkunFragment", "JSON Exception: ${e.message}")
                }
            },
            { error ->
                Log.e("AkunFragment", "Error: ${error.message}")
            })

        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest)
    }

    companion object {
        fun newInstance(token: String, userId: String, name: String, email: String, prodiId: String, password: String): AkunFragment {
            val fragment = AkunFragment()
            val args = Bundle()
            args.putString("token", token)
            args.putString("userId", userId)
            args.putString("name", name)
            args.putString("email", email)
            args.putString("prodiId", prodiId)
            args.putString("password", password)
            fragment.arguments = args
            return fragment
        }
    }
}
