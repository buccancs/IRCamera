/*
* Copyright (c) 2016-present Original Author <1032694760@qq.com>
 *
* The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
* http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.github.gzuliyujiang.wheelpicker.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Comment removed (contained Chinese characters)
 *
* @author （1032694760@qq.com）
 * @since 2019/5/14 17:09
 */
@Retention(RetentionPolicy.SOURCE)
public @interface TimeMode {
 /**
 * Comment removed (contained Chinese characters)
 */
 int NONE = -1;
 /**
 * Comment removed (contained Chinese characters)
 */
 int HOUR_24_NO_SECOND = 0;
 /**
 * Comment removed (contained Chinese characters)
 */
 int HOUR_24_HAS_SECOND = 1;
 /**
 * Comment removed (contained Chinese characters)
 */
 int HOUR_12_NO_SECOND = 2;
 /**
 * Comment removed (contained Chinese characters)
 */
 int HOUR_12_HAS_SECOND = 3;
}
