package com.topdon.gsr.network

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Zeroconf/mDNS service for PC Controller discovery
 */
class ZeroconfDiscoveryService(private val context: Context) {
    companion object {
        private const val TAG = "ZeroconfDiscoveryService"
        private const val SERVICE_TYPE = "_ircamera._tcp."
        private const val SERVICE_NAME = "IRCamera-Hub"
    }

    // Service management
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val discoveredServices = ConcurrentHashMap<String, NsdServiceInfo>()
    private var isRegistered = false
    private var isDiscovering = false
    
    // Listeners
    private var registrationListener: NsdManager.RegistrationListener? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null
    private var serviceEventListener: ServiceEventListener? = null

    /**
     * Unregister the service
     */
    fun unregisterService() {
        if (!isRegistered) return

        try {
            registrationListener?.let { nsdManager.unregisterService(it) }
            isRegistered = false
            Log.i(TAG, "Unregistered service")
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering service", e)
        }
    }

    /**
     * Get list of discovered PC Controllers
     */
    fun getDiscoveredControllers(): List<NetworkClient.ControllerInfo> {
        return discoveredServices.values.mapNotNull { serviceInfo ->
            try {
                val host = serviceInfo.host?.hostAddress ?: return@mapNotNull null
                val port = serviceInfo.port
                val deviceName = serviceInfo.serviceName
                val capabilities = emptyList<String>() // Capabilities not available in basic NSD

                NetworkClient.ControllerInfo(
                    ipAddress = host,
                    port = port,
                    deviceName = deviceName,
                    capabilities = capabilities,
                )
            } catch (e: Exception) {
                Log.w(TAG, "Failed to parse service info: ${serviceInfo.serviceName}", e)
                null
            }
        }
    }

    private fun createDiscoveryListener(): NsdManager.DiscoveryListener {
        return object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) {
                Log.d(TAG, "Service discovery started: $regType")
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                Log.d(TAG, "Service discovery success: ${service.serviceName}")

                // Don't discover our own service
                if (service.serviceName.startsWith(SERVICE_NAME)) {
                    return
                }

                // Resolve the service to get detailed information
                nsdManager.resolveService(service, createResolveListener())
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                Log.i(TAG, "Service lost: ${service.serviceName}")
                discoveredServices.remove(service.serviceName)
                serviceListener?.onServiceLost(service.serviceName)
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Log.i(TAG, "Discovery stopped: $serviceType")
                isDiscovering = false
            }

            override fun onStartDiscoveryFailed(
                serviceType: String,
                errorCode: Int,
            ) {
                Log.e(TAG, "Discovery failed to start: $serviceType, error: $errorCode")
                isDiscovering = false
                serviceListener?.onDiscoveryError(errorCode, "Failed to start discovery")
            }

            override fun onStopDiscoveryFailed(
                serviceType: String,
                errorCode: Int,
            ) {
                Log.e(TAG, "Discovery failed to stop: $serviceType, error: $errorCode")
                serviceListener?.onDiscoveryError(errorCode, "Failed to stop discovery")
            }
        }
    }

    private fun createResolveListener(): NsdManager.ResolveListener {
        return object : NsdManager.ResolveListener {
            override fun onResolveFailed(
                serviceInfo: NsdServiceInfo,
                errorCode: Int,
            ) {
                Log.e(TAG, "Resolve failed: ${serviceInfo.serviceName}, error: $errorCode")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                Log.i(TAG, "Service resolved: ${serviceInfo.serviceName} at ${serviceInfo.host}:${serviceInfo.port}")

                discoveredServices[serviceInfo.serviceName] = serviceInfo

                // Notify listener
                try {
                    val host = serviceInfo.host?.hostAddress ?: return
                    val port = serviceInfo.port
                    val deviceName = serviceInfo.serviceName
                    val capabilities = emptyList<String>() // Capabilities not available in basic NSD

                    val controllerInfo =
                        NetworkClient.ControllerInfo(
                            ipAddress = host,
                            port = port,
                            deviceName = deviceName,
                            capabilities = capabilities,
                        )

                    serviceListener?.onServiceDiscovered(controllerInfo)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to parse resolved service", e)
                }
            }
        }
    }

    private fun createRegistrationListener(): NsdManager.RegistrationListener {
        return object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {
                Log.i(TAG, "Service registered: ${serviceInfo.serviceName}")
                isRegistered = true
                serviceListener?.onServiceRegistered(serviceInfo.serviceName)
            }

            override fun onRegistrationFailed(
                serviceInfo: NsdServiceInfo,
                errorCode: Int,
            ) {
                Log.e(TAG, "Service registration failed: ${serviceInfo.serviceName}, error: $errorCode")
                isRegistered = false
                serviceListener?.onDiscoveryError(errorCode, "Registration failed")
            }

            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) {
                Log.i(TAG, "Service unregistered: ${serviceInfo.serviceName}")
                isRegistered = false
            }

            override fun onUnregistrationFailed(
                serviceInfo: NsdServiceInfo,
                errorCode: Int,
            ) {
                Log.e(TAG, "Service unregistration failed: ${serviceInfo.serviceName}, error: $errorCode")
                serviceListener?.onDiscoveryError(errorCode, "Unregistration failed")
            }
        }
    }

    /**
     * Clean up resources
     */
    fun cleanup() {
        stopDiscovery()
        unregisterService()
        discoveredServices.clear()
        serviceEventListener = null
        discoveryListener = null
        registrationListener = null
    }

    /**
     * Set service event listener
     */
    fun setServiceEventListener(listener: ServiceEventListener) {
        this.serviceEventListener = listener
    }
}

/**
 * Interface for service discovery events
 */
interface ServiceEventListener {
    fun onServiceFound(serviceInfo: NsdServiceInfo)
    fun onServiceLost(serviceInfo: NsdServiceInfo)
    fun onDiscoveryStarted()
    fun onDiscoveryStopped()
}
