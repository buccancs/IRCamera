package com.topdon.lib.core.config

import com.topdon.lib.core.repository.GalleryRepository

object ExtraKeyConfig {
    /**
     * boolean [Chinese text] - [Chinese text], [Chinese text]operation.
     * true-[Chinese text] false-[Chinese text]
     */
    const val IS_PICK_REPORT_IMG = "IS_PICK_REPORT_IMG"

    /**
     * boolean [Chinese text] - [Chinese text].
     * true-[Chinese text] false-[Chinese text]
     */
    const val IS_VIDEO = "IS_VIDEO"

    /**
     * boolean [Chinese text] - gallery[Chinese text]
     */
    const val HAS_BACK_ICON = "HAS_BACK_ICON"

    /**
     * boolean [Chinese text] - gallery[Chinese text]switch [Chinese text]line[Chinese text], TS004, TC007 [Chinese text]
     */
    const val CAN_SWITCH_DIR = "CAN_SWITCH_DIR"

    /**
     * boolean [Chinese text] - [Chinese text] TC007.
     * true-TC007 false-[Chinese text]
     */
    const val IS_TC007 = "IS_TC007"

    /**
     * boolean [Chinese text] - [Chinese text].
     * true-[Chinese text] false-[Chinese text]
     */
    const val IS_PICK_INSPECTOR = "IS_PICK_INSPECTOR"

    /**
     * boolean [Chinese text] - [Chinese text]
     * true-[Chinese text] false-[Chinese text]
     */
    const val IS_REPORT = "IS_REPORT"

    /**
     * Int [Chinese text] - [Chinese text]gallery[Chinese text] [Chinese text] [GalleryRepository.DirType] [Chinese text]
     */
    const val DIR_TYPE = "CUR_DIR_TYPE"

    /**
     * Int [Chinese text] - [Chinese text]in progress[Chinese text] index.
     */
    const val CURRENT_ITEM = "CURRENT_ITEM"

    /**
     * Long [Chinese text] - [Chinese text]: [Chinese text] Id.
     */
    const val DETECT_ID = "DETECT_ID"

    /**
     * Long [Chinese text] - [Chinese text]: [Chinese text] Id.
     */
    const val DIR_ID = "DIR_ID"

    /**
     * Long [Chinese text] - ID.
     */
    const val LONG_ID = "LONG_ID"

    /**
     * String [Chinese text] - URL.
     */
    const val URL = "URL"

    /**
     * String [Chinese text] - [Chinese text].
     */
    const val FILE_ABSOLUTE_PATH = "FILE_ABSOLUTE_PATH"

    /**
     * String [Chinese text] - [Chinese text] item [Chinese text].
     */
    const val ITEM_NAME = "ITEM_NAME"

    /**
     * String [Chinese text] - [Chinese text]text[Chinese text].
     */
    const val RESULT_INPUT_TEXT = "RESULT_INPUT_TEXT"

    /**
     * String [Chinese text] - [Chinese text].
     */
    const val RESULT_IMAGE_PATH = "RESULT_IMAGE_PATH"

    /**
     * String [Chinese text] - [Chinese text].
     */
    const val RESULT_PATH_WHITE = "RESULT_PATH_WHITE"

    /**
     * String [Chinese text] - [Chinese text].
     */
    const val RESULT_PATH_BLACK = "RESULT_PATH_BLACK"

    /**
     * List&lt;String&gt; [Chinese text] - [Chinese text].
     */
    const val IMAGE_PATH_LIST = "IMAGE_PATH_LIST"

    /**
     * Parcelable [Chinese text] - [Chinese text]Point/Line/Areamessage[Chinese text] (ImageTempBean).
     */
    const val IMAGE_TEMP_BEAN = "IMAGE_TEMP_BEAN"

    /**
     * Parcelable [Chinese text] - [Chinese text]message (ReportBean).
     */
    const val REPORT_BEAN = "REPORT_BEAN"

    /**
     * Parcelable [Chinese text] - [Chinese text]message (ReportInfoBean).
     */
    const val REPORT_INFO = "REPORT_INFO"

    /**
     * Parcelable [Chinese text] - [Chinese text] (ReportConditionBean).
     */
    const val REPORT_CONDITION = "REPORT_CONDITION"

    /**
     * Parcelable [Chinese text] - [Chinese text] (List<ReportIRBean>).
     */
    const val REPORT_IR_LIST = "REPORT_IR_LIST"

    /**
     * Parcelable [Chinese text] - [Chinese text]Settings[Chinese text] (CustomPseudoBean).
     */
    const val CUSTOM_PSEUDO_BEAN = "CUSTOM_PSEUDO_BEAN"

    /**
     * long [Chinese text] - Unix [Chinese text], [Chinese text].
     */
    const val TIME_MILLIS = "TIME_MILLIS"

    /**
     * String [Chinese text] - [Chinese text].
     * [Chinese text], [Chinese text] String[Chinese text]: 
     * point-point line-line fence-[Chinese text]
     */
    const val MONITOR_TYPE = "MONITOR_TYPE"

    const val IR_PATH = "ir_path"
    const val TEMP_HIGH = "temp_high"
    const val TEMP_LOW = "temp_low"

    const val IS_CAR_DETECT_ENTER = "IS_CAR_DETECT_ENTER"
}
