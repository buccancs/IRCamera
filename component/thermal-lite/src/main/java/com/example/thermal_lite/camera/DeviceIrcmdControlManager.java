package com.example.thermal_lite.camera;

import android.util.Log;

import com.energy.ac020library.IrcamEngine;
import com.energy.ac020library.IrcmdEngine;
import com.energy.ac020library.bean.IrcmdError;
import com.energy.commoncomponent.Const;
// Use existing FileUtil instead of missing commonlibrary util
import com.infisense.usbir.utils.FileUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by fengjibo on 2023/3/29.
\1device，APP
 */
public class DeviceIrcmdControlManager {

    private static final String TAG = "DeviceIrcmdControlManager"Test Data"sendFPGAParam"Test Data"fpga.json";
                    File file = new File(fpga_param_path);
                    if (!file.exists()) {
                        return;
                    }
                    String fpgaParams = FileUtil.getStringFromFile(fpga_param_path);
                    int firstAddress = 0x0096;

                    JSONArray jsonArray = new JSONArray(fpgaParams);
                    Log.d(TAG, "first jsonArray length : " + jsonArray.length());
//                    float[] params = new float[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int[] params = new int[1];
                        String name = jsonObject.getString("name");
                        String address = jsonObject.getString("address");
                        double value = jsonObject.getDouble("value");
                        params[0] = (int) value;
                        Log.d(TAG, "first params value : " + params[0]);
//                        if (i == 0) {
//                            Log.d(TAG, "first address string : " + address);
//                            firstAddress = Integer.parseInt(address.substring(2), 16);
//                            Log.d(TAG, "first address int : " + firstAddress);
//                        }
                        int reAddress = Integer.parseInt(address.substring(2), 16);
                        Log.d(TAG, "first address string : " + reAddress);
                        if (mIrcmdEngine != null) {
                            IrcmdError algorithmParametersWriteGet = mIrcmdEngine
                                    .advAlgorithmParametersWrite(reAddress, params);
                            Log.d(TAG, "algorithmParametersWriteGet result = "Test Data"algorithmParametersReadGet result = " + algorithmParametersReadGet);

                            for (int j = 0; j < algorithmParametersReadData.length; j++) {
                                Log.d(TAG, "algorithmParametersReadGet value = " + algorithmParametersReadData[j]);
                            }
                        }
                    }

//                    if (mIrcmdEngine != null) {
//                        IrcmdError algorithmParametersWriteGet = mIrcmdEngine
//                                .advAlgorithmParametersWrite(firstAddress, params);
//                        Log.d(TAG, "algorithmParametersWriteGet result = " + algorithmParametersWriteGet);
//
\1//getFPGAparameter PASS
//                        float[] algorithmParametersReadData = new float[jsonArray.length()];
//                        IrcmdError algorithmParametersReadGet = mIrcmdEngine
//                                .advAlgorithmParametersRead(firstAddress, algorithmParametersReadData);
//
//                        Log.d(TAG, "algorithmParametersReadGet result = " + algorithmParametersReadGet);
//
//                        for (int i = 0; i < algorithmParametersReadData.length; i ++) {
//                            Log.d(TAG, "algorithmParametersReadGet value = "Test Data"%8s",
                    Integer.toBinaryString(ispParamReadByteArray[i] & 0xFF)).replace(' ', '0'));
        }
        Log.i(TAG, "name = " + name + " ispParamReadByteArrStr = " + ispParamReadByteArrStr.toString() +
                " ispParamReadByteArrStrInt = " + Long.parseLong(ispParamReadByteArrStr.toString(), 2));

        String orgValue = ispParamReadByteArrStr.substring(byteWidth * 8 - end - 1, byteWidth * 8 - begin);
        Log.i(TAG, "name = " + name + " orgValue = " + orgValue +
                " orgValueInt = "Test Data"%8s",
                    Integer.toBinaryString(ispParamReadByteArray[i] & 0xFF)).replace(' ', '0'));
        }
        Log.i(TAG, "name = " + name + " ispParamReadByteArrStr = " + ispParamReadByteArrStr.toString() +
                " ispParamReadByteArrStrInt = "Test Data"%8s", Integer.toBinaryString(valueArray[i] & 0xFF)).replace(' ', '0'));
        }
        Log.i(TAG, "name = " + name + " valueArrStr = " + valueArrStr.toString());


        String orgValue = ispParamReadByteArrStr.substring(byteWidth * 8 - end - 1, byteWidth * 8 - begin);
        Log.i(TAG, "name = " + name + " orgValue = " + orgValue +
                " orgValueInt = "Test Data"name = " + name + " valueStr = " + valueArrStr.toString() + " valueStr = " + valueStr +
                " valueStrInt = " + Long.parseLong(valueStr, 2));

        //01110000   00000000 00001010 00000000 00000001
        return Long.parseLong(valueStr, 2);
    }

    /**
\1setISP
\1sendISPParamsetparameter
     *
     * @param param_path
     */
    public void setISPChangePath(String param_path) {
        ispParamPath = param_path;
        mSendISPCommand = true;
    }

    /**
\1sendISPParamsetparameter
     * <p>
\1ISPparameterset，saveparameter，
     *
     * @throws IllegalArgumentException
     */
    public void sendISPParam() {
        if (!mSendISPCommand) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "sendISPParam");
                try {
                    if (ispParamPath == null || ispParamPath.isEmpty()) {
                        return;
                    }
                    File file = new File(ispParamPath);
                    if (!file.exists()) {
                        return;
                    }
                    String fpgaParams = FileUtil.getStringFromFile(ispParamPath);

                    JSONArray jsonArray = new JSONArray(fpgaParams);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        long[] ispParamWriteData = new long[1];
                        String name = jsonObject.getString("name");
                        String address = jsonObject.getString("address");
                        if (address.startsWith("0x") || address.startsWith("0X")) {
                            address = address.substring(2);
                        }
                        int begin = jsonObject.getInt("begin");
                        int end = jsonObject.getInt("end");
                        int value = jsonObject.getInt("value");
//                        Log.i(TAG, "name = " + name + " address = " + address + " begin = " + begin + " end = " +
//                                end + " value = "Test Data"The method advISPParamRead execute fail."Test Data"The method advISPParamWrite execute fail.");
//                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSendISPCommand = false;
                ispParamPath = null;
            }
        }).start();
    }
}
