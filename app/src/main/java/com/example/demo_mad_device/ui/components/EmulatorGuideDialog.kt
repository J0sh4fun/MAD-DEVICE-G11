package com.example.demo_mad_device.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.demo_mad_device.ui.theme.MedicalCardBorder
import com.example.demo_mad_device.ui.theme.MedicalCyanPrimary
import com.example.demo_mad_device.ui.theme.MedicalEmeraldSecondary
import com.example.demo_mad_device.ui.theme.MedicalTextPrimary
import com.example.demo_mad_device.ui.theme.MedicalTextSecondary

@Composable
fun EmulatorGuideDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MedicalCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DeveloperMode,
                        contentDescription = null,
                        tint = MedicalCyanPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Emulator Demo Guide",
                            color = MedicalTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "MAD-DEVICE Hardware Simulation",
                            color = MedicalEmeraldSecondary,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MedicalTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                GuideItem(
                    icon = Icons.Default.Dashboard,
                    title = "1. Device Capabilities & System Info",
                    instructions = "Reads real android.os.Build metadata (Model, Manufacturer, ABI) and enumerates physical/virtual sensors via SensorManager."
                )

                GuideItem(
                    icon = Icons.Default.CameraAlt,
                    title = "2. CameraX Telehealth Imaging",
                    instructions = "Uses CameraX PreviewView. Click shutter for snapshot buffer. In Emulator settings (Extended Controls '...' -> Camera), set Front/Back camera to 'VirtualScene' or 'Webcam' to stream live feed."
                )

                GuideItem(
                    icon = Icons.Default.LocationOn,
                    title = "3. GPS & Emergency Dispatch",
                    instructions = "Connects to FusedLocationProviderClient. Open Extended Controls ('...') -> Location -> Single points or Routes, click 'Set Location' or start playback to stream live GPS coordinates and calculate distance to emergency center."
                )

                GuideItem(
                    icon = Icons.Default.Sensors,
                    title = "4. Motion Sensors & Fall Detection",
                    instructions = "Monitors TYPE_ACCELEROMETER and TYPE_GYROSCOPE. Test via 'Simulate Fall Impact' button in-app (triggers >25 m/s² alert) OR open Extended Controls ('...') -> Virtual Sensors -> rotate/move device sliders."
                )

                GuideItem(
                    icon = Icons.Default.BatteryChargingFull,
                    title = "5. Battery & Power Diagnostics",
                    instructions = "Listens to Intent.ACTION_BATTERY_CHANGED. Open Extended Controls ('...') -> Battery, adjust Battery Level (<15%), Charger Type (AC/USB/None), or use the in-app 'Simulate Critical Battery' toggle."
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedicalCyanPrimary,
                        contentColor = Color(0xFF0F172A)
                    )
                ) {
                    Text("Got It! Start Demo", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun GuideItem(
    icon: ImageVector,
    title: String,
    instructions: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A)
        ),
        border = BorderStroke(1.dp, MedicalCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MedicalCyanPrimary,
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    color = MedicalTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = instructions,
                    color = MedicalTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
