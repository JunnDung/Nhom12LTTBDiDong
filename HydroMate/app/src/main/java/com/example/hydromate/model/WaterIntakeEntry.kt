package com.example.hydromate.model

import java.time.LocalDateTime

data class WaterIntakeEntry(
    val amount: Int, // Lượng nước uống tính bằng ml
    val timestamp: LocalDateTime = LocalDateTime.now(), // Thời gian uống nước
    val id: String = timestamp.toString() // ID duy nhất cho mỗi lần uống nước
) 