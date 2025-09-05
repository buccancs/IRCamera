package com.topdon.module.thermal.ir.thermal.activity

import android.Manifest
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.topdon.lib.core.config.RouterConfig
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.activity.BaseIRActivity
import com.topdon.module.thermal.ir.thermal.fragment.GalleryPictureFragment
import com.topdon.module.thermal.ir.thermal.fragment.GalleryVideoFragment
import com.topdon.module.thermal.ir.databinding.ActivityGalleryBinding
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions


@Route(path = RouterConfig.IR_GALLERY_HOME)
class GalleryActivity : BaseIRActivity() {

    private lateinit var binding: ActivityGalleryBinding

//    override fun providerVMClass() = GalleryViewModel::class.java

    private val permissionList by lazy{
        if (this.applicationInfo.targetSdkVersion >= 34){
            listOf(
                Permission.READ_MEDIA_VIDEO,
                Permission.READ_MEDIA_IMAGES,
                Permission.WRITE_EXTERNAL_STORAGE,
            )
        } else if (this.applicationInfo.targetSdkVersion >= 33){
            mutableListOf(Permission.READ_MEDIA_VIDEO,
                Permission.READ_MEDIA_IMAGES,
                Permission.WRITE_EXTERNAL_STORAGE)
        }else{
            mutableListOf(Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun initContentView(): Int {
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        return 0
    }

    override fun initView() {
        // Title handling removed as toolbar_lay doesn't support it
        binding.galleryViewpager.adapter = ViewAdapter(this, supportFragmentManager)
        binding.galleryTab.setupWithViewPager(binding.galleryViewpager)

        XXPermissions.with(this)
            .permission(permissionList)
            .request { allGranted, grantedList ->
                // Handle permission result
            }
    }

    override fun initData() {
    }

    inner class ViewAdapter : FragmentPagerAdapter {
        private var titles: Array<String> = arrayOf()

        constructor (context: Context, fm: FragmentManager) : super(
            fm,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            titles = arrayOf("图片", "视频")
        }

        override fun getCount(): Int {
            return titles.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> GalleryPictureFragment()
                else -> GalleryVideoFragment()
            }
        }
    }

}