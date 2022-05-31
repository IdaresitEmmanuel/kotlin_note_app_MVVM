package com.example.kotlinnoteapp.view.notelistpage.adapters.listeners

import android.view.View
import com.example.kotlinnoteapp.data.models.NoteModel

abstract class ItemLongClickListener {
    abstract fun onItemLongClick(v: View, model: NoteModel, position: Int)
}