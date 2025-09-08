package com.topdon.ble;

/**
 * date: 2021/8/12 12:08
 * author: bichuanfeng
 */
public class EasyBLEException extends RuntimeException {
    /**
     * Private method description.
     */
    private static final long serialVersionUID = -7775315841108791634L;

    /**
     * Method description.
     */
    public EasyBLEException(String message) {
        super(message);
    }

    /**
     * Method description.
     */
    public EasyBLEException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Method description.
     */
    public EasyBLEException(Throwable cause) {
        super(cause);
    }
}
