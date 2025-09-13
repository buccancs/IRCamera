package com.topdon.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.topdon.ble.callback.MtuChangeCallback;
import com.topdon.ble.callback.NotificationChangeCallback;
import com.topdon.ble.callback.ReadCharacteristicCallback;
import com.topdon.ble.util.BluetoothPermissionUtils;
import com.topdon.commons.UUIDManager;
import com.topdon.commons.observer.Observable;
import com.topdon.commons.observer.Observe;
import com.topdon.commons.poster.RunOn;
import com.topdon.commons.poster.Tag;
import com.topdon.commons.poster.ThreadMode;
import com.topdon.commons.util.LLog;
import com.topdon.commons.util.StringUtils;
// LMS SDK temporarily disabled - uncomment when dependency is available
// import com.topdon.lms.sdk.xutils.common.util.MD5;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * BluetoothManager
 * Comment removed (contained Chinese characters)
 *
* @author chuanfeng.bi
 * @date 2021/11/19 11:10
 */
public class BluetoothManager implements EventObserver {
 private static final String TAG = "BluetoothManager";
 
 public static boolean iSReset = false;//Whether
 public static boolean isSending = false;//Whether
 public static boolean isClickStopCharging = false;//Whether
 private static BluetoothManager instance = null;
 private Device mDevice;
 private Connection connection;
 public static boolean isReceiveBleData = false;//Whether
 private BluetoothGattCharacteristic writeCharact = null;

 public static BluetoothManager getInstance() {
 if (instance == null)
 instance = new BluetoothManager();
 return instance;
 }

 public BluetoothManager() {
 }

 public Device getDevice() {
 return mDevice;
 }

 private void setMTUValue() {
 if (mDevice.isConnected()) {
 //SettingsMTU
 Log.e("bcf_ble", "Connection：" + mDevice.getName() + "");
 RequestBuilder<MtuChangeCallback> builder = null;
 if (mDevice.getName().contains("T-darts") || mDevice.getName().contains("TD")) {
 builder = new RequestBuilderFactory().getChangeMtuBuilder(240);
 } else {
 builder = new RequestBuilderFactory().getChangeMtuBuilder(503);
 }
 Request request = builder.setCallback(new MtuChangeCallback() {
 @Override
 public void onMtuChanged(@NonNull Request request, int mtu) {
 Log.d("wangchen", "MTUSuccess，Value：" + mtu);
 setReadCallback();
 }

 @Override
 public void onRequestFailed(@NonNull Request request, int failType, @Nullable Object value) {
 Log.d("bcf", "MTUFailed");
 }

 }).build();
 connection.execute(request);
 }
 }

 private void setReadCallback() {
 if (mDevice.isConnected()) {
 isSending = false;
 // Comment removed (contained Chinese characters)
 boolean isEnabled = connection.isNotificationOrIndicationEnabled(UUID.fromString(UUIDManager.SERVICE_UUID), UUID.fromString(UUIDManager.NOTIFY_UUID));
 LLog.w("bcf_ble", "WhetherNotifycation: " + isEnabled);
 RequestBuilder<NotificationChangeCallback> builder = new RequestBuilderFactory().getSetNotificationBuilder(UUID.fromString(UUIDManager.SERVICE_UUID), UUID.fromString(UUIDManager.NOTIFY_UUID), true);
 RequestBuilder<ReadCharacteristicCallback> builder1 = new RequestBuilderFactory().getReadCharacteristicBuilder(UUID.fromString(UUIDManager.SERVICE_UUID), UUID.fromString(UUIDManager.READ_UUID));
 // Comment removed (contained Chinese characters)
 builder.build().execute(connection);
 builder1.build().execute(connection);
 }
 }

 // Comment removed (contained Chinese characters)
 public void setCancelListening() {
 Observable observable = EasyBLE.getInstance().getObservable();
 if (observable != null) {
 EasyBLE.getInstance().unregisterObserver(this);
 }
 }

