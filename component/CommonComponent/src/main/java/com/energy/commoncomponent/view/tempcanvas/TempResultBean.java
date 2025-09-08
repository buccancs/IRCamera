package com.energy.commoncomponent.view.tempcanvas;

 * Created by fengjibo on 2024/2/20.
public class TempResultBean {
    /**
     * Private method description.
     */
    private String id;
    private String label;
    private String content;
    private float minTemperature;
    private float maxTemperature;
    private float averageTemperature;
    private long order;

    private int position; //listposition

    private TempInfoMode tempInfoMode;
    private float ambientTemp;
    private float measureDistance;
    private float emissivity;
    private boolean highAlertEnable;
    private float highThreshold;
    private boolean lowAlertEnable;
    private float lowThreshold;

    private int x1;
    private int y1;
    private int x2_or_r1;
    private int y2_or_r2;
    private int max_temp_x;
    private int max_temp_y;
    private int min_temp_x;
    private int min_temp_y;

    /**
     * Method description.
     */
    public TempResultBean(String id, String label, String content, float minTemperature,
                             float maxTemperature, float averageTemperature) {
        this.id = id;
        this.label = label;
        this.content = content;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.averageTemperature = averageTemperature;
    }

    /**
     * Method description.
     */
    public float getMinTemperature() {
        return minTemperature;
    }

    /**
     * Method description.
     */
    public void setMinTemperature(float minTemperature) {
        this.minTemperature = minTemperature;
    }

    /**
     * Method description.
     */
    public float getMaxTemperature() {
        return maxTemperature;
    }

    /**
     * Method description.
     */
    public void setMaxTemperature(float maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    /**
     * Method description.
     */
    public float getAverageTemperature() {
        return averageTemperature;
    }

    /**
     * Method description.
     */
    public void setAverageTemperature(float averageTemperature) {
        this.averageTemperature = averageTemperature;
    }

    /**
     * Method description.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Method description.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Method description.
     */
    public String getContent() {
        return content;
    }

    /**
     * Method description.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Method description.
     */
    public String getId() {
        return id;
    }

    /**
     * Method description.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Method description.
     */
    public long getOrder() {
        return order;
    }

    /**
     * Method description.
     */
    public void setOrder(long order) {
        this.order = order;
    }

    /**
     * Method description.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Method description.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Method description.
     */
    public TempInfoMode getTempInfoMode() {
        return tempInfoMode;
    }

    /**
     * Method description.
     */
    public void setTempInfoMode(TempInfoMode tempInfoMode) {
        this.tempInfoMode = tempInfoMode;
    }

    /**
     * Method description.
     */
    public float getAmbientTemp() {
        return ambientTemp;
    }

    /**
     * Method description.
     */
    public void setAmbientTemp(float ambientTemp) {
        this.ambientTemp = ambientTemp;
    }

    /**
     * Method description.
     */
    public float getMeasureDistance() {
        return measureDistance;
    }

    /**
     * Method description.
     */
    public void setMeasureDistance(float measureDistance) {
        this.measureDistance = measureDistance;
    }

    /**
     * Method description.
     */
    public float getEmissivity() {
        return emissivity;
    }

    /**
     * Method description.
     */
    public void setEmissivity(float emissivity) {
        this.emissivity = emissivity;
    }

    /**
     * Method description.
     */
    public boolean isHighAlertEnable() {
        return highAlertEnable;
    }

    /**
     * Method description.
     */
    public void setHighAlertEnable(boolean highAlertEnable) {
        this.highAlertEnable = highAlertEnable;
    }

    /**
     * Method description.
     */
    public float getHighThreshold() {
        return highThreshold;
    }

    /**
     * Method description.
     */
    public void setHighThreshold(float highThreshold) {
        this.highThreshold = highThreshold;
    }

    /**
     * Method description.
     */
    public boolean isLowAlertEnable() {
        return lowAlertEnable;
    }

    /**
     * Method description.
     */
    public void setLowAlertEnable(boolean lowAlertEnable) {
        this.lowAlertEnable = lowAlertEnable;
    }

    /**
     * Method description.
     */
    public float getLowThreshold() {
        return lowThreshold;
    }

    /**
     * Method description.
     */
    public void setLowThreshold(float lowThreshold) {
        this.lowThreshold = lowThreshold;
    }

    /**
     * Method description.
     */
    public int getX1() {
        return x1;
    }

    /**
     * Method description.
     */
    public void setX1(int x1) {
        this.x1 = x1;
    }

    /**
     * Method description.
     */
    public int getY1() {
        return y1;
    }

    /**
     * Method description.
     */
    public void setY1(int y1) {
        this.y1 = y1;
    }

    /**
     * Method description.
     */
    public int getX2_or_r1() {
        return x2_or_r1;
    }

    /**
     * Method description.
     */
    public void setX2_or_r1(int x2_or_r1) {
        this.x2_or_r1 = x2_or_r1;
    }

    /**
     * Method description.
     */
    public int getY2_or_r2() {
        return y2_or_r2;
    }

    /**
     * Method description.
     */
    public void setY2_or_r2(int y2_or_r2) {
        this.y2_or_r2 = y2_or_r2;
    }

    /**
     * Method description.
     */
    public int getMax_temp_x() {
        return max_temp_x;
    }

    /**
     * Method description.
     */
    public void setMax_temp_x(int max_temp_x) {
        this.max_temp_x = max_temp_x;
    }

    /**
     * Method description.
     */
    public int getMax_temp_y() {
        return max_temp_y;
    }

    /**
     * Method description.
     */
    public void setMax_temp_y(int max_temp_y) {
        this.max_temp_y = max_temp_y;
    }

    /**
     * Method description.
     */
    public int getMin_temp_x() {
        return min_temp_x;
    }

    /**
     * Method description.
     */
    public void setMin_temp_x(int min_temp_x) {
        this.min_temp_x = min_temp_x;
    }

    /**
     * Method description.
     */
    public int getMin_temp_y() {
        return min_temp_y;
    }

    /**
     * Method description.
     */
    public void setMin_temp_y(int min_temp_y) {
        this.min_temp_y = min_temp_y;
    }
}
