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
 * 二级菜单
 */
@SuppressLint("NotifyDataSetChanged")
class MenuSecondView : FrameLayout {
    /**
     * 该菜单的类型，由于不同的设备（单光、Dual light、Lite、TC007、2D编辑）菜单存在差异，用该枚举区分.
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

    /* *********************************************  public 方法  ********************************************* */
    /**
     * 测温: 0-> Photo capture      观测 10->Photo capture
     *
     * 测温: 1-> Point/Line/Area
     *
     *                    观测 11->AI识别
     *
     * 测温: 2-> Dual light
     *                    观测 13->Target
     *
     * 测温: 3-> Pseudo color颜色   观测 12->Pseudo color颜色
     *
     * 测温: 4-> Settings
     *
     *                    观测 15->Settings
     *
     * 测温: 5-> 温度档位
     *
     *                    观测 14->High/Low temperature points
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

    /* *********************************************  public 属性  ********************************************* */

    /**
     * 第 1 个菜单-Photo capture录像 各个操作的点击事件监听.
     * actionCode: 0-Photo capture/录像  1-图库  2-更多菜单  3-切换到Photo capture  4-切换到录像
     */
    var onCameraClickListener: ((actionCode: Int) -> Unit)?
        get() = cameraMenuView.onCameraClickListener
        set(value) {
            cameraMenuView.onCameraClickListener = value
        }
    /**
     * Temperature measurement mode-菜单2-Point/Line/Area 切换事件监听。
     */
    var onFenceListener: ((fenceType: FenceType, isSelected: Boolean) -> Unit)?
        get() = fenceAdapter.onFenceListener
        set(value) {
            fenceAdapter.onFenceListener = value
        }
    /**
     * Temperature measurement mode-菜单3-Dual light 点击事件监听。
     * isSelected: true-切换为选中 false-切换为未选中
     */
    var onTwoLightListener: ((twoLightType: TwoLightType, isSelected: Boolean) -> Unit)?
        get() = twoLightAdapter.onTwoLightListener
        set(value) {
            twoLightAdapter.onTwoLightListener = value
        }
    /**
     * Temperature measurement mode-菜单4-Pseudo color/Observation mode-菜单3-Pseudo color Pseudo color切换事件监听.
     * index-选中Pseudo color在列表中的 index，也就 TC007 要用
     * code-Pseudo color编码，由于历史遗留跟 index 对不上，非 TC007 时使用
     * size-预设Pseudo color数量，也就 TC007 要用
     */
    var onColorListener: ((index: Int, code: Int, size: Int) -> Unit)?
        get() = colorAdapter.onColorListener
        set(value) {
            colorAdapter.onColorListener = value
        }
    /**
     * Temperature measurement mode-菜单5-Settings/Observation mode-菜单6-Settings 点击事件监听.
     * isSelected: true-点击时为选中状态 false-点击时为未选中状态
     * 警示、字体、水印是以生效才视为高亮选中的，这里先保持旧代码逻辑，
     * Settings菜单的选中刷新丢给上层的 listener 去做，后面有空再考虑更改
     */
    var onSettingListener: ((type: SettingType, isSelected: Boolean) -> Unit)?
        get() = settingTeAdapter.onSettingListener
        set(value) {
            settingTeAdapter.onSettingListener = value
            settingObAdapter.onSettingListener = value
        }
    /**
     * Temperature measurement mode-菜单6-High/Low temperature range 点击事件监听.
     *
     * 由于历史遗留（已保存在 SharedPreferences 中），这里 code 取值为
     * - 自动切换：-1
     * - 高温(低增益)：0
     * - 常温(高增益)：1
     */
    var onTempLevelListener: ((code: Int) -> Unit)?
        get() = tempLevelAdapter.onTempLevelListener
        set(value) {
            tempLevelAdapter.onTempLevelListener = value
        }

    /**
     * Observation mode-菜单2-High/Low temperature source 点击事件监听.
     *
     * 由于历史遗留（已保存在 SharedPreferences 中），这里 code 取值为
     * - 什么都未选中：-1
     * - 动态识别：0
     * - 高温源：1
     * - 低温源：2
     */
    var onTempSourceListener: ((code: Int) -> Unit)?
        get() = tempSourceAdapter.onTempSourceListener
        set(value) {
            tempSourceAdapter.onTempSourceListener = value
        }
    /**
     * Observation mode-菜单4-Target 点击事件监听.
     */
    var onTargetListener: ((targetType: TargetType) -> Unit)?
        get() = targetAdapter.onTargetListener
        set(value) {
            targetAdapter.onTargetListener = value
        }
    /**
     * Observation mode-菜单5-High/Low temperature points 点击事件监听.
     */
    var onTempPointListener: ((type: TempPointType, isSelected: Boolean) -> Unit)?
        get() = tempPointAdapter.onTempPointListener
        set(value) {
            tempPointAdapter.onTempPointListener = value
        }

