package com.example.thermal_lite.camera;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.Utils;
import com.elvishew.xlog.XLog;
import com.energy.ac020library.IrcamEngine;
import com.energy.ac020library.IrcmdEngine;
import com.energy.ac020library.bean.AutoGainImageRes;
import com.energy.ac020library.bean.AutoGainSwitchCallback;
import com.energy.ac020library.bean.AutoGainSwitchInfo;
import com.energy.ac020library.bean.AutoGainSwitchParam;
import com.energy.ac020library.bean.CommonParams;
import com.energy.ac020library.bean.DevHandleParam;
import com.energy.ac020library.bean.ErrorCode;
import com.energy.ac020library.bean.HandleInitCallback;
import com.energy.ac020library.bean.IIrFrameCallback;
import com.energy.ac020library.bean.InfoLineBean;
import com.energy.ac020library.bean.IrcmdError;
import com.energy.ac020library.bean.UvcHandleParam;

import com.energy.commoncomponent.Const;
import com.energy.commoncomponent.bean.DeviceType;
import com.energy.commoncomponent.bean.RotateDegree;
// Use existing utilities instead of missing commonlibrary utils
import com.infisense.usbir.utils.FileUtil;
import com.blankj.utilcode.util.SPUtils;
import com.energy.commonlibrary.view.SurfaceNativeWindow;
import com.energy.irutilslibrary.LibIRParse;
import com.energy.irutilslibrary.LibIRProcess;
import com.energy.irutilslibrary.LibIRTemp;
import com.energy.irutilslibrary.bean.IRPROCSRCFMTType;
import com.energy.irutilslibrary.bean.LogLevel;
import com.energy.iruvccamera.bean.CameraSize;
import com.energy.iruvccamera.bean.UvcParams;
import com.energy.iruvccamera.usb.USBMonitor;
// BuildConfig import removed - use direct values if needed
import com.example.thermal_lite.IrConst;
import com.example.thermal_lite.ui.activity.IrDisplayActivity;
import com.example.thermal_lite.util.CommonUtil;
import com.infisense.usbir.utils.IRImageHelp;
import com.infisense.usbir.utils.PseudocodeUtils;
import com.topdon.lib.core.bean.AlarmBean;
import com.topdon.lib.ui.widget.LiteSurfaceView;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by fengjibo on 2023/3/17.
 */
public class CameraPreviewManager {

