package com.example.hydromate.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hydromate.R
import com.example.hydromate.model.UserWaterGoal
import com.example.hydromate.ui.theme.HydroMateTheme

@Composable
fun HomeScreen(
    userWaterGoal: UserWaterGoal,
    onSettingsClick: () -> Unit = {},
    onStatsClick: () -> Unit = {},
    onAddWaterClick: () -> Unit = {}
) {
    val waterGoal = userWaterGoal.dailyWaterGoal
    var currentWaterIntake by remember { mutableIntStateOf(0) }
    val progress = if (waterGoal > 0) currentWaterIntake.toFloat() / waterGoal.toFloat() else 0f
    
    // Màu chính của ứng dụng
    val primaryBlue = Color(0xFF00B2FF)
    val lightYellow = Color(0xFFFFE600)
    val transparentWhite = Color(0x33FFFFFF)
    val orangeIndicator = Color(0xFFFF6D33)
    
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
                    Text(
                        text = "••",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
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
                                .background(primaryBlue)
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
                // Nút quay lại
                IconButton(
                    onClick = { /* Xử lý sự kiện quay lại */ },
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Quay lại",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Khoảng trống giữa nút quay lại và chọn ngày
                Spacer(modifier = Modifier.width(60.dp))
                
                // Nút chọn ngày hiện tại
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(transparentWhite)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Hôm nay",
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
            
            // Khoảng trống để đẩy nội dung xuống
            Spacer(modifier = Modifier.weight(1f))
            
            // Thông báo ở giữa màn hình
            Text(
                text = "Sau khi uống một cốc nước\nnhấp vào nút \"+\" để ghi lại",
                textAlign = TextAlign.Center,
                color = Color(0xFF57CAFF),
                fontSize = 18.sp,
                lineHeight = 28.sp,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            // Khoảng trống để đẩy nút lên
            Spacer(modifier = Modifier.weight(1f))
        }
        
        // Nút thêm nước ở dưới cùng
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            FloatingActionButton(
                onClick = {
                    // Thêm 200ml mỗi lần nhấn
                    currentWaterIntake = (currentWaterIntake + 200).coerceAtMost(waterGoal)
                    onAddWaterClick()
                },
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
        HomeScreen(userWaterGoal = sampleUserWaterGoal)
    }
} 