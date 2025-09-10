package com.topdon.lib.core.config

import com.topdon.lib.core.repository.GalleryRepository

object ExtraKeyConfig {
    /**
     * boolean Type - data，data.
     * true-data false-data
     */
    const val IS_PICK_REPORT_IMG = "IS_PICK_REPORT_IMG"

    /**
     * boolean Type - data.
     * true-data false-data
     */
    const val IS_VIDEO = "IS_VIDEO"

    /**
     * boolean Type - Gallerydata
     */
    const val HAS_BACK_ICON = "HAS_BACK_ICON"

    /**
     * boolean Type - GallerydataSwitch data、TS004、TC007 data
     */
    const val CAN_SWITCH_DIR = "CAN_SWITCH_DIR"

    /**
     * boolean Type - dataTypedata TC007.
     * true-TC007 false-data
     */
    const val IS_TC007 = "IS_TC007"

    /**
     * boolean Type - data.
     * true-data false-data
     */
    const val IS_PICK_INSPECTOR = "IS_PICK_INSPECTOR"

    /**
     * boolean Type - data
     * true-data false-data
     */
    const val IS_REPORT = "IS_REPORT"

    /**
     * Int Type - dataGallerydataType data [GalleryRepository.DirType] data
     */
    const val DIR_TYPE = "CUR_DIR_TYPE"

    /**
     * Int Type - Currentdata index.
     */
    const val CURRENT_ITEM = "CURRENT_ITEM"

    /**
     * Long Type - data：data Id.
     */
    const val DETECT_ID = "DETECT_ID"

    /**
     * Long Type - data：data Id.
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
     * String Type - data.
     */
    const val FILE_ABSOLUTE_PATH = "FILE_ABSOLUTE_PATH"

    /**
     * String Type - data item data.
     */
    const val ITEM_NAME = "ITEM_NAME"

    /**
     * String Type - data.
     */
    const val RESULT_INPUT_TEXT = "RESULT_INPUT_TEXT"

    /**
     * String Type - data.
     */
    const val RESULT_IMAGE_PATH = "RESULT_IMAGE_PATH"

    /**
     * String Type - data.
     */
    const val RESULT_PATH_WHITE = "RESULT_PATH_WHITE"

    /**
     * String Type - data.
     */
    const val RESULT_PATH_BLACK = "RESULT_PATH_BLACK"

    /**
     * List&lt;String&gt; Type - data.
     */
    const val IMAGE_PATH_LIST = "IMAGE_PATH_LIST"

    /**
     * Parcelable Type - dataPoint/Line/Areadata (ImageTempBean).
     */
    const val IMAGE_TEMP_BEAN = "IMAGE_TEMP_BEAN"

    /**
     * Parcelable Type - dataAlldata (ReportBean).
     */
    const val REPORT_BEAN = "REPORT_BEAN"

    /**
     * Parcelable Type - data (ReportInfoBean).
     */
    const val REPORT_INFO = "REPORT_INFO"

    /**
     * Parcelable Type - data (ReportConditionBean).
     */
    const val REPORT_CONDITION = "REPORT_CONDITION"

    /**
     * Parcelable Type - Currentdata (List<ReportIRBean>).
     */
    const val REPORT_IR_LIST = "REPORT_IR_LIST"

    /**
     * Parcelable Type - dataSettingsdata (CustomPseudoBean).
     */
    const val CUSTOM_PSEUDO_BEAN = "CUSTOM_PSEUDO_BEAN"

    /**
     * long Type - Unix data，data.
     */
    const val TIME_MILLIS = "TIME_MILLIS"

    /**
     * String Type - dataType.
     * data，data Stringdata：
     * point-data line-data fence-data
     */
    const val MONITOR_TYPE = "MONITOR_TYPE"

    const val IR_PATH = "ir_path"
    const val TEMP_HIGH = "temp_high"
    const val TEMP_LOW = "temp_low"

    const val IS_CAR_DETECT_ENTER = "IS_CAR_DETECT_ENTER"
}
