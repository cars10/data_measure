package com.cars10.datameasure

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class WidgetDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_details)

        window.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.transparent)))

        AlertDialog.Builder(this@WidgetDetails).setTitle("Dialog title")
            .setMessage("Dialog message")
            .setPositiveButton("yes", DialogInterface.OnClickListener { dialog, which ->
                finish()
            }).setNegativeButton("no", DialogInterface.OnClickListener { dialog, which ->
                finish()
            }).show()
    }
}