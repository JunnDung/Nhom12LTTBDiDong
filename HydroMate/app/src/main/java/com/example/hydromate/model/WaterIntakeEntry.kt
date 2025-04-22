package com.example.hydromate.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class WaterIntakeEntry(
    val amount: Int, // Lượng nước uống tính bằng ml
    val timestampString: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), // Thời gian uống nước dưới dạng chuỗi
    val id: String = LocalDateTime.now().toString() // ID duy nhất cho mỗi lần uống nước
) {
    // Chuyển đổi từ chuỗi sang LocalDateTime
    fun getTimestamp(): LocalDateTime {
        return LocalDateTime.parse(timestampString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
    
    // Companion object để tạo từ LocalDateTime
    companion object {
        fun create(amount: Int, timestamp: LocalDateTime = LocalDateTime.now()): WaterIntakeEntry {
            val timestampStr = timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            return WaterIntakeEntry(
                amount = amount,
                timestampString = timestampStr,
                id = timestamp.toString()
            )
        }
    }
} 