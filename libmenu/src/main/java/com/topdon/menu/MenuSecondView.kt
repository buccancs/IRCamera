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
import com.topdon.menu.constant.MenuType
import com.topdon.menu.adapter.ColorAdapter
import com.topdon.menu.adapter.FenceAdapter
import com.topdon.menu.adapter.SettingAdapter
import com.topdon.menu.adapter.TargetAdapter
import com.topdon.menu.adapter.TempLevelAdapter
import com.topdon.menu.adapter.TempPointAdapter
import com.topdon.menu.adapter.TempSourceAdapter
import com.topdon.menu.adapter.TwoLightAdapter
import com.topdon.menu.constant.FenceType
import com.topdon.menu.constant.SettingType
import com.topdon.menu.constant.TargetType
import com.topdon.menu.constant.TempPointType
import com.topdon.menu.constant.TwoLightType
import com.topdon.menu.view.CameraMenuView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * [Chinese text]menu
 */
@SuppressLint("NotifyDataSetChanged")
class MenuSecondView : FrameLayout {
    /**
     * [Chinese text]menu[Chinese text], [Chinese text]([Chinese text], Dual light, Lite, TC007, 2D[Chinese text])menu[Chinese text], [Chinese text].
     */
    private val menuType: MenuType

    // Views - using findViewById instead of data binding
    private lateinit var cameraMenuView: CameraMenuView
    private lateinit var recyclerFence: RecyclerView
    private lateinit var recyclerTwoLight: RecyclerView
    private lateinit var recyclerColor: RecyclerView
    private lateinit var recyclerSettingTe: RecyclerView
    private lateinit var recyclerTempLevel: RecyclerView
    private lateinit var recyclerTempSource: RecyclerView
    private lateinit var recyclerTarget: RecyclerView
    private lateinit var recyclerTempPoint: RecyclerView
    private lateinit var recyclerSettingOb: RecyclerView

    /* *********************************************  public [Chinese text]  ********************************************* */
    /**
     * [Chinese text]: 0-> Photo capture      [Chinese text] 10->Photo capture
     *
     * [Chinese text]: 1-> Point/Line/Area
     *
     *                    [Chinese text] 11->AI[Chinese text]
     *
     * [Chinese text]: 2-> Dual light
     *                    [Chinese text] 13->Target
     *
     * [Chinese text]: 3-> Pseudo color[Chinese text]   [Chinese text] 12->Pseudo color[Chinese text]
     *
     * [Chinese text]: 4-> Settings
     *
     *                    [Chinese text] 15->Settings
     *
     * [Chinese text]: 5-> temperaturelevel
     *
     *                    [Chinese text] 14->High/Low temperature points
     */
    fun selectPosition(position: Int) {
        cameraMenuView.isVisible = position == 0 || position == 10
        recyclerFence.isVisible = position == 1
        recyclerTwoLight.isVisible = position == 2
        recyclerColor.isVisible = position == 3 || position == 12
        recyclerSettingTe.isVisible = position == 4
        recyclerTempLevel.isVisible = position == 5

        recyclerTempSource.isVisible = position == 11
        recyclerTarget.isVisible = position == 13
        recyclerTempPoint.isVisible = position == 14
        recyclerSettingOb.isVisible = position == 15
    }

    /* *********************************************  public [Chinese text]  ********************************************* */

