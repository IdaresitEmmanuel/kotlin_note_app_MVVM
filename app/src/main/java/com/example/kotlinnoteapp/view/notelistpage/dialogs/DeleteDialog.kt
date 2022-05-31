package com.example.kotlinnoteapp.view.notelistpage.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import com.example.kotlinnoteapp.MyApplication
import com.example.kotlinnoteapp.R

class DeleteDialog(var msg: String) : AppCompatDialogFragment() {
    var listener: DeleteDialogListener? =
        null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(msg)
            .setPositiveButton(
                "yes"
            ) { dialogInterface: DialogInterface, i: Int ->
                listener?.deleteSelectedNotes()
                dialogInterface.dismiss()
            }
            .setNegativeButton(
                "no"
            ) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
        return builder.create()
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        dialog.window!!
            .setBackgroundDrawable(
                ContextCompat.getDrawable(
                    MyApplication.appContext!!,
                    R.drawable.bg_dialog
                )
            )
        super.setupDialog(dialog, style)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as DeleteDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$e \n must implement DeleteDialogListener")
        }
    }

    interface DeleteDialogListener {
        fun deleteSelectedNotes()
    }
}