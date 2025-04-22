package com.example.hydromate.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.window.DialogProperties
import com.example.hydromate.R
import com.example.hydromate.model.UserWaterGoal
import com.example.hydromate.model.WaterIntakeEntry
import com.example.hydromate.ui.theme.HydroMateTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userWaterGoal: UserWaterGoal,
    waterIntakeEntries: List<WaterIntakeEntry>,
    onAddWaterIntake: (Int) -> Unit,
    onDeleteWaterIntake: (String) -> Unit,
    onSettingsClick: () -> Unit = {},
    onStatsClick: () -> Unit = {}
) {
    // Lưu trữ thông tin và trạng thái dialog xóa
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedEntryForDelete by remember { mutableStateOf<WaterIntakeEntry?>(null) }
    
    // Thêm biến trạng thái cho Date Picker Dialog
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    // Lấy mục tiêu từ userWaterGoal
    val waterGoal = userWaterGoal.dailyWaterGoal
    
    // Lọc danh sách nước theo ngày được chọn
    val filteredWaterEntries = waterIntakeEntries.filter { entry ->
        val entryDate = entry.getTimestamp().toLocalDate()
        entryDate.isEqual(selectedDate)
    }
    
    // Tính tổng lượng nước đã uống (chỉ tính cho ngày được chọn)
    val currentWaterIntake = filteredWaterEntries.sumOf { it.amount }
    
    // Tính toán tiến trình
    val progress = if (waterGoal > 0) {
        (currentWaterIntake.toFloat() / waterGoal.toFloat()).coerceAtMost(1.0f)
    } else {
        0f
    }
    
    // Màu chính của ứng dụng
    val primaryBlue = Color(0xFF00B2FF)
    val lightBlue = Color(0xFF99DDFF) // Màu xanh nước biển nhạt cho thanh tiến trình
    val lightYellow = Color(0xFFFFE600)
    val transparentWhite = Color(0x33FFFFFF)
    val orangeIndicator = Color(0xFFFF6D33)
    
    // Hàm xử lý xóa nước
    val handleDeleteConfirmation = {
        selectedEntryForDelete?.let { entry ->
            onDeleteWaterIntake(entry.id)
        }
        showDeleteDialog = false
        selectedEntryForDelete = null
    }
    
    // Hàm xử lý khi người dùng bấm vào một mục nước
    val onWaterItemClick = { entry: WaterIntakeEntry ->
        selectedEntryForDelete = entry
        showDeleteDialog = true
    }
    
    // DatePicker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val newDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            selectedDate = newDate
                        }
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue
                    )
                ) {
                    Text("Đồng ý")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDatePicker = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    )
                ) {
                    Text("Hủy")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Chọn ngày",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Phần nền xanh ở trên (chiếm khoảng 30% màn hình)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.23f)
                .background(primaryBlue)
        )
        
        // Phần nền trắng ở dưới (chiếm khoảng 70% màn hình)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.77f)
                .align(Alignment.BottomCenter)
                .background(Color.White)
        )
        
        // Nội dung chính
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Thanh công cụ trên cùng
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút thống kê (biểu đồ)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(onClick = onStatsClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_stats),
                        contentDescription = "Thống kê",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                // Nút ở giữa (có thể là logo hoặc biểu tượng ứng dụng)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_water_drop),
                        contentDescription = "Biểu tượng nước",
                        tint = primaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Nút cài đặt
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(onClick = onSettingsClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Cài đặt",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            // Thanh tiến trình nước
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 25.dp)
                    .height(50.dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(25.dp)),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Nền thanh tiến trình
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        // Phần đã hoàn thành
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxSize()
                                .background(lightBlue)
                        )
                    }
                    
                    // Con trỏ tiến trình (tam giác)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .size(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(12.dp)) {
                                val trianglePath = Path().apply {
                                    val width = size.width
                                    val height = size.height
                                    
                                    moveTo(width / 2, 0f)  // Đỉnh trên
                                    lineTo(0f, height)     // Góc dưới trái
                                    lineTo(width, height)  // Góc dưới phải
                                    close()
                                }
                                drawPath(
                                    path = trianglePath,
                                    color = orangeIndicator
                                )
                            }
                        }
                    }
                    
                    // Văn bản tiến trình
                    Text(
                        text = "$currentWaterIntake/${waterGoal}ml",
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            }
            
            // Thanh "Hôm nay" ở dưới
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                

                Spacer(modifier = Modifier.width(98.dp))
                
                // Nút chọn ngày hiện tại
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(transparentWhite)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .clickable { showDatePicker = true }
                ) {
                    // Hiển thị "Hôm nay" nếu là ngày hiện tại, ngược lại hiển thị ngày được chọn
                    val displayText = if (selectedDate.isEqual(LocalDate.now())) {
                        "Hôm nay"
                    } else {
                        selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    }
                    
                    Text(
                        text = displayText,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Chọn ngày",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Khoảng trống giữa chọn ngày và phía bên phải
                Spacer(modifier = Modifier.width(98.dp))
            }
            
            // Hiển thị danh sách các lần uống nước
            if (filteredWaterEntries.isNotEmpty()) {
                // Khoảng trống để đẩy nội dung xuống
                Spacer(modifier = Modifier.height(16.dp))
                
                // Hiển thị danh sách các lần uống nước dạng lưới ngang (4 vật/hàng)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredWaterEntries) { entry ->
                        val formattedTime = entry.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm"))
                        
                        WaterIntakeItem(
                            entry = entry,
                            formattedTime = formattedTime,
                            onClick = { onWaterItemClick(entry) }
                        )
                    }
                }
            } else {
                // Thông báo ở giữa màn hình
                // Hiển thị thông báo khác nhau cho ngày hiện tại và ngày trong quá khứ
                if (selectedDate.isEqual(LocalDate.now())) {
                    Text(
                        text = "Sau khi uống một cốc nước\nnhấp vào nút \"+\" để ghi lại",
                        textAlign = TextAlign.Center,
                        color = Color(0xFF57CAFF),
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 32.dp)
                    )
                } else {
                    Text(
                        text = "Không có dữ liệu uống nước\ncho ngày ${selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                        textAlign = TextAlign.Center,
                        color = Color(0xFF57CAFF),
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 32.dp)
                    )
                }
                
                // Khoảng trống để đẩy nút lên
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        
        // Nút thêm nước ở dưới cùng
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            FloatingActionButton(
                onClick = { onAddWaterIntake(0) },
                modifier = Modifier.size(70.dp),
                containerColor = lightYellow,
                contentColor = Color.DarkGray,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm nước",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        
        // Dialog xác nhận xóa
        if (showDeleteDialog) {
            DeleteConfirmationDialog(
                entry = selectedEntryForDelete,
                onConfirm = handleDeleteConfirmation,
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}

@Composable
fun WaterIntakeItem(
    entry: WaterIntakeEntry,
    formattedTime: String,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick) // Thêm clickable để xử lý sự kiện click
    ) {
        // Biểu tượng nước dựa vào lượng
        if (entry.amount == 300) {
            Icon(
                painter = painterResource(id = R.drawable.nuoc300ml),
                contentDescription = "Ly nước 300ml",
                tint = Color(0xFF57CAFF),
                modifier = Modifier.size(45.dp)
            )
            Text(
                text = "300 ml",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        } else if (entry.amount == 700) {
            Icon(
                painter = painterResource(id = R.drawable.nuoc700ml),
                contentDescription = "Chai nước 700ml",
                tint = Color(0xFF57CAFF),
                modifier = Modifier.size(45.dp)
            )
            Text(
                text = "700 ml",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        } else {
            // Đối với các lượng nước khác, vẫn sử dụng ly nước
            Icon(
                painter = painterResource(id = R.drawable.nuoc300ml),
                contentDescription = "Lượng nước tùy chỉnh",
                tint = Color(0xFF57CAFF),
                modifier = Modifier.size(45.dp)
            )
            Text(
                text = "${entry.amount} ml",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        
        // Hiển thị thời gian
        Text(
            text = formattedTime,
            color = Color.Gray,
            fontSize = 10.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    entry: WaterIntakeEntry?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    entry?.let {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Xóa lượng nước đã uống?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00B2FF)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Bạn có chắc muốn xóa ${entry.amount}ml nước đã uống lúc ${entry.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm"))}?",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Nút Hủy
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text("Hủy", color = Color.Black)
                        }
                        
                        // Nút Xóa
                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF5252)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text("Xóa", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HydroMateTheme {
        val sampleUserWaterGoal = UserWaterGoal(
            gender = "Nam",
            weight = 70,
            wakeUpTime = "06:00",
            sleepTime = "22:00",
            dailyWaterGoal = 2310
        )
        HomeScreen(userWaterGoal = sampleUserWaterGoal, waterIntakeEntries = listOf(), onAddWaterIntake = {}, onDeleteWaterIntake = {})
    }
} 