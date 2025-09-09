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
import com.topdon.menu.databinding.ViewMenuSecondBinding
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
 * Second-level menu component
 */
@SuppressLint("NotifyDataSetChanged")
class MenuSecondView : FrameLayout {
    /**
     * The menu type. Different devices (single light, dual light, Lite, TC007, 2D editing)
     * have different menus. This enum is used to distinguish between them.
     */
    private val menuType: MenuType

    // View binding for improved type safety and performance
    private lateinit var binding: ViewMenuSecondBinding


    /* ********************************************* Public Methods ********************************************* */
    /**
     * Temperature measurement: 0 -> Camera capture    Observation 10 -> Camera capture
     *
     * Temperature measurement: 1 -> Point line surface
     *
     *                         Observation 11 -> AI recognition
     *
     * Temperature measurement: 2 -> Dual light
     *                         Observation 13 -> Target
     *
     * Temperature measurement: 3 -> Pseudo-color    Observation 12 -> Pseudo-color
     *
     * Temperature measurement: 4-> Settings
     *
     *                    Observation 15->Settings
     *
     * Temperature measurement: 5-> Temperature level
     *
     *                    Observation 14->High/Low temperature points
     */
    fun selectPosition(position: Int) {
        binding.cameraMenuView.isVisible = position == 0 || position == 10
        binding.recyclerFence.isVisible = position == 1
        binding.recyclerTwoLight.isVisible = position == 2
        binding.recyclerColor.isVisible = position == 3 || position == 12
        binding.recyclerSettingTe.isVisible = position == 4
        binding.recyclerTempLevel.isVisible = position == 5

        binding.recyclerTempSource.isVisible = position == 11
        binding.recyclerTarget.isVisible = position == 13
        binding.recyclerTempPoint.isVisible = position == 14
        binding.recyclerSettingOb.isVisible = position == 15
    }



    /* *********************************************  public properties  ********************************************* */

    /**
     * First menu - Photo/Video, click event listener for various operations.
     * actionCode: 0-Photo/Video  1-Gallery  2-More menu  3-Switch to photo  4-Switch to video
     */
    var onCameraClickListener: ((actionCode: Int) -> Unit)?
        get() = binding.cameraMenuView.onCameraClickListener
        set(value) {
            binding.cameraMenuView.onCameraClickListener = value
        }
    /**
     * Temperature measurement mode - Menu 2 - Point/Line/Area switching event listener.
     */
    var onFenceListener: ((fenceType: FenceType, isSelected: Boolean) -> Unit)?
        get() = fenceAdapter.onFenceListener
        set(value) {
            fenceAdapter.onFenceListener = value
        }
    /**
     * Temperature measurement mode - Menu 3 - Dual light click event listener.
     * isSelected: true-Switch to selected false-Switch to unselected
     */
    var onTwoLightListener: ((twoLightType: TwoLightType, isSelected: Boolean) -> Unit)?
        get() = twoLightAdapter.onTwoLightListener
        set(value) {
            twoLightAdapter.onTwoLightListener = value
        }
    /**
     * Temperature measurement mode - Menu 4 - Pseudo-color / Observation mode - Menu 3 - Pseudo-color switching event listener.
     * index - Selected pseudo-color index in the list, only TC007 needs this
     * code - Pseudo-color encoding, due to historical reasons doesn't match index, used for non-TC007
     * size - Preset pseudo-color count, only TC007 needs this
     */
    var onColorListener: ((index: Int, code: Int, size: Int) -> Unit)?
        get() = colorAdapter.onColorListener
        set(value) {
            colorAdapter.onColorListener = value
        }
    /**
     * Temperature measurement mode - Menu 5 - Settings / Observation mode - Menu 6 - Settings click event listener.
     * isSelected: true-Selected state when clicked false-Unselected state when clicked
     * Warnings, fonts, watermarks are only considered highlighted when effective. Keep old code logic here,
     * Let upper layer listener handle settings menu selection refresh, may consider changing later
     */
    var onSettingListener: ((type: SettingType, isSelected: Boolean) -> Unit)?
        get() = settingTeAdapter.onSettingListener
        set(value) {
            settingTeAdapter.onSettingListener = value
            settingObAdapter.onSettingListener = value
        }
    /**
     * Temperature measurementMode-Menu6-[CN_TEXT]Low temperature[CN_TEXT] [CN_TEXT].
     *
     * [CN_TEXT]Historical legacy（Already saved[CN_TEXT] SharedPreferences [CN_TEXT]），[CN_TEXT] code [CN_TEXT]
     * - [CN_TEXT]Switch：-1
     * - High temperature(Low gain)：0
     * - Normal temperature(High gain)：1
     */
    var onTempLevelListener: ((code: Int) -> Unit)?
        get() = tempLevelAdapter.onTempLevelListener
        set(value) {
            tempLevelAdapter.onTempLevelListener = value
        }


