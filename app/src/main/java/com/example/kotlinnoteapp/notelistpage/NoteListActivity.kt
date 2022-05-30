package com.example.kotlinnoteapp.notelistpage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinnoteapp.R
import com.example.kotlinnoteapp.notelistpage.adapters.NoteListAdapter

class NoteListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        setUpUI()
    }

    private fun setUpUI(){
        val recyclerView = findViewById<RecyclerView>(R.id.noteListRV)
        val adapter = NoteListAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}