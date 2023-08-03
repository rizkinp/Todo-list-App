package com.example.project_uas_todo2

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.Calendar
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {
    //adapter menampilkan daftar tugas berdasarkan id user
    private lateinit var tugasList: MutableList<Tugas>
    private lateinit var tugasAdapter: TugasAdapter

    //spinner menampilkan mata kuliah berdasarkan id proid
    private lateinit var spinnerMatkul: Spinner
    private lateinit var mataKuliahList: MutableList<MataKuliah>
    private lateinit var mataKuliahAdapter: ArrayAdapter<String>
    private lateinit var greeting:TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val listView: ListView = view.findViewById(R.id.list_item_tugas)

        greeting = view.findViewById(R.id.greeting)
        val name = arguments?.getString("name")
        val greetingMessage = "Hay, $name!" // Ganti $userId dengan data yang sesuai dari intent sebelumnya
        greeting.text = greetingMessage

        // ...

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = tugasList[position]
            showUpdateDeleteDialog(selectedItem)
        }

        mataKuliahList = mutableListOf()

        tugasList = mutableListOf()
        tugasAdapter = TugasAdapter(requireContext(), tugasList)
        listView.adapter = tugasAdapter

        getDataForListView()

        val fabAdd: FloatingActionButton = view.findViewById(R.id.fabAdd)
        fabAdd.setOnClickListener {
            showInputDialog()
        }

