package com.example.hydromate.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hydromate.R
import com.example.hydromate.ui.theme.HydroMateTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    splashDuration: Long = 2000 // Thời gian splash mặc định là 2 giây
) {
    // Hiệu ứng scale cho logo
    val scale = remember { Animatable(0.0f) }
    
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 500)
        )
        delay(splashDuration - 500) // Trừ đi thời gian animation để tổng thời gian đúng bằng splashDuration
        onSplashFinished()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00B2FF)), // Màu xanh chủ đạo của ứng dụng
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo ứng dụng với hiệu ứng scale
            Image(
                painter = painterResource(id = R.drawable.ic_water_drop),
                contentDescription = "HydroMate Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value),
                contentScale = ContentScale.Fit,
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Tên ứng dụng
            Text(
                text = "HydroMate",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.scale(scale.value)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Slogan
            Text(
                text = "Người bạn đồng hành cho sức khỏe của bạn",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.scale(scale.value)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    HydroMateTheme {
        SplashScreen(onSplashFinished = {})
    }
} 