    /**
     * Temperature measurement mode-菜单2-Point/Line/Area 所用 Adapter.
     */
    private val fenceAdapter: FenceAdapter
    /**
     * Temperature measurement mode-菜单3-Dual light 所用 Adapter.
     */
    private val twoLightAdapter: TwoLightAdapter
    /**
     * Temperature measurement mode-菜单4-Pseudo color or Observation mode-菜单3-Pseudo color 所用 Adapter.
     */
    private val colorAdapter = ColorAdapter()
    /**
     * Temperature measurement mode-菜单5-Settings 所用 Adapter.
     */
    private val settingTeAdapter: SettingAdapter
    /**
     * Temperature measurement mode-菜单6-High/Low temperature range 所用 Adapter.
     */
    private val tempLevelAdapter: TempLevelAdapter

    /**
     * Observation mode-菜单2-High/Low temperature source 所用 Adapter.
     */
    private val tempSourceAdapter = TempSourceAdapter()
    /**
     * Observation mode-菜单4-Target 所用 Adapter.
     */
    private val targetAdapter = TargetAdapter()
    /**
     * Observation mode-菜单5-High/Low temperature points 所用 Adapter.
     */
    private val tempPointAdapter = TempPointAdapter()
    /**
     * Observation mode-菜单6-Settings 所用 Adapter.
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

            // 初始化 Temperature measurement mode-菜单2-Point/Line/Area 菜单
            fenceAdapter = FenceAdapter(menuType)
            recyclerFence.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerFence.adapter = fenceAdapter

            // 初始化 Temperature measurement mode-菜单3-Dual light 菜单
            twoLightAdapter = TwoLightAdapter(menuType)
            recyclerTwoLight.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerTwoLight.adapter = twoLightAdapter

            // 初始化 Temperature measurement mode-菜单4-Pseudo color or Observation mode-菜单3-Pseudo color 菜单
            recyclerColor.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerColor.adapter = colorAdapter

            // 初始化 Temperature measurement mode-菜单5-Settings 菜单
            settingTeAdapter = SettingAdapter(menuType)
            recyclerSettingTe.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerSettingTe.adapter = settingTeAdapter

            // 初始化 Temperature measurement mode-菜单6-High/Low temperature range 菜单
            tempLevelAdapter = TempLevelAdapter(menuType)
            recyclerTempLevel.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerTempLevel.adapter = tempLevelAdapter

            // 初始化 Observation mode-菜单2-High/Low temperature source 菜单
            recyclerTempSource.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerTempSource.adapter = tempSourceAdapter

            // 初始化 Observation mode-菜单4-Target 菜单
            recyclerTarget.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerTarget.adapter = targetAdapter

            // 初始化 Observation mode-菜单5-High/Low temperature points 菜单
            recyclerTempPoint.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerTempPoint.adapter = tempPointAdapter

            // 初始化 Observation mode-菜单6-Settings 菜单
            recyclerSettingOb.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerSettingOb.adapter = settingObAdapter
        }
    }

    /* *********************************************  菜单1-Photo capture录像  ********************************************* */
    /**
     * 当前是否处于录像模式.
     *
     * true-录像模式 false-Photo capture模式
     */
    var isVideoMode: Boolean
        get() = cameraMenuView.isVideoMode
        set(value) {
            cameraMenuView.isVideoMode = value
        }

    /**
     * 仅 TS001，测温/观测 切换时，关闭延时Photo capture、连续Photo capture、录像后，需要重置为Photo capture状态.
     */
    fun switchToCamera() {
        cameraMenuView.canSwitchMode = true
        cameraMenuView.isVideoMode = false
        cameraMenuView.setToNormal()
    }

    /**
     * 类似重置，这个方法的目的是重置状态为未Photo capture、未录像状态，且放开 Photo capture/录像 切换.
     * 在各个热成像 Activity 的 start()，以及当前 View 中调用
     */
    fun updateCameraModel() {
        cameraMenuView.canSwitchMode = true
        cameraMenuView.setToNormal()
    }

    fun refreshImg(type: GalleryRepository.DirType = GalleryRepository.DirType.LINE) {
        updateCameraModel()// 恢复状态
        CoroutineScope(Dispatchers.IO).launch {
            val path = GalleryRepository.readLatest(type)
            launch(Dispatchers.Main) {
                cameraMenuView.refreshGallery(path)
            }
        }
    }

    /**
     * 将中间 Photo capture/录像 按钮Settings为 Photo capture中-立即/Photo capture中-延迟/录像中
     */
    fun setToRecord(isDelay: Boolean) {
        cameraMenuView.canSwitchMode = false
        cameraMenuView.setToRecord(isDelay)
    }

    /**
     * 将中间 Photo capture/录像 按钮Settings为 Photo capture中-立即 状态
     */
    fun setToCamera() {
        cameraMenuView.setToRecord(false)
    }

    /* *****************************************  Temperature measurement mode-菜单2-Point/Line/Area  ***************************************** */
    /**
     * Temperature measurement mode-菜单2-Point/Line/Area 当前选中的菜单类型，若为 null 表示所有都未选中.
     */
    var fenceSelectType: FenceType?
        get() = fenceAdapter.selectType
        set(value) {
            fenceAdapter.selectType = value
        }

