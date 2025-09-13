package com.topdon.ble;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.topdon.ble.callback.ScanListener;
import com.topdon.ble.util.BluetoothPermissionUtils;
import com.topdon.ble.util.DefaultLogger;
import com.topdon.ble.util.Logger;
import com.topdon.commons.observer.Observable;
import com.topdon.commons.poster.MethodInfo;
import com.topdon.commons.poster.PosterDispatcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

/**
 * Enhanced EasyBLE with unified Shimmer Nordic and Topdon BLE integration.
 * 
 * This class now provides comprehensive BLE support through the UnifiedBleManager,
 * maintaining full backward compatibility while adding enterprise-grade features
 * for both Shimmer and Topdon BLE devices.
 * 
 * date: 2021/8/12 11:50
 * author: bichuanfeng
 * enhanced: IRCamera Unified BLE Integration Team
 */
public class EasyBLE {
    static volatile EasyBLE instance;
    private static final EasyBLEBuilder DEFAULT_BUILDER = new EasyBLEBuilder();
    private final ExecutorService executorService;
    private final PosterDispatcher posterDispatcher;
    private final BondController bondController;
    private final DeviceCreator deviceCreator;
    private final Observable observable;
    
    // Unified BLE manager for comprehensive device support
    private UnifiedBleManager unifiedBleManager;
    private final Logger logger;
    private final ScannerType scannerType;
    public final ScanConfiguration scanConfiguration;
    private Scanner scanner;
    private Application application;
    private boolean isInitialized;
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver broadcastReceiver;
    private final Map<String, Connection> connectionMap = new ConcurrentHashMap<>();
    //已connection的deviceMAC地址集合
    private final List<String> addressList = new CopyOnWriteArrayList<>();
    private final boolean useNordicBleBackend;
    private final boolean internalObservable;

    private EasyBLE() {
        this(DEFAULT_BUILDER);
    }

    EasyBLE(EasyBLEBuilder builder) {
        tryGetApplication();
        bondController = builder.bondController;
        scannerType = builder.scannerType;
        useNordicBleBackend = builder.useNordicBleBackend;
        deviceCreator = builder.deviceCreator == null ? new DefaultDeviceCreator() : builder.deviceCreator;
        scanConfiguration = builder.scanConfiguration == null ? new ScanConfiguration() : builder.scanConfiguration;
        logger = builder.logger == null ? new DefaultLogger("EasyBLE") : builder.logger;
        if (builder.observable != null) {
            internalObservable = false;
            observable = builder.observable;
            posterDispatcher = observable.getPosterDispatcher();
            executorService = posterDispatcher.getExecutorService();
        } else {
            internalObservable = true;
            executorService = builder.executorService;
            posterDispatcher = new PosterDispatcher(executorService, builder.methodDefaultThreadMode);
            observable = new Observable(posterDispatcher, builder.isObserveAnnotationRequired);
        }
        
        // Initialize unified BLE manager for comprehensive Shimmer and Topdon support
        if (application != null) {
            unifiedBleManager = UnifiedBleManager.getInstance(application);
        }
    }

    /**
     * Get/Retrieve实例。Singleton的
     */
    public static EasyBLE getInstance() {
        if (instance == null) {
            synchronized (EasyBLE.class) {
                if (instance == null) {
                    instance = new EasyBLE();
                }
            }
        }
        return instance;
    }

    public static EasyBLEBuilder getBuilder() {
        return new EasyBLEBuilder();
    }

    @Nullable
    Context getContext() {
        if (application == null) {
            tryAutoInit();
        }
        return application;
    }

    @SuppressLint("PrivateApi")
    private void tryGetApplication() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityThread");
            Method method = cls.getMethod("currentActivityThread");
            method.setAccessible(true);
            Object acThread = method.invoke(null);
            Method appMethod = acThread.getClass().getMethod("getApplication");
            application = (Application) appMethod.invoke(acThread);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    ExecutorService getExecutorService() {
        return executorService;
    }

