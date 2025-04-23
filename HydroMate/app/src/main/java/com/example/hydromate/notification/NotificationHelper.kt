package com.example.hydromate.notification

import android.Manifest
import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.hydromate.MainActivity
import com.example.hydromate.R
import com.example.hydromate.model.NotificationSettings
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Random

object NotificationHelper {
    
    private const val CHANNEL_ID = "hydro_mate_channel"
    private const val CHANNEL_NAME = "HydroMate Reminders"
    private const val CHANNEL_DESCRIPTION = "Thông báo nhắc uống nước"
    private const val NOTIFICATION_ID = 100
    private const val REQUEST_CODE_PREFIX = 1000
    private const val TAG = "NotificationHelper"
    
    // Tạo kênh thông báo cho Android 8.0+
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
            }
            
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    // Kiểm tra quyền thông báo
    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Trước Android 13 không cần kiểm tra quyền riêng
            true
        }
    }
    
    // Hiển thị thông báo tức thì
    fun showNotification(context: Context) {
        // Kiểm tra quyền thông báo
        if (!hasNotificationPermission(context)) {
            Log.w(TAG, "Không có quyền hiển thị thông báo")
            return
        }
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, 
            PendingIntent.FLAG_IMMUTABLE
        )
        
        // Chọn ngẫu nhiên một tin nhắn thông báo
        val message = getRandomWaterReminderMessage()
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle("Đã đến giờ uống nước!")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        
        try {
            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        } catch (e: SecurityException) {
            // Xử lý ngoại lệ liên quan đến quyền
            Log.e(TAG, "Lỗi hiển thị thông báo (quyền): ${e.message}")
        } catch (e: Exception) {
            // Xử lý các ngoại lệ khác
            Log.e(TAG, "Lỗi không xác định khi hiển thị thông báo", e)
        }
    }
    
    // Lên lịch thông báo dựa trên cài đặt
    fun scheduleNotifications(context: Context, settings: NotificationSettings) {
        // Hủy tất cả thông báo cũ trước khi lên lịch mới
        cancelAllNotifications(context)
        
        if (!settings.enabled) {
            return
        }
        
        // Kiểm tra quyền thông báo
        if (!hasNotificationPermission(context)) {
            Log.w(TAG, "Không có quyền hiển thị thông báo")
            return
        }
        
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Kiểm tra xem AlarmManager có được cấp phép để đặt lịch chính xác không
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.e(TAG, "Không có quyền SCHEDULE_EXACT_ALARM")
            return
        }
        
        // Định dạng giờ:phút
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        
        try {
            // Thời gian bắt đầu và kết thúc
            val startTime = LocalTime.parse(settings.startTime, formatter)
            val endTime = LocalTime.parse(settings.endTime, formatter)
            
            // Lấy thời gian hiện tại
            val now = LocalDateTime.now()
            val currentTime = now.toLocalTime()
            
            // Tính toán thời gian bắt đầu kế tiếp
            var nextTime: LocalDateTime
            
            if (currentTime.isBefore(startTime)) {
                // Thời gian hiện tại trước thời gian bắt đầu => lên lịch cho thời gian bắt đầu hôm nay
                nextTime = now.with(startTime)
            } else if (currentTime.isAfter(endTime)) {
                // Thời gian hiện tại sau thời gian kết thúc => lên lịch cho thời gian bắt đầu ngày mai
                nextTime = now.plusDays(1).with(startTime)
            } else {
                // Thời gian hiện tại nằm trong khoảng => lên lịch sau khoảng thời gian
                // Kiểm tra xem có đến giờ hiển thị thông báo ngay không
                if (isTriggerTimeNow(currentTime, settings)) {
                    // Hiển thị thông báo ngay lập tức
                    showNotification(context)
                }
                
                // Lên lịch cho thời gian kế tiếp
                nextTime = now.plusMinutes(settings.intervalMinutes.toLong())
                
                // Nếu thời gian kế tiếp vượt quá thời gian kết thúc, chuyển sang ngày hôm sau
                if (nextTime.toLocalTime().isAfter(endTime)) {
                    nextTime = now.plusDays(1).with(startTime)
                }
            }
            
            // Tạo intent cho BroadcastReceiver
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_PREFIX,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            
            // Lên lịch thông báo đầu tiên
            val triggerTime = nextTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            
            // Đặt báo thức chính xác
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi lên lịch thông báo: ${e.message}", e)
        }
    }
    
    // Kiểm tra xem thời gian hiện tại có phải là thời điểm hiển thị thông báo không
    private fun isTriggerTimeNow(currentTime: LocalTime, settings: NotificationSettings): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val startTime = LocalTime.parse(settings.startTime, formatter)
        
        // Nếu thời gian hiện tại là thời gian bắt đầu hoặc là bội của khoảng thông báo
        if (currentTime.hour == startTime.hour && currentTime.minute == startTime.minute) {
            return true
        }
        
        // Tính số phút từ thời gian bắt đầu đến thời gian hiện tại
        val startMinutes = startTime.hour * 60 + startTime.minute
        val currentMinutes = currentTime.hour * 60 + currentTime.minute
        val diffMinutes = currentMinutes - startMinutes
        
        // Nếu thời gian hiện tại là bội của khoảng thông báo kể từ thời gian bắt đầu
        if (diffMinutes > 0 && diffMinutes % settings.intervalMinutes == 0) {
            return true
        }
        
        return false
    }
    
    // Hủy tất cả thông báo đã lên lịch
    fun cancelAllNotifications(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_PREFIX,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        try {
            alarmManager.cancel(pendingIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi hủy thông báo: ${e.message}")
        }
    }
    
    // Lấy ngẫu nhiên một tin nhắn thông báo
    private fun getRandomWaterReminderMessage(): String {
        val messages = listOf(
            "Đã đến lúc uống nước rồi! Cơ thể bạn cần nước để hoạt động tốt.",
            "Bạn đã quên uống nước rồi phải không? Đến lúc bổ sung nước rồi đấy!",
            "Uống nước ngay để giữ cơ thể luôn khỏe mạnh nhé!",
            "Nhấp một ngụm nước sẽ giúp bạn tỉnh táo và tràn đầy năng lượng!",
            "Đừng quên uống nước nhé! Làn da của bạn sẽ cảm ơn bạn đấy.",
            "Hãy nhớ uống nước thường xuyên để duy trì sức khỏe tốt nhất!",
            "Một cốc nước ngay bây giờ sẽ giúp bạn làm việc hiệu quả hơn.",
            "Đừng để cơ thể bị mất nước! Uống nước ngay nào!"
        )
        
        return messages[Random().nextInt(messages.size)]
    }
} 