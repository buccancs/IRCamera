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
 * @since 2019/5/14 19:55
 */
public interface DateFormatter {

    /**
     * [Chinese text]
     *
     * @param year [Chinese text]
     * @return [Chinese text]
     */
    String formatYear(int year);

    /**
     * [Chinese text]
     *
     * @param month [Chinese text]
     * @return [Chinese text]
     */
    String formatMonth(int month);

    /**
     * [Chinese text]
     *
     * @param day [Chinese text]
     * @return [Chinese text]
     */
    String formatDay(int day);

}

