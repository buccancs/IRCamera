package com.topdon.commons.util;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;


 * @Desc
 * @ClassName UMUtils
 * @Email 616862466@qq.com
 * @Author
 * @Date 2023/3/28 13:53

public class UMUtils {

    public static void onEvent(Context mContext, String var1, String var2) {
        MobclickAgent.onEvent(mContext, var1, var2);
    }

    public static void onEvent(Context mContext, String var1) {
        MobclickAgent.onEvent(mContext, var1);
    }

}
