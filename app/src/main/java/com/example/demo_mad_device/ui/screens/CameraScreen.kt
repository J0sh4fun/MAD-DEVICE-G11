package com.example.demo_mad_device.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.SystemClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoCamera
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.demo_mad_device.ui.components.PermissionFallbackBanner
import com.example.demo_mad_device.ui.components.SectionHeader
import com.example.demo_mad_device.ui.components.StatusBadge
import com.example.demo_mad_device.ui.theme.MedicalCardBorder
import com.example.demo_mad_device.ui.theme.MedicalCyanPrimary
import com.example.demo_mad_device.ui.theme.MedicalEmeraldSecondary
import com.example.demo_mad_device.ui.theme.MedicalTextMuted
import com.example.demo_mad_device.ui.theme.MedicalTextPrimary
import com.example.demo_mad_device.ui.theme.MedicalTextSecondary
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor

data class CapturedWoundSnapshot(
    val file: File,
    val bitmap: Bitmap,
    val timestamp: String,
    val calculatedArea: String
)

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
        }
    )

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var lastSnapshot by remember { mutableStateOf<CapturedWoundSnapshot?>(null) }
    var isFlashOn by remember { mutableStateOf(false) }
    var captureStatusMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        SectionHeader(
            title = "Wound Telehealth & Dermatology Imaging",
            subtitle = "CameraX Viewfinder & High-Resolution Snapshot Capture",
            icon = Icons.Default.CameraAlt
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (!hasCameraPermission) {
            PermissionFallbackBanner(
                title = "Camera Access Denied",
                message = "Telehealth wound imaging requires camera permission to capture clinical records and analyze dermatology samples.",
                onRequestPermission = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            )
        } else {
            // Camera Viewfinder Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, MedicalCyanPrimary.copy(alpha = 0.8f)),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }

                                val capture = ImageCapture.Builder()
                                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                    .build()

                                imageCapture = capture

                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        capture
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }, ContextCompat.getMainExecutor(ctx))

                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Clinical Crosshair Overlay
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.Center)
                            .border(1.dp, MedicalCyanPrimary.copy(alpha = 0.5f), CircleShape)
                    )

                    // Top Bar Overlay
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .align(Alignment.TopStart),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(text = "LIVE CLINICAL FEED", isSuccess = true)

                        IconButton(
                            onClick = { isFlashOn = !isFlashOn },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = "Flash",
                                tint = if (isFlashOn) MedicalCyanPrimary else Color.White
                            )
                        }
                    }

                    // Shutter Button Row Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(12.dp)
                            .align(Alignment.BottomCenter),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                captureWoundImage(
                                    context = context,
                                    imageCapture = imageCapture,
                                    flashOn = isFlashOn,
                                    onSuccess = { snapshot ->
                                        lastSnapshot = snapshot
                                        captureStatusMessage = "Wound Snapshot Saved Successfully"
                                    },
                                    onError = { err ->
                                        captureStatusMessage = "Capture Error: ${err.message}"
                                    }
                                )
                            },
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MedicalCyanPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Capture Snapshot",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }

            if (captureStatusMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MedicalEmeraldSecondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = captureStatusMessage!!,
                            color = MedicalEmeraldSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Display Snapshot Saved Thumbnail
            if (lastSnapshot != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MedicalEmeraldSecondary.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Wound Snapshot Saved",
                                color = MedicalTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(text = "DICOM TAGGED", isSuccess = true)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Image(
                                bitmap = lastSnapshot!!.bitmap.asImageBitmap(),
                                contentDescription = "Captured Wound Image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, MedicalCardBorder, RoundedCornerShape(8.dp))
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Capture Time:",
                                    color = MedicalTextMuted,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = lastSnapshot!!.timestamp,
                                    color = MedicalTextPrimary,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Simulated Wound Area:",
                                    color = MedicalTextMuted,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = lastSnapshot!!.calculatedArea,
                                    color = MedicalCyanPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Record ID: WND-${System.currentTimeMillis().toString().takeLast(6)}",
                                    color = MedicalTextSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun captureWoundImage(
    context: Context,
    imageCapture: ImageCapture?,
    flashOn: Boolean,
    onSuccess: (CapturedWoundSnapshot) -> Unit,
    onError: (Exception) -> Unit
) {
    if (imageCapture == null) {
        onError(Exception("Camera not initialized"))
        return
    }

    val photoFile = File(
        context.cacheDir,
        "wound_snapshot_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
    )

    imageCapture.flashMode = if (flashOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                val timeStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
                val areaStr = "${String.format(Locale.US, "%.1f", (20..50).random() / 10.0)} cm²"

                val snapshot = CapturedWoundSnapshot(
                    file = photoFile,
                    bitmap = bitmap,
                    timestamp = timeStr,
                    calculatedArea = areaStr
                )
                onSuccess(snapshot)
            }

            override fun onError(exc: ImageCaptureException) {
                onError(exc)
            }
        }
    )
}
