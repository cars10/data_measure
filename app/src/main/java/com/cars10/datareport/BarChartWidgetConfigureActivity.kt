package com.cars10.datareport

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.cars10.datareport.databinding.BarChartWidgetConfigureBinding

/**
 * The configuration screen for the [BarChartWidget] AppWidget.
 */
@SuppressLint("UseSwitchCompatOrMaterialCode")
class BarChartWidgetConfigureActivity : AppCompatActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var dataPlanText: EditText
    private lateinit var dataPlanUnit: Spinner
    private lateinit var showReloadButton: Switch
    private lateinit var showUpdatedAt: Switch

    private var createWidget = View.OnClickListener {
        val context = this@BarChartWidgetConfigureActivity

        val dataPlan = dataPlanText.text.toString().toInt()
        val dataPlanUnit = dataPlanUnit.selectedItem.toString()
        val showReloadButtonSetting = showReloadButton.isChecked()
        val showUpdatedAtSetting = showUpdatedAt.isChecked()
        saveWidgetPrefs(
            context,
            appWidgetId,
            dataPlan,
            dataPlanUnit,
            showReloadButtonSetting,
            showUpdatedAtSetting
        )

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

        dataPlanText = binding.dataPlan
        dataPlanUnit = binding.spinner
        showReloadButton = binding.showReloadButton
        showUpdatedAt = binding.showUpdatedAt
        binding.saveButton.setOnClickListener(createWidget)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val widgetPrefs = DataUsageWidgetPrefs(this@BarChartWidgetConfigureActivity, appWidgetId)
        dataPlanText.setText(widgetPrefs.dataPlan().toString())
        showReloadButton.setChecked(widgetPrefs.showReload())
        showUpdatedAt.setChecked(widgetPrefs.showUpdatedAt())


        // DO THE SPINNING
        val spinner: Spinner = findViewById(R.id.spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.data_plan_sizes,
            android.R.layout.simple_spinner_item
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner.adapter = adapter

        val position = adapter.getPosition(widgetPrefs.dataPlanUnit())
        spinner.setSelection(position)
    }
}
