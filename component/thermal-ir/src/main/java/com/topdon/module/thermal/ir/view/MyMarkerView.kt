package com.topdon.module.thermal.ir.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import com.elvishew.xlog.XLog
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.topdon.lib.core.db.entity.ThermalEntity
import com.topdon.lib.core.tools.TimeTool
import com.topdon.lib.core.tools.UnitTools
import com.topdon.module.thermal.ir.R
import java.util.*

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
    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun refreshContent(e: Entry, highlight: Highlight) {
        try {
            if (e is CandleEntry) {
                tvContent.text = Utils.formatNumber(e.high, 0, true)
            } else {
                if (e.data is ThermalEntity) {
                    val data = e.data as ThermalEntity
                    val index = highlight.dataIndex // 曲线序号
                    val str = StringBuilder()
                    
                    when (index) {
                        0 -> {
                            str.append(com.blankj.utilcode.util.Utils.getApp().getString(R.string.chart_temperature) + ": ")
                                .append(UnitTools.showC(data.thermal))
                        }
                        1 -> {
                            str.append(com.blankj.utilcode.util.Utils.getApp().getString(R.string.chart_temperature_high) + ": ")
                                .append(UnitTools.showC(data.thermalMax))
                            str.append(System.getProperty("line.separator"))
                                .append(com.blankj.utilcode.util.Utils.getApp().getString(R.string.chart_temperature_low) + ": ")
                                .append(UnitTools.showC(data.thermalMin))
                        }
                        else -> {
                            str.append(com.blankj.utilcode.util.Utils.getApp().getString(R.string.chart_temperature_high) + ": ")
                                .append(UnitTools.showC(data.thermalMax))
                            str.append(System.getProperty("line.separator"))
                                .append(com.blankj.utilcode.util.Utils.getApp().getString(R.string.chart_temperature_low) + ": ")
                                .append(UnitTools.showC(data.thermalMin))
                        }
                    }
                    tvContent.text = str.toString()
                    timeText.text = TimeTool.showTimeSecond(data.createTime)
                } else {
                    val tempText = com.blankj.utilcode.util.Utils.getApp().getString(R.string.chart_temperature) + 
                                  ": " + String.format(Locale.ENGLISH, "%.1f", e.y) + UnitTools.showUnit()
                    tvContent.text = tempText
                    timeText.visibility = View.GONE
                }
            }
        } catch (ex: Exception) {
            XLog.e("MarkerView error: ${ex.message}")
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}