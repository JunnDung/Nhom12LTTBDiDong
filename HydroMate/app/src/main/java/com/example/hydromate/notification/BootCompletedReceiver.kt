package com.example.hydromate.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receiver để khôi phục các thông báo đã lên lịch sau khi thiết bị khởi động lại
 */
class BootCompletedReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Lấy cài đặt thông báo
                    val appDataStore = AppDataStoreFactory.create(context)
                    val settings = appDataStore.getNotificationSettingsSync()
                    
                    // Nếu thông báo được bật, lên lịch lại
                    if (settings.enabled) {
                        NotificationHelper.scheduleNotifications(context, settings)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
} 