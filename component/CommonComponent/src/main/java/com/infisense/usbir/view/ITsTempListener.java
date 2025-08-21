package com.infisense.usbir.view;

/**
 * Temperature listener interface for thermal component compatibility
 */
public interface ITsTempListener {

    default float tempCorrectByTs(Float temp){
        return temp;
    }

}