    /**
     * [Chinese text] 1 [Chinese text]menu-Photo capturerecording eachoperation[Chinese text]point[Chinese text]eventlistener.
     * actionCode: 0-Photo capture/recording  1-gallery  2-moremenu  3-switch[Chinese text]Photo capture  4-switch[Chinese text]recording
     */
    var onCameraClickListener: ((actionCode: Int) -> Unit)?
        get() = cameraMenuView.onCameraClickListener
        set(value) {
            cameraMenuView.onCameraClickListener = value
        }
    /**
     * Temperature measurement mode-menu2-Point/Line/Area switcheventlistener. 
     */
    var onFenceListener: ((fenceType: FenceType, isSelected: Boolean) -> Unit)?
        get() = fenceAdapter.onFenceListener
        set(value) {
            fenceAdapter.onFenceListener = value
        }
    /**
     * Temperature measurement mode-menu3-Dual light point[Chinese text]eventlistener. 
     * isSelected: true-switch[Chinese text]in progress false-switch[Chinese text]in progress
     */
    var onTwoLightListener: ((twoLightType: TwoLightType, isSelected: Boolean) -> Unit)?
        get() = twoLightAdapter.onTwoLightListener
        set(value) {
            twoLightAdapter.onTwoLightListener = value
        }
    /**
     * Temperature measurement mode-menu4-Pseudo color/Observation mode-menu3-Pseudo color Pseudo colorswitcheventlistener.
     * index-[Chinese text]in progressPseudo color[Chinese text]in progress[Chinese text] index, [Chinese text] TC007 [Chinese text]
     * code-Pseudo color[Chinese text], [Chinese text] index [Chinese text], [Chinese text] TC007 [Chinese text]
     * size-[Chinese text]Pseudo color[Chinese text], [Chinese text] TC007 [Chinese text]
     */
    var onColorListener: ((index: Int, code: Int, size: Int) -> Unit)?
        get() = colorAdapter.onColorListener
        set(value) {
            colorAdapter.onColorListener = value
        }
    /**
     * Temperature measurement mode-menu5-Settings/Observation mode-menu6-Settings point[Chinese text]eventlistener.
     * isSelected: true-point[Chinese text]in progress[Chinese text] false-point[Chinese text]in progress[Chinese text]
     * [Chinese text], [Chinese text], [Chinese text]high[Chinese text]in progress[Chinese text], [Chinese text], 
     * Settingsmenu[Chinese text]in progress[Chinese text] listener [Chinese text], [Chinese text]
     */
    var onSettingListener: ((type: SettingType, isSelected: Boolean) -> Unit)?
        get() = settingTeAdapter.onSettingListener
        set(value) {
            settingTeAdapter.onSettingListener = value
            settingObAdapter.onSettingListener = value
        }
    /**
     * Temperature measurement mode-menu6-High/Low temperature range point[Chinese text]eventlistener.
     *
     * [Chinese text]([Chinese text] SharedPreferences in progress), [Chinese text] code [Chinese text]
     * - [Chinese text]switch: -1
     * - high[Chinese text](low[Chinese text]): 0
     * - [Chinese text](high[Chinese text]): 1
     */
    var onTempLevelListener: ((code: Int) -> Unit)?
        get() = tempLevelAdapter.onTempLevelListener
        set(value) {
            tempLevelAdapter.onTempLevelListener = value
        }

    /**
     * Observation mode-menu2-High/Low temperature source point[Chinese text]eventlistener.
     *
     * [Chinese text]([Chinese text] SharedPreferences in progress), [Chinese text] code [Chinese text]
     * - [Chinese text]in progress: -1
     * - [Chinese text]: 0
     * - high[Chinese text]: 1
     * - low[Chinese text]: 2
     */
    var onTempSourceListener: ((code: Int) -> Unit)?
        get() = tempSourceAdapter.onTempSourceListener
        set(value) {
            tempSourceAdapter.onTempSourceListener = value
        }
    /**
     * Observation mode-menu4-Target point[Chinese text]eventlistener.
     */
    var onTargetListener: ((targetType: TargetType) -> Unit)?
        get() = targetAdapter.onTargetListener
        set(value) {
            targetAdapter.onTargetListener = value
        }
    /**
     * Observation mode-menu5-High/Low temperature points point[Chinese text]eventlistener.
     */
    var onTempPointListener: ((type: TempPointType, isSelected: Boolean) -> Unit)?
        get() = tempPointAdapter.onTempPointListener
        set(value) {
            tempPointAdapter.onTempPointListener = value
        }

    /**
     * Temperature measurement mode-menu2-Point/Line/Area used by Adapter.
     */
    private val fenceAdapter: FenceAdapter
    /**
     * Temperature measurement mode-menu3-Dual light used by Adapter.
     */
    private val twoLightAdapter: TwoLightAdapter
    /**
     * Temperature measurement mode-menu4-Pseudo color or Observation mode-menu3-Pseudo color used by Adapter.
     */
    private val colorAdapter = ColorAdapter()
    /**
     * Temperature measurement mode-menu5-Settings used by Adapter.
     */
    private val settingTeAdapter: SettingAdapter
    /**
     * Temperature measurement mode-menu6-High/Low temperature range used by Adapter.
     */
    private val tempLevelAdapter: TempLevelAdapter

