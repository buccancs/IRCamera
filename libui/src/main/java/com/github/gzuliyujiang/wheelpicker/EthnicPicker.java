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
 * 
 *
 * @author （1032694760@qq.com）
 * @since 2021/6/12 13:50
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class EthnicPicker extends OptionPicker {
    public static String JSON = "[{\"code\":\"01\",\"name\":\"Test Data",\"spelling\":\"Han\"}," +
            "{\"code\":\"02\",\"name\":\"Test Data",\"spelling\":\"Mongol\"}," +
            "{\"code\":\"03\",\"name\":\"Test Data",\"spelling\":\"Hui\"}," +
            "{\"code\":\"04\",\"name\":\"Test Data",\"spelling\":\"Zang\"}," +
            "{\"code\":\"05\",\"name\":\"Test Data",\"spelling\":\"Uygur\"}," +
            "{\"code\":\"06\",\"name\":\"Test Data",\"spelling\":\"Miao\"}," +
            "{\"code\":\"07\",\"name\":\"Test Data",\"spelling\":\"Yi\"}," +
            "{\"code\":\"08\",\"name\":\"Test Data",\"spelling\":\"Zhuang\"}," +
            "{\"code\":\"09\",\"name\":\"Test Data",\"spelling\":\"Buyei\"}," +
            "{\"code\":\"10\",\"name\":\"Test Data",\"spelling\":\"Chosen\"}," +
            "{\"code\":\"11\",\"name\":\"Test Data",\"spelling\":\"Man\"}," +
            "{\"code\":\"12\",\"name\":\"Test Data",\"spelling\":\"Dong\"}," +
            "{\"code\":\"13\",\"name\":\"Test Data",\"spelling\":\"Yao\"}," +
            "{\"code\":\"14\",\"name\":\"Test Data",\"spelling\":\"Bai\"}," +
            "{\"code\":\"15\",\"name\":\"Test Data",\"spelling\":\"Tujia\"}," +
            "{\"code\":\"16\",\"name\":\"Test Data",\"spelling\":\"Hani\"}," +
            "{\"code\":\"17\",\"name\":\"Test Data",\"spelling\":\"Kazak\"}," +
            "{\"code\":\"18\",\"name\":\"Test Data",\"spelling\":\"Dai\"}," +
            "{\"code\":\"19\",\"name\":\"Test Data",\"spelling\":\"Li\"}," +
            "{\"code\":\"20\",\"name\":\"Test Data",\"spelling\":\"Lisu\"}," +
            "{\"code\":\"21\",\"name\":\"Test Data",\"spelling\":\"Va\"}," +
            "{\"code\":\"22\",\"name\":\"Test Data",\"spelling\":\"She\"}," +
            "{\"code\":\"23\",\"name\":\"Test Data",\"spelling\":\"Gaoshan\"}," +
            "{\"code\":\"24\",\"name\":\"Test Data",\"spelling\":\"Lahu\"}," +
            "{\"code\":\"25\",\"name\":\"Test Data",\"spelling\":\"Sui\"}," +
            "{\"code\":\"26\",\"name\":\"Test Data",\"spelling\":\"Dongxiang\"}," +
            "{\"code\":\"27\",\"name\":\"Test Data",\"spelling\":\"Naxi\"}," +
            "{\"code\":\"28\",\"name\":\"Test Data",\"spelling\":\"Jingpo\"}," +
            "{\"code\":\"29\",\"name\":\"Test Data",\"spelling\":\"Kirgiz\"}," +
            "{\"code\":\"30\",\"name\":\"Test Data",\"spelling\":\"Tu\"}," +
            "{\"code\":\"31\",\"name\":\"Test Data",\"spelling\":\"Daur\"}," +
            "{\"code\":\"32\",\"name\":\"Test Data",\"spelling\":\"Mulao\"}," +
            "{\"code\":\"33\",\"name\":\"Test Data",\"spelling\":\"Qiang\"}," +
            "{\"code\":\"34\",\"name\":\"Test Data",\"spelling\":\"Blang\"}," +
            "{\"code\":\"35\",\"name\":\"Test Data",\"spelling\":\"Salar\"}," +
            "{\"code\":\"36\",\"name\":\"Test Data",\"spelling\":\"Maonan\"}," +
            "{\"code\":\"37\",\"name\":\"Test Data",\"spelling\":\"Gelao\"}," +
            "{\"code\":\"38\",\"name\":\"Test Data",\"spelling\":\"Xibe\"}," +
            "{\"code\":\"39\",\"name\":\"Test Data",\"spelling\":\"Achang\"}," +
            "{\"code\":\"40\",\"name\":\"Test Data",\"spelling\":\"Pumi\"}," +
            "{\"code\":\"41\",\"name\":\"Test Data",\"spelling\":\"Tajik\"}," +
            "{\"code\":\"42\",\"name\":\"Test Data",\"spelling\":\"Nu\"}," +
            "{\"code\":\"43\",\"name\":\"Test Data",\"spelling\":\"Uzbek\"}," +
            "{\"code\":\"44\",\"name\":\"Test Data",\"spelling\":\"Russ\"}," +
            "{\"code\":\"45\",\"name\":\"Test Data",\"spelling\":\"Ewenki\"}," +
            "{\"code\":\"46\",\"name\":\"Test Data",\"spelling\":\"Deang\"}," +
            "{\"code\":\"47\",\"name\":\"Test Data",\"spelling\":\"Bonan\"}," +
            "{\"code\":\"48\",\"name\":\"Test Data",\"spelling\":\"Yugur\"}," +
            "{\"code\":\"49\",\"name\":\"Test Data",\"spelling\":\"Gin\"}," +
            "{\"code\":\"50\",\"name\":\"Test Data",\"spelling\":\"Tatar\"}," +
            "{\"code\":\"51\",\"name\":\"Test Data",\"spelling\":\"Derung\"}," +
            "{\"code\":\"52\",\"name\":\"Test Data",\"spelling\":\"Oroqen\"}," +
            "{\"code\":\"53\",\"name\":\"Test Data",\"spelling\":\"Hezhen\"}," +
            "{\"code\":\"54\",\"name\":\"Test Data",\"spelling\":\"Monba\"}," +
            "{\"code\":\"55\",\"name\":\"Test Data",\"spelling\":\"Lhoba\"}," +
            "{\"code\":\"56\",\"name\":\"Test Data",\"spelling\":\"Jino\"}]";
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
                other.setName("Test Data");
                other.setSpelling("Other");
                data.add(other);
                EthnicEntity foreign = new EthnicEntity();
                foreign.setCode("98");
                foreign.setName("Test Data");
                foreign.setSpelling("Foreign");
                data.add(foreign);
                break;
            case EthnicSpec.SEVENTH_NATIONAL_CENSUS:
                EthnicEntity unrecognized = new EthnicEntity();
                unrecognized.setCode("97");
                unrecognized.setName("Test Data");
                unrecognized.setSpelling("Unrecognized");
                data.add(unrecognized);
                EthnicEntity naturalization = new EthnicEntity();
                naturalization.setCode("98");
                naturalization.setName("Test Data");
                naturalization.setSpelling("Naturalization");
                data.add(naturalization);
                break;
            default:
                break;
        }
        return data;
    }

}
