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
    private lateinit var dataPlanEditText: EditText
    private lateinit var dataPlanUnitSpinner: Spinner
    private lateinit var showReloadButtonSwitch: Switch
    private lateinit var showUpdatedAtSwitch: Switch

    private var createWidget = View.OnClickListener {
        val context = this@BarChartWidgetConfigureActivity

        val dataPlan = dataPlanEditText.text.toString().toInt()
        val dataPlanUnit = dataPlanUnitSpinner.selectedItem.toString()
        val showReloadButton = showReloadButtonSwitch.isChecked()
        val showUpdatedAt = showUpdatedAtSwitch.isChecked()
        saveWidgetPrefs(
            context,
            appWidgetId,
            dataPlan,
            dataPlanUnit,
            showReloadButton,
            showUpdatedAt
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

        dataPlanEditText = binding.dataPlan
        dataPlanUnitSpinner = binding.spinner
        showReloadButtonSwitch = binding.showReloadButton
        showUpdatedAtSwitch = binding.showUpdatedAt
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
        dataPlanEditText.setText(widgetPrefs.dataPlan().toString())
        showReloadButtonSwitch.setChecked(widgetPrefs.showReload())
        showUpdatedAtSwitch.setChecked(widgetPrefs.showUpdatedAt())


        // DO THE SPINNING
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.data_plan_sizes,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dataPlanUnitSpinner.adapter = adapter

        val position = adapter.getPosition(widgetPrefs.dataPlanUnit())
        dataPlanUnitSpinner.setSelection(position)
    }
}
