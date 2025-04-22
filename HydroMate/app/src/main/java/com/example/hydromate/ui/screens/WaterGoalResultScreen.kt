package com.example.hydromate.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hydromate.R
import com.example.hydromate.model.UserWaterGoal
import com.example.hydromate.ui.theme.HydroMateTheme

@Composable
fun WaterGoalResultScreen(
    userWaterGoal: UserWaterGoal,
    onFinishClick: () -> Unit
) {
    // Tính toán mục tiêu nước dựa trên thông tin người dùng
    val waterGoal = userWaterGoal.dailyWaterGoal
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.5f))
            
            // Hiển thị hình ảnh cốc nước
            Image(
                painter = painterResource(id = R.drawable.icontrangmuctieu),
                contentDescription = "Cốc nước",
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Tiêu đề
            Text(
                text = "Mục tiêu uống nước hằng ngày",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Hiển thị số ml
            Text(
                text = "$waterGoal ml",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF46C0E9),
                textAlign = TextAlign.Center
            )

            
            Spacer(modifier = Modifier.weight(1f))
            
            // Nút hoàn thành
            Button(
                onClick = onFinishClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(30.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF46C0E9)
                )
            ) {
                Text(
                    text = "BẮT ĐẦU NGAY",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WaterGoalResultScreenPreview() {
    HydroMateTheme {
        val sampleUserWaterGoal = UserWaterGoal(
            gender = "Nam",
            weight = 70,
            wakeUpTime = "06:00",
            sleepTime = "22:00",
            dailyWaterGoal = 2310
        )
        WaterGoalResultScreen(
            userWaterGoal = sampleUserWaterGoal,
            onFinishClick = {}
        )
    }
} 