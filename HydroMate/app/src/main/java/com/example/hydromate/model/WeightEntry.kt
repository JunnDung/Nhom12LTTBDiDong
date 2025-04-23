package com.example.hydromate.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class WeightEntry(
    val weight: Int,
    val dateString: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val id: String = LocalDate.now().toString()
) {
    fun getDate(): LocalDate {
        return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
    }
    
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