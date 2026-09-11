package com.example.demo_mad_device.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.demo_mad_device.ui.components.SectionHeader
import com.example.demo_mad_device.ui.components.StatusBadge
import com.example.demo_mad_device.ui.theme.MedicalCardBorder
import com.example.demo_mad_device.ui.theme.MedicalCyanPrimary
import com.example.demo_mad_device.ui.theme.MedicalEmeraldSecondary
import com.example.demo_mad_device.ui.theme.MedicalTextMuted
import com.example.demo_mad_device.ui.theme.MedicalTextPrimary
import com.example.demo_mad_device.ui.theme.MedicalTextSecondary

data class AppPermissionItem(
    val permission: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun PermissionsScreen() {
    val context = LocalContext.current

    val permissionsList = remember {
        listOf(
            AppPermissionItem(
                permission = Manifest.permission.CAMERA,
                title = "Camera Access",
                description = "Wound Telehealth & Dermatology documentation image capture via CameraX",
                icon = Icons.Default.CameraAlt
            ),
            AppPermissionItem(
                permission = Manifest.permission.ACCESS_FINE_LOCATION,
                title = "Fine Location (GPS)",
                description = "Emergency patient dispatch precise coordinates & hospital routing",
                icon = Icons.Default.LocationOn
            ),
            AppPermissionItem(
                permission = Manifest.permission.ACCESS_COARSE_LOCATION,
                title = "Coarse Location",
                description = "Cell-tower & Wi-Fi emergency dispatch location fallback",
                icon = Icons.Default.Map
            ),
            AppPermissionItem(
                permission = Manifest.permission.BODY_SENSORS,
                title = "Body Sensors",
                description = "Optical heart rate & continuous vital monitor hardware sensors",
                icon = Icons.Default.Favorite
            ),
            AppPermissionItem(
                permission = Manifest.permission.ACTIVITY_RECOGNITION,
                title = "Physical Activity",
                description = "Elderly fall detection motion filter & step telemetry",
                icon = Icons.Default.Sensors
            )
        )
    }

    val permissionStates = remember { mutableStateMapOf<String, Boolean>() }

    fun refreshPermissions() {
        permissionsList.forEach { item ->
            permissionStates[item.permission] = ContextCompat.checkSelfPermission(
                context,
                item.permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    LaunchedEffect(Unit) {
        refreshPermissions()
    }

    val singleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { _ ->
            refreshPermissions()
        }
    )

    val multipleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { _ ->
            refreshPermissions()
        }
    )

    val totalGranted = permissionStates.values.count { it }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        SectionHeader(
            title = "Centralized Permissions Center",
            subtitle = "Real-time Security & Medical Access Governance",
            icon = Icons.Default.Security
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2942)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MedicalCyanPrimary.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SECURITY AUDIT SCORE",
                            color = MedicalTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$totalGranted / ${permissionsList.size} Granted",
                            color = MedicalTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    StatusBadge(
                        text = if (totalGranted == permissionsList.size) "FULL ACCESS" else "PARTIAL ACCESS",
                        isSuccess = totalGranted == permissionsList.size
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            multipleLauncher.launch(
                                permissionsList.map { it.permission }.toTypedArray()
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MedicalCyanPrimary,
                            contentColor = Color(0xFF0F172A)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Grant All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        },
                        border = BorderStroke(1.dp, MedicalCardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalTextPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("App Info Settings", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Individual Permission Cards
        permissionsList.forEach { item ->
            val isGranted = permissionStates[item.permission] == true

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(
                    1.dp,
                    if (isGranted) MedicalEmeraldSecondary.copy(alpha = 0.4f) else MedicalCardBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = if (isGranted) MedicalEmeraldSecondary else MedicalCyanPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.title,
                                color = MedicalTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(
                                text = if (isGranted) "GRANTED" else "DENIED",
                                isSuccess = isGranted
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.description,
                            color = MedicalTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        if (!isGranted) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    singleLauncher.launch(item.permission)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MedicalCyanPrimary,
                                    contentColor = Color(0xFF0F172A)
                                ),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Request", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
