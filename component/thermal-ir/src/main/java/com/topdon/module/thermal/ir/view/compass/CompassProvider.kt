package com.topdon.module.thermal.ir.view.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import com.kylecorry.andromeda.sense.Sensors
import com.kylecorry.andromeda.sense.compass.ICompass
import com.kylecorry.andromeda.sense.compass.LegacyCompass
import com.kylecorry.andromeda.sense.magnetometer.Magnetometer

class CompassProvider(private val context: Context) {

    fun get(): ICompass {
        val useTrueNorth = true

        var source = CompassSource.RotationVector

        // Handle if the available sources have changed (not likely)
        val allSources = getAvailableSources(context)

        // There were no compass sensors found
        if (allSources.isEmpty()){
            return NullCompass()
        }

        if (!allSources.contains(source)) {
            source = allSources.firstOrNull() ?: CompassSource.Orientation
        }

        // Use the most compatible compass implementation available
        val compass = when (source) {
            CompassSource.RotationVector -> {
                // Fallback to LegacyCompass since RotationSensor doesn't implement ICompass
                LegacyCompass(context, useTrueNorth, SensorService.MOTION_SENSOR_DELAY)
            }

            CompassSource.GeomagneticRotationVector -> {
                // Fallback to LegacyCompass since GeomagneticRotationSensor doesn't implement ICompass
                LegacyCompass(context, useTrueNorth, SensorService.MOTION_SENSOR_DELAY)
            }

            CompassSource.CustomMagnetometer -> {
                // Fallback to LegacyCompass since GravityCompensatedCompass might not be available
                LegacyCompass(context, useTrueNorth, SensorService.MOTION_SENSOR_DELAY)
            }

            CompassSource.Orientation -> {
                LegacyCompass(context, useTrueNorth, SensorService.MOTION_SENSOR_DELAY)
            }
        }

        // Return a wrapped compass with quality monitoring
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