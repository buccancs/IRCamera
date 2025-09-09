package com.topdon.tc001.gsr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.csl.irCamera.R

/**
 * Adapter for displaying GSR data rows in detailed view
 */
class GSRDataRowAdapter(
    private val dataRows: List<GSRDataViewActivity.GSRDataRow>,
) : RecyclerView.Adapter<GSRDataRowAdapter.ViewHolder>() {

    companion object {
        // Constants for GSR data conversion
        private const val RESISTANCE_CONVERSION_FACTOR = 1000 // Ohms to kOhms
        private const val MAX_DISPLAY_ROWS = 100 // Performance limit
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rowNumber: TextView = view.findViewById(R.id.row_number)
        val timestamp: TextView = view.findViewById(R.id.timestamp)
        val gsrValue: TextView = view.findViewById(R.id.gsr_value)
        val resistance: TextView = view.findViewById(R.id.resistance)
        val conductance: TextView = view.findViewById(R.id.conductance)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_gsr_data_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val dataRow = dataRows[position]

        holder.rowNumber.text = dataRow.rowNumber.toString()
        holder.timestamp.text = dataRow.timestamp
        holder.gsrValue.text = "%.3f μS".format(dataRow.gsrValue)
        holder.resistance.text = "%.1f kΩ".format(dataRow.resistance / RESISTANCE_CONVERSION_FACTOR)
        holder.conductance.text = "%.6f S".format(dataRow.conductance)
    }

    override fun getItemCount() = minOf(dataRows.size, MAX_DISPLAY_ROWS) // Limit for performance
}
