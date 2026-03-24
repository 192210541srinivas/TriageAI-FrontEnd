package com.simats.triageai.utils

import android.app.Activity
import androidx.appcompat.app.AlertDialog

object NavigationUtils {

    fun showExitConfirmationDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle("Close App")
            .setMessage("Do you want to close app?")
            .setPositiveButton("Yes") { _, _ ->
                activity.finishAffinity()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }
}
