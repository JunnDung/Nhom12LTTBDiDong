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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    val primaryBlue = Color(0xFF00B2FF)
    val lightYellow = Color(0xFFFFE600)
    
    var selectedAmount by remember { mutableIntStateOf(300) }
    var customAmount by remember { mutableIntStateOf(500) }
    var showCustomInput by remember { mutableStateOf(false) }
    
    val minWater = 50
    val maxWater = 1000
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Thêm nước uống",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
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
                        color = primaryBlue
                    )
                }
                
                if (showCustomInput) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                showCustomInput = false
                                selectedAmount = 700
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .border(
                                    width = 2.dp,
                                    color = if (!showCustomInput && selectedAmount == 700) primaryBlue else Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.nuoc700ml),
                                contentDescription = "700ml",
                                tint = if (!showCustomInput && selectedAmount == 700) primaryBlue else Color.LightGray,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "700ml",
                            color = if (!showCustomInput && selectedAmount == 700) primaryBlue else Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                showCustomInput = false
                                selectedAmount = 300
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .border(
                                    width = 2.dp,
                                    color = if (!showCustomInput && selectedAmount == 300) primaryBlue else Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.nuoc300ml),
                                contentDescription = "300ml",
                                tint = if (!showCustomInput && selectedAmount == 300) primaryBlue else Color.LightGray,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "300ml",
                            color = if (!showCustomInput && selectedAmount == 300) primaryBlue else Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                
                Button(
                    onClick = {
                        val amount = if (showCustomInput) customAmount else selectedAmount
                        val waterEntry = WaterIntakeEntry.create(amount)
                        onAddWater(waterEntry)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue
                    ),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = "Thêm lượng nước",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
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