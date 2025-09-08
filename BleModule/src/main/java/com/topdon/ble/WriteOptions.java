package com.topdon.ble;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * [Chinese text]
 *
 * date: 2019/8/9 18:06
 * author: bichuanfeng
 */
public class WriteOptions {
    final int packageWriteDelayMillis;
    final int requestWriteDelayMillis;
    int packageSize;
    final boolean isWaitWriteResult;
    final int writeType;
    final boolean useMtuAsPackageSize;

    private WriteOptions(Builder builder) {
        packageWriteDelayMillis = builder.packageWriteDelayMillis;
        requestWriteDelayMillis = builder.requestWriteDelayMillis;
        packageSize = builder.packageSize;
        isWaitWriteResult = builder.isWaitWriteResult;
        writeType = builder.writeType;
        useMtuAsPackageSize = builder.useMtuAsPackageSize;
    }

        /**
     * [Chinese text]
     */
    public int getPackageWriteDelayMillis() {
        return packageWriteDelayMillis;
    }

        /**
     * [Chinese text], [Chinese text]{@link #getPackageWriteDelayMillis()}[Chinese text], [Chinese text]. 
     * [Chinese text], [Chinese text]
     */
    public int getRequestWriteDelayMillis() {
        return requestWriteDelayMillis;
    }

        /**
     * [Chinese text]
     */
    public int getPackageSize() {
        return packageSize;
    }

        /**
     * [Chinese text]
     */
    public boolean isWaitWriteResult() {
        return isWaitWriteResult;
    }

        /**
     * [Chinese text]mode
     */
    public int getWriteType() {
        return writeType;
    }

    public static/**
 * Builder class.
 * 
 * Provides builder functionality.
 */
 class Builder {
        private int packageWriteDelayMillis = 0;
        private int requestWriteDelayMillis = -1;
        private int packageSize = 20;
        private boolean isWaitWriteResult = true;
        private int writeType = -1;
        private boolean useMtuAsPackageSize = false;

            /**
     * [Chinese text]
     */
    public Builder setPackageWriteDelayMillis(int packageWriteDelayMillis) {
            this.packageWriteDelayMillis = packageWriteDelayMillis;
            return this;
        }

            /**
     * [Chinese text], [Chinese text]{@link #packageWriteDelayMillis}[Chinese text], [Chinese text]. 
     * [Chinese text], [Chinese text]
     */
    public Builder setRequestWriteDelayMillis(int requestWriteDelayMillis) {
            this.requestWriteDelayMillis = requestWriteDelayMillis;
            return this;
        }

            /**
     * [Chinese text]
     */
    public Builder setPackageSize(int packageSize) {
            if (packageSize > 0) {
                this.packageSize = packageSize;
            }
            return this;
        }

            /**
     * [Chinese text]
     */
    public Builder setWaitWriteResult(boolean waitWriteResult) {
            isWaitWriteResult = waitWriteResult;
            return this;
        }

            /**
     * Settings[Chinese text]mode
     *
     * @param writeType {@link BluetoothGattCharacteristic#WRITE_TYPE_DEFAULT}
     *                  <br>{@link BluetoothGattCharacteristic#WRITE_TYPE_NO_RESPONSE}
     *                  <br>{@link BluetoothGattCharacteristic#WRITE_TYPE_SIGNED}
     */
    public Builder setWriteType(int writeType) {
            if (writeType == BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT ||
                    writeType == BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE ||
                    writeType == BluetoothGattCharacteristic.WRITE_TYPE_SIGNED) {
                this.writeType = writeType;
            }            
            return this;
        }

            /**
     * Settings[Chinese text]MTU[Chinese text], [Chinese text]{@link #setPackageSize(int)}Settings[Chinese text]. [Chinese text] = mtu - 3
     */
    public Builder setMtuAsPackageSize() {
            useMtuAsPackageSize = true;
            return this;
        }
        
        public WriteOptions build() {
            return new WriteOptions(this);
        }
    }
}
