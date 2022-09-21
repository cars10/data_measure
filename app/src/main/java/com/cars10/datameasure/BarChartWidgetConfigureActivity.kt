package com.cars10.datameasure

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cars10.datameasure.databinding.BarChartWidgetConfigureBinding
import com.cars10.datameasure.widgets.updateAppWidget

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
            setTitle(R.string.app_name)
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

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
    }

    private fun renderWidget() {
        val context = this@BarChartWidgetConfigureActivity
        val appWidgetManager = AppWidgetManager.getInstance(context)
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    /** When override this method to fix issues with multiple widgets:
     *
     * - open configuration for widget 1
     * - press home
     * - open configuration for widget 2 (this opens the correct config for widget 2)
     * - press home
     * - open configuration for widget 1 again
     * => this opens the configuration for widget 2, despite us setting a different requestCode for the intent
     *
     * So we just call finish() in onUserLeaveHint to stop the activity when ever the user presses home.
     *
     **/
    public override fun onUserLeaveHint() {
        renderWidget()
        finish()
    }
}
