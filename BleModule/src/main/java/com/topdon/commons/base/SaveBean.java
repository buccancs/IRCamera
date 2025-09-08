package com.topdon.commons.base;

public class SaveBean {

    /**
     * Method description.
     */
    public String type;
    public String mac;
    public String name;

    public SaveBean(String type, String mac,String name) {
        this.type = type;
        this.mac = mac;
        this.name = name;
    }

    /**
     * Method description.
     */
    public SaveBean() {
    }

    /**
     * Method description.
     */
    public String getType() {
        return type;
    }

    /**
     * Method description.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Method description.
     */
    public String getMac() {
        return mac;
    }

    /**
     * Method description.
     */
    public void setMac(String mac) {
        this.mac = mac;
    }

    /**
     * Method description.
     */
    public String getName() {
        return name;
    }

    /**
     * Method description.
     */
    public void setName(String name) {
        this.name = name;
    }
}
