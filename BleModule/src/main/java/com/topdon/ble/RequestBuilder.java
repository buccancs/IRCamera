package com.topdon.ble;

import com.topdon.ble.callback.RequestCallback;

import java.util.UUID;

import com.topdon.commons.observer.Observe;
import com.topdon.commons.poster.RunOn;

/**
 * date: 2019/9/20 18:00
* author: bichuanfeng
 */
public class RequestBuilder<T extends RequestCallback> {
 String tag;
 RequestType type;
 UUID service;
 UUID characteristic;
 UUID descriptor;
 Object value;
 int priority;
 RequestCallback callback;
 WriteOptions writeOptions;

 RequestBuilder(RequestType type) {
 this.type = type;
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 public RequestBuilder<T> setTag(String tag) {
 this.tag = tag;
 return this;
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 public RequestBuilder<T> setPriority(int priority) {
 this.priority = priority;
 return this;
 }

 /**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 public RequestBuilder<T> setCallback(T callback) {
 this.callback = callback;
 return this;
 }

 public Request build() {
 return new GenericRequest(this);
 }
}
