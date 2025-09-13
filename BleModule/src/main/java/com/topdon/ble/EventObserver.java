package com.topdon.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.topdon.commons.observer.Observer;

import java.util.UUID;

/**
 * <p>
 * date: 2021/8/12 13:15
* author: bichuanfeng
 */
public interface EventObserver extends Observer {
 /**
 *
 */
 default void onBluetoothAdapterStateChanged(int state) {
 }

 /**
 *
 */
 default void onCharacteristicRead(Request request, byte[] value) {
 }

 /**
 *
 */
 default void onCharacteristicChanged(Device device, UUID service, UUID characteristic,
 byte[] value) {
 }

 /**
 *
 */
 default void onCharacteristicWrite(Request request, byte[] value) {
 }

 /**
 *
 */
 default void onRssiRead(Request request, int rssi) {
 }

 /**
 *
 */
 default void onDescriptorRead(Request request, byte[] value) {
 }

 /**
 *
 */
 default void onNotificationChanged(Request request, boolean isEnabled) {
 }

 /**
 *
 */
 default void onMtuChanged(Request request, int mtu) {
 }

 /**
 */
 default void onPhyChange(Request request, int txPhy, int rxPhy) {
 }

 /**
 *
 */
 default void onRequestFailed(Request request, int failType, Object value) {
 }

 /**
 *
 */
 default void onConnectionStateChanged(Device device) {
 }

 /**
 * ConnectionFailed
 *
 */
 default void onConnectFailed(Device device, int failType) {
 }

 /**
 *
 */
 default void onConnectTimeout(Device device, int type) {
 }
}
