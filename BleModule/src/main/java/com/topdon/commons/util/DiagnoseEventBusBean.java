package com.topdon.commons.util;

/**
 * DiagnoseEventBusBean class.
 * 
 * Provides functionality for diagnoseeventbusbean operations.
 */
public class DiagnoseEventBusBean {
    private int what;// 1 [Chinese text]  2 sn[Chinese text]  3[Chinese text] 4 [Chinese text]  5 Folder sn[Chinese text]   6 diagMenuMask
    private String language;
    private boolean snConnection;// true sn[Chinese text]  false [Chinese text]
    private boolean isDiagnose;// true [Chinese text]  false  [Chinese text]
    private long mDiagEntryType;// [Chinese text]
    private long mDiagMenuMask;// [Chinese text]
    private String snPath;// sn[Chinese text]

    public String getSnPath() {
        return snPath;
    }

    public void setSnPath(String snPath) {
        this.snPath = snPath;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isSnConnection() {
        return snConnection;
    }

    public void setSnConnection(boolean snConnection) {
        this.snConnection = snConnection;
    }

    public boolean isDiagnose() {
        return isDiagnose;
    }

    public void setDiagnose(boolean diagnose) {
        isDiagnose = diagnose;
    }

    public long getmDiagEntryType() {
        return mDiagEntryType;
    }

    public void setmDiagEntryType(long mDiagEntryType) {
        this.mDiagEntryType = mDiagEntryType;
    }

    public long getDiagMenuMask() {
        return mDiagMenuMask;
    }

    public void setDiagMenuMask(long diagMenuMask) {
        mDiagMenuMask = diagMenuMask;
    }
}
