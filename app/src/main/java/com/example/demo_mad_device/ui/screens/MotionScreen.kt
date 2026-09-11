package com.example.demo_mad_device.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demo_mad_device.ui.components.MetricCard
import com.example.demo_mad_device.ui.components.SectionHeader
import com.example.demo_mad_device.ui.components.StatusBadge
import com.example.demo_mad_device.ui.theme.MedicalAlertRed
import com.example.demo_mad_device.ui.theme.MedicalCardBorder
import com.example.demo_mad_device.ui.theme.MedicalCyanPrimary
import com.example.demo_mad_device.ui.theme.MedicalEmeraldSecondary
import com.example.demo_mad_device.ui.theme.MedicalTextMuted
import com.example.demo_mad_device.ui.theme.MedicalTextPrimary
import com.example.demo_mad_device.ui.theme.MedicalTextSecondary
import com.example.demo_mad_device.ui.theme.MedicalWarningAmber
import java.util.Locale
import kotlin.math.sqrt

private const val FALL_THRESHOLD_M_S2 = 25.0f

@Composable
fun MotionScreen() {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    // Accelerometer Values
    var accelX by remember { mutableFloatStateOf(0.1f) }
    var accelY by remember { mutableFloatStateOf(9.8f) }
    var accelZ by remember { mutableFloatStateOf(0.2f) }

    // Gyroscope Values
    var gyroX by remember { mutableFloatStateOf(0.01f) }
    var gyroY by remember { mutableFloatStateOf(0.02f) }
    var gyroZ by remember { mutableFloatStateOf(0.00f) }

    // Fall State
    var isFallDetected by remember { mutableStateOf(false) }
    var maxGForce by remember { mutableFloatStateOf(9.81f) }
    var simulatedSpikeActive by remember { mutableStateOf(false) }

    val netAcceleration = if (simulatedSpikeActive) {
        32.14f
    } else {
        sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ)
    }

    if (netAcceleration > maxGForce) {
        maxGForce = netAcceleration
    }

    if (netAcceleration > FALL_THRESHOLD_M_S2 && !isFallDetected) {
        isFallDetected = true
    }

    DisposableEffect(Unit) {
        val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null || simulatedSpikeActive) return
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        accelX = event.values[0]
                        accelY = event.values[1]
                        accelZ = event.values[2]
                    }
                    Sensor.TYPE_GYROSCOPE -> {
                        gyroX = event.values[0]
                        gyroY = event.values[1]
                        gyroZ = event.values[2]
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accelSensor?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }
        gyroSensor?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        SectionHeader(
            title = "Motion Sensors & Fall Detection",
            subtitle = "Realtime Accelerometer & Gyroscope Vector Monitoring",
            icon = Icons.Default.Sensors
        )

        Spacer(modifier = Modifier.height(12.dp))

        // High Priority Fall Alert Banner
        if (isFallDetected) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D)),
                border = BorderStroke(2.dp, MedicalAlertRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = null,
                            tint = MedicalAlertRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "EMERGENCY: Patient Fall Detected!",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Impact Magnitude Spike: ${String.format(Locale.US, "%.2f", netAcceleration)} m/s² (Threshold: >25.0 m/s²)\nNotifying emergency dispatch protocol...",
                        color = Color(0xFFFEE2E2),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                isFallDetected = false
                                simulatedSpikeActive = false
                            },
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("Cancel False Alarm", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Net Vector Magnitude Gauge Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                1.dp,
                if (netAcceleration > FALL_THRESHOLD_M_S2) MedicalAlertRed else MedicalCardBorder
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NET ACCELERATION MAGNITUDE",
                        color = MedicalTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    StatusBadge(
                        text = if (netAcceleration > FALL_THRESHOLD_M_S2) "CRITICAL IMPACT" else "NORMAL MOTION",
                        isSuccess = netAcceleration <= FALL_THRESHOLD_M_S2
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format(Locale.US, "%.2f", netAcceleration),
                        color = if (netAcceleration > FALL_THRESHOLD_M_S2) MedicalAlertRed else MedicalCyanPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "m/s²",
                        color = MedicalTextSecondary,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val progress = (netAcceleration / 40.0f).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (netAcceleration > FALL_THRESHOLD_M_S2) MedicalAlertRed else MedicalCyanPrimary,
                    trackColor = Color(0xFF0F172A),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Peak Session G-Force: ${String.format(Locale.US, "%.2f", maxGForce)} m/s²",
                        color = MedicalTextMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Trigger Threshold: >25.0 m/s²",
                        color = MedicalTextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Accelerometer Real-Time Gauges (3-Axis)
        Text(
            text = "ACCELEROMETER (m/s²)",
            color = MedicalTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Accel X",
                value = String.format(Locale.US, "%.2f", accelX),
                accentColor = MedicalCyanPrimary,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Accel Y",
                value = String.format(Locale.US, "%.2f", accelY),
                accentColor = MedicalCyanPrimary,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Accel Z",
                value = String.format(Locale.US, "%.2f", accelZ),
                accentColor = MedicalCyanPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gyroscope Real-Time Gauges (3-Axis)
        Text(
            text = "GYROSCOPE (rad/s)",
            color = MedicalTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Gyro X",
                value = String.format(Locale.US, "%.2f", gyroX),
                accentColor = MedicalEmeraldSecondary,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Gyro Y",
                value = String.format(Locale.US, "%.2f", gyroY),
                accentColor = MedicalEmeraldSecondary,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Gyro Z",
                value = String.format(Locale.US, "%.2f", gyroZ),
                accentColor = MedicalEmeraldSecondary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Emulator Presentation Helper Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2942)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MedicalCyanPrimary.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "EMULATOR TEST HELPER",
                    color = MedicalCyanPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Simulate a severe impact spike without shaking the PC or using external sensors.",
                    color = MedicalTextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            simulatedSpikeActive = true
                            accelX = 18.0f
                            accelY = 22.0f
                            accelZ = 15.0f
                            isFallDetected = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MedicalAlertRed,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Simulate Fall Impact", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            simulatedSpikeActive = false
                            isFallDetected = false
                            accelX = 0.1f
                            accelY = 9.8f
                            accelZ = 0.2f
                            maxGForce = 9.81f
                        },
                        border = BorderStroke(1.dp, MedicalCardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalTextPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset Baseline", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
