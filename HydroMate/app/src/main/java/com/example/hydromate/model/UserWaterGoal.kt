package com.example.hydromate.model

data class UserWaterGoal(
    val gender: String = "", // "Nam" hoặc "Nữ"
    val weight: Int = 0, // Cân nặng tính bằng kg
    val wakeUpTime: String = "06:00", // Thời gian thức dậy
    val sleepTime: String = "22:00", // Thời gian đi ngủ
    val dailyWaterGoal: Int = 0 // Mục tiêu uống nước hàng ngày tính bằng ml
) 