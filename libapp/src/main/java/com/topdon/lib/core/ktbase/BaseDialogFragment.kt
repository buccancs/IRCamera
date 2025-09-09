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
 * [CN_TEXT] DataBinding [CN_TEXT] DialogFragment.
 *
 * Created by LCG on 2024/10/14.
 */
abstract class BaseDialogFragment<B : ViewDataBinding> : AppCompatDialogFragment() {
    /**
     * [CN_TEXT] [onDestroyView] [CN_TEXT] binding [CN_TEXT] null，
     * [CN_TEXT] binding [CN_TEXT] null Type[CN_TEXT]，[CN_TEXT].
     */
    private var _binding: B? = null

    /**
     * Note：[CN_TEXT] Fragment [CN_TEXT]，binding [CN_TEXT] [onDestroyView] [CN_TEXT] null.
     *
     * [CN_TEXT] [onCreateView] [CN_TEXT] [onDestroyView] [CN_TEXT].
     */
    protected val binding: B get() = _binding!!

    /**
     * [CN_TEXT]，[CN_TEXT] DataBinding [CN_TEXT] layout [CN_TEXT] Id.
     */
    @LayoutRes
    protected abstract fun initContentLayoutId(): Int

    /**
     * [CN_TEXT]，[CN_TEXT] onViewCreated [CN_TEXT].
     */
    protected abstract fun initView(savedInstanceState: Bundle?)

    /**
     * [CN_TEXT] [Dialog.setCanceledOnTouchOutside] [CN_TEXT].
     */
    var isCanceledOnTouchOutSide: Boolean = true
        set(value) {
            field = value
            dialog?.setCanceledOnTouchOutside(value)
        }

    /**
     * [CN_TEXT]，[CN_TEXT] onCreateDialog [CN_TEXT] Dialog [CN_TEXT]Settings.
     */
    protected open fun afterDialogCreate(layoutParams: WindowManager.LayoutParams) {
    }

    /**
     * [CN_TEXT]，[CN_TEXT] Dialog [CN_TEXT] themeResId.
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
