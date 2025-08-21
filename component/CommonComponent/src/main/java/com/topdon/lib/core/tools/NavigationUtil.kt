package com.topdon.lib.core.tools

import android.content.Context
import android.content.Intent

/**
 * Navigation utility to replace ARouter functionality
 * Provides simple activity navigation for component modules
 */
object NavigationUtil {
    
    fun navigateToActivity(context: Context, targetClass: Class<*>) {
        val intent = Intent(context, targetClass)
        context.startActivity(intent)
    }
    
    fun navigateToActivityWithFlags(context: Context, targetClass: Class<*>, flags: Int) {
        val intent = Intent(context, targetClass)
        intent.flags = flags
        context.startActivity(intent)
    }
    
    fun navigateToActivityForResult(context: Context, targetClass: Class<*>, requestCode: Int) {
        if (context is androidx.appcompat.app.AppCompatActivity) {
            val intent = Intent(context, targetClass)
            context.startActivityForResult(intent, requestCode)
        }
    }
    
    fun navigateToActivityWithExtras(context: Context, targetClass: Class<*>, extras: Map<String, Any>) {
        val intent = Intent(context, targetClass)
        extras.forEach { (key, value) ->
            when (value) {
                is String -> intent.putExtra(key, value)
                is Int -> intent.putExtra(key, value)
                is Long -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                is Float -> intent.putExtra(key, value)
                is Double -> intent.putExtra(key, value)
                // Add more types as needed
            }
        }
        context.startActivity(intent)
    }
    
    fun finishActivity(context: Context) {
        if (context is androidx.appcompat.app.AppCompatActivity) {
            context.finish()
        }
    }
}