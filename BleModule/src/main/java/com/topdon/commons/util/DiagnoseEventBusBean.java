package com.topdon.commons.util;

 * @Desc
 * @ClassName DiagnoseLanguageBean
 * @Email 616862466@qq.com
 * @Author
 * @Date 2022/9/13 15:40

public class DiagnoseEventBusBean {
    /**
     * Private method description.
     */
    private int what;//1 2 sn 3 4 5 Folder sn 6 diagMenuMask
    private String language;
    private boolean snConnection;// true sn false
    private boolean isDiagnose;// true false
    private long mDiagEntryType;//
    private long mDiagMenuMask;//
    private String snPath;//sn

    /**
     * Method description.
     */
    public String getSnPath() {
        return snPath;
    }

    /**
     * Method description.
     */
    public void setSnPath(String snPath) {
        this.snPath = snPath;
    }

    /**
     * Method description.
     */
    public int getWhat() {
        return what;
    }

    /**
     * Method description.
     */
    public void setWhat(int what) {
        this.what = what;
    }

    /**
     * Method description.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Method description.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Method description.
     */
    public boolean isSnConnection() {
        return snConnection;
    }

    /**
     * Method description.
     */
    public void setSnConnection(boolean snConnection) {
        this.snConnection = snConnection;
    }

    /**
     * Method description.
     */
    public boolean isDiagnose() {
        return isDiagnose;
    }

    /**
     * Method description.
     */
    public void setDiagnose(boolean diagnose) {
        isDiagnose = diagnose;
    }

    /**
     * Method description.
     */
    public long getmDiagEntryType() {
        return mDiagEntryType;
    }

    /**
     * Method description.
     */
    public void setmDiagEntryType(long mDiagEntryType) {
        this.mDiagEntryType = mDiagEntryType;
    }

    /**
     * Method description.
     */
    public long getDiagMenuMask() {
        return mDiagMenuMask;
    }

    /**
     * Method description.
     */
    public void setDiagMenuMask(long diagMenuMask) {
        mDiagMenuMask = diagMenuMask;
    }
}
