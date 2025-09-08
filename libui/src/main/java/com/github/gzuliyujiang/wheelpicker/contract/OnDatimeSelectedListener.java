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

package com.github.gzuliyujiang.wheelpicker.contract;

/**
 * [Chinese text]
 *
 * @author [Chinese text](1032694760@qq.com)
 * @since 2021/6/5 17:29
 */
public interface OnDatimeSelectedListener {

    /**
     * [Chinese text]
     *
     * @param year   [Chinese text]
     * @param month  [Chinese text]
     * @param day    [Chinese text]
     * @param hour   [Chinese text]
     * @param minute [Chinese text]
     * @param second [Chinese text]
     */
    void onDatimeSelected(int year, int month, int day, int hour, int minute, int second);

}
