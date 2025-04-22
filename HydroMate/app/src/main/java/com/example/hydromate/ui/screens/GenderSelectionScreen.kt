package com.example.hydromate.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

@Composable
fun GenderSelectionScreen(
    onNextClick: (String) -> Unit,
    onBackClick: () -> Unit = {}
) {
    var selectedGender by remember { mutableStateOf("") }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Nút Back ở góc trái
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
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(70.dp))
            
            // Tiêu đề
            Text(
                text = "Chọn giới tính của bạn",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF46C0E9), // Màu xanh dương nhạt
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mô tả
            Text(
                text = "Chúng tôi sẽ đưa ra lời khuyên phù hợp với bạn",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.weight(0.7f))
            
            // Lựa chọn giới tính
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
            ) {
                // Lựa chọn Nam
                GenderOptionCircle(
                    gender = "Nam",
                    isSelected = selectedGender == "Nam",
                    imageRes = R.drawable.gioitinhnam,
                    onClick = { selectedGender = "Nam" }
                )
                
                Spacer(modifier = Modifier.width(40.dp))
                
                // Lựa chọn Nữ
                GenderOptionCircle(
                    gender = "Nữ",
                    isSelected = selectedGender == "Nữ",
                    imageRes = R.drawable.gioitinhnu,
                    onClick = { selectedGender = "Nữ" }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Nút tiếp tục
            Button(
                onClick = { onNextClick(selectedGender) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(30.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF46C0E9),
                    disabledContainerColor = Color(0xFFB8E2F8)
                ),
                enabled = selectedGender.isNotEmpty()
            ) {
                Text(
                    text = "TIẾP TỤC",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}

@Composable
fun GenderOptionCircle(
    gender: String,
    isSelected: Boolean,
    imageRes: Int,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(85.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) 
                        Color(0xFF1E88E5) 
                    else 
                        if (gender == "Nam") Color(0xFFE9F5FD) else Color(0xFFFBE8EA)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Giới tính $gender",
                modifier = Modifier.size(90.dp),
                contentScale = ContentScale.Fit
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = gender,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color(0xFF1E88E5) else Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GenderSelectionScreenPreview() {
    HydroMateTheme {
        GenderSelectionScreen(onNextClick = {})
    }
} 