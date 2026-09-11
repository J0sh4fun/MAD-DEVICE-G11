# HealthGuard Pro (MAD-DEVICE) 🩺📱

**HealthGuard Pro** is an Android application demonstrating native hardware integrations and sensor capabilities within a modern **Telehealth & Medical Diagnostics** context. Built with **Jetpack Compose**, **CameraX**, **Play Services Location**, and Android's native **Sensor Framework**, it provides a showcase of device hardware interaction, real-time sensor monitoring, and emulator testing workflows for Mobile Application Development (MAD).

---

## 🚀 Key Features

### 📊 1. System Info & Hardware Diagnostics (`DashboardScreen`)
- **Build Metadata**: Queries `android.os.Build` for device model, manufacturer, hardware ABI, Android SDK version, build fingerprint, and bootloader status.
- **Sensor Enumeration**: Automatically scans and lists physical/virtual hardware sensors via `SensorManager` (Accelerometer, Gyroscope, Magnetometer, Light, Proximity, Barometer, Step Counter, Heart Rate, etc.).

### 📷 2. Wound Cam & Telehealth Imaging (`CameraScreen`)
- **CameraX Integration**: High-performance camera preview using CameraX `PreviewView` and `ImageCapture`.
- **Hardware Control**: Switch between Front and Back cameras, toggle device torch/flash.
- **Snapshot Capture**: Simulates high-resolution wound diagnostic captures with instant preview buffer and metadata output.

### 📍 3. GPS & Emergency Dispatch (`LocationScreen`)
- **Fused Location Provider**: Accesses high-accuracy GPS coordinates via Google Play Services `FusedLocationProviderClient`.
- **Real-Time Tracking**: Displays latitude, longitude, altitude, velocity, accuracy radius, and location provider source.
- **Emergency Dispatch Telemetry**: Calculates real-time bearing and straight-line distance to emergency response centers.

### ⚡ 4. Fall Detection & Motion Sensors (`MotionScreen`)
- **Real-Time Dynamics**: Streams continuous 3-axis readings from `TYPE_ACCELEROMETER` ($m/s^2$) and `TYPE_GYROSCOPE` ($rad/s$).
- **G-Force & Vector Calculation**: Computes total acceleration magnitude ($\sqrt{x^2 + y^2 + z^2}$).
- **Fall Detection Algorithm**: Monitors impact thresholds (> $25\,m/s^2$) and triggers instant medical emergency alerts upon fall detection. Includes in-app impact simulation for rapid testing.

### 🔋 5. Power Monitor & Battery Diagnostics (`BatteryScreen`)
- **BroadcastReceiver Integration**: Monitors `Intent.ACTION_BATTERY_CHANGED` for live battery updates.
- **Telemetry Breakdown**: Battery charge percentage, charging state (AC, USB, Wireless, None), battery health status, voltage, temperature, and technology chemistry.
- **Low Power Safeguards**: Visual alerts and toggles for critical power state (< 15%) simulation.

### 🛡️ 6. Runtime Permissions Center (`PermissionsScreen`)
- **Centralized Security Hub**: Interactive permission management for Camera, Fine/Coarse Location, Body Sensors, and Activity Recognition.
- **Status Indicators**: Real-time permission grant status checking with single-tap system request flows.

---

## 🛠️ Architecture & Tech Stack

| Technology | Description |
|---|---|
| **Language** | 100% Kotlin |
| **UI Toolkit** | Jetpack Compose with Material 3 Design System (`HealthGuardTheme`) |
| **Navigation** | Navigation Compose |
| **Camera** | AndroidX CameraX (`camera-core`, `camera-camera2`, `camera-lifecycle`, `camera-view`) |
| **Location** | Google Play Services Location (`play-services-location`) |
| **Sensors** | Android `SensorManager` & `SensorEventListener` |
| **Target SDK** | Android 35 / 37 (Min SDK 26) |

---

## 📁 Project Structure

```
com.example.demo_mad_device/
├── MainActivity.kt               # Main entry point & Scaffold layout with BottomNav
├── ui/
│   ├── components/
│   │   ├── CommonComponents.kt   # Reusable UI cards, gauges, and status headers
│   │   └── EmulatorGuideDialog.kt# In-app interactive guide for Emulator hardware testing
│   ├── navigation/
│   │   └── NavRoutes.kt          # Sealed class routes & navigation item definitions
│   ├── screens/
│   │   ├── DashboardScreen.kt    # System info & sensor enumeration
│   │   ├── CameraScreen.kt       # CameraX telehealth preview & photo capture
│   │   ├── LocationScreen.kt     # GPS tracking & emergency dispatch distance
│   │   ├── MotionScreen.kt       # Motion sensors & fall detection logic
│   │   ├── BatteryScreen.kt      # Battery telemetry & broadcast receiver
│   │   └── PermissionsScreen.kt  # Runtime permissions control panel
│   └── theme/
│       ├── Color.kt              # Medical dark theme palette (Cyan/Emerald)
│       ├── Theme.kt              # HealthGuardTheme definition
│       └── Type.kt               # Typography configurations
```

---

## 🧪 Android Emulator Hardware Simulation Guide

To test hardware features on the Android Studio Emulator, use the **Extended Controls** (`...` menu on the emulator sidebar):

| Module | Feature | Extended Controls Setup |
|---|---|---|
| **📷 Camera** | Live Feed Simulation | Go to **Extended Controls** (`...`) -> **Camera** -> Set Front/Back camera source to **VirtualScene** or **Webcam**. |
| **📍 GPS** | Live Location Streaming | Go to **Extended Controls** (`...`) -> **Location** -> Enter coordinates or import a GPX/KML route, then click **Set Location** or **Play**. |
| **⚡ Motion** | Sensor Movement | Go to **Extended Controls** (`...`) -> **Virtual Sensors** -> Adjust device orientation or acceleration sliders. Or tap **Simulate Fall Impact** directly in the app. |
| **🔋 Battery** | Power States | Go to **Extended Controls** (`...`) -> **Battery** -> Adjust Battery Level, Charging Status (Charging/Discharging), and Charger Type (AC/USB). |

---

## 🔑 Permissions Declared

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.BODY_SENSORS" />
<uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
```

---

## ⚡ Getting Started

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd MAD_DEVICE
   ```

2. **Open in Android Studio**:
   - Open Android Studio (Ladybug 2024.2+ recommended).
   - Allow Gradle sync to complete automatically.

3. **Build & Run**:
   - Select an Android Virtual Device (AVD) running API 26+ or connect a physical Android device.
   - Run via `./gradlew installDebug` or press **Run** `Shift + F10` in Android Studio.
