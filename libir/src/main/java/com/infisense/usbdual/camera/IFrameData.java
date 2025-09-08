package com.infisense.usbdual.camera;

import com.infisense.usbdual.Const;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

    /**
     * IFrameData class.
     *
     * Provides iframedata functionality.
     */
    public class IFrameData {
        /**
     * [Chinese text], ARGB, [Chinese text]: 
     * [Chinese text] x [Chinese text]high[Chinese text] x 4.
     */
    public static int FUSION_LEN = Const.DUAL_WIDTH * Const.DUAL_HEIGHT * 4;
    /**
     * [Chinese text], [Chinese text]temperature[Chinese text], YUV-Y16, [Chinese text]: 
     * [Chinese text] x [Chinese text]high[Chinese text] x 2.
     */
    public static int ORIGINAL_LEN = Const.IR_WIDTH * Const.IR_HEIGHT * 2;
    /**
     * [Chinese text]temperature[Chinese text], YUV-422, [Chinese text]: 
     * [Chinese text] x [Chinese text]high[Chinese text] x 2.
     */
    public static int REMAP_TEMP_LEN = Const.DUAL_WIDTH * Const.DUAL_HEIGHT * 2;
    /**
     * [Chinese text]visible[Chinese text], RGB24, [Chinese text]: 
     * [Chinese text]visible[Chinese text] x [Chinese text]visible[Chinese text]high[Chinese text] x 3.
     */
    public static int LIGHT_LEN = Const.VL_WIDTH * Const.VL_HEIGHT * 3;
    /**
     * [Chinese text]visible[Chinese text], ARGB, [Chinese text]: 
     * [Chinese text]visible[Chinese text] x [Chinese text]visible[Chinese text]high[Chinese text] x 4.
     */
    public static int P_IN_P_LEN = Const.DUAL_WIDTH * Const.DUAL_HEIGHT * 4;
    /**
     * [Chinese text]in progress[Chinese text]visible[Chinese text], 
     * [Chinese text] [Chinese text], [Chinese text], [Chinese text]temperature, [Chinese text]temperature, [Chinese text]visible[Chinese text], [Chinese text]in progress[Chinese text]visible[Chinese text] [Chinese text].
     * [Chinese text].
     */
    public static int FRAME_LEN = FUSION_LEN + ORIGINAL_LEN + ORIGINAL_LEN + REMAP_TEMP_LEN + LIGHT_LEN + P_IN_P_LEN;

    /**
     * [Chinese text]in progress ARGB <b>[Chinese text]</b> [Chinese text]in progress.
     */
    public static byte[] readFusionData(@NonNull byte[] frame, @Nullable byte[] fusionData) {
        if (fusionData == null) {
            fusionData = new byte[FUSION_LEN];
        }
        System.arraycopy(frame, 0, fusionData, 0, fusionData.length);   // [Chinese text], ARGB
        return fusionData;
    }

    /**
     * [Chinese text]in progress YUV-16 <b>[Chinese text]</b> [Chinese text]in progress.
     */
    public static byte[] readNorIRData(@NonNull byte[] frame, @Nullable byte[] irData) {
        if (irData == null) {
            irData = new byte[ORIGINAL_LEN];
        }
        System.arraycopy(frame, FUSION_LEN, irData, 0, irData.length); // [Chinese text], YUV-Y16
        return irData;
    }

    /**
     * [Chinese text]in progress YUV-16 <b>[Chinese text]temperature[Chinese text]</b> [Chinese text]in progress.
     */
    public static byte[] readNorTempData(@NonNull byte[] frame, @Nullable byte[] norTempData) {
        if (norTempData == null) {
            norTempData = new byte[ORIGINAL_LEN];
        }
        System.arraycopy(frame, FUSION_LEN + ORIGINAL_LEN, norTempData, 0, norTempData.length); // [Chinese text]temperature[Chinese text], YUV-Y16
        return norTempData;
    }

    /**
     * [Chinese text]in progress YUV-422 <b>[Chinese text]temperature[Chinese text]</b> [Chinese text]in progress.
     */
    public static byte[] readRemapTempData(@NonNull byte[] frame, @Nullable byte[] remapTempData) {
        if (remapTempData == null) {
            remapTempData = new byte[REMAP_TEMP_LEN];
        }
        System.arraycopy(frame, FUSION_LEN + ORIGINAL_LEN + ORIGINAL_LEN, remapTempData, 0, remapTempData.length); // [Chinese text]temperature[Chinese text], YUV-422
        return remapTempData;
    }
}
