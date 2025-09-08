package com.topdon.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

/**
 * date: 2021/8/12 13:45
 * author: bichuanfeng
 */
public interface Connection {
    UUID clientCharacteristicConfig = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    /**
     * [Chinese text]
     */
    int REQUEST_FAIL_TYPE_REQUEST_FAILED = 0;
    int REQUEST_FAIL_TYPE_CHARACTERISTIC_NOT_EXIST = 1;
    int REQUEST_FAIL_TYPE_DESCRIPTOR_NOT_EXIST = 2;
    int REQUEST_FAIL_TYPE_SERVICE_NOT_EXIST = 3;
    /**
     * [Chinese text][BluetoothGatt.GATT_SUCCESS]
     */
    int REQUEST_FAIL_TYPE_GATT_STATUS_FAILED = 4;
    int REQUEST_FAIL_TYPE_GATT_IS_NULL = 5;
    int REQUEST_FAIL_TYPE_BLUETOOTH_ADAPTER_DISABLED = 6;
    int REQUEST_FAIL_TYPE_REQUEST_TIMEOUT = 7;
    int REQUEST_FAIL_TYPE_CONNECTION_DISCONNECTED = 8;
    int REQUEST_FAIL_TYPE_CONNECTION_RELEASED = 9;

    // ----------[Chinese text]---------
    int TIMEOUT_TYPE_CANNOT_DISCOVER_DEVICE = 0;
    /**
     * [Chinese text], [Chinese text]
     */
    int TIMEOUT_TYPE_CANNOT_CONNECT = 1;
    /**
     * [Chinese text], [Chinese text], [Chinese text][BluetoothGattCallback.onServicesDiscovered][Chinese text]
     */
    int TIMEOUT_TYPE_CANNOT_DISCOVER_SERVICES = 2;

    // -------------[Chinese text]-------------------
    /**
     * [Chinese text]
     */
    int CONNECT_FAIL_TYPE_MAXIMUM_RECONNECTION = 1;
    /**
     * [Chinese text]
     */
    int CONNECT_FAIL_TYPE_CONNECTION_IS_UNSUPPORTED = 2;

    @NonNull
    Device getDevice();

    /**
     * [Chinese text]Settings[Chinese text]
     */
    int getMtu();

    /**
     * [Chinese text]
     */
    void reconnect();

    /**
     * [Chinese text]
     */
    void disconnect();

    /**
     * [Chinese text]
     */
    void refresh();

    /**
     * [Chinese text]
     */
    void release();

    /**
     * [Chinese text], [Chinese text]observation[Chinese text]
     */
    void releaseNoEvent();

    /**
     * [Chinese text]
     */
    @NonNull
    ConnectionState getConnectionState();

    /**
     * [Chinese text]
     */
    boolean isAutoReconnectEnabled();

    @Nullable
    BluetoothGatt getGatt();

    /**
     * [Chinese text], [Chinese text]event
     */
    void clearRequestQueue();

    /**
     * [Chinese text]in progress[Chinese text], [Chinese text]null, [Chinese text], [Chinese text]event
     */
    void clearRequestQueueByType(RequestType type);

    @NonNull
    ConnectionConfiguration getConnectionConfiguration();

    @Nullable
    BluetoothGattService getService(UUID service);

    @Nullable
    BluetoothGattCharacteristic getCharacteristic(UUID service, UUID characteristic);

    @Nullable
    BluetoothGattDescriptor getDescriptor(UUID service, UUID characteristic, UUID descriptor);

    /**
     * [Chinese text]
     */
    void execute(Request request);

    /**
     * [Chinese text]Indication[Chinese text]
     */
    boolean isNotificationOrIndicationEnabled(BluetoothGattCharacteristic characteristic);

    /**
     * [Chinese text]Indication[Chinese text]
     */
    boolean isNotificationOrIndicationEnabled(UUID service, UUID characteristic);

    /**
     * Settings[Chinese text]
     */
    void setBluetoothGattCallback(BluetoothGattCallback callback);

    /**
     * [Chinese text]
     *
     * @param service        [Chinese text]UUID
     * @param characteristic [Chinese text]UUID
     * @param property       [Chinese text]. {@link BluetoothGattCharacteristic#PROPERTY_WRITE}[Chinese text]
     */
    boolean hasProperty(UUID service, UUID characteristic, int property);
}
