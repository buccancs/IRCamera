package com.topdon.ble.callback;

import com.topdon.ble.Request;

/**
 * date: 2021/8/12 17:40
* author: bichuanfeng
 */
public interface WriteCharacteristicCallback extends RequestFailedCallback {
 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 void onCharacteristicWrite(Request request, byte[] value);
}
