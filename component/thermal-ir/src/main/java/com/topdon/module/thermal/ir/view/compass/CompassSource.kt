package com.topdon.module.thermal.ir.view.compass

/**
 * Compass source enumeration for thermal imaging compass functionality.
 */
enum class CompassSource(val id: String) {
    RotationVector("rotation_vector"),
    GeomagneticRotationVector("geomagnetic_rotation_vector"),
    CustomMagnetometer("custom_magnetometer"),
    Orientation("orientation")
}