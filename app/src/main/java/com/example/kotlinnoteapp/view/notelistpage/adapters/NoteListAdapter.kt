package com.example.kotlinnoteapp.view.notelistpage.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinnoteapp.R
import com.example.kotlinnoteapp.data.models.NoteModel
import com.example.kotlinnoteapp.view.notelistpage.adapters.listeners.ItemClickListener
import com.example.kotlinnoteapp.view.notelistpage.adapters.listeners.ItemLongClickListener

class NoteListAdapter(
    private var noteList: ArrayList<NoteModel>,
    private var clickListener: ItemClickListener,
    private var longClickListener: ItemLongClickListener )
    : RecyclerView.Adapter<NoteListAdapter.ViewHolder>() {
    var isActionMode : Boolean = false
    set(value) {
        field = value
        refresh()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh(){
        notifyDataSetChanged()
    }

    fun selectNote(position: Int) {
        noteList[position]
            .isSelected = !noteList[position].isSelected
        notifyItemChanged(position)
    }

    fun unselectAll() {
        for (i in noteList.indices) {
            noteList[i].isSelected = false
        }
    }

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
        holder.checkBox.isClickable = false
        holder.checkBox.visibility = if(isActionMode) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener {
            clickListener.itemClick(
                model,
                position
            )
        }
        holder.itemView.setOnLongClickListener { view: View? ->
            longClickListener.onItemLongClick(view!!, model, position)
            true
        }
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