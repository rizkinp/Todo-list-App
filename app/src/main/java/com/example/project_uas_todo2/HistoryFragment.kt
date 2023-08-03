package com.example.project_uas_todo2

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class HistoryFragment : Fragment() {
    private lateinit var listView: ListView
    private lateinit var adapter: HistoryAdapter
    private val historyList: MutableList<History> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        listView = view.findViewById(R.id.listView)

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedHistory = historyList[position]
            showHistoryDetails(selectedHistory)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataForListView()
    }

    private fun getDataForListView() {
        val userId = arguments?.getString("userId")

        val url = "http://192.168.60.64/project_uas_todo/public/api/history/user/$userId" // Ganti dengan URL API Anda

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    historyList.clear()

                    for (i in 0 until response.length()) {
                        val history = response.getJSONObject(i)
                        val historyId = history.getString("id")
                        val historyUserId = history.getString("user_id")
                        val historyTopic = history.getString("topic")
                        val historyStatus = history.getString("status")

                        // Buat objek History dan tambahkan ke daftar history
                        val historyDateCompleted = history.optString("date_completed")
                        val historyObj = History(historyId, historyUserId, historyTopic, historyStatus, historyDateCompleted)

                        historyList.add(historyObj)
                    }

                    // Buat adapter kustom untuk ListView menggunakan daftar history
                    adapter = HistoryAdapter(requireContext(), historyList)
                    listView.adapter = adapter

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
            })

        Volley.newRequestQueue(requireContext()).add(jsonArrayRequest)
    }
    private fun showHistoryDetails(history: History) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Rincian History")
        dialogBuilder.setMessage("Topic: ${history.topic}\nStatus: ${history.status}\nDate Completed: ${history.dateCompleted}")
        dialogBuilder.setPositiveButton("Tutup") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }


    companion object {
        fun newInstance(userId: String, prodiId: String): HistoryFragment {
            val fragment = HistoryFragment()
            val args = Bundle()
            args.putString("userId", userId)
            args.putString("prodiId", prodiId)
            fragment.arguments = args
            return fragment
        }
    }


}



