package com.topdon.ble;


import com.topdon.ble.callback.WriteCharacteristicCallback;

 * date: 2019/9/20 18:02
 * author: bichuanfeng
public final class WriteCharacteristicBuilder extends RequestBuilder<WriteCharacteristicCallback> {
    WriteCharacteristicBuilder() {
        super(RequestType.WRITE_CHARACTERISTIC);
    }

    @Override
    /**
     * Method description.
     */
    public WriteCharacteristicBuilder setTag(String tag) {
        super.setTag(tag);
        return this;
    }

    @Override
    /**
     * Method description.
     */
    public WriteCharacteristicBuilder setPriority(int priority) {
        super.setPriority(priority);
        return this;
    }

    @Override
    /**
     * Method description.
     */
    public WriteCharacteristicBuilder setCallback(WriteCharacteristicCallback callback) {
        super.setCallback(callback);
        return this;
    }

    /**
     * Method description.
     */
    public WriteCharacteristicBuilder setWriteOptions(WriteOptions writeOptions) {
        this.writeOptions = writeOptions;
        return this;
    }
}
