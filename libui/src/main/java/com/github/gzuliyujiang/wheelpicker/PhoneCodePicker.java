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
import androidx.annotation.StyleRes;

import com.github.gzuliyujiang.dialog.DialogLog;
import com.github.gzuliyujiang.wheelpicker.entity.PhoneCodeEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * [Chinese text]
 *
 * @author [Chinese text](1032694760@qq.com)
 * @since 2019/5/10 16:44
 */
@SuppressWarnings("unused")
    /**
     * PhoneCodePicker class.
     *
     * Provides phonecodepicker functionality.
     */
    public class PhoneCodePicker extends OptionPicker {
    public static String JSON = "[{\"prefix\":\"1\",\"en\":\"USA\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1\",\"en\":\"PuertoRico\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1\",\"en\":\"Canada\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"7\",\"en\":\"Russia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"7\",\"en\":\"Kazeakhstan\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"20\",\"en\":\"Egypt\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"27\",\"en\":\"South Africa\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"30\",\"en\":\"Greece\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"31\",\"en\":\"Netherlands\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"32\",\"en\":\"Belgium\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"33\",\"en\":\"France\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"34\",\"en\":\"Spain\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"36\",\"en\":\"Hungary\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"40\",\"en\":\"Romania\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"41\",\"en\":\"Switzerland\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"43\",\"en\":\"Austria\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"44\",\"en\":\"United Kingdom\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"44\",\"en\":\"Jersey\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"44\",\"en\":\"Isle of Man\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"44\",\"en\":\"Guernsey\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"45\",\"en\":\"Denmark\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"46\",\"en\":\"Sweden\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"47\",\"en\":\"Norway\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"48\",\"en\":\"Poland\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"51\",\"en\":\"Peru\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"52\",\"en\":\"Mexico\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"53\",\"en\":\"Cuba\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"54\",\"en\":\"Argentina\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"55\",\"en\":\"Brazill\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"56\",\"en\":\"Chile\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"57\",\"en\":\"Colombia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"58\",\"en\":\"Venezuela\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"60\",\"en\":\"Malaysia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"61\",\"en\":\"Australia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"62\",\"en\":\"Indonesia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"63\",\"en\":\"Philippines\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"64\",\"en\":\"NewZealand\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"65\",\"en\":\"Singapore\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"66\",\"en\":\"Thailand\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"81\",\"en\":\"Japan\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"82\",\"en\":\"Korea\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"84\",\"en\":\"Vietnam\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"86\",\"en\":\"China\",\"cn\":\"in progress[Chinese text]\"},\n" +
            "{\"prefix\":\"90\",\"en\":\"Turkey\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"91\",\"en\":\"Indea\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"92\",\"en\":\"Pakistan\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"93\",\"en\":\"Italy\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"93\",\"en\":\"Afghanistan\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"94\",\"en\":\"SriLanka\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"94\",\"en\":\"Germany\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"95\",\"en\":\"Myanmar\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"98\",\"en\":\"Iran\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"212\",\"en\":\"Morocco\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"213\",\"en\":\"Algera\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"216\",\"en\":\"Tunisia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"218\",\"en\":\"Libya\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"220\",\"en\":\"Gambia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"221\",\"en\":\"Senegal\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"222\",\"en\":\"Mauritania\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"223\",\"en\":\"Mali\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"224\",\"en\":\"Guinea\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"225\",\"en\":\"Cote divoire\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"226\",\"en\":\"Burkina Faso\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"227\",\"en\":\"Niger\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"228\",\"en\":\"Togo\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"229\",\"en\":\"Benin\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"230\",\"en\":\"Mauritius\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"231\",\"en\":\"Liberia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"232\",\"en\":\"Sierra Leone\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"233\",\"en\":\"Ghana\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"234\",\"en\":\"Nigeria\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"235\",\"en\":\"Chad\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"236\",\"en\":\"Central African Republic\",\"cn\":\"in progress[Chinese text]\"},\n" +
            "{\"prefix\":\"237\",\"en\":\"Cameroon\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"238\",\"en\":\"Cape Verde\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"239\",\"en\":\"Sao Tome and Principe\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"240\",\"en\":\"Guinea\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"241\",\"en\":\"Gabon\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"242\",\"en\":\"Republic of the Congo\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"243\",\"en\":\"Democratic Republic of the Congo\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"244\",\"en\":\"Angola\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"247\",\"en\":\"Ascension\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"248\",\"en\":\"Seychelles\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"249\",\"en\":\"Sudan\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"250\",\"en\":\"Rwanda\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"251\",\"en\":\"Ethiopia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"253\",\"en\":\"Djibouti\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"254\",\"en\":\"Kenya\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"255\",\"en\":\"Tanzania\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"256\",\"en\":\"Uganda\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"257\",\"en\":\"Burundi\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"258\",\"en\":\"Mozambique\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"260\",\"en\":\"Zambia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"261\",\"en\":\"Madagascar\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"262\",\"en\":\"Reunion\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"262\",\"en\":\"Mayotte\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"263\",\"en\":\"Zimbabwe\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"264\",\"en\":\"Namibia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"265\",\"en\":\"Malawi\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"266\",\"en\":\"Lesotho\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"267\",\"en\":\"Botwana\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"268\",\"en\":\"Swaziland\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"269\",\"en\":\"Comoros\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"297\",\"en\":\"Aruba\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"298\",\"en\":\"Faroe Islands\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"299\",\"en\":\"Greenland\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"350\",\"en\":\"Gibraltar\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"351\",\"en\":\"Portugal\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"352\",\"en\":\"Luxembourg\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"353\",\"en\":\"Ireland\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"354\",\"en\":\"Iceland\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"355\",\"en\":\"Albania\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"356\",\"en\":\"Malta\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"357\",\"en\":\"Cyprus\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"358\",\"en\":\"Finland\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"359\",\"en\":\"Bulgaria\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"370\",\"en\":\"Lithuania\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"371\",\"en\":\"Latvia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"372\",\"en\":\"Estonia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"373\",\"en\":\"Moldova\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"374\",\"en\":\"Armenia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"375\",\"en\":\"Belarus\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"376\",\"en\":\"Andorra\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"377\",\"en\":\"Monaco\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"378\",\"en\":\"San Marino\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"380\",\"en\":\"Ukraine\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"381\",\"en\":\"Serbia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"382\",\"en\":\"Montenegro\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"383\",\"en\":\"Kosovo\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"385\",\"en\":\"Croatia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"386\",\"en\":\"Slovenia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"387\",\"en\":\"Bosnia and Herzegovina\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"389\",\"en\":\"Macedonia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"420\",\"en\":\"Czech Republic\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"421\",\"en\":\"Slovakia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"423\",\"en\":\"Liechtenstein\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"501\",\"en\":\"Belize\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"502\",\"en\":\"Guatemala\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"503\",\"en\":\"EISalvador\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"504\",\"en\":\"Honduras\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"505\",\"en\":\"Nicaragua\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"506\",\"en\":\"Costa Rica\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"507\",\"en\":\"Panama\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"509\",\"en\":\"Haiti\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"590\",\"en\":\"Guadeloupe\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"591\",\"en\":\"Bolivia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"592\",\"en\":\"Guyana\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"593\",\"en\":\"Ecuador\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"594\",\"en\":\"French Guiana\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"595\",\"en\":\"Paraguay\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"596\",\"en\":\"Martinique\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"597\",\"en\":\"Suriname\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"598\",\"en\":\"Uruguay\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"599\",\"en\":\"Netherlands Antillse\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"670\",\"en\":\"Timor Leste\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"673\",\"en\":\"Brunei\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"675\",\"en\":\"Papua New Guinea\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"676\",\"en\":\"Tonga\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"678\",\"en\":\"Vanuatu\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"679\",\"en\":\"Fiji\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"682\",\"en\":\"Cook Islands\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"684\",\"en\":\"Samoa Eastern\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"685\",\"en\":\"Samoa Western\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"687\",\"en\":\"New Caledonia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"689\",\"en\":\"French Polynesia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"852\",\"en\":\"Hong Kong\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"853\",\"en\":\"Macao\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"855\",\"en\":\"Cambodia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"856\",\"en\":\"Laos\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"880\",\"en\":\"Bangladesh\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"886\",\"en\":\"Taiwan\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"960\",\"en\":\"Maldives\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"961\",\"en\":\"Lebanon\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"962\",\"en\":\"Jordan\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"963\",\"en\":\"Syria\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"964\",\"en\":\"Iraq\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"965\",\"en\":\"Kuwait\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"966\",\"en\":\"Saudi Arabia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"967\",\"en\":\"Yemen\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"968\",\"en\":\"Oman\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"970\",\"en\":\"Palestinian\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"971\",\"en\":\"United Arab Emirates\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"972\",\"en\":\"Israel\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"973\",\"en\":\"Bahrain\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"974\",\"en\":\"Qotar\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"975\",\"en\":\"Bhutan\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"976\",\"en\":\"Mongolia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"977\",\"en\":\"Nepal\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"992\",\"en\":\"Tajikistan\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"993\",\"en\":\"Turkmenistan\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"994\",\"en\":\"Azerbaijan\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"995\",\"en\":\"Georgia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"996\",\"en\":\"Kyrgyzstan\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"998\",\"en\":\"Uzbekistan\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1242\",\"en\":\"Bahamas\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1246\",\"en\":\"Barbados\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1264\",\"en\":\"Anguilla\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1268\",\"en\":\"Antigua and Barbuda\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1340\",\"en\":\"Virgin Islands\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1345\",\"en\":\"Cayman Islands\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1441\",\"en\":\"Bermuda\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1473\",\"en\":\"Grenada\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1649\",\"en\":\"Turks and Caicos Islands\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1664\",\"en\":\"Montserrat\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1671\",\"en\":\"Guam\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1758\",\"en\":\"St.Lucia\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1767\",\"en\":\"Dominica\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1784\",\"en\":\"St.Vincent\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1809\",\"en\":\"Dominican Republic\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1868\",\"en\":\"Trinidad and Tobago\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1869\",\"en\":\"St Kitts and Nevis\",\"cn\":\"[Chinese text]\"},\n" +
            "{\"prefix\":\"1876\",\"en\":\"Jamaica\",\"cn\":\"[Chinese text]\"}]";
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
            china.setName("in progress[Chinese text]+86");
            china.setEnglish("Chinese Mainland");
            data.add(china);
            PhoneCodeEntity hongKong = new PhoneCodeEntity();
            hongKong.setCode("+852");
            hongKong.setName("[Chinese text]+852");
            hongKong.setEnglish("Hong Kong");
            data.add(hongKong);
            PhoneCodeEntity macao = new PhoneCodeEntity();
            macao.setCode("+853");
            macao.setName("[Chinese text]+853");
            macao.setEnglish("Macao");
            data.add(macao);
            PhoneCodeEntity taiwan = new PhoneCodeEntity();
            taiwan.setCode("+886");
            taiwan.setName("[Chinese text]+886");
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
