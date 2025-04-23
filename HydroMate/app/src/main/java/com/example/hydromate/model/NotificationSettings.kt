package com.example.hydromate.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationSettings(
    val enabled: Boolean = true,
    val intervalMinutes: Int = 60,
    val startTime: String = "08:00",
    val endTime: String = "22:00"
) 