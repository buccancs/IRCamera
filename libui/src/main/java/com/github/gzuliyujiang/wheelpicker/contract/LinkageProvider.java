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

import androidx.annotation.NonNull;

import java.util.List;

/**
 * [Chinese text]
 *
 * @author [Chinese text](1032694760@qq.com)
 * @since 2019/6/17 11:27
 */
public interface LinkageProvider {
    int INDEX_NO_FOUND = -1;

    /**
     * [Chinese text]
     *
     * @return [Chinese text]true[Chinese text]
     */
    boolean firstLevelVisible();

    /**
     * [Chinese text]
     *
     * @return [Chinese text]true[Chinese text]
     */
    boolean thirdLevelVisible();

    /**
     * [Chinese text]
     *
     * @return [Chinese text]
     */
    @NonNull
    List<?> provideFirstData();

    /**
     * [Chinese text]
     *
     * @param firstIndex [Chinese text]
     * @return [Chinese text]
     */
    @NonNull
    List<?> linkageSecondData(int firstIndex);

    /**
     * [Chinese text]
     *
     * @param firstIndex  [Chinese text]
     * @param secondIndex [Chinese text]
     * @return [Chinese text]
     */
    @NonNull
    List<?> linkageThirdData(int firstIndex, int secondIndex);

    /**
     * [Chinese text]
     *
     * @param firstValue [Chinese text]
     * @return [Chinese text]
     */
    int findFirstIndex(Object firstValue);

    /**
     * [Chinese text]
     *
     * @param firstIndex  [Chinese text]
     * @param secondValue [Chinese text]
     * @return [Chinese text]
     */
    int findSecondIndex(int firstIndex, Object secondValue);

    /**
     * [Chinese text]
     *
     * @param firstIndex  [Chinese text]
     * @param secondIndex [Chinese text]
     * @param thirdValue  [Chinese text]
     * @return [Chinese text]
     */
    int findThirdIndex(int firstIndex, int secondIndex, Object thirdValue);

}