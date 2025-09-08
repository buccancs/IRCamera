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
 * [Chinese text] DataBinding [Chinese text] DialogFragment.
 *
 * Created by LCG on 2024/10/14.
 */
abstract class BaseDialogFragment<B : ViewDataBinding> : AppCompatDialogFragment() {
    /**
     * [Chinese text] [onDestroyView] [Chinese text] binding [Chinese text] null, 
     * [Chinese text] binding [Chinese text] null [Chinese text], [Chinese text].
     */
    private var _binding: B? = null

    /**
     * [Chinese text]: [Chinese text] Fragment [Chinese text], binding [Chinese text] [onDestroyView] [Chinese text] null.
     *
     * only[Chinese text] [onCreateView] [Chinese text] [onDestroyView] [Chinese text].
     */
    protected val binding: B get() = _binding!!

    /**
     * [Chinese text]implement[Chinese text], [Chinese text] DataBinding [Chinese text] layout [Chinese text] Id.
     */
    @LayoutRes
    protected abstract fun initContentLayoutId(): Int

    /**
     * [Chinese text]implement[Chinese text], [Chinese text] onViewCreated [Chinese text].
     */
    protected abstract fun initView(savedInstanceState: Bundle?)

    /**
     * [Chinese text] [Dialog.setCanceledOnTouchOutside] [Chinese text].
     */
    var isCanceledOnTouchOutSide: Boolean = true
        set(value) {
            field = value
            dialog?.setCanceledOnTouchOutside(value)
        }

    /**
     * [Chinese text], [Chinese text] onCreateDialog [Chinese text] Dialog [Chinese text]Settings.
     */
    protected open fun afterDialogCreate(layoutParams: WindowManager.LayoutParams) {
    }

    /**
     * [Chinese text], [Chinese text] Dialog [Chinese text] themeResId.
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
