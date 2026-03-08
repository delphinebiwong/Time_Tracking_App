package com.example.timetrackingapp.domain.model

data class Project(
    val id: String = "",
    val name: String = "",
    val colorName: String = "Blue",
    val goalHours: Int = 0,
    val totalTimeSeconds: Long = 0L,
    val userId: String = ""
) {
    val colorHex: String
        get() = when (colorName) {
            "Red" -> "#F44336"
            "Blue" -> "#2196F3"
            "Green" -> "#4CAF50"
            "Yellow" -> "#FFEB3B"
            "Purple" -> "#9C27B0"
            "Orange" -> "#FF9800"
            "Pink" -> "#E91E63"
            "Teal" -> "#009688"
            else -> "#9E9E9E"
        }
}