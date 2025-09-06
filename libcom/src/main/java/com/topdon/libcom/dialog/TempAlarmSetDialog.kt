package com.topdon.libcom.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.topdon.lib.core.tools.UnitTools
import com.topdon.libcom.AlarmHelp
import com.topdon.libcom.R
import com.topdon.lib.core.bean.AlarmBean
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.tools.ToastTools
import com.topdon.libcom.databinding.DialogTempAlarmSetBinding

class TempAlarmSetDialog(
    context: Context,
    private val isEdit: Boolean,
) : Dialog(context, R.style.app_compat_dialog), CompoundButton.OnCheckedChangeListener {

    var alarmBean = AlarmBean()
        set(value) {
            field = value.copy()
        }

    /**
     * 保存点击事件监听.
     */
    var onSaveListener: ((alarmBean: AlarmBean) -> Unit)? = null

    /**
     * 用于播放报警铃声.
     */
    private var mediaPlayer: MediaPlayer? = null

    public var hideAlarmMark = false

    private lateinit var binding: DialogTempAlarmSetBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        binding = DialogTempAlarmSetBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        initView()

        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            it.attributes = layoutParams
        }
    }

    override fun onBackPressed() {
        dismiss()
    }

    private fun initView() {
        binding.clRoot.setOnClickListener { dismiss() }
        binding.clClose.setOnClickListener { dismiss() }
        binding.tvSave.setOnClickListener { save() }
        binding.ivRingtone1.setOnClickListener { selectRingtone(0) }
        binding.ivRingtone2.setOnClickListener { selectRingtone(1) }
        binding.ivRingtone3.setOnClickListener { selectRingtone(2) }
        binding.ivRingtone4.setOnClickListener { selectRingtone(3) }
        binding.ivRingtone5.setOnClickListener { selectRingtone(4) }
        binding.switchAlarmHigh.setOnCheckedChangeListener(this)
        binding.switchAlarmLow.setOnCheckedChangeListener(this)
        binding.switchAlarmMark.setOnCheckedChangeListener(this)
        binding.switchAlarmRingtone.setOnCheckedChangeListener(this)

        binding.imgMarkHigh.setOnClickListener {
            showColorDialog(true)
        }
        binding.imgMarkLow.setOnClickListener {
            showColorDialog(false)
        }
        binding.ivCheckStoke.setOnClickListener {
            if (!binding.ivCheckStoke.isSelected) {
                binding.ivCheckStoke.isSelected = true
                binding.ivCheckMatrix.isSelected = false
                alarmBean.markType = AlarmBean.TYPE_ALARM_MARK_STROKE
            }
        }
        binding.ivCheckMatrix.setOnClickListener {
            if (!binding.ivCheckMatrix.isSelected) {
                binding.ivCheckStoke.isSelected = false
                binding.ivCheckMatrix.isSelected = true
                alarmBean.markType = AlarmBean.TYPE_ALARM_MARK_MATRIX
            }
        }

        binding.tvAlarmHighUnit.text = UnitTools.showUnit()
        binding.tvAlarmLowUnit.text = UnitTools.showUnit()
    }

    override fun show() {
        super.show()
        refreshAlarmView()
    }

    private fun refreshAlarmView() {
        binding.switchAlarmHigh.isChecked = alarmBean.isHighOpen
        binding.switchAlarmLow.isChecked = alarmBean.isLowOpen
        binding.switchAlarmMark.isChecked = isEdit || alarmBean.isMarkOpen
        if (!isEdit) {
            binding.switchAlarmRingtone.isChecked = alarmBean.isRingtoneOpen
        }
        binding.ivCheckStoke.isSelected = alarmBean.markType == AlarmBean.TYPE_ALARM_MARK_STROKE
        binding.ivCheckMatrix.isSelected = alarmBean.markType == AlarmBean.TYPE_ALARM_MARK_MATRIX
        Glide.with(context).load(ColorDrawable(alarmBean.highColor)).into(binding.imgCAlarmHigh)
        Glide.with(context).load(ColorDrawable(alarmBean.lowColor)).into(binding.imgCAlarmLow)

        binding.etAlarmHigh.isEnabled = binding.switchAlarmHigh.isChecked
        binding.etAlarmLow.isEnabled = binding.switchAlarmLow.isChecked
        binding.clAlarmMark.isVisible = isEdit || binding.switchAlarmMark.isChecked
        binding.clRingtoneSelect.isVisible = !isEdit && binding.switchAlarmRingtone.isChecked
        binding.tvAlarmRingtone.isVisible = !isEdit
        binding.switchAlarmRingtone.isVisible = !isEdit
        if (hideAlarmMark){
            binding.tvAlarmMark.visibility = View.GONE
            binding.switchAlarmMark.visibility = View.GONE
            binding.clAlarmMark.visibility = View.GONE
        }
        binding.switchAlarmMark.isVisible = !isEdit
        if (alarmBean.highTemp == Float.MAX_VALUE) {
            binding.etAlarmHigh.setText("")
        } else {
            binding.etAlarmHigh.setText(UnitTools.showUnitValue(alarmBean.highTemp).toString())
        }
        if (alarmBean.lowTemp == Float.MIN_VALUE) {
            binding.etAlarmLow.setText("")
        } else {
            binding.etAlarmLow.setText(UnitTools.showUnitValue(alarmBean.lowTemp).toString())
        }
        binding.ivRingtone1.isSelected = false
        binding.ivRingtone2.isSelected = false
        binding.ivRingtone3.isSelected = false
        binding.ivRingtone4.isSelected = false
        binding.ivRingtone5.isSelected = false
        when (alarmBean.ringtoneType) {
            0 -> binding.ivRingtone1.isSelected = true
            1 -> binding.ivRingtone2.isSelected = true
            2 -> binding.ivRingtone3.isSelected = true
            3 -> binding.ivRingtone4.isSelected = true
            4 -> binding.ivRingtone5.isSelected = true
        }
    }

    private fun save() {
        try {
            val inputHigh = if (binding.switchAlarmHigh.isChecked) {
                if (binding.etAlarmHigh.text.isNotEmpty()) UnitTools.showToCValue(binding.etAlarmHigh.text.toString().toFloat()) else null
            } else {
                null
            }
            val inputLow = if (binding.switchAlarmLow.isChecked) {
                if (binding.etAlarmLow.text.isNotEmpty()) UnitTools.showToCValue(binding.etAlarmLow.text.toString().toFloat()) else null
            } else {
                null
            }
            if (inputHigh != null && inputLow != null && inputLow > inputHigh) {
                ToastTools.showShort(R.string.tip_input_format)
                return
            }
        } catch (e: Exception) {
            ToastTools.showShort(R.string.tip_input_format)
            return
        }

        val inputHigh = if (binding.etAlarmHigh.text.isNotEmpty()) binding.etAlarmHigh.text.toString() else ""
        val inputLow = if (binding.etAlarmLow.text.isNotEmpty()) binding.etAlarmLow.text.toString() else ""
        var highValue: Float? = null
        var lowValue: Float? = null
        try {
            highValue = if (inputHigh.isNotEmpty()) UnitTools.showToCValue(inputHigh.toFloat()) else null
            lowValue = if (inputLow.isNotEmpty()) UnitTools.showToCValue(inputLow.toFloat()) else null
        } catch (_: Exception) {

        }
        alarmBean.highTemp = highValue ?: Float.MAX_VALUE
        alarmBean.lowTemp = lowValue ?: Float.MIN_VALUE
        alarmBean.isHighOpen = binding.switchAlarmHigh.isChecked
        alarmBean.isLowOpen = binding.switchAlarmLow.isChecked
        alarmBean.isRingtoneOpen = binding.switchAlarmRingtone.isChecked

        onSaveListener?.invoke(alarmBean)

        dismiss()
    }

    private fun showColorDialog(isHigh: Boolean) {
        val colorPickDialog = ColorPickDialog(context, if (isHigh) alarmBean.highColor else alarmBean.lowColor,-1)
        colorPickDialog.onPickListener = { it: Int, i1: Int ->
            if (isHigh) {
                alarmBean.highColor = it
                Glide.with(context).load(ColorDrawable(it)).into(binding.imgCAlarmHigh)
            } else {
                alarmBean.lowColor = it
                Glide.with(context).load(ColorDrawable(it)).into(binding.imgCAlarmLow)
            }
        }
        colorPickDialog.show()
    }

    override fun dismiss() {
        super.dismiss()
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (_: Exception) {

        }

    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.switch_alarm_high -> {//高温报警
                binding.etAlarmHigh.isEnabled = isChecked
                alarmBean.isHighOpen = isChecked
            }

            R.id.switch_alarm_low -> {//低温报警
                binding.etAlarmLow.isEnabled = isChecked
                alarmBean.isLowOpen = isChecked
            }

            R.id.switch_alarm_mark -> {//区域标记
                binding.clAlarmMark.isVisible = isChecked
                alarmBean.isMarkOpen = isChecked
            }

            R.id.switch_alarm_ringtone -> {//报警铃声
                binding.clRingtoneSelect.isVisible = isChecked
                if (isChecked) {
                    selectRingtone(alarmBean.ringtoneType)
                } else {
                    selectRingtone(null)
                }
            }
        }
    }

    /**
     * 设置当前选中的铃声，null 表示关闭.
     */
    private fun selectRingtone(position: Int?) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
            }
        } catch (_: Exception) {
        }
        if (position == null) {
            return
        }
        alarmBean.ringtoneType = position

        binding.ivRingtone1.isSelected = false
        binding.ivRingtone2.isSelected = false
        binding.ivRingtone3.isSelected = false
        binding.ivRingtone4.isSelected = false
        binding.ivRingtone5.isSelected = false
        when (position) {
            0 -> binding.ivRingtone1.isSelected = true
            1 -> binding.ivRingtone2.isSelected = true
            2 -> binding.ivRingtone3.isSelected = true
            3 -> binding.ivRingtone4.isSelected = true
            4 -> binding.ivRingtone5.isSelected = true
        }
        when (position) {
            0 -> mediaPlayer = MediaPlayer.create(context, R.raw.ringtone1)
            1 -> mediaPlayer = MediaPlayer.create(context, R.raw.ringtone2)
            2 -> mediaPlayer = MediaPlayer.create(context, R.raw.ringtone3)
            3 -> mediaPlayer = MediaPlayer.create(context, R.raw.ringtone4)
            4 -> mediaPlayer = MediaPlayer.create(context, R.raw.ringtone5)
        }
        mediaPlayer?.start()
    }
}