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
     * Start discovery of PC Controllers
     */
    fun startDiscovery(): Boolean {
        if (isDiscovering) {
            Log.w(TAG, "Discovery already active")
            return true
        }

        try {
            discoveryListener = object : NsdManager.DiscoveryListener {
                override fun onDiscoveryStarted(regType: String) {
                    isDiscovering = true
                    Log.d(TAG, "Discovery started")
                }

                override fun onServiceFound(service: NsdServiceInfo) {
                    Log.d(TAG, "Service found: ${service.serviceName}")
                    if (service.serviceType == SERVICE_TYPE) {
                        nsdManager.resolveService(service, createResolveListener())
                    }
                }

                override fun onServiceLost(service: NsdServiceInfo) {
                    Log.d(TAG, "Service lost: ${service.serviceName}")
                    discoveredServices.remove(service.serviceName)
                    serviceEventListener?.onControllerLost(service.serviceName)
                }

                override fun onDiscoveryStopped(serviceType: String) {
                    isDiscovering = false
                    Log.d(TAG, "Discovery stopped")
                }

                override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                    isDiscovering = false
                    Log.e(TAG, "Start discovery failed: $errorCode")
                }

                override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                    Log.e(TAG, "Stop discovery failed: $errorCode")
                }
            }

            nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start discovery", e)
            return false
        }
    }

    /**
     * Register this device as a service
     */
    fun registerService(deviceId: String, port: Int): Boolean {
        if (isRegistered) {
            Log.w(TAG, "Service already registered")
            return true
        }

        try {
            val serviceInfo = NsdServiceInfo().apply {
                serviceName = "IRCamera-Android-$deviceId"
                serviceType = SERVICE_TYPE
                setPort(port)
            }

            registrationListener = object : NsdManager.RegistrationListener {
                override fun onServiceRegistered(nsdServiceInfo: NsdServiceInfo) {
                    isRegistered = true
                    Log.d(TAG, "Service registered: ${nsdServiceInfo.serviceName}")
                }

                override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                    isRegistered = false
                    Log.e(TAG, "Registration failed: $errorCode")
                }

                override fun onServiceUnregistered(arg0: NsdServiceInfo) {
                    isRegistered = false
                    Log.d(TAG, "Service unregistered")
                }

                override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                    Log.e(TAG, "Unregistration failed: $errorCode")
                }
            }

            nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register service", e)
            return false
        }
    }

    /**
     * Stop discovery
     */
    fun stopDiscovery() {
        if (!isDiscovering) return

        try {
            discoveryListener?.let { nsdManager.stopServiceDiscovery(it) }
            isDiscovering = false
            Log.d(TAG, "Discovery stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping discovery", e)
        }
    }

    /**
     * Create resolve listener
     */
    private fun createResolveListener(): NsdManager.ResolveListener {
        return object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.e(TAG, "Resolve failed: $errorCode")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                Log.d(TAG, "Service resolved: ${serviceInfo.serviceName}")
                discoveredServices[serviceInfo.serviceName] = serviceInfo
                
                // Notify about discovered controller
                val controllerInfo = try {
                    val host = serviceInfo.host?.hostAddress ?: return
                    val port = serviceInfo.port
                    val deviceName = serviceInfo.serviceName

                    ControllerInfo(
                        ipAddress = host,
                        port = port,
                        controllerId = deviceName,
                        name = deviceName,
                        capabilities = emptyList()
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error creating controller info", e)
                    return
                }
                
                serviceEventListener?.onControllerDiscovered(controllerInfo)
            }
        }
    }

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
    fun getDiscoveredControllers(): List<ControllerInfo> {
        return discoveredServices.values.mapNotNull { serviceInfo ->
            try {
                val host = serviceInfo.host?.hostAddress ?: return@mapNotNull null
                val port = serviceInfo.port
                val deviceName = serviceInfo.serviceName
                val capabilities = emptyList<String>() // Capabilities not available in basic NSD

                ControllerInfo(
                    ipAddress = host,
                    port = port,
                    controllerId = deviceName,
                    name = deviceName,
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
                serviceEventListener?.onControllerLost(service.serviceName)
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
                serviceEventListener?.onDiscoveryError(errorCode, "Failed to start discovery")
            }

            override fun onStopDiscoveryFailed(
                serviceType: String,
                errorCode: Int,
            ) {
                Log.e(TAG, "Discovery failed to stop: $serviceType, error: $errorCode")
                serviceEventListener?.onDiscoveryError(errorCode, "Failed to stop discovery")
            }
        }
    }

    private fun createRegistrationListener(): NsdManager.RegistrationListener {
        return object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {
                Log.i(TAG, "Service registered: ${serviceInfo.serviceName}")
                isRegistered = true
                serviceEventListener?.onServiceRegistered(serviceInfo.serviceName)
            }

            override fun onRegistrationFailed(
                serviceInfo: NsdServiceInfo,
                errorCode: Int,
            ) {
                Log.e(TAG, "Service registration failed: ${serviceInfo.serviceName}, error: $errorCode")
                isRegistered = false
                serviceEventListener?.onDiscoveryError(errorCode, "Registration failed")
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
                serviceEventListener?.onDiscoveryError(errorCode, "Unregistration failed")
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
    fun onControllerDiscovered(controllerInfo: ControllerInfo)
    fun onControllerLost(controllerId: String)
    fun onDiscoveryStarted()
    fun onDiscoveryStopped()
    fun onDiscoveryError(errorCode: Int, message: String)
    fun onServiceRegistered(serviceName: String)
}
