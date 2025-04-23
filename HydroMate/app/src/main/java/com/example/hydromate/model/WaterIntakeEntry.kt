package com.example.hydromate.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class WaterIntakeEntry(
    val amount: Int,
    val timestampString: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    val id: String = LocalDateTime.now().toString()
) {
    fun getTimestamp(): LocalDateTime {
        return LocalDateTime.parse(timestampString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
    
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