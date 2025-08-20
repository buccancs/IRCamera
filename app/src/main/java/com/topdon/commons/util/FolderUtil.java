package com.topdon.commons.util;

import android.text.TextUtils;
import android.util.Log;

import com.topdon.lms.sdk.LMS;

import java.io.File;

public class FolderUtil {
    public static String mPath = "/data/user/0/com.topdon.diag.artidiag/files";
    public static String mUserId;
    public static String fileName;
    public static String tdartsSn;

    public static String getFileName() {
        return fileName;
    }

    public static void setFileName(String mfileName) {
        fileName = mfileName;
    }

    public static void setUserId(String userId) {
        mUserId = userId;
    }

    public static void init() {
        mUserId = PreUtil.getInstance(Topdon.getApp()).get("VCI_" + LMS.getInstance().getLoginName());
        setUserId(mUserId);
        Log.e("bcf", "FolderUtil mUserId: " + mUserId);
        mPath = Topdon.getApp().getExternalFilesDir("").getAbsolutePath();
        Log.e("bcf", "FolderUtil init: " + mPath);
        initPath();
    }

    public static void initTDarts(String tdSn) {
        tdartsSn = tdSn;
        String mPath = Topdon.getApp().getExternalFilesDir("").getAbsolutePath();
        Log.e("bcf", fileName + "---FolderUtil initTDarts: " + mPath);
        if (!TextUtils.isEmpty(tdSn)) {
            File rfidFile = new File(mPath + fileName + tdSn + "/RFID/");
            if (!rfidFile.exists()) {
                Log.e("bcf", fileName + "---FolderUtil initTDarts: create");
                rfidFile.mkdirs();
            }
        }
    }

    public static void initFilePath() {
        String basePath = Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName;
        String downPath = basePath + "Download/";
        Log.e("bcf", fileName + "--下载路径初始化--" + downPath);
        File file = new File(downPath);
        if (!file.exists()) {
            Log.e("bcf", fileName + "---下载路径初始化创建 ");
            file.mkdirs();
        }
    }

    private static void initPath() {
        if (!TextUtils.isEmpty(mUserId)) {
            // Consolidated regional diagnosis folders into single International folder
            File internationalLibsFile = new File(mPath + fileName + mUserId + "/Diagnosis/International/");
            if (!internationalLibsFile.exists()) {
                internationalLibsFile.mkdirs();
            }
            File publicLibsFile = new File(mPath + fileName + mUserId + "/Diagnosis/Public/");
            if (!publicLibsFile.exists()) {
                publicLibsFile.mkdirs();
            }

            // Consolidated regional immobilizer folders into single International folder
            File immoInternationalFile = new File(mPath + fileName + mUserId + "/Immo/International/");
            if (!immoInternationalFile.exists()) {
                immoInternationalFile.mkdirs();
            }

            File rfidLibsFile = new File(mPath + fileName + mUserId + "/RFID/");
            if (!rfidLibsFile.exists()) {
                rfidLibsFile.mkdirs();
            }

            File energy = new File(mPath + fileName + mUserId + "/NewEnergy/");
            if (!energy.exists()) {
                energy.mkdirs();
            }

            File shotFile = new File(mPath + fileName + mUserId + "/Shot/");
            if (!shotFile.exists()) {
                shotFile.mkdirs();
            }

            File pdfFile = new File(mPath + fileName + mUserId + "/Pdf/");
            if (!pdfFile.exists()) {
                pdfFile.mkdirs();
            }

            File dataStreamFile = new File(mPath + fileName + mUserId + "/Datastream/");
            if (!dataStreamFile.exists()) {
                dataStreamFile.mkdirs();
            }

            File appFile = new File(mPath + fileName + "App/");
            if (!appFile.exists()) {
                appFile.mkdirs();
            }

            File firmwareFile = new File(mPath + fileName + "Firmware/");
            if (!firmwareFile.exists()) {
                firmwareFile.mkdirs();
            }

            File tdartsFile = new File(mPath + fileName + "T-darts/");
            if (!tdartsFile.exists()) {
                tdartsFile.mkdirs();
            }

            File userDiagnose = new File(mPath + fileName + "UserData/Diagnose/");
            if (!userDiagnose.exists()) {
                userDiagnose.mkdirs();
            }

            File userImmo = new File(mPath + fileName + "UserData/Immo/");
            if (!userImmo.exists()) {
                userImmo.mkdirs();
            }

            File userNewEnergy = new File(mPath + fileName + "UserData/NewEnergy/");
            if (!userNewEnergy.exists()) {
                userNewEnergy.mkdirs();
            }

            File userRFID = new File(mPath + fileName + "UserData/RFID/");
            if (!userRFID.exists()) {
                userRFID.mkdirs();
            }

            File downFile = new File(mPath + fileName + mUserId + "/Download/");
            if (!downFile.exists()) {
                downFile.mkdirs();
            }

            File diagHistoryFile = new File(mPath + fileName + mUserId + "/History/Diagnose/");
            if (!diagHistoryFile.exists()) {
                diagHistoryFile.mkdirs();
            }

            File seriveHistoryFile = new File(mPath + fileName + mUserId + "/History/Service/");
            if (!seriveHistoryFile.exists()) {
                seriveHistoryFile.mkdirs();
            }

            File galleryFile = new File(mPath + fileName + mUserId + "/Gallery/");
            if (!galleryFile.exists()) {
                galleryFile.mkdirs();
            }
            File dataLogFile = new File(mPath + fileName + mUserId + "/DataLog/");
            if (!dataLogFile.exists()) {
                dataLogFile.mkdirs();
            }

            // Removed empty log file creation blocks - these were not creating directories anyway

            File feedbackLog = new File(mPath + fileName + mUserId + "/FeedbackLog/");
            if (!feedbackLog.exists()) {
                feedbackLog.mkdirs();
            }

            File autovinLog = new File(mPath + fileName + mUserId + "/autovinLog/");
            if (!autovinLog.exists()) {
                autovinLog.mkdirs();
            }

        }
    }

