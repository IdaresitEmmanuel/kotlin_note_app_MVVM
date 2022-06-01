package com.example.kotlinnoteapp.view.editnotepage

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinnoteapp.R
import com.example.kotlinnoteapp.data.models.NoteModel
import com.example.kotlinnoteapp.util.ISOStringToDate
import java.util.*

class EditNoteActivity : AppCompatActivity() {
    private lateinit var viewModel: EditNoteViewModel
    private lateinit var saveImageBtn :ImageButton
    private lateinit var optionsBtn : ImageButton
    private lateinit var backBtn : ImageButton
    private lateinit var title : EditText
    private lateinit var body : AppCompatEditText
    private lateinit var date : TextView

    override fun onBackPressed() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)
        val intent = intent
        val id = intent.getIntExtra("id", -1)
        val title = intent.getStringExtra("title")
        val date = intent.getStringExtra("date")
        val body = intent.getStringExtra("body")
        val model =
            NoteModel(id, title ?: "",
                ISOStringToDate(date ?: Date().toString()),
                body ?: "")
        viewModel =  ViewModelProvider(this)[EditNoteViewModel::class.java]
        if (id != -1) {
            viewModel.setNote(model)
        }
        setUpUI()
    }

    private fun setUpUI(){

        saveImageBtn = findViewById(R.id.save_note)
        optionsBtn  = findViewById(R.id.note_options)
        backBtn = findViewById(R.id.back_ib)
        title = findViewById(R.id.title_et)
        body = findViewById(R.id.body_tet)
        date = findViewById(R.id.date_tv)

        title.setText(viewModel.title)
        date.text = viewModel.date
        body.setText(viewModel.noteBody)


        title.addTextChangedListener(getTitleTextWatcher())
        body.addTextChangedListener(getNoteTextWatcher())

        title.onFocusChangeListener = OnFocusChangeListener { _: View?, b: Boolean ->
            if (b) {
                optionsBtn.visibility = View.GONE
                saveImageBtn.visibility = View.VISIBLE
            }
        }
        body.onFocusChangeListener = OnFocusChangeListener { _: View?, b: Boolean ->
            if (b) {
                optionsBtn.visibility = View.GONE
                saveImageBtn.visibility = View.VISIBLE
            }
        }

        backBtn.setOnClickListener { this.onBackPressed() }

        optionsBtn.setOnClickListener { v: View? ->
            showPopUp(v!!)
        }

        saveImageBtn.setOnClickListener {
            val v = currentFocus
            if (v is EditText) {
                v.clearFocus()
                val imm =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
            }
            val result = viewModel.saveNote()
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
            if (viewModel.id > -1) {
                optionsBtn.visibility = View.VISIBLE
                saveImageBtn.visibility = View.GONE
            }
        }
    }

    private fun getTitleTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                viewModel.title = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        }
    }

    private fun getNoteTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                viewModel.noteBody = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        }
    }

    private fun showPopUp(v: View) {
        val popup = PopupMenu(this, v)
        popup.inflate(R.menu.edit_note_menu)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            if (menuItem.itemId == R.id.delete_note) {
                val result = viewModel.deleteFromDatabase()
                if (result) {
                    Toast.makeText(
                        this@EditNoteActivity,
                        "Successfully deleted!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    Toast.makeText(this@EditNoteActivity, "An error occurred!", Toast.LENGTH_SHORT)
                        .show()
                }
                onBackPressed()
            }
            true
        }
        popup.show()
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
}