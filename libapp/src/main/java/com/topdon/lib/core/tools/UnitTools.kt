package com.topdon.lib.core.tools

import com.topdon.lib.core.common.SharedManager
import java.util.*

object UnitTools {
 /**
 * Comment removed (contained Chinese characters)
 *
 * @param float Temperature
 */
 @JvmStatic
 fun showC(float: Float): String {
 val str =
 if (SharedManager.getTemperature() == 1) {
 // Temperature
 "${String.format(Locale.ENGLISH, "%.1f", float)}°C"
 } else {
 // Comment removed (contained Chinese characters)
 "${String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))}°F"
 }
 return str
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * @param float Temperature
 */
 @JvmStatic
 fun showC(
 float: Float,
 isC: Boolean,
 ): String {
 val str =
 if (isC) {
 // Temperature
 "${String.format(Locale.ENGLISH, "%.1f", float)}°C"
 } else {
 // Comment removed (contained Chinese characters)
 "${String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))}°F"
 }
 return str
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 @JvmStatic
 fun showIntervalC(
 min: Int,
 max: Int,
 ): String {
 val str =
 if (SharedManager.getTemperature() == 1) {
 // Temperature
 "$min~$max°C"
 } else {
 // Comment removed (contained Chinese characters)
 val maxT: Int = (max * 1.8000 + 32.00).toInt()
 val minT: Int = (min * 1.8000 + 32.00).toInt()
 "$minT~$maxT°F"
 }
 return str
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 @JvmStatic
 fun showConfigC(
 min: Int,
 max: Int,
 ): String {
 val str =
 if (SharedManager.getTemperature() == 1) {
 // Temperature
 "($min~$max°C)"
 } else {
 // Comment removed (contained Chinese characters)
 val maxT: Int = (max * 1.8000 + 32.00).toInt()
 val minT: Int = (min * 1.8000 + 32.00).toInt()
 "($minT~$maxT°F)"
 }
 return str
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * @param float Temperature
 */
 @JvmStatic
 fun showUnit(): String {
 val str =
 if (SharedManager.getTemperature() == 1) {
 // Temperature
 "°C"
 } else {
 // Comment removed (contained Chinese characters)
 "°F"
 }
 return str
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * @param float Temperature
 */
 @JvmStatic
 fun showUnitValue(value: Float): Float {
 val str =
 if (SharedManager.getTemperature() == 1) {
 // Temperature
 value
 } else {
 // Comment removed (contained Chinese characters)
 toF(value)
 }
 return str.toFloat()
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * @param float Temperature
 */
 @JvmStatic
 fun showUnitValue(
 value: Float,
 showC: Boolean,
 ): Float {
 if (value == Float.MAX_VALUE || value == Float.MIN_VALUE) {
 return value
 }
 val str =
 if (showC) {
 // Temperature
 value
 } else {
 // Comment removed (contained Chinese characters)
 toF(value)
 }
 return str.toFloat()
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * @param float Temperature
 */
 @JvmStatic
 fun showToCValue(
 value: Float,
 isShowC: Boolean,
 ): Float {
 val str =
 if (isShowC) {
 // Temperature
 value
 } else {
 // Comment removed (contained Chinese characters)
 toC(value)
 }
 return str.toFloat()
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * @param float Temperature
 */
 @JvmStatic
 fun showToCValue(value: Float): Float {
 val str =
 if (SharedManager.getTemperature() == 1) {
 // Temperature
 value
 } else {
 // Comment removed (contained Chinese characters)
 toC(value)
 }
 return str.toFloat()
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 fun toF(value: Float): Float {
 return value * 1.8000f + 32.00f
 }

 /**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 fun toC(value: Float): Float {
 return (value - 32.0f) / 1.8000f
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 */
 @JvmStatic
 fun showNoUnit(float: Float): String {
 val str =
 if (SharedManager.getTemperature() == 1) { // 
 String.format(Locale.ENGLISH, "%.1f", float)
 } else {
 String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))
 }
 return if (str.endsWith(".0")) str.substring(0, str.length - 2) else str
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 */
 @JvmStatic
 fun showWithUnit(float: Float): String {
 val str =
 if (SharedManager.getTemperature() == 1) { // 
 String.format(Locale.ENGLISH, "%.1f", float)
 } else {
 String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))
 }
 return (if (str.endsWith(".0")) str.substring(0, str.length - 2) else str) + showUnit()
 }
}
