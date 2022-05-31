package com.example.kotlinnoteapp.view.notelistpage

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinnoteapp.R
import com.example.kotlinnoteapp.data.models.NoteModel
import com.example.kotlinnoteapp.view.editnotepage.EditNoteActivity
import com.example.kotlinnoteapp.view.notelistpage.adapters.NoteListAdapter
import com.example.kotlinnoteapp.view.notelistpage.adapters.listeners.ItemClickListener
import com.example.kotlinnoteapp.view.notelistpage.adapters.listeners.ItemLongClickListener
import com.example.kotlinnoteapp.view.notelistpage.dialogs.DeleteDialog
import com.example.kotlinnoteapp.view.notelistpage.dialogs.FilterDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NoteListActivity : AppCompatActivity(), FilterDialog.FilterDialogListener, DeleteDialog.DeleteDialogListener {
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : NoteListAdapter
    private lateinit var viewModel : NoteListViewModel
    private lateinit var searchBar: LinearLayoutCompat
    private lateinit var actionBar:LinearLayoutCompat
    private lateinit var closeActionBarIbn: ImageButton
    private lateinit var deleteBtn:ImageButton
    private lateinit var filterBtn:ImageButton
    private lateinit var selectedCountTv: TextView
    private lateinit var searchEt: EditText
    private lateinit var fab : FloatingActionButton

    override fun onBackPressed() {
        if (viewModel.isActionMode) {
            setActionMode(false)
            adapter.unselectAll()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)
        viewModel = ViewModelProvider(this)[NoteListViewModel::class.java]
        setUpUI()
    }

    private fun setUpUI(){
        closeActionBarIbn = findViewById(R.id.close_action_bar_ibn)
        closeActionBarIbn.setOnClickListener { onBackPressed() }
        searchBar = findViewById(R.id.search_bar)
        actionBar = findViewById(R.id.action_bar)

        searchEt = findViewById(R.id.search_et)
        searchEt.addTextChangedListener(getSearchTextWatcher())

        selectedCountTv = findViewById(R.id.selected_count_tv)

        filterBtn = findViewById(R.id.filter_button)
        filterBtn.setOnClickListener {
            val dialog = FilterDialog()
            dialog.show(supportFragmentManager, "filter dialog")
        }

        deleteBtn = findViewById(R.id.delete_ibn)
        deleteBtn.setOnClickListener {
            val count: Int = viewModel.selectedList.size
            val msg: String = if (count == 1) {
                "Are you sure you want to delete $count item?"
            } else {
                "Are you sure you want to delete $count items?"
            }
            val dialog = DeleteDialog(msg)
            dialog.show(supportFragmentManager, "filter dialog")
        }

        fab = findViewById(R.id.fab_add)
        fab.setOnClickListener {
            val intent = Intent(this, EditNoteActivity::class.java)
            someActivityResultLauncher.launch(intent)
        }
        setUpRecyclerAdapter()
    }

    private fun setUpRecyclerAdapter(){
        adapter = NoteListAdapter(viewModel.noteListFiltered, object : ItemClickListener() {

            override fun itemClick(model: NoteModel, position: Int) {
                if (viewModel.isActionMode) {
                    adapter.selectNote(position)
                    val count: Int = viewModel.selectedList.size
                    val countText: String = if (count == 1) {
                        "$count item selected"
                    } else {
                        "$count items selected"
                    }
                    selectedCountTv.text = countText
                    if (viewModel.selectedList.isEmpty()) {
                        setActionMode(false)
                    }
                } else {
                    val intent = Intent(this@NoteListActivity, EditNoteActivity::class.java)
                    intent.putExtra("id", model.id)
                    intent.putExtra("title", model.title)
                    intent.putExtra("date", model.getISOStringDate())
                    intent.putExtra("body", model.body)
                    someActivityResultLauncher.launch(intent)
                }
            }


        }, object : ItemLongClickListener() {
                override fun onItemLongClick(v: View, model: NoteModel, position: Int) {
                    if (!viewModel.isActionMode) {
                        setActionMode(true)
                        v.callOnClick()
                    }
                }


            })

        recyclerView = findViewById(R.id.noteListRV)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun getSearchTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                viewModel.filterList(charSequence.toString())
                adapter.refresh()
            }

            override fun afterTextChanged(editable: Editable) {}
        }
    }

    private fun setUpActionModeView() {
        if (viewModel.isActionMode) {
            searchBar.visibility = View.GONE
            actionBar.visibility = View.VISIBLE
            fab.visibility = View.GONE
            closeActionBarIbn.visibility = View.VISIBLE
        } else {
            searchBar.visibility = View.VISIBLE
            actionBar.visibility = View.GONE
            fab.visibility = View.VISIBLE
            closeActionBarIbn.visibility = View.GONE
        }
    }

    fun setActionMode(b: Boolean) {
        viewModel.setIsActionMode(b)
        setUpActionModeView()
        adapter.isActionMode = b
    }

    var someActivityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            reload()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun reload() {
        viewModel.refreshNotes()
        adapter.refresh()
        searchEt.setText("")
    }

    override fun deleteSelectedNotes() {
        if (viewModel.deleteSelectedNotes())
            Toast.makeText(this@NoteListActivity, "Successfully deleted!", Toast.LENGTH_SHORT)
                .show()
         else
            Toast.makeText(this@NoteListActivity, "An error occurred!", Toast.LENGTH_SHORT).show()
        reload()
        setActionMode(false)
    }
}