    public static String getOtaPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + "/s/";
    }

    public static String getDataBasePath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName;
    }

    public static String getTDartsRootPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + tdartsSn + "/";
    }

    public static String getRootPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/";
    }

    public static String getVehiclesPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Diagnosis/";
    }

    public static String getImmoPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Immo/";
    }

    public static String getImmoInternationalPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Immo/International/";
    }

    public static String getRfidTopScanPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + tdartsSn + "/RFID/";
    }

    public static String getRfidPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/RFID/";
    }

    public static String getInternationalPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Diagnosis/International/";
    }

    // Consolidated regional paths - all now point to International folder
    public static String getAsiaPath() {
        return getInternationalPath();
    }

    public static String getAmericaPath() {
        return getInternationalPath();
    }

    public static String getEuropePath() {
        return getInternationalPath();
    }

    public static String getVehiclePublicPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Diagnosis/Public/";
    }

    public static String getVehicleTopScanPublicPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName;
    }

    public static String getShotPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Shot/";
    }

    public static String getDataStreamPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Datastream/";
    }

    public static String getPdfPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Pdf/";
    }

    public static String getAppPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "App/";
    }

    public static String getFirmwarePath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "Firmware/";
    }

    public static String getTdartsUpgradePath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "T-darts/";
    }

    public static String getDownloadPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Download/";
    }

    public static String getDiagHistoryPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/History/Diagnose/";
    }

    public static String getServiceHistoryPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/History/Service/";
    }

    public static String getLogPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "Log/";
    }

    public static String getSoLogPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "Log/SoLog/";
    }

    public static String getGalleryPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/Gallery/";
    }

    public static String getDataLogPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/DataLog/";
    }

    public static String getDiagDataLogPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/DataLog/DIAG/";
    }

    public static String getImmoDataLogPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/DataLog/IMMO/";
    }

    public static String getFeedbackLogPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/FeedbackLog/";
    }

    public static String getUserDataDiag() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "UserData/Diagnose/";
    }

    public static String getUserDataImmo() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "UserData/Immo/";
    }

    public static String getUserDataNewEnergy() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "UserData/NewEnergy/";
    }

    public static String getUserDataRFID() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "UserData/RFID/";
    }

    public static String getSoftDownPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + "Download/";
    }

    public static String getAutoVinLogPath() {
        return Topdon.getApp().getExternalFilesDir("").getAbsolutePath() + fileName + mUserId + "/autovinLog/";
    }

}