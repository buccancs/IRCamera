package com.topdon.ble;

import android.bluetooth.BluetoothDevice;
import android.os.Build;

import androidx.annotation.IntRange;

import androidx.annotation.RequiresApi;

import com.topdon.ble.callback.MtuChangeCallback;
import com.topdon.ble.callback.NotificationChangeCallback;
import com.topdon.ble.callback.PhyChangeCallback;
import com.topdon.ble.callback.ReadCharacteristicCallback;
import com.topdon.ble.callback.ReadRssiCallback;

import java.util.UUID;

/**
 * date: 2019/9/20 18:06
 * author: bichuanfeng
 */
public class RequestBuilderFactory {
    /**
     * Get/Retrievemodify最大传输单元请求Build器
     *
     * @param mtu 要modify成的值
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public RequestBuilder<MtuChangeCallback> getChangeMtuBuilder(@IntRange(from = 23, to = 517) int mtu) {
        if (mtu < 23) {
            mtu = 23;
        } else if (mtu > 517) {
            mtu = 517;
        }
        RequestBuilder<MtuChangeCallback> builder = new RequestBuilder<>(RequestType.CHANGE_MTU);
        builder.value = mtu;
        return builder;
    }

    /**
     * Get/Retrieve读取bluetoothdevice的特征请求Build器
     *
     * @param service        serviceUUID
     * @param characteristic 特征UUID
     */
    public RequestBuilder<ReadCharacteristicCallback> getReadCharacteristicBuilder(UUID service, UUID characteristic) {
        RequestBuilder<ReadCharacteristicCallback> builder = new RequestBuilder<>(RequestType.READ_CHARACTERISTIC);
        builder.service = service;
        builder.characteristic = characteristic;
        return builder;
    }

    /**
     * Get/Retrieve开关datanotification请求Build器
     *
     * @param service        serviceUUID
     * @param characteristic 特征UUID
     * @param enable         开启或Close
     */
    public RequestBuilder<NotificationChangeCallback> getSetNotificationBuilder(UUID service, UUID characteristic,
                                                                                boolean enable) {
        RequestBuilder<NotificationChangeCallback> builder = new RequestBuilder<>(RequestType.SET_NOTIFICATION);
        builder.service = service;
        builder.characteristic = characteristic;
        builder.value = enable ? 1 : 0;
        return builder;
    }

    /**
     * Get/Retrieve开关Indication请求Build器
     *
     * @param service        serviceUUID
     * @param characteristic 特征UUID
     * @param enable         开启或Close
     */
    public RequestBuilder<NotificationChangeCallback> getSetIndicationBuilder(UUID service, UUID characteristic,
                                                                               boolean enable) {
        RequestBuilder<NotificationChangeCallback> builder = new RequestBuilder<>(RequestType.SET_INDICATION);
        builder.service = service;
        builder.characteristic = characteristic;
        builder.value = enable ? 1 : 0;
        return builder;
    }

    /**
     * Get/Retrieve读取描述符的值请求Build器
     *
     * @param service        serviceUUID
     * @param characteristic 特征UUID
     * @param descriptor     描述符UUID
     */
    public RequestBuilder<NotificationChangeCallback> getReadDescriptorBuilder(UUID service, UUID characteristic,
                                                                                UUID descriptor) {
        RequestBuilder<NotificationChangeCallback> builder = new RequestBuilder<>(RequestType.READ_DESCRIPTOR);
        builder.service = service;
        builder.characteristic = characteristic;
        builder.descriptor = descriptor;
        return builder;
    }

    /**
     * Get/Retrieve向特征写入请求Build器
     *
     * @param service        serviceUUID
     * @param characteristic 特征UUID
     * @param value          要写入特征的值
     */
    public WriteCharacteristicBuilder getWriteCharacteristicBuilder(UUID service, UUID characteristic,
                                                                            byte[] value) {
        Inspector.requireNonNull(value, "value can't be null");
        WriteCharacteristicBuilder builder = new WriteCharacteristicBuilder();
        builder.service = service;
        builder.characteristic = characteristic;
        builder.value = value;
        return builder;
    }

    /**
     * Get/Retrieve读取已connection的bluetoothdevice的信号强度请求Build器
     */
    public RequestBuilder<ReadRssiCallback> getReadRssiBuilder() {
        return new RequestBuilder<>(RequestType.READ_RSSI);
    }

    /**
     * Get/Retrieve读取物理层Send器和Receive器请求Build器
     */
    @RequiresApi(Build.VERSION_CODES.O)
    public RequestBuilder<PhyChangeCallback> getReadPhyBuilder() {
        return new RequestBuilder<>(RequestType.READ_PHY);
    }

    /**
     * Get/Retrievesettings物理层Send器和Receive器偏好请求Build器
     *
     * @param txPhy      物理层Send器偏好。{@link BluetoothDevice#PHY_LE_1M_MASK}等
     * @param rxPhy      物理层Receive器偏好。{@link BluetoothDevice#PHY_LE_1M_MASK}等
     * @param phyOptions 物理层BLE首选传输encoding。{@link BluetoothDevice#PHY_OPTION_NO_PREFERRED}等
     */
    @RequiresApi(Build.VERSION_CODES.O)
    public RequestBuilder<PhyChangeCallback> getSetPreferredPhyBuilder(int txPhy, int rxPhy, int phyOptions) {
        RequestBuilder<PhyChangeCallback> builder = new RequestBuilder<>(RequestType.SET_PREFERRED_PHY);
        builder.value = new int[]{txPhy, rxPhy, phyOptions};
        return builder;
    }
}
