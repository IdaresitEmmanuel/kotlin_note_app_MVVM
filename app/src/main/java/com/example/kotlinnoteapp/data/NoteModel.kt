package com.example.kotlinnoteapp.data

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class NoteModel(
    var id:Int = -1,
    var title:String,
    var date: Date = Date(),
    var body: String,
    var isSelected:Boolean = false){

    companion object {
        fun getEmptyNote() : NoteModel = NoteModel(title = "", body = "")
        fun getExampleList(count: Int): ArrayList<NoteModel> {
            val tempList = ArrayList<NoteModel>()
            for (i in 0 until count) {
                tempList.add(NoteModel(title="Test Title", body = "Absolute.y a test body".repeat(10)))
            }
            return tempList
        }
    }

    fun getFormattedDate() : String = SimpleDateFormat("MMM dd yyyy HH:mm aa", Locale.ENGLISH).format(date)

    fun getISOStringDate() : String = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).format(date)


}
