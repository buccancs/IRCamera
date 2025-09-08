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

    EasyBLEBuilder() {
    }

        /**
     * [Chinese text], [Chinese text]Android5.0[Chinese text]{@link ScannerType#LE}, [Chinese text]{@link ScannerType#LEGACY}. 
     * [Chinese text]Android5.0[Chinese text], [Chinese text]{@link ScannerType#LE}[Chinese text]
     */
    public EasyBLEBuilder setScannerType(ScannerType scannerType) {
        Inspector.requireNonNull(scannerType, "scannerType can't be null");
        this.scannerType = scannerType;
        return this;
    }

        /**
     * [Chinese text]line[Chinese text]
     */
    public EasyBLEBuilder setExecutorService(ExecutorService executorService) {
        Inspector.requireNonNull(executorService, "executorService can't be null");
        this.executorService = executorService;
        return this;
    }

        /**
     * [Chinese text]
     */
    public EasyBLEBuilder setDeviceCreator(DeviceCreator deviceCreator) {
        Inspector.requireNonNull(deviceCreator, "deviceCreator can't be null");
        this.deviceCreator = deviceCreator;
        return this;
    }

        /**
     * [Chinese text]. [Chinese text]Settings[Chinese text], [Chinese text], [Chinese text]
     */
    public EasyBLEBuilder setBondController(BondController bondController) {
        Inspector.requireNonNull(bondController, "bondController can't be null");
        this.bondController = bondController;
        return this;
    }

        /**
     * observation[Chinese text]line[Chinese text], [Chinese text]line[Chinese text]
     */
    public EasyBLEBuilder setMethodDefaultThreadMode(ThreadMode mode) {
        Inspector.requireNonNull(mode, "mode can't be null");
        methodDefaultThreadMode = mode;
        return this;
    }

        /**
     * [Chinese text]
     */
    public EasyBLEBuilder setScanConfiguration(ScanConfiguration scanConfiguration) {
        Inspector.requireNonNull(scanConfiguration, "scanConfiguration can't be null");
        this.scanConfiguration = scanConfiguration;
        return this;
    }

        /**
     * [Chinese text]
     */
    public EasyBLEBuilder setLogger(Logger logger) {
        Inspector.requireNonNull(logger, "logger can't be null");
        this.logger = logger;
        return this;
    }

        /**
     * [Chinese text]observation[Chinese text], [Chinese text]. 
     * <br>[Chinese text]observation[Chinese text]Settings, {@link #setMethodDefaultThreadMode(ThreadMode)}, 
     * {@link #setObserveAnnotationRequired(boolean)}, {@link #setExecutorService(ExecutorService)}[Chinese text]
     */
    public EasyBLEBuilder setObservable(Observable observable) {
        Inspector.requireNonNull(observable, "observable can't be null");
        this.observable = observable;
        return this;
    }

        /**
     * [Chinese text]{@link Observe}[Chinese text]observation[Chinese text]
     *
     * @param observeAnnotationRequired true: [Chinese text]{@link Observe}[Chinese text]. false: [Chinese text]
     */
    public EasyBLEBuilder setObserveAnnotationRequired(boolean observeAnnotationRequired) {
        isObserveAnnotationRequired = observeAnnotationRequired;
        return this;
    }

        /**
     * [Chinese text]EasyBLE[Chinese text]
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
