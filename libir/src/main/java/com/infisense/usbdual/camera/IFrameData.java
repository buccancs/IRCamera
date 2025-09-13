package com.infisense.usbdual.camera;

import com.infisense.usbdual.Const;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class IFrameData {
 /**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 public static int FUSION_LEN = Const.DUAL_WIDTH * Const.DUAL_HEIGHT * 4;
 /**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 public static int ORIGINAL_LEN = Const.IR_WIDTH * Const.IR_HEIGHT * 2;
 /**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 public static int REMAP_TEMP_LEN = Const.DUAL_WIDTH * Const.DUAL_HEIGHT * 2;
 /**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 public static int LIGHT_LEN = Const.VL_WIDTH * Const.VL_HEIGHT * 3;
 /**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 public static int P_IN_P_LEN = Const.DUAL_WIDTH * Const.DUAL_HEIGHT * 4;
 /**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 public static int FRAME_LEN = FUSION_LEN + ORIGINAL_LEN + ORIGINAL_LEN + REMAP_TEMP_LEN + LIGHT_LEN + P_IN_P_LEN;

 /**
 * Comment removed (contained Chinese characters)
 */
 public static byte[] readFusionData(@NonNull byte[] frame, @Nullable byte[] fusionData) {
 if (fusionData == null) {
 fusionData = new byte[FUSION_LEN];
 }
 System.arraycopy(frame, 0, fusionData, 0, fusionData.length); //fusion，ARGB
 return fusionData;
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 public static byte[] readNorIRData(@NonNull byte[] frame, @Nullable byte[] irData) {
 if (irData == null) {
 irData = new byte[ORIGINAL_LEN];
 }
 System.arraycopy(frame, FUSION_LEN, irData, 0, irData.length); //infrared，YUV-Y16
 return irData;
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 public static byte[] readNorTempData(@NonNull byte[] frame, @Nullable byte[] norTempData) {
 if (norTempData == null) {
 norTempData = new byte[ORIGINAL_LEN];
 }
 System.arraycopy(frame, FUSION_LEN + ORIGINAL_LEN, norTempData, 0, norTempData.length); //Temperature，YUV-Y16
 return norTempData;
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 public static byte[] readRemapTempData(@NonNull byte[] frame, @Nullable byte[] remapTempData) {
 if (remapTempData == null) {
 remapTempData = new byte[REMAP_TEMP_LEN];
 }
 System.arraycopy(frame, FUSION_LEN + ORIGINAL_LEN + ORIGINAL_LEN, remapTempData, 0, remapTempData.length); //Temperature，YUV-422
 return remapTempData;
 }
}
