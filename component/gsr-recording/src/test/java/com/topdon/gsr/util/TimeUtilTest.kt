package com.topdon.gsr.util

import org.junit.Test
import org.junit.Assert.*
import java.util.Locale

/**
 * Unit tests for TimeUtil
 */
class TimeUtilTest {

    @Test
    fun testPcTimeOffset() {
        val initialOffset = TimeUtil.getPcTimeOffset()
        assertEquals(0L, initialOffset)
        
        val testOffset = 5000L
        TimeUtil.setPcTimeOffset(testOffset)
        assertEquals(testOffset, TimeUtil.getPcTimeOffset())
        
        // Reset for other tests
        TimeUtil.setPcTimeOffset(0L)
    }

    @Test
    fun testUtcTimestamp() {
        val offset = 1000L
        TimeUtil.setPcTimeOffset(offset)
        
        val systemTime = System.currentTimeMillis()
        val utcTime = TimeUtil.getUtcTimestamp()
        
        assertTrue("UTC time should be greater than system time", utcTime > systemTime)
        assertTrue("UTC time should be close to system time + offset", 
                   Math.abs(utcTime - (systemTime + offset)) < 100)
        
        // Reset
        TimeUtil.setPcTimeOffset(0L)
    }

    @Test
    fun testTimeConversion() {
        val offset = 2000L
        TimeUtil.setPcTimeOffset(offset)
        
        val systemTime = System.currentTimeMillis()
        val utcTime = TimeUtil.systemToUtc(systemTime)
        val backToSystem = TimeUtil.utcToSystem(utcTime)
        
        assertEquals(systemTime + offset, utcTime)
        assertEquals(systemTime, backToSystem)
        
        // Reset
        TimeUtil.setPcTimeOffset(0L)
    }

    @Test
    fun testFormatTimestamp() {
        val timestamp = 1640995200000L // 2022-01-01 00:00:00 UTC
        val formatted = TimeUtil.formatTimestamp(timestamp)
        
        assertTrue("Formatted time should contain year", formatted.contains("2022") || formatted.contains("2021"))
        assertTrue("Formatted time should contain time separator", formatted.contains(":"))
    }

    @Test
    fun testGenerateSessionId() {
        val sessionId1 = TimeUtil.generateSessionId()
        val sessionId2 = TimeUtil.generateSessionId()
        val customId = TimeUtil.generateSessionId("CUSTOM")
        
        assertTrue("Session ID should start with GSR", sessionId1.startsWith("GSR_"))
        assertTrue("Session ID should start with GSR", sessionId2.startsWith("GSR_"))
        assertTrue("Custom session ID should start with CUSTOM", customId.startsWith("CUSTOM_"))
        
        // Check that IDs are not empty and contain underscores
        assertTrue("Session ID should not be empty", sessionId1.length > 4)
        assertTrue("Session ID should contain underscore", sessionId1.contains("_"))
    }
}