//        listView.setOnItemClickListener { _, _, position, _ ->
//            val selectedItem = tugasList[position]
//            showInputDialog(selectedItem)
//        }


        return view
    }

    private fun getDataForListView() {
        val userId = arguments?.getString("userId")
        val prodiId = arguments?.getString("prodiId")

        val url = "http://192.168.60.64/project_uas_todo/public/api/tugas/user/$userId"
        val params = HashMap<String, String>()
        params["user_id"] = userId ?: ""
        params["prodi_id"] = prodiId ?: ""

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    tugasList.clear()

                    for (i in 0 until response.length()) {
                        val tugas = response.getJSONObject(i)
                        val tugasId = tugas.getString("id")
                        val tugasUserId = tugas.getString("user_id")
                        val tugasTopic = tugas.getString("topic")
                        val tugasDescription = tugas.getString("description")
                        val tugasMataKuliahId = tugas.getString("mata_kuliah_id")
                        val tugasDueDate = tugas.getString("due_date")

                        // Mendapatkan nama mata kuliah berdasarkan mataKuliahId
                        CoroutineScope(Dispatchers.Main).launch {
                            val mataKuliahName = getMataKuliahName(tugasMataKuliahId)

                            // Buat objek Tugas dan tambahkan ke daftar tugas
                            val tugasObj = Tugas(tugasId, tugasUserId, tugasTopic, tugasDescription, mataKuliahName, tugasDueDate)
                            tugasList.add(tugasObj)
                            tugasAdapter.notifyDataSetChanged()
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("HomeFragment", "Error: ${error.message}")
            })

        Volley.newRequestQueue(requireContext()).add(jsonArrayRequest)
    }

    //Fungsi menampilkan nama mata kuliah dari tugas
    private suspend fun getMataKuliahName(mataKuliahId: String): String = withContext(Dispatchers.IO) {
        val mataKuliahUrl = "http://192.168.60.64/project_uas_todo/public/api/mata-kuliah/$mataKuliahId"
        var mataKuliahName = ""

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, mataKuliahUrl, null,
            { response ->
                try {
                    mataKuliahName = response.getString("name")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("HomeFragment", "Error: ${error.message}")
            })

        val requestQueue = Volley.newRequestQueue(requireContext())
        val responseFuture = RequestFuture.newFuture<JSONObject>()
        requestQueue.add(jsonObjectRequest)

        try {
            val response = responseFuture.get(1, TimeUnit.SECONDS)
            mataKuliahName = response.getString("name")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mataKuliahName
    }

    // Fungsi mengambil mata kuliah berdasarkan prodi untuk dialog
    private fun getMataKuliahDataForDialog2(adapter: ArrayAdapter<String>) {
        val prodiId = arguments?.getString("prodiId")
        val url = "http://192.168.60.64/project_uas_todo/public/api/mata-kuliah/prodi/$prodiId"

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    for (i in 0 until response.length()) {
                        val mataKuliah = response.getJSONObject(i)
                        val mataKuliahName = mataKuliah.getString("name")

                        // Tambahkan nama mata kuliah ke adapter
                        adapter.add(mataKuliahName)
                    }
                    // Refresh adapter setelah menambahkan semua mata kuliah
                    adapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("HomeFragment", "Error: ${error.message}")
            })

        Volley.newRequestQueue(requireContext()).add(jsonArrayRequest)
    }
    private fun getMataKuliahDataForDialog(adapter: ArrayAdapter<String>) {
        val prodiId = arguments?.getString("prodiId")
        val url = "http://192.168.60.64/project_uas_todo/public/api/mata-kuliah/prodi/$prodiId"

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    mataKuliahList.clear()

                    for (i in 0 until response.length()) {
                        val mataKuliah = response.getJSONObject(i)
                        val mataKuliahId = mataKuliah.getInt("id")
                        val mataKuliahName = mataKuliah.getString("name")

                        // Tambahkan objek MataKuliah ke mataKuliahList
                        mataKuliahList.add(MataKuliah(mataKuliahId, mataKuliahName))

                        // Tambahkan nama mata kuliah ke adapter
                        adapter.add(mataKuliahName)
                    }

                    // Refresh adapter setelah menambahkan semua mata kuliah
                    adapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("HomeFragment", "Error: ${error.message}")
            })

        Volley.newRequestQueue(requireContext()).add(jsonArrayRequest)
    }



    private fun showInputDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_input_data, null, false)

        dialogBuilder.setView(dialogView)

        // Inisialisasi spinner mata kuliah di dalam dialog
        val spinnerMatkulDialog: Spinner = dialogView.findViewById(R.id.spinnerMatkul)
        val mataKuliahAdapterDialog: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item)
        mataKuliahAdapterDialog.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMatkulDialog.adapter = mataKuliahAdapterDialog

        // Panggil metode untuk mendapatkan data mata kuliah
        getMataKuliahDataForDialog(mataKuliahAdapterDialog)

        //membuat kalendar
        val button_date = dialogView.findViewById<Button>(R.id.buttonDueDate)
        button_date.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // Mengubah format tanggal menjadi string yang diinginkan (misalnya: dd/MM/yyyy)
                val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                button_date.setText(formattedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

        //mengambil inputan user
        val editTextTopic: EditText = dialogView.findViewById(R.id.editTextTopic)
        val editTextDescription: EditText = dialogView.findViewById(R.id.editTextDescription)

        dialogBuilder.setPositiveButton("Simpan") { dialog, _ ->
            val selectedMataKuliahName = spinnerMatkulDialog.selectedItem.toString()
            val topic = editTextTopic.text.toString().trim()
            val description = editTextDescription.text.toString().trim()

            // Temukan objek MataKuliah berdasarkan nama mata kuliah yang dipilih
            val selectedMataKuliah = mataKuliahList.find { it.name == selectedMataKuliahName }

            // Melakukan validasi inputan (misalnya, tidak boleh kosong)
            if (topic.isNotEmpty() && description.isNotEmpty() && selectedMataKuliah != null) {
                val userId = arguments?.getString("userId")
                val prodiId = arguments?.getString("prodiId")

                val url = "http://192.168.60.64/project_uas_todo/public/api/tugas"
                val jsonObject = JSONObject()
                jsonObject.put("user_id", userId)
                jsonObject.put("mata_kuliah_id", selectedMataKuliah.id)
                jsonObject.put("topic", topic)
                jsonObject.put("description", description)
                jsonObject.put("due_date", button_date.text.toString())
                // Tambahkan data lain yang diperlukan sesuai kebutuhan

                val requestBody = jsonObject.toString()

                val stringRequest = object : StringRequest(Method.POST, url,
                    { response ->
                        // Tanggapan sukses dari server
                        Toast.makeText(requireContext(), "Tugas berhasil disimpan!", Toast.LENGTH_SHORT).show()
                        getDataForListView()
                    },
                    { error ->
                        // Tanggapan gagal dari server
                        Toast.makeText(requireContext(), "Gagal menyimpan tugas: ${error.message}", Toast.LENGTH_SHORT).show()
                    }) {
                    override fun getBodyContentType(): String {
                        return "application/json"
                    }

                    override fun getBody(): ByteArray {
                        return requestBody.toByteArray(Charset.defaultCharset())
                    }
                }

                Volley.newRequestQueue(requireContext()).add(stringRequest)
            } else {
                Toast.makeText(requireContext(), "Harap isi semua bidang!", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = dialogBuilder.create()
        dialog.show()
    }


//    private fun showUpdateDeleteDialog(selectedTugas: Tugas) {
//        val dialogBuilder = AlertDialog.Builder(requireContext())
//        val inflater: LayoutInflater = layoutInflater
//        val dialogView: View = inflater.inflate(R.layout.dialog_input_data, null, false)
//
//        dialogBuilder.setView(dialogView)
//
//        // Inisialisasi spinner mata kuliah di dalam dialog
//        val spinnerMatkulDialog: Spinner = dialogView.findViewById(R.id.spinnerMatkul)
//        val mataKuliahAdapterDialog: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item)
//        mataKuliahAdapterDialog.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerMatkulDialog.adapter = mataKuliahAdapterDialog
//
//        // Panggil metode untuk mendapatkan data mata kuliah
//        getMataKuliahDataForDialog(mataKuliahAdapterDialog)
//
//        // Set nilai default untuk spinner mata kuliah
//        val selectedMataKuliahIndex = mataKuliahList.indexOfFirst { it.name == selectedTugas.mataKuliahId }
//        spinnerMatkulDialog.setSelection(selectedMataKuliahIndex)
//
//        //membuat kalendar
//        val button_date = dialogView.findViewById<Button>(R.id.buttonDueDate)
//        button_date.setOnClickListener {
//            val calendar = Calendar.getInstance()
//            val year = calendar.get(Calendar.YEAR)
//            val month = calendar.get(Calendar.MONTH)
//            val day = calendar.get(Calendar.DAY_OF_MONTH)
//
//            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
//                // Mengubah format tanggal menjadi string yang diinginkan (misalnya: dd/MM/yyyy)
//                val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
//                button_date.setText(formattedDate)
//            }, year, month, day)
//
//            datePickerDialog.show()
//        }
//
//        //mengambil inputan user
//        val editTextTopic: EditText = dialogView.findViewById(R.id.editTextTopic)
//        val editTextDescription: EditText = dialogView.findViewById(R.id.editTextDescription)
//
//        // Mengisi data tugas yang dipilih ke dalam form dialog
//        editTextTopic.setText(selectedTugas.topic)
//        editTextDescription.setText(selectedTugas.description)
//        button_date.setText(selectedTugas.dueDate)
//
//        dialogBuilder.setPositiveButton("Update") { dialog, _ ->
//            val selectedMataKuliahName = spinnerMatkulDialog.selectedItem.toString()
//            val topic = editTextTopic.text.toString().trim()
//            val description = editTextDescription.text.toString().trim()
//
//            // Temukan objek MataKuliah berdasarkan nama.
//            val selectedMataKuliah = mataKuliahList.find { it.name.equals(selectedMataKuliahName, ignoreCase = true) }
//
//            if (selectedMataKuliah != null && topic.isNotEmpty() && description.isNotEmpty()) {
//                selectedTugas.mataKuliahId = selectedMataKuliah.name
//                selectedTugas.topic = topic
//                selectedTugas.description = description
//                selectedTugas.dueDate = button_date.text.toString()
//
//                // Update data tugas pada adapter
//                tugasAdapter.notifyDataSetChanged()
//
//                // TODO: Lakukan aksi update data di database atau tempat penyimpanan lainnya
//
//                dialog.dismiss()
//            } else {
//                Toast.makeText(requireContext(), "Silakan isi semua field", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//
//        dialogBuilder.setNegativeButton("Delete") { dialog, _ ->
//            // Hapus data tugas dari adapter
//            tugasList.remove(selectedTugas)
//            tugasAdapter.notifyDataSetChanged()
//
//            // TODO: Lakukan aksi hapus data di database atau tempat penyimpanan lainnya
//
//            dialog.dismiss()
//        }
//
//        val alertDialog = dialogBuilder.create()
//        alertDialog.show()
//    }

    private fun showUpdateDeleteDialog(selectedTugas: Tugas) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Pilih tindakan:")
        dialogBuilder.setPositiveButton("Update") { _, _ ->
            updateTugas(selectedTugas)
        }
        dialogBuilder.setNegativeButton("Selesai") { _, _ ->
            deleteTugas(selectedTugas)
        }
        dialogBuilder.setNeutralButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }



    private fun updateTugas(selectedTugas: Tugas) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_input_data, null, false)

        dialogBuilder.setView(dialogView)

        // Inisialisasi spinner mata kuliah di dalam dialog
        val spinnerMatkulDialog: Spinner = dialogView.findViewById(R.id.spinnerMatkul)
        val mataKuliahAdapterDialog: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item)
        mataKuliahAdapterDialog.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMatkulDialog.adapter = mataKuliahAdapterDialog

        // Panggil metode untuk mendapatkan data mata kuliah
        getMataKuliahDataForDialog(mataKuliahAdapterDialog)

        // Set nilai default untuk spinner mata kuliah
        val selectedMataKuliahIndex = mataKuliahList.indexOfFirst { it.name == selectedTugas.mataKuliahId }
        spinnerMatkulDialog.setSelection(selectedMataKuliahIndex)

        // Mengatur tanggal
        val button_date = dialogView.findViewById<Button>(R.id.buttonDueDate)
        button_date.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // Mengubah format tanggal menjadi string yang diinginkan (misalnya: dd/MM/yyyy)
                val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                button_date.text = formattedDate
            }, year, month, day)

            datePickerDialog.show()
        }

        val editTextTopic: EditText = dialogView.findViewById(R.id.editTextTopic)
        val editTextDescription: EditText = dialogView.findViewById(R.id.editTextDescription)

        // Mengisi data tugas yang dipilih ke dalam form dialog
        editTextTopic.setText(selectedTugas.topic)
        editTextDescription.setText(selectedTugas.description)
        button_date.text = selectedTugas.dueDate

        dialogBuilder.setPositiveButton("Update") { dialog, _ ->
            val selectedMataKuliahName = spinnerMatkulDialog.selectedItem.toString()
            val topic = editTextTopic.text.toString().trim()
            val description = editTextDescription.text.toString().trim()

            // Temukan objek MataKuliah berdasarkan nama.
            val selectedMataKuliah = mataKuliahList.find { it.name.equals(selectedMataKuliahName, ignoreCase = true) }

            if (selectedMataKuliah != null && topic.isNotEmpty() && description.isNotEmpty()) {
                selectedTugas.mataKuliahId = selectedMataKuliah.name
                selectedTugas.topic = topic
                selectedTugas.description = description
                selectedTugas.dueDate = button_date.text.toString()

                // Update data tugas pada adapter
                tugasAdapter.notifyDataSetChanged()

                // Lakukan aksi update data di database atau tempat penyimpanan lainnya
                val url = "http://192.168.60.64/project_uas_todo/public/api/tugas/${selectedTugas.id}"
                val jsonObject = JSONObject()
                jsonObject.put("user_id", selectedTugas.userId)
                jsonObject.put("mata_kuliah_id", selectedMataKuliah.id)
                jsonObject.put("topic", topic)
                jsonObject.put("description", description)
                jsonObject.put("due_date", button_date.text.toString())
                // Tambahkan data lain yang diperlukan sesuai kebutuhan

                val requestBody = jsonObject.toString()

                val stringRequest = object : StringRequest(Method.PUT, url,
                    { response ->
                        // Tanggapan sukses dari server
                        Toast.makeText(requireContext(), "Tugas berhasil diperbarui!", Toast.LENGTH_SHORT).show()

                    },
                    { error ->
                        // Tanggapan gagal dari server
                        Toast.makeText(requireContext(), "Gagal memperbarui tugas: ${error.message}", Toast.LENGTH_SHORT).show()
                    }) {
                    override fun getBodyContentType(): String {
                        return "application/json"
                    }

                    override fun getBody(): ByteArray {
                        return requestBody.toByteArray(Charset.defaultCharset())
                    }
                }

                Volley.newRequestQueue(requireContext()).add(stringRequest)

                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Silakan isi semua field", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBuilder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }


    private fun deleteTugas(selectedTugas: Tugas) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Apakah tugas telah selesai?")
        dialogBuilder.setPositiveButton("Ya") { dialog, _ ->
            // Hapus data tugas dari adapter
            tugasList.remove(selectedTugas)
            tugasAdapter.notifyDataSetChanged()

            // Lakukan aksi hapus data di database atau tempat penyimpanan lainnya
            val url = "http://192.168.60.64/project_uas_todo/public/api/tugas/${selectedTugas.id}"

            val stringRequest = object : StringRequest(Method.DELETE, url,
                { response ->
                    // Tanggapan sukses dari server
                    Toast.makeText(requireContext(), "Tugas telah selesai!", Toast.LENGTH_SHORT).show()

                    insertTugasToHistory(selectedTugas)
                },
                { error ->
                    // Tanggapan gagal dari server
                    Toast.makeText(requireContext(), "Tugas belum selesai: ${error.message}", Toast.LENGTH_SHORT).show()
                }) {
                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }

            Volley.newRequestQueue(requireContext()).add(stringRequest)

            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }


    private fun insertTugasToHistory(selectedTugas: Tugas) {
        val historyUrl = "http://192.168.60.64/project_uas_todo/public/api/history"
        val requestQueue = Volley.newRequestQueue(requireContext())

        val historyRequest = object : StringRequest(Method.POST, historyUrl,
            { response ->
                // Tanggapan sukses dari server
                Log.d(TAG, "Tugas berhasil dimasukkan ke history!")
            },
            { error ->
                // Tanggapan gagal dari server
                Log.e(TAG, "Gagal memasukkan tugas ke history: ${error.message}")
            }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                val jsonObject = JSONObject()
                jsonObject.put("user_id", selectedTugas.userId)
                jsonObject.put("topic", selectedTugas.topic)
                jsonObject.put("status", "Completed")

                return jsonObject.toString().toByteArray()
            }
        }

        requestQueue.add(historyRequest)
    }




    companion object {
        fun newInstance(userId: String, prodiId: String, name: String): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString("userId", userId)
            args.putString("prodiId", prodiId)
            args.putString("name", name)
            fragment.arguments = args
            return fragment
        }
    }

}





