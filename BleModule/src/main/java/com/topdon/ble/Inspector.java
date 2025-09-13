package com.topdon.ble;

/**
 * date: 2019/8/5 16:10
* author: bichuanfeng
 */
final class Inspector {
 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 static <T> T requireNonNull(T obj, String message) {
 if (obj == null)
 throw new EasyBLEException(message);
 return obj;
 }
}
