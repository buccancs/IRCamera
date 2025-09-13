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
     * @param tag 请求标识，用于标识每次请求，规则自定。可以用来区分相同type的不同批次请求
     */
    public RequestBuilder<T> setTag(String tag) {
        this.tag = tag;
        return this;
    }

    /**
     * @param priority 请求优先级，值越大，优先级越高，用于请求queue中插队
     */
    public RequestBuilder<T> setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    /**
     * 如果settings了Callback，则Observer不会收到此次请求的message；不settings则使用ObserverReceive请求结果。
     * <br>Callbackmethod使用{@link RunOn}注解指定执行line程，Observermethod使用{@link Observe}注解指定执行line程
     */
    public RequestBuilder<T> setCallback(T callback) {
        this.callback = callback;
        return this;
    }

    public Request build() {
        return new GenericRequest(this);
    }
}