    /**
     * Observation mode-menu2-High/Low temperature source used by Adapter.
     */
    private val tempSourceAdapter = TempSourceAdapter()
    /**
     * Observation mode-menu4-Target used by Adapter.
     */
    private val targetAdapter = TargetAdapter()
    /**
     * Observation mode-menu5-High/Low temperature points used by Adapter.
     */
    private val tempPointAdapter = TempPointAdapter()
    /**
     * Observation mode-menu6-Settings used by Adapter.
     */
    private val settingObAdapter = SettingAdapter(isObserver = true)

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.MenuSecondView, defStyleAttr, defStyleRes)
        menuType = when (typedArray.getInt(R.styleable.MenuSecondView_deviceType, 0)) {
            0 -> MenuType.SINGLE_LIGHT
            1 -> MenuType.DOUBLE_LIGHT
            2 -> MenuType.Lite
            4 -> MenuType.GALLERY_EDIT
            else -> MenuType.TC007
        }
        typedArray.recycle()

        if (isInEditMode) {
            LayoutInflater.from(context).inflate(R.layout.view_menu_second, this, true)
            fenceAdapter = FenceAdapter(menuType)
            twoLightAdapter = TwoLightAdapter(menuType)
            settingTeAdapter = SettingAdapter(menuType)
            tempLevelAdapter = TempLevelAdapter(menuType)
        } else {
            LayoutInflater.from(context).inflate(R.layout.view_menu_second, this, true)
            
            // Initialize views using findViewById
            cameraMenuView = findViewById(R.id.camera_menu_view)
            recyclerFence = findViewById(R.id.recycler_fence)
            recyclerTwoLight = findViewById(R.id.recycler_two_light)
            recyclerColor = findViewById(R.id.recycler_color)
            recyclerSettingTe = findViewById(R.id.recycler_setting_te)
            recyclerTempLevel = findViewById(R.id.recycler_temp_level)
            recyclerTempSource = findViewById(R.id.recycler_temp_source)
            recyclerTarget = findViewById(R.id.recycler_target)
            recyclerTempPoint = findViewById(R.id.recycler_temp_point)
            recyclerSettingOb = findViewById(R.id.recycler_setting_ob)

            refreshImg(GalleryRepository.DirType.LINE)

            // [Chinese text] Temperature measurement mode-menu2-Point/Line/Area menu
            fenceAdapter = FenceAdapter(menuType)
            recyclerFence.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerFence.adapter = fenceAdapter

            // [Chinese text] Temperature measurement mode-menu3-Dual light menu
            twoLightAdapter = TwoLightAdapter(menuType)
            recyclerTwoLight.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerTwoLight.adapter = twoLightAdapter

            // [Chinese text] Temperature measurement mode-menu4-Pseudo color or Observation mode-menu3-Pseudo color menu
            recyclerColor.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerColor.adapter = colorAdapter

            // [Chinese text] Temperature measurement mode-menu5-Settings menu
            settingTeAdapter = SettingAdapter(menuType)
            recyclerSettingTe.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerSettingTe.adapter = settingTeAdapter

            // [Chinese text] Temperature measurement mode-menu6-High/Low temperature range menu
            tempLevelAdapter = TempLevelAdapter(menuType)
            recyclerTempLevel.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerTempLevel.adapter = tempLevelAdapter

            // [Chinese text] Observation mode-menu2-High/Low temperature source menu
            recyclerTempSource.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerTempSource.adapter = tempSourceAdapter

            // [Chinese text] Observation mode-menu4-Target menu
            recyclerTarget.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerTarget.adapter = targetAdapter

            // [Chinese text] Observation mode-menu5-High/Low temperature points menu
            recyclerTempPoint.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerTempPoint.adapter = tempPointAdapter

            // [Chinese text] Observation mode-menu6-Settings menu
            recyclerSettingOb.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerSettingOb.adapter = settingObAdapter
        }
    }

    /* *********************************************  menu1-Photo capturerecording  ********************************************* */
    /**
     * [Chinese text]recordingmode.
     *
     * true-recordingmode false-Photo capturemode
     */
    var isVideoMode: Boolean
        get() = cameraMenuView.isVideoMode
        set(value) {
            cameraMenuView.isVideoMode = value
        }

    /**
     * only TS001, [Chinese text]/[Chinese text] switch[Chinese text], [Chinese text]Photo capture, [Chinese text]Photo capture, recording[Chinese text], [Chinese text]Photo capture[Chinese text].
     */
    fun switchToCamera() {
        cameraMenuView.canSwitchMode = true
        cameraMenuView.isVideoMode = false
        cameraMenuView.setToNormal()
    }

    /**
     * [Chinese text], [Chinese text]Photo capture, [Chinese text]recording[Chinese text], [Chinese text] Photo capture/recording switch.
     * [Chinese text]each[Chinese text] Activity [Chinese text] start(), [Chinese text] View in progress[Chinese text]
     */
    fun updateCameraModel() {
        cameraMenuView.canSwitchMode = true
        cameraMenuView.setToNormal()
    }

    fun refreshImg(type: GalleryRepository.DirType = GalleryRepository.DirType.LINE) {
        updateCameraModel()// [Chinese text]
        CoroutineScope(Dispatchers.IO).launch {
            val path = GalleryRepository.readLatest(type)
            launch(Dispatchers.Main) {
                cameraMenuView.refreshGallery(path)
            }
        }
    }

    /**
     * [Chinese text]in progress[Chinese text] Photo capture/recording buttonSettings[Chinese text] Photo capturein progress-[Chinese text]/Photo capturein progress-[Chinese text]/recordingin progress
     */
    fun setToRecord(isDelay: Boolean) {
        cameraMenuView.canSwitchMode = false
        cameraMenuView.setToRecord(isDelay)
    }

    /**
     * [Chinese text]in progress[Chinese text] Photo capture/recording buttonSettings[Chinese text] Photo capturein progress-[Chinese text] [Chinese text]
     */
    fun setToCamera() {
        cameraMenuView.setToRecord(false)
    }

    /* *****************************************  Temperature measurement mode-menu2-Point/Line/Area  ***************************************** */
    /**
     * Temperature measurement mode-menu2-Point/Line/Area [Chinese text]in progress[Chinese text]menu[Chinese text], [Chinese text] null [Chinese text]in progress.
     */
    var fenceSelectType: FenceType?
        get() = fenceAdapter.selectType
        set(value) {
            fenceAdapter.selectType = value
        }

    /* *****************************************  Temperature measurement mode-menu3-Dual light  ***************************************** */
    /**
     * [Chinese text]Dual light[Chinese text]
     * - [Chinese text]:   [Chinese text]
     * - Lite:  [Chinese text]
     * - Dual light:   Dual light1, Dual light2, [Chinese text], visible[Chinese text]
     * - TC007: Dual light, [Chinese text], visible[Chinese text], [Chinese text]in progress[Chinese text]
     */
    var twoLightType: TwoLightType
        get() = twoLightAdapter.twoLightType
        set(value) {
            twoLightAdapter.twoLightType = value
        }

    /**
     * SettingsDual light[Chinese text]
     * - [Chinese text]:   [Chinese text]in progress[Chinese text], [Chinese text]
     * - Lite:  [Chinese text]in progress[Chinese text], [Chinese text]
     * - Dual light:   [Chinese text], [Chinese text]in progress[Chinese text], [Chinese text]
     * - TC007: [Chinese text], , [Chinese text]
     */
    fun setTwoLightSelected(twoLightType: TwoLightType, isSelected: Boolean) {
        twoLightAdapter.setSelected(twoLightType, isSelected)
    }

    /* **********************************  Temperature measurement mode-menu4-Pseudo color/Observation mode-menu3-Pseudo color  ********************************** */
    /**
     * [Chinese text]Pseudo color[Chinese text], [Chinese text]in progressPseudo colormenuin progress[Chinese text]Pseudo color, [Chinese text] code [Chinese text] code, [Chinese text]in progress[Chinese text]. 
     * @param code 1-[Chinese text] 3-[Chinese text] 4-[Chinese text]1 5-[Chinese text]2 6-[Chinese text]3 7-[Chinese text] 8-[Chinese text] 9-[Chinese text]4 10-[Chinese text]5 11-[Chinese text]
     */
    fun setPseudoColor(code: Int) {
        colorAdapter.selectCode = code
    }

    /* **********************************  Temperature measurement mode-menu5-Settings or Observation mode-menu6-Settings  ********************************** */
    /**
     * SettingsSettingsmenuin progress[Chinese text]in progress[Chinese text]
     */
    fun setSettingSelected(settingType: SettingType, isSelected: Boolean) {
        settingTeAdapter.setSelected(settingType, isSelected)
        settingObAdapter.setSelected(settingType, isSelected)
    }

    /**
     * SettingsSettingsmenuin progress[Chinese text]
     * @param rotateAngle [Chinese text]! [Chinese text], [Chinese text] UI [Chinese text]
     */
    fun setSettingRotate(rotateAngle: Int) {
        settingTeAdapter.rotateAngle = rotateAngle
        settingObAdapter.rotateAngle = rotateAngle
    }

    /* *****************************************  Temperature measurement mode-menu6-High/Low temperature range  ***************************************** */
    /**
     * temperaturelevel[Chinese text]
     *
     * true-[Chinese text] false-[Chinese text]
     */
    var isUnitF: Boolean
        get() = tempLevelAdapter.isUnitF
        set(value) {
            tempLevelAdapter.isUnitF = value
        }
    /**
     * Settings Temperature measurement mode-menu6-High/Low temperature range temperaturelevel.
     *
     * [Chinese text]([Chinese text] SharedPreferences in progress), [Chinese text] code [Chinese text]
     * - [Chinese text]switch: -1
     * - high[Chinese text](low[Chinese text]): 0
     * - [Chinese text](high[Chinese text]): 1
     */
    fun setTempLevel(code: Int) {
        tempLevelAdapter.selectCode = code
    }

    /* *****************************************  Observation mode-menu2-High/Low temperature source  ***************************************** */
    /**
     * Settings Observation mode-menu2-High/Low temperature source [Chinese text]in progress.
     *
     * [Chinese text]([Chinese text] SharedPreferences in progress), [Chinese text] code [Chinese text]
     * - [Chinese text]in progress: -1
     * - [Chinese text]: 0
     * - high[Chinese text]: 1
     * - low[Chinese text]: 2
     */
    fun setTempSource(code: Int) {
        tempSourceAdapter.selectCode = code
    }

    /* *****************************************  Observation mode-menu4-Target  ***************************************** */
    /**
     * Settings Observation mode-menu4-Target [Chinese text]in progress[Chinese text]
     */
    fun setTargetSelected(targetType: TargetType, isSelected: Boolean) {
        targetAdapter.setSelected(targetType, isSelected)
    }
    /**
     * Settings Observation mode-menu4-Target-measurementmode [Chinese text].
     *
     * [Chinese text]([Chinese text] SharedPreferences in progress), [Chinese text] code [Chinese text]
     * - [Chinese text]: 10
     * - [Chinese text]: 11
     * - [Chinese text]: 12
     * - [Chinese text]: 13
     */
    fun setTargetMode(modeCode: Int) {
        targetAdapter.setTargetMode(modeCode)
    }

    /* *****************************************  Observation mode-menu5-High/Low temperature points  ***************************************** */
    /**
     * Settings Observation mode-menu5-High/Low temperature points menuin progress, high[Chinese text]point [Chinese text] low[Chinese text]point [Chinese text]in progress[Chinese text]. 
     */
    fun setTempPointSelect(tempPointType: TempPointType, isSelected: Boolean) {
        tempPointAdapter.setSelected(tempPointType, isSelected)
    }
    /**
     * [Chinese text] Observation mode-menu5-High/Low temperature points menu[Chinese text]in progress[Chinese text]. 
     * [Chinese text], [Chinese text]in progress[Chinese text]. 
     */
    fun clearTempPointSelect() {
        tempPointAdapter.clearAllSelect()
    }
}