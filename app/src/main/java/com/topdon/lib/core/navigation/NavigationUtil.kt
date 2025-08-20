package com.topdon.lib.core.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

/**
 * Navigation utility to replace ARouter functionality
 * Provides simple Intent-based navigation for activities
 */
object NavigationUtil {

    /**
     * Navigate to an activity with optional extras
     */
    fun navigateTo(context: Context, targetClass: Class<*>, extras: Bundle? = null, requestCode: Int? = null) {
        val intent = Intent(context, targetClass)
        extras?.let { intent.putExtras(it) }
        
        if (requestCode != null && context is Activity) {
            context.startActivityForResult(intent, requestCode)
        } else {
            context.startActivity(intent)
        }
    }

    /**
     * Navigate to an activity with extras using key-value pairs
     */
    fun navigateTo(context: Context, targetClass: Class<*>, vararg extras: Pair<String, Any>, requestCode: Int? = null) {
        val intent = Intent(context, targetClass)
        
        for ((key, value) in extras) {
            when (value) {
                is String -> intent.putExtra(key, value)
                is Int -> intent.putExtra(key, value)
                is Long -> intent.putExtra(key, value)
                is Float -> intent.putExtra(key, value)
                is Double -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                is Bundle -> intent.putExtra(key, value)
                // Add more types as needed
                else -> throw IllegalArgumentException("Unsupported extra type: ${value::class.java}")
            }
        }
        
        if (requestCode != null && context is Activity) {
            context.startActivityForResult(intent, requestCode)
        } else {
            context.startActivity(intent)
        }
    }
}