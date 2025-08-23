package com.tbruyelle.rxpermissions2

import androidx.fragment.app.Fragment

/**
 * Stub RxPermissions2 for permission handling
 */
class RxPermissions(private val fragment: Fragment) {
    
    fun request(vararg permissions: String): io.reactivex.Observable<Boolean> {
        // Stub implementation - always return true for permissions
        return io.reactivex.Observable.just(true)
    }
    
    fun requestEach(vararg permissions: String): io.reactivex.Observable<Permission> {
        return io.reactivex.Observable.fromArray(*permissions.map { Permission(it, true, false) }.toTypedArray())
    }
    
    class Permission(val name: String, val granted: Boolean, val shouldShowRequestPermissionRationale: Boolean)
}