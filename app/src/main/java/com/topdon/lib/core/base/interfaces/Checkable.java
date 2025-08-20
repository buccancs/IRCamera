package com.topdon.lib.core.base.interfaces;

public interface Checkable<T> {
    boolean isChecked();
    
    T setChecked(boolean isChecked);
}
