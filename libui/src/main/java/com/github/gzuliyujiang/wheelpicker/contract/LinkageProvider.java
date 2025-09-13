/*
 * Copyright (c) 2016-present human<1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.github.gzuliyujiang.wheelpicker.contract;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * 
 *
 * @author （1032694760@qq.com）
 * @since 2019/6/17 11:27
 */
public interface LinkageProvider {
    int INDEX_NO_FOUND = -1;

    /**
     * 
     *
     * @return Returntrue
     */
    boolean firstLevelVisible();

    /**
     * 
     *
     * @return Returntrue
     */
    boolean thirdLevelVisible();

    /**
     * 
     *
     * @return 
     */
    @NonNull
    List<?> provideFirstData();

    /**
     * 
     *
     * @param firstIndex index
     * @return 
     */
    @NonNull
    List<?> linkageSecondData(int firstIndex);

    /**
     * 
     *
     * @param firstIndex  index
     * @param secondIndex index
     * @return 
     */
    @NonNull
    List<?> linkageThirdData(int firstIndex, int secondIndex);

    /**
     * index
     *
     * @param firstValue 
     * @return index
     */
    int findFirstIndex(Object firstValue);

    /**
     * index
     *
     * @param firstIndex  index
     * @param secondValue 
     * @return index
     */
    int findSecondIndex(int firstIndex, Object secondValue);

    /**
     * index
     *
     * @param firstIndex  index
     * @param secondIndex index
     * @param thirdValue  
     * @return index
     */
    int findThirdIndex(int firstIndex, int secondIndex, Object thirdValue);

}