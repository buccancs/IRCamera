package com.topdon.lib.core.config

import com.topdon.lib.core.repository.GalleryRepository

object ExtraKeyConfig {
    /**
     * boolean  - ，.
     * true- false-
     */
    const val IS_PICK_REPORT_IMG = "IS_PICK_REPORT_IMG"

    /**
     * boolean  - .
     * true- false-
     */
    const val IS_VIDEO = "IS_VIDEO"

    /**
     * boolean  - 
     */
    const val HAS_BACK_ICON = "HAS_BACK_ICON"

    /**
     * boolean  -  、TS004、TC007 
     */
    const val CAN_SWITCH_DIR = "CAN_SWITCH_DIR"

    /**
     * boolean  -  TC007.
     * true-TC007 false-
     */
    const val IS_TC007 = "IS_TC007"

    /**
     * boolean  - .
     * true- false-
     */
    const val IS_PICK_INSPECTOR = "IS_PICK_INSPECTOR"

    /**
     * boolean  - 
     * true- false-
     */
    const val IS_REPORT = "IS_REPORT"

    /**
     * Int  -   [GalleryRepository.DirType] 
     */
    const val DIR_TYPE = "CUR_DIR_TYPE"

    /**
     * Int  -  index.
     */
    const val CURRENT_ITEM = "CURRENT_ITEM"

    /**
     * Long  - ： Id.
     */
    const val DETECT_ID = "DETECT_ID"

    /**
     * Long  - ： Id.
     */
    const val DIR_ID = "DIR_ID"

    /**
     * Long  - ID.
     */
    const val LONG_ID = "LONG_ID"

    /**
     * String  - URL.
     */
    const val URL = "URL"

    /**
     * String  - .
     */
    const val FILE_ABSOLUTE_PATH = "FILE_ABSOLUTE_PATH"

    /**
     * String  -  item .
     */
    const val ITEM_NAME = "ITEM_NAME"

    /**
     * String  - .
     */
    const val RESULT_INPUT_TEXT = "RESULT_INPUT_TEXT"

    /**
     * String  - .
     */
    const val RESULT_IMAGE_PATH = "RESULT_IMAGE_PATH"

    /**
     * String  - .
     */
    const val RESULT_PATH_WHITE = "RESULT_PATH_WHITE"

    /**
     * String  - .
     */
    const val RESULT_PATH_BLACK = "RESULT_PATH_BLACK"

    /**
     * List&lt;String&gt;  - .
     */
    const val IMAGE_PATH_LIST = "IMAGE_PATH_LIST"

    /**
     * Parcelable  -  (ImageTempBean).
     */
    const val IMAGE_TEMP_BEAN = "IMAGE_TEMP_BEAN"

    /**
     * Parcelable  -  (ReportBean).
     */
    const val REPORT_BEAN = "REPORT_BEAN"

    /**
     * Parcelable  -  (ReportInfoBean).
     */
    const val REPORT_INFO = "REPORT_INFO"

    /**
     * Parcelable  -  (ReportConditionBean).
     */
    const val REPORT_CONDITION = "REPORT_CONDITION"

    /**
     * Parcelable  -  (List<ReportIRBean>).
     */
    const val REPORT_IR_LIST = "REPORT_IR_LIST"

    /**
     * Parcelable  -  (CustomPseudoBean).
     */
    const val CUSTOM_PSEUDO_BEAN = "CUSTOM_PSEUDO_BEAN"

    /**
     * long  - Unix ，.
     */
    const val TIME_MILLIS = "TIME_MILLIS"

    /**
     * String  - .
     * ， String：
     * point- line- fence-
     */
    const val MONITOR_TYPE = "MONITOR_TYPE"

    const val IR_PATH = "ir_path"
    const val TEMP_HIGH = "temp_high"
    const val TEMP_LOW = "temp_low"

    const val IS_CAR_DETECT_ENTER = "IS_CAR_DETECT_ENTER"
}
