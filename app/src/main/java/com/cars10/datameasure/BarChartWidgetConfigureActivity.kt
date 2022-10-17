package com.cars10.datameasure

import android.Manifest
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cars10.datameasure.databinding.BarChartWidgetConfigureBinding
import com.cars10.datameasure.widgets.updateAppWidget

private var STORAGE_PERMISSION_REQUEST = 1337

class BarChartWidgetConfigureActivity : AppCompatActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var binding: BarChartWidgetConfigureBinding

    private fun saveWidget() {
        renderWidget()

        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        binding = BarChartWidgetConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.apply {
            inflateMenu(R.menu.bar_chart_widget_configure_menu)
            setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.save) saveWidget()
                true
            }
        }

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        val widgetSettings = WidgetSettingsFragment()
        widgetSettings.appWidgetId = appWidgetId
        supportFragmentManager.beginTransaction().replace(R.id.settings_container, widgetSettings)
            .commit()

        val widgetPreview = BarChartWidgetFragment()
        widgetPreview.appWidgetId = appWidgetId
        supportFragmentManager.beginTransaction().replace(R.id.widget_preview, widgetPreview)
            .commit()

        val storagePermission =
            checkReadExternalStoragePermission() == PackageManager.PERMISSION_GRANTED

        if (storagePermission) {
            setWidgetPreviewBackgroundImage()
            setAllowWallpaperPreviewVisibility(false)
        } else {
            setAllowWallpaperPreviewVisibility(true)
            val allowWallpaperPreview = findViewById<LinearLayout>(R.id.allow_wallpaper_preview)
            allowWallpaperPreview.setOnClickListener { _ ->
                trySetWidgetPreviewBackground()
            }
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
    }

    private fun trySetWidgetPreviewBackground() {
        if (checkReadExternalStoragePermission() == PackageManager.PERMISSION_GRANTED) {
            val wallpaperManager = WallpaperManager.getInstance(this)
            val widgetPreviewBackground = findViewById<ImageView>(R.id.widget_preview_background)
            widgetPreviewBackground.clipToOutline = true
            widgetPreviewBackground.setImageDrawable(wallpaperManager.drawable)
        } else {
            requestReadExternalStoragePermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResult: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult)

        if (requestCode == STORAGE_PERMISSION_REQUEST) {
            val granted = grantResult[0] == PackageManager.PERMISSION_GRANTED
            if (granted) {
                setWidgetPreviewBackgroundImage()
                setAllowWallpaperPreviewVisibility(false)
            }
        }
    }

    private fun setAllowWallpaperPreviewVisibility(visible: Boolean) {
        val allowWallpaperPreview = findViewById<LinearLayout>(R.id.allow_wallpaper_preview)
        if (visible) {
            allowWallpaperPreview.visibility = View.VISIBLE
        } else {
            allowWallpaperPreview.visibility = View.INVISIBLE
        }
    }

    @SuppressLint("MissingPermission")
    private fun setWidgetPreviewBackgroundImage() {
        val wallpaperManager = WallpaperManager.getInstance(this)
        val widgetPreviewBackground = findViewById<ImageView>(R.id.widget_preview_background)
        widgetPreviewBackground.clipToOutline = true
        widgetPreviewBackground.setImageDrawable(wallpaperManager.drawable)
    }


    private fun checkReadExternalStoragePermission(): Int {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private fun requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST
        )
    }

    private fun renderWidget() {
        val context = this@BarChartWidgetConfigureActivity
        val appWidgetManager = AppWidgetManager.getInstance(context)
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }
}
