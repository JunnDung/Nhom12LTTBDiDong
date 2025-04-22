package com.example.hydromate.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hydromate.R
import com.example.hydromate.model.UserWaterGoal
import com.example.hydromate.model.WaterIntakeEntry
import com.example.hydromate.model.WeightEntry
import com.example.hydromate.ui.theme.HydroMateTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatsScreen(
    userWaterGoal: UserWaterGoal,
    waterIntakeEntries: List<WaterIntakeEntry>,
    weightEntries: List<WeightEntry>,
    onAddWeight: (Int) -> Unit,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    // Màu chính của ứng dụng
    val primaryBlue = Color(0xFF00B2FF)
    
    // State cho việc hiển thị dữ liệu theo tuần, tháng, năm
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("TUẦN", "THÁNG", "NĂM")
    
    // State cho khoảng thời gian hiện tại
    var currentStartDate by remember { mutableStateOf(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))) }
    var currentEndDate by remember { mutableStateOf(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))) }
    
    // Dialog thêm cân nặng mới
    var showAddWeightDialog by remember { mutableStateOf(false) }
    var selectedWeight by remember { mutableIntStateOf(weightEntries.firstOrNull()?.weight ?: 70) }
    
    // Cập nhật khoảng thời gian dựa vào tab được chọn
    fun updateDateRange() {
        when (selectedTabIndex) {
            0 -> { // TUẦN
                currentStartDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                currentEndDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            }
            1 -> { // THÁNG
                currentStartDate = LocalDate.now().withDayOfMonth(1)
                currentEndDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
            }
            2 -> { // NĂM
                currentStartDate = LocalDate.now().withDayOfYear(1)
                currentEndDate = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear())
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header với nền xanh
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(primaryBlue)
        )
        
        // Nội dung chính
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút quay lại
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Biểu tượng thống kê ở giữa
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_stats),
                        contentDescription = "Thống kê",
                        tint = primaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
            }
            
            // Tabs cho việc chọn tuần/tháng/năm
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x3300B2FF)),
                indicator = {},
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            updateDateRange()
                        },
                        modifier = Modifier
                            .padding(6.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (selectedTabIndex == index) Color.White
                                else Color.Transparent
                            )
                    ) {
                        Text(
                            text = title,
                            color = if (selectedTabIndex == index) primaryBlue else Color.LightGray,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Khoảng thời gian hiện tại
            Text(
                text = getDateRangeText(currentStartDate, currentEndDate, selectedTabIndex),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Phần nội dung thống kê (chiếm phần lớn màn hình)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                // Biểu đồ lượng nước
                StatsCard(
                    title = "Lượng nước",
                    unit = "%",
                    data = getWaterPercentageData(waterIntakeEntries, userWaterGoal, selectedTabIndex),
                    currentValue = getTodayWaterPercentage(waterIntakeEntries, userWaterGoal),
                    color = primaryBlue
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Biểu đồ cân nặng
                StatsCard(
                    title = "Cân nặng",
                    unit = "kg",
                    data = getWeightData(weightEntries, selectedTabIndex),
                    currentValue = weightEntries.firstOrNull()?.weight ?: 0,
                    color = Color(0xFF4CAF50)
                )
                
                // Thông tin cân nặng hiện tại
                WeightSummary(weightEntries)
            }
        }
        
        // FAB để thêm cân nặng mới
        FloatingActionButton(
            onClick = { showAddWeightDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp),
            containerColor = Color(0xFF4CAF50)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Thêm cân nặng",
                tint = Color.White
            )
        }
        
        // Dialog thêm cân nặng mới
        if (showAddWeightDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { showAddWeightDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Cập nhật cân nặng",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Nút giảm
                            androidx.compose.material3.IconButton(
                                onClick = { if (selectedWeight > 1) selectedWeight-- }
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_minus),
                                    contentDescription = "Giảm",
                                    tint = Color.Gray
                                )
                            }
                            
                            // Hiển thị cân nặng
                            Text(
                                text = "$selectedWeight",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                            
                            // Nút tăng
                            androidx.compose.material3.IconButton(
                                onClick = { selectedWeight++ }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Tăng",
                                    tint = Color.Gray
                                )
                            }
                        }
                        
                        Text(
                            text = "kg",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Nút cập nhật
                        androidx.compose.material3.Button(
                            onClick = {
                                onAddWeight(selectedWeight)
                                showAddWeightDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text("Cập nhật cân nặng")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    unit: String,
    data: List<Pair<String, Float>>,
    currentValue: Int,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Tiêu đề và giá trị hiện tại
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (title == "Lượng nước") {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_water_drop),
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_weight),
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "$title $unit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.DarkGray
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "$currentValue",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = color
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Biểu đồ đơn giản
            val displayData = if (data.size > 31) {
                // Nếu có quá nhiều dữ liệu (ví dụ: tháng), chỉ hiển thị 7 mục
                val step = data.size / 7
                data.filterIndexed { index, _ -> index % step == 0 }.take(7)
            } else {
                data
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                displayData.forEach { (label, value) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        val barHeight = 80.dp * value
                        
                        // Thanh đồ thị
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height(barHeight)
                                .background(
                                    color = color,
                                    shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                                )
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Nhãn
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            // Đường cơ sở
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun WeightSummary(weightEntries: List<WeightEntry>) {
    // Sắp xếp các mục cân nặng theo thời gian (mới nhất đầu tiên)
    val sortedEntries = weightEntries.sortedByDescending { it.getDate() }
    
    // Lấy cân nặng hiện tại (mục mới nhất)
    val currentWeight = sortedEntries.firstOrNull()?.weight ?: 0
    
    // Chỉ hiển thị giá trị tối đa và tối thiểu khi có ít nhất 1 mục
    val maxWeight = if (sortedEntries.isNotEmpty()) {
        sortedEntries.maxOf { it.weight }
    } else 0
    
    val minWeight = if (sortedEntries.isNotEmpty()) {
        sortedEntries.minOf { it.weight }
    } else 0
    
    // Ngày đo cân nặng gần nhất
    val latestDate = sortedEntries.firstOrNull()?.getDate()
    val formattedDate = latestDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "--/--/----"
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Thông tin ngày cập nhật gần nhất
            if (sortedEntries.isNotEmpty()) {
                Text(
                    text = "Cập nhật: $formattedDate",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
                
                Spacer(modifier = Modifier.height(10.dp))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "HIỆN HÀNH:",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${currentWeight}kg",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
                
                Divider(
                    modifier = Modifier
                        .height(50.dp)
                        .width(1.dp),
                    color = Color.LightGray
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "NẶNG NHẤT:",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${maxWeight}kg",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
                
                Divider(
                    modifier = Modifier
                        .height(50.dp)
                        .width(1.dp),
                    color = Color.LightGray
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "NHẸ NHẤT:",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${minWeight}kg",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

// Hàm helper để lấy đoạn text hiển thị khoảng thời gian
@RequiresApi(Build.VERSION_CODES.O)
fun getDateRangeText(startDate: LocalDate, endDate: LocalDate, tabIndex: Int): String {
    val formatter = DateTimeFormatter.ofPattern("MM-dd")
    
    return when (tabIndex) {
        0 -> "${startDate.format(formatter)}~${endDate.format(formatter)}" // Tuần
        1 -> startDate.format(DateTimeFormatter.ofPattern("MM-yyyy")) // Tháng
        2 -> startDate.format(DateTimeFormatter.ofPattern("yyyy")) // Năm
        else -> ""
    }
}

// Hàm để tính phần trăm lượng nước đã uống hôm nay
@RequiresApi(Build.VERSION_CODES.O)
fun getTodayWaterPercentage(waterEntries: List<WaterIntakeEntry>, userWaterGoal: UserWaterGoal): Int {
    val today = LocalDate.now()
    val todayWaterIntake = waterEntries
        .filter { entry ->
            val entryDate = entry.getTimestamp().toLocalDate()
            entryDate.isEqual(today)
        }
        .sumOf { it.amount }
    
    return if (userWaterGoal.dailyWaterGoal > 0) {
        ((todayWaterIntake.toFloat() / userWaterGoal.dailyWaterGoal) * 100).toInt()
    } else {
        0
    }
}

// Hàm helper để tạo dữ liệu cho biểu đồ nước
@RequiresApi(Build.VERSION_CODES.O)
fun getWaterPercentageData(
    waterEntries: List<WaterIntakeEntry>,
    userWaterGoal: UserWaterGoal,
    tabIndex: Int
): List<Pair<String, Float>> {
    val today = LocalDate.now()
    val dailyGoal = userWaterGoal.dailyWaterGoal.toFloat()
    
    return when (tabIndex) {
        0 -> { // Tuần
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val daysOfWeek = listOf("Th2", "Th3", "Th4", "Th5", "Th6", "Th7", "CN")
            
            daysOfWeek.mapIndexed { index, day ->
                val currentDate = startOfWeek.plusDays(index.toLong())
                val dateWaterIntake = waterEntries
                    .filter { entry ->
                        val entryDate = entry.getTimestamp().toLocalDate()
                        entryDate.isEqual(currentDate)
                    }
                    .sumOf { it.amount }
                
                val percentage = if (dailyGoal > 0) {
                    (dateWaterIntake / dailyGoal).coerceIn(0f, 1f)
                } else {
                    0f
                }
                
                day to percentage
            }
        }
        1 -> { // Tháng
            val startOfMonth = today.withDayOfMonth(1)
            val weeks = (1..4).map { "T$it" }
            
            weeks.mapIndexed { index, week ->
                val weekStart = startOfMonth.plusWeeks(index.toLong())
                val weekEnd = if (index < 3) {
                    weekStart.plusWeeks(1).minusDays(1)
                } else {
                    // Tuần cuối có thể kéo dài đến hết tháng
                    startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth())
                }
                
                val weekWaterIntake = waterEntries
                    .filter { entry ->
                        val entryDate = entry.getTimestamp().toLocalDate()
                        entryDate >= weekStart && entryDate <= weekEnd
                    }
                    .sumOf { it.amount }
                
                // Tính mục tiêu nước cho khoảng thời gian một tuần
                val daysInWeek = weekEnd.toEpochDay() - weekStart.toEpochDay() + 1
                val weeklyGoal = dailyGoal * daysInWeek
                
                val percentage = if (weeklyGoal > 0) {
                    (weekWaterIntake / weeklyGoal).coerceIn(0f, 1f)
                } else {
                    0f
                }
                
                week to percentage
            }
        }
        else -> { // Năm
            val months = (1..12).map { "T$it" }
            
            months.mapIndexed { index, month ->
                val currentMonth = LocalDate.of(today.year, index + 1, 1)
                val monthWaterIntake = waterEntries
                    .filter { entry ->
                        val entryDate = entry.getTimestamp().toLocalDate()
                        entryDate.year == currentMonth.year && entryDate.monthValue == currentMonth.monthValue
                    }
                    .sumOf { it.amount }
                
                // Giả định rằng mục tiêu hàng tháng là mục tiêu hàng ngày * số ngày trong tháng
                val daysInMonth = currentMonth.lengthOfMonth()
                val monthlyGoal = dailyGoal * daysInMonth
                
                val percentage = if (monthlyGoal > 0) {
                    (monthWaterIntake / monthlyGoal).coerceIn(0f, 1f)
                } else {
                    0f
                }
                
                month to percentage
            }
        }
    }
}

// Hàm helper để tạo dữ liệu cho biểu đồ cân nặng
@RequiresApi(Build.VERSION_CODES.O)
fun getWeightData(
    weightEntries: List<WeightEntry>,
    tabIndex: Int
): List<Pair<String, Float>> {
    if (weightEntries.isEmpty()) {
        return when (tabIndex) {
            0 -> listOf("Th2", "Th3", "Th4", "Th5", "Th6", "Th7", "CN").map { it to 0f }
            1 -> (1..4).map { "T$it" to 0f }
            else -> (1..12).map { "T$it" to 0f }
        }
    }
    
    val today = LocalDate.now()
    val sortedEntries = weightEntries.sortedByDescending { it.getDate() }
    val maxWeight = weightEntries.maxOf { it.weight }.toFloat()
    val minWeight = weightEntries.minOf { it.weight }.toFloat() 
    val range = if (maxWeight > minWeight) maxWeight - minWeight else 10f
    
    return when (tabIndex) {
        0 -> { // Tuần
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val daysOfWeek = listOf("Th2", "Th3", "Th4", "Th5", "Th6", "Th7", "CN")
            
            daysOfWeek.mapIndexed { index, day ->
                val currentDate = startOfWeek.plusDays(index.toLong())
                
                // Tìm mục cân nặng gần nhất đến ngày này (có thể là trước đó)
                val weightForDay = weightEntries
                    .filter { it.getDate().isEqual(currentDate) || it.getDate().isBefore(currentDate) }
                    .maxByOrNull { it.getDate() }
                
                if (weightForDay != null) {
                    // Chuẩn hóa cân nặng để hiển thị trên biểu đồ
                    val normalizedWeight = if (range > 0) {
                        (weightForDay.weight - minWeight) / range
                    } else {
                        0.5f // Nếu min = max thì hiển thị ở giữa
                    }
                    day to normalizedWeight
                } else {
                    day to 0f
                }
            }
        }
        1 -> { // Tháng
            val startOfMonth = today.withDayOfMonth(1)
            val weeks = (1..4).map { "T$it" }
            
            weeks.mapIndexed { index, week ->
                val weekStart = startOfMonth.plusWeeks(index.toLong())
                val weekEnd = if (index < 3) {
                    weekStart.plusWeeks(1).minusDays(1)
                } else {
                    // Tuần cuối có thể kéo dài đến hết tháng
                    startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth())
                }
                
                // Tìm mục cân nặng trong tuần này
                val weightForWeek = weightEntries
                    .filter { 
                        val entryDate = it.getDate()
                        (entryDate.isEqual(weekStart) || entryDate.isAfter(weekStart)) && 
                        (entryDate.isEqual(weekEnd) || entryDate.isBefore(weekEnd))
                    }
                    .maxByOrNull { it.getDate() }
                
                // Nếu không có, tìm mục gần nhất trước đó
                val fallbackWeight = if (weightForWeek == null) {
                    weightEntries
                        .filter { it.getDate().isBefore(weekStart) }
                        .maxByOrNull { it.getDate() }
                } else null
                
                val weightEntry = weightForWeek ?: fallbackWeight
                
                if (weightEntry != null) {
                    val normalizedWeight = if (range > 0) {
                        (weightEntry.weight - minWeight) / range
                    } else {
                        0.5f
                    }
                    week to normalizedWeight
                } else {
                    week to 0f
                }
            }
        }
        else -> { // Năm
            val months = (1..12).map { "T$it" }
            
            months.mapIndexed { index, month ->
                val monthValue = index + 1
                val monthStart = LocalDate.of(today.year, monthValue, 1)
                val monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth())
                
                // Tìm mục cân nặng trong tháng này
                val weightForMonth = weightEntries
                    .filter { 
                        val entryDate = it.getDate()
                        entryDate.year == today.year && 
                        entryDate.monthValue == monthValue &&
                        (entryDate.isEqual(monthStart) || entryDate.isAfter(monthStart)) &&
                        (entryDate.isEqual(monthEnd) || entryDate.isBefore(monthEnd))
                    }
                    .maxByOrNull { it.getDate() }
                
                // Nếu không có, tìm mục gần nhất trước đó
                val fallbackWeight = if (weightForMonth == null && monthValue > 1) {
                    // Tìm các tháng trước đó
                    weightEntries
                        .filter { 
                            val entryDate = it.getDate()
                            entryDate.year == today.year && entryDate.monthValue < monthValue
                        }
                        .maxByOrNull { it.getDate() }
                } else null
                
                val weightEntry = weightForMonth ?: fallbackWeight
                
                if (weightEntry != null) {
                    val normalizedWeight = if (range > 0) {
                        (weightEntry.weight - minWeight) / range
                    } else {
                        0.5f
                    }
                    month to normalizedWeight
                } else {
                    month to 0f
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun StatsScreenPreview() {
    HydroMateTheme {
        val sampleUserWaterGoal = UserWaterGoal(
            gender = "Nam",
            weight = 70,
            wakeUpTime = "06:00",
            sleepTime = "22:00",
            dailyWaterGoal = 2310
        )
        StatsScreen(
            userWaterGoal = sampleUserWaterGoal,
            waterIntakeEntries = emptyList(),
            weightEntries = emptyList(),
            onAddWeight = {}
        )
    }
} 