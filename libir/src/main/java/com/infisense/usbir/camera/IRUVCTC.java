package com.infisense.usbir.camera;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.SystemClock;
import android.util.Log;

import com.energy.iruvc.ircmd.ConcreteIRCMDBuilder;
import com.energy.iruvc.ircmd.IRCMD;
import com.energy.iruvc.ircmd.IRCMDType;
import com.energy.iruvc.sdkisp.LibIRProcess;
import com.energy.iruvc.usb.USBMonitor;
import com.energy.iruvc.utils.AutoGainSwitchCallback;
import com.energy.iruvc.utils.AvoidOverexposureCallback;
import com.energy.iruvc.utils.CommonParams;
import com.energy.iruvc.utils.DeviceType;
import com.energy.iruvc.utils.IFrameCallback;
import com.energy.iruvc.utils.SynchronizedBitmap;
import com.energy.iruvc.uvc.CameraSize;
import com.energy.iruvc.uvc.ConcreateUVCBuilder;
import com.energy.iruvc.uvc.ConnectCallback;
import com.energy.iruvc.uvc.UVCCamera;
import com.energy.iruvc.uvc.UVCType;
import com.infisense.usbir.config.MsgCode;
import com.infisense.usbir.event.IRMsgEvent;
import com.infisense.usbir.event.PreviewComplete;
import com.infisense.usbir.utils.FileUtil;
import com.infisense.usbir.utils.ScreenUtils;
import com.infisense.usbir.utils.USBMonitorCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * infrared
 */
public class IRUVCTC {
    private static final String TAG = "IRUVC_DATA";
    private final IFrameCallback iFrameCallback;
    public UVCCamera uvcCamera;
    private IRCMD ircmd;
    //
    private final USBMonitor mUSBMonitor;
    private final ConnectCallback mConnectCallback; // usb
    private byte[] imageSrc;
    private byte[] temperatureSrc;
    private final int imageOrTempDataLength = 256 * 192 * 2; // infrared
    private final SynchronizedBitmap syncimage;
    /**
     * gainswitch
     */
    private final LibIRProcess.AutoGainSwitchInfo_t auto_gain_switch_info = new LibIRProcess.AutoGainSwitchInfo_t();
    private final LibIRProcess.GainSwitchParam_t gain_switch_param = new LibIRProcess.GainSwitchParam_t();
    private int rotateInt = 0;

    // ，，
    private boolean isFrameReady = true;
    // currentgainstate
    private final CommonParams.GainStatus gainStatus = CommonParams.GainStatus.HIGH_GAIN;
    private final byte[] temperatureTemp = new byte[imageOrTempDataLength];
    // infrared+TNR
    private boolean isTempReplacedWithTNREnabled;
    private final CommonParams.DataFlowMode defaultDataFlowMode;
    private boolean isRestart;
    public boolean auto_gain_switch = false;
    private final boolean auto_over_portect = false;
    public byte[] imageEditTemp = null;
    private int pids[] = {0x5840, 0x3901, 0x5830, 0x5838};
    private IFrameCallBackListener iFrameCallBackListener;

    private IFrameReadListener iFrameReadListener;
    public volatile boolean isFirstFrame;

    public void setIFrameCallBackListener(IFrameCallBackListener iFrameCallBackListener) {
        this.iFrameCallBackListener = iFrameCallBackListener;
    }

    public void setiFirstFrameListener(IFrameReadListener iFrameReadListener) {
        this.iFrameReadListener = iFrameReadListener;
    }

    public interface IFrameCallBackListener {
        void updateData();
    }

    public interface IFrameReadListener {
        void frameRead();
    }

