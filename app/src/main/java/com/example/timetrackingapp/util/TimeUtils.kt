package com.example.timetrackingapp.util

/**
 * Utility class for time-related calculations and formatting.
 * This helps in maintaining consistency across the app and facilitates unit testing.
 */
object TimeUtils {

    /**
     * Formats a duration in seconds into a HH:mm:ss string.
     * @param seconds The total duration in seconds.
     * @return A formatted string like "01:15:30".
     */
    fun formatDuration(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return String.format("%02d:%02d:%02d", h, m, s)
    }

    /**
     * Calculates the total duration from a list of sessions.
     * @param sessions List of session durations in seconds.
     * @return Total duration in seconds.
     */
    fun calculateTotalTime(sessions: List<Long>): Long {
        return sessions.sum()
    }
}