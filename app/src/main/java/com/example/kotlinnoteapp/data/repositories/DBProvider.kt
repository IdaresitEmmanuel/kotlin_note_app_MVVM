package com.example.kotlinnoteapp.data.repositories

import android.content.ContentValues
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.kotlinnoteapp.MyApplication
import com.example.kotlinnoteapp.constants.NoteFilter
import com.example.kotlinnoteapp.data.models.NoteModel
import com.example.kotlinnoteapp.util.ISOStringToDate
import java.util.*

class DBProvider private constructor() :
    SQLiteOpenHelper(MyApplication.appContext, DB_NAME, null, VERSION) {
    private var db: SQLiteDatabase? = null
    private fun openDatabase() {
        db = this.writableDatabase
    }

    private fun closeDatabase() {
        db!!.close()
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(CREATE_NOTE_TABLE)
        } catch (exception: SQLException) {
            Log.e("error getting notes", exception.toString())
        }
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //drop table if already exist
        try {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $NOTE_TABLE")
        } catch (exception: SQLException) {
            Log.e("error getting notes", exception.toString())
        }
        //create new table
        onCreate(sqLiteDatabase)
    }

    val notes: ArrayList<NoteModel>
        get() {
            openDatabase()
            val noteModelList: ArrayList<NoteModel> = ArrayList<NoteModel>()
            val filter: NoteFilter = AppPreferences.instance?.filter ?: NoteFilter.none
            var cur: Cursor? = null
            db!!.beginTransaction()
            val orderString: String? = if (filter == NoteFilter.byDate)
                "$NOTE_DATE DESC" else null

            try {
                cur = db!!.query(NOTE_TABLE, null, null, null, null, null, orderString)
                if (cur != null) if (cur.moveToFirst()) do {
                    val id = cur.getInt(cur.getColumnIndexOrThrow(NOTE_ID))
                    val title = cur.getString(cur.getColumnIndexOrThrow(NOTE_TITLE))
                    val date = cur.getString(cur.getColumnIndexOrThrow(NOTE_DATE))
                    val body = cur.getString(cur.getColumnIndexOrThrow(NOTE_BODY))
                    noteModelList.add(NoteModel(id, title, ISOStringToDate(date), body))
                } while (cur.moveToNext())
            } catch (e: Exception) {
                Log.e("error getting notes", e.toString())
            } finally {
                db!!.endTransaction()
                closeDatabase()
                assert(cur != null)
                cur!!.close()
            }
//            if (filter.equals(NoteFilter.alphabetical)) {
//
//
//            }
            return noteModelList
        }

    fun addNote(noteModel: NoteModel): Boolean {
        openDatabase()
        val cv = ContentValues()
        cv.put(NOTE_TITLE, noteModel.title)
        cv.put(NOTE_DATE, noteModel.getISOStringDate())
        cv.put(NOTE_BODY, noteModel.body)
        val result = db!!.insert(NOTE_TABLE, null, cv)
        closeDatabase()
        return result > -1
    }

    val lastNoteId: Int
        get() {
            openDatabase()
            var id = -1
            var cur: Cursor? = null
            db!!.beginTransaction()
            try {
                cur = db!!.query(NOTE_TABLE, null, null, null, null, null, null)
                if (cur != null) {
                    if (cur.moveToLast()) {
                        id = cur.getInt(cur.getColumnIndexOrThrow(NOTE_ID))
                    }
                }
            } catch (e: Exception) {
                Log.e("error getting notes", e.toString())
            } finally {
                db!!.endTransaction()
                cur!!.close()
                closeDatabase()
            }
            return id
        }

    fun updateNote(noteModel: NoteModel): Boolean {
        openDatabase()
        val cv = ContentValues()
        cv.put(NOTE_TITLE, noteModel.title)
        cv.put(NOTE_BODY, noteModel.body)
        val result = db!!.update(
            NOTE_TABLE,
            cv,
            "$NOTE_ID=?",
            arrayOf(java.lang.String.valueOf(noteModel.id))
        )
        closeDatabase()
        return result > -1
    }

    fun deleteNote(id: Int): Boolean {
        openDatabase()
        val result = db!!.delete(NOTE_TABLE, "$NOTE_ID=?", arrayOf(id.toString()))
        closeDatabase()
        return result > -1
    }

    companion object {
        private const val VERSION = 3
        private const val DB_NAME = "noteDatabase"
        private const val NOTE_TABLE = "noteTable"
        private const val NOTE_ID = "id"
        private const val NOTE_TITLE = "title"
        private const val NOTE_DATE = "noteDate"
        private const val NOTE_BODY = "noteBody"
        private val CREATE_NOTE_TABLE = String.format(
            "CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT)",
            NOTE_TABLE,
            NOTE_ID,
            NOTE_TITLE,
            NOTE_DATE,
            NOTE_BODY
        )
        var instance: DBProvider? = null
            get() {
                if (field == null) {
                    field = DBProvider()
                }
                return field
            }
            private set
    }
}