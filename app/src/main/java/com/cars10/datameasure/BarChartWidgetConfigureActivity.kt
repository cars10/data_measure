package com.cars10.datameasure

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cars10.datameasure.databinding.BarChartWidgetConfigureBinding

/**
 * The configuration screen for the [BarChartWidget] AppWidget.
 */
class BarChartWidgetConfigureActivity : AppCompatActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    private var createWidget = View.OnClickListener {
        val context = this@BarChartWidgetConfigureActivity

        val appWidgetManager = AppWidgetManager.getInstance(context)
        updateAppWidget(context, appWidgetManager, appWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }
    private lateinit var binding: BarChartWidgetConfigureBinding

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        binding = BarChartWidgetConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveButton.setOnClickListener(createWidget)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainerView, WidgetSettingsFragment(appWidgetId))
            .commit()

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
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
        finish()
    }
}
