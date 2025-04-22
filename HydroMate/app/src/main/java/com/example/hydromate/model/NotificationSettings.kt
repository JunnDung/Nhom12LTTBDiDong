package com.example.hydromate.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationSettings(
    val enabled: Boolean = true,
    val intervalMinutes: Int = 60, // Tần suất thông báo (phút)
    val startTime: String = "08:00", // Thời gian bắt đầu
    val endTime: String = "22:00" // Thời gian kết thúc
) 