    /**
     * ObservationMode-Menu2-[CN_TEXT]Low temperature[CN_TEXT] [CN_TEXT].
     *
     * [CN_TEXT]Historical legacy（Already saved[CN_TEXT] SharedPreferences [CN_TEXT]），[CN_TEXT] code [CN_TEXT]
     * - Nothing isSelected：-1
     * - Dynamic recognition：0
     * - High temperature[CN_TEXT]：1
     * - Low temperature[CN_TEXT]：2
     */
    var onTempSourceListener: ((code: Int) -> Unit)?
        get() = tempSourceAdapter.onTempSourceListener
        set(value) {
            tempSourceAdapter.onTempSourceListener = value
        }
    /**
     * ObservationMode-Menu4-Target [CN_TEXT].
     */
    var onTargetListener: ((targetType: TargetType) -> Unit)?
        get() = targetAdapter.onTargetListener
        set(value) {
            targetAdapter.onTargetListener = value
        }
    /**
     * ObservationMode-Menu5-High/Low temperature points [CN_TEXT].
     */
    var onTempPointListener: ((type: TempPointType, isSelected: Boolean) -> Unit)?
        get() = tempPointAdapter.onTempPointListener
        set(value) {
            tempPointAdapter.onTempPointListener = value
        }





    /**
     * Temperature measurementMode-Menu2-Point/Line/Area [CN_TEXT] Adapter.
     */
    private val fenceAdapter: FenceAdapter
    /**
     * Temperature measurement mode - Menu 3 - Dual light [CN_TEXT] Adapter.
     */
    private val twoLightAdapter: TwoLightAdapter
    /**
     * Temperature measurementMode-Menu4-Pseudo-color or ObservationMode-Menu3-Pseudo-color [CN_TEXT] Adapter.
     */
    private val colorAdapter = ColorAdapter()
    /**
     * Temperature measurementMode-Menu5-Settings [CN_TEXT] Adapter.
     */
    private val settingTeAdapter: SettingAdapter
    /**
     * Temperature measurementMode-Menu6-[CN_TEXT]Low temperature[CN_TEXT] [CN_TEXT] Adapter.
     */
    private val tempLevelAdapter: TempLevelAdapter


    /**
     * ObservationMode-Menu2-[CN_TEXT]Low temperature[CN_TEXT] [CN_TEXT] Adapter.
     */
    private val tempSourceAdapter = TempSourceAdapter()
    /**
     * ObservationMode-Menu4-Target [CN_TEXT] Adapter.
     */
    private val targetAdapter = TargetAdapter()
    /**
     * ObservationMode-Menu5-High/Low temperature points [CN_TEXT] Adapter.
     */
    private val tempPointAdapter = TempPointAdapter()
    /**
     * ObservationMode-Menu6-Settings [CN_TEXT] Adapter.
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
            binding = ViewMenuSecondBinding.inflate(LayoutInflater.from(context), this, true)
            fenceAdapter = FenceAdapter(menuType)
            twoLightAdapter = TwoLightAdapter(menuType)
            settingTeAdapter = SettingAdapter(menuType)
            tempLevelAdapter = TempLevelAdapter(menuType)
        } else {
            // Initialize view binding - replaces findViewById calls
            binding = ViewMenuSecondBinding.inflate(LayoutInflater.from(context), this, true)

            refreshImg(GalleryRepository.DirType.LINE)

            //[CN_TEXT] Temperature measurementMode-Menu2-Point/Line/Area Menu
            fenceAdapter = FenceAdapter(menuType)
            binding.recyclerFence.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerFence.adapter = fenceAdapter

            //[CN_TEXT] Temperature measurement mode - Menu 3 - Dual light Menu
            twoLightAdapter = TwoLightAdapter(menuType)
            binding.recyclerTwoLight.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerTwoLight.adapter = twoLightAdapter

            //[CN_TEXT] Temperature measurementMode-Menu4-Pseudo-color or ObservationMode-Menu3-Pseudo-color Menu
            binding.recyclerColor.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerColor.adapter = colorAdapter

            //[CN_TEXT] Temperature measurementMode-Menu5-Settings Menu
            settingTeAdapter = SettingAdapter(menuType)
            binding.recyclerSettingTe.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerSettingTe.adapter = settingTeAdapter

            //[CN_TEXT] Temperature measurementMode-Menu6-[CN_TEXT]Low temperature[CN_TEXT] Menu
            tempLevelAdapter = TempLevelAdapter(menuType)
            binding.recyclerTempLevel.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerTempLevel.adapter = tempLevelAdapter



            //[CN_TEXT] ObservationMode-Menu2-[CN_TEXT]Low temperature[CN_TEXT] Menu
            binding.recyclerTempSource.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerTempSource.adapter = tempSourceAdapter

            //[CN_TEXT] ObservationMode-Menu4-Target Menu
            binding.recyclerTarget.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerTarget.adapter = targetAdapter

            //[CN_TEXT] ObservationMode-Menu5-High/Low temperature points Menu
            binding.recyclerTempPoint.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerTempPoint.adapter = tempPointAdapter

            //[CN_TEXT] ObservationMode-Menu6-Settings Menu
            binding.recyclerSettingOb.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.recyclerSettingOb.adapter = settingObAdapter
        }
    }


    /* *********************************************  Menu1-PhotoVideo  ********************************************* */
    /**
     * Current[CN_TEXT]VideoMode.
     *
     * true-VideoMode false-PhotoMode
     */
    var isVideoMode: Boolean
        get() = binding.cameraMenuView.isVideoMode
        set(value) {
            binding.cameraMenuView.isVideoMode = value
        }

