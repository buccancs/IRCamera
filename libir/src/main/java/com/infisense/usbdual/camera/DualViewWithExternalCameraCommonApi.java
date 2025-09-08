package com.infisense.usbdual.camera;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.energy.commonlibrary.view.SurfaceNativeWindow;
import com.energy.iruvc.dual.ConcreateDualBuilder;
import com.energy.iruvc.dual.DualType;
import com.energy.iruvc.dual.DualUVCCamera;
import com.energy.iruvc.sdkisp.LibIRParse;
import com.energy.iruvc.sdkisp.LibIRProcess;
import com.energy.iruvc.utils.AutoGainSwitchCallback;
import com.energy.iruvc.utils.AvoidOverexposureCallback;
import com.energy.iruvc.utils.CommonParams;
import com.energy.iruvc.utils.DualCameraParams;
import com.energy.iruvc.utils.IFrameCallback;
import com.energy.iruvc.utils.IIRFrameCallback;
import com.energy.iruvc.uvc.UVCCamera;
import com.infisense.usbdual.Const;
import com.infisense.usbir.utils.OpencvTools;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Locale;

import static com.infisense.usbdual.camera.IFrameData.FRAME_LEN;

/**
 * Created by fengjibo on 2023/9/20.
 */
public class DualViewWithExternalCameraCommonApi extends BaseDualView {

    private final String TAG = "DualViewWithExternalCameraCommonApi";
    private DualUVCCamera dualUVCCamera;
    private final IFrameCallback iFrameCallback;
    private final IIRFrameCallback irFrameCallback;
    public SurfaceView cameraview;
    public boolean isRun = true;
    // [Chinese text]
    public int count = 0;
    private long timestart = 0;
    private double fps = 0;
    // [Chinese text]switch[Chinese text]auto_gain_switch[Chinese text]auto_gain_switch_running[Chinese text]true
    public boolean auto_gain_switch = false;
    public boolean auto_gain_switch_running = true;
    public boolean auto_over_protect = false;
    private LibIRProcess.AutoGainSwitchInfo_t auto_gain_switch_info = new LibIRProcess.AutoGainSwitchInfo_t();
    private LibIRProcess.GainSwitchParam_t gain_switch_param = new LibIRProcess.GainSwitchParam_t();
    private CommonParams.GainStatus gainStatus = CommonParams.GainStatus.HIGH_GAIN;
    public Bitmap bitmap;
    public Bitmap supIROlyNoFusionBitmap;
    public Bitmap supMixBitmap;
    public Bitmap supIROlyBitmap;

    private boolean valid = false;
    private Bitmap mScaledBitmap;
    private Handler handler;

    // [Chinese text]IRISP[Chinese text]
    private boolean isUseIRISP = false;

    private SurfaceNativeWindow mSurfaceNativeWindow;
    private Surface mSurface;

    private DualCameraParams.FusionType mCurrentFusionType;
    private boolean firstFrame = false;
    private byte[] irRGBAData;// [Chinese text] 192 *256
    private byte[] preIrData;// [Chinese text] 192 *256 * 2
    private byte[] preTempData;// [Chinese text]temperature[Chinese text] 192 *256 * 2
    private byte[] preIrARGBData;// [Chinese text]ARGB[Chinese text] 192 * 256 * 4
    public byte[] frameData = new byte[FRAME_LEN];// [Chinese text]

    public byte[] frameIrAndTempData = new byte[192 * 256 * 4];

    public int rotate = 180; // [Chinese text], [Chinese text]180[Chinese text]

    private volatile boolean isOpenAmplify = false;
    private final byte[] amplifyMixRotateArray;// [Chinese text] 640 * MULTIPLE * 480 * MULTIPLE
    private final byte[] amplifyIRRotateArray;// [Chinese text] 256 * MULTIPLE * 192 * MULTIPLE

    public static final int MULTIPLE = 2;

    public boolean isOpenAmplify() {
        return isOpenAmplify;
    }

    public void setOpenAmplify(boolean openAmplify) {
        isOpenAmplify = openAmplify;
    }

