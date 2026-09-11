package com.example.demo_mad_device.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
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
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.demo_mad_device.ui.components.MetricCard
import com.example.demo_mad_device.ui.components.PermissionFallbackBanner
import com.example.demo_mad_device.ui.components.SectionHeader
import com.example.demo_mad_device.ui.components.StatusBadge
import com.example.demo_mad_device.ui.theme.MedicalCardBorder
import com.example.demo_mad_device.ui.theme.MedicalCyanPrimary
import com.example.demo_mad_device.ui.theme.MedicalEmeraldSecondary
import com.example.demo_mad_device.ui.theme.MedicalTextMuted
import com.example.demo_mad_device.ui.theme.MedicalTextPrimary
import com.example.demo_mad_device.ui.theme.MedicalTextSecondary
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

// St. Jude Emergency Medical Center Mock Target Coordinates
private const val EMERGENCY_CENTER_LAT = 37.4220
private const val EMERGENCY_CENTER_LNG = -122.0841

@Composable
fun LocationScreen() {
    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        }
    )

    var currentLatitude by remember { mutableStateOf<Double?>(37.421998) }
    var currentLongitude by remember { mutableStateOf<Double?>(-122.084000) }
    var currentAltitude by remember { mutableStateOf<Double?>(15.2) }
    var currentAccuracy by remember { mutableStateOf<Float?>(4.2f) }
    var currentSpeed by remember { mutableStateOf<Float?>(0.0f) }
    var lastFixTime by remember { mutableStateOf("Live Fix Active") }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdate() {
        if (!hasLocationPermission) return

        fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                currentLatitude = loc.latitude
                currentLongitude = loc.longitude
                currentAltitude = loc.altitude
                currentAccuracy = loc.accuracy
                currentSpeed = loc.speed
                lastFixTime = "Last fix: Just now"
            }
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
            .setMinUpdateIntervalMillis(1500L)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(res: LocationResult) {
                val loc = res.lastLocation ?: return
                currentLatitude = loc.latitude
                currentLongitude = loc.longitude
                currentAltitude = loc.altitude
                currentAccuracy = loc.accuracy
                currentSpeed = loc.speed
                lastFixTime = "Realtime Stream"
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            requestLocationUpdate()
        }
    }

    // Distance calculation to Emergency Center
    val distanceResults = remember(currentLatitude, currentLongitude) {
        FloatArray(1).also { array ->
            if (currentLatitude != null && currentLongitude != null) {
                Location.distanceBetween(
                    currentLatitude!!,
                    currentLongitude!!,
                    EMERGENCY_CENTER_LAT,
                    EMERGENCY_CENTER_LNG,
                    array
                )
            }
        }
    }

    val distanceMeters = distanceResults[0]
    val distanceKm = distanceMeters / 1000.0
    val estimatedMinutesEta = (distanceKm / 50.0 * 60).coerceAtLeast(1.5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        SectionHeader(
            title = "Location & Emergency Dispatch Telemetry",
            subtitle = "FusedLocationProviderClient High-Precision Positioning",
            icon = Icons.Default.LocationOn
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (!hasLocationPermission) {
            PermissionFallbackBanner(
                title = "Location Access Denied",
                message = "Emergency patient dispatch and live ambulance routing require FINE/COARSE location permission.",
                onRequestPermission = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            )
        } else {
            // Live Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2942)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MedicalCyanPrimary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = MedicalCyanPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "GPS HARDWARE TELEMETRY",
                                color = MedicalTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = lastFixTime,
                                color = MedicalTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                    StatusBadge(text = "HIGH PRECISION", isSuccess = true)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Coordinates Metric Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Latitude",
                    value = String.format(Locale.US, "%.6f°", currentLatitude ?: 0.0),
                    subtitle = if (currentLatitude != null && currentLatitude!! >= 0) "North" else "South",
                    icon = Icons.Default.Explore,
                    accentColor = MedicalCyanPrimary,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Longitude",
                    value = String.format(Locale.US, "%.6f°", currentLongitude ?: 0.0),
                    subtitle = if (currentLongitude != null && currentLongitude!! >= 0) "East" else "West",
                    icon = Icons.Default.Explore,
                    accentColor = MedicalCyanPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Altitude",
                    value = String.format(Locale.US, "%.1f", currentAltitude ?: 0.0),
                    unit = "m",
                    subtitle = "WGS84 Ellipsoid",
                    accentColor = MedicalEmeraldSecondary,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Accuracy",
                    value = String.format(Locale.US, "±%.1f", currentAccuracy ?: 0.0),
                    unit = "m",
                    subtitle = if ((currentAccuracy ?: 10f) <= 10f) "Optimal Signal" else "Coarse Signal",
                    accentColor = MedicalEmeraldSecondary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Emergency Dispatch Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MedicalCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = MedicalCyanPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "St. Jude Emergency Medical Center",
                                color = MedicalTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Calculated Distance",
                                color = MedicalTextMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = if (distanceMeters < 1000) {
                                    "${distanceMeters.toInt()} meters"
                                } else {
                                    String.format(Locale.US, "%.2f km", distanceKm)
                                },
                                color = MedicalCyanPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Emergency Dispatch ETA",
                                color = MedicalTextMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = String.format(Locale.US, "%.1f mins", estimatedMinutesEta),
                                color = MedicalEmeraldSecondary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Target Center Coords: $EMERGENCY_CENTER_LAT°, $EMERGENCY_CENTER_LNG°",
                        color = MedicalTextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { requestLocationUpdate() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MedicalCyanPrimary,
                                contentColor = Color(0xFF0F172A)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Refresh GPS Fix", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
