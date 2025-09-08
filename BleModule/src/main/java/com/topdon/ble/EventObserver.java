package com.topdon.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.topdon.commons.observer.Observer;

import java.util.UUID;

/**
 * [Chinese text]event. [Chinese text], [Chinese text], [Chinese text], [Chinese text]
 * <p>
 * date: 2021/8/12 13:15
 * author: bichuanfeng
 */
public interface EventObserver extends Observer {
    /**
     * [Chinese text]
     *
     * @param state {@link BluetoothAdapter#STATE_OFF}[Chinese text]
     */
    default void onBluetoothAdapterStateChanged(int state) {
    }

    /**
     * [Chinese text]
     *
     * @param request [Chinese text]
     * @param value   [Chinese text]
     */
    default void onCharacteristicRead(Request request, byte[] value) {
    }

    /**
     * [Chinese text]
     *
     * @param device         [Chinese text]
     * @param service        [Chinese text]UUID
     * @param characteristic [Chinese text]UUID
     * @param value          [Chinese text]
     */
    default void onCharacteristicChanged(Device device, UUID service, UUID characteristic,
                                         byte[] value) {
    }

    /**
     * [Chinese text]
     *
     * @param request [Chinese text]
     * @param value   [Chinese text]
     */
    default void onCharacteristicWrite(Request request, byte[] value) {
    }

    /**
     * [Chinese text]
     *
     * @param request [Chinese text]
     * @param rssi    [Chinese text]
     */
    default void onRssiRead(Request request, int rssi) {
    }

    /**
     * [Chinese text]
     *
     * @param request [Chinese text]
     * @param value   [Chinese text]
     */
    default void onDescriptorRead(Request request, byte[] value) {
    }

    /**
     * [Chinese text] / Indication[Chinese text]
     *
     * @param request   [Chinese text]
     * @param isEnabled [Chinese text]
     */
    default void onNotificationChanged(Request request, boolean isEnabled) {
    }

    /**
     * [Chinese text]
     *
     * @param request [Chinese text]
     * @param mtu     [Chinese text]
     */
    default void onMtuChanged(Request request, int mtu) {
    }

    /**
     * @param request [Chinese text]
     * @param txPhy   [Chinese text]. {@link BluetoothDevice#PHY_LE_1M_MASK}[Chinese text]
     * @param rxPhy   [Chinese text]. {@link BluetoothDevice#PHY_LE_1M_MASK}[Chinese text]
     */
    default void onPhyChange(Request request, int txPhy, int rxPhy) {
    }

    /**
     * [Chinese text]
     *
     * @param request  [Chinese text]
     * @param failType [Chinese text]. {@link Connection#REQUEST_FAIL_TYPE_GATT_IS_NULL}[Chinese text]
     * @param value    [Chinese text], [Chinese text]null
     */
    default void onRequestFailed(Request request, int failType, Object value) {
    }

    /**
     * [Chinese text]
     *
     * @param device [Chinese text]. [Chinese text]{@link Device#getConnectionState()}, [Chinese text]{@link ConnectionState#CONNECTED}[Chinese text]
     */
    default void onConnectionStateChanged(Device device) {
    }

    /**
     * [Chinese text]
     *
     * @param device   [Chinese text]
     * @param failType [Chinese text]. {@link Connection#CONNECT_FAIL_TYPE_MAXIMUM_RECONNECTION}[Chinese text]
     */
    default void onConnectFailed(Device device, int failType) {
    }

    /**
     * [Chinese text]
     *
     * @param device [Chinese text]
     * @param type   [Chinese text]. {@link Connection#TIMEOUT_TYPE_CANNOT_CONNECT}
     */
    default void onConnectTimeout(Device device, int type) {
    }
}