        /**
     * @param handler
     */
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

        /**
     * @param cameraview
     * @param irUVCCamera
     * @param dataFlowMode
     * @param vlCameraWidth
     * @param vlCameraHeight
     * @param irCameraWidth
     * @param irCameraHeight
     * @param dualCameraWidth
     * @param dualCameraHeight
     */
    public DualViewWithExternalCameraCommonApi(SurfaceView cameraview, UVCCamera irUVCCamera,
                                               CommonParams.DataFlowMode dataFlowMode,
                                               int irCameraWidth, int irCameraHeight, int vlCameraWidth, int vlCameraHeight,
                                               int dualCameraWidth, int dualCameraHeight,
                                               boolean isUseIRISP,int rotate,IIRFrameCallback irFrameCallback) {
        Const.CAMERA_WIDTH = vlCameraWidth;
        Const.CAMERA_HEIGHT = vlCameraHeight;
        Const.IR_WIDTH = irCameraHeight;
        Const.IR_HEIGHT = irCameraWidth;
        Const.VL_WIDTH = vlCameraHeight;
        Const.VL_HEIGHT = vlCameraWidth;
        Const.DUAL_WIDTH = dualCameraWidth;
        Const.DUAL_HEIGHT = dualCameraHeight;
        this.rotate = rotate;
        onFrameCallbacks = new ArrayList<>();
        fusionLength = Const.DUAL_WIDTH * Const.DUAL_HEIGHT * 4;
        irSize = Const.IR_WIDTH * Const.IR_HEIGHT;
        vlSize = Const.VL_WIDTH * Const.VL_HEIGHT * 3;
        remapTempSize = Const.DUAL_WIDTH * Const.DUAL_HEIGHT * 2;
        remapTempData = new byte[Const.DUAL_WIDTH * Const.DUAL_HEIGHT * 2];
        mixData = new byte[fusionLength];
        normalTempData = new byte[irSize * 2];
        irData = new byte[irSize * 2];
        vlData = new byte[vlSize];
        vlARGBData = new byte[fusionLength];
        amplifyMixRotateArray = new byte[fusionLength * MULTIPLE * MULTIPLE];
        amplifyIRRotateArray = new byte[irData.length * MULTIPLE * MULTIPLE];
        this.irFrameCallback = irFrameCallback;

        this.isUseIRISP = isUseIRISP;
        this.cameraview = cameraview;
        bitmap = Bitmap.createBitmap(dualCameraWidth, dualCameraHeight, Bitmap.Config.ARGB_8888);
        supIROlyNoFusionBitmap = Bitmap.createBitmap(irCameraWidth * MULTIPLE,
                irCameraHeight * MULTIPLE, Bitmap.Config.ARGB_8888);
        supIROlyBitmap = Bitmap.createBitmap(irCameraWidth,
                irCameraHeight, Bitmap.Config.ARGB_8888);
        supMixBitmap = Bitmap.createBitmap(Const.DUAL_WIDTH * MULTIPLE,
                Const.DUAL_HEIGHT * MULTIPLE, Bitmap.Config.ARGB_8888);
        // DualUVCCamera [Chinese text]
        ConcreateDualBuilder concreateDualBuilder = new ConcreateDualBuilder();
        dualUVCCamera = concreateDualBuilder
                .setDualType(DualType.USB_DUAL)
                // [Chinese text]high[Chinese text]
                .setIRSize(Const.IR_WIDTH, Const.IR_HEIGHT)
                .setVLSize(Const.VL_WIDTH, Const.VL_HEIGHT)
                .setDualSize(Const.DUAL_HEIGHT, Const.DUAL_WIDTH)
                .setDataFlowMode(dataFlowMode)
                .setPreviewCameraStyle(CommonParams.PreviewCameraStyle.EXTERNAL_CAMERA)
                .setDeviceStyle(CommonParams.DeviceStyle.ALL_IN_ONE)
                .setUseDualGPU(false)
                /**
                 * start[Chinese text]line[Chinese text]
                 * [Chinese text]CPUlow[Chinese text], [Chinese text]line[Chinese text]
                 * setUseDualGPU [Chinese text]true GPU[Chinese text]
                 */
                .setMultiThreadHandleDualEnable(false)
                .build();
        DualCameraParams.TypeLoadParameters rotateT = DualCameraParams.TypeLoadParameters.ROTATE_0;
        if (rotate == 0) {
            rotateT = DualCameraParams.TypeLoadParameters.ROTATE_0;
        } else if (rotate == 90) {
            rotateT = DualCameraParams.TypeLoadParameters.ROTATE_90;
        } else if (rotate == 180) {
            rotateT = DualCameraParams.TypeLoadParameters.ROTATE_180;
        } else if (rotate == 270) {
            rotateT = DualCameraParams.TypeLoadParameters.ROTATE_270;
        }
        dualUVCCamera.setImageRotate(rotateT);
        dualUVCCamera.addIrUVCCamera(irUVCCamera);
        mSurfaceNativeWindow = new SurfaceNativeWindow();

        /**
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
        int high_gain_over_temp_data = (int) ((110 + 273.15) * 16 * 4); // high[Chinese text]temperature
        float pixel_above_prop = 0.02f;// [Chinese text]
        int switch_frame_cnt = 7 * 15;// [Chinese text]([Chinese text]15[Chinese text], [Chinese text]7 * 15[Chinese text]7[Chinese text])
        int close_frame_cnt = 10 * 15;// [Chinese text], [Chinese text]([Chinese text]15[Chinese text], [Chinese text]10 * 15[Chinese text]10[Chinese text])

        LibIRProcess.ImageRes_t imageRes = new LibIRProcess.ImageRes_t();
        imageRes.height = (char) (192);
        imageRes.width = (char) 256;

        irRGBAData = new byte[irSize * 4];
        preIrData = new byte[irSize * 2];// [Chinese text] 192 *256 * 2
        preTempData = new byte[irSize * 2];// [Chinese text]temperature[Chinese text] 192 *256 * 2
        preIrARGBData = new byte[irSize * 2 * 2];;// [Chinese text]ARGB[Chinese text] 192 * 256 * 4
        iFrameCallback = new IFrameCallback() {
            /**
             * frame [Chinese text] (CPU)
             * frame [Chinese text] dualwidth * dualHeight * 4 + irWidth * irHeight * 2 + irWidth * irHeight * 2 + dualwidth *
             * dualHeight * 2 + vlWidth * vlHeight * 3 + dualwidth * dualHeight * 4
             * [Chinese text]
             * mixData [Chinese text], [Chinese text]ARGB, [Chinese text]dualwidth * dualHeight * 4
             * irData [Chinese text], [Chinese text]Y16, [Chinese text]irWidth * irHeight * 2
             * tempData [Chinese text]temperature[Chinese text], [Chinese text]Y16, [Chinese text]irWidth * irHeight * 2
             * remapTempData [Chinese text]temperature[Chinese text] [Chinese text]YUV422 dualwidth * dualHeight * 2
             * vlData [Chinese text]visible[Chinese text] [Chinese text]RGB24 vlWidth * vlHeight * 3
             * vlARGBData [Chinese text]visible[Chinese text] dualwidth * dualHeight * 4(only[Chinese text]in progress[Chinese text]mode[Chinese text])
             * [Chinese text]in progress[Chinese text]modeScreenFusion:mixData [Chinese text], [Chinese text]ARGB, [Chinese text]dualwidth * dualHeight * 4
             * [Chinese text]mode[Chinese text]IROnlyNoFusion, [Chinese text]irData[Chinese text]tempData,[Chinese text]
             */
            /**
             * frame [Chinese text] (GPU)
             * frame [Chinese text] dualwidth * dualHeight * 4 + irWidth * irHeight * 2 + irWidth * irHeight * 2 + dualwidth *
             * dualHeight * 2 + vlWidth * vlHeight * 4
             * [Chinese text]
             * mixData [Chinese text], [Chinese text]ARGB, [Chinese text]dualwidth * dualHeight * 4
             * irData [Chinese text], [Chinese text]Y16, [Chinese text]irWidth * irHeight * 2
             * tempData [Chinese text]temperature[Chinese text], [Chinese text]Y16, [Chinese text]irWidth * irHeight * 2
             * remapTempData [Chinese text]temperature[Chinese text] [Chinese text]YUV422 dualwidth * dualHeight * 2
             * vlData [Chinese text]visible[Chinese text] [Chinese text]ARGB vlWidth * vlHeight * 4
             * [Chinese text]in progress[Chinese text]modeScreenFusion:mixData [Chinese text], [Chinese text]ARGB, [Chinese text]dualwidth * dualHeight * 4
             * [Chinese text]mode[Chinese text]IROnlyNoFusion, [Chinese text]irData[Chinese text]tempData,[Chinese text]
             */
            @Override
            public void onFrame(byte[] frame) {
                if (frame.length == 1) {
                    if (handler != null) {
                        handler.sendEmptyMessage(Const.RESTART_USB);
                    }
                    Log.d(TAG, "RESTART_USB");
                    return;
                }
                // [Chinese text]
                count++;
                if (count == 100) {
                    count = 0;
                    long currentTimeMillis = System.currentTimeMillis();
                    if (timestart != 0) {
                        long timeuse = currentTimeMillis - timestart;
                        fps = 100 * 1000 / (timeuse + 0.0);
                    }
                    timestart = currentTimeMillis;
                    Log.d(TAG, "frame.length = " + frame.length + " fps=" + String.format(Locale.US, "%.1f", fps) +
                            " dataFlowMode = " + dataFlowMode);
                }
                System.arraycopy(frame, 0, mixData, 0, fusionLength);
                System.arraycopy(frame, fusionLength, irData, 0, irSize * 2);
                System.arraycopy(frame, fusionLength + irSize * 2, normalTempData, 0, irSize * 2);

                System.arraycopy(frame, fusionLength + irSize * 4, remapTempData, 0,
                        Const.DUAL_WIDTH * Const.DUAL_HEIGHT * 2);

                System.arraycopy(frame, fusionLength + irSize * 4 + Const.DUAL_WIDTH * Const.DUAL_HEIGHT * 2, vlData,
                        0, vlSize);
                System.arraycopy(frame, 0, frameData, 0, FRAME_LEN); // [Chinese text]
                // [Chinese text]temperature[Chinese text]
                System.arraycopy(frame, dualCameraWidth*dualCameraHeight*4, frameIrAndTempData, 0, frameIrAndTempData.length);

                // [Chinese text]in progress[Chinese text]modeScreenFusion:mixData [Chinese text], [Chinese text]ARGB, [Chinese text]dualwidth * dualHeight * 4
//                if (mCurrentFusionType == DualCameraParams.FusionType.ScreenFusion) {
//                    System.arraycopy(frame, fusionLength + irSize * 4 + remapTempSize + vlSize, vlARGBData, 0,
//                            fusionLength);
//                }

                // [Chinese text]IROnlyNoFusionmode, [Chinese text]temperature[Chinese text], [Chinese text]256*192
                if (mCurrentFusionType == DualCameraParams.FusionType.IROnlyNoFusion) {
                    for (OnFrameCallback onFrameCallback : onFrameCallbacks) {
                        onFrameCallback.onFame(mixData, normalTempData, fps);
                    }
                } else {
                    for (OnFrameCallback onFrameCallback : onFrameCallbacks) {
                        onFrameCallback.onFame(mixData, remapTempData, fps);
                    }
                }

                mSurface = cameraview.getHolder().getSurface();

                if (mCurrentFusionType == DualCameraParams.FusionType.IROnlyNoFusion) {
                    LibIRParse.converyArrayYuv422ToARGB(irData, Const.IR_WIDTH * Const.IR_HEIGHT, irRGBAData);
                    if (isOpenAmplify){
                        OpencvTools.supImage(irData,Const.IR_HEIGHT,Const.IR_WIDTH, amplifyIRRotateArray);
                        if (mSurface != null) {
                            mSurfaceNativeWindow.onDrawFrame(mSurface, amplifyIRRotateArray,
                                    Const.IR_WIDTH * MULTIPLE,
                                    Const.IR_HEIGHT * MULTIPLE);
                        }
                    }else {
                        if (mSurface != null) {
                            mSurfaceNativeWindow.onDrawFrame(mSurface, irRGBAData, Const.IR_HEIGHT, Const.IR_WIDTH);
                        }
                    }
                }else {
                    if (isOpenAmplify){
                        if (mCurrentFusionType == DualCameraParams.FusionType.IROnly){
                            OpencvTools.supImageMix(mixData,Const.DUAL_HEIGHT,Const.DUAL_WIDTH, mixData);
                            if (mSurface != null) {
                                mSurfaceNativeWindow.onDrawFrame(mSurface, mixData, Const.DUAL_WIDTH, Const.DUAL_HEIGHT);
                            }
                        }else {
                            OpencvTools.supImage(mixData,Const.DUAL_HEIGHT,Const.DUAL_WIDTH, amplifyMixRotateArray);
                            if (mSurface != null) {
                                mSurfaceNativeWindow.onDrawFrame(mSurface, amplifyMixRotateArray,
                                        Const.DUAL_WIDTH * MULTIPLE,
                                        Const.DUAL_HEIGHT * MULTIPLE);
                            }
                        }
                    }else {
                        if (mSurface != null) {
                            mSurfaceNativeWindow.onDrawFrame(mSurface, mixData, Const.DUAL_WIDTH, Const.DUAL_HEIGHT);
                        }
                    }
                }

                if (!isUseIRISP && !firstFrame) {
                    firstFrame = true;
                    if (handler != null) {
                        handler.sendEmptyMessage(Const.HIDE_LOADING);
                    }
                }

//                if (saveData) {
//                    saveData = false;
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            FileUtil.saveByteFile(cameraview.getContext(), mixData, "mix");
//                            FileUtil.saveByteFile(cameraview.getContext(), remapTempData, "remap_temp");
//                            FileUtil.saveByteFile(cameraview.getContext(), irData, "ir_data");
//                            FileUtil.saveByteFile(cameraview.getContext(), normalTempData, "temp_data");
//                            FileUtil.saveByteFile(cameraview.getContext(), vlData, "vl_data");
//                            FileUtil.saveByteFile(cameraview.getContext(), vlARGBData, "vl_argb_data");
//                        }
//                    }).start();
//
//                }
                // [Chinese text]
                // [Chinese text]switch, [Chinese text]switch
                if (dataFlowMode == CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT) {
                    System.arraycopy(frame, fusionLength + irSize * 2, normalTempData, 0, irSize * 2);
                    if (auto_gain_switch && auto_gain_switch_running) {
                        USBMonitorManager.getInstance().getIrcmd().autoGainSwitch(normalTempData, imageRes,
                                auto_gain_switch_info, gain_switch_param, new AutoGainSwitchCallback() {
                                    @Override
                                    public void onAutoGainSwitchState(CommonParams.PropTPDParamsValue.GAINSELStatus gainselStatus) {
                                        Log.d(TAG, "onAutoGainSwitchState = " + gainselStatus.getValue());
                                        auto_gain_switch_running = false;
                                        resetAutoGainInfo();
                                    }

                                    @Override
                                    public void onAutoGainSwitchResult(CommonParams.PropTPDParamsValue.GAINSELStatus gainselStatus, int result) {
                                        Log.d(TAG, "onAutoGainSwitchResult = " + gainselStatus.getValue() + "  result" +
                                                ":" + result);
                                        auto_gain_switch_running = true;
                                    }
                                });
                    }
                    // [Chinese text]
                    if (auto_over_protect) {
                        USBMonitorManager.getInstance().getIrcmd().avoidOverexposure(false, gainStatus,
                                normalTempData, imageRes, low_gain_over_temp_data, high_gain_over_temp_data,
                                pixel_above_prop, switch_frame_cnt, close_frame_cnt, new AvoidOverexposureCallback() {
                                    @Override
                                    public void onAvoidOverexposureState(boolean avoidOverexpol) {
                                        Log.d(TAG, "onAvoidOverexposureState = " + avoidOverexpol);
                                    }
                                });
                    }
                }

            }
        };
    }

    public void resetAutoGainInfo() {
        auto_gain_switch_info.switched_flag = 0;
        auto_gain_switch_info.cur_switched_cnt = 0;
        auto_gain_switch_info.cur_detected_low_cnt = 0;
        auto_gain_switch_info.cur_detected_high_cnt = 0;
    }

        /**
     *
     */
    public void startPreview() {
        /**
         * setIrDataPreHandleEnable [Chinese text]
         * [Chinese text]SettingssetIrFrameCallback
         * [Chinese text]setFusion(HSLFusion)mode[Chinese text], [Chinese text]setIsothermal,Pseudo color, [Chinese text]Pseudo color[Chinese text]setPseudocolor, setCustomPseudocolor[Chinese text]
         */
        switchIrPreDataHandleEnable(true);
        dualUVCCamera.setFrameCallback(iFrameCallback);
        dualUVCCamera.onStartPreview();
        firstFrame = false;
    }

        /**
     * @return
     */
    public DualUVCCamera getDualUVCCamera() {
        return dualUVCCamera;
    }

        /**
     *
     */
    public void stopPreview() {
        dualUVCCamera.setFrameCallback(null);
        dualUVCCamera.onStopPreview();
        SystemClock.sleep(200);
        dualUVCCamera.onDestroy();
    }

    public void switchIrPreDataHandleEnable(boolean enable) {
        dualUVCCamera.setIrDataPreHandleEnable(enable);
        dualUVCCamera.setIrFrameCallback(enable? irFrameCallback : null);
    }

    public byte[] getRemapTempData() {
        return remapTempData;
    }

    public Bitmap getScaledBitmap() {
        if (isOpenAmplify){
            if (mCurrentFusionType == DualCameraParams.FusionType.IROnlyNoFusion){
                // [Chinese text]mode
                supIROlyNoFusionBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(amplifyIRRotateArray, 0,
                        supIROlyNoFusionBitmap.getWidth() * supIROlyNoFusionBitmap.getHeight() * 4));
                mScaledBitmap = Bitmap.createScaledBitmap(supIROlyNoFusionBitmap,
                        ((ViewGroup)cameraview.getParent()).getWidth(),
                        ((ViewGroup)cameraview.getParent()).getHeight(), true);
            }else if (mCurrentFusionType == DualCameraParams.FusionType.IROnly){
                bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(mixData, 0, bitmap.getWidth() * bitmap.getHeight() * 4));
                mScaledBitmap = Bitmap.createScaledBitmap(bitmap, ((ViewGroup)cameraview.getParent()).getWidth(),
                        ((ViewGroup)cameraview.getParent()).getHeight(), true);
            } else {
                supMixBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(mixData, 0, supMixBitmap.getWidth() * supMixBitmap.getHeight() * 4));
                mScaledBitmap = Bitmap.createScaledBitmap(supMixBitmap, ((ViewGroup)cameraview.getParent()).getWidth(),
                        ((ViewGroup)cameraview.getParent()).getHeight(), true);
            }
        }else {
            bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(mixData, 0, bitmap.getWidth() * bitmap.getHeight() * 4));
            mScaledBitmap = Bitmap.createScaledBitmap(bitmap, ((ViewGroup)cameraview.getParent()).getWidth(),
                    ((ViewGroup)cameraview.getParent()).getHeight(), true);
        }
        return mScaledBitmap;
    }

    private boolean saveData = false;

    public void saveData() {
        saveData = true;
    }

    public void setGainStatus(CommonParams.GainStatus gainStatus) {
        this.gainStatus = gainStatus;
    }

    public void setCurrentFusionType(DualCameraParams.FusionType currentFusionType) {
        this.mCurrentFusionType = currentFusionType;
        if (dualUVCCamera != null) {
            dualUVCCamera.setFusion(currentFusionType);
        }
    }
}
