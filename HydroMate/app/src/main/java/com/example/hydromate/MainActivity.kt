package com.example.hydromate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.hydromate.model.UserWaterGoal
import com.example.hydromate.ui.navigation.AppNavigation
import com.example.hydromate.ui.theme.HydroMateTheme

class MainActivity : ComponentActivity() {
    // Biến để lưu thông tin người dùng giữa các phiên
    private var userWaterGoal: UserWaterGoal? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            HydroMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        onSetupComplete = { completedUserWaterGoal ->
                            // Lưu thông tin người dùng
                            userWaterGoal = completedUserWaterGoal
                            
                            // Trong tương lai, lưu dữ liệu vào local storage hoặc database
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HydroMateTheme {
        Greeting("Android")
    }
}