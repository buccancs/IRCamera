package com.infisense.usbir.camera;//package com.infisense.usbir.camera;
//import android.content.Context;
//import android.hardware.usb.UsbDevice;
//import android.os.Handler;
//import android.os.SystemClock;
//import android.util.Log;
//import com.infisense.iruvc.sdkisp.Libircmd;
//import com.infisense.iruvc.sdkisp.Libirprocess;
//import com.infisense.iruvc.usb.DeviceFilter;
//import com.infisense.iruvc.usb.IFrameCallback;
//import com.infisense.iruvc.usb.USBMonitor;
//import com.infisense.iruvc.usb.UVCCamera;
//import com.infisense.iruvc.utils.SynchronizedBitmap;
//import com.infisense.usbir.R;
//import com.infisense.usbir.activity.IRDisplayActivity;
//import java.util.List;
//public class IRUVC {
//    private static final String TAG = IRUVC;
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
//    private boolean auto_gain_switch = false;
//    private boolean auto_over_portect = false;
//    private Libirprocess.AutoGainSwitchInfo_t auto_gain_switch_info = new Libirprocess.AutoGainSwitchInfo_t();
//    private Libirprocess.GainSwitchParam_t gain_switch_param = new Libirprocess.GainSwitchParam_t();
//    private int count = 0;
//    private Handler handler;
//    private boolean rotate = false;
//     * @param cameraHeight
//     * @param cameraWidth
//     * @param context
//     * @param syncimage
//    public IRUVC(int cameraHeight, int cameraWidth, Context context, SynchronizedBitmap syncimage) {
//        this.cameraHeight = cameraHeight;
//        this.cameraWidth = cameraWidth;
//        this.context = context;
//        this.syncimage = syncimage;
//        init(cameraHeight, cameraWidth, context);
//        // USBMonitor
//        mUSBMonitor = new USBMonitor(context, new USBMonitor.OnDeviceConnectListener() {
//            // called by checking usb device
//            // do request device permission
//            @Override
//            public void onAttach(UsbDevice device) {
//                Log.w(TAG, onAttach);
//                if (isIRpid(device.getProductId())) {
//                    if (uvcCamera == null || !uvcCamera.getOpenStatus()) {
//                        mUSBMonitor.requestPermission(device);
//                    }
//                }
//            }
//            // called by connect to usb camera
//            // do open camera,start previewing
//            @Override
//            public void onConnect(final UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock, boolean createNew) {
//                Log.w(TAG, onConnect);
//                if (isIRpid(device.getProductId())) {
//                    if (createNew) {
//                        open(ctrlBlock);
//                        start();
//                    }
//                }
//            }
//            // called by disconnect to usb camera
//            // do nothing
//            @Override
//            public void onDisconnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {
//                Log.w(TAG, onDisconnect);
//            }
//            // called by taking out usb device
//            // do close camera
//            @Override
//            public void onDettach(UsbDevice device) {
//                Log.w(TAG, onDettach);
//                if (isIRpid(device.getProductId())) {
//                    if (uvcCamera != null && uvcCamera.getOpenStatus()) {
//                        stop();
//                    }
//                }
//            }
//            @Override
//            public void onCancel(UsbDevice device) {
//                Log.w(TAG, onCancel);
//            }
//        });
//        // auto gain switch parameter
//        gain_switch_param.above_pixel_prop = 0.1f;    //0-1
//        gain_switch_param.above_temp_data = (int) ((130 + 273.15) * 16 * 4);
//        gain_switch_param.below_pixel_prop = 0.95f;   //0-1
//        gain_switch_param.below_temp_data = (int) ((110 + 273.15) * 16 * 4);
//        auto_gain_switch_info.switch_frame_cnt = 5 * 25;
//        auto_gain_switch_info.waiting_frame_cnt = 7 * 25;
//        //over_portect parameter
//        int low_gain_over_temp_data = (int) ((550 + 273.15) * 16 * 4);
//        int high_gain_over_temp_data = (int) ((100 + 273.15) * 16 * 4);
//        float pixel_above_prop = 0.02f;         //0-1
//        iFrameCallback = frame -> {
//            count++;
//            if (count == 100) {
//                count = 0;
//                Log.d(onFrame,  + frame.length);
//            }
//            if (syncimage == null) return;
//            syncimage.start = true;
//            synchronized (syncimage.dataLock) {
//                // sensor
//                int length = frame.length - 1;
//                if (frame[length] == 1) {
//                    if (handler != null)
//                        handler.sendEmptyMessage(IRDisplayActivity.RESTART_USB);
//                    Log.d(TAG, RESTART_USB);
//                    return;
//                }
//                 * copyimage
//                 * frame
//                 * 256*384256*192256*192
//                 * 90
//                System.arraycopy(frame, 0, image, 0, length / 2);
//                Libirprocess.ImageRes_t imageRes = new Libirprocess.ImageRes_t();
//                imageRes.height = (char) (cameraHeight / 2);
//                imageRes.width = (char) cameraWidth;
//                if (rotate) {
//                    byte[] temp = new byte[length / 2];
//                    System.arraycopy(frame, length / 2, temp, 0, length / 2);
//                    Libirprocess.rotate_right_90(temp, imageRes, Libirprocess.IRPROC_SRC_FMT_Y14, temperature);
//                } else {
//                    System.arraycopy(frame, length / 2, temperature, 0, length / 2);
//                }
//                if (auto_gain_switch) {
//                    Libircmd.auto_gain_switch(temperature, imageRes, auto_gain_switch_info,
//                            gain_switch_param, uvcCamera.nativePtr);
//                }
//                if (auto_over_portect) {
//                    Libircmd.avoid_overexposure(temperature, imageRes, low_gain_over_temp_data,
//                            high_gain_over_temp_data, pixel_above_prop, 15 * 25, uvcCamera.nativePtr);
//                }
//            }
//        };
//    }
//     * @param handler
//    public void setHandler(Handler handler) {
//        this.handler = handler;
//    }
//     * @param rotate
//    public void setRotate(boolean rotate) {
//        this.rotate = rotate;
//    }
//     * @param image
//    public void setImage(byte[] image) {
//        this.image = image;
//    }
//     * @param temperature
//    public void setTemperature(byte[] temperature) {
//        this.temperature = temperature;
//    }
//     * PIDPID
//     * @param devpid
//     * @return
//    private boolean isIRpid(int devpid) {
//        for (int x : pids) {
//            if (x == devpid) return true;
//        }
//        return false;
//    }
//     * @param cameraHeight
//     * @param cameraWidth
//     * @param context
//    public void init(int cameraHeight, int cameraWidth, Context context) {
//        Log.w(TAG, init);
//        uvcCamera = new UVCCamera(cameraWidth, cameraHeight, context);
//        uvcCamera.create();
//    }
//    public void registerUSB() {
//        if (mUSBMonitor != null) {
//            mUSBMonitor.register();
//        }
//    }
//    public void unregisterUSB() {
//        if (mUSBMonitor != null) {
//            mUSBMonitor.unregister();
//        }
//    }
//     * @return
//    public List<UsbDevice> getUsbDeviceList() {
//        List<DeviceFilter> deviceFilters = DeviceFilter
//                .getDeviceFilters(context, R.xml.device_filter);
//        if (mUSBMonitor == null || deviceFilters == null)
////            throw new NullPointerException(mUSBMonitor =+mUSBMonitor+deviceFilters=;+deviceFilters);
//            return null;
//        // matching all of filter devices
//        return mUSBMonitor.getDeviceList(deviceFilters);
//    }
//     * @param index
//    public void requestPermission(int index) {
//        List<UsbDevice> devList = getUsbDeviceList();
//        if (devList == null || devList.size() == 0) {
//            return;
//        }
//        int count = devList.size();
//        if (index >= count)
//            new IllegalArgumentException(index illegal,should be < devList.size());
//        if (mUSBMonitor != null) {
//            mUSBMonitor.requestPermission(getUsbDeviceList().get(index));
//        }
//    }
//     * @param ctrlBlock
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
//    public void start() {
//        Log.w(TAG, start);
//        uvcCamera.setOpenStatus(true);
//        uvcCamera.setFrameCallback(iFrameCallback);
//        //uvcCamera.setgetframemode(uvcCamera.GET_FRAME_ASYNC);
//        //default sync mode for some devices  Lost-Packet
//        //uvcCamera.DEFAULT_BANDWIDTH=0.3f;//hub
//        uvcCamera.startPreview();
//    }
//    public void stop() {
//        Log.w(TAG, stop);
//        if (uvcCamera != null) {
//            if (uvcCamera.getOpenStatus()) {
//                uvcCamera.stopPreview();
//            }
//            final UVCCamera camera;
//            camera = uvcCamera;
//            uvcCamera = null;
//            SystemClock.sleep(200);
//            camera.destroy();
//        }
//    }
//}
