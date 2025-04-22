package com.example.hydromate.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hydromate.model.NotificationSettings
import com.example.hydromate.model.UserWaterGoal
import com.example.hydromate.model.WaterIntakeEntry
import com.example.hydromate.model.WeightEntry
import com.example.hydromate.notification.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class AppViewModel(private val appDataStore: AppDataStore) : ViewModel() {
    
    // StateFlow cho UserWaterGoal
    private val _userWaterGoal = MutableStateFlow(UserWaterGoal())
    val userWaterGoal: StateFlow<UserWaterGoal> = _userWaterGoal.asStateFlow()
    
    // StateFlow cho danh sách lần uống nước
    val waterIntakeEntries = appDataStore.getWaterIntakeEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // StateFlow cho danh sách cân nặng
    val weightEntries = appDataStore.getWeightEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // StateFlow cho cài đặt thông báo
    val notificationSettings = appDataStore.getNotificationSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NotificationSettings()
        )
    
    // Lưu và cập nhật cài đặt thông báo
    fun saveNotificationSettings(settings: NotificationSettings, context: Context) {
        viewModelScope.launch {
            appDataStore.saveNotificationSettings(settings)
            
            // Cập nhật lịch thông báo tương ứng
            if (settings.enabled) {
                NotificationHelper.scheduleNotifications(context, settings)
            } else {
                NotificationHelper.cancelAllNotifications(context)
            }
        }
    }
    
    init {
        // Khởi tạo dữ liệu
        loadUserWaterGoal()
    }
    
    // Đọc UserWaterGoal từ DataStore
    private fun loadUserWaterGoal() {
        viewModelScope.launch {
            appDataStore.getUserWaterGoal().collect { goal ->
                _userWaterGoal.value = goal
            }
        }
    }
    
    // Lưu UserWaterGoal
    fun saveUserWaterGoal(userWaterGoal: UserWaterGoal) {
        viewModelScope.launch {
            appDataStore.saveUserWaterGoal(userWaterGoal)
        }
    }
    
    // Thêm lần uống nước mới
    fun addWaterIntakeEntry(amount: Int, timestamp: LocalDateTime = LocalDateTime.now()) {
        viewModelScope.launch {
            val entry = WaterIntakeEntry.create(amount, timestamp)
            appDataStore.addWaterIntakeEntry(entry)
        }
    }
    
    // Xóa một lần uống nước
    fun removeWaterIntakeEntry(entryId: String) {
        viewModelScope.launch {
            appDataStore.removeWaterIntakeEntry(entryId)
        }
    }
    
    // Thêm mục cân nặng mới
    fun addWeightEntry(weight: Int, date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            val entry = WeightEntry.create(weight, date)
            appDataStore.addWeightEntry(entry)
        }
    }
    
    // Lấy tổng lượng nước đã uống trong ngày
    fun getTodayWaterIntake(): Int {
        val today = LocalDate.now()
        return waterIntakeEntries.value
            .filter { 
                val entryDate = it.getTimestamp().toLocalDate()
                entryDate.isEqual(today)
            }
            .sumOf { it.amount }
    }
    
    // Kiểm tra xem người dùng đã đạt mục tiêu lượng nước hôm nay chưa
    fun isTodayWaterGoalAchieved(): Boolean {
        val todayIntake = getTodayWaterIntake()
        val goal = userWaterGoal.value.dailyWaterGoal
        return todayIntake >= goal
    }
    
    // Lấy tiến độ hoàn thành mục tiêu nước hôm nay (0.0 - 1.0)
    fun getTodayWaterProgress(): Float {
        val todayIntake = getTodayWaterIntake()
        val goal = userWaterGoal.value.dailyWaterGoal
        return if (goal > 0) (todayIntake.toFloat() / goal).coerceAtMost(1.0f) else 0f
    }
    
    // Lấy mục cân nặng mới nhất
    fun getLatestWeight(): Int? {
        return weightEntries.value.maxByOrNull { it.getDate() }?.weight
    }
    
    // Factory để tạo ViewModel
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
                val dataStore = AppDataStore(context)
                return AppViewModel(dataStore) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 