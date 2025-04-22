package com.example.hydromate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hydromate.data.AppViewModel
import com.example.hydromate.model.UserWaterGoal
import com.example.hydromate.model.WaterIntakeEntry
import com.example.hydromate.ui.screens.AddWaterScreen
import com.example.hydromate.ui.screens.GenderSelectionScreen
import com.example.hydromate.ui.screens.HomeScreen
import com.example.hydromate.ui.screens.LoadingScreen
import com.example.hydromate.ui.screens.SettingsScreen
import com.example.hydromate.ui.screens.SleepTimeScreen
import com.example.hydromate.ui.screens.SplashScreen
import com.example.hydromate.ui.screens.StatsScreen
import com.example.hydromate.ui.screens.WakeUpTimeScreen
import com.example.hydromate.ui.screens.WaterGoalResultScreen
import com.example.hydromate.ui.screens.WeightInputScreen
import com.example.hydromate.ui.screens.WelcomeScreen

@Composable
fun AppNavigation(
    onSetupComplete: (UserWaterGoal) -> Unit,
    appViewModel: AppViewModel
) {
    val navController = rememberNavController()

    // Lấy userWaterGoal từ ViewModel
    val userWaterGoal by appViewModel.userWaterGoal.collectAsState()
    
    // Lưu dữ liệu người dùng khi đi qua các màn hình setup
    var gender by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf(0) }
    var wakeUpTime by remember { mutableStateOf("06:00") }
    var sleepTime by remember { mutableStateOf("22:00") }
    var waterGoal by remember { mutableStateOf(0) }
    
    // Tạo đối tượng UserWaterGoal tạm thời để truyền giữa các màn hình setup
    val tempUserWaterGoal by remember(gender, weight, wakeUpTime, sleepTime, waterGoal) {
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
        startDestination = AppScreens.Splash.route
    ) {
        composable(AppScreens.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    // Kiểm tra xem người dùng đã thiết lập chưa
                    if (userWaterGoal.dailyWaterGoal > 0) {
                        navController.navigate(AppScreens.Home.route) {
                            popUpTo(AppScreens.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(AppScreens.Welcome.route) {
                            popUpTo(AppScreens.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        
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
                userWaterGoal = tempUserWaterGoal,
                onFinishClick = {
                    // Điều hướng trực tiếp đến màn hình Home
                    navController.navigate(AppScreens.Home.route) {
                        // Xóa tất cả các màn hình trước đó khỏi back stack
                        popUpTo(AppScreens.Welcome.route) { inclusive = true }
                    }
                    
                    // Lưu thông tin người dùng mới vào ViewModel
                    onSetupComplete(tempUserWaterGoal)
                    
                    // Thêm mục cân nặng ban đầu
                    appViewModel.addWeightEntry(weight)
                }
            )
        }
        
        composable(AppScreens.Home.route) {
            // Lấy danh sách lần uống nước từ ViewModel
            val waterEntries by appViewModel.waterIntakeEntries.collectAsState()
            
            HomeScreen(
                userWaterGoal = userWaterGoal,
                waterIntakeEntries = waterEntries,
                onAddWaterIntake = { 
                    navController.navigate(AppScreens.AddWater.route)
                },
                onDeleteWaterIntake = { entryId ->
                    appViewModel.removeWaterIntakeEntry(entryId)
                },
                onSettingsClick = {
                    navController.navigate(AppScreens.Settings.route)
                },
                onStatsClick = {
                    navController.navigate(AppScreens.Stats.route)
                }
            )
        }
        
        composable(AppScreens.AddWater.route) {
            AddWaterScreen(
                onDismiss = {
                    navController.popBackStack()
                },
                onAddWater = { waterEntry ->
                    appViewModel.addWaterIntakeEntry(waterEntry.amount)
                    navController.popBackStack()
                }
            )
        }
        
        composable(AppScreens.Stats.route) {
            val waterEntries by appViewModel.waterIntakeEntries.collectAsState()
            val weightEntries by appViewModel.weightEntries.collectAsState()
            
            StatsScreen(
                userWaterGoal = userWaterGoal,
                waterIntakeEntries = waterEntries,
                weightEntries = weightEntries,
                onAddWeight = { weight ->
                    appViewModel.addWeightEntry(weight)
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate(AppScreens.Home.route) {
                        popUpTo(AppScreens.Home.route) { inclusive = true }
                    }
                },
                onSettingsClick = {
                    navController.navigate(AppScreens.Settings.route)
                }
            )
        }
        
        composable(AppScreens.Settings.route) {
            SettingsScreen(
                appViewModel = appViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate(AppScreens.Home.route) {
                        popUpTo(AppScreens.Home.route) { inclusive = true }
                    }
                },
                onStatsClick = {
                    navController.navigate(AppScreens.Stats.route) {
                        popUpTo(AppScreens.Settings.route)
                    }
                },
                appVersion = "1.0"
            )
        }
    }
}

sealed class AppScreens(val route: String) {
    object Splash : AppScreens("splash")
    object Welcome : AppScreens("welcome")
    object GenderSelection : AppScreens("gender_selection")
    object WeightInput : AppScreens("weight_input") 
    object WakeUpTime : AppScreens("wake_up_time")
    object SleepTime : AppScreens("sleep_time")
    object Loading : AppScreens("loading")
    object WaterGoalResult : AppScreens("water_goal_result")
    object Home : AppScreens("home")
    object Settings : AppScreens("settings")
    object Stats : AppScreens("stats")
    object AddWater : AppScreens("add_water")
} 