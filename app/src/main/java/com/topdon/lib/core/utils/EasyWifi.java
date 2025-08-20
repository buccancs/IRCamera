package com.topdon.lib.core.utils;

import android.content.Context;
import android.util.Log;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;

import com.topdon.lib.core.BaseApplication;

public class EasyWifi {
    private static volatile EasyWifi mInstance;
    private WifiConnectCallback wifiConnectCallback;
    String TAG = "EasyWifi";
    private final WifiManager wifiManager = (WifiManager) BaseApplication.instance.getSystemService(Context.WIFI_SERVICE);
    private final ConnectivityManager connectivityManager = (ConnectivityManager) BaseApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE);

    public enum WifiCapability {
        WIFI_CIPHER_WEP,
        WIFI_CIPHER_WPA,
        WIFI_CIPHER_NO_PASS
    }

