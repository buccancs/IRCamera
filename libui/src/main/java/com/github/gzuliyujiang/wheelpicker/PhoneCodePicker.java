 * Copyright (c) 2016-present <1032694760@qq.com>
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN AS IS BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.

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

 * @author 1032694760@qq.com
 * @since 2019/5/10 16:44
@SuppressWarnings("unused")
public class PhoneCodePicker extends OptionPicker {
    /**
     * Method description.
     */
    public static String JSON = "[{\"prefix\":\"1\",\"en\":\"USA\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1\",\"en\":\"PuertoRico\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1\",\"en\":\"Canada\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"7\",\"en\":\"Russia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"7\",\"en\":\"Kazeakhstan\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"20\",\"en\":\"Egypt\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"27\",\"en\":\"South Africa\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"30\",\"en\":\"Greece\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"31\",\"en\":\"Netherlands\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"32\",\"en\":\"Belgium\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"33\",\"en\":\"France\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"34\",\"en\":\"Spain\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"36\",\"en\":\"Hungary\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"40\",\"en\":\"Romania\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"41\",\"en\":\"Switzerland\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"43\",\"en\":\"Austria\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"44\",\"en\":\"United Kingdom\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"44\",\"en\":\"Jersey\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"44\",\"en\":\"Isle of Man\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"44\",\"en\":\"Guernsey\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"45\",\"en\":\"Denmark\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"46\",\"en\":\"Sweden\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"47\",\"en\":\"Norway\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"48\",\"en\":\"Poland\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"51\",\"en\":\"Peru\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"52\",\"en\":\"Mexico\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"53\",\"en\":\"Cuba\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"54\",\"en\":\"Argentina\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"55\",\"en\":\"Brazill\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"56\",\"en\":\"Chile\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"57\",\"en\":\"Colombia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"58\",\"en\":\"Venezuela\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"60\",\"en\":\"Malaysia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"61\",\"en\":\"Australia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"62\",\"en\":\"Indonesia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"63\",\"en\":\"Philippines\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"64\",\"en\":\"NewZealand\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"65\",\"en\":\"Singapore\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"66\",\"en\":\"Thailand\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"81\",\"en\":\"Japan\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"82\",\"en\":\"Korea\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"84\",\"en\":\"Vietnam\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"86\",\"en\":\"China\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"90\",\"en\":\"Turkey\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"91\",\"en\":\"Indea\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"92\",\"en\":\"Pakistan\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"93\",\"en\":\"Italy\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"93\",\"en\":\"Afghanistan\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"94\",\"en\":\"SriLanka\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"94\",\"en\":\"Germany\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"95\",\"en\":\"Myanmar\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"98\",\"en\":\"Iran\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"212\",\"en\":\"Morocco\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"213\",\"en\":\"Algera\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"216\",\"en\":\"Tunisia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"218\",\"en\":\"Libya\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"220\",\"en\":\"Gambia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"221\",\"en\":\"Senegal\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"222\",\"en\":\"Mauritania\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"223\",\"en\":\"Mali\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"224\",\"en\":\"Guinea\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"225\",\"en\":\"Cote divoire\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"226\",\"en\":\"Burkina Faso\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"227\",\"en\":\"Niger\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"228\",\"en\":\"Togo\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"229\",\"en\":\"Benin\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"230\",\"en\":\"Mauritius\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"231\",\"en\":\"Liberia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"232\",\"en\":\"Sierra Leone\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"233\",\"en\":\"Ghana\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"234\",\"en\":\"Nigeria\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"235\",\"en\":\"Chad\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"236\",\"en\":\"Central African Republic\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"237\",\"en\":\"Cameroon\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"238\",\"en\":\"Cape Verde\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"239\",\"en\":\"Sao Tome and Principe\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"240\",\"en\":\"Guinea\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"241\",\"en\":\"Gabon\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"242\",\"en\":\"Republic of the Congo\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"243\",\"en\":\"Democratic Republic of the Congo\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"244\",\"en\":\"Angola\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"247\",\"en\":\"Ascension\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"248\",\"en\":\"Seychelles\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"249\",\"en\":\"Sudan\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"250\",\"en\":\"Rwanda\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"251\",\"en\":\"Ethiopia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"253\",\"en\":\"Djibouti\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"254\",\"en\":\"Kenya\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"255\",\"en\":\"Tanzania\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"256\",\"en\":\"Uganda\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"257\",\"en\":\"Burundi\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"258\",\"en\":\"Mozambique\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"260\",\"en\":\"Zambia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"261\",\"en\":\"Madagascar\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"262\",\"en\":\"Reunion\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"262\",\"en\":\"Mayotte\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"263\",\"en\":\"Zimbabwe\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"264\",\"en\":\"Namibia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"265\",\"en\":\"Malawi\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"266\",\"en\":\"Lesotho\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"267\",\"en\":\"Botwana\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"268\",\"en\":\"Swaziland\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"269\",\"en\":\"Comoros\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"297\",\"en\":\"Aruba\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"298\",\"en\":\"Faroe Islands\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"299\",\"en\":\"Greenland\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"350\",\"en\":\"Gibraltar\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"351\",\"en\":\"Portugal\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"352\",\"en\":\"Luxembourg\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"353\",\"en\":\"Ireland\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"354\",\"en\":\"Iceland\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"355\",\"en\":\"Albania\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"356\",\"en\":\"Malta\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"357\",\"en\":\"Cyprus\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"358\",\"en\":\"Finland\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"359\",\"en\":\"Bulgaria\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"370\",\"en\":\"Lithuania\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"371\",\"en\":\"Latvia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"372\",\"en\":\"Estonia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"373\",\"en\":\"Moldova\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"374\",\"en\":\"Armenia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"375\",\"en\":\"Belarus\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"376\",\"en\":\"Andorra\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"377\",\"en\":\"Monaco\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"378\",\"en\":\"San Marino\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"380\",\"en\":\"Ukraine\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"381\",\"en\":\"Serbia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"382\",\"en\":\"Montenegro\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"383\",\"en\":\"Kosovo\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"385\",\"en\":\"Croatia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"386\",\"en\":\"Slovenia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"387\",\"en\":\"Bosnia and Herzegovina\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"389\",\"en\":\"Macedonia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"420\",\"en\":\"Czech Republic\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"421\",\"en\":\"Slovakia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"423\",\"en\":\"Liechtenstein\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"501\",\"en\":\"Belize\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"502\",\"en\":\"Guatemala\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"503\",\"en\":\"EISalvador\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"504\",\"en\":\"Honduras\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"505\",\"en\":\"Nicaragua\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"506\",\"en\":\"Costa Rica\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"507\",\"en\":\"Panama\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"509\",\"en\":\"Haiti\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"590\",\"en\":\"Guadeloupe\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"591\",\"en\":\"Bolivia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"592\",\"en\":\"Guyana\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"593\",\"en\":\"Ecuador\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"594\",\"en\":\"French Guiana\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"595\",\"en\":\"Paraguay\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"596\",\"en\":\"Martinique\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"597\",\"en\":\"Suriname\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"598\",\"en\":\"Uruguay\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"599\",\"en\":\"Netherlands Antillse\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"670\",\"en\":\"Timor Leste\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"673\",\"en\":\"Brunei\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"675\",\"en\":\"Papua New Guinea\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"676\",\"en\":\"Tonga\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"678\",\"en\":\"Vanuatu\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"679\",\"en\":\"Fiji\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"682\",\"en\":\"Cook Islands\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"684\",\"en\":\"Samoa Eastern\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"685\",\"en\":\"Samoa Western\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"687\",\"en\":\"New Caledonia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"689\",\"en\":\"French Polynesia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"852\",\"en\":\"Hong Kong\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"853\",\"en\":\"Macao\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"855\",\"en\":\"Cambodia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"856\",\"en\":\"Laos\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"880\",\"en\":\"Bangladesh\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"886\",\"en\":\"Taiwan\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"960\",\"en\":\"Maldives\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"961\",\"en\":\"Lebanon\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"962\",\"en\":\"Jordan\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"963\",\"en\":\"Syria\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"964\",\"en\":\"Iraq\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"965\",\"en\":\"Kuwait\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"966\",\"en\":\"Saudi Arabia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"967\",\"en\":\"Yemen\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"968\",\"en\":\"Oman\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"970\",\"en\":\"Palestinian\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"971\",\"en\":\"United Arab Emirates\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"972\",\"en\":\"Israel\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"973\",\"en\":\"Bahrain\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"974\",\"en\":\"Qotar\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"975\",\"en\":\"Bhutan\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"976\",\"en\":\"Mongolia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"977\",\"en\":\"Nepal\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"992\",\"en\":\"Tajikistan\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"993\",\"en\":\"Turkmenistan\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"994\",\"en\":\"Azerbaijan\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"995\",\"en\":\"Georgia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"996\",\"en\":\"Kyrgyzstan\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"998\",\"en\":\"Uzbekistan\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1242\",\"en\":\"Bahamas\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1246\",\"en\":\"Barbados\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1264\",\"en\":\"Anguilla\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1268\",\"en\":\"Antigua and Barbuda\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1340\",\"en\":\"Virgin Islands\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1345\",\"en\":\"Cayman Islands\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1441\",\"en\":\"Bermuda\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1473\",\"en\":\"Grenada\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1649\",\"en\":\"Turks and Caicos Islands\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1664\",\"en\":\"Montserrat\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1671\",\"en\":\"Guam\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1758\",\"en\":\"St.Lucia\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1767\",\"en\":\"Dominica\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1784\",\"en\":\"St.Vincent\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1809\",\"en\":\"Dominican Republic\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1868\",\"en\":\"Trinidad and Tobago\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1869\",\"en\":\"St Kitts and Nevis\",\"cn\":\"\"},\n" +
            "{\"prefix\":\"1876\",\"en\":\"Jamaica\",\"cn\":\"\"}]";
    /**
     * Private method description.
     */
    private boolean onlyChina = false;

