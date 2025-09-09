package com.topdon.lib.core.config

import com.topdon.lib.core.repository.GalleryRepository

object ExtraKeyConfig {
    /**
     * boolean Type - [CN_TEXT]，[CN_TEXT].
     * true-[CN_TEXT] false-[CN_TEXT]
     */
    const val IS_PICK_REPORT_IMG = "IS_PICK_REPORT_IMG"

    /**
     * boolean Type - [CN_TEXT].
     * true-[CN_TEXT] false-[CN_TEXT]
     */
    const val IS_VIDEO = "IS_VIDEO"

    /**
     * boolean Type - Gallery[CN_TEXT]
     */
    const val HAS_BACK_ICON = "HAS_BACK_ICON"

    /**
     * boolean Type - Gallery[CN_TEXT]Switch [CN_TEXT]、TS004、TC007 [CN_TEXT]
     */
    const val CAN_SWITCH_DIR = "CAN_SWITCH_DIR"

    /**
     * boolean Type - [CN_TEXT]Type[CN_TEXT] TC007.
     * true-TC007 false-[CN_TEXT]
     */
    const val IS_TC007 = "IS_TC007"

    /**
     * boolean Type - [CN_TEXT].
     * true-[CN_TEXT] false-[CN_TEXT]
     */
    const val IS_PICK_INSPECTOR = "IS_PICK_INSPECTOR"

    /**
     * boolean Type - [CN_TEXT]
     * true-[CN_TEXT] false-[CN_TEXT]
     */
    const val IS_REPORT = "IS_REPORT"

    /**
     * Int Type - [CN_TEXT]Gallery[CN_TEXT]Type [CN_TEXT] [GalleryRepository.DirType] [CN_TEXT]
     */
    const val DIR_TYPE = "CUR_DIR_TYPE"

    /**
     * Int Type - Current[CN_TEXT] index.
     */
    const val CURRENT_ITEM = "CURRENT_ITEM"

    /**
     * Long Type - [CN_TEXT]：[CN_TEXT] Id.
     */
    const val DETECT_ID = "DETECT_ID"

    /**
     * Long Type - [CN_TEXT]：[CN_TEXT] Id.
     */
    const val DIR_ID = "DIR_ID"

    /**
     * Long Type - ID.
     */
    const val LONG_ID = "LONG_ID"

    /**
     * String Type - URL.
     */
    const val URL = "URL"

    /**
     * String Type - [CN_TEXT].
     */
    const val FILE_ABSOLUTE_PATH = "FILE_ABSOLUTE_PATH"

    /**
     * String Type - [CN_TEXT] item [CN_TEXT].
     */
    const val ITEM_NAME = "ITEM_NAME"

    /**
     * String Type - [CN_TEXT].
     */
    const val RESULT_INPUT_TEXT = "RESULT_INPUT_TEXT"

    /**
     * String Type - [CN_TEXT].
     */
    const val RESULT_IMAGE_PATH = "RESULT_IMAGE_PATH"

    /**
     * String Type - [CN_TEXT].
     */
    const val RESULT_PATH_WHITE = "RESULT_PATH_WHITE"

    /**
     * String Type - [CN_TEXT].
     */
    const val RESULT_PATH_BLACK = "RESULT_PATH_BLACK"

    /**
     * List&lt;String&gt; Type - [CN_TEXT].
     */
    const val IMAGE_PATH_LIST = "IMAGE_PATH_LIST"

    /**
     * Parcelable Type - [CN_TEXT]Point/Line/Area[CN_TEXT] (ImageTempBean).
     */
    const val IMAGE_TEMP_BEAN = "IMAGE_TEMP_BEAN"

    /**
     * Parcelable Type - [CN_TEXT]All[CN_TEXT] (ReportBean).
     */
    const val REPORT_BEAN = "REPORT_BEAN"

    /**
     * Parcelable Type - [CN_TEXT] (ReportInfoBean).
     */
    const val REPORT_INFO = "REPORT_INFO"

    /**
     * Parcelable Type - [CN_TEXT] (ReportConditionBean).
     */
    const val REPORT_CONDITION = "REPORT_CONDITION"

    /**
     * Parcelable Type - Current[CN_TEXT] (List<ReportIRBean>).
     */
    const val REPORT_IR_LIST = "REPORT_IR_LIST"

    /**
     * Parcelable Type - [CN_TEXT]Settings[CN_TEXT] (CustomPseudoBean).
     */
    const val CUSTOM_PSEUDO_BEAN = "CUSTOM_PSEUDO_BEAN"

    /**
     * long Type - Unix [CN_TEXT]，[CN_TEXT].
     */
    const val TIME_MILLIS = "TIME_MILLIS"

    /**
     * String Type - [CN_TEXT]Type.
     * [CN_TEXT]，[CN_TEXT] String[CN_TEXT]：
     * point-[CN_TEXT] line-[CN_TEXT] fence-[CN_TEXT]
     */
    const val MONITOR_TYPE = "MONITOR_TYPE"

    const val IR_PATH = "ir_path"
    const val TEMP_HIGH = "temp_high"
    const val TEMP_LOW = "temp_low"

    const val IS_CAR_DETECT_ENTER = "IS_CAR_DETECT_ENTER"
}
