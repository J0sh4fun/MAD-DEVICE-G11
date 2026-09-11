package com.example.demo_mad_device.ui.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
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
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

@Composable
fun BatteryScreen() {
    val context = LocalContext.current

    var actualLevel by remember { mutableIntStateOf(85) }
    var chargingStatus by remember { mutableStateOf("Discharging") }
    var powerSource by remember { mutableStateOf("Battery Power") }
    var healthStatus by remember { mutableStateOf("Good") }
    var batteryTemp by remember { androidx.compose.runtime.mutableFloatStateOf(32.5f) }
    var voltageMv by remember { mutableIntStateOf(4120) }
    var technology by remember { mutableStateOf("Li-ion") }

    var isSimulatingCriticalBattery by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                    val rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    if (rawLevel != -1 && scale != -1) {
                        actualLevel = (rawLevel * 100 / scale.toFloat()).toInt()
                    }

                    val statusInt = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    chargingStatus = when (statusInt) {
                        BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
                        BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
                        BatteryManager.BATTERY_STATUS_FULL -> "Full"
                        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
                        else -> "Unknown"
                    }

                    val pluggedInt = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
                    powerSource = when (pluggedInt) {
                        BatteryManager.BATTERY_PLUGGED_AC -> "AC Power"
                        BatteryManager.BATTERY_PLUGGED_USB -> "USB Bus"
                        BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
                        else -> "Battery Power"
                    }

                    val healthInt = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
                    healthStatus = when (healthInt) {
                        BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                        BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
                        else -> "Normal"
                    }

                    val tempRaw = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                    batteryTemp = tempRaw / 10.0f

                    voltageMv = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
                    technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"
                }
            }
        }

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, filter)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    val displayPercentage = if (isSimulatingCriticalBattery) 12 else actualLevel
    val isCritical = displayPercentage < 15

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        SectionHeader(
            title = "Battery & Power Diagnostics",
            subtitle = "Continuous Vital Monitor Power Subsystem",
            icon = Icons.Default.BatteryChargingFull
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Low Battery Safety Banner
        if (isCritical) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF451A03)),
                border = BorderStroke(2.dp, MedicalWarningAmber),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MedicalWarningAmber,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Warning: Low Battery",
                            color = MedicalWarningAmber,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Continuous physiological telemetry will be suspended.",
                            color = Color(0xFFFEF3C7),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Battery Gauge Main Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                1.dp,
                if (isCritical) MedicalWarningAmber else MedicalCardBorder
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isCritical) Icons.Default.BatteryAlert else Icons.Default.BatteryChargingFull,
                            contentDescription = null,
                            tint = if (isCritical) MedicalWarningAmber else MedicalCyanPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CHARGE LEVEL",
                            color = MedicalTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    StatusBadge(
                        text = if (isCritical) "CRITICAL <15%" else chargingStatus.uppercase(),
                        isSuccess = !isCritical,
                        warning = isCritical
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$displayPercentage%",
                        color = if (isCritical) MedicalWarningAmber else MedicalTextPrimary,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "($powerSource)",
                        color = MedicalCyanPrimary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { displayPercentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isCritical) MedicalWarningAmber else MedicalEmeraldSecondary,
                    trackColor = Color(0xFF0F172A),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Battery Diagnostics Metrics Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Health State",
                value = healthStatus,
                subtitle = "Battery Condition",
                icon = Icons.Default.Bolt,
                accentColor = MedicalEmeraldSecondary,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Temperature",
                value = String.format(Locale.US, "%.1f°C", batteryTemp),
                subtitle = "Thermal Sensor",
                icon = Icons.Default.Thermostat,
                accentColor = MedicalCyanPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Voltage",
                value = "$voltageMv",
                unit = "mV",
                subtitle = "Subsystem Bus",
                accentColor = MedicalCyanPrimary,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Chemistry",
                value = technology,
                subtitle = "Hardware Spec",
                accentColor = MedicalEmeraldSecondary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // In-App Simulation Toggle Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2942)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MedicalCyanPrimary.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Simulate Critical Battery (<15%)",
                        color = MedicalTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Triggers the low-battery continuous telemetry suspension safety banner instantly.",
                        color = MedicalTextSecondary,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Switch(
                    checked = isSimulatingCriticalBattery,
                    onCheckedChange = { isSimulatingCriticalBattery = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF0F172A),
                        checkedTrackColor = MedicalWarningAmber,
                        uncheckedThumbColor = MedicalTextSecondary,
                        uncheckedTrackColor = Color(0xFF1E293B)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
