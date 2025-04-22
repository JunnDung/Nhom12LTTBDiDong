package com.example.hydromate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hydromate.model.UserWaterGoal
import com.example.hydromate.ui.screens.GenderSelectionScreen
import com.example.hydromate.ui.screens.HomeScreen
import com.example.hydromate.ui.screens.LoadingScreen
import com.example.hydromate.ui.screens.SettingsScreen
import com.example.hydromate.ui.screens.SleepTimeScreen
import com.example.hydromate.ui.screens.WakeUpTimeScreen
import com.example.hydromate.ui.screens.WaterGoalResultScreen
import com.example.hydromate.ui.screens.WeightInputScreen
import com.example.hydromate.ui.screens.WelcomeScreen

@Composable
fun AppNavigation(
    onSetupComplete: (UserWaterGoal) -> Unit
) {
    val navController = rememberNavController()

    // Lưu dữ liệu người dùng khi đi qua các màn hình
    var gender by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf(0) }
    var wakeUpTime by remember { mutableStateOf("06:00") }
    var sleepTime by remember { mutableStateOf("22:00") }
    var waterGoal by remember { mutableStateOf(0) }
    
    // Tạo đối tượng UserWaterGoal để truyền giữa các màn hình
    val userWaterGoal by remember(gender, weight, wakeUpTime, sleepTime, waterGoal) {
        mutableStateOf(
            UserWaterGoal(
                gender = gender,
                weight = weight,
                wakeUpTime = wakeUpTime,
                sleepTime = sleepTime,
                dailyWaterGoal = waterGoal
            )
        )
    }

    NavHost(
        navController = navController,
        startDestination = AppScreens.Welcome.route
    ) {
        composable(AppScreens.Welcome.route) {
            WelcomeScreen(
                onStartClick = {
                    navController.navigate(AppScreens.GenderSelection.route)
                }
            )
        }

        composable(AppScreens.GenderSelection.route) {
            GenderSelectionScreen(
                onNextClick = { selectedGender ->
                    gender = selectedGender
                    navController.navigate(AppScreens.WeightInput.route)
                }
            )
        }

        composable(AppScreens.WeightInput.route) {
            WeightInputScreen(
                onNextClick = { selectedWeight ->
                    weight = selectedWeight
                    navController.navigate(AppScreens.WakeUpTime.route)
                }
            )
        }

        composable(AppScreens.WakeUpTime.route) {
            WakeUpTimeScreen(
                onNextClick = { selectedWakeUpTime ->
                    wakeUpTime = selectedWakeUpTime
                    navController.navigate(AppScreens.SleepTime.route)
                }
            )
        }

        composable(AppScreens.SleepTime.route) {
            SleepTimeScreen(
                onNextClick = { selectedSleepTime ->
                    sleepTime = selectedSleepTime
                    navController.navigate(AppScreens.Loading.route)
                }
            )
        }
        
        composable(AppScreens.Loading.route) {
            LoadingScreen(
                onLoadingComplete = {
                    // Tính toán mục tiêu nước dựa trên giới tính và cân nặng
                    val baseAmount = when (gender) {
                        "Nam" -> weight * 35
                        "Nữ" -> weight * 31
                        else -> weight * 33
                    }
                    
                    // Làm tròn đến 100ml gần nhất
                    waterGoal = (baseAmount / 100) * 100
                    
                    navController.navigate(AppScreens.WaterGoalResult.route)
                }
            )
        }
        
        composable(AppScreens.WaterGoalResult.route) {
            WaterGoalResultScreen(
                userWaterGoal = userWaterGoal,
                onFinishClick = {
                    // Điều hướng trực tiếp đến màn hình Home thay vì WaterGoalScreen
                    navController.navigate(AppScreens.Home.route) {
                        // Xóa tất cả các màn hình trước đó khỏi back stack
                        popUpTo(AppScreens.Welcome.route) { inclusive = true }
                    }
                    
                    // Thông báo cho MainActivity rằng quá trình thiết lập đã hoàn tất
                    onSetupComplete(userWaterGoal)
                }
            )
        }
        
        composable(AppScreens.Home.route) {
            HomeScreen(
                userWaterGoal = userWaterGoal,
                onSettingsClick = {
                    // Điều hướng đến màn hình cài đặt
                    navController.navigate(AppScreens.Settings.route)
                },
                onStatsClick = {
                    // Có thể thêm điều hướng đến màn hình thống kê ở đây
                }
            )
        }
        
        composable(AppScreens.Settings.route) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                appVersion = "1.0"
            )
        }
    }
}

sealed class AppScreens(val route: String) {
    object Welcome : AppScreens("welcome")
    object GenderSelection : AppScreens("gender_selection")
    object WeightInput : AppScreens("weight_input") 
    object WakeUpTime : AppScreens("wake_up_time")
    object SleepTime : AppScreens("sleep_time")
    object Loading : AppScreens("loading")
    object WaterGoalResult : AppScreens("water_goal_result")
    object Home : AppScreens("home")
    object Settings : AppScreens("settings")
} 