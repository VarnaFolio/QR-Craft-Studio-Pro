package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screen.GenerateScreen
import com.example.ui.screen.HistoryScreen
import com.example.ui.screen.ProScreen
import com.example.ui.screen.ScannerScreen
import com.example.ui.screen.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.QRCodeViewModel

class MainActivity : ComponentActivity() {
  private val qrCodeViewModel: QRCodeViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        var showSplash by remember { mutableStateOf(true) }

        if (showSplash) {
          SplashScreen(
            onSplashFinished = { showSplash = false }
          )
        } else {
          val navController = rememberNavController()
          val context = LocalContext.current

          NavHost(navController = navController, startDestination = "generate") {
            composable("generate") {
              GenerateScreen(
                viewModel = qrCodeViewModel,
                onNavigateHistory = { navController.navigate("history") },
                onNavigateScanner = { navController.navigate("scanner") },
                onNavigatePro = { navController.navigate("pro") }
              )
            }
            composable("history") {
              HistoryScreen(
                viewModel = qrCodeViewModel,
                onNavigateCreate = { navController.navigate("generate") { popUpTo("generate") { inclusive = true } } },
                onNavigateScanner = { navController.navigate("scanner") },
                onNavigatePro = { navController.navigate("pro") }
              )
            }
            composable("scanner") {
              ScannerScreen(
                viewModel = qrCodeViewModel,
                onNavigateCreate = { navController.navigate("generate") { popUpTo("generate") { inclusive = true } } },
                onNavigateHistory = { navController.navigate("history") },
                onNavigatePro = { navController.navigate("pro") }
              )
            }
            composable("pro") {
              ProScreen(
                viewModel = qrCodeViewModel,
                onNavigateCreate = { navController.navigate("generate") { popUpTo("generate") { inclusive = true } } },
                onNavigateHistory = { navController.navigate("history") },
                onNavigateScanner = { navController.navigate("scanner") }
              )
            }
          }
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun GenerateScreenPreview() {
  MyApplicationTheme {
    // Preview without viewModel if needed
  }
}
