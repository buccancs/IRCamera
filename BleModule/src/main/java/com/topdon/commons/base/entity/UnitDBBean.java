package com.topdon.commons.base.entity;

import java.io.Serializable;

 * @Desc
 * @ClassName UnitDBBean
 * @Email 616862466@qq.com
 * @Author
 * @Date 2022/12/21 15:38
public class UnitDBBean implements Serializable {
//    {
//        : ,
//            : m,
//            : ,
//            : yd.,
//            : ,
//            : 1  = 1.094,
//            : 1.094
//    },

    /**
     * Private method description.
     */
    private static final long serialVersionUID = -1L;
    /**
     * Method description.
     */
    public Long dbid;
    String LoginName;//
    int unitType;//0 1
    String conversionRelation;//
    String preUnit;//
    String preName;//
    String afterUnit;//
    String afterName;//
    String conversionFormula;//
    String calcFactor;//


    public Long getDbid() {
        return dbid;
    }

    /**
     * Method description.
     */
    public void setDbid(Long dbid) {
        this.dbid = dbid;
    }

    /**
     * Method description.
     */
    public String getLoginName() {
        return LoginName;
    }

    /**
     * Method description.
     */
    public void setLoginName(String loginName) {
        LoginName = loginName;
    }

    /**
     * Method description.
     */
    public int getUnitType() {
        return unitType;
    }

    /**
     * Method description.
     */
    public void setUnitType(int unitType) {
        this.unitType = unitType;
    }

    /**
     * Method description.
     */
    public String getConversionRelation() {
        return conversionRelation;
    }

    /**
     * Method description.
     */
    public void setConversionRelation(String conversionRelation) {
        this.conversionRelation = conversionRelation;
    }

    /**
     * Method description.
     */
    public String getPreUnit() {
        return preUnit;
    }

    /**
     * Method description.
     */
    public void setPreUnit(String preUnit) {
        this.preUnit = preUnit;
    }

    /**
     * Method description.
     */
    public String getPreName() {
        return preName;
    }

    /**
     * Method description.
     */
    public void setPreName(String preName) {
        this.preName = preName;
    }

    /**
     * Method description.
     */
    public String getAfterUnit() {
        return afterUnit;
    }

    /**
     * Method description.
     */
    public void setAfterUnit(String afterUnit) {
        this.afterUnit = afterUnit;
    }

    /**
     * Method description.
     */
    public String getAfterName() {
        return afterName;
    }

    /**
     * Method description.
     */
    public void setAfterName(String afterName) {
        this.afterName = afterName;
    }

    /**
     * Method description.
     */
    public String getConversionFormula() {
        return conversionFormula;
    }

    /**
     * Method description.
     */
    public void setConversionFormula(String conversionFormula) {
        this.conversionFormula = conversionFormula;
    }

    /**
     * Method description.
     */
    public String getCalcFactor() {
        return calcFactor;
    }

    /**
     * Method description.
     */
    public void setCalcFactor(String calcFactor) {
        this.calcFactor = calcFactor;
    }


}
