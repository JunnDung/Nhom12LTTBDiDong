package com.example.hydromate.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hydromate.R
import com.example.hydromate.ui.theme.HydroMateTheme
import com.example.hydromate.ui.theme.PrimaryBlue
import kotlin.math.abs

@Composable
fun WeightInputScreen(
    onNextClick: (Int) -> Unit,
    onBackClick: () -> Unit = {}
) {
    var weight by remember { mutableIntStateOf(70) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Nút quay lại
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Quay lại",
                tint = Color.LightGray
            )
        }
        
        // Biểu tượng cân nặng ở giữa bên trái
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterStart)
                .offset(x = (-40).dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.icontrangcannang),
                contentDescription = "Biểu tượng cân nặng",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // Tiêu đề
            Text(
                text = "Bạn nặng bao nhiêu",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mô tả
            Text(
                text = "Chúng tôi cần thông tin cân nặng của bạn để xây dựng kế hoạch cung cấp nước tốt hơn.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            
            Spacer(modifier = Modifier.weight(0.8f))
            
            // Khu vực hiển thị cân nặng có thể vuốt
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            // Nếu vuốt lên (delta âm), tăng cân nặng
                            // Nếu vuốt xuống (delta dương), giảm cân nặng
                            if (abs(delta) > 5) { // Thêm ngưỡng để tránh thay đổi quá nhạy
                                if (delta < 0 && weight < 150) {
                                    weight++
                                } else if (delta > 0 && weight > 30) {
                                    weight--
                                }
                            }
                        }
                    )
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val (_, y) = dragAmount
                            // Nếu vuốt lên (y âm), tăng cân nặng
                            // Nếu vuốt xuống (y dương), giảm cân nặng
                            if (abs(y) > 5) { // Thêm ngưỡng để tránh thay đổi quá nhạy
                                if (y < 0 && weight < 150) {
                                    weight++
                                } else if (y > 0 && weight > 30) {
                                    weight--
                                }
                            }
                        }
                    }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp)
                ) {
                    // Số cân nặng trước đó với màu xám nhạt
                    Text(
                        text = "${weight - 1}",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.LightGray.copy(alpha = 0.6f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Divider phía trên
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        color = Color.LightGray.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )
                    
                    // Cân nặng hiện tại với màu xanh và size lớn
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "$weight",
                            fontSize = 70.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        
                        Spacer(modifier = Modifier.width(10.dp))
                        
                        Text(
                            text = "kg",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                    
                    // Divider phía dưới
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        color = Color.LightGray.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Số cân nặng tiếp theo với màu xám nhạt
                    Text(
                        text = "${weight + 1}",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.LightGray.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Nút tiếp tục với gradient màu
            Button(
                onClick = { onNextClick(weight) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(30.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                )
            ) {
                Text(
                    text = "TIẾP TỤC",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeightInputScreenPreview() {
    HydroMateTheme {
        WeightInputScreen(onNextClick = {})
    }
} 