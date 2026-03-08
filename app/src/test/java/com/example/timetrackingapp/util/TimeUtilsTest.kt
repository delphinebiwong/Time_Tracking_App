package com.example.timetrackingapp.util

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeUtilsTest {

    @Test
    fun formatDuration_correctlyFormatsSeconds() {
        val seconds = 3661L // 1 hour, 1 minute, 1 second
        val result = TimeUtils.formatDuration(seconds)
        assertEquals("01:01:01", result)
    }

    @Test
    fun formatDuration_zeroSeconds() {
        val seconds = 0L
        val result = TimeUtils.formatDuration(seconds)
        assertEquals("00:00:00", result)
    }

    @Test
    fun calculateTotalTime_sumsCorrectly() {
        val sessions = listOf(100L, 200L, 300L)
        val result = TimeUtils.calculateTotalTime(sessions)
        assertEquals(600L, result)
    }
}