    /**
     * @param cameraWidth     cameraWidth:256,cameraHeight:384,+
     *                        cameraWidth:256,cameraHeight:192,
     *                        cameraWidth:256,cameraHeight:192,(startY16ModePreview，Y16_MODE_TEMPERATURE)
     * @param connectCallback settingsusb
     */
    public IRUVCTC(int cameraWidth, int cameraHeight, Context context, SynchronizedBitmap syncimage,
                   CommonParams.DataFlowMode dataFlowMode,
                   ConnectCallback connectCallback, USBMonitorCallback usbMonitorCallback) {
        this.syncimage = syncimage;
        this.mConnectCallback = connectCallback;
        this.defaultDataFlowMode = dataFlowMode;
        isFirstFrame = true;

        //
        initUVCCamera();
        // ：USBMonitor
        mUSBMonitor = new USBMonitor(context, new USBMonitor.OnDeviceConnectListener() {

            // called by checking usb device
            // do request device permission
            @Override
            public void onAttach(UsbDevice device) {
                Log.w(TAG, "onAttach");
                if (uvcCamera == null || !uvcCamera.getOpenStatus()) {
                    mUSBMonitor.requestPermission(device);
                }
                if (usbMonitorCallback != null) {
                    usbMonitorCallback.onAttach();
                }
            }

            @Override
            public void onGranted(UsbDevice usbDevice, boolean granted) {
                Log.w(TAG, "onGranted");
                if (usbMonitorCallback != null) {
                    usbMonitorCallback.onGranted();
                }
            }

            // called by connect to usb camera
            // do open camera,start previewing
            @Override
            public void onConnect(final UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock, boolean createNew) {
                Log.w(TAG, "onConnect");
                if (isIRpid(device.getProductId())){
                    if (createNew) {
                        openUVCCamera(ctrlBlock);

                        // list
                        List<CameraSize> previewList = getAllSupportedSize();
                        for (CameraSize size : previewList) {
                            Log.i(TAG, "SupportedSize : " + size.width + " * " + size.height);
                        }

                        // list，，cmdSDK
                        initIRCMD();

                        if (ircmd != null) {
                            Log.d(TAG, "startPreview");
                            // list，settings(，)
                            // openUVCCamera，
                            isTempReplacedWithTNREnabled = ircmd.isTempReplacedWithTNREnabled(DeviceType.P2);
                            if (isTempReplacedWithTNREnabled) {
                                // infrared+TNR，，
                                if (uvcCamera != null) {
                                    uvcCamera.setUSBPreviewSize(cameraWidth, cameraHeight * 2);
                                }
                            } else {
                                // TNR
                                if (uvcCamera != null) {
                                    uvcCamera.setUSBPreviewSize(cameraWidth, cameraHeight);
                                }
                            }
                            startPreview();
                        }

                        if (usbMonitorCallback != null) {
                            usbMonitorCallback.onConnect();
                        }
                    }
                }
            }

            // called by disconnect to usb camera
            // do nothing
            @Override
            public void onDisconnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {
                Log.w(TAG, "onDisconnect");
                if (usbMonitorCallback != null) {
                    usbMonitorCallback.onDisconnect();
                }
            }

            // called by taking out usb device
            // do close camera
            @Override
            public void onDettach(UsbDevice device) {
                Log.w(TAG, "onDettach");
                if (uvcCamera != null && uvcCamera.getOpenStatus()) {
                    if (usbMonitorCallback != null) {
                        usbMonitorCallback.onDettach();
                    }
                }
            }

            @Override
            public void onCancel(UsbDevice device) {
                Log.w(TAG, "onCancel");
                if (usbMonitorCallback != null) {
                    usbMonitorCallback.onCancel();
                }
            }
        });
        /*
         * gainswitch，gainswitch，
         */
        // gainswitchauto gain switch parameter
        gain_switch_param.above_pixel_prop = 0.1f;    //high -> low gain,
        gain_switch_param.above_temp_data = (int) ((130 + 273.15) * 16 * 4); //high -> low gain,gaingainswitch
        gain_switch_param.below_pixel_prop = 0.95f;   //low -> high gain,
        gain_switch_param.below_temp_data = (int) ((110 + 273.15) * 16 * 4);//low -> high gain,gaingainswitch
        auto_gain_switch_info.switch_frame_cnt = 5 * 15; //continuousgainswitch(15，5 * 155)
        auto_gain_switch_info.waiting_frame_cnt = 7 * 15;//gainswitch，gainswitch(15，7 * 157)
        // over_portect parameter
        int low_gain_over_temp_data = (int) ((550 + 273.15) * 16 * 4); //gain
        int high_gain_over_temp_data = (int) ((150 + 273.15) * 16 * 4); //gain
        float pixel_above_prop = 0.02f;//
        int switch_frame_cnt = 7 * 15;//continuous(15，7 * 157)
        int close_frame_cnt = 10 * 15;//，(15，10 * 1510)


        LibIRProcess.ImageRes_t imageRes = new LibIRProcess.ImageRes_t();
        imageRes.height = (char) (dataFlowMode == CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT ? cameraHeight / 2
                : cameraHeight);
        imageRes.width = (char) cameraWidth;

        // 
        iFrameCallback = new IFrameCallback() {
            @Override
            public void onFrame(byte[] frame) {
                if (!isFrameReady) {
                    return;
                }
                if (syncimage == null) {
                    return;
                }
                syncimage.start = true;
                //
                synchronized (syncimage.dataLock) {
                    // ，sensor
                    int length = frame.length - 1;
                    if (frame[length] == 1) {
                        // bad frame
                        EventBus.getDefault().post(new IRMsgEvent(MsgCode.RESTART_USB));
                        return;
                    }
                    if (imageEditTemp != null && imageEditTemp.length >= length) {
                        //saved
                        System.arraycopy(frame, 0, imageEditTemp, 0, length);
                    }
//                    try {
//                        byte[] tmpBy = new byte[256*192*2];
//                        System.arraycopy(frame, imageOrTempDataLength, tmpBy, 0,
//                                imageOrTempDataLength);
//                        LibIRTemp tmp = new LibIRTemp(256,192);
//                        tmp.setTempData(tmpBy);
//                        LibIRTemp.TemperatureSampleResult result = tmp.getTemperatureOfRect(new Rect(0, 0, 256,192));
//                        Log.w("update3",result.maxTemperature+"///"+result.minTemperature);
//                    }catch (Exception  e){
//
//                    }
                    if (dataFlowMode == CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT) {
                        /*
                         * +
                         * copyinfraredimagearray
                         * framearrayinfrared，，
                         * 256*384，256*192infrared，256*192，
                         * 90，,infraredImageThread。
                         */
                        System.arraycopy(frame, 0, imageSrc, 0, imageOrTempDataLength);
                        /*
                         * 
                         * ，，，
                         */
                        if (length >= imageOrTempDataLength * 2) {

                            if (rotateInt == 270) {
                                // 270
                                System.arraycopy(frame, imageOrTempDataLength, temperatureTemp, 0,
                                        imageOrTempDataLength);
                                LibIRProcess.rotateRight90(temperatureTemp, imageRes,
                                        CommonParams.IRPROCSRCFMTType.IRPROC_SRC_FMT_Y14, temperatureSrc);
                            } else if (rotateInt == 90) {
                                // 90
                                System.arraycopy(frame, imageOrTempDataLength, temperatureTemp, 0,
                                        imageOrTempDataLength);
                                LibIRProcess.rotateLeft90(temperatureTemp, imageRes,
                                        CommonParams.IRPROCSRCFMTType.IRPROC_SRC_FMT_Y14, temperatureSrc);
                            } else if (rotateInt == 180) {
                                // 180
                                System.arraycopy(frame, imageOrTempDataLength, temperatureTemp, 0,
                                        imageOrTempDataLength);
                                LibIRProcess.rotate180(temperatureTemp, imageRes,
                                        CommonParams.IRPROCSRCFMTType.IRPROC_SRC_FMT_Y14, temperatureSrc);
                            } else {
                                // 0
                                System.arraycopy(frame, imageOrTempDataLength, temperatureSrc, 0,
                                        imageOrTempDataLength);
//                                System.arraycopy(frame, length / 2, temperatureSrc, 0, length / 2);
                            }
                            if (ircmd != null) {
                                // gainswitch，effectivegainswitch
                                if (auto_gain_switch) {
                                    ircmd.autoGainSwitch(temperatureSrc, imageRes, auto_gain_switch_info,
                                            gain_switch_param, new AutoGainSwitchCallback() {
                                                @Override
                                                public void onAutoGainSwitchState(CommonParams.PropTPDParamsValue.GAINSELStatus gainselStatus) {
                                                    Log.i(TAG, "onAutoGainSwitchState->" + gainselStatus.getValue());
                                                }

                                                @Override
                                                public void onAutoGainSwitchResult(CommonParams.PropTPDParamsValue.GAINSELStatus gainselStatus, int result) {
                                                    Log.i(TAG,
                                                            "onAutoGainSwitchResult->" + gainselStatus.getValue() +
                                                                    " result=" + result);
                                                }
                                            });
                                }
                                // 
                                if (auto_over_portect) {
                                    ircmd.avoidOverexposure(false, gainStatus, temperatureSrc, imageRes,
                                            low_gain_over_temp_data,
                                            high_gain_over_temp_data, pixel_above_prop, switch_frame_cnt,
                                            close_frame_cnt,
                                            new AvoidOverexposureCallback() {
                                                @Override
                                                public void onAvoidOverexposureState(boolean avoidOverexpol) {
                                                    Log.i(TAG,
                                                            "onAvoidOverexposureState->avoidOverexpol=" + avoidOverexpol);
                                                }
                                            });
                                }
                            }
                        }
                    } else {
                        /*
                         * infrared
                         * copyinfraredimagearray
                         * 90，,infraredImageThread。
                         */
                        System.arraycopy(frame, 0, imageSrc, 0, imageOrTempDataLength);
                    }
                    if (iFrameCallBackListener != null) {
                        iFrameCallBackListener.updateData();
                    }
                }
                if (isFirstFrame && iFrameReadListener != null) {
                    iFrameReadListener.frameRead();
                    isFirstFrame = false;
                }
            }

        };
    }