 public Connection connect(Device device) {
 mDevice = device;
 ConnectionConfiguration config = new ConnectionConfiguration();
 config.setConnectTimeoutMillis(10000);
 config.setRequestTimeoutMillis(7000);
 config.setAutoReconnect(false);
 config.setReconnectImmediatelyMaxTimes(3);
 connection = EasyBLE.getInstance().connect(device, config, this);//ConnectionStatus，SettingsConnectionStatus
 connection.setBluetoothGattCallback(new BluetoothGattCallback() {
 @Override
 public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
 Log.d("ble_bcf_data", "Status：status: " + status + " ：" + StringUtils.toHex(characteristic.getValue()));
 setBleData("Status：status: " + status + " ：" + StringUtils.toHex(characteristic.getValue()));
 }
 });
 return connection;
 }

 public Connection connect(String mac,String name){
 ConnectionConfiguration configuration = new ConnectionConfiguration();
 configuration.setConnectTimeoutMillis(10000);
 configuration.setRequestTimeoutMillis(7000);
 configuration.setAutoReconnect(false);
 configuration.setReconnectImmediatelyMaxTimes(3);
 connection = EasyBLE.getInstance().connect(mac, configuration, this);
 mDevice = connection.getDevice();
 mDevice.setName(name);
 return connection;
 }

 public void release() {
 Log.d("bcf", "BLEConnection");
 EasyBLE.getInstance().disconnectConnection(mDevice);
 EasyBLE.getInstance().release();
 EasyBLE.getInstance().releaseConnection(mDevice);
 }

 public boolean isConnected() {
 if (mDevice == null)
 return false;
 return mDevice.isConnected();
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 @Tag("onConnectionStateChanged")
 @Observe
 @RunOn(ThreadMode.MAIN)
 @Override
 public void onConnectionStateChanged(@NonNull Device device) {
 if (device.getConnectionState() != ConnectionState.SERVICE_DISCOVERED || device.getConnectionState() != ConnectionState.DISCONNECTED) {
 EventBus.getDefault().post(device.getConnectionState());
 Log.e("wangchen", "--" + device.getConnectionState());
 }
 Log.d("ywq", "MyObserver ConnectionStatus：" + device.getConnectionState() + " WhetherConnection： " + device.isConnected() + "-----：" + device.getName() + "-------mac: " + device.getAddress());
 switch (device.getConnectionState()) {
 case SCANNING_FOR_RECONNECTION:
 break;
 case CONNECTING:
 break;
 case CONNECTED:
 break;
 case DISCONNECTED:
 EventBus.getDefault().post(ConnectionState.DISCONNECTED.name());
 break;
 case RELEASED:
 EventBus.getDefault().post(ConnectionState.RELEASED.name());
 break;
 case SERVICE_DISCOVERED:
 setMTUValue();
// setReadCallback();
 if (device.isConnected()) {
 EventBus.getDefault().post(ConnectionState.SERVICE_DISCOVERED.name());
 }
 break;
 }
 }

 @Override
 public void onConnectFailed(Device device, int failType) {
 Log.e("bcf_ble", "ConnectionFailed" + device.getName());
 EventBus.getDefault().post(device.getConnectionState());
 }

 @Override
 public void onConnectTimeout(Device device, int type) {
 Log.e("bcf_ble", "Connection");
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 @Observe
 @Override
 public void onNotificationChanged(@NonNull Request request, boolean isEnabled) {
 String typeTag = "";
 if (request.getType() == RequestType.SET_NOTIFICATION) {
 typeTag = "";
 EventBus.getDefault().post(ConnectionState.MTU_SUCCESS);
 } else {
 typeTag = "Indication";
 }
 Log.d("bcf_ble", "onNotificationChanged ：" + typeTag + "：" + (isEnabled ? "" : ""));
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * @param data
 */
 public boolean writeBuletoothData(byte[] data) {
 if (mDevice == null || !mDevice.isConnected()) {
 return false;
 }
 
 // Check BLUETOOTH_CONNECT permission before GATT operations
 if (!com.topdon.ble.util.BluetoothPermissionUtils.hasBluetoothConnectPermission(EasyBLE.getInstance().getContext())) {
 Log.w(TAG, "Missing BLUETOOTH_CONNECT permission for GATT operations");
 return false;
 }
 
 try {
 writeCharact = connection.getCharacteristic(UUID.fromString(UUIDManager.SERVICE_UUID), UUID.fromString(UUIDManager.WRITE_UUID));
 connection.getGatt().setCharacteristicNotification(writeCharact, true); // Settings
 // Comment removed (contained Chinese characters)
 writeCharact.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
 writeCharact.setValue(data);
// Comment removed (contained Chinese characters)
 return connection.getGatt().writeCharacteristic(writeCharact);
 } catch (SecurityException e) {
 Log.e(TAG, "SecurityException during GATT write operation: " + e.getMessage());
 return false;
 }
 }

 @Observe
 @Override
 public void onCharacteristicRead(Request request, byte[] value) {
 // Comment removed (contained Chinese characters)
 String data = StringUtils.toHex(value); // String
// Log.d("ble_bcf_data", "onCharacteristicRead: " + data);
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 @Observe
 @Override
 public void onCharacteristicChanged(Device device, UUID service, UUID characteristic, byte[] value) {
 Log.e("ble_bcf_data", "：" + StringUtils.toHex(value));
 EventBus.getDefault().post(value);
 }

 public static void setBleData(String message) {
// String savePath = ActivityUtils.getTopActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
// SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
// Comment removed (contained Chinese characters)
// Date date = new Date(System.currentTimeMillis());
//
// SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
// Comment removed (contained Chinese characters)
// Date date1 = new Date(System.currentTimeMillis());
//
// FileIOUtils.writeFileFromString(savePath + "/log/" + simpleDateFormat.format(date) + ".txt", simpleDateFormat1.format(date1) + ":" + message + "\n", true);
 }

}
