package com.infisense.usbir.view;

/**
* @author: CaiSongL
 * @date: 2023/11/3 14:03
 */
public interface ITsTempListener {

 default float tempCorrectByTs(Float temp){
 return temp;
 }

}
