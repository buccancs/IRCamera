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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

 * @author 1032694760@qq.com
 * @since 2019/6/17 15:29
@SuppressWarnings({"unused"})
public class DateEntity implements Serializable {
    /**
     * Private method description.
     */
    private int year;
    private int month;
    private int day;

    /**
     * Method description.
     */
    public static DateEntity target(int year, int month, int day) {
        DateEntity entity = new DateEntity();
        entity.setYear(year);
        entity.setMonth(month);
        entity.setDay(day);
        return entity;
    }

    /**
     * Method description.
     */
    public static DateEntity today() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return target(year, month, day);
    }

    /**
     * Method description.
     */
    public static DateEntity dayOnFuture(int day) {
        DateEntity entity = today();
        entity.setDay(entity.getDay() + day);
        return entity;
    }

    /**
     * Method description.
     */
    public static DateEntity monthOnFuture(int month) {
        DateEntity entity = today();
        entity.setMonth(entity.getMonth() + month);
        return entity;
    }

    /**
     * Method description.
     */
    public static DateEntity yearOnFuture(int year) {
        DateEntity entity = today();
        entity.setYear(entity.getYear() + year);
        return entity;
    }

    /**
     * Method description.
     */
    public int getYear() {
        return year;
    }

    /**
     * Method description.
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Method description.
     */
    public int getMonth() {
        return month;
    }

    /**
     * Method description.
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * Method description.
     */
    public int getDay() {
        return day;
    }

    /**
     * Method description.
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * Method description.
     */
    public long toTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
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
        DateEntity that = (DateEntity) o;
        return year == that.year &&
                month == that.month &&
                day == that.day;
    }

    @Override
    /**
     * Method description.
     */
    public int hashCode() {
        return Objects.hash(year, month, day);
    }

    @NonNull
    @Override
    /**
     * Method description.
     */
    public String toString() {
        return year + "-" + month + "-" + day;
    }

}