    /* *****************************************  Temperature measurement mode-菜单3-Dual light  ***************************************** */
    /**
     * 当前单选的Dual light类型
     * - 单光：  不应该使用这个属性
     * - Lite： 不应该使用这个属性
     * - Dual light：  Dual light1、Dual light2、红外、可见光
     * - TC007：Dual light、红外、可见光、画中画
     */
    var twoLightType: TwoLightType
        get() = twoLightAdapter.twoLightType
        set(value) {
            twoLightAdapter.twoLightType = value
        }

    /**
     * SettingsDual light多选状态
     * - 单光：  画中画、融合度
     * - Lite： 画中画、融合度
     * - Dual light：  配准、画中画、融合度
     * - TC007：配准、、融合度
     */
    fun setTwoLightSelected(twoLightType: TwoLightType, isSelected: Boolean) {
        twoLightAdapter.setSelected(twoLightType, isSelected)
    }

    /* **********************************  Temperature measurement mode-菜单4-Pseudo color/Observation mode-菜单3-Pseudo color  ********************************** */
    /**
     * 根据指定的Pseudo color代号，选中Pseudo color菜单中的指定Pseudo color，若传递的 code 为不支持 code，则为全部未选中效果。
     * @param code 1-白热 3-铁红 4-彩虹1 5-彩虹2 6-彩虹3 7-红热 8-热铁 9-彩虹4 10-彩虹5 11-黑热
     */
    fun setPseudoColor(code: Int) {
        colorAdapter.selectCode = code
    }

    /* **********************************  Temperature measurement mode-菜单5-Settings or Observation mode-菜单6-Settings  ********************************** */
    /**
     * SettingsSettings菜单中指定选项的选中状态
     */
    fun setSettingSelected(settingType: SettingType, isSelected: Boolean) {
        settingTeAdapter.setSelected(settingType, isSelected)
        settingObAdapter.setSelected(settingType, isSelected)
    }

    /**
     * SettingsSettings菜单中旋转选项的角度
     * @param rotateAngle 注意！这个值是机芯的旋转角度，非 UI 旋转角度
     */
    fun setSettingRotate(rotateAngle: Int) {
        settingTeAdapter.rotateAngle = rotateAngle
        settingObAdapter.rotateAngle = rotateAngle
    }

    /* *****************************************  Temperature measurement mode-菜单6-High/Low temperature range  ***************************************** */
    /**
     * 温度档位是否使用华氏度作为单位
     *
     * true-华氏度 false-摄氏度
     */
    var isUnitF: Boolean
        get() = tempLevelAdapter.isUnitF
        set(value) {
            tempLevelAdapter.isUnitF = value
        }
    /**
     * Settings Temperature measurement mode-菜单6-High/Low temperature range 温度档位.
     *
     * 由于历史遗留（已保存在 SharedPreferences 中），这里 code 取值为
     * - 自动切换：-1
     * - 高温(低增益)：0
     * - 常温(高增益)：1
     */
    fun setTempLevel(code: Int) {
        tempLevelAdapter.selectCode = code
    }

    /* *****************************************  Observation mode-菜单2-High/Low temperature source  ***************************************** */
    /**
     * Settings Observation mode-菜单2-High/Low temperature source 选中.
     *
     * 由于历史遗留（已保存在 SharedPreferences 中），这里 code 取值为
     * - 什么都未选中：-1
     * - 动态识别：0
     * - 高温源：1
     * - 低温源：2
     */
    fun setTempSource(code: Int) {
        tempSourceAdapter.selectCode = code
    }

    /* *****************************************  Observation mode-菜单4-Target  ***************************************** */
    /**
     * Settings Observation mode-菜单4-Target 指定选项的选中状态
     */
    fun setTargetSelected(targetType: TargetType, isSelected: Boolean) {
        targetAdapter.setSelected(targetType, isSelected)
    }
    /**
     * Settings Observation mode-菜单4-Target-测量模式 图标类型.
     *
     * 由于历史遗留（已保存在 SharedPreferences 中），这里 code 取值为
     * - 人：10
     * - 羊：11
     * - 狗：12
     * - 鸟：13
     */
    fun setTargetMode(modeCode: Int) {
        targetAdapter.setTargetMode(modeCode)
    }

    /* *****************************************  Observation mode-菜单5-High/Low temperature points  ***************************************** */
    /**
     * Settings Observation mode-菜单5-High/Low temperature points 菜单中，高温点 或 低稳点 的选中状态。
     */
    fun setTempPointSelect(tempPointType: TempPointType, isSelected: Boolean) {
        tempPointAdapter.setSelected(tempPointType, isSelected)
    }
    /**
     * 清除 Observation mode-菜单5-High/Low temperature points 菜单的所有选中状态。
     * 这里维持原有逻辑，后续考虑是否直接给选中删除得了。
     */
    fun clearTempPointSelect() {
        tempPointAdapter.clearAllSelect()
    }
}