    public void setRotate(int rotateInt) {
        this.rotateInt = rotateInt;
    }

    public void setImageSrc(byte[] image) {
        this.imageSrc = image;
    }

    public void setTemperatureSrc(byte[] temperatureSrc) {
        this.temperatureSrc = temperatureSrc;
    }

    public void setFrameReady(boolean frameReady) {
        isFrameReady = frameReady;
    }

    public boolean isRestart() {
        return isRestart;
    }

    public void setRestart(boolean restart) {
        isRestart = restart;
    }

    /**
     * init UVCCamera
     */
    private void initUVCCamera() {
        Log.i(TAG, "uvcCamera create");
        uvcCamera = new ConcreateUVCBuilder()
                .setUVCType(UVCType.USB_UVC)
                .build();
        /**
         * 
         * ，，，configuration
         */
        uvcCamera.setDefaultBandwidth(0.5F);
    }

    /**
     * init IRCMD
     * list，，cmdSDK
     */
    private void initIRCMD() {
        if (uvcCamera != null) {
            ircmd = new ConcreteIRCMDBuilder()
                    .setIrcmdType(IRCMDType.USB_IR_256_384)
                    .setIdCamera(uvcCamera.getNativePtr())
                    .build();
            //ircmd，initialize，initialize，
            //setCreateResultCallback
            if (ircmd == null) {
                EventBus.getDefault().post(new PreviewComplete());
                return;
            }
            if (mConnectCallback != null) {
                mConnectCallback.onIRCMDCreate(ircmd);
            }
        }
    }

