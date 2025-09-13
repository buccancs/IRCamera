package com.topdon.menu.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.topdon.menu.R as MenuR
import com.topdon.menu.databinding.ViewCameraMenuBinding

/**
 * Menu 1 - Photo and video capture related functionality.
 *
 * Central photo/video button states:
 * - Photo mode - Normal
 * - Photo mode - Capturing - Instant capture
 * - Photo mode - Capturing - Delayed capture
 * - Video mode - Normal
 * - Video mode - Recording
 *
 * Created by LCG on 2024/11/8.
 */

/**
 * Custom Camera menu view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
/**
 * CameraMenuView implements custom user interface component functionality.
 *
 * @author IRCamera Development Team
 * @since 1.0
 */
class CameraMenuView : FrameLayout, View.OnClickListener {
    companion object {
        /** onCameraClickListener event code: photo/video capture */
        const val CODE_ACTION = 0

        /** onCameraClickListener event code: gallery */
        const val CODE_GALLERY = 1

        /** onCameraClickListener event code: more menu */
        const val CODE_MORE = 2

        /** onCameraClickListener event code: switch to photo */
        const val CODE_TO_PHOTO = 3

        /** onCameraClickListener event code: switch to video */
        const val CODE_TO_VIDEO = 4
    }

    /**
     * Whether currently in video mode.
     *
     * true-video mode false-photo mode
     */
    var isVideoMode: Boolean
        get() = binding.viewPager2.currentItem == 1
        set(value) {
            binding.viewPager2.currentItem = if (value) 1 else 0
        }

    /**
     * Controls whether photo capture/video recording text is visible and switchable. 
     * Switching is not allowed during photo capture or video recording.
     *
     * true - visible and switchable, false - not visible and not switchable
     */
    var canSwitchMode: Boolean
        get() = binding.viewPager2.isUserInputEnabled
        set(value) {
            binding.viewPager2.isUserInputEnabled = value
            binding.tvPhoto.isVisible = value
            binding.tvVideo.isVisible = value
        }

    /**
     * Click event listener for various operations.
     * actionCode: 0-photo capture/video recording 1-gallery 2-more menu 3-switch to photo 4-switch to video
     */
    var onCameraClickListener: ((actionCode: Int) -> Unit)? = null

    /**
     * Sets the central photo capture/video recording button to normal state (not capturing/not recording)
     */
    fun setToNormal() {
        if (isVideoMode) {
            binding.ivAction.setImageResource(MenuR.drawable.svg_camera_video_normal)
        } else {
            binding.ivAction.setImageResource(MenuR.drawable.svg_camera_photo_normal)
        }
    }

    /**
     * Sets the central photo capture/video recording button to recording state: photo capturing (instant/delayed) or video recording
     * @param isDelay true-delayed capture false-instant capture (irrelevant for video recording)
     */
    fun setToRecord(isDelay: Boolean) {
        if (isVideoMode) {
            binding.ivAction.setImageResource(MenuR.drawable.svg_camera_video_record)
        } else {
            if (isDelay) {
                binding.ivAction.setImageResource(MenuR.drawable.svg_camera_photo_record_delay)
            } else {
                binding.ivAction.setImageResource(MenuR.drawable.svg_camera_photo_record_at_once)
            }
        }
    }

    /**
     * Refreshes the gallery cover using the specified local absolute path.
     */
    fun refreshGallery(path: String) {
        try {
            Glide.with(this)
                .load(path)
                .apply(
                    RequestOptions.bitmapTransform(MultiTransformation(CenterCrop()))
                        .placeholder(MenuR.drawable.shape_oval_33)
                        .error(MenuR.drawable.shape_oval_33),
                )
                .into(binding.ivGallery)
        } catch (_: Exception) {
        }
    }

    private lateinit var binding: ViewCameraMenuBinding

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
    ) {
        if (isInEditMode) {
            LayoutInflater.from(context).inflate(MenuR.layout.view_camera_menu, this, true)
        } else {
            binding = ViewCameraMenuBinding.inflate(LayoutInflater.from(context), this, true)

            binding.viewPager2.adapter = MenuCameraAdapter()
            binding.viewPager2.registerOnPageChangeCallback(MyOnPageChangeCallback())

            binding.ivAction.setOnClickListener(this)
            binding.ivGallery.setOnClickListener(this)
            binding.ivMore.setOnClickListener(this)
            binding.tvPhoto.setOnClickListener(this)
            binding.tvVideo.setOnClickListener(this)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        binding.viewPager2.dispatchTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    /**
     * Considering the time required for photo capture and video recording, need to prevent users from rapid clicking.
     * Saves click timestamp to avoid duplicate operations.
     */
    private var lastClickTime: Long = 0

    override fun onClick(v: View?) {
        when (v) {
            binding.ivAction -> { // Start photo/Start recording/Stop recording
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime > 500) {
                    lastClickTime = currentTime
                    onCameraClickListener?.invoke(CODE_ACTION)
                }
            }
            binding.ivGallery -> { 
                onCameraClickListener?.invoke(CODE_GALLERY)
            }
            binding.ivMore -> { // More menu
                onCameraClickListener?.invoke(CODE_MORE)
            }
            binding.tvPhoto -> { // Photo text
                binding.viewPager2.currentItem = 0
            }
            binding.tvVideo -> { // Video text
                binding.viewPager2.currentItem = 1
            }
        }
    }

    inner class MyOnPageChangeCallback : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int,
        ) {
            val scrollWidth: Float = binding.tvPhoto.width / 2f + binding.tvVideo.width / 2f
            if (position == 0) {
                binding.tvPhoto.translationX = -scrollWidth * positionOffset
                binding.tvVideo.translationX = -scrollWidth * positionOffset
            } else {
                binding.tvPhoto.translationX = -scrollWidth
                binding.tvVideo.translationX = -scrollWidth
            }
        }

        override fun onPageSelected(position: Int) {
            binding.tvPhoto.isSelected = position == 0
            binding.tvVideo.isSelected = position == 1
            binding.ivAction.setImageResource(
                if (position == 1) MenuR.drawable.svg_camera_video_normal else MenuR.drawable.svg_camera_photo_normal,
            )
            onCameraClickListener?.invoke(if (position == 1) CODE_TO_VIDEO else CODE_TO_PHOTO)
        }
    }

    /**
     * Adapter used by ViewPager2.
     */
    
/**
 * Custom Menu camera view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
/**
 * MenuCameraAdapter provides data binding between data source and UI components.
 *
 * @author IRCamera Development Team
 * @since 1.0
 */
    class MenuCameraAdapter : RecyclerView.Adapter<MenuCameraAdapter.ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): ViewHolder {
            val view = View(parent.context)
            view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int,
        ) {
        }

        override fun getItemCount(): Int = 2

        /**
         * ViewHolder(rootView: class
         */
/**
 * Custom View holder view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
/**
 * ViewHolder implements custom user interface component functionality.
 *
 * @author IRCamera Development Team
 * @since 1.0
 */
        class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView)
    }
}
