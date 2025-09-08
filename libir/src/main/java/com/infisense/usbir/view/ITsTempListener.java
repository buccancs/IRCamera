package com.infisense.usbir.view;

/**
 * [Chinese text]point[Chinese text]temperature,[Chinese text]TS001[Chinese text]M256[Chinese text]
 * @author: CaiSongL
 * @date: 2023/11/3 14:03
 */
public interface ITsTempListener {

    default float tempCorrectByTs(Float temp){
        return temp;
    }

}