    /**
     * Method description.
     */
    public PhoneCodePicker(@NonNull Activity activity) {
        super(activity);
    }

    /**
     * Method description.
     */
    public PhoneCodePicker(@NonNull Activity activity, @StyleRes int themeResId) {
        super(activity, themeResId);
    }

    /**
     * Method description.
     */
    public void setOnlyChina(boolean onlyChina) {
        this.onlyChina = onlyChina;
        setData(provideData());
    }

    @Override
    /**
     * Method description.
     */
    public void setDefaultValue(Object item) {
        if (item instanceof String) {
            setDefaultValueByName(item.toString());
        } else {
            super.setDefaultValue(item);
        }
    }

    /**
     * Method description.
     */
    public void setDefaultValueByCode(String code) {
        PhoneCodeEntity entity = new PhoneCodeEntity();
        entity.setCode(code);
        super.setDefaultValue(entity);
    }

    /**
     * Method description.
     */
    public void setDefaultValueByName(String name) {
        PhoneCodeEntity entity = new PhoneCodeEntity();
        entity.setName(name);
        super.setDefaultValue(entity);
    }

    /**
     * Method description.
     */
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
            china.setName("+86");
            china.setEnglish("Chinese Mainland");
            data.add(china);
            PhoneCodeEntity hongKong = new PhoneCodeEntity();
            hongKong.setCode("+852");
            hongKong.setName("+852");
            hongKong.setEnglish("Hong Kong");
            data.add(hongKong);
            PhoneCodeEntity macao = new PhoneCodeEntity();
            macao.setCode("+853");
            macao.setName("+853");
            macao.setEnglish("Macao");
            data.add(macao);
            PhoneCodeEntity taiwan = new PhoneCodeEntity();
            taiwan.setCode("+886");
            taiwan.setName("+886");
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
