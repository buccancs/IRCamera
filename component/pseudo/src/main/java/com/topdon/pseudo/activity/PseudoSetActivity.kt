package com.topdon.pseudo.activity

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.topdon.lib.core.common.ProductType
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.UnitTools
import com.topdon.pseudo.R
import com.topdon.pseudo.bean.CustomPseudoBean
import com.topdon.pseudo.constant.*
import com.topdon.pseudo.databinding.ActivityPseudoSetBinding
import java.lang.NumberFormatException
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 颜色模式（自定义渲染）设置界面.
 *
 * 需要传递
 * - [ExtraKeyConfig.IS_TC007] - 是否设置 TC007 的自定义渲染
 * - [ExtraKeyConfig.CUSTOM_PSEUDO_BEAN] - 自定义渲染相关设置项.（可选，不传则从 SharedPreferences 中读取配置.）
 *
 * 返回 result
 * - [ExtraKeyConfig.CUSTOM_PSEUDO_BEAN] - 自定义渲染相关设置项.
 */
class PseudoSetActivity : BaseActivity(), View.OnClickListener {
    /**
     * 从上一界面传递过来的，自定义渲染相关设置项.
     */
    private lateinit var customPseudoBean: CustomPseudoBean
    private lateinit var binding: ActivityPseudoSetBinding


    override fun initContentView(): Int {
        binding = ActivityPseudoSetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        return R.layout.activity_pseudo_set
    }

