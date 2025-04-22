package com.example.hydromate.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hydromate.R
import com.example.hydromate.ui.theme.HydroMateTheme
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    onLoadingComplete: () -> Unit,
    loadingDuration: Long = 2000 // Thời gian loading mặc định là 2 giây
) {
    // Biến để kiểm soát trạng thái loading
    var isLoading by remember { mutableStateOf(true) }
    
    // Hiệu ứng để chuyển màn hình sau khoảng thời gian loading
    LaunchedEffect(key1 = true) {
        delay(loadingDuration)
        isLoading = false
        onLoadingComplete()
    }
    
    // Biến để xoay progress indicator
    val rotation = remember { Animatable(0f) }
    
    LaunchedEffect(key1 = true) {
        rotation.animateTo(
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1500,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hiển thị loading indicator với animation xoay
            CircularProgressIndicator(
                modifier = Modifier.size(80.dp),
                color = Color(0xFF46C0E9),
                strokeWidth = 6.dp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Văn bản thông báo
            Text(
                text = "Đang tạo kế hoạch cho bạn...",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    HydroMateTheme {
        LoadingScreen(onLoadingComplete = {})
    }
} 