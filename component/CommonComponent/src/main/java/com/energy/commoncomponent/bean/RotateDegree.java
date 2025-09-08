package com.energy.commoncomponent.bean;

/**
 * Created by fengjibo on 2023/7/4.
 */
public enum RotateDegree {

    DEGREE_0(0),
    //
    DEGREE_90(1),
    //
    DEGREE_180(2),
    //
    DEGREE_270(3);

    /**
     * Private method description.
     */
    private final int value;

    RotateDegree(int value) {
        this.value = value;
    }

    /**
     * Method description.
     */
    public int getValue() {
        return value;
    }

    /**
     * Method description.
     */
    public static RotateDegree valueOf(int value) {
        RotateDegree[] types = RotateDegree.values();
        for(RotateDegree type: types){
            if (type.value == value) {
                return type;
            }
        }
        return DEGREE_0;
    }
}
