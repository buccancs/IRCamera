 * Copyright (c) 2016-present <1032694760@qq.com>
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN AS IS BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.

package com.github.gzuliyujiang.wheelpicker.entity;

import androidx.annotation.NonNull;

import com.github.gzuliyujiang.wheelview.contract.TextProvider;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

 * @author 1032694760@qq.com
 * @since 2021/10/28 9:17
public class SexEntity implements TextProvider, Serializable {
    /**
     * Private method description.
     */
    private static final boolean IS_CHINESE;
    private String id;
    private String name;
    private String english;

    static {
        IS_CHINESE = Locale.getDefault().getDisplayLanguage().contains("");
    }

    /**
     * Method description.
     */
    public String getId() {
        return id;
    }

    /**
     * Method description.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Method description.
     */
    public String getName() {
        return name;
    }

    /**
     * Method description.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method description.
     */
    public String getEnglish() {
        return english;
    }

    /**
     * Method description.
     */
    public void setEnglish(String english) {
        this.english = english;
    }

    @Override
    /**
     * Method description.
     */
    public String provideText() {
        if (IS_CHINESE) {
            return name;
        }
        return english;
    }

    @Override
    /**
     * Method description.
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SexEntity that = (SexEntity) o;
        return Objects.equals(id, that.id) ||
                Objects.equals(name, that.name) ||
                Objects.equals(english, that.english);
    }

    @Override
    /**
     * Method description.
     */
    public int hashCode() {
        return Objects.hash(id, name, english);
    }

    @NonNull
    @Override
    /**
     * Method description.
     */
    public String toString() {
        return "SexEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", english" + english + '\'' +
                '}';
    }

}
