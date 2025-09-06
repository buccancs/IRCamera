package com.topdon.module.user.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.module.user.R
import com.topdon.module.user.databinding.DialogDownloadProBinding
import java.text.DecimalFormat

/**
 * 下载进度提示弹框.
 * Created by LCG on 2024/3/5.
 */
class DownloadProDialog(context: Context) : Dialog(context, R.style.InfoDialog) {

    private val binding: DialogDownloadProBinding = DialogDownloadProBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setContentView(binding.root)

        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = (ScreenUtil.getScreenWidth(context) * 0.72).toInt()
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }
    }

    /**
     * 刷新进度值
     */
    fun refreshProgress(current: Long, total: Long) {
        val progress = (current * 100f / total).toInt()
        binding.tvSize.text = "${context.getString(R.string.detail_len)}: ${getFileSizeStr(current)}/${getFileSizeStr(total)}"
        binding.progressBar.progress = progress
        binding.tvProgress.text = "${progress}%"
    }

    private fun getFileSizeStr(size: Long): String = if (size < 1024) {
        "${size}B"
    } else if (size < 1024 * 1024) {
        DecimalFormat("#.0").format(size.toDouble() / 1024) + "KB"
    } else if (size < 1024 * 1024 * 1024) {
        DecimalFormat("#.0").format(size.toDouble() / 1024 / 1024) + "MB"
    } else {
        DecimalFormat("#.0").format(size.toDouble() / 1024 / 1024 / 1024) + "GB"
    }
}