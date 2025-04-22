package com.example.hydromate.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.delay
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WakeUpTimeScreen(
    onNextClick: (String) -> Unit,
    onBackClick: () -> Unit = {}
) {
    // Giá trị mặc định
    var selectedHour by remember { mutableIntStateOf(7) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var isDragging by remember { mutableStateOf(false) }
    var lastDragTimestamp by remember { mutableStateOf(0L) }
    
    // Format giờ thức dậy để trả về
    val formattedTime by remember {
        derivedStateOf {
            String.format("%02d:%02d", selectedHour, selectedMinute)
        }
    }
    
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
        
        // Biểu tượng giọt nước ở bên trái
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.CenterStart)
                .offset(x = (-30).dp, y = (50).dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.icontrangthucday),
                contentDescription = "Hình minh họa thức dậy",
                modifier = Modifier.size(180.dp),
                contentScale = ContentScale.Fit
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(70.dp))
            
            // Tiêu đề
            Text(
                text = "Bạn thức dậy lúc mấy giờ",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF46C0E9),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mô tả
            Text(
                text = "Chúng tôi sẽ nhắc bạn uống nước sau khi thức dậy.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Số phụ phía trên với màu nhạt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = String.format("%02d", if (selectedHour > 0) selectedHour - 1 else 23),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.LightGray.copy(alpha = 0.6f),
                    modifier = Modifier.padding(end = 15.dp)
                )
                
                Text(
                    text = String.format("%02d", if (selectedMinute > 0) selectedMinute - 1 else 59),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.LightGray.copy(alpha = 0.6f)
                )
            }
            
            // Divider trên số giờ phút
            Divider(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp),
                color = Color.LightGray.copy(alpha = 0.5f),
                thickness = 1.dp
            )
            
            // Khu vực vuốt
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            // Vuốt ở phần giữa, thay đổi phút
                            if (abs(delta) > 3.5) { // Tăng ngưỡng để giảm độ nhạy
                                val currentTime = System.currentTimeMillis()
                                if ((currentTime - lastDragTimestamp) > 80L) { // Thêm debounce để giảm tốc độ
                                    lastDragTimestamp = currentTime
                                    
                                    if (delta < 0) {
                                        // Vuốt lên, tăng phút
                                        selectedMinute = if (selectedMinute < 59) selectedMinute + 1 else 0
                                    } else {
                                        // Vuốt xuống, giảm phút
                                        selectedMinute = if (selectedMinute > 0) selectedMinute - 1 else 59
                                    }
                                }
                            }
                        }
                    )
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val (x, y) = dragAmount
                            
                            // Xác định phần nào của màn hình đang được vuốt
                            val screenWidth = size.width
                            val touchX = change.position.x
                            
                            if (abs(y) > 3.5) { // Tăng ngưỡng để giảm độ nhạy
                                val currentTime = System.currentTimeMillis()
                                if ((currentTime - lastDragTimestamp) > 80L) { // Thêm debounce để giảm tốc độ
                                    lastDragTimestamp = currentTime
                                    
                                    if (touchX < screenWidth * 0.4) {
                                        // Vuốt bên trái, thay đổi giờ
                                        if (y < 0) {
                                            // Vuốt lên, tăng giờ
                                            selectedHour = if (selectedHour < 23) selectedHour + 1 else 0
                                        } else {
                                            // Vuốt xuống, giảm giờ
                                            selectedHour = if (selectedHour > 0) selectedHour - 1 else 23
                                        }
                                    } else if (touchX > screenWidth * 0.6) {
                                        // Vuốt bên phải, thay đổi phút
                                        if (y < 0) {
                                            // Vuốt lên, tăng phút
                                            selectedMinute = if (selectedMinute < 59) selectedMinute + 1 else 0
                                        } else {
                                            // Vuốt xuống, giảm phút
                                            selectedMinute = if (selectedMinute > 0) selectedMinute - 1 else 59
                                        }
                                    } else {
                                        // Vuốt ở giữa, thay đổi cả hai (tùy thuộc vào hướng ngang)
                                        if (abs(x) > abs(y)) {
                                            // Vuốt ngang nhiều hơn, ưu tiên phút
                                            if (y < 0) {
                                                // Vuốt lên, tăng phút
                                                selectedMinute = if (selectedMinute < 59) selectedMinute + 1 else 0
                                            } else {
                                                // Vuốt xuống, giảm phút
                                                selectedMinute = if (selectedMinute > 0) selectedMinute - 1 else 59
                                            }
                                        } else {
                                            // Vuốt dọc nhiều hơn, ưu tiên giờ
                                            if (y < 0) {
                                                // Vuốt lên, tăng giờ
                                                selectedHour = if (selectedHour < 23) selectedHour + 1 else 0
                                            } else {
                                                // Vuốt xuống, giảm giờ
                                                selectedHour = if (selectedHour > 0) selectedHour - 1 else 23
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
            ) {
                // Giờ chính với màu xanh
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = String.format("%02d", selectedHour),
                        fontSize = 70.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF46C0E9)
                    )
                    
                    Text(
                        text = ":",
                        fontSize = 70.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF46C0E9),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    
                    Text(
                        text = String.format("%02d", selectedMinute),
                        fontSize = 70.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF46C0E9)
                    )
                }
            }
            
            // Divider dưới số giờ phút
            Divider(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp),
                color = Color.LightGray.copy(alpha = 0.5f),
                thickness = 1.dp
            )
            
            // Số phụ phía dưới với màu nhạt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = String.format("%02d", if (selectedHour < 23) selectedHour + 1 else 0),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.LightGray.copy(alpha = 0.6f),
                    modifier = Modifier.padding(end = 15.dp)
                )
                
                Text(
                    text = String.format("%02d", if (selectedMinute < 59) selectedMinute + 1 else 0),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.LightGray.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Nút tiếp tục
            Button(
                onClick = { onNextClick(formattedTime) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(30.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF46C0E9)
                )
            ) {
                Text(
                    text = "TIẾP TỤC",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WakeUpTimeScreenPreview() {
    HydroMateTheme {
        WakeUpTimeScreen(onNextClick = {})
    }
} 