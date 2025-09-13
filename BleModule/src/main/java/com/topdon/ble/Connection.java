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
 * Comment removed (contained Chinese characters)
 */
 int REQUEST_FAIL_TYPE_REQUEST_FAILED = 0;
 int REQUEST_FAIL_TYPE_CHARACTERISTIC_NOT_EXIST = 1;
 int REQUEST_FAIL_TYPE_DESCRIPTOR_NOT_EXIST = 2;
 int REQUEST_FAIL_TYPE_SERVICE_NOT_EXIST = 3;
 /**
 * Comment removed (contained Chinese characters)
 */
 int REQUEST_FAIL_TYPE_GATT_STATUS_FAILED = 4;
 int REQUEST_FAIL_TYPE_GATT_IS_NULL = 5;
 int REQUEST_FAIL_TYPE_BLUETOOTH_ADAPTER_DISABLED = 6;
 int REQUEST_FAIL_TYPE_REQUEST_TIMEOUT = 7;
 int REQUEST_FAIL_TYPE_CONNECTION_DISCONNECTED = 8;
 int REQUEST_FAIL_TYPE_CONNECTION_RELEASED = 9;
 int REQUEST_FAIL_TYPE_NO_PERMISSION = 10;

 // Comment removed (contained Chinese characters)
 int TIMEOUT_TYPE_CANNOT_DISCOVER_DEVICE = 0;
 /**
 * Comment removed (contained Chinese characters)
 */
 int TIMEOUT_TYPE_CANNOT_CONNECT = 1;
 /**
 * Comment removed (contained Chinese characters)
 */
 int TIMEOUT_TYPE_CANNOT_DISCOVER_SERVICES = 2;

 //-------------ConnectionFailedType-------------------
 /**
 * Comment removed (contained Chinese characters)
 */
 int CONNECT_FAIL_TYPE_MAXIMUM_RECONNECTION = 1;
 /**
 * Comment removed (contained Chinese characters)
 */
 int CONNECT_FAIL_TYPE_CONNECTION_IS_UNSUPPORTED = 2;
 /**
 * Comment removed (contained Chinese characters)
 */
 int CONNECT_FAIL_TYPE_NO_PERMISSION = 3;

 @NonNull
 Device getDevice();

 /**
 * Comment removed (contained Chinese characters)
 */
 int getMtu();

 /**
 * Comment removed (contained Chinese characters)
 */
 void reconnect();

 /**
 * DisconnectConnection
 */
 void disconnect();

 /**
 * Comment removed (contained Chinese characters)
 */
 void refresh();

 /**
 * Comment removed (contained Chinese characters)
 */
 void release();

 /**
 * Comment removed (contained Chinese characters)
 */
 void releaseNoEvent();

 /**
 * Comment removed (contained Chinese characters)
 */
 @NonNull
 ConnectionState getConnectionState();

 /**
 * Comment removed (contained Chinese characters)
 */
 boolean isAutoReconnectEnabled();

 @Nullable
 BluetoothGatt getGatt();

 /**
 * Comment removed (contained Chinese characters)
 */
 void clearRequestQueue();

 /**
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 */
 void execute(Request request);

 /**
 * Comment removed (contained Chinese characters)
 */
 boolean isNotificationOrIndicationEnabled(BluetoothGattCharacteristic characteristic);

 /**
 * Comment removed (contained Chinese characters)
 */
 boolean isNotificationOrIndicationEnabled(UUID service, UUID characteristic);

 /**
 * Comment removed (contained Chinese characters)
 */
 void setBluetoothGattCallback(BluetoothGattCallback callback);

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 boolean hasProperty(UUID service, UUID characteristic, int property);
}
