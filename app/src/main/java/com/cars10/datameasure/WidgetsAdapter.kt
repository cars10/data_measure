package com.cars10.datameasure

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView

class WidgetsAdapter(private val context: Context, private val widgetIds: IntArray) :
    RecyclerView.Adapter<WidgetsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val widgetPreview: FragmentContainerView = itemView.findViewById(R.id.widget_preview)
        val card: CardView = itemView.findViewById(R.id.widget_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val widgetView = inflater.inflate(R.layout.widget_row, parent, false)
        return ViewHolder(widgetView)
    }

    override fun onBindViewHolder(viewHolder: WidgetsAdapter.ViewHolder, position: Int) {
        val widgetId = widgetIds[position]

        val newContainerId = View.generateViewId()
        viewHolder.widgetPreview.id = newContainerId

        val widgetPreview = BarChartWidgetFragment().apply {
            appWidgetId = widgetId
        }
        val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
        manager.beginTransaction().replace(newContainerId, widgetPreview).commit()

        viewHolder.card.setOnClickListener { view ->
            val intent = Intent(context, BarChartWidgetConfigureActivity::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)

            view.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return widgetIds.size
    }
}
