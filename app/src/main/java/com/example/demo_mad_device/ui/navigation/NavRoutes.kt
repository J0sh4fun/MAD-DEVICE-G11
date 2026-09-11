package com.example.demo_mad_device.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavRoute(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : NavRoute("dashboard", "System Info", Icons.Default.Dashboard)
    object Camera : NavRoute("camera", "Wound Cam", Icons.Default.CameraAlt)
    object Location : NavRoute("location", "GPS Dispatch", Icons.Default.LocationOn)
    object Motion : NavRoute("motion", "Fall Detection", Icons.Default.Sensors)
    object Battery : NavRoute("battery", "Power Monitor", Icons.Default.BatteryChargingFull)
    object Permissions : NavRoute("permissions", "Permissions", Icons.Default.Security)

    companion object {
        val items = listOf(Dashboard, Camera, Location, Motion, Battery, Permissions)
    }
}
