package com.example.hydromate.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.hydromate.R
import com.example.hydromate.data.AppViewModel
import com.example.hydromate.model.NotificationSettings

@Composable
fun SettingsScreen(
    appViewModel: AppViewModel,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onStatsClick: () -> Unit = {},
    appVersion: String = "1.0"
) {
    val context = LocalContext.current
    val primaryBlue = Color(0xFF00B2FF)
    val scrollState = rememberScrollState()
    
    val notificationSettings by appViewModel.notificationSettings.collectAsState()
    
    var showNotificationDialog by remember { mutableStateOf(false) }
    
    var tempSettings by remember(notificationSettings) { 
        mutableStateOf(notificationSettings) 
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(primaryBlue)
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowLeft,
                        contentDescription = "Quay lại",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_water_drop),
                        contentDescription = "Logo",
                        tint = primaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
            }
            
            Text(
                text = "Phiên bản: $appVersion",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color.White)
                    .padding(vertical = 8.dp)
            ) {
                SettingItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notifications),
                            contentDescription = "Lời nhắc nhở",
                            tint = primaryBlue
                        )
                    },
                    title = "Lời nhắc nhở",
                    subtitle = if (notificationSettings.enabled) 
                        "Mỗi ${notificationSettings.intervalMinutes} phút từ ${notificationSettings.startTime} đến ${notificationSettings.endTime}" 
                    else 
                        "Đã tắt",
                    onClick = {
                        tempSettings = notificationSettings
                        showNotificationDialog = true
                    }
                )
                
                Divider(modifier = Modifier.padding(start = 56.dp, end = 16.dp))
                
                SettingItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Đánh giá",
                            tint = Color(0xFFFFD700)
                        )
                    },
                    title = "Đánh giá chúng tôi",
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("market://details?id=${context.packageName}")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                            }
                            context.startActivity(intent)
                        }
                    }
                )
                
                Divider(modifier = Modifier.padding(start = 56.dp, end = 16.dp))
                
                SettingItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_email),
                            contentDescription = "Nhận xét",
                            tint = primaryBlue
                        )
                    },
                    title = "Nhận xét",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:feedback@hydromate.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Phản hồi về ứng dụng HydroMate")
                        }
                        context.startActivity(intent)
                    }
                )
                
                Divider(modifier = Modifier.padding(start = 56.dp, end = 16.dp))
                
                SettingItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_more),
                            contentDescription = "Khác",
                            tint = primaryBlue
                        )
                    },
                    title = "Khác",
                    onClick = {
                        // Xử lý khi nhấn vào mục khác
                    }
                )
                
                Divider(modifier = Modifier.padding(start = 56.dp, end = 16.dp))
                
                SettingItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = "Phiên bản",
                            tint = Color.Gray
                        )
                    },
                    title = "Phiên bản: $appVersion",
                    onClick = {
                        // Không làm gì khi nhấn vào phiên bản
                    },
                    isClickable = false
                )
            }
        }
        
        if (showNotificationDialog) {
            NotificationSettingsDialog(
                notificationsEnabled = tempSettings.enabled,
                notificationInterval = tempSettings.intervalMinutes,
                startTime = tempSettings.startTime,
                endTime = tempSettings.endTime,
                onEnabledChange = { tempSettings = tempSettings.copy(enabled = it) },
                onIntervalChange = { tempSettings = tempSettings.copy(intervalMinutes = it) },
                onStartTimeChange = { tempSettings = tempSettings.copy(startTime = it) },
                onEndTimeChange = { tempSettings = tempSettings.copy(endTime = it) },
                onDismiss = { showNotificationDialog = false },
                onSave = {
                    appViewModel.saveNotificationSettings(tempSettings, context)
                    showNotificationDialog = false
                }
            )
        }
    }
}

@Composable
fun SettingItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    isClickable: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isClickable) { onClick() }
            .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = if (title.startsWith("Phiên bản")) Color.Gray else Color.DarkGray
            )
            
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsDialog(
    notificationsEnabled: Boolean,
    notificationInterval: Int,
    startTime: String,
    endTime: String,
    onEnabledChange: (Boolean) -> Unit,
    onIntervalChange: (Int) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val primaryBlue = Color(0xFF00B2FF)
    
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Cài đặt lời nhắc nhở",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Bật thông báo",
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                    
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = onEnabledChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = primaryBlue,
                            checkedTrackColor = primaryBlue.copy(alpha = 0.5f)
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (notificationsEnabled) {
                    Text(
                        text = "Tần suất thông báo: $notificationInterval phút",
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Slider(
                        value = notificationInterval.toFloat(),
                        onValueChange = { onIntervalChange(it.toInt()) },
                        valueRange = 30f..180f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = primaryBlue,
                            activeTrackColor = primaryBlue
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showStartTimePicker = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Thời gian bắt đầu",
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                        
                        Text(
                            text = startTime,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryBlue
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showEndTimePicker = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Thời gian kết thúc",
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                        
                        Text(
                            text = endTime,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryBlue
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue
                    )
                ) {
                    Text(
                        text = "Lưu cài đặt",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
    
    if (showStartTimePicker) {
        TimePickerDialog(
            initialTime = parseTimeString(startTime),
            onTimeSelected = { hour, minute ->
                val formattedTime = formatTime(hour, minute)
                onStartTimeChange(formattedTime)
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false }
        )
    }
    
    if (showEndTimePicker) {
        TimePickerDialog(
            initialTime = parseTimeString(endTime),
            onTimeSelected = { hour, minute ->
                val formattedTime = formatTime(hour, minute)
                onEndTimeChange(formattedTime)
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: Pair<Int, Int>,
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.first,
        initialMinute = initialTime.second,
        is24Hour = true
    )
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        containerColor = Color.White,
                        periodSelectorSelectedContainerColor = Color(0xFF00B2FF),
                        selectorColor = Color(0xFF00B2FF)
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray
                        )
                    ) {
                        Text("Hủy")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            onTimeSelected(
                                timePickerState.hour,
                                timePickerState.minute
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00B2FF)
                        )
                    ) {
                        Text("Đồng ý")
                    }
                }
            }
        }
    }
}

private fun parseTimeString(timeString: String): Pair<Int, Int> {
    val parts = timeString.split(":")
    return try {
        Pair(parts[0].toInt(), parts[1].toInt())
    } catch (e: Exception) {
        Pair(8, 0) // Mặc định 8:00
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    return String.format("%02d:%02d", hour, minute)
} 