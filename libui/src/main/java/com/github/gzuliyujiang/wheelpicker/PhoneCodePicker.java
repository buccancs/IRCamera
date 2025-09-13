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
import androidx.annotation.StyleRes;

import com.github.gzuliyujiang.dialog.DialogLog;
import com.github.gzuliyujiang.wheelpicker.entity.PhoneCodeEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @author （1032694760@qq.com）
 * @since 2019/5/10 16:44
 */
@SuppressWarnings("unused")
public class PhoneCodePicker extends OptionPicker {
    public static String JSON = "[{\"prefix\":\"1\",\"en\":\"USA\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1\",\"en\":\"PuertoRico\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1\",\"en\":\"Canada\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"7\",\"en\":\"Russia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"7\",\"en\":\"Kazeakhstan\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"20\",\"en\":\"Egypt\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"27\",\"en\":\"South Africa\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"30\",\"en\":\"Greece\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"31\",\"en\":\"Netherlands\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"32\",\"en\":\"Belgium\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"33\",\"en\":\"France\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"34\",\"en\":\"Spain\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"36\",\"en\":\"Hungary\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"40\",\"en\":\"Romania\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"41\",\"en\":\"Switzerland\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"43\",\"en\":\"Austria\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"44\",\"en\":\"United Kingdom\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"44\",\"en\":\"Jersey\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"44\",\"en\":\"Isle of Man\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"44\",\"en\":\"Guernsey\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"45\",\"en\":\"Denmark\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"46\",\"en\":\"Sweden\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"47\",\"en\":\"Norway\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"48\",\"en\":\"Poland\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"51\",\"en\":\"Peru\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"52\",\"en\":\"Mexico\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"53\",\"en\":\"Cuba\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"54\",\"en\":\"Argentina\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"55\",\"en\":\"Brazill\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"56\",\"en\":\"Chile\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"57\",\"en\":\"Colombia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"58\",\"en\":\"Venezuela\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"60\",\"en\":\"Malaysia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"61\",\"en\":\"Australia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"62\",\"en\":\"Indonesia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"63\",\"en\":\"Philippines\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"64\",\"en\":\"NewZealand\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"65\",\"en\":\"Singapore\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"66\",\"en\":\"Thailand\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"81\",\"en\":\"Japan\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"82\",\"en\":\"Korea\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"84\",\"en\":\"Vietnam\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"86\",\"en\":\"China\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"90\",\"en\":\"Turkey\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"91\",\"en\":\"Indea\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"92\",\"en\":\"Pakistan\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"93\",\"en\":\"Italy\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"93\",\"en\":\"Afghanistan\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"94\",\"en\":\"SriLanka\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"94\",\"en\":\"Germany\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"95\",\"en\":\"Myanmar\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"98\",\"en\":\"Iran\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"212\",\"en\":\"Morocco\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"213\",\"en\":\"Algera\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"216\",\"en\":\"Tunisia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"218\",\"en\":\"Libya\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"220\",\"en\":\"Gambia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"221\",\"en\":\"Senegal\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"222\",\"en\":\"Mauritania\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"223\",\"en\":\"Mali\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"224\",\"en\":\"Guinea\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"225\",\"en\":\"Cote divoire\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"226\",\"en\":\"Burkina Faso\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"227\",\"en\":\"Niger\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"228\",\"en\":\"Togo\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"229\",\"en\":\"Benin\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"230\",\"en\":\"Mauritius\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"231\",\"en\":\"Liberia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"232\",\"en\":\"Sierra Leone\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"233\",\"en\":\"Ghana\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"234\",\"en\":\"Nigeria\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"235\",\"en\":\"Chad\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"236\",\"en\":\"Central African Republic\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"237\",\"en\":\"Cameroon\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"238\",\"en\":\"Cape Verde\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"239\",\"en\":\"Sao Tome and Principe\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"240\",\"en\":\"Guinea\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"241\",\"en\":\"Gabon\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"242\",\"en\":\"Republic of the Congo\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"243\",\"en\":\"Democratic Republic of the Congo\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"244\",\"en\":\"Angola\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"247\",\"en\":\"Ascension\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"248\",\"en\":\"Seychelles\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"249\",\"en\":\"Sudan\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"250\",\"en\":\"Rwanda\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"251\",\"en\":\"Ethiopia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"253\",\"en\":\"Djibouti\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"254\",\"en\":\"Kenya\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"255\",\"en\":\"Tanzania\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"256\",\"en\":\"Uganda\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"257\",\"en\":\"Burundi\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"258\",\"en\":\"Mozambique\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"260\",\"en\":\"Zambia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"261\",\"en\":\"Madagascar\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"262\",\"en\":\"Reunion\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"262\",\"en\":\"Mayotte\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"263\",\"en\":\"Zimbabwe\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"264\",\"en\":\"Namibia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"265\",\"en\":\"Malawi\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"266\",\"en\":\"Lesotho\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"267\",\"en\":\"Botwana\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"268\",\"en\":\"Swaziland\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"269\",\"en\":\"Comoros\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"297\",\"en\":\"Aruba\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"298\",\"en\":\"Faroe Islands\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"299\",\"en\":\"Greenland\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"350\",\"en\":\"Gibraltar\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"351\",\"en\":\"Portugal\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"352\",\"en\":\"Luxembourg\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"353\",\"en\":\"Ireland\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"354\",\"en\":\"Iceland\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"355\",\"en\":\"Albania\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"356\",\"en\":\"Malta\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"357\",\"en\":\"Cyprus\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"358\",\"en\":\"Finland\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"359\",\"en\":\"Bulgaria\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"370\",\"en\":\"Lithuania\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"371\",\"en\":\"Latvia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"372\",\"en\":\"Estonia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"373\",\"en\":\"Moldova\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"374\",\"en\":\"Armenia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"375\",\"en\":\"Belarus\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"376\",\"en\":\"Andorra\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"377\",\"en\":\"Monaco\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"378\",\"en\":\"San Marino\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"380\",\"en\":\"Ukraine\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"381\",\"en\":\"Serbia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"382\",\"en\":\"Montenegro\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"383\",\"en\":\"Kosovo\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"385\",\"en\":\"Croatia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"386\",\"en\":\"Slovenia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"387\",\"en\":\"Bosnia and Herzegovina\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"389\",\"en\":\"Macedonia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"420\",\"en\":\"Czech Republic\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"421\",\"en\":\"Slovakia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"423\",\"en\":\"Liechtenstein\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"501\",\"en\":\"Belize\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"502\",\"en\":\"Guatemala\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"503\",\"en\":\"EISalvador\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"504\",\"en\":\"Honduras\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"505\",\"en\":\"Nicaragua\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"506\",\"en\":\"Costa Rica\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"507\",\"en\":\"Panama\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"509\",\"en\":\"Haiti\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"590\",\"en\":\"Guadeloupe\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"591\",\"en\":\"Bolivia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"592\",\"en\":\"Guyana\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"593\",\"en\":\"Ecuador\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"594\",\"en\":\"French Guiana\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"595\",\"en\":\"Paraguay\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"596\",\"en\":\"Martinique\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"597\",\"en\":\"Suriname\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"598\",\"en\":\"Uruguay\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"599\",\"en\":\"Netherlands Antillse\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"670\",\"en\":\"Timor Leste\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"673\",\"en\":\"Brunei\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"675\",\"en\":\"Papua New Guinea\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"676\",\"en\":\"Tonga\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"678\",\"en\":\"Vanuatu\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"679\",\"en\":\"Fiji\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"682\",\"en\":\"Cook Islands\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"684\",\"en\":\"Samoa Eastern\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"685\",\"en\":\"Samoa Western\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"687\",\"en\":\"New Caledonia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"689\",\"en\":\"French Polynesia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"852\",\"en\":\"Hong Kong\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"853\",\"en\":\"Macao\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"855\",\"en\":\"Cambodia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"856\",\"en\":\"Laos\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"880\",\"en\":\"Bangladesh\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"886\",\"en\":\"Taiwan\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"960\",\"en\":\"Maldives\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"961\",\"en\":\"Lebanon\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"962\",\"en\":\"Jordan\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"963\",\"en\":\"Syria\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"964\",\"en\":\"Iraq\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"965\",\"en\":\"Kuwait\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"966\",\"en\":\"Saudi Arabia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"967\",\"en\":\"Yemen\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"968\",\"en\":\"Oman\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"970\",\"en\":\"Palestinian\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"971\",\"en\":\"United Arab Emirates\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"972\",\"en\":\"Israel\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"973\",\"en\":\"Bahrain\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"974\",\"en\":\"Qotar\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"975\",\"en\":\"Bhutan\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"976\",\"en\":\"Mongolia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"977\",\"en\":\"Nepal\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"992\",\"en\":\"Tajikistan\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"993\",\"en\":\"Turkmenistan\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"994\",\"en\":\"Azerbaijan\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"995\",\"en\":\"Georgia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"996\",\"en\":\"Kyrgyzstan\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"998\",\"en\":\"Uzbekistan\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1242\",\"en\":\"Bahamas\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1246\",\"en\":\"Barbados\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1264\",\"en\":\"Anguilla\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1268\",\"en\":\"Antigua and Barbuda\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1340\",\"en\":\"Virgin Islands\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1345\",\"en\":\"Cayman Islands\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1441\",\"en\":\"Bermuda\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1473\",\"en\":\"Grenada\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1649\",\"en\":\"Turks and Caicos Islands\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1664\",\"en\":\"Montserrat\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1671\",\"en\":\"Guam\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1758\",\"en\":\"St.Lucia\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1767\",\"en\":\"Dominica\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1784\",\"en\":\"St.Vincent\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1809\",\"en\":\"Dominican Republic\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1868\",\"en\":\"Trinidad and Tobago\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1869\",\"en\":\"St Kitts and Nevis\",\"cn\":\"Test Data"},\n" +
            "{\"prefix\":\"1876\",\"en\":\"Jamaica\",\"cn\":\"Test Data"}]";
    private boolean onlyChina = false;

    public PhoneCodePicker(@NonNull Activity activity) {
        super(activity);
    }

    public PhoneCodePicker(@NonNull Activity activity, @StyleRes int themeResId) {
        super(activity, themeResId);
    }

    public void setOnlyChina(boolean onlyChina) {
        this.onlyChina = onlyChina;
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
        PhoneCodeEntity entity = new PhoneCodeEntity();
        entity.setCode(code);
        super.setDefaultValue(entity);
    }

    public void setDefaultValueByName(String name) {
        PhoneCodeEntity entity = new PhoneCodeEntity();
        entity.setName(name);
        super.setDefaultValue(entity);
    }

    public void setDefaultValueByEnglish(String english) {
        PhoneCodeEntity entity = new PhoneCodeEntity();
        entity.setEnglish(english);
        super.setDefaultValue(entity);
    }

    @Override
    protected List<?> provideData() {
        List<PhoneCodeEntity> data = new ArrayList<>();
        if (onlyChina) {
            PhoneCodeEntity china = new PhoneCodeEntity();
            china.setCode("+86");
            china.setName("Test Data");
            china.setEnglish("Chinese Mainland");
            data.add(china);
            PhoneCodeEntity hongKong = new PhoneCodeEntity();
            hongKong.setCode("+852");
            hongKong.setName("Test Data");
            hongKong.setEnglish("Hong Kong");
            data.add(hongKong);
            PhoneCodeEntity macao = new PhoneCodeEntity();
            macao.setCode("+853");
            macao.setName("Test Data");
            macao.setEnglish("Macao");
            data.add(macao);
            PhoneCodeEntity taiwan = new PhoneCodeEntity();
            taiwan.setCode("+886");
            taiwan.setName("Test Data");
            taiwan.setEnglish("Taiwan");
            data.add(taiwan);
        } else {
            try {
                JSONArray jsonArray = new JSONArray(JSON);
                for (int i = 0, n = jsonArray.length(); i < n; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    PhoneCodeEntity entity = new PhoneCodeEntity();
                    entity.setCode("+" + jsonObject.getString("prefix"));
                    entity.setName(jsonObject.getString("cn"));
                    entity.setEnglish(jsonObject.getString("en"));
                    data.add(entity);
                }
            } catch (JSONException e) {
                DialogLog.print(e);
            }
        }
        return data;
    }

}
