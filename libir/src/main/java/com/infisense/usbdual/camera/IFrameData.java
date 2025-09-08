package com.infisense.usbdual.camera;

import com.infisense.usbdual.Const;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class IFrameData {
     * ARGB
     *  x  x 4.
    public static int FUSION_LEN = Const.DUAL_WIDTH * Const.DUAL_HEIGHT * 4;
     * YUV-Y16
     *  x  x 2.
    public static int ORIGINAL_LEN = Const.IR_WIDTH * Const.IR_HEIGHT * 2;
     * YUV-422
     *  x  x 2.
    public static int REMAP_TEMP_LEN = Const.DUAL_WIDTH * Const.DUAL_HEIGHT * 2;
     * RGB24
     *  x  x 3.
    public static int LIGHT_LEN = Const.VL_WIDTH * Const.VL_HEIGHT * 3;
     * ARGB
     *  x  x 4.
    public static int P_IN_P_LEN = Const.DUAL_WIDTH * Const.DUAL_HEIGHT * 4;
     *  .
     * .
    public static int FRAME_LEN = FUSION_LEN + ORIGINAL_LEN + ORIGINAL_LEN + REMAP_TEMP_LEN + LIGHT_LEN + P_IN_P_LEN;



     *  ARGB <b></b> .
    public static byte[] readFusionData(@NonNull byte[] frame, @Nullable byte[] fusionData) {
        if (fusionData == null) {
            fusionData = new byte[FUSION_LEN];
        }
        System.arraycopy(frame, 0, fusionData, 0, fusionData.length); //，ARGB
        return fusionData;
    }

     *  YUV-16 <b></b> .
    public static byte[] readNorIRData(@NonNull byte[] frame, @Nullable byte[] irData) {
        if (irData == null) {
            irData = new byte[ORIGINAL_LEN];
        }
        System.arraycopy(frame, FUSION_LEN, irData, 0, irData.length); //，YUV-Y16
        return irData;
    }

     *  YUV-16 <b></b> .
    public static byte[] readNorTempData(@NonNull byte[] frame, @Nullable byte[] norTempData) {
        if (norTempData == null) {
            norTempData = new byte[ORIGINAL_LEN];
        }
        System.arraycopy(frame, FUSION_LEN + ORIGINAL_LEN, norTempData, 0, norTempData.length); //，YUV-Y16
        return norTempData;
    }

     *  YUV-422 <b></b> .
    public static byte[] readRemapTempData(@NonNull byte[] frame, @Nullable byte[] remapTempData) {
        if (remapTempData == null) {
            remapTempData = new byte[REMAP_TEMP_LEN];
        }
        System.arraycopy(frame, FUSION_LEN + ORIGINAL_LEN + ORIGINAL_LEN, remapTempData, 0, remapTempData.length); //，YUV-422
        return remapTempData;
    }
}
