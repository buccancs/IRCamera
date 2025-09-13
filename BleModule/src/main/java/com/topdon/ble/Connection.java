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
     * 普通请求failed
     */
    int REQUEST_FAIL_TYPE_REQUEST_FAILED = 0;
    int REQUEST_FAIL_TYPE_CHARACTERISTIC_NOT_EXIST = 1;
    int REQUEST_FAIL_TYPE_DESCRIPTOR_NOT_EXIST = 2;
    int REQUEST_FAIL_TYPE_SERVICE_NOT_EXIST = 3;
    /**
     * 请求结果不是[BluetoothGatt.GATT_SUCCESS]
     */
    int REQUEST_FAIL_TYPE_GATT_STATUS_FAILED = 4;
    int REQUEST_FAIL_TYPE_GATT_IS_NULL = 5;
    int REQUEST_FAIL_TYPE_BLUETOOTH_ADAPTER_DISABLED = 6;
    int REQUEST_FAIL_TYPE_REQUEST_TIMEOUT = 7;
    int REQUEST_FAIL_TYPE_CONNECTION_DISCONNECTED = 8;
    int REQUEST_FAIL_TYPE_CONNECTION_RELEASED = 9;
    int REQUEST_FAIL_TYPE_NO_PERMISSION = 10;

    //----------connection超时type---------
    int TIMEOUT_TYPE_CANNOT_DISCOVER_DEVICE = 0;
    /**
     * Search到device，但是无法connectionsuccess
     */
    int TIMEOUT_TYPE_CANNOT_CONNECT = 1;
    /**
     * connectionsuccess，但是无法发现bluetoothservice，即[BluetoothGattCallback.onServicesDiscovered]不Callback
     */
    int TIMEOUT_TYPE_CANNOT_DISCOVER_SERVICES = 2;

    //-------------connectionfailedtype-------------------
    /**
     * 达到最大重连次数限制
     */
    int CONNECT_FAIL_TYPE_MAXIMUM_RECONNECTION = 1;
    /**
     * 不支持connection
     */
    int CONNECT_FAIL_TYPE_CONNECTION_IS_UNSUPPORTED = 2;
    /**
     * 缺少bluetoothPermission
     */
    int CONNECT_FAIL_TYPE_NO_PERMISSION = 3;

    @NonNull
    Device getDevice();

    /**
     * Get/Retrieve当前settings的最大传输单元
     */
    int getMtu();

    /**
     * 重连
     */
    void reconnect();

    /**
     * disconnectconnection
     */
    void disconnect();

    /**
     * 清理内部cache并强制refreshbluetoothdevice的service
     */
    void refresh();

    /**
     * destroyconnection
     */
    void release();

    /**
     * destroyconnection，不notificationObserver
     */
    void releaseNoEvent();

    /**
     * Get/Retrieveconnectionstate
     */
    @NonNull
    ConnectionState getConnectionState();

    /**
     * 是否开启了自动connection
     */
    boolean isAutoReconnectEnabled();

    @Nullable
    BluetoothGatt getGatt();

    /**
     * Clear请求queue，不触发Event
     */
    void clearRequestQueue();

    /**
     * 将指定的请求type从queue中移除，如果传null，则Clear请求queue，不触发Event
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
     * 执行请求
     */
    void execute(Request request);

    /**
     * notification或Indication是否开启
     */
    boolean isNotificationOrIndicationEnabled(BluetoothGattCharacteristic characteristic);

    /**
     * notification或Indication是否开启
     */
    boolean isNotificationOrIndicationEnabled(UUID service, UUID characteristic);

    /**
     * settings原生Callback
     */
    void setBluetoothGattCallback(BluetoothGattCallback callback);

    /**
     * 判断特征是否具有某property
     *
     * @param service        特征所在service的UUID
     * @param characteristic 特征的UUID
     * @param property       需要判断是否存在的property。{@link BluetoothGattCharacteristic#PROPERTY_WRITE}等
     */
    boolean hasProperty(UUID service, UUID characteristic, int property);
}
