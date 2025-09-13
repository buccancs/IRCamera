package com.topdon.module.thermal.ir.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.repository.GalleryRepository.DirType
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.fragment.IRGalleryTabFragment
import com.topdon.module.thermal.ir.viewmodel.IRGalleryTabViewModel

/**
 * 图库.
 *
 * 需要传递parameter：
 * - [ExtraKeyConfig.DIR_TYPE] - 要查看的目录type 具体取值由 [DirType] 定义
 *
 * Created by LCG on 2024/2/22.
 */
class IRGalleryHomeActivity : BaseActivity() {

    private val viewModel by viewModels<IRGalleryTabViewModel>()
    private var isTS004Remote = false

    override fun initContentView(): Int = R.layout.activity_ir_gallery_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isTS004Remote = intent.getIntExtra(ExtraKeyConfig.DIR_TYPE, 0) == DirType.TS004_REMOTE.ordinal

        if (savedInstanceState == null) {
            val bundle = Bundle().apply {
                putBoolean(ExtraKeyConfig.CAN_SWITCH_DIR, false)
                putBoolean(ExtraKeyConfig.HAS_BACK_ICON, true)
                putInt(ExtraKeyConfig.DIR_TYPE, intent.getIntExtra(ExtraKeyConfig.DIR_TYPE, 0))
            }

            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, IRGalleryTabFragment::class.java, bundle)
                .commit()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.isEditModeLD.value = false
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        viewModel.isEditModeLD.observe(this) {
            callback.isEnabled = it
        }
    }

    override fun initView() {
        // Initialize views
    }

    override fun initData() {
        // Initialize data
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
