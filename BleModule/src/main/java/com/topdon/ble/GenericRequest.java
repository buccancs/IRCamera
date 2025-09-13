package com.topdon.ble;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.topdon.ble.callback.RequestCallback;

import java.util.Queue;
import java.util.UUID;

/**
 * date: 2021/8/12 13:44
* author: bichuanfeng
 */
class GenericRequest implements Request, Comparable<GenericRequest> {
 Device device;
 private final String tag;
 RequestType type;
 UUID service;
 UUID characteristic;
 UUID descriptor;
 Object value;
 int priority;
 RequestCallback callback;
 WriteOptions writeOptions;
 byte[] descriptorTemp;//SaveValue
 // Comment removed (contained Chinese characters)
 Queue<byte[]> remainQueue;
 byte[] sendingBytes;
 //--------------------------------

 GenericRequest(RequestBuilder builder) {
 tag = builder.tag;
 type = builder.type;
 service = builder.service;
 characteristic = builder.characteristic;
 descriptor = builder.descriptor;
 priority = builder.priority;
 value = builder.value;
 callback = builder.callback;
 writeOptions = builder.writeOptions;
 }

 @Override
 public int compareTo(GenericRequest other) {
 return Integer.compare(other.priority, priority);
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 @NonNull
 public Device getDevice() {
 return device;
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 @NonNull
 public RequestType getType() {
 return type;
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 @Nullable
 public String getTag() {
 return tag;
 }

 @Nullable
 public UUID getService() {
 return service;
 }

 @Nullable
 public UUID getCharacteristic() {
 return characteristic;
 }

 @Nullable
 public UUID getDescriptor() {
 return descriptor;
 }

 @Override
 public void execute(Connection connection) {
 if (connection != null) {
 connection.execute(this);
 }
 }
}
