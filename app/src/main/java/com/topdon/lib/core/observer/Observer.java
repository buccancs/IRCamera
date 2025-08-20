package com.topdon.lib.core.observer;

public interface Observer {
    @Observe
    default void onChanged(Object o) {}
}
