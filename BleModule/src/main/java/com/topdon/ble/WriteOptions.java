package com.topdon.ble;

import android.bluetooth.BluetoothGattCharacteristic;

 * date: 2019/8/9 18:06
 * author: bichuanfeng
public class WriteOptions {
    final int packageWriteDelayMillis;
    final int requestWriteDelayMillis;
    int packageSize;
    final boolean isWaitWriteResult;
    final int writeType;
    final boolean useMtuAsPackageSize;

    /**
     * Private method description.
     */
    private WriteOptions(Builder builder) {
        packageWriteDelayMillis = builder.packageWriteDelayMillis;
        requestWriteDelayMillis = builder.requestWriteDelayMillis;
        packageSize = builder.packageSize;
        isWaitWriteResult = builder.isWaitWriteResult;
        writeType = builder.writeType;
        useMtuAsPackageSize = builder.useMtuAsPackageSize;
    }

    /**
     * Method description.
     */
    public int getPackageWriteDelayMillis() {
        return packageWriteDelayMillis;
    }

     * {@link #getPackageWriteDelayMillis()}
    /**
     * Method description.
     */
    public int getRequestWriteDelayMillis() {
        return requestWriteDelayMillis;
    }

    /**
     * Method description.
     */
    public int getPackageSize() {
        return packageSize;
    }

    /**
     * Method description.
     */
    public boolean isWaitWriteResult() {
        return isWaitWriteResult;
    }

    /**
     * Method description.
     */
    public int getWriteType() {
        return writeType;
    }

    /**
     * Method description.
     */
    public static class Builder {
        private int packageWriteDelayMillis = 0;
        private int requestWriteDelayMillis = -1;
        private int packageSize = 20;
        private boolean isWaitWriteResult = true;
        private int writeType = -1;
        private boolean useMtuAsPackageSize = false;

        public Builder setPackageWriteDelayMillis(int packageWriteDelayMillis) {
            this.packageWriteDelayMillis = packageWriteDelayMillis;
            return this;
        }

         * {@link #packageWriteDelayMillis}
        public Builder setRequestWriteDelayMillis(int requestWriteDelayMillis) {
            this.requestWriteDelayMillis = requestWriteDelayMillis;
            return this;
        }

        public Builder setPackageSize(int packageSize) {
            if (packageSize > 0) {
                this.packageSize = packageSize;
            }
            return this;
        }

        public Builder setWaitWriteResult(boolean waitWriteResult) {
            isWaitWriteResult = waitWriteResult;
            return this;
        }

         * @param writeType {@link BluetoothGattCharacteristic#WRITE_TYPE_DEFAULT}
         *                  <br>{@link BluetoothGattCharacteristic#WRITE_TYPE_NO_RESPONSE}
         *                  <br>{@link BluetoothGattCharacteristic#WRITE_TYPE_SIGNED}
        public Builder setWriteType(int writeType) {
            if (writeType == BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT ||
                    writeType == BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE ||
                    writeType == BluetoothGattCharacteristic.WRITE_TYPE_SIGNED) {
                this.writeType = writeType;
            }
            return this;
        }

         * MTU{@link #setPackageSize(int)} = mtu - 3
        public Builder setMtuAsPackageSize() {
            useMtuAsPackageSize = true;
            return this;
        }
        
        public WriteOptions build() {
            return new WriteOptions(this);
        }
    }
}
