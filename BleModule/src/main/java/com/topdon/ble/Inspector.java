package com.topdon.ble;

/**
 * date: 2019/8/5 16:10
 * author: bichuanfeng
 */
final class Inspector {
    /**
     * [Chinese text]EasyBLEException
     *
     * @param obj     [Chinese text]
     * @param message [Chinese text]
     */
    static <T> T requireNonNull(T obj, String message) {
        if (obj == null)
            throw new EasyBLEException(message);
        return obj;
    }
}
