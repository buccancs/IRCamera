/*
 * Copyright (c) 2016-present [Chinese text]<1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http:// license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.github.gzuliyujiang.wheelpicker;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.gzuliyujiang.dialog.ModalDialog;
import com.github.gzuliyujiang.wheelpicker.contract.OnYearPickedListener;
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity;
import com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity;
import com.github.gzuliyujiang.wheelpicker.widget.YearWheelLayout;
import com.topdon.lib.ui.R;

import java.util.Calendar;

/**
 * [Chinese text]
 *
 * @author [Chinese text](1032694760@qq.com)
 * @since 2021/6/5 18:21
 */
@SuppressWarnings({"unused", "WeakerAccess"})
    /**
     * YearPicker class.
     *
     * Provides yearpicker functionality.
     */
    public class YearPicker extends ModalDialog {
    protected YearWheelLayout wheelLayout;
    private OnYearPickedListener onYearPickedListener;

    public YearPicker(@NonNull Activity activity, @Nullable Integer year) {
        super(activity);

        // [Chinese text]range[Chinese text]in progress[Chinese text]
        int nowYear = Calendar.getInstance().get(Calendar.YEAR);
        DatimeEntity startTimeEntity = new DatimeEntity();
        startTimeEntity.setDate(DateEntity.target(nowYear - 1000, 1, 1));
        DatimeEntity defaultEntity = new DatimeEntity();
        defaultEntity.setDate(DateEntity.target(year == null ? nowYear : year, 1, 1));
        wheelLayout.setRange(startTimeEntity, DatimeEntity.now(), defaultEntity);

        wheelLayout.setCurtainEnabled(true);// [Chinese text]in progress[Chinese text]
        wheelLayout.setCurtainColor(ContextCompat.getColor(getContext(), R.color.wheel_select_bg));       // [Chinese text]in progress[Chinese text]
        wheelLayout.setSelectedTextColor(ContextCompat.getColor(getContext(), R.color.wheel_select_text));// [Chinese text]in progresstext[Chinese text]
        wheelLayout.setTextColor(ContextCompat.getColor(getContext(), R.color.wheel_unselect_text));      // [Chinese text]in progresstext[Chinese text]

        wheelLayout.setResetWhenLinkage(false, false);// [Chinese text], [Chinese text]

    }

    @NonNull
    @Override
    protected View createBodyView() {
        wheelLayout = new YearWheelLayout(activity);
        return wheelLayout;
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected void onOk() {
        if (onYearPickedListener != null) {
            onYearPickedListener.onYearPicked(wheelLayout.getSelectedYear());
        }
    }

    public YearPicker setOnYearPickedListener(OnYearPickedListener onYearPickedListener) {
        this.onYearPickedListener = onYearPickedListener;
        return this;
    }

    public final YearWheelLayout getWheelLayout() {
        return wheelLayout;
    }

}
