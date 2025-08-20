package com.topdon.lib.core.config

object RouterConfig {

    private const val GROUP_APP = "app"
    private const val GROUP_IR = "ir"
    private const val GROUP_USER = "user"
    private const val GROUP_REPORT = "report"
    private const val GROUP_CALIBRATE = "calibrate"


    const val MAIN = "/$GROUP_APP/main"
    const val CLAUSE = "/$GROUP_APP/clause"
    const val POLICY = "/$GROUP_APP/policy"
    const val VERSION = "/$GROUP_APP/version"
    const val PDF = "/$GROUP_APP/app/pdf"
    const val IR_MORE_HELP = "/$GROUP_APP/app/more_help"
    const val IR_GALLERY_EDIT = "/$GROUP_APP/gallery/edit"
    const val WEB_VIEW = "/$GROUP_APP/WebViewActivity"

    const val IR_MAIN = "/$GROUP_IR/irMain"
    const val IR_FRAME = "/$GROUP_IR/frame"
    const val IR_SETTING = "/$GROUP_IR/setting"
    const val IR_THERMAL_MONITOR = "/$GROUP_IR/monitor"
    const val IR_MONITOR_CHART = "/$GROUP_IR/monitor/chart"
    const val IR_THERMAL_LOG_MP_CHART = "/$GROUP_IR/log/mp/chart"
    const val IR_GALLERY_HOME = "/$GROUP_IR/gallery/home"
    const val IR_GALLERY_DETAIL_01 = "/$GROUP_IR/gallery/detail01"
    const val IR_VIDEO_GSY = "/$GROUP_IR/video/gsy"
    const val IR_CAMERA_SETTING = "/$GROUP_IR/camera/setting"
    const val IR_CORRECTION = "/$GROUP_IR/correction"
    const val IR_CORRECTION_TWO = "/$GROUP_IR/correction2"
    const val IR_CORRECTION_THREE = "/$GROUP_IR/correction3"
    const val IR_CORRECTION_FOUR = "/$GROUP_IR/correction4"
    const val IR_IMG_PICK = "/$GROUP_IR/ImagePickIRActivity"
    const val IR_IMG_PICK_PLUS = "/$GROUP_IR/ImagePickIRPlushActivity"
    const val IR_DEVICE_ADD = "/$GROUP_IR/irMain"  // Redirect to main IR - TC007/TS004 support removed

    const val REPORT_CREATE_FIRST = "/$GROUP_REPORT/create/first"
    const val REPORT_CREATE_SECOND = "/$GROUP_REPORT/create/second"
    const val REPORT_PREVIEW_FIRST = "/$GROUP_REPORT/preview/first"
    const val REPORT_PREVIEW_SECOND = "/$GROUP_REPORT/preview/second"
    const val REPORT_DETAIL = "/$GROUP_REPORT/detail"
    const val REPORT_LIST = "/$GROUP_REPORT/list"
    const val REPORT_PICK_IMG = "/$GROUP_REPORT/pick/img"
    const val REPORT_PREVIEW = "/$GROUP_REPORT/preview"

    const val QUESTION = "/$GROUP_USER/question"//FAQ
    const val QUESTION_DETAILS = "/$GROUP_USER/question/details"//FAQ
    const val UNIT = "/$GROUP_USER/unit"
    const val ELECTRONIC_MANUAL = "/$GROUP_USER/electronic_manual"
    const val AUTO_SAVE = "/$GROUP_USER/auto_save"
    
    // Legacy device routes - redirect to standard MORE
    const val TC_MORE = "/$GROUP_USER/more"

    const val MANUAL_START = "/$GROUP_CALIBRATE/manual/first"
    const val IR_FRAME_PLUSH = "/$GROUP_IR/frame/plush"

    const val IR_TCLITE = "/lite/tcLite"
    const val IR_THERMAL_MONITOR_LITE = "/lite/monitor"
    const val IR_IMG_PICK_LITE = "/lite/ImagePickIRLiteActivity"
    const val IR_MONITOR_CHART_LITE = "/lite/monitor/chart"
    const val IR_CORRECTION_THREE_LITE = "/lite/correction3"
    const val IR_CORRECTION_FOUR_LITE = "/lite/correction4"

}