    /**
     * [CN_TEXT] TS001，Temperature measurement/Observation Switch[CN_TEXT]，[CN_TEXT]Photo、[CN_TEXT]Photo、Video[CN_TEXT]，[CN_TEXT]PhotoState.
     */
    fun switchToCamera() {
        binding.cameraMenuView.canSwitchMode = true
        binding.cameraMenuView.isVideoMode = false
        binding.cameraMenuView.setToNormal()
    }

    /**
     * [CN_TEXT]，[CN_TEXT]State[CN_TEXT]Photo、[CN_TEXT]VideoState，[CN_TEXT] Photo/Video Switch.
     * [CN_TEXT] Activity [CN_TEXT] start()，[CN_TEXT]Current View [CN_TEXT]
     */
    fun updateCameraModel() {
        binding.cameraMenuView.canSwitchMode = true
        binding.cameraMenuView.setToNormal()
    }

    fun refreshImg(type: GalleryRepository.DirType = GalleryRepository.DirType.LINE) {
        updateCameraModel()//[CN_TEXT]State
        CoroutineScope(Dispatchers.IO).launch {
            val path = GalleryRepository.readLatest(type)
            launch(Dispatchers.Main) {
                binding.cameraMenuView.refreshGallery(path)
            }
        }
    }

    /**
     * [CN_TEXT] Photo/Video [CN_TEXT]Settings[CN_TEXT] Photo[CN_TEXT]-[CN_TEXT]/Photo[CN_TEXT]-[CN_TEXT]/Video[CN_TEXT]
     */
    fun setToRecord(isDelay: Boolean) {
        binding.cameraMenuView.canSwitchMode = false
        binding.cameraMenuView.setToRecord(isDelay)
    }

    /**
     * [CN_TEXT] Photo/Video [CN_TEXT]Settings[CN_TEXT] Photo[CN_TEXT]-[CN_TEXT] State
     */
    fun setToCamera() {
        binding.cameraMenuView.setToRecord(false)
    }


    /* *****************************************  Temperature measurementMode-Menu2-Point/Line/Area  ***************************************** */
    /**
     * Temperature measurementMode-Menu2-Point/Line/Area CurrentSelected[CN_TEXT]MenuType，[CN_TEXT] null [CN_TEXT]All[CN_TEXT]Selected.
     */
    var fenceSelectType: FenceType?
        get() = fenceAdapter.selectType
        set(value) {
            fenceAdapter.selectType = value
        }


    /* *****************************************  Temperature measurement mode - Menu 3 - Dual light  ***************************************** */
    /**
     * Current single-selection dual light type
     * - Single light:  Should not usethis property
     * - Lite： Should not usethis property
     * - Dual light：  Dual light1、Dual light2、Infrared、Visible light
     * - TC007：Dual light、Infrared、Visible light、Picture in picture
     */
    var twoLightType: TwoLightType
        get() = twoLightAdapter.twoLightType
        set(value) {
            twoLightAdapter.twoLightType = value
        }

    /**
     * SettingsDual light[CN_TEXT]State
     * - Single light:  Picture in picture、Fusion degree
     * - Lite： Picture in picture、Fusion degree
     * - Dual light：  Registration、Picture in picture、Fusion degree
     * - TC007：Registration、、Fusion degree
     */
    fun setTwoLightSelected(twoLightType: TwoLightType, isSelected: Boolean) {
        twoLightAdapter.setSelected(twoLightType, isSelected)
    }


