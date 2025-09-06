package com.topdon.module.thermal.ir.report.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import com.topdon.lib.core.tools.UnitTools
import com.topdon.module.thermal.R
import com.topdon.module.thermal.ir.report.bean.ImageTempBean
import com.topdon.module.thermal.databinding.ViewReportIrInputBinding

class ReportIRInputView: LinearLayout {

    private val binding: ViewReportIrInputBinding

    companion object {
        private const val TYPE_FULL = 0 //全图
        private const val TYPE_POINT = 1//点
        private const val TYPE_LINE = 2 //线
        private const val TYPE_RECT = 3 //面
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("SetTextI18n")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        binding = ViewReportIrInputBinding.inflate(LayoutInflater.from(context), this, true)

        binding.clExplain.etItem.inputType = InputType.TYPE_CLASS_TEXT
        binding.clExplain.etItem.filters = arrayOf(LengthFilter(150))

        setSwitchListener(binding.clMax.switchItem, binding.clMax.etItem)
        setSwitchListener(binding.clMin.switchItem, binding.clMin.etItem)
        setSwitchListener(binding.clAverage.switchItem, binding.clAverage.etItem)
        setSwitchListener(binding.clExplain.switchItem, binding.clExplain.etItem)

        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.ReportIRInputView)
        val type = typeArray.getInt(R.styleable.ReportIRInputView_type, TYPE_FULL)
        val index = typeArray.getInt(R.styleable.ReportIRInputView_index, 0)
        typeArray.recycle()

        binding.clTitle.isVisible = index == 0
        binding.viewLine.isVisible = index > 0

        when (type) {
            TYPE_FULL -> {
                binding.tvTitle.setText(R.string.thermal_full_rect)
                binding.clMin.root.isVisible = true
                binding.clAverage.root.isVisible = false
                binding.clMax.tvItemName.text = context.getString(R.string.chart_temperature_high) + " (${UnitTools.showUnit()})"
                binding.clMin.tvItemName.text = context.getString(R.string.chart_temperature_low) + " (${UnitTools.showUnit()})"
                binding.clExplain.tvItemName.text = context.getString(R.string.album_report_comment)
            }
            TYPE_POINT -> {
                binding.tvTitle.text = context.getString(R.string.thermal_point) + "(P)"
                binding.clMin.root.isVisible = false
                binding.clAverage.root.isVisible = false
                binding.clMax.tvItemName.text = "P${index + 1} " + context.getString(R.string.chart_temperature) + " (${UnitTools.showUnit()})"
                binding.clExplain.tvItemName.text = "P${index + 1} " + context.getString(R.string.album_report_comment)
            }
            TYPE_LINE -> {
                binding.tvTitle.text = context.getString(R.string.thermal_line) + "(L)"
                binding.clMin.root.isVisible = true
                binding.clAverage.root.isVisible = true
                binding.clMax.tvItemName.text = "L${index + 1} " + context.getString(R.string.chart_temperature_high) + " (${UnitTools.showUnit()})"
                binding.clMin.tvItemName.text = "L${index + 1} " + context.getString(R.string.chart_temperature_low) + " (${UnitTools.showUnit()})"
                binding.clAverage.tvItemName.text = "L${index + 1} " + context.getString(R.string.album_report_mean_temperature) + " (${UnitTools.showUnit()})"
                binding.clExplain.tvItemName.text = "L${index + 1} " + context.getString(R.string.album_report_comment)
            }
            TYPE_RECT -> {
                binding.tvTitle.text = context.getString(R.string.thermal_rect) + "(R)"
                binding.clMin.root.isVisible = true
                binding.clAverage.root.isVisible = true
                binding.clMax.tvItemName.text = "R${index + 1} " + context.getString(R.string.chart_temperature_high) + " (${UnitTools.showUnit()})"
                binding.clMin.tvItemName.text = "R${index + 1} " + context.getString(R.string.chart_temperature_low) + " (${UnitTools.showUnit()})"
                binding.clAverage.tvItemName.text = "R${index + 1} " + context.getString(R.string.album_report_mean_temperature) + " (${UnitTools.showUnit()})"
                binding.clExplain.tvItemName.text = "R${index + 1} " + context.getString(R.string.album_report_comment)
            }
        }
    }

    fun isSwitchMaxCheck() = binding.clMax.switchItem.isChecked
    fun isSwitchMinCheck() = binding.clMin.switchItem.isChecked
    fun isSwitchAverageCheck() = binding.clAverage.switchItem.isChecked
    fun isSwitchExplainCheck() = binding.clExplain.switchItem.isChecked

    fun getMaxInput() = binding.clMax.etItem.text.toString()
    fun getMinInput() = binding.clMin.etItem.text.toString()
    fun getAverageInput() = binding.clAverage.etItem.text.toString()
    fun getExplainInput() = binding.clExplain.etItem.text.toString()

    fun refreshData(tempBean: ImageTempBean.TempBean?) {
        tempBean?.max?.let {
            binding.clMax.etItem.setText(UnitTools.showUnitValue(it.toFloat())?.toString())
        }
        tempBean?.min?.let {
            binding.clMin.etItem.setText(UnitTools.showUnitValue(it.toFloat())?.toString())
        }
        tempBean?.average?.let {
            binding.clAverage.etItem.setText(UnitTools.showUnitValue(it.toFloat())?.toString())
        }
        binding.clExplain.etItem.setText("")
    }

    private fun setSwitchListener(switchCompat: SwitchCompat, editText: EditText) {
        switchCompat.setOnCheckedChangeListener { _, isChecked ->
            editText.isVisible = isChecked
        }
    }
}