    PosterDispatcher getPosterDispatcher() {
        return posterDispatcher;
    }

    DeviceCreator getDeviceCreator() {
        return deviceCreator;
    }

    Observable getObservable() {        
        return observable;
    }

    Logger getLogger() {
        return logger;
    }

    public ScannerType getScannerType() {
        return scanner == null ? null : scanner.getType();
    }

    public boolean isInitialized() {
        return isInitialized && application != null && instance != null;
    }

    /**
     * bluetooth是否开启
     */
    public boolean isBluetoothOn() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    /**
     * Get the unified BLE manager for comprehensive Shimmer and Topdon device support.
     * 
     * @return UnifiedBleManager instance, or null if not initialized
     */
    @Nullable
    public UnifiedBleManager getUnifiedBleManager() {
        if (unifiedBleManager == null && application != null) {
            unifiedBleManager = UnifiedBleManager.getInstance(application);
        }
        return unifiedBleManager;
    }

    /**
     * Check if unified BLE manager is available and initialized.
     * 
     * @return true if unified manager is ready for use
     */
    public boolean isUnifiedBleManagerReady() {
        UnifiedBleManager manager = getUnifiedBleManager();
        return manager != null && manager.initialize();
    }

    private class InnerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED: //bluetooth开关state变化 
                        if (bluetoothAdapter != null) {
                            //notificationObserverbluetoothstate
                            observable.notifyObservers(MethodInfoGenerator.onBluetoothAdapterStateChanged(bluetoothAdapter.getState()));
                            if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) { //bluetoothClose
                                logger.log(Log.DEBUG, Logger.TYPE_GENERAL, "bluetoothClose了");
                                //notificationSearch器
                                if (scanner != null) {
                                    scanner.onBluetoothOff();
                                }
                                //disconnect所有connection
                                disconnectAllConnections();
                            } else if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                logger.log(Log.DEBUG, Logger.TYPE_GENERAL, "bluetooth开启了");
                                //重连所有settings了自动重连的connection
                                for (Connection connection : connectionMap.values()) {
                                    if (connection.isAutoReconnectEnabled()) {
                                        connection.reconnect();
                                    }
                                }
                            }
                        }
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        if (scanner instanceof ClassicScanner) {
                            ClassicScanner scanner = (ClassicScanner) EasyBLE.this.scanner;
                            scanner.setScanning(true);
                        }
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        if (scanner instanceof ClassicScanner) {
                            ClassicScanner scanner = (ClassicScanner) EasyBLE.this.scanner;
                            scanner.setScanning(false);
                        }
                        break;
                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device != null && scanner instanceof ClassicScanner) {
                            int rssi = -120;
                            Bundle extras = intent.getExtras();
                            if (extras != null) {
                                rssi = extras.getShort(BluetoothDevice.EXTRA_RSSI);
                            }                            
                            ((ClassicScanner) scanner).parseScanResult(device, false, null, rssi, null);
                        }
                        break;    
                }
            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) { //bluetooth开关state变化 
                if (bluetoothAdapter != null) {
                    //notificationObserverbluetoothstate
                    observable.notifyObservers(MethodInfoGenerator.onBluetoothAdapterStateChanged(bluetoothAdapter.getState()));
                    if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) { //bluetoothClose
                        logger.log(Log.DEBUG, Logger.TYPE_GENERAL, "bluetoothClose了");
                        //notificationSearch器
                        if (scanner != null) {
                            scanner.onBluetoothOff();
                        }
                        //disconnect所有connection
                        disconnectAllConnections();
                    } else if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                        logger.log(Log.DEBUG, Logger.TYPE_GENERAL, "bluetooth开启了");
                        //重连所有settings了自动重连的connection
                        for (Connection connection : connectionMap.values()) {
                            if (connection.isAutoReconnectEnabled()) {
                                connection.reconnect();
                            }
                        }
                    }
                }
            }
        }
    }

    public synchronized void initialize(Application application) {
        if (isInitialized()) {
            return;
        }
        Inspector.requireNonNull(application, "application can't be");
        this.application = application;
        //Check是否支持BLE
        if (!application.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return;
        }
        //Get/Retrievebluetoothconfiguration器
        BluetoothManager bluetoothManager = (BluetoothManager) application.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null || bluetoothManager.getAdapter() == null) {
            return;
        }
        bluetoothAdapter = bluetoothManager.getAdapter();
        //Registerbluetooth开关state广播Receive者
        if (broadcastReceiver == null) {
            broadcastReceiver = new InnerBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            application.registerReceiver(broadcastReceiver, filter);
        }        
        isInitialized = true;
    }

    private synchronized boolean checkStatus() {
        Inspector.requireNonNull(instance, "EasyBLE instance has been destroyed!");
        if (!isInitialized) {
            if (!tryAutoInit()) {
                String msg = "The SDK has not been initialized, make sure to call EasyBLE.getInstance().initialize(Application) first.";
                logger.log(Log.ERROR, Logger.TYPE_GENERAL, msg);
                return false;
            }
        } else if (application == null) {
            return tryAutoInit();
        }
        return true;
    }

    private boolean tryAutoInit() {
        tryGetApplication();
        if (application != null) {
            initialize(application);
        }
        return isInitialized();
    }

    /**
     * Log输出控制
     */
    public void setLogEnabled(boolean isEnabled) {
        logger.setEnabled(isEnabled);
    }

    /**
     * Close所有connection并release资源
     */
    public synchronized void release() {
        if (broadcastReceiver != null) {
            application.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        isInitialized = false;
        if (scanner != null) {
            scanner.release();
        }
        releaseAllConnections();
        if (internalObservable) {
            observable.unregisterAll();
            posterDispatcher.clearTasks();
        }
    }

    /**
     * destroy，可重新Build
     */
    public void destroy() {
        release();
        synchronized (EasyBLE.class) {
            instance = null;
        }
    }

    /**
     * Registerconnectionstate及dataReceiveObserver
     */
    public void registerObserver(EventObserver observer) {
        if (checkStatus()) {
            observable.registerObserver(observer);
        }
    }

    /**
     * 查询Observer是否Register
     */
    public boolean isObserverRegistered(EventObserver observer) {
        return observable.isRegistered(observer);
    }

    /**
     * CancelRegisterconnectionstate及dataReceiveObserver
     */
    public void unregisterObserver(EventObserver observer) {
        observable.unregisterObserver(observer);
    }

    /**
     * notification所有ObserverEvent变化，通常只用在
     *
     * @param info methodinfo实例
     */
    public void notifyObservers(MethodInfo info) {
        if (checkStatus()) {
            observable.notifyObservers(info);
        }
    }
    
    //Check并实例化Search器
    private void checkAndInstanceScanner() {
        if (scanner == null) {
            synchronized (this) {
                if (bluetoothAdapter != null && scanner == null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (scannerType == ScannerType.LEGACY) {
                            scanner = new LegacyScanner(this, bluetoothAdapter);
                        } else if (scannerType == ScannerType.CLASSIC) {
                            scanner = new ClassicScanner(this, bluetoothAdapter);
                        } else {
                            scanner = new LeScanner(this, bluetoothAdapter);
                        }
                    } else if (scannerType == ScannerType.CLASSIC) {
                        scanner = new ClassicScanner(this, bluetoothAdapter);
                    } else {
                        scanner = new LegacyScanner(this, bluetoothAdapter);
                    }
                }
            }
        }        
    }
    
    /**
     * addSearchListener器
     */
    public void addScanListener(ScanListener listener) {
        checkAndInstanceScanner();
        if (checkStatus() && scanner != null) {
            scanner.addScanListener(listener);
        }
    }

    /**
     * 移除SearchListener器
     */
    public void removeScanListener(ScanListener listener) {
        if (scanner != null) {
            scanner.removeScanListener(listener);
        }
    }

    /**
     * 是否正在Search
     */
    public boolean isScanning() {
        return scanner != null && scanner.isScanning();
    }

    /**
     * SearchBLEdevice
     */
    public void startScan() {
        checkAndInstanceScanner();
        if (checkStatus() && scanner != null) {
            scanner.startScan(application);
        }
    }

    /**
     * stopSearch
     */
    public void stopScan() {
        if (checkStatus() && scanner != null) {
            scanner.stopScan(false);
        }
    }

    /**
     * stopSearch，不触发Callback
     */
    public void stopScanQuietly() {
        if (checkStatus() && scanner != null) {
            scanner.stopScan(true);
        }
    }

    /**
     * createconnection
     *
     * @param address bluetooth地址
     * @return Returncreate的connection实例，createfailed则Returnnull
     */
    @Nullable
    public Connection connect(String address) {
        return connect(address, null, null);
    }

    /**
     * createconnection
     *
     * @param address       bluetooth地址
     * @param configuration connectionconfiguration
     * @return Returncreate的connection实例，createfailed则Returnnull
     */
    @Nullable
    public Connection connect(String address, ConnectionConfiguration configuration) {
        return connect(address, configuration, null);
    }

    /**
     * createconnection
     *
     * @param address  bluetooth地址
     * @param observer 伴生Observer
     * @return Returncreate的connection实例，createfailed则Returnnull
     */
    @Nullable
    public Connection connect(String address, EventObserver observer) {
        return connect(address, null, observer);
    }

    /**
     * createconnection
     *
     * @param address       bluetooth地址
     * @param configuration connectionconfiguration
     * @param observer      伴生Observer
     * @return Returncreate的connection实例，createfailed则Returnnull
     */
    @Nullable
    public Connection connect(String address, ConnectionConfiguration configuration,
                              EventObserver observer) {
        if (checkStatus()) {
            Inspector.requireNonNull(address, "address can't be null");
            BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(address);
            if (remoteDevice != null) {
                return connect(new Device(remoteDevice), configuration, observer);
            }
        }
        return null;
    }

    /**
     * createconnection
     *
     * @param device bluetoothdevice实例
     * @return Returncreate的connection实例，createfailed则Returnnull
     */
    @Nullable
    public Connection connect(Device device) {
        return connect(device, null, null);
    }

    /**
     * createconnection
     *
     * @param device        bluetoothdevice实例
     * @param configuration connectionconfiguration
     * @return Returncreate的connection实例，createfailed则Returnnull
     */
    @Nullable
    public Connection connect(Device device, ConnectionConfiguration configuration) {
        return connect(device, configuration, null);
    }

    /**
     * createconnection
     *
     * @param device   bluetoothdevice实例
     * @param observer 伴生Observer
     * @return Returncreate的connection实例，createfailed则Returnnull
     */
    @Nullable
    public Connection connect(Device device, EventObserver observer) {
        return connect(device, null, observer);
    }

    /**
     * createconnection
     *
     * @param device        bluetoothdevice实例
     * @param configuration connectionconfiguration
     * @param observer      伴生Observer
     * @return Returncreate的connection实例，createfailed则Returnnull
     */
    @Nullable
    public synchronized Connection connect(final Device device, ConnectionConfiguration configuration,
                                           final EventObserver observer) {
        if (checkStatus()) {
            Inspector.requireNonNull(device, "device can't be null");
            Connection connection = connectionMap.remove(device.getAddress());
            //如果connection已存在，先release掉
            if (connection != null) {
                connection.releaseNoEvent();
            }
            Boolean isConnectable = device.isConnectable();
            if (isConnectable == null || isConnectable) {
                int connectDelay = 0;
                if (bondController != null && bondController.accept(device)) {
                    BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(device.getAddress());
                    if (BluetoothPermissionUtils.hasBluetoothConnectPermission(getContext())) {
                        try {
                            if (remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                                connectDelay = createBond(device.getAddress()) ? 1500 : 0;
                            }
                        } catch (SecurityException e) {
                            logger.log(Log.WARN, Logger.TYPE_CONNECTION_STATE, 
                                "SecurityException checking bond state: " + e.getMessage());
                        }
                    } else {
                        logger.log(Log.WARN, Logger.TYPE_CONNECTION_STATE, 
                            "Missing BLUETOOTH_CONNECT permission for bonding operations");
                    }
                }
                // Choose connection implementation based on configuration
                if (useNordicBleBackend) {
                    logger.log(Log.INFO, Logger.TYPE_CONNECTION_STATE, 
                        "Creating Nordic BLE-enhanced connection for improved reliability: " + device.getAddress());
                    connection = new NordicConnectionImpl(this, bluetoothAdapter, device, configuration, connectDelay, observer);
                } else {
                    logger.log(Log.DEBUG, Logger.TYPE_CONNECTION_STATE, 
                        "Creating standard EasyBLE connection: " + device.getAddress());
                    connection = new ConnectionImpl(this, bluetoothAdapter, device, configuration, connectDelay, observer);
                }
                connectionMap.put(device.address, connection);
                addressList.add(device.address);
                return connection;
            } else {
                String message = String.format(Locale.US, "connect failed! [type: unconnectable, name: %s, addr: %s]",
                        device.getName(), device.getAddress());
                logger.log(Log.ERROR, Logger.TYPE_CONNECTION_STATE, message);
                if (observer != null) {
                    posterDispatcher.post(observer, MethodInfoGenerator.onConnectFailed(device, Connection.CONNECT_FAIL_TYPE_CONNECTION_IS_UNSUPPORTED));
                }
                observable.notifyObservers(MethodInfoGenerator.onConnectFailed(device, Connection.CONNECT_FAIL_TYPE_CONNECTION_IS_UNSUPPORTED));
            }
        }
        return null;
    }

    /**
     * Get/Retrieve所有connection，无序的
     */
    @NonNull
    public Collection<Connection> getConnections() {
        return connectionMap.values();
    }

    /**
     * Get/Retrieve所有connection，有序的
     */
    @NonNull
    public List<Connection> getOrderedConnections() {
        List<Connection> list = new ArrayList<>();
        for (String address : addressList) {
            Connection connection = connectionMap.get(address);
            if (connection != null) {
                list.add(connection);
            }
        }
        return list;
    }

    /**
     * Get/Retrieve第一个connection
     */
    @Nullable
    public Connection getFirstConnection() {
        return addressList.isEmpty() ? null : connectionMap.get(addressList.get(0));
    }

    /**
     * Get/Retrieve最后一个connection
     */
    @Nullable
    public Connection getLastConnection() {
        return addressList.isEmpty() ? null : connectionMap.get(addressList.get(addressList.size() - 1));
    }

    @Nullable
    public Connection getConnection(Device device) {
        return device == null ? null : connectionMap.get(device.getAddress());
    }

    @Nullable
    public Connection getConnection(String address) {
        return address == null ? null : connectionMap.get(address);
    }

    /**
     * disconnectconnection
     */
    public void disconnectConnection(Device device) {
        if (checkStatus() && device != null) {
            Connection connection = connectionMap.get(device.getAddress());
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * disconnectconnection
     */
    public void disconnectConnection(String address) {
        if (checkStatus() && address != null) {
            Connection connection = connectionMap.get(address);
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * disconnect所有connection
     */
    public void disconnectAllConnections() {
        if (checkStatus()) {
            for (Connection connection : connectionMap.values()) {
                connection.disconnect();
            }
        }
    }

    /**
     * release所有connection
     */
    public void releaseAllConnections() {
        if (checkStatus()) {
            for (Connection connection : connectionMap.values()) {
                connection.release();
            }
            connectionMap.clear();
            addressList.clear();
        }
    }

    /**
     * releaseconnection
     */
    public void releaseConnection(String address) {
        if (checkStatus() && address != null) {
            addressList.remove(address);
            Connection connection = connectionMap.remove(address);
            if (connection != null) {
                connection.release();
            }
        }
    }

    /**
     * releaseconnection
     */
    public void releaseConnection(Device device) {
        if (checkStatus() && device != null) {
            addressList.remove(device.getAddress());
            Connection connection = connectionMap.remove(device.getAddress());
            if (connection != null) {
                connection.release();
            }
        }
    }

    /**
     * 重连所有device
     */
    public void reconnectAll() {
        if (checkStatus()) {
            for (Connection connection : connectionMap.values()) {
                if (connection.getConnectionState() != ConnectionState.SERVICE_DISCOVERED) {
                    connection.reconnect();
                }
            }
        }
    }

    /**
     * 重连device
     */
    public void reconnect(Device device) {
        if (checkStatus() && device != null) {
            Connection connection = connectionMap.get(device.getAddress());
            if (connection != null && connection.getConnectionState() != ConnectionState.SERVICE_DISCOVERED) {
                connection.reconnect();
            }
        }
    }

    /**
     * 根据MAC地址Get/Retrievedevice的配对state
     *
     * @return {@link BluetoothDevice#BOND_NONE}，{@link BluetoothDevice#BOND_BONDED}，{@link BluetoothDevice#BOND_BONDING}
     */
    public int getBondState(String address) {
        checkStatus();
        if (!BluetoothPermissionUtils.hasBluetoothConnectPermission(getContext())) {
            logger.log(Log.WARN, Logger.TYPE_CONNECTION_STATE, 
                "Missing BLUETOOTH_CONNECT permission for getBondState()");
            return BluetoothDevice.BOND_NONE;
        }
        
        try {
            return bluetoothAdapter.getRemoteDevice(address).getBondState();
        } catch (SecurityException e) {
            logger.log(Log.WARN, Logger.TYPE_CONNECTION_STATE, 
                "SecurityException getting bond state: " + e.getMessage());
            return BluetoothDevice.BOND_NONE;
        } catch (Exception e) {
            return BluetoothDevice.BOND_NONE;
        }
    }

    /**
     * start配对
     *
     * @param address device地址
     */
    public boolean createBond(String address) {
        checkStatus();
        if (!BluetoothPermissionUtils.hasBluetoothConnectPermission(getContext())) {
            logger.log(Log.WARN, Logger.TYPE_CONNECTION_STATE, 
                "Missing BLUETOOTH_CONNECT permission for createBond()");
            return false;
        }
        
        try {
            BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(address);
            return remoteDevice.getBondState() != BluetoothDevice.BOND_NONE || remoteDevice.createBond();
        } catch (SecurityException e) {
            logger.log(Log.WARN, Logger.TYPE_CONNECTION_STATE, 
                "SecurityException creating bond: " + e.getMessage());
            return false;
        } catch (Exception ignore) {
            return false;
        }
    }

    /**
     * 根据Filter器，Clear配对
     */
    @SuppressWarnings("all")
    public void clearBondDevices(RemoveBondFilter filter) {
        checkStatus();
        if (bluetoothAdapter != null) {
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : devices) {
                if (filter == null || filter.accept(device)) {
                    try {
                        device.getClass().getMethod("removeBond").invoke(device);
                    } catch (Exception ignore) {
                    }
                }
            }
        }
    }

    /**
     * 解除配对
     *
     * @param address device地址
     */
    @SuppressWarnings("all")
    public void removeBond(String address) {
        checkStatus();
        try {
            BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(address);
            if (remoteDevice.getBondState() != BluetoothDevice.BOND_NONE) {
                remoteDevice.getClass().getMethod("removeBond").invoke(remoteDevice);
            }
        } catch (Exception ignore) {
        }
    }
}
