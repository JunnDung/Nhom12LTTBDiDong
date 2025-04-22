package com.example.hydromate.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {
    
    private val TAG = "NotificationReceiver"
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Đã nhận broadcast để hiển thị thông báo")
        
        // Hiển thị thông báo
        try {
            NotificationHelper.showNotification(context)
            Log.d(TAG, "Đã hiển thị thông báo thành công")
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi hiển thị thông báo: ${e.message}", e)
        }
        
        // Lên lịch cho thông báo tiếp theo
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Lấy cài đặt thông báo hiện tại
                val appDataStore = AppDataStoreFactory.create(context)
                val settings = appDataStore.getNotificationSettingsSync()
                
                Log.d(TAG, "Đã đọc cài đặt thông báo: $settings")
                
                // Nếu thông báo vẫn được bật, lên lịch thông báo kế tiếp
                if (settings.enabled) {
                    NotificationHelper.scheduleNotifications(context, settings)
                    Log.d(TAG, "Đã lên lịch thông báo tiếp theo")
                } else {
                    Log.d(TAG, "Thông báo đã bị tắt, không lên lịch tiếp")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi lên lịch thông báo tiếp theo: ${e.message}", e)
            }
        }
    }
}

/**
 * Factory để tạo AppDataStore mà không phụ thuộc vào ViewModel
 */
object AppDataStoreFactory {
    fun create(context: Context): com.example.hydromate.data.AppDataStore {
        return com.example.hydromate.data.AppDataStore(context)
    }
} 