    /**
     *
     */
    public void registerUSB() {
        if (mUSBMonitor != null) {
            mUSBMonitor.register();
        }
    }

    /**
     *
     */
    public void unregisterUSB() {
        if (mUSBMonitor != null) {
            mUSBMonitor.unregister();
        }
    }

    private void openUVCCamera(USBMonitor.UsbControlBlock ctrlBlock) {
        Log.i(TAG, "openUVCCamera");
        if (ctrlBlock.getProductId() == 0x3901) {
            if (syncimage != null) {
                syncimage.type = 1;
            }
        }
        if (uvcCamera == null) {
            initUVCCamera();
        }
        // uvc
        if (uvcCamera.openUVCCamera(ctrlBlock) == 0) {
            // UVCCamera
            if (mConnectCallback != null && uvcCamera != null) {
                mConnectCallback.onCameraOpened(uvcCamera);
            }
        }
    }

    /**
     * list
     */
    private List<CameraSize> getAllSupportedSize() {
        List<CameraSize> previewList = new ArrayList<>();
        if (uvcCamera != null) {
            Log.w(TAG, "getSupportedSize = " + uvcCamera.getSupportedSize());
            previewList = uvcCamera.getSupportedSizeList();
        }
        Log.w(TAG, "getSupportedSize = " + uvcCamera.getSupportedSize());
        for (CameraSize size : previewList) {
            Log.i(TAG, "SupportedSize : " + size.width + " * " + size.height);
        }
        return previewList;
    }

