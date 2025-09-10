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
 * dialog DataBinding dialog DialogFragment.
 *
 * Created by LCG on 2024/10/14.
 */
abstract class BaseDialogFragment<B : ViewDataBinding> : AppCompatDialogFragment() {
    /**
     * dialog [onDestroyView] dialog binding dialog null，
     * dialog binding dialog null Typedialog，dialog.
     */
    private var _binding: B? = null

    /**
     * Note：dialog Fragment dialog，binding dialog [onDestroyView] dialog null.
     *
     * dialog [onCreateView] dialog [onDestroyView] dialog.
     */
    protected val binding: B get() = _binding!!

    /**
     * dialog，dialog DataBinding dialog layout dialog Id.
     */
    @LayoutRes
    protected abstract fun initContentLayoutId(): Int

    /**
     * dialog，dialog onViewCreated dialog.
     */
    protected abstract fun initView(savedInstanceState: Bundle?)

    /**
     * dialog [Dialog.setCanceledOnTouchOutside] dialog.
     */
    var isCanceledOnTouchOutSide: Boolean = true
        set(value) {
            field = value
            dialog?.setCanceledOnTouchOutside(value)
        }

    /**
     * dialog，dialog onCreateDialog dialog Dialog dialogSettings.
     */
    protected open fun afterDialogCreate(layoutParams: WindowManager.LayoutParams) {
    }

    /**
     * dialog，dialog Dialog dialog themeResId.
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
