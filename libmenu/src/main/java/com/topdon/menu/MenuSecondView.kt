package com.topdon.menu

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.repository.GalleryRepository
import com.topdon.menu.adapter.ColorAdapter
import com.topdon.menu.adapter.FenceAdapter
import com.topdon.menu.adapter.SettingAdapter
import com.topdon.menu.adapter.TargetAdapter
import com.topdon.menu.adapter.TempLevelAdapter
import com.topdon.menu.adapter.TempPointAdapter
import com.topdon.menu.adapter.TempSourceAdapter
import com.topdon.menu.adapter.TwoLightAdapter
import com.topdon.menu.constant.FenceType
import com.topdon.menu.constant.MenuType
import com.topdon.menu.constant.SettingType
import com.topdon.menu.constant.TargetType
import com.topdon.menu.constant.TempPointType
import com.topdon.menu.constant.TwoLightType
import com.topdon.menu.databinding.ViewMenuSecondBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

    var twoLightType: TwoLightType
        get() = twoLightAdapter.twoLightType
        set(value) {
            twoLightAdapter.twoLightType = value
        }

    fun setPseudoColor(code: Int) {
        colorAdapter.selectCode = code
    }

    // **********************************  Temperature measurementMode-Menu5-Settings or ObservationMode-Menu6-Settings  **********************************

    fun setSettingRotate(rotateAngle: Int) {
        settingTeAdapter.rotateAngle = rotateAngle
        settingObAdapter.rotateAngle = rotateAngle
    }

    // *****************************************  Temperature measurementMode-Menu6-dataLow temperaturedata  *****************************************

    /**
     * Temperature leveldataFahrenheitdata
     *
     * true-Fahrenheit false-Celsius
     */
    var isUnitF: Boolean
        get() = tempLevelAdapter.isUnitF
        set(value) {
            tempLevelAdapter.isUnitF = value
        }

    /**
     * Settings Temperature measurementMode-Menu6-dataLow temperaturedata Temperature level.
     *
     * Historical legacy (already saved in SharedPreferences), corresponding code values
     * - Default switch: -1
     * - High temperature(Low gain)：0
     * - Normal temperature(High gain)：1
     */
    fun setTempLevel(code: Int) {
        tempLevelAdapter.selectCode = code
    }

    // *****************************************  ObservationMode-Menu2-dataLow temperaturedata  *****************************************

    /**
     * Settings ObservationMode-Menu2-dataLow temperaturedata Selected.
     *
     * Historical legacy (already saved in SharedPreferences), corresponding code values
     * - Nothing isSelected：-1
     * - Dynamic recognition：0
     * - High temperature mode: 1
     * - Low temperature mode: 2
     */
    fun setTempSource(code: Int) {
        tempSourceAdapter.selectCode = code
    }

    // *****************************************  ObservationMode-Menu4-Target  *****************************************

    /**
     * Settings ObservationMode-Menu4-Target SpecifiedOptiondataSelectedState
     */
    fun setTargetSelected(
        targetType: TargetType,
        isSelected: Boolean,
    ) {
        targetAdapter.setSelected(targetType, isSelected)
    }

    /**
     * Settings ObservationMode-Menu4-Target-dataMode dataType.
     *
     * Historical legacy (already saved in SharedPreferences), corresponding code values
     * - Person：10
     * - Sheep：11
     * - Dog：12
     * - Bird：13
     */
    fun setTargetMode(modeCode: Int) {
        targetAdapter.setTargetMode(modeCode)
    }

    // *****************************************  ObservationMode-Menu5-High/Low temperature points  *****************************************

    /**
     * Settings ObservationMode-Menu5-High/Low temperature points Menudata，High temperaturedata data data dataSelectedState。
     */
    fun setTempPointSelect(
        tempPointType: TempPointType,
        isSelected: Boolean,
    ) {
        tempPointAdapter.setSelected(tempPointType, isSelected)
    }

    /**
     * Clear ObservationMode-Menu5-High/Low temperature points MenudataAllSelectedState。
     * dataMaintain original logic，Consider in the futureWhether to directlySelectedDeletedone。
     */
    fun clearTempPointSelect() {
        tempPointAdapter.clearAllSelect()
    }
}
