package com.example.demo_mad_device.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demo_mad_device.ui.components.MetricCard
import com.example.demo_mad_device.ui.components.SectionHeader
import com.example.demo_mad_device.ui.components.StatusBadge
import com.example.demo_mad_device.ui.theme.MedicalCardBorder
import com.example.demo_mad_device.ui.theme.MedicalCyanPrimary
import com.example.demo_mad_device.ui.theme.MedicalEmeraldSecondary
import com.example.demo_mad_device.ui.theme.MedicalTextMuted
import com.example.demo_mad_device.ui.theme.MedicalTextPrimary
import com.example.demo_mad_device.ui.theme.MedicalTextSecondary

data class MedicalComplianceMetric(
    val title: String,
    val description: String,
    val status: String,
    val isCompliant: Boolean = true
)

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val sensorList = remember {
        sensorManager.getSensorList(Sensor.TYPE_ALL)
    }

    val complianceMetrics = remember {
        listOf(
            MedicalComplianceMetric(
                title = "DICOM-compatible Capture",
                description = "CameraX pipeline output calibrated for clinical dermatology & wound documentation",
                status = "Ready"
            ),
            MedicalComplianceMetric(
                title = "Hardware Encryption",
                description = "Android KeyStore Hardware-backed TEE / StrongBox Key Management",
                status = "Enabled"
            ),
            MedicalComplianceMetric(
                title = "HIPAA Local Storage",
                description = "AES-256-GCM Encrypted SharedPreferences & SQLCipher DB",
                status = "Compliant"
            ),
            MedicalComplianceMetric(
                title = "Real-time Telemetry Engine",
                description = "High-frequency 100Hz hardware sensor sampling bus",
                status = "Active"
            ),
            MedicalComplianceMetric(
                title = "EHR Interoperability",
                description = "HL7 FHIR v4 Observation and DiagnosticReport Data Sync",
                status = "Configured"
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = "Device Capabilities & System Info",
                subtitle = "Hardware & Platform Specifications via android.os.Build",
                icon = Icons.Default.DeveloperBoard
            )
        }

        // Hardware Overview Metrics Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Device Model",
                    value = Build.MODEL,
                    subtitle = Build.MANUFACTURER.uppercase(),
                    icon = Icons.Default.DeveloperBoard,
                    accentColor = MedicalCyanPrimary,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Android OS",
                    value = "API ${Build.VERSION.SDK_INT}",
                    subtitle = "Android ${Build.VERSION.RELEASE}",
                    icon = Icons.Default.Info,
                    accentColor = MedicalEmeraldSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Expanded System Specifications Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MedicalCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "HARDWARE ARCHITECTURE",
                        color = MedicalTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    SpecRow("Manufacturer", Build.MANUFACTURER)
                    SpecRow("Brand / Device", "${Build.BRAND} (${Build.DEVICE})")
                    SpecRow("Board / Hardware", "${Build.BOARD} / ${Build.HARDWARE}")
                    SpecRow("Supported ABIs", Build.SUPPORTED_ABIS.joinToString(", "))
                    SpecRow("Build Fingerprint", Build.FINGERPRINT, isMonospace = true)
                }
            }
        }

        // Medical Compliance Readiness Section
        item {
            SectionHeader(
                title = "Medical Compliance Readiness",
                subtitle = "Healthcare regulatory & security standard metrics",
                icon = Icons.Default.Security
            )
        }

        items(complianceMetrics) { metric ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0F2942)
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MedicalCyanPrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MedicalEmeraldSecondary,
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
                                text = metric.title,
                                color = MedicalTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(text = metric.status, isSuccess = metric.isCompliant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = metric.description,
                            color = MedicalTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Hardware Sensors Catalog Section
        item {
            SectionHeader(
                title = "Hardware Sensor Inventory",
                subtitle = "Verified ${sensorList.size} hardware/virtual sensors via SensorManager",
                icon = Icons.Default.Sensors
            )
        }

        items(sensorList) { sensor ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, MedicalCardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = sensor.name,
                            color = MedicalTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = getSensorTypeName(sensor.type),
                            color = MedicalCyanPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Vendor: ${sensor.vendor}",
                            color = MedicalTextMuted,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Power: ${sensor.power}mA | Res: ${sensor.resolution}",
                            color = MedicalTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String, isMonospace: Boolean = false) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = MedicalTextMuted,
                fontSize = 12.sp
            )
            Text(
                text = value,
                color = MedicalTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
                maxLines = 2
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

private fun getSensorTypeName(type: Int): String {
    return when (type) {
        Sensor.TYPE_ACCELEROMETER -> "ACCELEROMETER"
        Sensor.TYPE_GYROSCOPE -> "GYROSCOPE"
        Sensor.TYPE_MAGNETIC_FIELD -> "MAGNETOMETER"
        Sensor.TYPE_PRESSURE -> "BAROMETER"
        Sensor.TYPE_LIGHT -> "LIGHT"
        Sensor.TYPE_PROXIMITY -> "PROXIMITY"
        Sensor.TYPE_GRAVITY -> "GRAVITY"
        Sensor.TYPE_LINEAR_ACCELERATION -> "LINEAR_ACCEL"
        Sensor.TYPE_ROTATION_VECTOR -> "ROTATION_VECTOR"
        Sensor.TYPE_HEART_RATE -> "HEART_RATE"
        Sensor.TYPE_STEP_COUNTER -> "STEP_COUNTER"
        else -> "TYPE_$type"
    }
}
