package com.topdon.lib.core;

import java.util.UUID;

/**
 * Simple UUID manager for BLE operations
 */
public class UUIDManager {
    
    // Common BLE UUIDs
    public static final UUID GENERIC_ACCESS_SERVICE = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
    public static final UUID GENERIC_ATTRIBUTE_SERVICE = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
    public static final UUID DEVICE_NAME_CHARACTERISTIC = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb");
    
    private UUIDManager() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Convert 16-bit UUID to full 128-bit UUID
     */
    public static UUID toFullUUID(int uuid16) {
        return UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", uuid16));
    }
}