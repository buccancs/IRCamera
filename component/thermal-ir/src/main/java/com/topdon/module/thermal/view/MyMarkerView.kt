package com.topdon.module.thermal.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.topdon.lib.core.db.entity.ThermalEntity
import com.topdon.lib.core.tools.NumberTools
import com.topdon.lib.core.tools.TimeTool
import com.topdon.module.thermal.ir.R

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ViewConstructor")
class MyMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById(R.id.tvContent)
    private val timeText: TextView = findViewById(R.id.time_text)

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @SuppressLint("DefaultLocale")
    override fun refreshContent(e: Entry, highlight: Highlight) {
        val index = highlight.dataIndex // 曲线序号
        val data = e.data as ThermalEntity
        
        if (e is CandleEntry) {
            tvContent.text = Utils.formatNumber(e.high, 0, true)
        } else {
            val str = StringBuilder()
            val thermalStr = NumberTools.to02(data.thermal)
            val thermalMaxStr = NumberTools.to02(data.thermalMax)
            val thermalMinStr = NumberTools.to02(data.thermalMin)
            
            when (index) {
                0 -> str.append("温度:").append(thermalStr)
                1 -> {
                    str.append("最高温度:").append(thermalMaxStr)
                    str.append(System.getProperty("line.separator")).append("最低温度:").append(thermalMinStr)
                }
                else -> {
                    str.append("最高温度:").append(thermalMaxStr)
                    str.append(System.getProperty("line.separator")).append("最低温度:").append(thermalMinStr)
                }
            }
            tvContent.text = str.toString()
            timeText.text = TimeTool.showTimeSecond(data.createTime)
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}