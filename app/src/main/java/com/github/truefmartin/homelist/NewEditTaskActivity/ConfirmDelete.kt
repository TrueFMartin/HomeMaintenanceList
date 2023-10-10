package com.github.truefmartin.homelist.NewEditTaskActivity

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ConfirmDelete(private val onDeleteFunction: (Unit) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction.
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Confirm Task Deletion")
                .setPositiveButton("Delete") { _, _ ->
                    Log.d("ConfirmDelete", "User confirmed deleting of task")
                    onDeleteFunction
                }
                .setNegativeButton("Cancel") { _, _->
                    // User cancelled the dialog.
                    Log.d("ConfirmDelete", "User canceled deleting of task")
                }
            // Create the AlertDialog object and return it.
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}