    override fun initView() {
        val isTC007 = intent.getBooleanExtra(ExtraKeyConfig.IS_TC007, false)
        customPseudoBean = intent.getParcelableExtra(ExtraKeyConfig.CUSTOM_PSEUDO_BEAN) ?: CustomPseudoBean.loadFromShared(isTC007)
        switchDynamicCustom(customPseudoBean.isUseCustomPseudo)

        //加载温度配置
        binding.etMaxTemp.setText(UnitTools.showNoUnit(customPseudoBean.maxTemp))
        binding.etMinTemp.setText(UnitTools.showNoUnit(customPseudoBean.minTemp))
        binding.tvMaxTempUnit.text = UnitTools.showUnit()
        binding.tvMinTempUnit.text = UnitTools.showUnit()

        switchColorType(customPseudoBean.isColorCustom)

        //加载自定义颜色配置
        binding.pseudoPickView.onSelectChangeListener = {
            reset6CustomColor()
            color_select_view.reset()
            when (binding.pseudoPickView.sourceColors[it]) {
                0xff0000ff.toInt() -> binding.viewCustomColor1.isSelected = true
                0xffff0000.toInt() -> binding.viewCustomColor2.isSelected = true
                0xff00ff00.toInt() -> binding.viewCustomColor3.isSelected = true
                0xffffff00.toInt() -> binding.viewCustomColor4.isSelected = true
                0xff000000.toInt() -> binding.viewCustomColor5.isSelected = true
                0xffffffff.toInt() -> binding.viewCustomColor6.isSelected = true
            }
            color_select_view.selectColor(binding.pseudoPickView.sourceColors[it])
            binding.ivCustomAdd.isEnabled = binding.pseudoPickView.sourceColors.size < 7
            binding.ivCustomDel.isEnabled = binding.pseudoPickView.sourceColors.size > 3 && !binding.pseudoPickView.isCurrentOnlyLimit()
        }
        binding.pseudoPickView.reset(
            customPseudoBean.selectIndex,
            customPseudoBean.getCustomColors(),
            customPseudoBean.getCustomZAltitudes(),
            customPseudoBean.getCustomPlaces()
        )

        //加载推荐颜色配置
        binding.viewRecommendColor1.background = buildRectDrawableArray(ColorRecommend.colorList1)
        binding.viewRecommendColor2.background = buildRectDrawableArray(ColorRecommend.colorList2)
        binding.viewRecommendColor3.background = buildRectDrawableArray(ColorRecommend.getColorByIndex(isTC007, 2))
        binding.viewRecommendColor4.background = buildRectDrawableArray(ColorRecommend.colorList4)
        binding.viewRecommendColor5.background = buildRectDrawableArray(ColorRecommend.colorList5)
        switchRecommendColorIndex(customPseudoBean.customRecommendIndex)

        switchUseGray(customPseudoBean.isUseGray)


        binding.clDynamic.setOnClickListener(this)
        binding.clCustom.setOnClickListener(this)
        binding.tvColorCustom.setOnClickListener(this)
        binding.tvColorRecommend.setOnClickListener(this)
        binding.viewCustomColor1.setOnClickListener(this)
        binding.viewCustomColor2.setOnClickListener(this)
        binding.viewCustomColor3.setOnClickListener(this)
        binding.viewCustomColor4.setOnClickListener(this)
        binding.viewCustomColor5.setOnClickListener(this)
        binding.viewCustomColor6.setOnClickListener(this)
        binding.ivCustomAdd.setOnClickListener(this)
        binding.ivCustomDel.setOnClickListener(this)
        binding.viewRecommendBgColor1.setOnClickListener(this)
        binding.viewRecommendBgColor2.setOnClickListener(this)
        binding.viewRecommendBgColor3.setOnClickListener(this)
        binding.viewRecommendBgColor4.setOnClickListener(this)
        binding.viewRecommendBgColor5.setOnClickListener(this)
        binding.clOverGrey.setOnClickListener(this)
        binding.clOverColor.setOnClickListener(this)
        binding.tvConfirm.setOnClickListener(this)
        binding.tvCancel.setOnClickListener(this)

        color_select_view.onSelectListener = {
            reset6CustomColor()
            when (it) {
                0xff0000ff.toInt() -> binding.viewCustomColor1.isSelected = true
                0xffff0000.toInt() -> binding.viewCustomColor2.isSelected = true
                0xff00ff00.toInt() -> binding.viewCustomColor3.isSelected = true
                0xffffff00.toInt() -> binding.viewCustomColor4.isSelected = true
                0xff000000.toInt() -> binding.viewCustomColor5.isSelected = true
                0xffffffff.toInt() -> binding.viewCustomColor6.isSelected = true
            }
            binding.pseudoPickView.refreshColor(it)
        }
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.clDynamic -> {//动态渲染
                switchDynamicCustom(false)
            }
            binding.clCustom -> {//自定义
                switchDynamicCustom(true)
            }
            binding.tvColorCustom -> {//颜色-自定义
                switchColorType(true)
            }
            binding.tvColorRecommend -> {//颜色-推荐
                switchColorType(false)
                switchRecommendColorIndex(customPseudoBean.customRecommendIndex)
            }

            binding.viewCustomColor1 -> {//颜色-自定义-颜色值拾取1
                reset6CustomColor()
                binding.viewCustomColor1.isSelected = true
                color_select_view.selectColor(0xff0000ff.toInt())
                binding.pseudoPickView.refreshColor(0xff0000ff.toInt())
            }
            binding.viewCustomColor2 -> {//颜色-自定义-颜色值拾取2
                reset6CustomColor()
                binding.viewCustomColor2.isSelected = true
                color_select_view.selectColor(0xffff0000.toInt())
                binding.pseudoPickView.refreshColor(0xffff0000.toInt())
            }
            binding.viewCustomColor3 -> {//颜色-自定义-颜色值拾取3
                reset6CustomColor()
                binding.viewCustomColor3.isSelected = true
                color_select_view.selectColor(0xff00ff00.toInt())
                binding.pseudoPickView.refreshColor(0xff00ff00.toInt())
            }
            binding.viewCustomColor4 -> {//颜色-自定义-颜色值拾取4
                reset6CustomColor()
                binding.viewCustomColor4.isSelected = true
                color_select_view.selectColor(0xffffff00.toInt())
                binding.pseudoPickView.refreshColor(0xffffff00.toInt())
            }
            binding.viewCustomColor5 -> {//颜色-自定义-颜色值拾取5
                reset6CustomColor()
                binding.viewCustomColor5.isSelected = true
                color_select_view.selectColor(0xff000000.toInt())
                binding.pseudoPickView.refreshColor(0xff000000.toInt())
            }
            binding.viewCustomColor6 -> {//颜色-自定义-颜色值拾取6
                reset6CustomColor()
                binding.viewCustomColor6.isSelected = true
                color_select_view.selectColor(0xffffffff.toInt())
                binding.pseudoPickView.refreshColor(0xffffffff.toInt())
            }

            binding.ivCustomAdd -> {//颜色-自定义-添加
                binding.pseudoPickView.add()
            }
            binding.ivCustomDel -> {//颜色-自定义-删除
                binding.pseudoPickView.del()
            }

            binding.viewRecommendBgColor1 -> {//颜色-推荐-铁红
                switchRecommendColorIndex(0)
            }
            binding.viewRecommendBgColor2 -> {//颜色-推荐-黑红
                switchRecommendColorIndex(1)
            }
            binding.viewRecommendBgColor3 -> {//颜色-推荐-自然
                switchRecommendColorIndex(2)
            }
            binding.viewRecommendBgColor4 -> {//颜色-推荐-岩浆
                switchRecommendColorIndex(3)
            }
            binding.viewRecommendBgColor5 -> {//颜色-推荐-辉金
                switchRecommendColorIndex(4)
            }

            binding.clOverGrey -> {//灰度渐变
                switchUseGray(true)
            }
            binding.clOverColor -> {//等色
                switchUseGray(false)
            }

            binding.tvConfirm -> {//确定
                if (binding.clCustomContent.isVisible) {//使用自定义渲染
                    val inputMax = binding.etMaxTemp.text.toString()
                    if (inputMax.isEmpty()) {
                        ToastUtils.showShort(R.string.tip_input_format)
                        return
                    }
                    val inputMin = binding.etMinTemp.text.toString()
                    if (inputMin.isEmpty()) {
                        ToastUtils.showShort(R.string.tip_input_format)
                        return
                    }

                    val maxTemp = try {
                        UnitTools.showToCValue(BigDecimal(inputMax).setScale(1, RoundingMode.HALF_UP).toFloat())
                    } catch (e: NumberFormatException) {
                        null
                    }
                    val minTemp = try {
                        UnitTools.showToCValue(BigDecimal(inputMin).setScale(1, RoundingMode.HALF_UP).toFloat())
                    } catch (e: NumberFormatException) {
                        null
                    }
                    if(maxTemp == null || minTemp == null || maxTemp < minTemp || maxTemp > 550f || minTemp < -20f) {
                        ToastUtils.showShort(R.string.tip_input_format)
                        return
                    }
                    if (maxTemp - minTemp < 0.1f) {
                        ToastUtils.showShort(R.string.tip_input_format)
                        return
                    }
                    customPseudoBean.maxTemp = maxTemp
                    customPseudoBean.minTemp = minTemp
                    customPseudoBean.selectIndex = binding.pseudoPickView.selectIndex
                    customPseudoBean.colors = binding.pseudoPickView.sourceColors
                    customPseudoBean.zAltitudes = binding.pseudoPickView.zAltitudes
                    customPseudoBean.places = binding.pseudoPickView.places
                }

                val resultIntent = Intent()
                resultIntent.putExtra(ExtraKeyConfig.CUSTOM_PSEUDO_BEAN, customPseudoBean)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
            binding.tvCancel -> {//取消
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }

    /**
     * 在 动态渲染 与 自定义 之间切换.
     * @param isToCustom true-切换到自定义 false-切换到动态渲染
     */
    private fun switchDynamicCustom(isToCustom: Boolean) {
        customPseudoBean.isUseCustomPseudo = isToCustom
        binding.clCustomContent.isVisible = isToCustom
        binding.clDynamic.isSelected = !isToCustom
        binding.clCustom.isSelected = isToCustom
        binding.ivDynamic.setImageResource(if (isToCustom) R.drawable.svg_pseudo_set_dynamic_not_select else R.drawable.svg_pseudo_set_dynamic_select)
        binding.ivCustom.setImageResource(if (isToCustom) R.drawable.svg_pseudo_set_custom_select else R.drawable.svg_pseudo_set_custom_not_select)
        binding.tvDynamicTitle.setTextColor(if (isToCustom) 0xffffffff.toInt() else 0xffffba42.toInt())
        binding.tvCustomTitle.setTextColor(if (isToCustom) 0xffffba42.toInt() else 0xffffffff.toInt())
    }

    /**
     * 在自定义渲染-颜色设置中的 自定义 与 推荐 之间切换.
     * @param isToCustom true-切换到自定义 false-切换到推荐
     */
    private fun switchColorType(isToCustom: Boolean) {
        customPseudoBean.isColorCustom = isToCustom
        binding.clColorCustom.isVisible = isToCustom
        binding.clColorRecommend.isVisible = !isToCustom
        binding.tvColorCustom.setTextColor(if (isToCustom) 0xffffba42.toInt() else 0xffffffff.toInt())
        binding.tvColorRecommend.setTextColor(if (isToCustom) 0xffffffff.toInt() else 0xffffba42.toInt())
        binding.tvColorCustom.setBackgroundResource(if (isToCustom) R.drawable.bg_corners50_solid_2a183e_stroke_theme else 0)
        binding.tvColorRecommend.setBackgroundResource(if (isToCustom) 0 else R.drawable.bg_corners50_solid_2a183e_stroke_theme)
    }

    /**
     * 将自定义颜色设置中，6个预设颜色值重置为均未选中状态.
     */
    private fun reset6CustomColor() {
        binding.viewCustomColor1.isSelected = false
        binding.viewCustomColor2.isSelected = false
        binding.viewCustomColor3.isSelected = false
        binding.viewCustomColor4.isSelected = false
        binding.viewCustomColor5.isSelected = false
        binding.viewCustomColor6.isSelected = false
    }

    /**
     * 切换 推荐颜色 中的 5 个选项.
     * @param 0-铁红 1-黑红 2-自然 3-岩浆 4-辉金
     */
    private fun switchRecommendColorIndex(index: Int) {
        when (customPseudoBean.customRecommendIndex) {
            0 -> {
                binding.tvRecommendColor1.setTextColor(0x80ffffff.toInt())
                binding.viewRecommendBgColor1.setBackgroundResource(R.drawable.bg_corners04_stroke_30_ff)
            }
            1 -> {
                binding.tvRecommendColor2.setTextColor(0x80ffffff.toInt())
                binding.viewRecommendBgColor2.setBackgroundResource(R.drawable.bg_corners04_stroke_30_ff)
            }
            2 -> {
                binding.tvRecommendColor3.setTextColor(0x80ffffff.toInt())
                binding.viewRecommendBgColor3.setBackgroundResource(R.drawable.bg_corners04_stroke_30_ff)
            }
            3 -> {
                binding.tvRecommendColor4.setTextColor(0x80ffffff.toInt())
                binding.viewRecommendBgColor4.setBackgroundResource(R.drawable.bg_corners04_stroke_30_ff)
            }
            4 -> {
                binding.tvRecommendColor5.setTextColor(0x80ffffff.toInt())
                binding.viewRecommendBgColor5.setBackgroundResource(R.drawable.bg_corners04_stroke_30_ff)
            }
        }
        when (index) {
            0 -> {
                binding.tvRecommendColor1.setTextColor(0xffffba42.toInt())
                binding.viewRecommendBgColor1.setBackgroundResource(R.drawable.bg_corners04_stroke_2dp_ffba42)
            }
            1 -> {
                binding.tvRecommendColor2.setTextColor(0xffffba42.toInt())
                binding.viewRecommendBgColor2.setBackgroundResource(R.drawable.bg_corners04_stroke_2dp_ffba42)
            }
            2 -> {
                binding.tvRecommendColor3.setTextColor(0xffffba42.toInt())
                binding.viewRecommendBgColor3.setBackgroundResource(R.drawable.bg_corners04_stroke_2dp_ffba42)
            }
            3 -> {
                binding.tvRecommendColor4.setTextColor(0xffffba42.toInt())
                binding.viewRecommendBgColor4.setBackgroundResource(R.drawable.bg_corners04_stroke_2dp_ffba42)
            }
            4 -> {
                binding.tvRecommendColor5.setTextColor(0xffffba42.toInt())
                binding.viewRecommendBgColor5.setBackgroundResource(R.drawable.bg_corners04_stroke_2dp_ffba42)
            }
        }
        customPseudoBean.customRecommendIndex = index
    }

    private fun switchUseGray(isUseGray: Boolean) {
        binding.ivOverGreySelect.isVisible = isUseGray
        binding.ivOverColorSelect.isVisible = !isUseGray
        binding.tvOverGrey.setTextColor(if (isUseGray) 0xffffba42.toInt() else 0xffffffff.toInt())
        binding.tvOverColor.setTextColor(if (isUseGray) 0xffffffff.toInt() else 0xffffba42.toInt())
        binding.clOverGrey.setBackgroundResource(if (isUseGray) R.drawable.bg_corners05_solid_2a183e_stroke_theme else R.drawable.bg_corners05_solid_626569)
        binding.clOverColor.setBackgroundResource(if (isUseGray) R.drawable.bg_corners05_solid_626569 else R.drawable.bg_corners05_solid_2a183e_stroke_theme)
        customPseudoBean.isUseGray = isUseGray
    }




    private fun buildRectDrawableArray(color: IntArray): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.cornerRadius = SizeUtils.dp2px(4f).toFloat()
        drawable.colors = color
        drawable.orientation = GradientDrawable.Orientation.LEFT_RIGHT
        return drawable
    }
}