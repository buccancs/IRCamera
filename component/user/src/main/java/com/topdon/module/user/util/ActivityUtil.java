package com.topdon.module.user.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.topdon.lms.sdk.utils.NetworkUtil;
import com.topdon.lms.sdk.weiget.TToast;
import com.topdon.module.user.R;

    /**
     * ActivityUtil class.
     *
     * Provides activityutil functionality.
     */
    public class ActivityUtil {
        /**
     * [Chinese text]
     * @param mContext [Chinese text]
     */
    public static void goSystemCustomer(Context mContext) {
        Log.w("bcf", "[Chinese text]point[Chinese text]event");
        String url = "https:// www.topdon.cc/tc-chat";
        goSystemBrowser(mContext, url);
    }

        /**
     * [Chinese text]
     * @param mContext [Chinese text]
     */
    public static void goSystemBrowser(Context mContext, String url) {
        Log.w("bcf", "goSystemBrowser");
        if (!NetworkUtil.isConnected(mContext)) {
            TToast.shortToast(mContext, R.string.lms_setting_http_error);
            return;
        }
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