    /* **********************************  Temperature measurementMode-Menu4-Pseudo-color/ObservationMode-Menu3-Pseudo-color  ********************************** */
    /**
     * [CN_TEXT]Specified[CN_TEXT]Pseudo-color[CN_TEXT]，SelectedPseudo-colorMenu[CN_TEXT]SpecifiedPseudo-color，[CN_TEXT] code [CN_TEXT] code，[CN_TEXT]Selected[CN_TEXT]。
     * @param code 1-White hot 3-Iron red 4-Rainbow1 5-Rainbow2 6-Rainbow3 7-Red hot 8-Hot iron 9-Rainbow4 10-Rainbow5 11-Black hot
     */
    fun setPseudoColor(code: Int) {
        colorAdapter.selectCode = code
    }


    /* **********************************  Temperature measurementMode-Menu5-Settings or ObservationMode-Menu6-Settings  ********************************** */
    /**
     * SettingsSettingsMenu[CN_TEXT]SpecifiedOption[CN_TEXT]SelectedState
     */
    fun setSettingSelected(settingType: SettingType, isSelected: Boolean) {
        settingTeAdapter.setSelected(settingType, isSelected)
        settingObAdapter.setSelected(settingType, isSelected)
    }

    /**
     * SettingsSettingsMenu[CN_TEXT]RotateOption[CN_TEXT]Angle
     * @param rotateAngle Note！[CN_TEXT]Core[CN_TEXT]RotateAngle，[CN_TEXT] UI RotateAngle
     */
    fun setSettingRotate(rotateAngle: Int) {
        settingTeAdapter.rotateAngle = rotateAngle
        settingObAdapter.rotateAngle = rotateAngle
    }


    /* *****************************************  Temperature measurementMode-Menu6-[CN_TEXT]Low temperature[CN_TEXT]  ***************************************** */
    /**
     * Temperature level[CN_TEXT]Fahrenheit[CN_TEXT]
     *
     * true-Fahrenheit false-Celsius
     */
    var isUnitF: Boolean
        get() = tempLevelAdapter.isUnitF
        set(value) {
            tempLevelAdapter.isUnitF = value
        }
    /**
     * Settings Temperature measurementMode-Menu6-[CN_TEXT]Low temperature[CN_TEXT] Temperature level.
     *
     * [CN_TEXT]Historical legacy（Already saved[CN_TEXT] SharedPreferences [CN_TEXT]），[CN_TEXT] code [CN_TEXT]
     * - [CN_TEXT]Switch：-1
     * - High temperature(Low gain)：0
     * - Normal temperature(High gain)：1
     */
    fun setTempLevel(code: Int) {
        tempLevelAdapter.selectCode = code
    }






    /* *****************************************  ObservationMode-Menu2-[CN_TEXT]Low temperature[CN_TEXT]  ***************************************** */
    /**
     * Settings ObservationMode-Menu2-[CN_TEXT]Low temperature[CN_TEXT] Selected.
     *
     * [CN_TEXT]Historical legacy（Already saved[CN_TEXT] SharedPreferences [CN_TEXT]），[CN_TEXT] code [CN_TEXT]
     * - Nothing isSelected：-1
     * - Dynamic recognition：0
     * - High temperature[CN_TEXT]：1
     * - Low temperature[CN_TEXT]：2
     */
    fun setTempSource(code: Int) {
        tempSourceAdapter.selectCode = code
    }


    /* *****************************************  ObservationMode-Menu4-Target  ***************************************** */
    /**
     * Settings ObservationMode-Menu4-Target SpecifiedOption[CN_TEXT]SelectedState
     */
    fun setTargetSelected(targetType: TargetType, isSelected: Boolean) {
        targetAdapter.setSelected(targetType, isSelected)
    }
    /**
     * Settings ObservationMode-Menu4-Target-[CN_TEXT]Mode [CN_TEXT]Type.
     *
     * [CN_TEXT]Historical legacy（Already saved[CN_TEXT] SharedPreferences [CN_TEXT]），[CN_TEXT] code [CN_TEXT]
     * - Person：10
     * - Sheep：11
     * - Dog：12
     * - Bird：13
     */
    fun setTargetMode(modeCode: Int) {
        targetAdapter.setTargetMode(modeCode)
    }

    
    /* *****************************************  ObservationMode-Menu5-High/Low temperature points  ***************************************** */
    /**
     * Settings ObservationMode-Menu5-High/Low temperature points Menu[CN_TEXT]，High temperature[CN_TEXT] [CN_TEXT] [CN_TEXT] [CN_TEXT]SelectedState。
     */
    fun setTempPointSelect(tempPointType: TempPointType, isSelected: Boolean) {
        tempPointAdapter.setSelected(tempPointType, isSelected)
    }
    /**
     * Clear ObservationMode-Menu5-High/Low temperature points Menu[CN_TEXT]AllSelectedState。
     * [CN_TEXT]Maintain original logic，Consider in the futureWhether to directlySelectedDeletedone。
     */
    fun clearTempPointSelect() {
        tempPointAdapter.clearAllSelect()
    }
}