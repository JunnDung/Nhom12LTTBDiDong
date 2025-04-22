package com.example.hydromate

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hydromate.data.AppViewModel
import com.example.hydromate.model.UserWaterGoal
import com.example.hydromate.notification.NotificationHelper
import com.example.hydromate.ui.navigation.AppNavigation
import com.example.hydromate.ui.theme.HydroMateTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    private val TAG = "MainActivity"
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Nếu quyền được cấp, lên lịch thông báo
            Log.d(TAG, "Quyền thông báo được cấp, lên lịch thông báo")
            scheduleNotificationsIfEnabled()
        } else {
            // Thông báo người dùng về việc cần cấp quyền
            Log.d(TAG, "Quyền thông báo bị từ chối")
            Toast.makeText(
                this, 
                "Bạn cần cấp quyền thông báo để nhận nhắc nhở uống nước", 
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private val requestExactAlarmLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // Kiểm tra xem quyền đã được cấp chưa
        checkAndScheduleNotifications()
    }
    
    // Scope để thực hiện các công việc ngoài luồng UI
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Tạo kênh thông báo
        NotificationHelper.createNotificationChannel(this)
        
        // Kiểm tra và yêu cầu quyền
        checkAndRequestPermissions()
        
        setContent {
            // Khởi tạo ViewModel
            val appViewModel: AppViewModel = viewModel(
                factory = AppViewModel.Factory(applicationContext)
            )
            
            HydroMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        onSetupComplete = { userWaterGoal ->
                            // Lưu mục tiêu nước khi hoàn thành thiết lập
                            appViewModel.saveUserWaterGoal(userWaterGoal)
                        },
                        appViewModel = appViewModel
                    )
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Khi ứng dụng được mở lại, kiểm tra và lên lịch thông báo nếu cần
        checkAndScheduleNotifications()
    }
    
    private fun checkAndRequestPermissions() {
        // Yêu cầu quyền thông báo trên Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Đã có quyền thông báo, kiểm tra quyền đặt lịch chính xác
                checkExactAlarmPermission()
            }
        } else {
            // Dưới Android 13 không cần quyền thông báo riêng
            checkExactAlarmPermission()
        }
    }
    
    private fun checkExactAlarmPermission() {
        // Kiểm tra quyền đặt lịch chính xác trên Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // Mở cài đặt để người dùng cấp quyền
                Toast.makeText(
                    this,
                    "Ứng dụng cần quyền đặt lịch chính xác để gửi thông báo đúng giờ",
                    Toast.LENGTH_LONG
                ).show()
                
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                requestExactAlarmLauncher.launch(intent)
            } else {
                // Đã có quyền đặt lịch chính xác, lên lịch thông báo
                scheduleNotificationsIfEnabled()
            }
        } else {
            // Dưới Android 12 không cần quyền đặt lịch chính xác riêng
            scheduleNotificationsIfEnabled()
        }
    }
    
    private fun checkAndScheduleNotifications() {
        // Kiểm tra tất cả quyền cần thiết
        var canSchedule = true
        
        // Kiểm tra quyền thông báo (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                canSchedule = false
            }
        }
        
        // Kiểm tra quyền đặt lịch chính xác (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                canSchedule = false
            }
        }
        
        // Nếu có đủ quyền, lên lịch thông báo
        if (canSchedule) {
            scheduleNotificationsIfEnabled()
        } else {
            Log.d(TAG, "Không đủ quyền để lên lịch thông báo")
        }
    }
    
    private fun scheduleNotificationsIfEnabled() {
        applicationScope.launch {
            try {
                val appDataStore = com.example.hydromate.notification.AppDataStoreFactory.create(applicationContext)
                val notificationSettings = appDataStore.getNotificationSettingsSync()
                
                // Lên lịch thông báo nếu đã bật
                if (notificationSettings.enabled) {
                    Log.d(TAG, "Lên lịch thông báo với cài đặt: $notificationSettings")
                    NotificationHelper.scheduleNotifications(applicationContext, notificationSettings)
                } else {
                    Log.d(TAG, "Thông báo bị tắt trong cài đặt")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi lên lịch thông báo: ${e.message}", e)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HydroMateTheme {
        Greeting("Android")
    }
}