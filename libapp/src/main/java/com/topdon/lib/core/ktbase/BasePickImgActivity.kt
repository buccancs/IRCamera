package com.topdon.lib.core.ktbase

import com.topdon.lib.core.R
import com.topdon.lib.core.databinding.ActivityImagePickIrPlushBinding
import com.topdon.lib.core.dialog.TipDialog

/**
 * Base activity for image selection and editing functionality
 */
abstract class BasePickImgActivity : BaseBindingActivity<ActivityImagePickIrPlushBinding>() {

    private fun showExitTipsDialog(listener: (() -> Unit)) {
        TipDialog.Builder(this)
            .setMessage(R.string.diy_tip_save)
            .setPositiveListener(R.string.app_exit) {
                listener.invoke()
            }
            .setCancelListener(R.string.app_cancel)
            .create().show()
    }

    override fun disConnected() {
        super.disConnected()
        finish()
    }
}
