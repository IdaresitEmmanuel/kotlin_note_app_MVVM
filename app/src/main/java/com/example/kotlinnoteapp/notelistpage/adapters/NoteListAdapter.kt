package com.example.kotlinnoteapp.notelistpage.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinnoteapp.R
import com.example.kotlinnoteapp.data.NoteModel

class NoteListAdapter : RecyclerView.Adapter<NoteListAdapter.ViewHolder>() {

    var noteList = NoteModel.getExampleList(10)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.model_note_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteListAdapter.ViewHolder, position: Int) {
        val model = noteList[position]
        holder.title.text = model.title
        holder.date.text = model.getFormattedDate()
        holder.body.text = model.body
        holder.checkBox.isChecked = model.isSelected
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var checkBox: CheckBox = itemView.findViewById(R.id.note_checkbox)
        var title : TextView = itemView.findViewById(R.id.note_title)
        var date : TextView = itemView.findViewById(R.id.note_date)
        var body : TextView = itemView.findViewById(R.id.note_body)

    }
}