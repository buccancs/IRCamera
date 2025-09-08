package com.topdon.commons.util;

import android.util.Log;

import com.elvishew.xlog.XLog;
import com.topdon.ble.BuildConfig;

 * LLog
 * @author chuanfeng.bi
 * @date 2021/11/16 16:34
public class LLog {
    /**
     * Private method description.
     */
    private static boolean isDebug = BuildConfig.DEBUG;


    /**
     * Method description.
     */
    public static void d(String tag, String value) {
        XLog.tag(tag).d(value);
//        if (isDebug) {
//            Log.d(tag, value);
//        }
    }

    /**
     * Method description.
     */
    public static void i(String tag, String value) {
        XLog.tag(tag).i(value);
//        if (isDebug) {
//            Log.i(tag, value);
//        }
    }

    /**
     * Method description.
     */
    public static void w(String tag, String value) {
        XLog.tag(tag).w(value);
//        if (isDebug) {
//            Log.w(tag, value);
//        }
    }

    /**
     * Method description.
     */
    public static void e(String tag, String value) {
        XLog.tag(tag).e(value);
//        if (isDebug) {
//            Log.e(tag, value);
//        }
    }


    /**
     * Method description.
     */
    public final static int MAX_LENGTH = 2000;

     * @param tag
     * @param msg
    public static void LogMaxPrint(String tag, String msg) {
        if (msg.length() > MAX_LENGTH) {
            int length = MAX_LENGTH + 1;
            String remain = msg;
            int index = 0;
            while (length > MAX_LENGTH) {
                index++;
                Log.v(tag + "[" + index + "]", " \n" + remain.substring(0, MAX_LENGTH));
                remain = remain.substring(MAX_LENGTH);
                length = remain.length();
            }
            if (length <= MAX_LENGTH) {
                index++;
                Log.v(tag + "[" + index + "]", " \n" + remain);
            }
        } else {
            Log.v(tag, msg);
        }
    }

}
