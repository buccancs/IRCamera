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
     * @param tag [Chinese text], for[Chinese text], [Chinese text]. [Chinese text]
     */
    public RequestBuilder<T> setTag(String tag) {
        this.tag = tag;
        return this;
    }

        /**
     * @param priority [Chinese text], [Chinese text], [Chinese text]high, for[Chinese text]in progress[Chinese text]
     */
    public RequestBuilder<T> setPriority(int priority) {
        this.priority = priority;
        return this;
    }

        /**
     * [Chinese text]Settings[Chinese text], [Chinese text]observation[Chinese text]; [Chinese text]Settings[Chinese text]observation[Chinese text]. 
     * <br>[Chinese text]{@link RunOn}[Chinese text]line[Chinese text], observation[Chinese text]{@link Observe}[Chinese text]line[Chinese text]
     */
    public RequestBuilder<T> setCallback(T callback) {
        this.callback = callback;
        return this;
    }

    public Request build() {
        return new GenericRequest(this);
    }
}
