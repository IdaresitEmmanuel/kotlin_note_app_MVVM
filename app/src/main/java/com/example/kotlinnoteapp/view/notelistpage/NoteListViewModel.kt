package com.example.kotlinnoteapp.view.notelistpage

import androidx.lifecycle.ViewModel
import com.example.kotlinnoteapp.data.models.NoteModel
import com.example.kotlinnoteapp.data.repositories.DBProvider
import kotlin.collections.ArrayList

class NoteListViewModel : ViewModel() {
    private var noteList: ArrayList<NoteModel> = ArrayList()
    var noteListFiltered: ArrayList<NoteModel> = ArrayList()
    var isActionMode = false
    fun refreshNotes() {
        noteList = DBProvider.instance?.notes ?: ArrayList()
        if(noteListFiltered.isNotEmpty())
            for (i in 0 until noteListFiltered.size-1) {
            var shouldDelete = true
            for (model in noteList) {
                if (model.id == noteListFiltered[i].id) {
                    shouldDelete = false
                    break
                }
            }
            if (shouldDelete) {
                noteListFiltered.remove(noteListFiltered[i])
            }
        }
    }

    private fun refreshAllNotes() {
        noteList = DBProvider.instance?.notes ?: ArrayList()
        noteListFiltered.addAll(noteList)
    }

    fun setIsActionMode(value: Boolean) {
        isActionMode = value
    }

    val selectedList: ArrayList<NoteModel>
        get() {
            val tempList: ArrayList<NoteModel> = ArrayList()
            for (i in noteList.indices) {
                if (noteList[i].isSelected) {
                    tempList.add(noteList[i])
                }
            }
            return tempList
        }

    fun deleteSelectedNotes(): Boolean {
        val selectedList: ArrayList<NoteModel> = selectedList
        var isDeleteSuccessful = false
        for (i in selectedList.indices) {
            isDeleteSuccessful =
                DBProvider.instance?.deleteNote(selectedList[i].id) ?: false
        }
        return isDeleteSuccessful
    }

    // Filter
    fun filterList(query: String) {
        noteListFiltered.clear()
        if (query.isEmpty()) {
            noteListFiltered.addAll(noteList)
            return
        }
        for (note in noteList) {
            if (note.title.lowercase().contains(query.lowercase())) {
                noteListFiltered.add(note)
            }
        }
    }

    init {
        refreshAllNotes()
    }
}