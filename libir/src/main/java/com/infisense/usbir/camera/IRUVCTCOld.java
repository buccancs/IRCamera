//package com.infisense.usbir.camera;
//
//import android.content.Context;
//import android.hardware.usb.UsbDevice;
//import android.os.SystemClock;
//import android.util.Log;
//
//import com.elvishew.xlog.XLog;
//import com.infisense.iruvc.sdkisp.LibIRProcess;
//import com.infisense.iruvc.sdkisp.Libircmd;
//import com.infisense.iruvc.sdkisp.Libirprocess;
//import com.infisense.iruvc.usb.DeviceFilter;
//import com.infisense.iruvc.usb.IFrameCallback;
//import com.infisense.iruvc.usb.USBMonitor;
//import com.infisense.iruvc.usb.UVCCamera;
//import com.infisense.iruvc.utils.CommonParams;
//import com.infisense.iruvc.utils.SynchronizedBitmap;
//import com.infisense.iruvc.uvc.ConnectCallback;
//import com.infisense.usbir.R;
//import com.infisense.usbir.config.MsgCode;
//import com.infisense.usbir.event.IRMsgEvent;
//import com.infisense.usbir.utils.USBMonitorCallback;
//import com.topdon.lib.core.bean.event.device.DeviceCameraEvent;
//import com.topdon.lib.core.bean.event.device.ResetConnectEvent;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.util.List;
//
///**
// * device -> bytes
// * Graph
// */
//public class IRUVCTC {
//
//    private static final String TAG = "IRUVC";
//    private final int TinyB = 0x3901;
//    private final IFrameCallback iFrameCallback;
//    private final Context context;
//    public UVCCamera uvcCamera;
//    private USBMonitor mUSBMonitor;
//    private int cameraWidth;
//    private int cameraHeight;
//    private byte[] image;
//    private byte[] temperature;
//    private SynchronizedBitmap syncimage;
//    // PID
//    private int pids[] = {0x5840, 0x3901, 0x5830, 0x5838};
//    public boolean auto_gain_switch = false;
//    private boolean auto_over_portect = false;
//    /**
//     * 
//     */
//    private LibIRProcess.AutoGainSwitchInfo_t auto_gain_switch_info = new LibIRProcess.AutoGainSwitchInfo_t();
//    private LibIRProcess.GainSwitchParam_t gain_switch_param = new LibIRProcess.GainSwitchParam_t();
//    private int count = 0;
//    private int rotate = 0;
//    long timeLog = 0L;//
//
//    private byte[] imageTemp = null;
//    private byte[] temperatureTemp = null;
//    private int countTemp = 0;
//    public byte[] imageEditTemp = null;
//    Long updateTime = 0L;
//
//    /**
//     * @param cameraHeight
//     * @param cameraWidth
//     * @param context
//     * @param syncimage
//     */
//    public IRUVCTC(int cameraHeight, int cameraWidth, Context context, SynchronizedBitmap syncimage,
//                   CommonParams.DataFlowMode dataFlowMode, boolean isUseIRISP, boolean isUseGPU,
//                   ConnectCallback connectCallback, USBMonitorCallback usbMonitorCallback) {
//        this.mContext = context;
//        this.syncimage = syncimage;
//        this.isUseIRISP = isUseIRISP;
//        this.isUseGPU = isUseGPU;
//        this.mConnectCallback = connectCallback;
//        this.defaultDataFlowMode = dataFlowMode;
//        init(cameraHeight, cameraWidth, context);
//
//
//        // ：USBMonitorCallback function
//        mUSBMonitor = new USBMonitor(context, new USBMonitor.OnDeviceConnectListener() {
//
//            // called by checking usb device
//            // do request device permission
//            @Override
//            public void onAttach(UsbDevice device) {
//                XLog.tag(TAG).w("onAttach");
//                if (isIRpid(device.getProductId())) {
//                    if (uvcCamera == null || !uvcCamera.getOpenStatus()) {
//                        mUSBMonitor.requestPermission(device);
//                    }
//                }
//            }
//
//            @Override
//            public void onGranted(UsbDevice usbDevice, boolean b) {
//
//            }
//
//            // called by connect to usb camera
//            // do open camera,start previewing
//            @Override
//            public void onConnect(final UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock, boolean createNew) {
//                XLog.tag(TAG).w("onConnect");
//                if (isIRpid(device.getProductId())) {
//                    if (createNew) {
//                        open(ctrlBlock);
//                        start();
//                    }
//                }
//                EventBus.getDefault().post(new ResetConnectEvent(1));
//            }
//
//            // called by disconnect to usb camera
//            // do nothing
//            @Override
//            public void onDisconnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {
//                XLog.tag(TAG).w("onDisconnect");
//            }
//
//            // called by taking out usb device
//            // do close camera
//            @Override
//            public void onDettach(UsbDevice device) {
//                XLog.tag(TAG).w("onDetach");
//                if (isIRpid(device.getProductId())) {
//                    if (uvcCamera != null && uvcCamera.getOpenStatus()) {
//                        stop();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancel(UsbDevice device) {
//                //usb permission
//                XLog.tag(TAG).w("onCancel");
//
//            }
//        });
//        // auto gain switch parameter
//        gain_switch_param.above_pixel_prop = 0.1f;    //high -> low gain,
//        gain_switch_param.above_temp_data = (int) ((130 + 273.15) * 16 * 4); //high -> low gain,
//        gain_switch_param.below_pixel_prop = 0.95f;   //low -> high gain,
//        gain_switch_param.below_temp_data = (int) ((110 + 273.15) * 16 * 4);//low -> high gain,
//        auto_gain_switch_info.switch_frame_cnt = 5 * 15; //(Graph15，5 * 155)
//        auto_gain_switch_info.waiting_frame_cnt = 7 * 15;//，(Graph15，7 * 157)
//        //over_portect parameter
//        int low_gain_over_temp_data = (int) ((550 + 273.15) * 16 * 4);
//        int high_gain_over_temp_data = (int) ((100 + 273.15) * 16 * 4);
//        float pixel_above_prop = 0.02f;         //0-1
//
//        // 
//        iFrameCallback = frame -> {
//            Log.d(TAG, "frame: " + "："+(System.currentTimeMillis()-updateTime));
//            updateTime = System.currentTimeMillis();
//            // ，
//            if (count++ >= 25) {
//                count = 1;
//                Log.d(TAG, "frame: " + frame.length);
//            }
//            if (syncimage == null) return;
//            syncimage.start = true;
//            synchronized (syncimage.dataLock) {
//                // ，sensor
//                int length = frame.length - 1;
//                if (frame[length] == 1) {
//                    EventBus.getDefault().post(new IRMsgEvent(MsgCode.RESTART_USB));
//                    XLog.tag(TAG).i("RESTART_USB");
//                    return;
//                }
//                /**
//                 * copyimageArray
//                 * GraphframeArray，，
//                 * 256*384，256*192，256*192，
//                 * 90，。
//                 */
//                if (imageEditTemp != null && imageEditTemp.length >= length) {
//                    //
//                    System.arraycopy(frame, 0, imageEditTemp, 0, length);
//                }
//                System.arraycopy(frame, 0, image, 0, length / 2);
//                Libirprocess.ImageRes_t imageRes = new Libirprocess.ImageRes_t();
//                imageRes.height = (char) (cameraHeight / 2);
//                imageRes.width = (char) cameraWidth;
////                Libirprocess.rotate_right_90(frame, imageRes, Libirprocess.IRPROC_SRC_FMT_Y14, imageEditTemp);
////                //
////                System.arraycopy(frame, length / 2, temperatureSrc, 0, length / 2);
//
////                //
////                countTemp++;
////                if (countTemp == 100) {
////                    imageTemp = new byte[length / 2];
////                    temperatureTemp = new byte[length / 2];
////
////                    System.arraycopy(frame, 0, imageTemp, 0, length / 2);
////                    XLog.tag("ahh").i("imageTemp: " + ByteUtils.INSTANCE.toHexString(imageTemp, " "));
////
////                    System.arraycopy(frame, length / 2, temperatureTemp, 0, length / 2);
////                    XLog.tag("ahh").i("temperatureTemp: " + ByteUtils.INSTANCE.toHexString(temperatureTemp, " "));
////                }
//
//                if (rotate == 270) {
//                    // 270
//                    byte[] temp = new byte[length / 2];
//                    System.arraycopy(frame, length / 2, temp, 0, length / 2);
//                    Libirprocess.rotate_right_90(temp, imageRes, Libirprocess.IRPROC_SRC_FMT_Y14, temperature);
//                } else if (rotate == 90) {
//                    // 90
//                    byte[] temp = new byte[length / 2];
//                    System.arraycopy(frame, length / 2, temp, 0, length / 2);
//                    Libirprocess.rotate_left_90(temp, imageRes, Libirprocess.IRPROC_SRC_FMT_Y14, temperature);
//                } else if (rotate == 180) {
//                    // 180
//                    byte[] temp = new byte[length / 2];
//                    System.arraycopy(frame, length / 2, temp, 0, length / 2);
//                    Libirprocess.rotate_180(temp, imageRes, Libirprocess.IRPROC_SRC_FMT_Y14, temperature);
//                } else {
//                    // 0
//                    System.arraycopy(frame, length / 2, temperature, 0, length / 2);
//                }
//                // ，
//                if (auto_gain_switch) {
//                    Libircmd.auto_gain_switch(temperature, imageRes, auto_gain_switch_info, gain_switch_param, uvcCamera.nativePtr);
//                }
//                // 
//                if (auto_over_portect) {
//                    Libircmd.avoid_overexposure(temperature, imageRes, low_gain_over_temp_data,
//                            high_gain_over_temp_data, pixel_above_prop, 15 * 25, uvcCamera.nativePtr);
//                }
//            }
//        };
//    }
//
//    /**
//     * @param rotate
//     */
//    public void setRotate(int rotate) {
//        this.rotate = rotate;
//    }
//
//    /**
//     * @param image
//     */
//    public void setImage(byte[] image) {
//        this.image = image;
//    }
//
//    /**
//     * @param temperature
//     */
//    public void setTemperature(byte[] temperature) {
//        this.temperature = temperature;
//    }
//
//    public void setImageEditSrc(byte[] imageEditTemp) {
//        this.imageEditTemp = imageEditTemp;
//    }
//
//    /**
//     * ，PIDPID
//     *
//     * @param devpid
//     * @return
//     */
//    private boolean isIRpid(int devpid) {
//        for (int x : pids) {
//            if (x == devpid) return true;
//        }
//        return false;
//    }
//
//    /**
//     * @param cameraHeight
//     * @param cameraWidth
//     * @param context
//     */
//    public void init(int cameraHeight, int cameraWidth, Context context) {
//        XLog.tag(TAG).w("init");
//        uvcCamera = new UVCCamera(cameraWidth, cameraHeight, context);
//        uvcCamera.create();
//        EventBus.getDefault().post(new DeviceCameraEvent(100));
//    }
//
//    /**
//     *
//     */
//    public void registerUSB() {
//        if (mUSBMonitor != null) {
//            mUSBMonitor.register();
//        }
//    }
//
//    /**
//     *
//     */
//    public void unregisterUSB() {
//        if (mUSBMonitor != null) {
//            mUSBMonitor.unregister();
//        }
//    }
//
//    /**
//     * @return
//     */
//    public List<UsbDevice> getUsbDeviceList() {
////        List<DeviceFilter> deviceFiltersTemp = DeviceFilter.getDeviceFilters(context, R.xml.device_filter);
//        List<DeviceFilter> deviceFilters = DeviceFilter.getDeviceFilters(context, R.xml.ir_device_filter);
//        if (mUSBMonitor == null || deviceFilters == null)
////            throw new NullPointerException("mUSBMonitor ="+mUSBMonitor+"deviceFilters=;"+deviceFilters);
//            return null;
//        // matching all of filter devices
//        return mUSBMonitor.getDeviceList(deviceFilters);
//    }
//
//    /**
//     * @param index
//     */
//    public void requestPermission(int index) {
//        List<UsbDevice> devList = getUsbDeviceList();
//        if (devList == null || devList.size() == 0) {
//            return;
//        }
//        int count = devList.size();
//        if (index >= count)
//            new IllegalArgumentException("index illegal,should be < devList.size()");
//        if (mUSBMonitor != null) {
//            mUSBMonitor.requestPermission(getUsbDeviceList().get(index));
//        }
//    }
//
//    /**
//     * @param ctrlBlock
//     */
//    public void open(USBMonitor.UsbControlBlock ctrlBlock) {
//        if (ctrlBlock.getProductId() == TinyB) {
//            if (syncimage != null) {
//                syncimage.type = 1;
//            }
//        }
//        if (uvcCamera == null) {
//            init(cameraHeight, cameraWidth, context);
//        }
//        uvcCamera.open(ctrlBlock);
//    }
//
//    /**
//     *
//     */
//    public void start() {
//        try {
//            XLog.tag(TAG).w("start");
//            uvcCamera.setOpenStatus(true);
//            uvcCamera.setFrameCallback(iFrameCallback); //
//            //uvcCamera.setgetframemode(uvcCamera.GET_FRAME_ASYNC);
//            //default sync mode for some devices  Lost-Packet
//            //uvcCamera.DEFAULT_BANDWIDTH=0.3f;//hub
//            uvcCamera.startPreview(); //
//            new Thread(() -> {
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                EventBus.getDefault().post(new DeviceCameraEvent(101));
//                //
//                if (uvcCamera != null) {
//                    if (syncimage.type == 1) {
//                        Libircmd.tiny1b_shutter_manual(uvcCamera.nativePtr);
//                    } else {
//                        //
//                        Libircmd.ooc_b_update(Libircmd.B_UPDATE, uvcCamera.nativePtr);
//                    }
//                }
//            }).start();
//        }catch (Exception e){
//            Log.w("sdk", e.getMessage());
//        }
//
//    }
//
//    /**
//     *
//     */
//    public void stop() {
//        XLog.tag(TAG).w("stop");
////        if (uvcCamera != null) {
////            if (uvcCamera.getOpenStatus()) {
////                uvcCamera.stopPreview();
////            }
////            final UVCCamera camera;
////            camera = uvcCamera;
////            uvcCamera = null;
////            SystemClock.sleep(200);
////            camera.destroy();
////            EventBus.getDefault().post(new ResetConnectEvent(3));
////        }
//    }
//
////    Disposable disposable = null;
////    private boolean isRun = false;
////
////    private void monitor() {
////        if (disposable != null) {
////            disposable.dispose();
////        }
////        disposable = Observable.interval(1L, TimeUnit.SECONDS).take(1000)
////                .subscribeOn(Schedulers.io())
////                .subscribe(aLong -> {
////                    Log.w("123", "aLong" + aLong);
//////                    if (isRun) {
//////                        if (timeLog != 0 && System.currentTimeMillis() - timeLog > 1000) {
//////                            //
//////                            EventBus.getDefault().post(new DeviceConnectEvent(false, null));
//////                            XLog.w("1s,");
////////                ToastTools.INSTANCE.showShort("1s,");
//////                        }
//////                        timeLog = System.currentTimeMillis();
//////                    }
////                });
////        Log.w("123", "Observable.timer");
////    }
////
////    private void cancelMonitor() {
////        isRun = false;
////        if (disposable != null) {
////            disposable.dispose();
////        }
////    }
//
//}
