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
* @author （1032694760@qq.com）
 * @since 2021/6/12 13:50
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class EthnicPicker extends OptionPicker {
 public static String JSON = "[{\"code\":\"01\",\"name\":\"\",\"spelling\":\"Han\"}," +
 "{\"code\":\"02\",\"name\":\"\",\"spelling\":\"Mongol\"}," +
 "{\"code\":\"03\",\"name\":\"\",\"spelling\":\"Hui\"}," +
 "{\"code\":\"04\",\"name\":\"\",\"spelling\":\"Zang\"}," +
 "{\"code\":\"05\",\"name\":\"\",\"spelling\":\"Uygur\"}," +
 "{\"code\":\"06\",\"name\":\"\",\"spelling\":\"Miao\"}," +
 "{\"code\":\"07\",\"name\":\"\",\"spelling\":\"Yi\"}," +
 "{\"code\":\"08\",\"name\":\"\",\"spelling\":\"Zhuang\"}," +
 "{\"code\":\"09\",\"name\":\"\",\"spelling\":\"Buyei\"}," +
 "{\"code\":\"10\",\"name\":\"\",\"spelling\":\"Chosen\"}," +
 "{\"code\":\"11\",\"name\":\"\",\"spelling\":\"Man\"}," +
 "{\"code\":\"12\",\"name\":\"\",\"spelling\":\"Dong\"}," +
 "{\"code\":\"13\",\"name\":\"\",\"spelling\":\"Yao\"}," +
 "{\"code\":\"14\",\"name\":\"\",\"spelling\":\"Bai\"}," +
 "{\"code\":\"15\",\"name\":\"\",\"spelling\":\"Tujia\"}," +
 "{\"code\":\"16\",\"name\":\"\",\"spelling\":\"Hani\"}," +
 "{\"code\":\"17\",\"name\":\"\",\"spelling\":\"Kazak\"}," +
 "{\"code\":\"18\",\"name\":\"\",\"spelling\":\"Dai\"}," +
 "{\"code\":\"19\",\"name\":\"\",\"spelling\":\"Li\"}," +
 "{\"code\":\"20\",\"name\":\"\",\"spelling\":\"Lisu\"}," +
 "{\"code\":\"21\",\"name\":\"\",\"spelling\":\"Va\"}," +
 "{\"code\":\"22\",\"name\":\"\",\"spelling\":\"She\"}," +
 "{\"code\":\"23\",\"name\":\"\",\"spelling\":\"Gaoshan\"}," +
 "{\"code\":\"24\",\"name\":\"\",\"spelling\":\"Lahu\"}," +
 "{\"code\":\"25\",\"name\":\"\",\"spelling\":\"Sui\"}," +
 "{\"code\":\"26\",\"name\":\"\",\"spelling\":\"Dongxiang\"}," +
 "{\"code\":\"27\",\"name\":\"\",\"spelling\":\"Naxi\"}," +
 "{\"code\":\"28\",\"name\":\"\",\"spelling\":\"Jingpo\"}," +
 "{\"code\":\"29\",\"name\":\"\",\"spelling\":\"Kirgiz\"}," +
 "{\"code\":\"30\",\"name\":\"\",\"spelling\":\"Tu\"}," +
 "{\"code\":\"31\",\"name\":\"\",\"spelling\":\"Daur\"}," +
 "{\"code\":\"32\",\"name\":\"\",\"spelling\":\"Mulao\"}," +
 "{\"code\":\"33\",\"name\":\"\",\"spelling\":\"Qiang\"}," +
 "{\"code\":\"34\",\"name\":\"\",\"spelling\":\"Blang\"}," +
 "{\"code\":\"35\",\"name\":\"\",\"spelling\":\"Salar\"}," +
 "{\"code\":\"36\",\"name\":\"\",\"spelling\":\"Maonan\"}," +
 "{\"code\":\"37\",\"name\":\"\",\"spelling\":\"Gelao\"}," +
 "{\"code\":\"38\",\"name\":\"\",\"spelling\":\"Xibe\"}," +
 "{\"code\":\"39\",\"name\":\"\",\"spelling\":\"Achang\"}," +
 "{\"code\":\"40\",\"name\":\"\",\"spelling\":\"Pumi\"}," +
 "{\"code\":\"41\",\"name\":\"\",\"spelling\":\"Tajik\"}," +
 "{\"code\":\"42\",\"name\":\"\",\"spelling\":\"Nu\"}," +
 "{\"code\":\"43\",\"name\":\"\",\"spelling\":\"Uzbek\"}," +
 "{\"code\":\"44\",\"name\":\"\",\"spelling\":\"Russ\"}," +
 "{\"code\":\"45\",\"name\":\"Temp\",\"spelling\":\"Ewenki\"}," +
 "{\"code\":\"46\",\"name\":\"\",\"spelling\":\"Deang\"}," +
 "{\"code\":\"47\",\"name\":\"\",\"spelling\":\"Bonan\"}," +
 "{\"code\":\"48\",\"name\":\"\",\"spelling\":\"Yugur\"}," +
 "{\"code\":\"49\",\"name\":\"\",\"spelling\":\"Gin\"}," +
 "{\"code\":\"50\",\"name\":\"\",\"spelling\":\"Tatar\"}," +
 "{\"code\":\"51\",\"name\":\"\",\"spelling\":\"Derung\"}," +
 "{\"code\":\"52\",\"name\":\"\",\"spelling\":\"Oroqen\"}," +
 "{\"code\":\"53\",\"name\":\"\",\"spelling\":\"Hezhen\"}," +
 "{\"code\":\"54\",\"name\":\"\",\"spelling\":\"Monba\"}," +
 "{\"code\":\"55\",\"name\":\"\",\"spelling\":\"Lhoba\"}," +
 "{\"code\":\"56\",\"name\":\"\",\"spelling\":\"Jino\"}]";
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
 other.setName("");
 other.setSpelling("Other");
 data.add(other);
 EthnicEntity foreign = new EthnicEntity();
 foreign.setCode("98");
 foreign.setName("");
 foreign.setSpelling("Foreign");
 data.add(foreign);
 break;
 case EthnicSpec.SEVENTH_NATIONAL_CENSUS:
 EthnicEntity unrecognized = new EthnicEntity();
 unrecognized.setCode("97");
 unrecognized.setName("human");
 unrecognized.setSpelling("Unrecognized");
 data.add(unrecognized);
 EthnicEntity naturalization = new EthnicEntity();
 naturalization.setCode("98");
 naturalization.setName("");
 naturalization.setSpelling("Naturalization");
 data.add(naturalization);
 break;
 default:
 break;
 }
 return data;
 }

}
