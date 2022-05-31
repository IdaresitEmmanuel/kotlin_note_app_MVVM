package com.example.kotlinnoteapp.view.editnotepage

import android.util.Log
import androidx.lifecycle.ViewModel

import com.example.kotlinnoteapp.data.models.NoteModel
import com.example.kotlinnoteapp.data.repositories.DBProvider

class EditNoteViewModel : ViewModel() {
    private var note: NoteModel = NoteModel.getEmptyNote()
    fun setNote(model: NoteModel) {
        note = model
    }

    val id: Int
        get() = note.id
    val date: String
        get() = note.getFormattedDate()
    var title: String
        get() = note.title
        set(title) {
            note.title = title
        }
    var noteBody: String
        get() = note.body
        set(body) {
            note.body = body
        }

    fun saveNote(): String {
        val tempString: String = if (note.id > -1) {
            val result: Boolean = DBProvider.instance?.updateNote(note) ?: false
            if (result) {
                "note updated!"
            } else {
                "something went wrong!"
            }
        } else {
            val result: Boolean = DBProvider.instance?.addNote(note) ?: false
            setNewId()
            if (result) {
                "note successfully added to database!"
            } else {
                "something went wrong!, try again"
            }
        }
        return tempString
    }

    fun setNewId() {
        note.id = DBProvider.instance?.lastNoteId ?: -1
    }

    fun deleteFromDatabase(): Boolean {
        return DBProvider.instance?.deleteNote(note.id) ?: false
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("EditNoteViewModel", "ViewModel has been destroyed")
    }

    init {
        Log.i("EditNoteViewModel", "ViewModel has been created")
    }
}