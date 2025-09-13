package com.topdon.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.topdon.commons.observer.Observer;

import java.util.UUID;

/**
 * Comment removed (contained Chinese characters)
 * <p>
 * date: 2021/8/12 13:15
* author: bichuanfeng
 */
public interface EventObserver extends Observer {
 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 */
 default void onBluetoothAdapterStateChanged(int state) {
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 default void onCharacteristicRead(Request request, byte[] value) {
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 default void onCharacteristicChanged(Device device, UUID service, UUID characteristic,
 byte[] value) {
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 default void onCharacteristicWrite(Request request, byte[] value) {
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 default void onRssiRead(Request request, int rssi) {
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 default void onDescriptorRead(Request request, byte[] value) {
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 default void onNotificationChanged(Request request, boolean isEnabled) {
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 default void onMtuChanged(Request request, int mtu) {
 }

 /**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 default void onPhyChange(Request request, int txPhy, int rxPhy) {
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 default void onRequestFailed(Request request, int failType, Object value) {
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 */
 default void onConnectionStateChanged(Device device) {
 }

 /**
 * ConnectionFailed
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 default void onConnectFailed(Device device, int failType) {
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 default void onConnectTimeout(Device device, int type) {
 }
}
