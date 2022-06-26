package com.cars10.datareport

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.cars10.datareport.databinding.BarChartWidgetConfigureBinding
import vadiole.colorpicker.ColorModel
import vadiole.colorpicker.ColorPickerDialog

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
    private lateinit var fontSizeSeekBar: SeekBar
    private lateinit var choseBackgroundColorButton: Button
    private lateinit var choseTextColorButton: Button

    private var createWidget = View.OnClickListener {
        val context = this@BarChartWidgetConfigureActivity

        val dataPlan = dataPlanEditText.text.toString().toInt()
        val dataPlanUnit = dataPlanUnitSpinner.selectedItem.toString()
        val showReloadButton = showReloadButtonSwitch.isChecked()
        val showUpdatedAt = showUpdatedAtSwitch.isChecked()
        val fontSize = fontSizeSeekBar.getProgress()
        val backgroundColor = Color.parseColor("#" + choseBackgroundColorButton.text)
        val textColor = Color.parseColor("#" + choseTextColorButton.text)

        saveWidgetPrefs(
            context,
            appWidgetId,
            dataPlan,
            dataPlanUnit,
            showReloadButton,
            showUpdatedAt,
            fontSize,
            backgroundColor,
            textColor
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

        dataPlanEditText = binding.dataPlan
        dataPlanUnitSpinner = binding.spinner
        showReloadButtonSwitch = binding.showReloadButton
        showUpdatedAtSwitch = binding.showUpdatedAt
        fontSizeSeekBar = binding.fontSize
        choseBackgroundColorButton = binding.choseBackgroundColor
        choseTextColorButton = binding.choseTextColor

        dataPlanEditText.setText(widgetPrefs.dataPlan().toString())
        showReloadButtonSwitch.setChecked(widgetPrefs.showReload())
        showUpdatedAtSwitch.setChecked(widgetPrefs.showUpdatedAt())
        fontSizeSeekBar.setProgress(widgetPrefs.fontSize())
        choseBackgroundColorButton.setBackgroundColor(widgetPrefs.backgroundColor())
        choseBackgroundColorButton.setText(Integer.toHexString(widgetPrefs.backgroundColor()))
        choseTextColorButton.setBackgroundColor(widgetPrefs.textColor())
        choseTextColorButton.setText(Integer.toHexString(widgetPrefs.textColor()))

        choseBackgroundColorButton.setOnClickListener {
            val colorPicker: ColorPickerDialog = ColorPickerDialog.Builder()
                .setInitialColor(widgetPrefs.backgroundColor())
                .setColorModel(ColorModel.ARGB)
                .setColorModelSwitchEnabled(true)
                .setButtonOkText(android.R.string.ok)
                .setButtonCancelText(android.R.string.cancel)
                .onColorSelected { color: Int ->
                    choseBackgroundColorButton.setBackgroundColor(color)
                    choseBackgroundColorButton.setText(Integer.toHexString(color))
                }
                .create()
            colorPicker.show(supportFragmentManager, "color_picker")
        }

        choseTextColorButton.setOnClickListener {
            val colorPicker: ColorPickerDialog = ColorPickerDialog.Builder()
                .setInitialColor(widgetPrefs.textColor())
                .setColorModel(ColorModel.ARGB)
                .setColorModelSwitchEnabled(true)
                .setButtonOkText(android.R.string.ok)
                .setButtonCancelText(android.R.string.cancel)
                .onColorSelected { color: Int ->
                    choseTextColorButton.setBackgroundColor(color)
                    choseTextColorButton.setText(Integer.toHexString(color))
                }
                .create()
            colorPicker.show(supportFragmentManager, "color_picker")
        }

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
