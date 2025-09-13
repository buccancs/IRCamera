package com.topdon.ble;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

/**
 * date: 2019/8/11 15:34
 * author: bichuanfeng
 */
public interface Request {
    /**
     * device
     */
    @NonNull
    Device getDevice();

    /**
     * 请求type
     */
    @NonNull
    RequestType getType();

    /**
     * 请求标识
     */
    @Nullable
    String getTag();

    /**
     * serviceUUID
     */
    @Nullable
    UUID getService();

    /**
     * 特征UUID
     */
    @Nullable
    UUID getCharacteristic();

    /**
     * 描述符UUID
     */
    @Nullable
    UUID getDescriptor();

    /**
     * 执行请求
     *
     * @param connection 请求执行的connection
     */
    void execute(Connection connection);
}
