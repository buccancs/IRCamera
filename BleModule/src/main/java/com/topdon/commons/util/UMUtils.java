package com.topdon.commons.util;

import android.content.Context;

/**
 * @Desc 友盟埋点工具类 - Safe implementation with optional UMeng dependency
 * @ClassName UMUtils
 * @Email 616862466@qq.com
 * @Author 子墨
 * @Date 2023/3/28 13:53
 */

public class UMUtils {

    public static void onEvent(Context mContext, String var1, String var2) {
        try {
            // Use reflection to avoid hard dependency on UMeng
            Class<?> mobclickAgent = Class.forName("com.umeng.analytics.MobclickAgent");
            mobclickAgent.getMethod("onEvent", Context.class, String.class, String.class)
                    .invoke(null, mContext, var1, var2);
        } catch (Exception e) {
            // UMeng not available - silently ignore
        }
    }

    public static void onEvent(Context mContext, String var1) {
        try {
            // Use reflection to avoid hard dependency on UMeng
            Class<?> mobclickAgent = Class.forName("com.umeng.analytics.MobclickAgent");
            mobclickAgent.getMethod("onEvent", Context.class, String.class)
                    .invoke(null, mContext, var1);
        } catch (Exception e) {
            // UMeng not available - silently ignore
        }
    }
}
