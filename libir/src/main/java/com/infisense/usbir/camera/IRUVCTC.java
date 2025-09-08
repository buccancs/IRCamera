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
     * [Chinese text]
     */
    public class IRUVCTC {
    private static final String TAG = "IRUVC_DATA";
    private final IFrameCallback iFrameCallback;
    public UVCCamera uvcCamera;
    private IRCMD ircmd;
    //
    private final USBMonitor mUSBMonitor;
    private final ConnectCallback mConnectCallback; // usb[Chinese text]
    private byte[] imageSrc;
    private byte[] temperatureSrc;
    private final int imageOrTempDataLength = 256 * 192 * 2; // [Chinese text]temperature[Chinese text]
    private final SynchronizedBitmap syncimage;
    /**
     * [Chinese text]switch
     */
    private final LibIRProcess.AutoGainSwitchInfo_t auto_gain_switch_info = new LibIRProcess.AutoGainSwitchInfo_t();
    private final LibIRProcess.GainSwitchParam_t gain_switch_param = new LibIRProcess.GainSwitchParam_t();
    private int rotateInt = 0;

    // [Chinese text], [Chinese text], [Chinese text]
    private boolean isFrameReady = true;
    // [Chinese text]
    private final CommonParams.GainStatus gainStatus = CommonParams.GainStatus.HIGH_GAIN;
    private final byte[] temperatureTemp = new byte[imageOrTempDataLength];
    // [Chinese text]+TNR[Chinese text]
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

        /**
     * IFrameCallBackListener class.
     *
     * Provides iframecallbacklistener functionality.
     */
    public interface IFrameCallBackListener {
        void updateData();
    }

        /**
     * IFrameReadListener class.
     *
     * Provides iframereadlistener functionality.
     */
    public interface IFrameReadListener {
        void frameRead();
    }

        /**
     * @param cameraWidth     cameraWidth:256,cameraHeight:384,[Chinese text]+temperature
     *                        cameraWidth:256,cameraHeight:192,[Chinese text]
     *                        cameraWidth:256,cameraHeight:192,([Chinese text]startY16ModePreview, [Chinese text]Y16_MODE_TEMPERATURE)temperature
     * @param connectCallback Settingsusb[Chinese text]
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
        // [Chinese text]: USBMonitor[Chinese text]line[Chinese text]in progress[Chinese text]
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

                        // [Chinese text]
                        List<CameraSize> previewList = getAllSupportedSize();
                        for (CameraSize size : previewList) {
                            Log.i(TAG, "SupportedSize : " + size.width + " * " + size.height);
                        }

                        // [Chinese text], [Chinese text], [Chinese text]cmd[Chinese text]SDK
                        initIRCMD();

                        if (ircmd != null) {
                            Log.d(TAG, "startPreview");
                            // [Chinese text], [Chinese text]Settings[Chinese text]high([Chinese text], [Chinese text])
                            // [Chinese text]openUVCCamera[Chinese text]in progress[Chinese text], [Chinese text]
                            isTempReplacedWithTNREnabled = ircmd.isTempReplacedWithTNREnabled(DeviceType.P2);
                            if (isTempReplacedWithTNREnabled) {
                                // [Chinese text]+TNR[Chinese text], [Chinese text], [Chinese text]
                                if (uvcCamera != null) {
                                    uvcCamera.setUSBPreviewSize(cameraWidth, cameraHeight * 2);
                                }
                            } else {
                                // [Chinese text]TNR[Chinese text]
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
         * [Chinese text]switch[Chinese text], [Chinese text]switch[Chinese text], [Chinese text]implement
         */
        // [Chinese text]switch[Chinese text]auto gain switch parameter
        gain_switch_param.above_pixel_prop = 0.1f;    // forhigh -> low gain,[Chinese text]
        gain_switch_param.above_temp_data = (int) ((130 + 273.15) * 16 * 4); // forhigh -> low gain,high[Chinese text]low[Chinese text]switch[Chinese text]temperature
        gain_switch_param.below_pixel_prop = 0.95f;   // forlow -> high gain,[Chinese text]
        gain_switch_param.below_temp_data = (int) ((110 + 273.15) * 16 * 4);// forlow -> high gain,low[Chinese text]high[Chinese text]switch[Chinese text]temperature
        auto_gain_switch_info.switch_frame_cnt = 5 * 15; // [Chinese text]switch([Chinese text]15[Chinese text], [Chinese text]5 * 15[Chinese text]5[Chinese text])
        auto_gain_switch_info.waiting_frame_cnt = 7 * 15;// [Chinese text]switch[Chinese text], [Chinese text]switch[Chinese text]([Chinese text]15[Chinese text], [Chinese text]7 * 15[Chinese text]7[Chinese text])
        // [Chinese text]over_portect parameter
        int low_gain_over_temp_data = (int) ((550 + 273.15) * 16 * 4); // low[Chinese text]temperature
        int high_gain_over_temp_data = (int) ((150 + 273.15) * 16 * 4); // high[Chinese text]temperature
        float pixel_above_prop = 0.02f;// [Chinese text]
        int switch_frame_cnt = 7 * 15;// [Chinese text]([Chinese text]15[Chinese text], [Chinese text]7 * 15[Chinese text]7[Chinese text])
        int close_frame_cnt = 10 * 15;// [Chinese text], [Chinese text]([Chinese text]15[Chinese text], [Chinese text]10 * 15[Chinese text]10[Chinese text])

        LibIRProcess.ImageRes_t imageRes = new LibIRProcess.ImageRes_t();
        imageRes.height = (char) (dataFlowMode == CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT ? cameraHeight / 2
                : cameraHeight);
        imageRes.width = (char) cameraWidth;

        // [Chinese text]
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
                    // [Chinese text], [Chinese text]sensor
                    int length = frame.length - 1;
                    if (frame[length] == 1) {
                        // bad frame
                        EventBus.getDefault().post(new IRMsgEvent(MsgCode.RESTART_USB));
                        return;
                    }
                    if (imageEditTemp != null && imageEditTemp.length >= length) {
                        // [Chinese text]
                        System.arraycopy(frame, 0, imageEditTemp, 0, length);
                    }
//                    try {
//                        byte[] tmpBy = new byte[256*192*2];
//                        System.arraycopy(frame, imageOrTempDataLength, tmpBy, 0,
//                                imageOrTempDataLength);
//                        LibIRTemp tmp = new LibIRTemp(256,192);
//                        tmp.setTempData(tmpBy);
//                        LibIRTemp.TemperatureSampleResult result = tmp.getTemperatureOfRect(new Rect(0, 0, 256,192));

//                    }catch (Exception  e){
//
//                    }
                    if (dataFlowMode == CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT) {
                        /*
                         * [Chinese text]+temperature
                         * copy[Chinese text]image[Chinese text]in progress
                         * [Chinese text]frame[Chinese text]in progress[Chinese text], [Chinese text]temperature[Chinese text], 
                         * [Chinese text]256*384[Chinese text], [Chinese text]256*192[Chinese text], [Chinese text]256*192[Chinese text]temperature[Chinese text], 
                         * [Chinese text]in progress[Chinese text]90[Chinese text], [Chinese text],[Chinese text]ImageThreadin progress[Chinese text]. 
                         */
                        System.arraycopy(frame, 0, imageSrc, 0, imageOrTempDataLength);
                        /*
                         * [Chinese text]temperature[Chinese text]
                         * [Chinese text]in progress, [Chinese text]temperature[Chinese text], [Chinese text], [Chinese text]
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
                                // [Chinese text]switch, [Chinese text]switch
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
                                // [Chinese text]
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
                         * [Chinese text]
                         * copy[Chinese text]image[Chinese text]in progress
                         * [Chinese text]in progress[Chinese text]90[Chinese text], [Chinese text],[Chinese text]ImageThreadin progress[Chinese text]. 
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
         * [Chinese text]
         * [Chinese text], [Chinese text], [Chinese text], [Chinese text]
         */
        uvcCamera.setDefaultBandwidth(0.5F);
    }

    /**
     * init IRCMD
     * [Chinese text], [Chinese text], [Chinese text]cmd[Chinese text]SDK
     */
    private void initIRCMD() {
        if (uvcCamera != null) {
            ircmd = new ConcreteIRCMDBuilder()
                    .setIrcmdType(IRCMDType.USB_IR_256_384)
                    .setIdCamera(uvcCamera.getNativePtr())
                    .build();
            // [Chinese text]ircmd[Chinese text], [Chinese text], [Chinese text], [Chinese text]
            // [Chinese text]message[Chinese text]setCreateResultCallback[Chinese text]
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
        // uvc[Chinese text]
        if (uvcCamera.openUVCCamera(ctrlBlock) == 0) {
            // UVCCamera[Chinese text]
            if (mConnectCallback != null && uvcCamera != null) {
                mConnectCallback.onCameraOpened(uvcCamera);
            }
        }
    }

    /**
     * [Chinese text]
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
     * [Chinese text], [Chinese text]PID[Chinese text]PID[Chinese text]
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
     * [Chinese text]
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
             * [Chinese text]+temperature[Chinese text]
             * YUV422[Chinese text]
             */
            Log.i(TAG, "defaultDataFlowMode = IMAGE_AND_TEMP_OUTPUT or IMAGE_OUTPUT");
            // YUV[Chinese text]
            setFrameReady(false);
            if (isRestart) {
                // 1.[Chinese text]([Chinese text], [Chinese text]y16mode[Chinese text])
                if (ircmd.stopPreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0) == 0) {
                    Log.i(TAG, "stopPreview complete");
                    // 2. [Chinese text], Settings[Chinese text]256*384
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
             * in progress[Chinese text]
             */
            // Y16[Chinese text]([Chinese text]TNR[Chinese text], [Chinese text]ISP[Chinese text])
            setFrameReady(false);
            if (isRestart) {
                if (ircmd.stopPreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0) == 0) {
                    Log.i(TAG, "stopPreview complete in progress[Chinese text] restart");
                    if (ircmd.startPreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                            CommonParams.StartPreviewSource.SOURCE_SENSOR,
                            ScreenUtils.getPreviewFPSByDataFlowMode(defaultDataFlowMode),
                            CommonParams.StartPreviewMode.VOC_DVP_MODE, defaultDataFlowMode) == 0) {
                        Log.i(TAG, "startPreview complete in progress[Chinese text] restart");
                        try {
                            /*
                             * [Chinese text], [Chinese text]5840[Chinese text], [Chinese text]in progress[Chinese text]
                             * [Chinese text]
                             */
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (ircmd.startY16ModePreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                                FileUtil.getY16SrcTypeByDataFlowMode(defaultDataFlowMode)) == 0) {
                            handleStartPreviewComplete();
                        } else {
                            Log.e(TAG, "startY16ModePreview error in progress[Chinese text] restart");
                        }
                    } else {
                        Log.e(TAG, "startPreview error in progress[Chinese text] restart");
                    }
                } else {
                    Log.e(TAG, "stopPreview error in progress[Chinese text] restart");
                }
            } else {
                /*
                 * [Chinese text]ISP[Chinese text]
                 * [Chinese text]+TNR[Chinese text],[Chinese text]25Hz
                 */
                boolean isTempReplacedWithTNREnabled = ircmd.isTempReplacedWithTNREnabled(DeviceType.P2);
                Log.i(TAG,
                        "defaultDataFlowMode = others isTempReplacedWithTNREnabled = " + isTempReplacedWithTNREnabled);
                if (isTempReplacedWithTNREnabled) {
                    /*
                     * [Chinese text] [Chinese text]+TNR [Chinese text]
                     */
                    // [Chinese text]P2[Chinese text], [Chinese text]startY16ModePreview[Chinese text]
//                    if (ircmd.startY16ModePreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0,
//                            FileUtil.getY16SrcTypeByDataFlowMode(defaultDataFlowMode)) == 0) {
//                        handleStartPreviewComplete();
//                    } else {

//                    }
                    // [Chinese text]M2[Chinese text], [Chinese text]startPreview[Chinese text], [Chinese text]startY16ModePreview[Chinese text]
                    if (ircmd.stopPreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0) == 0) {
                        Log.i(TAG, "stopPreview complete [Chinese text]+TNR");
                        if (ircmd.startPreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                                CommonParams.StartPreviewSource.SOURCE_SENSOR,
                                ScreenUtils.getPreviewFPSByDataFlowMode(CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT),
                                CommonParams.StartPreviewMode.VOC_DVP_MODE,
                                CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT) == 0) {
                            Log.i(TAG, "startPreview complete [Chinese text]+TNR");
                            try {
                                /*
                                 * [Chinese text], [Chinese text]5840[Chinese text], [Chinese text]in progress[Chinese text]
                                 * [Chinese text]
                                 */
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (ircmd.startY16ModePreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                                    FileUtil.getY16SrcTypeByDataFlowMode(CommonParams.DataFlowMode.TNR_OUTPUT)) == 0) {
                                handleStartPreviewComplete();
                            } else {
                                Log.e(TAG, "startY16ModePreview error [Chinese text]+TNR");
                            }
                        } else {
                            Log.e(TAG, "startPreview error [Chinese text]+TNR");
                        }
                    } else {
                        Log.e(TAG, "stopPreview error [Chinese text]+TNR");
                    }
                } else {
                    /*
                     * [Chinese text]TNR [Chinese text]
                     * [Chinese text]YUV[Chinese text], [Chinese text]mode[Chinese text]Y16in progress[Chinese text], [Chinese text], [Chinese text]in progress[Chinese text]
                     * [Chinese text], [Chinese text]mode[Chinese text]Y16mode, [Chinese text]Y16mode, [Chinese text]
                     */
                    // [Chinese text] startY16ModePreview in progress[Chinese text], [Chinese text]y16
                    if (ircmd.stopPreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0) == 0) {
                        Log.i(TAG, "stopPreview complete [Chinese text]TNR");
                        if (ircmd.startPreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                                CommonParams.StartPreviewSource.SOURCE_SENSOR,
                                ScreenUtils.getPreviewFPSByDataFlowMode(defaultDataFlowMode),
                                CommonParams.StartPreviewMode.VOC_DVP_MODE, defaultDataFlowMode) == 0) {
                            Log.i(TAG, "startPreview complete [Chinese text]TNR");
                            try {
                                /*
                                 * [Chinese text], [Chinese text]5840[Chinese text], [Chinese text]in progress[Chinese text]
                                 * [Chinese text]
                                 */
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (ircmd.startY16ModePreview(CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                                    FileUtil.getY16SrcTypeByDataFlowMode(defaultDataFlowMode)) == 0) {
                                handleStartPreviewComplete();
                            } else {
                                Log.e(TAG, "startY16ModePreview error [Chinese text]TNR");
                            }
                        } else {
                            Log.e(TAG, "startPreview error [Chinese text]TNR");
                        }
                    } else {
                        Log.e(TAG, "stopPreview error [Chinese text]TNR");
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
            // IRCMD[Chinese text]
            if (ircmd != null) {
                ircmd.onDestroy();
                ircmd = null;
            }

            SystemClock.sleep(200);

            // initIRISPModule [Chinese text] destroyIRISPModule[Chinese text], [Chinese text]
            camera.onDestroyPreview();

        }
    }

    /**
     *
     */
    private void handleStartPreviewComplete() {
        // [Chinese text]kt,bt,nuc_t[Chinese text]Settingstemperature[Chinese text], [Chinese text]operation[Chinese text]
        new Thread(() -> EventBus.getDefault().post(new PreviewComplete())).start();
    }

}
