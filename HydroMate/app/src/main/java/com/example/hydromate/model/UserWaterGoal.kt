package com.example.hydromate.model

import kotlinx.serialization.Serializable

@Serializable
data class UserWaterGoal(
    val gender: String = "",
    val weight: Int = 0,
    val wakeUpTime: String = "06:00",
    val sleepTime: String = "22:00",
    val dailyWaterGoal: Int = 0
) 