    private final String TAG = "CameraPreviewManager"Test Data"onFrame frame.length = " + length + " onFrame fps=" + String.format("%.1f"Test Data"max temp : " + maxTemperature + " min temp : " + minTemperature);
                    } else if (FRAME_OUT_PUT_FORMAT == CommonParams.FrameOutputFormat.NV12_AND_TEMP_OUTPUT) {
\1gettemperaturedata
                        System.arraycopy(frame, mIrLength + mInfoLength, mTempData, 0, mTempLength);

//                    mLibIRTemp.setTempData(mTempData);
//                    LibIRTemp.TemperatureSampleResult temperatureSampleResult =
//                            mLibIRTemp.getTemperatureOfRect(new Rect(0, 0, mPreviewWidth / 2, mPreviewHeight - 1));
//                    float maxTemperature = temperatureSampleResult.maxTemperature;
//                    float minTemperature = temperatureSampleResult.minTemperature;
//                    Log.d(TAG, "max temp : " + maxTemperature + " mix temp : "Test Data"NV12_AND_TEMP_OUTPUT"Test Data"onAutoGainSwitchState switch");
                        mIrcamEngine.advAutoGainSwitch(mTempData, mAutoGainImageRes, mAutoGainSwitchInfo, mGainSwitchParam, new AutoGainSwitchCallback() {
                            @Override
                            public void onAutoGainSwitchState(int gainselStatus) {
                                Log.d(TAG, "onAutoGainSwitchState : " + gainselStatus);
                            }

                            @Override
                            public void onAutoGainSwitchResult(int gainselStatus, int result) {
                                Log.d(TAG, "onAutoGainSwitchResult : " + gainselStatus);
                                Log.d(TAG, "onAutoGainSwitchResult : " + result);
                            }
                        });
                    }
                }catch (Exception e){
                    XLog.e(TAG,"Test Data"+e.getMessage());
                }
            }
        };
    }

    public void initData() {
\1TextimagedataText
\1TextFrameOutputFormatTextNV12_IMAGE_OUTPUTTextNV12_IMAGE_OUTPUT，mFrameFormatTypeTextFRAME_FORMAT_NV12
\1TextFrameOutputFormatTextYUYV_AND_TEMP_OUTPUTTextYUYV_IMAGE_OUTPUT，mFrameFormatTypeTextFRAME_FORMAT_YUYV

\1Text，Text
        mStreamWidth = IrConst.DEFAULT_STREAM_WIDTH;
        mStreamHeight = IrConst.DEFAULT_STREAM_HEIGHT;

        boolean isDoubleImage = IrConst.DEFAULT_DOUBLE_IMAGE;
        if (isDoubleImage) {
            setFrameOutPutFormat(CommonParams.FrameOutputFormat.YUYV_AND_TEMP_OUTPUT);
        } else {
            setFrameOutPutFormat(CommonParams.FrameOutputFormat.YUYV_IMAGE_OUTPUT);
        }
\1Text
        if (Const.DEVICE_TYPE == DeviceType.DEVICE_TYPE_X3
                || Const.DEVICE_TYPE == DeviceType.DEVICE_TYPE_P2L
                || Const.DEVICE_TYPE == DeviceType.DEVICE_TYPE_X2PRO
                || Const.DEVICE_TYPE == DeviceType.DEVICE_TYPE_TC2C) {
            mInfoDataHeight = 2;
        } else {
            mInfoDataHeight = 0;
        }
        switch (FRAME_OUT_PUT_FORMAT) {
            case YUYV_IMAGE_OUTPUT:
                /**
\1image YUYV； 256*192； framedata（）256*192*2=98304
                 */
                mFrameFormatType = UvcParams.FrameFormatType.FRAME_FORMAT_YUYV;

\1Textprocessing，Text
                mPreviewWidth = mStreamWidth;
                mPreviewHeight = mStreamHeight - mInfoDataHeight;
\1Textinfrareddata
                mIrLength = mPreviewWidth * mPreviewHeight * 2;
                mIrData = new byte[mIrLength];
\1Text
                mInfoLength = mPreviewWidth * mInfoDataHeight * 2;
                mInfoData = new byte[mInfoLength];
                //
                mIrARGBLength = mPreviewWidth * mPreviewHeight * 2 * 2;
                mIrARGBData = new byte[mIrARGBLength];
                mIrYuvData = new byte[mIrLength / 2];
                mIrRotateData = new byte[mIrARGBLength];

                //zeta zoom code
                initZetaZoomData();

                break;
            case YUYV_AND_TEMP_OUTPUT:
                /**
\1image YUYV++temperatureY16； 256*386； framedata（）256*192*2+256*2*2+256*192*2=197632
                 */
                mFrameFormatType = UvcParams.FrameFormatType.FRAME_FORMAT_YUYV;

\1Textprocessing，Text
                if (mShowDoubleImage) {
                    mPreviewWidth = mStreamWidth;
                    mPreviewHeight = mStreamHeight;
                } else {
                    mPreviewWidth = mStreamWidth;
                    mPreviewHeight = (mStreamHeight - mInfoDataHeight) / 2;
                }

\1Textinfrareddata
                mIrLength = mPreviewWidth * mPreviewHeight * 2;
                mIrData = new byte[mIrLength];
                if (!mShowDoubleImage) {
\1Text
                    mInfoLength = mPreviewWidth * mInfoDataHeight * 2;
                    mInfoData = new byte[mInfoLength];
                }

                mIrARGBLength = mPreviewWidth * mPreviewHeight * 2 * 2;
                mIrARGBData = new byte[mIrARGBLength];
                mTempLength = mPreviewWidth * mPreviewHeight * 2;
                mTempData = new byte[mTempLength];
                mIrRotateData = new byte[mIrARGBLength];
                mTempRotateData = new byte[mIrARGBLength];
                break;
            case NV12_IMAGE_OUTPUT:
                /**
\1image NV12
\1 640*512
\1framedata（）640*512*1.5=491520
                 */
                mFrameFormatType = UvcParams.FrameFormatType.FRAME_FORMAT_NV12;

                mPreviewWidth = 640;
                mPreviewHeight = 512;

                mStreamWidth = 640;
                mStreamHeight = 512;

                mIrLength = (int) (mPreviewWidth * mPreviewHeight * 1.5);
                mIrData = new byte[mIrLength];
                mIrARGBLength = mPreviewWidth * mPreviewHeight * 2 * 2;
                mIrARGBData = new byte[mIrARGBLength];

                break;
            case NV12_AND_TEMP_OUTPUT:
                /**
\1image NV12++temperatureY16+Dummy
\1 640*900
\1framedata（）640*512*1.5+640*2*2+640*512*2+640*2*2=1152000
                 */
                mFrameFormatType = UvcParams.FrameFormatType.FRAME_FORMAT_NV12;

                mPreviewWidth = 640;
                mPreviewHeight = 512;

                mStreamWidth = 640;
                mStreamHeight = 1200;

                mIrLength = (int) (mPreviewWidth * mPreviewHeight * 1.5);
                mIrData = new byte[mIrLength];
                mIrARGBLength = mPreviewWidth * mPreviewHeight * 2 * 2;
                mIrARGBData = new byte[mIrARGBLength];
                mTempLength = mPreviewWidth * mPreviewHeight * 2;
                mTempData = new byte[mTempLength];
                break;
            default:
                break;
        }

        Log.i(TAG, "mPreviewWidth = " + mPreviewWidth + " mPreviewHeight = "Test Data"basicVideoStreamContinueResult=" + basicVideoStreamContinueResult);
                    mMainHandler.sendEmptyMessage(IrDisplayActivity.HIDE_LOADING);
                }
            }, 10000);
        } else {
            mMainHandler.sendEmptyMessage(IrDisplayActivity.HIDE_LOADING);
        }
    }

    public Bitmap scaledBitmap(){
        return scaledBitmap(false);
    }
    byte[] tmpData = null;
    public Bitmap scaledBitmap(Boolean isTakePhoto){
        if (tmpData == null){
            tmpData = new byte[mIrARGBLength];
        }
        System.arraycopy(mIrRotateData,0,tmpData,0,mIrARGBLength);
        mPhotoBitmap = Bitmap.createBitmap(mFinalImageWidth, mFinalImageHeight, Bitmap.Config.ARGB_8888);
        if (isTakePhoto){
            System.arraycopy(frameIrAndTempData, 0, takePhotoIrAndTempData, 0, takePhotoIrAndTempData.length);
        }
        mPhotoBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(tmpData));
        return mPhotoBitmap;
    }

    /**
\1getdevice
     *
     * @return
     */
    public List<CameraSize> getAllSupportedSize() {
        return mIrcamEngine.getUsbSupportInfo();
    }

    /**
\1initialize，
     */
    private void initHandleEngine(USBMonitor.UsbControlBlock ctrlBlock, boolean isStartPreview) {
        UvcHandleParam uvcHandleParam = new UvcHandleParam();
        /**
\1setuvccameraparameter
         */
        uvcHandleParam.setCtrlBlock(ctrlBlock);

        int fps = IrConst.DEFAULT_STREAM_FPS;
        /**
\1frame，device，failed
         */
        uvcHandleParam.setFps(fps);

        float bandwidth = SPUtils.getInstance().getFloat(
                IrConst.KEY_DEFAULT_STREAM_BANDWIDTH, IrConst.DEFAULT_STREAM_BANDWIDTH);

        /**
\1
\1，，，configuration
         */
        uvcHandleParam.setBandwidth(bandwidth);

        Log.d(TAG, "initHandleEngine UvcHandleParam = " + uvcHandleParam.toString());

        LibIRProcess.irprocessLogRegister(LogLevel.SDK_LOG_NO_PRINT);
        LibIRProcess.getIRProcessVersion();
        LibIRParse.irparseLogRegister(LogLevel.SDK_LOG_NO_PRINT);
        LibIRParse.getIRParseVersion();
        LibIRTemp.irtempLogRegister(LogLevel.SDK_LOG_NO_PRINT);
        LibIRTemp.getIRTempVersion();

        mIrcamEngine = IrcamEngine.Builder()
                .setLogLevel(CommonParams.LogLevel.SDK_LOG_DEBUG)
                .setStreamWidth(mStreamWidth)
                .setStreamHeight(mStreamHeight)
                .setDriverType(CommonParams.DriverType.USB)
                /**
\1set
                 */
                .setFrameOutputFormat(FRAME_OUT_PUT_FORMAT)
                .setUvcHandleParam(uvcHandleParam)
                .build();
        Log.d(TAG, "stopPreview onSuccess initHandle : ");
        mIrcamEngine.initHandle(new HandleInitCallback() {
            @Override
            public void onSuccess(IrcmdEngine ircmdEngine) {
                DeviceIrcmdControlManager.getInstance().setIrcamEngine(mIrcamEngine);
                DeviceIrcmdControlManager.getInstance().setIrcmdEngine(ircmdEngine);
                Log.d(TAG, "IrcamVersion : " + mIrcamEngine.ircamVersion());
                Log.d(TAG, "IrcmdVersion : " + ircmdEngine.ircmdVersion());
                Log.d(TAG, "IrcamVersion number: " + mIrcamEngine.ircamVersionNumber());
                Log.d(TAG, "IrcmdVersion number: " + ircmdEngine.ircmdVersionNumber());
                if (isStartPreview) {
                    handleStartPreview();
                }
            }

            @Override
            public void onFail(ErrorCode errorCode) {
                mMainHandler.sendEmptyMessage(IrDisplayActivity.HANDLE_INIT_FAIL);
            }
        });
    }

    /**
\1
     */
    public void startPreview() {
        Log.d(TAG, "startPreview");
        if (mIrcamEngine != null) {
            mIrcamEngine.setIrFrameCallback(mIIrFrameCallback);
            int result = mIrcamEngine.startVideoStream();
            if (result != 0) {
                mMainHandler.sendEmptyMessage(IrDisplayActivity.PREVIEW_FAIL);
            }
            if (Const.DEVICE_TYPE == DeviceType.DEVICE_TYPE_X3) {
                DeviceIrcmdControlManager.getInstance().sendFPGAParam();
                DeviceIrcmdControlManager.getInstance().sendISPParam();
            }
        }
        TempCompensation.getInstance().startTempCompensation();
    }

    /**
\1
     */
    public void pausePreview() {
        if (mIrcamEngine != null) {
            mIrcamEngine.pauseVideoStream();
        }
    }

    /**
\1
     */
    public void resumePreview() {
        if (mIrcamEngine != null) {
            mIrcamEngine.resumeVideoStream();
        }
    }

    /**
\1
     */
    public void closePreview() {
        if (mIrcamEngine != null) {
            mIrcamEngine.closeVideoStream();
            mIrcamEngine.releaseVideoStream();
            mIrcamEngine.destroyHandle();
            mIrcamEngine = null;
        }
    }

    /**
\1
     */
    public void stopPreview() {
        Log.i(TAG, "stopPreview"Test Data"basicVideoStreamPause=" + ircmdError);
        }
        if (mIrcamEngine != null) {
            mIrcamEngine.setIrFrameCallback(null);
            mIrcamEngine.stopVideoStream();
        }
    }

    /**
\1
     */
    public void releaseSource() {
        mIIrFrameCallback = null;
        mIrARGBData = null;
        mIrData = null;
        mOnTempDataChangeCallback = null;
        setAutoSwitchGainEnable(false);
        DeviceIrcmdControlManager.getInstance().setIrcmdEngine(null);
        DeviceIrcmdControlManager.getInstance().setIrcamEngine(null);
    }

    public void updateDevHandleParam(DevHandleParam devHandleParam) {
        if (mIrcamEngine != null) {
            mIrcamEngine.updateDevHandleParam(devHandleParam);
        }
    }

    public void setSaveData(boolean mSaveData) {
        this.mSaveData = mSaveData;
    }

    public void setTakePhoto(boolean takePhoto) {
        this.mTakePhoto = takePhoto;
    }

    public IrcamEngine getIrcamEngine() {
        return mIrcamEngine;
    }

    public void setImageRotate(RotateDegree imageRotate) {
        this.mImageRotate = imageRotate;
        mIrRotateData = null;
        mIrRotateData = new byte[mIrARGBLength];
        Log.d(TAG, "setImageRotate : "Test Data":liteText："+e.getMessage());
        }
    }

    //==============================Zeta Zoom start =======================================//
    private void initZetaZoomData() {
        //zeta zoom code
//        if (BuildConfig.zetazoomEnable) {
//            ZetaZoomHelper.getInstance().initData(mPreviewWidth, mPreviewHeight);
//            int imageWidth = ZetaZoomHelper.getInstance().getImageWidth();
//            int imageHeight = ZetaZoomHelper.getInstance().getImageHeight();
//            Log.d(TAG, "imageWidth" + imageWidth);
//            Log.d(TAG, "imageHeight" + imageHeight);
//            //zeta zoom code
//            mResultARBGDataForZetaZoom = new byte[imageWidth * imageHeight * 4];
//        }
    }

    private void handleSurfaceDisplayForZetaZoom() {
        //zeta zoom code
//        boolean isZetaZoom = ZetaZoomHelper.getInstance().isZetaZoomEnable();
//        if (isZetaZoom) {
//            Log.d(TAG, "isZetaZoom");
//            ZetaZoomHelper.getInstance().zetazoomRun(mIrData, mResultARBGDataForZetaZoom);
//            mFinalImageWidth = ZetaZoomHelper.getInstance().getImageWidth();
//            mFinalImageHeight = ZetaZoomHelper.getInstance().getImageHeight();
\1//NativeWindowdrawing
//            mSurface = mSurfaceView.getHolder().getSurface();
//            if (mSurface != null) {
//                mSurfaceNativeWindow.onDrawFrame(mSurface, mResultARBGDataForZetaZoom, mFinalImageWidth, mFinalImageHeight);
//            }
//        } else {
//            handleSurfaceDisplay();
//        }
    }

    //==============================Zeta Zoom end =======================================//

    public AlarmBean getAlarmBean() {
        return alarmBean;
    }

    public void setAlarmBean(AlarmBean alarmBean) {
        this.alarmBean = alarmBean;
    }

    public void setLimit(float max, float min, int maxColor, int minColor) {
        this.max = max;
        this.min = min;
        this.maxColor = maxColor;
        this.minColor = minColor;
    }

    public void setColorList(int[] colorList, @Nullable float[] places, boolean isUseGray, float customMaxTemp, float customMinTemp) {
        irImageHelp.setColorList(colorList, places, isUseGray,customMaxTemp,customMinTemp);
    }
    public void setPseudocolorMode(int pseudocolorMode) {
        this.pseudocolorMode = pseudocolorMode;
    }
}
