package com.example.project_uas_todo2

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class HistoryAdapter(context: Context, private val historyList: List<History>) : ArrayAdapter<History>(context, 0, historyList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        val viewHolder: ViewHolder

        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.list_item_history, parent, false)
            viewHolder = ViewHolder(itemView)
            itemView.tag = viewHolder
        } else {
            viewHolder = itemView.tag as ViewHolder
        }

        val history = historyList[position]

        viewHolder.textViewTopic.text = history.topic
        viewHolder.textViewStatus.text = history.status

        if (history.status == "Completed") {
            // Jika status Completed, set teks topik dengan coretan
            viewHolder.textViewTopic.paintFlags = viewHolder.textViewTopic.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            // Jika status tidak Completed, hapus coretan pada teks topik
            viewHolder.textViewTopic.paintFlags = viewHolder.textViewTopic.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        return itemView!!
    }

    private class ViewHolder(view: View) {
        val textViewTopic: TextView = view.findViewById(R.id.textViewTopic)
        val textViewStatus: TextView = view.findViewById(R.id.textViewStatus)
    }
}

