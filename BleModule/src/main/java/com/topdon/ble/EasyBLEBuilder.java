package com.topdon.ble;

import com.topdon.ble.util.Logger;
import com.topdon.commons.observer.Observable;
import com.topdon.commons.poster.ThreadMode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * date: 2021/8/12 12:02
 * author: bichuanfeng
 */
public class EasyBLEBuilder {
    private final static ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    BondController bondController;
    DeviceCreator deviceCreator;
    ThreadMode methodDefaultThreadMode = ThreadMode.MAIN;
    ExecutorService executorService = DEFAULT_EXECUTOR_SERVICE;
    ScanConfiguration scanConfiguration;
    Observable observable;
    Logger logger;
    boolean isObserveAnnotationRequired = false;
    ScannerType scannerType;
    boolean useNordicBleBackend = true; // Enable Nordic BLE by default for enhanced reliability

    EasyBLEBuilder() {
    }

    /**
     * 指定bluetooth扫描器，默认为系统Android5.0以上使用{@link ScannerType#LE}，否则使用{@link ScannerType#LEGACY}。
     * 系统小于Android5.0时，指定{@link ScannerType#LE}无效
     */
    public EasyBLEBuilder setScannerType(ScannerType scannerType) {
        Inspector.requireNonNull(scannerType, "scannerType can't be null");
        this.scannerType = scannerType;
        return this;
    }

    /**
     * 自定义line程池用来执行后台task
     */
    public EasyBLEBuilder setExecutorService(ExecutorService executorService) {
        Inspector.requireNonNull(executorService, "executorService can't be null");
        this.executorService = executorService;
        return this;
    }

    /**
     * device实例Build器
     */
    public EasyBLEBuilder setDeviceCreator(DeviceCreator deviceCreator) {
        Inspector.requireNonNull(deviceCreator, "deviceCreator can't be null");
        this.deviceCreator = deviceCreator;
        return this;
    }

    /**
     * 配对控制器。如果settings了控制器，则会在connection时，尝试配对
     */
    public EasyBLEBuilder setBondController(BondController bondController) {
        Inspector.requireNonNull(bondController, "bondController can't be null");
        this.bondController = bondController;
        return this;
    }

    /**
     * Observer或者Callback的method在没有使用注解指定调用line程时，默认被调用的line程
     */
    public EasyBLEBuilder setMethodDefaultThreadMode(ThreadMode mode) {
        Inspector.requireNonNull(mode, "mode can't be null");
        methodDefaultThreadMode = mode;
        return this;
    }

    /**
     * Searchconfiguration
     */
    public EasyBLEBuilder setScanConfiguration(ScanConfiguration scanConfiguration) {
        Inspector.requireNonNull(scanConfiguration, "scanConfiguration can't be null");
        this.scanConfiguration = scanConfiguration;
        return this;
    }

    /**
     * Log打印
     */
    public EasyBLEBuilder setLogger(Logger logger) {
        Inspector.requireNonNull(logger, "logger can't be null");
        this.logger = logger;
        return this;
    }

    /**
     * 被Observer，messageRelease者。
     * <br>如果Observer被settings，{@link #setMethodDefaultThreadMode(ThreadMode)}、
     * {@link #setObserveAnnotationRequired(boolean)}、{@link #setExecutorService(ExecutorService)}将不起作用
     */
    public EasyBLEBuilder setObservable(Observable observable) {
        Inspector.requireNonNull(observable, "observable can't be null");
        this.observable = observable;
        return this;
    }

    /**
     * 是否强制使用{@link Observe}注解才会收到被Observer的message
     * 
     * @param observeAnnotationRequired true：只有method上加{@link Observe}注解的才会收到message。false：加不加注解都会收到message
     */
    public EasyBLEBuilder setObserveAnnotationRequired(boolean observeAnnotationRequired) {
        isObserveAnnotationRequired = observeAnnotationRequired;
        return this;
    }

    /**
     * Enable or disable Nordic BLE backend for enhanced reliability.
     * When enabled, uses Nordic BLE library for more robust BLE communication.
     * When disabled, uses the original EasyBLE implementation.
     * 
     * @param useNordicBackend true to use Nordic BLE backend (recommended), false for original
     */
    public EasyBLEBuilder setUseNordicBleBackend(boolean useNordicBackend) {
        this.useNordicBleBackend = useNordicBackend;
        return this;
    }

    /**
     * 根据当前configurationBuildEasyBLE实例
     */
    public EasyBLE build() {
        synchronized (EasyBLE.class) {
            if (EasyBLE.instance != null) {
                throw new EasyBLEException("EasyBLE instance already exists. It can only be instantiated once.");
            }
            EasyBLE.instance = new EasyBLE(this);
            return EasyBLE.instance;
        }
    }
}
