package com.example.kotlinnoteapp.view.notelistpage.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.kotlinnoteapp.R
import com.example.kotlinnoteapp.constants.NoteFilter
import com.example.kotlinnoteapp.data.repositories.AppPreferences

class FilterDialog : AppCompatDialogFragment() {
    private var none: RadioButton? = null
    private var alpha: RadioButton? = null
    private var bDate: RadioButton? = null
    private var listener: FilterDialogListener? =
        null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val view: View = layoutInflater.inflate(R.layout.fragment_filter_dialog, null)
        none = view.findViewById(R.id.filter_none)
        alpha = view.findViewById(R.id.filter_alphabetical)
        bDate = view.findViewById(R.id.filter_by_date)
        none?.setOnClickListener { _: View? ->
            checkFilter(
                NoteFilter.none
            )
        }
        alpha?.setOnClickListener { _: View? ->
            checkFilter(
                NoteFilter.alphabetical
            )
        }
        bDate?.setOnClickListener { _: View? ->
            checkFilter(
                NoteFilter.byDate
            )
        }
        setChecked()
        builder.setView(view)
        return builder.create()
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        super.setupDialog(dialog, style)
    }

    private fun setChecked() {
        when (AppPreferences.instance?.filter ?: NoteFilter.none) {
            NoteFilter.none -> none!!.isChecked = true
            NoteFilter.alphabetical -> alpha!!.isChecked = true
            NoteFilter.byDate -> bDate!!.isChecked = true
        }
    }

    private fun checkFilter(filter: NoteFilter) {
        AppPreferences.instance?.filter = filter
        listener?.reload()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as FilterDialogListener
        } catch (e: Exception) {
            Log.d("FilterDialogFragment:", e.toString())
            throw ClassCastException(
                context
                    .toString() + " must implement FilterDialogListener"
            )
        }
    }

    interface FilterDialogListener {
        fun reload()
    }
}