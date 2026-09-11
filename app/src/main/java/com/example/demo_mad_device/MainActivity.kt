package com.example.demo_mad_device

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.demo_mad_device.ui.components.EmulatorGuideDialog
import com.example.demo_mad_device.ui.navigation.NavRoute
import com.example.demo_mad_device.ui.screens.BatteryScreen
import com.example.demo_mad_device.ui.screens.CameraScreen
import com.example.demo_mad_device.ui.screens.DashboardScreen
import com.example.demo_mad_device.ui.screens.LocationScreen
import com.example.demo_mad_device.ui.screens.MotionScreen
import com.example.demo_mad_device.ui.screens.PermissionsScreen
import com.example.demo_mad_device.ui.theme.HealthGuardTheme
import com.example.demo_mad_device.ui.theme.MedicalCardBorder
import com.example.demo_mad_device.ui.theme.MedicalCyanPrimary
import com.example.demo_mad_device.ui.theme.MedicalEmeraldSecondary
import com.example.demo_mad_device.ui.theme.MedicalTextPrimary
import com.example.demo_mad_device.ui.theme.MedicalTextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthGuardTheme {
                HealthGuardMainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthGuardMainApp() {
    val navController = rememberNavController()
    var showGuideDialog by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (showGuideDialog) {
        EmulatorGuideDialog(onDismiss = { showGuideDialog = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "HealthGuard Pro",
                            color = MedicalTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "MAD-DEVICE Healthcare Integration",
                            color = MedicalCyanPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = "Logo",
                            tint = MedicalEmeraldSecondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showGuideDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = "Emulator Demo Guide",
                            tint = MedicalCyanPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavRoute.items.forEach { item ->
                    val selected = currentDestination?.route == item.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (selected) MedicalCyanPrimary else MedicalTextSecondary
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                color = if (selected) MedicalCyanPrimary else MedicalTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF0F2942)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = NavRoute.Dashboard.route
            ) {
                composable(NavRoute.Dashboard.route) {
                    DashboardScreen()
                }
                composable(NavRoute.Camera.route) {
                    CameraScreen()
                }
                composable(NavRoute.Location.route) {
                    LocationScreen()
                }
                composable(NavRoute.Motion.route) {
                    MotionScreen()
                }
                composable(NavRoute.Battery.route) {
                    BatteryScreen()
                }
                composable(NavRoute.Permissions.route) {
                    PermissionsScreen()
                }
            }
        }
    }
}
