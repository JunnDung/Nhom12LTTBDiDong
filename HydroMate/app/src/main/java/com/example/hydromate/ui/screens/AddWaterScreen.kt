package com.example.hydromate.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.hydromate.R
import com.example.hydromate.model.WaterIntakeEntry
import com.example.hydromate.ui.theme.HydroMateTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AddWaterScreen(
    onDismiss: () -> Unit = {},
    onAddWater: (WaterIntakeEntry) -> Unit = {}
) {
    // Màu chính của ứng dụng
    val primaryBlue = Color(0xFF00B2FF)
    val lightYellow = Color(0xFFFFE600)
    
    var selectedAmount by remember { mutableIntStateOf(300) }
    var customAmount by remember { mutableIntStateOf(500) }
    var showCustomInput by remember { mutableStateOf(false) }
    
    // Kích thước tối đa và tối thiểu cho slider
    val minWater = 50
    val maxWater = 1000
    
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tiêu đề
                Text(
                    text = "Thêm nước uống",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
                // Hiển thị lượng nước đã chọn
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${if (showCustomInput) customAmount else selectedAmount} ml",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // Phần tùy chỉnh nếu đã chọn
                if (showCustomInput) {
                    // Các nút tăng giảm và slider
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Nút giảm
                        Button(
                            onClick = { 
                                customAmount = (customAmount - 50).coerceAtLeast(minWater)
                            },
                            modifier = Modifier.size(50.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "-",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        // Slider cho phép tùy chỉnh nhanh
                        Slider(
                            value = customAmount.toFloat(),
                            onValueChange = { customAmount = it.toInt() },
                            valueRange = minWater.toFloat()..maxWater.toFloat(),
                            steps = ((maxWater - minWater) / 50) - 1,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = primaryBlue,
                                activeTrackColor = primaryBlue,
                                inactiveTrackColor = Color.LightGray
                            )
                        )
                        
                        // Nút tăng
                        Button(
                            onClick = { 
                                customAmount = (customAmount + 50).coerceAtMost(maxWater)
                            },
                            modifier = Modifier.size(50.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tăng"
                            )
                        }
                    }
                    
                    // TextField để nhập trực tiếp
                    OutlinedTextField(
                        value = customAmount.toString(),
                        onValueChange = { 
                            val newValue = it.filter { char -> char.isDigit() }
                            if (newValue.isNotEmpty()) {
                                val amount = newValue.toInt().coerceIn(minWater, maxWater)
                                customAmount = amount
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryBlue,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = primaryBlue,
                            unfocusedLabelColor = Color.Gray
                        ),
                        label = { Text("Lượng nước (ml)") },
                        suffix = { Text("ml") }
                    )
                }
                
                // Các tùy chọn lượng nước
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tùy chỉnh
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                showCustomInput = true
                                selectedAmount = customAmount
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .border(
                                    width = 2.dp,
                                    color = if (showCustomInput) primaryBlue else Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.nuoc300ml),
                                contentDescription = "Tùy chỉnh",
                                tint = if (showCustomInput) primaryBlue else Color.LightGray,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tùy chỉnh",
                            color = if (showCustomInput) primaryBlue else Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    
                    // 700ml
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                selectedAmount = 700
                                showCustomInput = false
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .border(
                                    width = 2.dp,
                                    color = if (selectedAmount == 700 && !showCustomInput) primaryBlue else Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.nuoc700ml),
                                contentDescription = "Chai nước 700ml",
                                tint = if (selectedAmount == 700 && !showCustomInput) primaryBlue else Color.LightGray,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "700ml",
                            color = if (selectedAmount == 700 && !showCustomInput) primaryBlue else Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    
                    // 300ml
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                selectedAmount = 300
                                showCustomInput = false
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .border(
                                    width = 2.dp,
                                    color = if (selectedAmount == 300 && !showCustomInput) primaryBlue else Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.nuoc300ml),
                                contentDescription = "Ly nước 300ml",
                                tint = if (selectedAmount == 300 && !showCustomInput) primaryBlue else Color.LightGray,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "300ml",
                            color = if (selectedAmount == 300 && !showCustomInput) primaryBlue else Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                
                // Row thông báo và nút thêm
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nút thêm nước
                    FloatingActionButton(
                        onClick = {
                            val amount = if (showCustomInput) customAmount else selectedAmount
                            if (amount > 0) {
                                onAddWater(WaterIntakeEntry(amount = amount))
                                onDismiss()
                            }
                        },
                        modifier = Modifier.size(50.dp),
                        containerColor = lightYellow,
                        contentColor = Color.DarkGray,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Thêm nước",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddWaterScreenPreview() {
    HydroMateTheme {
        AddWaterScreen()
    }
} 