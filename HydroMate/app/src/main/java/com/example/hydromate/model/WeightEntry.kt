package com.example.hydromate.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class WeightEntry(
    val weight: Int, // Cân nặng tính bằng kg
    val dateString: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), // Ngày ghi nhận cân nặng dưới dạng chuỗi
    val id: String = LocalDate.now().toString() // ID duy nhất cho mỗi mục cân nặng
) {
    // Chuyển đổi từ chuỗi sang LocalDate
    fun getDate(): LocalDate {
        return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
    }
    
    // Companion object để tạo từ LocalDate
    companion object {
        fun create(weight: Int, date: LocalDate = LocalDate.now()): WeightEntry {
            val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            return WeightEntry(
                weight = weight,
                dateString = dateStr,
                id = date.toString()
            )
        }
    }
} 