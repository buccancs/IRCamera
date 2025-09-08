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
 * @since 2021/10/28 8:37
public class ConstellationEntity implements TextProvider, Serializable {
    /**
     * Private method description.
     */
    private static final boolean IS_CHINESE;
    private String id;
    private String startDate;
    private String endDate;
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
    public String getStartDate() {
        return startDate;
    }

    /**
     * Method description.
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Method description.
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Method description.
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
        ConstellationEntity that = (ConstellationEntity) o;
        return Objects.equals(id, that.id) ||
                Objects.equals(startDate, that.startDate) ||
                Objects.equals(endDate, that.endDate) ||
                Objects.equals(name, that.name) ||
                Objects.equals(english, that.english);
    }

    @Override
    /**
     * Method description.
     */
    public int hashCode() {
        return Objects.hash(id, startDate, endDate, name, english);
    }

    @NonNull
    @Override
    /**
     * Method description.
     */
    public String toString() {
        return "ConstellationEntity{" +
                "id='" + id + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", name='" + name + '\'' +
                ", english" + english + '\'' +
                '}';
    }

}
