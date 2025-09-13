package com.topdon.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.topdon.commons.observer.Observer;

import java.util.UUID;

/**
 * 各种Event。bluetoothstate，connectionstate，读取到特征值，写入结果Callback等等
 * <p>
 * date: 2021/8/12 13:15
 * author: bichuanfeng
 */
public interface EventObserver extends Observer {
    /**
     * bluetooth开关state变化
     *
     * @param state {@link BluetoothAdapter#STATE_OFF}等
     */
    default void onBluetoothAdapterStateChanged(int state) {
    }

    /**
     * 读取到特征值
     *
     * @param request 请求
     * @param value   读取到的data
     */
    default void onCharacteristicRead(Request request, byte[] value) {
    }

    /**
     * 特征值变化
     *
     * @param device         device
     * @param service        serviceUUID
     * @param characteristic 特征UUID
     * @param value          data
     */
    default void onCharacteristicChanged(Device device, UUID service, UUID characteristic,
                                         byte[] value) {
    }

    /**
     * success写入特征值
     *
     * @param request 请求
     * @param value   写入的data
     */
    default void onCharacteristicWrite(Request request, byte[] value) {
    }

    /**
     * 读取到device的信号强度
     *
     * @param request 请求
     * @param rssi    信号强度
     */
    default void onRssiRead(Request request, int rssi) {
    }

    /**
     * 读取到描述符值
     *
     * @param request 请求
     * @param value   读取到的data
     */
    default void onDescriptorRead(Request request, byte[] value) {
    }

    /**
     * notification开关变化 / Indication开关变化
     *
     * @param request   请求
     * @param isEnabled 开启或Close
     */
    default void onNotificationChanged(Request request, boolean isEnabled) {
    }

    /**
     * 最大传输单元变化
     *
     * @param request 请求
     * @param mtu     最大传输单元新的值
     */
    default void onMtuChanged(Request request, int mtu) {
    }

    /**
     * @param request 请求
     * @param txPhy   物理层Send器偏好。{@link BluetoothDevice#PHY_LE_1M_MASK}等
     * @param rxPhy   物理层Receive器偏好。{@link BluetoothDevice#PHY_LE_1M_MASK}等
     */
    default void onPhyChange(Request request, int txPhy, int rxPhy) {
    }

    /**
     * 请求failed
     *
     * @param request  请求
     * @param failType failedtype。{@link Connection#REQUEST_FAIL_TYPE_GATT_IS_NULL}等
     * @param value    请求时带的data，可能为null
     */
    default void onRequestFailed(Request request, int failType, Object value) {
    }

    /**
     * connectionstate变化
     *
     * @param device device。state{@link Device#getConnectionState()}，可能的值{@link ConnectionState#CONNECTED}等
     */
    default void onConnectionStateChanged(Device device) {
    }

    /**
     * connectionfailed
     *
     * @param device   device
     * @param failType failedtype。{@link Connection#CONNECT_FAIL_TYPE_MAXIMUM_RECONNECTION}等
     */
    default void onConnectFailed(Device device, int failType) {
    }

    /**
     * connection超时
     *
     * @param device device
     * @param type   原因。{@link Connection#TIMEOUT_TYPE_CANNOT_CONNECT}
     */
    default void onConnectTimeout(Device device, int type) {
    }
}
