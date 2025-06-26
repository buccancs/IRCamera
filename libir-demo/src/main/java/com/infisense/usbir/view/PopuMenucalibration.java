package com.infisense.usbir.view;//package com.infisense.usbir.view;
//
//import android.content.Context;
//import android.graphics.drawable.ColorDrawable;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//import android.widget.Toast;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.infisense.iruvc.sdkisp.Libircmd;
//import com.infisense.usbir.R;
//import com.infisense.usbir.camera.IRUVC;
//
//public class PopuMenucalibration {
//    private static final String TAG = "PopuMenucalibration";
//    private PopupWindow popupWindow;
//    private Button singlepointsumit;
//    private Button doiblepointsumit;
//    private Button endpointsumit;
//    private Button cancel;
//    private Button bengin;
//    private Button start;
//    private Button savecfg;
//    private Button restorecfg;
//    private EditText x, y, gain;
//    private Button add_dp;
//    private Button rm_dp;
//    private EditText singlepoint;
//    private EditText lowpoint;
//    private EditText highpoint;
//    public IRUVC usbcamera;
//    View view;
//
//    public PopuMenucalibration(Context context) {
//        char[] point = new char[2];
//        view = LayoutInflater.from(context).inflate(R.layout.layout_calibration, null);
//        singlepointsumit = view.findViewById(R.id.singlepointsumit);
//        doiblepointsumit = view.findViewById(R.id.doiblepointsumit);
//        cancel = view.findViewById(R.id.cancel);
//        singlepoint = view.findViewById(R.id.singlepoint);
//        lowpoint = view.findViewById(R.id.lowpoint);
//        highpoint = view.findViewById(R.id.highpoint);
//        endpointsumit = view.findViewById(R.id.endpointsumit);
//        bengin = view.findViewById(R.id.bengin);
//        start = view.findViewById(R.id.start);
//        x = view.findViewById(R.id.x);
//        y = view.findViewById(R.id.y);
//        gain = view.findViewById(R.id.gain);
//        add_dp = view.findViewById(R.id.add_dp);
//        rm_dp = view.findViewById(R.id.rm_dp);
//        savecfg = view.findViewById(R.id.savecfg);
//        restorecfg = view.findViewById(R.id.restorecfg);
//        View.OnClickListener handler = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onViewClicked: " + view.getId());
//                int id = view.getId();
//                if (id == R.id.singlepointsumit) {
//                    if (usbcamera.uvcCamera != null) {
//                        if (singlepoint.getText().toString().length() != 0)
//                            Libircmd.set_tpd_recal(Libircmd.RECAL_1_POINT, Integer.parseInt(singlepoint.getText().toString()), usbcamera.uvcCamera.nativePtr);
//                        //usbcamera.uvcCamera.setTPD_RECAL(Integer.parseInt(singlepoint.getText().toString()));
//                    }
//                } else if (id == R.id.doiblepointsumit) {
//                    if (usbcamera.uvcCamera != null) {
//                        if (lowpoint.getText().toString().length() != 0)
//                            Libircmd.set_tpd_recal(Libircmd.RECAL_2_POINT_FIRST, Integer.parseInt(lowpoint.getText().toString()), usbcamera.uvcCamera.nativePtr);
//                        //usbcamera.uvcCamera.setTPD_RECAL2(Integer.parseInt(lowpoint.getText().toString()), 1);
//                        doiblepointsumit.setVisibility(View.INVISIBLE);
//                        endpointsumit.setVisibility(View.VISIBLE);
//                    }
//                } else if (id == R.id.endpointsumit) {
//                    if (usbcamera.uvcCamera != null) {
//                        if (highpoint.getText().toString().length() != 0)
//                            Libircmd.set_tpd_recal(Libircmd.RECAL_2_POINT_END, Integer.parseInt(highpoint.getText().toString()), usbcamera.uvcCamera.nativePtr);
//                        //usbcamera.uvcCamera.setTPD_RECAL2(Integer.parseInt(highpoint.getText().toString()), 2);
//                        endpointsumit.setVisibility(View.INVISIBLE);
//                        doiblepointsumit.setVisibility(View.VISIBLE);
//                    }
//                } else if (id == R.id.cancel) {
//                    if (usbcamera.uvcCamera != null) {
//                        Libircmd.restore_default_cfg(2, usbcamera.uvcCamera.nativePtr);
//                        singlepoint.setText("");
//                        lowpoint.setText("");
//                        highpoint.setText("");
//                        endpointsumit.setVisibility(View.INVISIBLE);
//                        doiblepointsumit.setVisibility(View.VISIBLE);
//                        //usbcamera.uvcCamera.setRESTORE_TPD();
//                        // showShortMsg("重启摄像头生效");
//                        //singlepoint.setText(usbcamera.uvcCamera.getEnverionmentTemperature()+"");
//                    }
//                } else if (id == R.id.bengin) {
//                    if (usbcamera.uvcCamera != null) {
//                        Libircmd.revcover_disable(usbcamera.uvcCamera.nativePtr);
//                    }
//                } else if (id == R.id.start) {
//                    if (usbcamera.uvcCamera != null) {
//                        if (gain.getText().toString().length() != 0)
//                            Libircmd.auto_calc_revcover((byte) Integer.parseInt(gain.getText().toString()), usbcamera.uvcCamera.nativePtr);
//                        Libircmd.revcover_enable(usbcamera.uvcCamera.nativePtr);
//                    }
//                } else if (id == R.id.add_dp) {
//                    if (x.getText().toString().length() != 0)
//                        point[0] = (char) Integer.parseInt(x.getText().toString());
//                    if (y.getText().toString().length() != 0)
//                        point[1] = (char) Integer.parseInt(y.getText().toString());
//                    if (usbcamera.uvcCamera != null) {
//                        Libircmd.add_dp(point, usbcamera.uvcCamera.nativePtr);
//                    }
//                } else if (id == R.id.rm_dp) {
//                    if (x.getText().toString().length() != 0)
//                        point[0] = (char) Integer.parseInt(x.getText().toString());
//                    if (y.getText().toString().length() != 0)
//                        point[1] = (char) Integer.parseInt(y.getText().toString());
//                    if (usbcamera.uvcCamera != null) {
//                        Libircmd.remove_dp(point, usbcamera.uvcCamera.nativePtr);
//                    }
//                } else if (id == R.id.savecfg) {
//                    if (usbcamera.uvcCamera != null) {
//                        Libircmd.save_cfg(0, usbcamera.uvcCamera.nativePtr);
//                    }
//                } else if (id == R.id.restorecfg) {// 恢复出厂标定
//                    if (usbcamera.uvcCamera != null) {
//                        Libircmd.restore_default_cfg(0, usbcamera.uvcCamera.nativePtr);
//                        Toast toast = Toast.makeText(view.getContext(), "重启模组生效", Toast.LENGTH_SHORT);
//                        //toast.setGravity(Gravity.CENTER, 0, 0);
//                        toast.show();
//                    }
//                }
//            }
//        };
//        singlepointsumit.setOnClickListener(handler);
//        doiblepointsumit.setOnClickListener(handler);
//        endpointsumit.setOnClickListener(handler);
//        cancel.setOnClickListener(handler);
//        bengin.setOnClickListener(handler);
//        start.setOnClickListener(handler);
//        add_dp.setOnClickListener(handler);
//        rm_dp.setOnClickListener(handler);
//        savecfg.setOnClickListener(handler);
//        restorecfg.setOnClickListener(handler);
//        popupWindow = new PopupWindow(view);
//        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setFocusable(true);
//        popupWindow.setOutsideTouchable(false);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000)); // 解决 7.0 手机，点击外部不消失
//        popupWindow.setAnimationStyle(R.style.contextMenuAnim);
//        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        //创建布局管理
//        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//    }
//
//    public void showheight(LinearLayout linearLayout, int popupheight) {
//        popupWindow.showAtLocation(linearLayout, Gravity.NO_GRAVITY, 0, popupheight);
//    }
//
//    private void showShortMsg(String msg) {
//        Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT).show();
//    }
//}