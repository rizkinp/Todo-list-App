package com.example.project_uas_todo2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TugasAdapter(context: Context, tugasList: List<Tugas>) :
    ArrayAdapter<Tugas>(context, 0, tugasList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        val tugas = getItem(position)

        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.list_item_tugas, parent, false)
        }

//        val textViewId: TextView = itemView!!.findViewById(R.id.textViewId)
//        val textViewUserId: TextView = itemView.findViewById(R.id.textViewUserId)
        val textViewTopic: TextView = itemView!!.findViewById(R.id.textViewTopic)
        val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        val textViewMataKuliahId: TextView = itemView.findViewById(R.id.textViewMataKuliahId)
        val textViewDueDate: TextView = itemView.findViewById(R.id.textViewDueDate)

        // Set text values for each TextView
//        textViewId.text = "ID: ${tugas?.id}"
//        textViewUserId.text = "User ID: ${tugas?.userId}"
        textViewTopic.text = "Topic: ${tugas?.topic}"
        textViewDescription.text = "Description: ${tugas?.description}"
        textViewMataKuliahId.text = "Mata Kuliah ID: ${tugas?.mataKuliahId}"
        textViewDueDate.text = "Due Date: ${tugas?.dueDate}"

        return itemView
    }
}

//class TugasAdapter(private val tugasList: List<Tugas>, private val itemClickListener: OnItemClickListener) :
//    RecyclerView.Adapter<TugasAdapter.TugasViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TugasViewHolder {
//        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.dialog_input_data, parent, false)
//        return TugasViewHolder(itemView)
//    }
//
//    override fun onBindViewHolder(holder: TugasViewHolder, position: Int) {
//        val currentItem = tugasList[position]
//
//        holder.textViewTopic.text = currentItem.topic
//        holder.textViewDescription.text = currentItem.description
//        holder.textViewDueDate.text = currentItem.dueDate
//        holder.textViewMataKuliah.text = currentItem.mataKuliahId
//
//        holder.itemView.setOnClickListener {
//            itemClickListener.onItemClick(currentItem)
//        }
//
////        holder.buttonUpdate.setOnClickListener {
////            itemClickListener.onUpdateClick(currentItem)
////        }
////
////        holder.buttonDelete.setOnClickListener {
////            itemClickListener.onDeleteClick(currentItem)
////        }
//    }
//
//    override fun getItemCount() = tugasList.size
//
//    inner class TugasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val textViewTopic: TextView = itemView.findViewById(R.id.textViewTopic)
//        val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
//        val textViewDueDate: TextView = itemView.findViewById(R.id.textViewDueDate)
//        val textViewMataKuliah: TextView = itemView.findViewById(R.id.spinnerMatkul)
////        val buttonUpdate: Button = itemView.findViewById(R.id.buttonUpdate)
////        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)
//    }
//
//    interface OnItemClickListener {
//        fun onItemClick(tugas: Tugas)
//        fun onUpdateClick(tugas: Tugas)
//        fun onDeleteClick(tugas: Tugas)
//    }
//}

