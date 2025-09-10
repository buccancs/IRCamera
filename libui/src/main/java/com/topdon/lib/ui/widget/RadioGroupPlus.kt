package com.topdon.lib.ui.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.IdRes

    fun check(
        @IdRes id: Int,
    ) {
        // don't even bother
        if (id != -1 && id == checkedRadioButtonId) {
            return
        }
        if (checkedRadioButtonId != -1) {
            setCheckedStateForView(checkedRadioButtonId, false)
        }
        if (id != -1) {
            setCheckedStateForView(id, true)
        }
        setCheckedId(id)
    }

    private fun setCheckedId(
        @IdRes id: Int,
    ) {
        checkedRadioButtonId = id
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener!!.onCheckedChanged(this, checkedRadioButtonId)
        }
    }

    private fun setCheckedStateForView(
        viewId: Int,
        checked: Boolean,
    ) {
        // Use direct view traversal instead of findViewById for better performance in custom ViewGroup
        val checkedView = findViewTraversal(viewId)
        if (checkedView != null && checkedView is RadioButton) {
            checkedView.isChecked = checked
        }
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        mOnCheckedChangeListener = listener
    }

        override fun setBaseAttributes(
            a: TypedArray,
            widthAttr: Int,
            heightAttr: Int,
        ) {
            width =
                if (a.hasValue(widthAttr)) {
                    a.getLayoutDimension(widthAttr, "layout_width")
                } else {
                    WRAP_CONTENT
                }
            height =
                if (a.hasValue(heightAttr)) {
                    a.getLayoutDimension(heightAttr, "layout_height")
                } else {
                    WRAP_CONTENT
                }
        }
    }

        fun onCheckedChanged(
            group: RadioGroupPlus,
            @IdRes checkedId: Int,
        )
    }

    private inner class CheckedStateTracker : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(
            buttonView: CompoundButton,
            isChecked: Boolean,
        ) {
            // prevents from infinite recursion
            if (mProtectFromCheckedChange) {
                return
            }
            mProtectFromCheckedChange = true
            if (checkedRadioButtonId != -1) {
                setCheckedStateForView(checkedRadioButtonId, false)
            }
            mProtectFromCheckedChange = false
            val id = buttonView.id
            setCheckedId(id)
        }
    }

    /**
     *
     * A pass-through listener acts upon the events and dispatches them
     * to another listener. This allows the table layout to set its own internal
     * hierarchy change listener without preventing the user to setup his.
     */
    private inner class PassThroughHierarchyChangeListener :
        OnHierarchyChangeListener {
        var mOnHierarchyChangeListener: OnHierarchyChangeListener? = null

        fun traverseTree(view: View) {
            if (view is RadioButton) {
                var id = view.getId()
                // generates an id if it's missing
                if (id == NO_ID) {
                    id = generateViewId()
                    view.setId(id)
                }
                view.setOnCheckedChangeListener(
                    mChildOnCheckedChangeListener,
                )
            }
            if (view !is ViewGroup) {
                return
            }
            val viewGroup = view
            if (viewGroup.childCount == 0) {
                return
            }
            for (i in 0 until viewGroup.childCount) {
                traverseTree(viewGroup.getChildAt(i))
            }
        }

        /**
         * {@inheritDoc}
         */
        override fun onChildViewAdded(
            parent: View,
            child: View,
        ) {
            traverseTree(child)
            if (parent === this@RadioGroupPlus && child is RadioButton) {
                var id = child.getId()
                // generates an id if it's missing
                if (id == NO_ID) {
                    id = generateViewId()
                    child.setId(id)
                }
                child.setOnCheckedChangeListener(
                    mChildOnCheckedChangeListener,
                )
            }
            mOnHierarchyChangeListener?.onChildViewAdded(parent, child)
        }

        /**
         * {@inheritDoc}
         */
        override fun onChildViewRemoved(
            parent: View,
            child: View,
        ) {
            if (parent === this@RadioGroupPlus && child is RadioButton) {
                child.setOnCheckedChangeListener(null)
            }
            mOnHierarchyChangeListener?.onChildViewRemoved(parent, child)
        }
    }
}
