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

import androidx.annotation.NonNull;

import com.github.gzuliyujiang.dialog.DialogLog;
import com.github.gzuliyujiang.wheelpicker.annotation.EthnicSpec;
import com.github.gzuliyujiang.wheelpicker.entity.EthnicEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * [Chinese text]
 *
 * @author [Chinese text](1032694760@qq.com)
 * @since 2021/6/12 13:50
 */
@SuppressWarnings({"WeakerAccess", "unused"})
    /**
     * EthnicPicker class.
     *
     * Provides ethnicpicker functionality.
     */
    public class EthnicPicker extends OptionPicker {
    public static String JSON = "[{\"code\":\"01\",\"name\":\"[Chinese text]\",\"spelling\":\"Han\"}," +
            "{\"code\":\"02\",\"name\":\"[Chinese text]\",\"spelling\":\"Mongol\"}," +
            "{\"code\":\"03\",\"name\":\"[Chinese text]\",\"spelling\":\"Hui\"}," +
            "{\"code\":\"04\",\"name\":\"[Chinese text]\",\"spelling\":\"Zang\"}," +
            "{\"code\":\"05\",\"name\":\"[Chinese text]\",\"spelling\":\"Uygur\"}," +
            "{\"code\":\"06\",\"name\":\"[Chinese text]\",\"spelling\":\"Miao\"}," +
            "{\"code\":\"07\",\"name\":\"[Chinese text]\",\"spelling\":\"Yi\"}," +
            "{\"code\":\"08\",\"name\":\"[Chinese text]\",\"spelling\":\"Zhuang\"}," +
            "{\"code\":\"09\",\"name\":\"[Chinese text]\",\"spelling\":\"Buyei\"}," +
            "{\"code\":\"10\",\"name\":\"[Chinese text]\",\"spelling\":\"Chosen\"}," +
            "{\"code\":\"11\",\"name\":\"[Chinese text]\",\"spelling\":\"Man\"}," +
            "{\"code\":\"12\",\"name\":\"[Chinese text]\",\"spelling\":\"Dong\"}," +
            "{\"code\":\"13\",\"name\":\"[Chinese text]\",\"spelling\":\"Yao\"}," +
            "{\"code\":\"14\",\"name\":\"[Chinese text]\",\"spelling\":\"Bai\"}," +
            "{\"code\":\"15\",\"name\":\"[Chinese text]\",\"spelling\":\"Tujia\"}," +
            "{\"code\":\"16\",\"name\":\"[Chinese text]\",\"spelling\":\"Hani\"}," +
            "{\"code\":\"17\",\"name\":\"[Chinese text]\",\"spelling\":\"Kazak\"}," +
            "{\"code\":\"18\",\"name\":\"[Chinese text]\",\"spelling\":\"Dai\"}," +
            "{\"code\":\"19\",\"name\":\"[Chinese text]\",\"spelling\":\"Li\"}," +
            "{\"code\":\"20\",\"name\":\"[Chinese text]\",\"spelling\":\"Lisu\"}," +
            "{\"code\":\"21\",\"name\":\"[Chinese text]\",\"spelling\":\"Va\"}," +
            "{\"code\":\"22\",\"name\":\"[Chinese text]\",\"spelling\":\"She\"}," +
            "{\"code\":\"23\",\"name\":\"high[Chinese text]\",\"spelling\":\"Gaoshan\"}," +
            "{\"code\":\"24\",\"name\":\"[Chinese text]\",\"spelling\":\"Lahu\"}," +
            "{\"code\":\"25\",\"name\":\"[Chinese text]\",\"spelling\":\"Sui\"}," +
            "{\"code\":\"26\",\"name\":\"[Chinese text]\",\"spelling\":\"Dongxiang\"}," +
            "{\"code\":\"27\",\"name\":\"[Chinese text]\",\"spelling\":\"Naxi\"}," +
            "{\"code\":\"28\",\"name\":\"[Chinese text]\",\"spelling\":\"Jingpo\"}," +
            "{\"code\":\"29\",\"name\":\"[Chinese text]\",\"spelling\":\"Kirgiz\"}," +
            "{\"code\":\"30\",\"name\":\"[Chinese text]\",\"spelling\":\"Tu\"}," +
            "{\"code\":\"31\",\"name\":\"[Chinese text]\",\"spelling\":\"Daur\"}," +
            "{\"code\":\"32\",\"name\":\"[Chinese text]\",\"spelling\":\"Mulao\"}," +
            "{\"code\":\"33\",\"name\":\"[Chinese text]\",\"spelling\":\"Qiang\"}," +
            "{\"code\":\"34\",\"name\":\"[Chinese text]\",\"spelling\":\"Blang\"}," +
            "{\"code\":\"35\",\"name\":\"[Chinese text]\",\"spelling\":\"Salar\"}," +
            "{\"code\":\"36\",\"name\":\"[Chinese text]\",\"spelling\":\"Maonan\"}," +
            "{\"code\":\"37\",\"name\":\"[Chinese text]\",\"spelling\":\"Gelao\"}," +
            "{\"code\":\"38\",\"name\":\"[Chinese text]\",\"spelling\":\"Xibe\"}," +
            "{\"code\":\"39\",\"name\":\"[Chinese text]\",\"spelling\":\"Achang\"}," +
            "{\"code\":\"40\",\"name\":\"[Chinese text]\",\"spelling\":\"Pumi\"}," +
            "{\"code\":\"41\",\"name\":\"[Chinese text]\",\"spelling\":\"Tajik\"}," +
            "{\"code\":\"42\",\"name\":\"[Chinese text]\",\"spelling\":\"Nu\"}," +
            "{\"code\":\"43\",\"name\":\"[Chinese text]\",\"spelling\":\"Uzbek\"}," +
            "{\"code\":\"44\",\"name\":\"[Chinese text]\",\"spelling\":\"Russ\"}," +
            "{\"code\":\"45\",\"name\":\"[Chinese text]\",\"spelling\":\"Ewenki\"}," +
            "{\"code\":\"46\",\"name\":\"[Chinese text]\",\"spelling\":\"Deang\"}," +
            "{\"code\":\"47\",\"name\":\"[Chinese text]\",\"spelling\":\"Bonan\"}," +
            "{\"code\":\"48\",\"name\":\"[Chinese text]\",\"spelling\":\"Yugur\"}," +
            "{\"code\":\"49\",\"name\":\"[Chinese text]\",\"spelling\":\"Gin\"}," +
            "{\"code\":\"50\",\"name\":\"[Chinese text]\",\"spelling\":\"Tatar\"}," +
            "{\"code\":\"51\",\"name\":\"[Chinese text]\",\"spelling\":\"Derung\"}," +
            "{\"code\":\"52\",\"name\":\"[Chinese text]\",\"spelling\":\"Oroqen\"}," +
            "{\"code\":\"53\",\"name\":\"[Chinese text]\",\"spelling\":\"Hezhen\"}," +
            "{\"code\":\"54\",\"name\":\"[Chinese text]\",\"spelling\":\"Monba\"}," +
            "{\"code\":\"55\",\"name\":\"[Chinese text]\",\"spelling\":\"Lhoba\"}," +
            "{\"code\":\"56\",\"name\":\"[Chinese text]\",\"spelling\":\"Jino\"}]";
    private int ethnicSpec = EthnicSpec.DEFAULT;

    public EthnicPicker(@NonNull Activity activity) {
        super(activity);
    }

    public EthnicPicker(@NonNull Activity activity, int themeResId) {
        super(activity, themeResId);
    }

    public void setEthnicSpec(@EthnicSpec int ethnicSpec) {
        this.ethnicSpec = ethnicSpec;
        setData(provideData());
    }

    @Override
    public void setDefaultValue(Object item) {
        if (item instanceof String) {
            setDefaultValueByName(item.toString());
        } else {
            super.setDefaultValue(item);
        }
    }

    public void setDefaultValueByCode(String code) {
        EthnicEntity entity = new EthnicEntity();
        entity.setCode(code);
        super.setDefaultValue(entity);
    }

    public void setDefaultValueByName(String name) {
        EthnicEntity entity = new EthnicEntity();
        entity.setName(name);
        super.setDefaultValue(entity);
    }

    public void setDefaultValueBySpelling(String spelling) {
        EthnicEntity entity = new EthnicEntity();
        entity.setSpelling(spelling);
        super.setDefaultValue(entity);
    }

    @Override
    protected List<EthnicEntity> provideData() {
        ArrayList<EthnicEntity> data = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(JSON);
            for (int i = 0, n = jsonArray.length(); i < n; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                EthnicEntity entity = new EthnicEntity();
                entity.setCode(jsonObject.getString("code"));
                entity.setName(jsonObject.getString("name"));
                entity.setSpelling(jsonObject.getString("spelling"));
                data.add(entity);
            }
        } catch (JSONException e) {
            DialogLog.print(e);
        }
        switch (ethnicSpec) {
            case EthnicSpec.DEFAULT:
                EthnicEntity other = new EthnicEntity();
                other.setCode("97");
                other.setName("[Chinese text]");
                other.setSpelling("Other");
                data.add(other);
                EthnicEntity foreign = new EthnicEntity();
                foreign.setCode("98");
                foreign.setName("[Chinese text]");
                foreign.setSpelling("Foreign");
                data.add(foreign);
                break;
            case EthnicSpec.SEVENTH_NATIONAL_CENSUS:
                EthnicEntity unrecognized = new EthnicEntity();
                unrecognized.setCode("97");
                unrecognized.setName("[Chinese text]");
                unrecognized.setSpelling("Unrecognized");
                data.add(unrecognized);
                EthnicEntity naturalization = new EthnicEntity();
                naturalization.setCode("98");
                naturalization.setName("[Chinese text]");
                naturalization.setSpelling("Naturalization");
                data.add(naturalization);
                break;
            default:
                break;
        }
        return data;
    }

}
