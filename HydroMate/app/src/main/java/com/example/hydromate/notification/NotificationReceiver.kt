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
        // Hiển thị thông báo
        try {
            NotificationHelper.showNotification(context)
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi hiển thị thông báo: ${e.message}", e)
        }
        
        // Lên lịch cho thông báo tiếp theo
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Lấy cài đặt thông báo hiện tại
                val appDataStore = AppDataStoreFactory.create(context)
                val settings = appDataStore.getNotificationSettingsSync()
                
                // Nếu thông báo vẫn được bật, lên lịch thông báo kế tiếp
                if (settings.enabled) {
                    NotificationHelper.scheduleNotifications(context, settings)
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