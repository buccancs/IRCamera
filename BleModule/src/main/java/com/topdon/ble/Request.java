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
     * [Chinese text]
     */
    @NonNull
    Device getDevice();

    /**
     * [Chinese text]
     */
    @NonNull
    RequestType getType();

    /**
     * [Chinese text]
     */
    @Nullable
    String getTag();

    /**
     * [Chinese text]UUID
     */
    @Nullable
    UUID getService();

    /**
     * [Chinese text]UUID
     */
    @Nullable
    UUID getCharacteristic();

    /**
     * [Chinese text]UUID
     */
    @Nullable
    UUID getDescriptor();

    /**
     * [Chinese text]
     *
     * @param connection [Chinese text]
     */
    void execute(Connection connection);
}
