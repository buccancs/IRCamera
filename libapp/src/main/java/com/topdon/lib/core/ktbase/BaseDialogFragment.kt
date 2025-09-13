package com.topdon.lib.core.ktbase

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import com.topdon.lib.core.R

/**
 * Comment removed (contained Chinese characters)
 *
 * Created by LCG on 2024/10/14.
 */
abstract class BaseDialogFragment<B : ViewDataBinding> : AppCompatDialogFragment() {
 /**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 private var _binding: B? = null

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 */
 protected val binding: B get() = _binding!!

 /**
 * Comment removed (contained Chinese characters)
 */
 @LayoutRes
 protected abstract fun initContentLayoutId(): Int

 /**
 * Comment removed (contained Chinese characters)
 */
 protected abstract fun initView(savedInstanceState: Bundle?)

 /**
 * Comment removed (contained Chinese characters)
 */
 var isCanceledOnTouchOutSide: Boolean = true
 set(value) {
 field = value
 dialog?.setCanceledOnTouchOutside(value)
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 protected open fun afterDialogCreate(layoutParams: WindowManager.LayoutParams) {
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 @StyleRes
 protected open fun getDialogThemeResId(): Int = R.style.base_dialog

 override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
 val dialog = Dialog(requireContext(), getDialogThemeResId())
 dialog.setCancelable(isCancelable)
 dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutSide)
 dialog.window?.let {
 val layoutParams = it.attributes
 afterDialogCreate(layoutParams)
 dialog.onWindowAttributesChanged(layoutParams)
 }
 return dialog
 }

 override fun onCreateView(
 inflater: LayoutInflater,
 container: ViewGroup?,
 savedInstanceState: Bundle?,
 ): View? {
 _binding = DataBindingUtil.inflate(inflater, initContentLayoutId(), container, false)
 _binding?.lifecycleOwner = viewLifecycleOwner
 return binding.root
 }

 override fun onViewCreated(
 view: View,
 savedInstanceState: Bundle?,
 ) {
 initView(savedInstanceState)
 }

 override fun onDestroyView() {
 super.onDestroyView()
 _binding = null
 }

 fun show(context: Context) {
 if (isAdded) {
 return
 }
 if (context is FragmentActivity) {
 super.show(context.supportFragmentManager, null)
 context.supportFragmentManager.executePendingTransactions()
 }
 }
}
