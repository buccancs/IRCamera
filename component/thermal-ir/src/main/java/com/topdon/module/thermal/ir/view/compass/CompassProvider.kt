package com.topdon.module.thermal.ir.view.compass

import android.content.Context
import android.hardware.Sensor
import com.kylecorry.andromeda.sense.Sensors
import com.kylecorry.andromeda.sense.compass.ICompass
import com.kylecorry.andromeda.sense.compass.LegacyCompass
import com.kylecorry.andromeda.sense.magnetometer.Magnetometer

/**
 * Compass provider utility class for thermal imaging operations.
 * Provides helper functions and common functionality.
 */
class CompassProvider(private val context: Context) {

    fun get(): ICompass {
        val useTrueNorth = true

        var source = CompassSource.RotationVector
        
        // Debug logging to identify which version is running
        android.util.Log.d("CompassProvider", "Using fixed CompassProvider implementation v2.0")

        // Handle if the available sources have changed (not likely)
        val allSources = getAvailableSources(context)

        // There were no compass sensors found
        if (allSources.isEmpty())
            {
                return NullCompass()
            }

        if (!allSources.contains(source)) {
            source = allSources.firstOrNull() ?: CompassSource.Orientation
        }

        // Use the most compatible compass implementation available
        // Always using LegacyCompass to avoid ClassCastException
        val compass = when (source) {
            CompassSource.RotationVector -> {
                android.util.Log.d("CompassProvider", "Creating LegacyCompass for RotationVector")
                LegacyCompass(context, useTrueNorth, SensorService.MOTION_SENSOR_DELAY)
            }

            CompassSource.GeomagneticRotationVector -> {
                android.util.Log.d("CompassProvider", "Creating LegacyCompass for GeomagneticRotationVector")
                LegacyCompass(context, useTrueNorth, SensorService.MOTION_SENSOR_DELAY)
            }

            CompassSource.CustomMagnetometer -> {
                android.util.Log.d("CompassProvider", "Creating LegacyCompass for CustomMagnetometer")
                LegacyCompass(context, useTrueNorth, SensorService.MOTION_SENSOR_DELAY)
            }

            CompassSource.Orientation -> {
                android.util.Log.d("CompassProvider", "Creating LegacyCompass for Orientation")
                LegacyCompass(context, useTrueNorth, SensorService.MOTION_SENSOR_DELAY)
            }

        // Return a wrapped compass with quality monitoring
        android.util.Log.d("CompassProvider", "Wrapping compass with MagQualityCompassWrapper: ${compass.javaClass.simpleName}")
        return MagQualityCompassWrapper(
            compass,
            Magnetometer(context, SensorManager.SENSOR_DELAY_NORMAL)
        )
    }

    companion object {
        /**
         * Returns the available compass sources in order of quality
         */
        fun getAvailableSources(context: Context): List<CompassSource> {
            val sources = mutableListOf<CompassSource>()

            if (Sensors.hasSensor(context, Sensor.TYPE_ROTATION_VECTOR)) {
                sources.add(CompassSource.RotationVector)
            }

            if (Sensors.hasSensor(context, Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)) {
                sources.add(CompassSource.GeomagneticRotationVector)
            }

            if (Sensors.hasSensor(context, Sensor.TYPE_MAGNETIC_FIELD)) {
                sources.add(CompassSource.CustomMagnetometer)
            }

            @Suppress("DEPRECATION")
            if (Sensors.hasSensor(context, Sensor.TYPE_ORIENTATION)) {
                sources.add(CompassSource.Orientation)
            }

            return sources
        }
    }
}