    /**
     * infrared，PIDPID
     *
     * @param devpid
     * @return
     */
    private boolean isIRpid(int devpid) {
        for (int x : pids) {
            if (x == devpid) return true;
        }
        return false;
    }

    /**
     * 
     */
    private void startPreview() {
        if (ircmd == null) {
            return;
        }
        Log.i(TAG, "startPreview isRestart : " + isRestart + " defaultDataFlowMode : " + defaultDataFlowMode);
        uvcCamera.setOpenStatus(true);
        uvcCamera.setFrameCallback(iFrameCallback);
        uvcCamera.onStartPreview();

        if (CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT == defaultDataFlowMode ||
                CommonParams.DataFlowMode.IMAGE_OUTPUT == defaultDataFlowMode) {
            /*
             * infrared+infrared
             * YUV422
             */
            Log.i(TAG, "defaultDataFlowMode = IMAGE_AND_TEMP_OUTPUT or IMAGE_OUTPUT");
            // YUV
            setFrameReady(false);
            if (isRestart) {
                // 1.（，y16）
                if (ircmd.stopPreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0) == 0) {
                    Log.i(TAG, "stopPreview complete");
                    // 2. ，settings256*384
                    if (ircmd.startPreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                            CommonParams.StartPreviewSource.SOURCE_SENSOR,
                            ScreenUtils.getPreviewFPSByDataFlowMode(defaultDataFlowMode),
                            CommonParams.StartPreviewMode.VOC_DVP_MODE,
                            defaultDataFlowMode) == 0) {
                        Log.i(TAG, "startPreview complete");
                        handleStartPreviewComplete();
                    }
                } else {
                    Log.e(TAG, "stopPreview error");
                }
            } else {
                handleStartPreviewComplete();
            }
        } else {
            /*
             * 
             */
            // Y16(TNR，ISP)
            setFrameReady(false);
            if (isRestart) {
                if (ircmd.stopPreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0) == 0) {
                    Log.i(TAG, "Test Data");
                    if (ircmd.startPreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                            CommonParams.StartPreviewSource.SOURCE_SENSOR,
                            ScreenUtils.getPreviewFPSByDataFlowMode(defaultDataFlowMode),
                            CommonParams.StartPreviewMode.VOC_DVP_MODE, defaultDataFlowMode) == 0) {
                        Log.i(TAG, "Test Data");
                        try {
                            /*
                             * ，5840，black hotwhite hot
                             * 
                             */
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (ircmd.startY16ModePreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                                FileUtil.getY16SrcTypeByDataFlowMode(defaultDataFlowMode)) == 0) {
                            handleStartPreviewComplete();
                        } else {
                            Log.e(TAG, "Test Data");
                        }
                    } else {
                        Log.e(TAG, "Test Data");
                    }
                } else {
                    Log.e(TAG, "Test Data");
                }
            } else {
                /*
                 * ISP
                 * infrared+TNR,25Hz
                 */
                boolean isTempReplacedWithTNREnabled = ircmd.isTempReplacedWithTNREnabled(DeviceType.P2);
                Log.i(TAG,
                        "defaultDataFlowMode = others isTempReplacedWithTNREnabled = " + isTempReplacedWithTNREnabled);
                if (isTempReplacedWithTNREnabled) {
                    /*
                     *  infrared+TNR 
                     */
                    // P2，startY16ModePreview
//                    if (ircmd.startY16ModePreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0,
//                            FileUtil.getY16SrcTypeByDataFlowMode(defaultDataFlowMode)) == 0) {
//                        handleStartPreviewComplete();
//                    } else {
//                        Log.e(TAG, "startY16ModePreview error");
//                    }
                    // M2，startPreview，startY16ModePreview
                    if (ircmd.stopPreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0) == 0) {
                        Log.i(TAG, "stopPreview complete infrared+TNR");
                        if (ircmd.startPreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                                CommonParams.StartPreviewSource.SOURCE_SENSOR,
                                ScreenUtils.getPreviewFPSByDataFlowMode(CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT),
                                CommonParams.StartPreviewMode.VOC_DVP_MODE,
                                CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT) == 0) {
                            Log.i(TAG, "startPreview complete infrared+TNR");
                            try {
                                /*
                                 * ，5840，black hotwhite hot
                                 * 
                                 */
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (ircmd.startY16ModePreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                                    FileUtil.getY16SrcTypeByDataFlowMode(CommonParams.DataFlowMode.TNR_OUTPUT)) == 0) {
                                handleStartPreviewComplete();
                            } else {
                                Log.e(TAG, "startY16ModePreview error infrared+TNR");
                            }
                        } else {
                            Log.e(TAG, "startPreview error infrared+TNR");
                        }
                    } else {
                        Log.e(TAG, "stopPreview error infrared+TNR");
                    }
                } else {
                    /*
                     * TNR 
                     * YUV，Y16，，
                     * ，Y16，Y16，
                     */
                    //  startY16ModePreview ，y16
                    if (ircmd.stopPreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0) == 0) {
                        Log.i(TAG, "Test Data");
                        if (ircmd.startPreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                                CommonParams.StartPreviewSource.SOURCE_SENSOR,
                                ScreenUtils.getPreviewFPSByDataFlowMode(defaultDataFlowMode),
                                CommonParams.StartPreviewMode.VOC_DVP_MODE, defaultDataFlowMode) == 0) {
                            Log.i(TAG, "Test Data");
                            try {
                                /*
                                 * ，5840，black hotwhite hot
                                 * 
                                 */
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (ircmd.startY16ModePreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                                    FileUtil.getY16SrcTypeByDataFlowMode(defaultDataFlowMode)) == 0) {
                                handleStartPreviewComplete();
                            } else {
                                Log.e(TAG, "Test Data");
                            }
                        } else {
                            Log.e(TAG, "Test Data");
                        }
                    } else {
                        Log.e(TAG, "Test Data");
                    }
                }
            }
        }
    }

    /**
     *
     */
    public void stopPreview() {
        Log.i(TAG, "stopPreview");
        if (uvcCamera != null) {
            if (uvcCamera.getOpenStatus()) {
                uvcCamera.onStopPreview();
            }
            uvcCamera.setFrameCallback(null);
            final UVCCamera camera;
            camera = uvcCamera;
            uvcCamera = null;
            //IRCMD
            if (ircmd != null) {
                ircmd.onDestroy();
                ircmd = null;
            }

            SystemClock.sleep(200);

            //initIRISPModule  destroyIRISPModule，
            camera.onDestroyPreview();

        }
    }

    /**
     *
     */
    private void handleStartPreviewComplete() {
        // kt,bt,nuc_tsettings，
        new Thread(() -> EventBus.getDefault().post(new PreviewComplete())).start();
    }

}
