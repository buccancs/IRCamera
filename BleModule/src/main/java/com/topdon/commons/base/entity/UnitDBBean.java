package com.topdon.commons.base.entity;

import java.io.Serializable;

/**
 * @Desc 单位conversion
 * @ClassName UnitDBBean
 * @Email 616862466@qq.com
 * @Author 子墨
 * @Date 2022/12/21 15:38
 */
public class UnitDBBean implements Serializable {
//    {
//        "conversion关系": "公转英",
//            "conversion前单位": "m",
//            "conversion前中文name": "米",
//            "conversion后单位": "yd.",
//            "conversion后中文name": "码",
//            "conversion公式": "1 米 = 1.094码",
//            "calculation因子": "1.094"
//    },

    private static final long serialVersionUID = -1L;
    public Long dbid;
    String LoginName;//Login账号
    int unitType;//0 公制type  1 英制type
    String conversionRelation;//conversion关系
    String preUnit;//conversion前单位
    String preName;//conversion前中文name
    String afterUnit;//conversion后单位
    String afterName;//conversion后中文name
    String conversionFormula;//conversion公式
    String calcFactor;//calculation因子

    public Long getDbid() {
        return dbid;
    }

    public void setDbid(Long dbid) {
        this.dbid = dbid;
    }

    public String getLoginName() {
        return LoginName;
    }

    public void setLoginName(String loginName) {
        LoginName = loginName;
    }

    public int getUnitType() {
        return unitType;
    }

    public void setUnitType(int unitType) {
        this.unitType = unitType;
    }

    public String getConversionRelation() {
        return conversionRelation;
    }

    public void setConversionRelation(String conversionRelation) {
        this.conversionRelation = conversionRelation;
    }

    public String getPreUnit() {
        return preUnit;
    }

    public void setPreUnit(String preUnit) {
        this.preUnit = preUnit;
    }

    public String getPreName() {
        return preName;
    }

    public void setPreName(String preName) {
        this.preName = preName;
    }

    public String getAfterUnit() {
        return afterUnit;
    }

    public void setAfterUnit(String afterUnit) {
        this.afterUnit = afterUnit;
    }

    public String getAfterName() {
        return afterName;
    }

    public void setAfterName(String afterName) {
        this.afterName = afterName;
    }

    public String getConversionFormula() {
        return conversionFormula;
    }

    public void setConversionFormula(String conversionFormula) {
        this.conversionFormula = conversionFormula;
    }

    public String getCalcFactor() {
        return calcFactor;
    }

    public void setCalcFactor(String calcFactor) {
        this.calcFactor = calcFactor;
    }

}
