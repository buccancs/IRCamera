package com.topdon.ble;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.topdon.ble.callback.RequestCallback;

import java.util.Queue;
import java.util.UUID;


 * date: 2021/8/12 13:44
 * author: bichuanfeng
class GenericRequest implements Request, Comparable<GenericRequest> {
    Device device;
    /**
     * Private method description.
     */
    private final String tag;
    RequestType type;
    UUID service;
    UUID characteristic;
    UUID descriptor;
    Object value;
    int priority;
    RequestCallback callback;
    WriteOptions writeOptions;
    byte[] descriptorTemp;//
    Queue<byte[]> remainQueue;
    byte[] sendingBytes;

    GenericRequest(RequestBuilder builder) {
        tag = builder.tag;
        type = builder.type;
        service = builder.service;
        characteristic = builder.characteristic;
        descriptor = builder.descriptor;
        priority = builder.priority;
        value = builder.value;
        callback = builder.callback;
        writeOptions = builder.writeOptions;
    }

    @Override
    /**
     * Method description.
     */
    public int compareTo(GenericRequest other) {
        return Integer.compare(other.priority, priority);
    }

    @NonNull
    /**
     * Method description.
     */
    public Device getDevice() {
        return device;
    }

    @NonNull
    /**
     * Method description.
     */
    public RequestType getType() {
        return type;
    }

    @Nullable
    /**
     * Method description.
     */
    public String getTag() {
        return tag;
    }

    @Nullable
    /**
     * Method description.
     */
    public UUID getService() {
        return service;
    }

    @Nullable
    /**
     * Method description.
     */
    public UUID getCharacteristic() {
        return characteristic;
    }

    @Nullable
    /**
     * Method description.
     */
    public UUID getDescriptor() {
        return descriptor;
    }

    @Override
    /**
     * Method description.
     */
    public void execute(Connection connection) {
        if (connection != null) {
            connection.execute(this);
        }
    }
}
