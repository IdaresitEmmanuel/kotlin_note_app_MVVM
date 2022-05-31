package com.example.kotlinnoteapp.view.notelistpage.adapters.listeners

import com.example.kotlinnoteapp.data.models.NoteModel


abstract class ItemClickListener {
    abstract fun itemClick(model: NoteModel, position: Int)
}