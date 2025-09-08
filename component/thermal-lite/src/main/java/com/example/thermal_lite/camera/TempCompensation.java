package com.example.thermal_lite.camera;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.energy.ac020library.IrcamEngine;
import com.energy.ac020library.bean.CommonParams;
import com.energy.ac020library.bean.IFileHandleCallback;
import com.energy.ac020library.bean.IrcmdError;
import com.energy.commoncomponent.Const;
import com.energy.commoncomponent.bean.DeviceType;
// Use existing utilities instead of missing commonlibrary utils
import com.infisense.usbir.utils.FileUtil;
import com.blankj.utilcode.util.SPUtils;
import com.energy.irutilslibrary.LibIRTemp;
import com.example.thermal_lite.util.CommonUtil;
import com.topdon.lib.core.BaseApplication;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

    /**
     * Tiny2c[Chinese text]temperature[Chinese text]
     */
    public class TempCompensation {
    private final String TAG = "TempCompensation";
    // [Chinese text]
    private final int HANDLER_KEY_INIT = 1000;
    // 1s[Chinese text]
    private final int HANDLER_KEY_1s = 1001;
    // [Chinese text](4s\6s[Chinese text])
    private final int HANDLER_KEY_AFTER = 1002;
    // [Chinese text]s
    private final int ALL_DURATION = 30;
    private HandlerThread handlerThread;
    private Handler handler;

    // NUC_T
    private short[] nucT;
    // NUC[Chinese text]
    private volatile int deltaNUC;
    // [Chinese text]
    private volatile int nucNew;
    // VTemp[Chinese text]
    private volatile int deltaVTemp;
    // VTemp[Chinese text]
    private volatile int vTempStart;
    // start[Chinese text]
    private volatile long startTime;

    // [Chinese text]
    private volatile boolean isCompensation = false;
    // [Chinese text]
    private volatile boolean isStart = false;

        /**
     * [Chinese text]y=-0.0705571*x2+14.72725379*x-30.49372144
     */
    public static String DEFAULT_PARAM1 = "-0.0705";
    public static String DEFAULT_PARAM2 = "14.7272";
    public static String DEFAULT_PARAM3 = "30.4937";
    public static final String KEY_PARAM1 = "KEY_PARAM1";
    public static final String KEY_PARAM2 = "KEY_PARAM2";
    public static final String KEY_PARAM3 = "KEY_PARAM3";
    private double param1 = -0.0705;
    private double param2 = 14.7272;
    private double param3 = 30.4937;

    private static TempCompensation mInstance;

    public static synchronized TempCompensation getInstance() {
        if (mInstance == null) {
            mInstance = new TempCompensation();
        }
        return mInstance;
    }

        /**
     * [Chinese text]nuc-t[Chinese text]
     */
    public void getNucTData() {

        if (DeviceIrcmdControlManager.getInstance().getIrcmdEngine() == null){
            return;
        }
        byte[] snData = new byte[64];
        DeviceIrcmdControlManager.getInstance().getIrcmdEngine().basicDeviceInfoGet(CommonParams.DeviceInfoType.TYPE_DEVICE_SN, snData);
        String sn = new String(snData).trim().replace("\0", "");
        File file = new File(BaseApplication.instance.getExternalCacheDir(), "NUC_T_HIGH_" + sn + ".bin");
        if (file.exists()) {
            Log.d(TAG, "File exists, reading from: " + file.getAbsolutePath());
            byte[] nucTableHighByte = FileUtil.readFile2BytesByStream(BaseApplication.instance.getApplicationContext(), file);
            nucT = byteToShort(nucTableHighByte);
            Log.d(TAG, "getNucTData int: " + Arrays.toString(nucT));
        } else {
            Log.d(TAG, "getNucTData path: " + file.getAbsolutePath());
            readFlashData(CommonParams.SdFilePath.DEFAULT_DATA_NUC_T_HIGH, file.getPath(),
                    progress -> Log.d(TAG, "getNucTData readFlashData DEFAULT_DATA_NUC_T_HIGH : " + progress));
            byte[] nucTableHighByte = FileUtil.readFile2BytesByStream(BaseApplication.instance.getApplicationContext(), file);
            Log.d(TAG, "getNucTData byte: " + nucTableHighByte.length + "---" + Arrays.toString(nucTableHighByte));
            nucT = byteToShort(nucTableHighByte);
            Log.d(TAG, "getNucTData int: " + Arrays.toString(nucT));
        }
    }

    /**
     * [Chinese text]FlashData
     *
     * @param sdFilePath    CommonParams.SdFilePath.DEFAULT_DATA_NUC_T_HIGH
     * @param localFilePath [Chinese text]--[Chinese text]
     */
    private void readFlashData(CommonParams.SdFilePath sdFilePath, String localFilePath,
                               IFileHandleCallback iFileHandleCallback) {
        IrcamEngine ircamEngine = CameraPreviewManager.getInstance().getIrcamEngine();
        if (ircamEngine == null) {
            return;
        }
        int result = 0;
        try {
            result = ircamEngine.advFileRead(sdFilePath, localFilePath, iFileHandleCallback);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, sdFilePath + "  advFileRead fail !");
        }
        if (result != 0) {
            Log.d(TAG, sdFilePath + "  advFileRead fail !");
        }
    }

        /**
     * [Chinese text]start
     */
    public void startTempCompensation() {
        if (Const.DEVICE_TYPE != DeviceType.DEVICE_TYPE_TC2C) {
            return;
        }
        if (handlerThread != null) {
            return;
        }

        // [Chinese text], [Chinese text]
        param1 = Double.parseDouble(SPUtils.getInstance().getString(
                KEY_PARAM1, DEFAULT_PARAM1));
        param2 = Double.parseDouble(SPUtils.getInstance().getString(
                KEY_PARAM2, DEFAULT_PARAM2));
        param3 = Double.parseDouble(SPUtils.getInstance().getString(
                KEY_PARAM3, DEFAULT_PARAM3));
        Log.d(TAG, "param1:" + param1 + "--param2:" + param2 + "--param3:" + param3);

        handlerThread = new HandlerThread("TempCompensation");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                try {
                    if (HANDLER_KEY_INIT == msg.what) {
                        Log.d(TAG, "HANDLER_KEY_INIT");
                        isStart = true;
                        // [Chinese text]
                        if (DeviceIrcmdControlManager.getInstance().getIrcmdEngine()!=null){
                            IrcmdError basicAutoFFCStatusSet = DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
                                    .basicAutoFFCStatusSet(CommonParams.AutoFFCStatus.AUTO_FFC_DISABLED);
                            Log.d(TAG, "basicAutoFFCStatusSet=" + basicAutoFFCStatusSet);
                        }
                        // [Chinese text]NUC-T
                        getNucTData();
                    } else if (HANDLER_KEY_1s == msg.what) {
                        // [Chinese text]
                        if (DeviceIrcmdControlManager.getInstance().getIrcmdEngine() != null){
                            IrcmdError nativeAdvManualFFCUpdateResult = DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
                                    .basicFFCUpdate();
                            Log.d(TAG, "nativeAdvManualFFCUpdateResult=" + nativeAdvManualFFCUpdateResult);
                            // [Chinese text]Vtemp_start
                            int[] nativeAdvDeviceRealtimeStatusGetValue = new int[1];
                            IrcmdError advDeviceRealtimeStatusGetResult = DeviceIrcmdControlManager.getInstance()
                                    .getIrcmdEngine()
                                    .advDeviceRealtimeStatusGet(CommonParams.RealtimeStatusType.ADV_IR_SENSOR_VTEMP,
                                            nativeAdvDeviceRealtimeStatusGetValue);
                            Log.d(TAG, "advDeviceRealtimeStatusGetResult=" + advDeviceRealtimeStatusGetResult);
                            // [Chinese text](1s[Chinese text])[Chinese text]Vtemp_start
                            vTempStart = nativeAdvDeviceRealtimeStatusGetValue[0];
                            Log.d(TAG, "Vtemp_start=" + vTempStart);
                            // [Chinese text]NUC_new=0[Chinese text];
                            nucNew = 0;
                            // start[Chinese text]
                            isCompensation = true;
                            startTime = System.currentTimeMillis();
                        }
                    } else if (HANDLER_KEY_AFTER == msg.what) {
                        if (DeviceIrcmdControlManager.getInstance().getIrcmdEngine() != null){
                            Log.d(TAG, "[Chinese text]");
                            // [Chinese text]
                            IrcmdError advManualFFCUpdateResult = DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
                                    .advManualFFCUpdate(CommonParams.FFCShutterBehaviorMode.ONLY_B_UPDATE);
                            Log.d(TAG, "advManualFFCUpdateResult=" + advManualFFCUpdateResult);
                            // [Chinese text]NUC_neW=DeltaNUC
                            nucNew = (int) (param1 * deltaVTemp * deltaVTemp + param2 * deltaVTemp - param3);
                            Log.d(TAG, "NUC_new=" + nucNew);

                            // 6s
                            handler.sendEmptyMessageDelayed(HANDLER_KEY_AFTER, 6000);
                        }
                    }
                }catch (Exception e){
                    Log.d(TAG, "temperature[Chinese text]=" + e.getMessage());
                }
            }
        };
        // init
        handler.sendEmptyMessage(HANDLER_KEY_INIT);
        // 1s
        handler.sendEmptyMessageDelayed(HANDLER_KEY_1s, 1000);
        // 4s
        handler.sendEmptyMessageDelayed(HANDLER_KEY_AFTER, 4000);
    }

        /**
     * [Chinese text]temperature
     * [Chinese text]temperature[Chinese text]temperature
     *
     * @param temp
     * @return
     */
    public float compensateTemp(float temp) {
        if (!isCompensation) {
            return temp;
        }
        return getNewTempValue(temp);
    }

        /**
     * [Chinese text]DeltaNUC[Chinese text]DeltaVTEMP[Chinese text]
     * [Chinese text]vtemp, [Chinese text]temperature[Chinese text], [Chinese text]compensateTemp()
     */
    public void getDeltaNucAndVTemp() {
        if (!isCompensation) {
            return;
        }
        if (DeviceIrcmdControlManager.getInstance()
                .getIrcmdEngine() != null){
            Log.d(TAG, "getDeltaNucAndVTemp start");
            // [Chinese text]vtemp
            int[] nativeAdvDeviceRealtimeStatusGetValue = new int[1];
            IrcmdError nativeAdvDeviceRealtimeStatusGetResult = DeviceIrcmdControlManager.getInstance()
                    .getIrcmdEngine()
                    .advDeviceRealtimeStatusGet(CommonParams.RealtimeStatusType.ADV_IR_SENSOR_VTEMP,
                            nativeAdvDeviceRealtimeStatusGetValue);
            int currentVTemp = nativeAdvDeviceRealtimeStatusGetValue[0];
            Log.d(TAG, "getDeltaNucAndVTemp currentVTemp = " + currentVTemp);
            // [Chinese text]DeltaVTemp
            deltaVTemp = vTempStart - currentVTemp;
            Log.d(TAG, "getDeltaNucAndVTemp deltaVTemp=" + deltaVTemp + "----NUC_new:" + nucNew);
            // [Chinese text]DeltaNUC
            deltaNUC = (int) (param1 * deltaVTemp * deltaVTemp + param2 * deltaVTemp - param3 - nucNew);
            Log.d(TAG, "getDeltaNucAndVTemp deltaNUC=" + deltaNUC);
        }
    }

    /**
     * 2D[Chinese text]temperature[Chinese text]
     * @param temp
     * @param deltaTime
     * @param nucT
     * @param deltaNUC
     * @return
     */
    private float getNewTempValue(float temp,long deltaTime,short[] nucT,int deltaNUC) {
        if (nucT == null) {
            return temp;
        }
        Log.d(TAG, "getNewTempValue start:" + temp);
        // [Chinese text]nuc
        int[] nucValue = new int[1];
        LibIRTemp.reverseCalcNUCWithNucT(nucT, temp, nucValue);
        int nuc = nucValue[0];
        Log.d(TAG, "getNewTempValue NUC: " + nuc);
        // [Chinese text]nuc
        int nucOut = nuc + deltaNUC;
        // [Chinese text]20s[Chinese text], line[Chinese text]
        long edgeTime = (ALL_DURATION - 10) * 1000L;
        if (deltaTime > edgeTime) {
            nucOut = nuc + deltaNUC * (int) (deltaTime % edgeTime / 1000 * -0.1 + 1);
        }
        Log.d(TAG, "getNewTempValue nucOut: " + nucOut);
        // [Chinese text]temperature[Chinese text]
        int[] newTemp = new int[1];
        LibIRTemp.remapTemp(nucT, nucOut, newTemp);
        int newTempInt = newTemp[0];
        float newTempFloat = newTempInt / 16f - 273.15f;
        Log.d(TAG, "getNewTempValue end: " + newTempFloat);

        // [Chinese text]30s
        isCompensation = deltaTime < ALL_DURATION * 1000L;
        if (!isCompensation) {
            stopTempCompensation(true);
        }
        return newTempFloat;
    }

    /**
     * [Chinese text]temperature[Chinese text]
     *
     * @return
     */
    private float getNewTempValue(float temp) {
        if (nucT == null) {
            return temp;
        }
        Log.d(TAG, "getNewTempValue start:" + temp);
        // [Chinese text]nuc
        int[] nucValue = new int[1];
        LibIRTemp.reverseCalcNUCWithNucT(nucT, temp, nucValue);
        int nuc = nucValue[0];
        Log.d(TAG, "getNewTempValue NUC: " + nuc);
        long deltaTime = System.currentTimeMillis() - startTime;
        // [Chinese text]nuc
        int nucOut = nuc + deltaNUC;
        // [Chinese text]20s[Chinese text], line[Chinese text]
        long edgeTime = (ALL_DURATION - 10) * 1000L;
        if (deltaTime > edgeTime) {
            nucOut = nuc + deltaNUC * (int) (deltaTime % edgeTime / 1000 * -0.1 + 1);
        }
        Log.d(TAG, "getNewTempValue nucOut: " + nucOut);
        // [Chinese text]temperature[Chinese text]
        int[] newTemp = new int[1];
        LibIRTemp.remapTemp(nucT, nucOut, newTemp);
        int newTempInt = newTemp[0];
        float newTempFloat = newTempInt / 16f - 273.15f;
        Log.d(TAG, "getNewTempValue end: " + newTempFloat);

        // [Chinese text]30s
        isCompensation = deltaTime < ALL_DURATION * 1000L;
        if (!isCompensation) {
            stopTempCompensation(true);
        }
        return newTempFloat;
    }

        /**
     * stoptemperature[Chinese text]
     */
    public void stopTempCompensation(boolean autoStop) {
        if (Const.DEVICE_TYPE != DeviceType.DEVICE_TYPE_TC2C) {
            return;
        }
        if (autoStop && isStart && DeviceIrcmdControlManager.getInstance().getIrcmdEngine() != null) {
            // stop[Chinese text], [Chinese text]
            IrcmdError basicAutoFFCStatusSet = DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
                    .basicAutoFFCStatusSet(CommonParams.AutoFFCStatus.AUTO_FFC_ENABLE);
            Log.d(TAG, "basicAutoFFCStatusSet=" + basicAutoFFCStatusSet);
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (handlerThread != null) {
            handlerThread.quitSafely();
            handlerThread = null;
        }
        isStart = false;
        isCompensation = false;
    }
    private short[] byteToShort(byte[] data) {
        short[] shortValue = new short[data.length / 2];
        for (int i = 0; i < shortValue.length; i++) {
            shortValue[i] = (short) ((data[i * 2] & 0xff) | ((data[i * 2 + 1] & 0xff) << 8));
        }
        return shortValue;
    }
}
