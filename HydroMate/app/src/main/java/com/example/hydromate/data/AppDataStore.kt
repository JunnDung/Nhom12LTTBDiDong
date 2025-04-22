package com.example.hydromate.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.hydromate.model.NotificationSettings
import com.example.hydromate.model.UserWaterGoal
import com.example.hydromate.model.WaterIntakeEntry
import com.example.hydromate.model.WeightEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Extension để tạo DataStore riêng cho ứng dụng
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hydro_mate_settings")

class AppDataStore(private val context: Context) {
    
    // Keys cho DataStore
    private companion object {
        val USER_WATER_GOAL_KEY = stringPreferencesKey("user_water_goal")
        val WATER_INTAKE_ENTRIES_KEY = stringPreferencesKey("water_intake_entries")
        val WEIGHT_ENTRIES_KEY = stringPreferencesKey("weight_entries")
        val NOTIFICATION_SETTINGS_KEY = stringPreferencesKey("notification_settings")
    }
    
    // JSON encoder/decoder
    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }
    
    // Lưu UserWaterGoal
    suspend fun saveUserWaterGoal(userWaterGoal: UserWaterGoal) {
        val userWaterGoalJson = json.encodeToString(userWaterGoal)
        context.dataStore.edit { preferences ->
            preferences[USER_WATER_GOAL_KEY] = userWaterGoalJson
        }
    }
    
    // Lấy UserWaterGoal
    fun getUserWaterGoal(): Flow<UserWaterGoal> {
        return context.dataStore.data.map { preferences ->
            val userWaterGoalJson = preferences[USER_WATER_GOAL_KEY] ?: return@map UserWaterGoal()
            try {
                json.decodeFromString(userWaterGoalJson)
            } catch (e: Exception) {
                UserWaterGoal()
            }
        }
    }
    
    // Lấy UserWaterGoal đồng bộ
    suspend fun getUserWaterGoalSync(): UserWaterGoal {
        return getUserWaterGoal().first()
    }
    
    // Lưu danh sách lần uống nước
    suspend fun saveWaterIntakeEntries(entries: List<WaterIntakeEntry>) {
        val entriesJson = json.encodeToString(entries)
        context.dataStore.edit { preferences ->
            preferences[WATER_INTAKE_ENTRIES_KEY] = entriesJson
        }
    }
    
    // Lấy danh sách lần uống nước
    fun getWaterIntakeEntries(): Flow<List<WaterIntakeEntry>> {
        return context.dataStore.data.map { preferences ->
            val entriesJson = preferences[WATER_INTAKE_ENTRIES_KEY] ?: return@map emptyList()
            try {
                json.decodeFromString<List<WaterIntakeEntry>>(entriesJson)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    // Lấy danh sách lần uống nước đồng bộ
    suspend fun getWaterIntakeEntriesSync(): List<WaterIntakeEntry> {
        return getWaterIntakeEntries().first()
    }
    
    // Thêm một lần uống nước mới
    suspend fun addWaterIntakeEntry(entry: WaterIntakeEntry) {
        val currentEntries = getWaterIntakeEntriesSync().toMutableList()
        currentEntries.add(entry)
        saveWaterIntakeEntries(currentEntries)
    }
    
    // Xóa một lần uống nước
    suspend fun removeWaterIntakeEntry(entryId: String) {
        val currentEntries = getWaterIntakeEntriesSync().toMutableList()
        val updatedEntries = currentEntries.filter { it.id != entryId }
        saveWaterIntakeEntries(updatedEntries)
    }
    
    // Lưu danh sách cân nặng
    suspend fun saveWeightEntries(entries: List<WeightEntry>) {
        val entriesJson = json.encodeToString(entries)
        context.dataStore.edit { preferences ->
            preferences[WEIGHT_ENTRIES_KEY] = entriesJson
        }
    }
    
    // Lấy danh sách cân nặng
    fun getWeightEntries(): Flow<List<WeightEntry>> {
        return context.dataStore.data.map { preferences ->
            val entriesJson = preferences[WEIGHT_ENTRIES_KEY] ?: return@map emptyList()
            try {
                json.decodeFromString<List<WeightEntry>>(entriesJson)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    // Lấy danh sách cân nặng đồng bộ
    suspend fun getWeightEntriesSync(): List<WeightEntry> {
        return getWeightEntries().first()
    }
    
    // Thêm một mục cân nặng mới
    suspend fun addWeightEntry(entry: WeightEntry) {
        val currentEntries = getWeightEntriesSync().toMutableList()
        
        // Kiểm tra xem đã có mục cân nặng cho ngày này chưa
        val existingEntryIndex = currentEntries.indexOfFirst { it.id == entry.id }
        
        if (existingEntryIndex != -1) {
            // Nếu đã có, thay thế
            currentEntries[existingEntryIndex] = entry
        } else {
            // Nếu chưa có, thêm mới
            currentEntries.add(entry)
        }
        
        saveWeightEntries(currentEntries)
    }
    
    // Lưu cài đặt thông báo
    suspend fun saveNotificationSettings(settings: NotificationSettings) {
        val settingsJson = json.encodeToString(settings)
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_SETTINGS_KEY] = settingsJson
        }
    }
    
    // Lấy cài đặt thông báo
    fun getNotificationSettings(): Flow<NotificationSettings> {
        return context.dataStore.data.map { preferences ->
            val settingsJson = preferences[NOTIFICATION_SETTINGS_KEY] ?: return@map NotificationSettings()
            try {
                json.decodeFromString(settingsJson)
            } catch (e: Exception) {
                NotificationSettings()
            }
        }
    }
    
    // Lấy cài đặt thông báo đồng bộ
    suspend fun getNotificationSettingsSync(): NotificationSettings {
        return getNotificationSettings().first()
    }
} 