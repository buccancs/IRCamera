# AI Agent Guidelines for IRCamera - Enterprise Multi-Device Thermal Imaging Platform

## 1. Project Context and Documentation

This document contains the primary set of instructions for you, the AI coding agent. All guidelines herein are derived
from and must be consistent with the official project documentation, which serves as the ultimate source of truth:

* `README.md` (Project Overview and Features)
* `docs/TECHNICAL_SPECIFICATIONS.md` (Technical Requirements)
* `docs/modules/` (Module-specific Documentation)

You must adhere to the specifications, architectures, and requirements detailed in these source documents by following
the specific instructions laid out in the subsequent sections of this guideline file.

## **2. Project-Level Instructions for the AI Agent**

You are an expert software architect and developer. Your task is to maintain and enhance the IRCamera thermal imaging platform
codebase with enterprise-grade thermal imaging capabilities, multi-device support, and advanced analytics.

### **2.1. Project Overview and Goal**

The goal is to maintain and enhance an enterprise-grade thermal imaging platform consisting of two main applications: 
a **PC Controller (Hub)** and an **Android Thermal Imaging Application (Spoke)**. The system must support multiple thermal 
camera devices, real-time processing, machine learning integration, and precise data synchronization for industrial, 
research, and commercial applications.

### **2.2. Core Architecture: Hub-and-Spoke Model**

The IRCamera platform is built on a **Hub-and-Spoke** client-server architecture optimized for thermal imaging:

* **Hub (PC Controller):** The central master controller for advanced thermal data processing, device coordination, 
  ML inference, cloud integration, and multi-device management.
* **Spoke (Android Thermal App):** Mobile thermal imaging application responsible for hardware interfacing with 
  thermal cameras (TC001, TC007, TS004, HIKVision), real-time capture, local processing, and data transmission.

-----

## **3. Coding Guidelines and Standards**

You must adhere to the following coding standards and best practices for all thermal imaging platform code.

### **3.1. General Guidelines**

* **Language:** All code, comments, and documentation must be in English.
* **Readability:** Prioritize clean, readable, and self-documenting code. Use clear and descriptive names for variables,
  functions, and classes. Keep functions short and focused on a single responsibility.
* **Documentation:**
    * Generate Python docstrings for all public modules, classes, and functions.
    * Generate KDoc comments for all public classes and methods in the Kotlin codebase.
* **Error Handling:** Implement robust error handling using `try-except` (Python) and `try-catch` (Kotlin) blocks.
  Provide meaningful error messages that can be logged and displayed to the user.
* **Logging:** Implement structured logging throughout both applications to facilitate debugging. Log key events,
  errors, and state changes.

### **3.2. Python (PC Controller) Guidelines**

* **Style Guide:** Adhere strictly to the **PEP 8** style guide for all Python code.
* **Type Hinting:** All function and method signatures **must** include type hints.
* **Threading:** All network operations and other long-running tasks must be executed in background `QThread` workers to
  ensure the PyQt6 GUI remains responsive and never blocks.
* **Performance:** For performance-critical operations (e.g., real-time sensor data processing, video frame handling),
  you must implement the logic in the C++ `native_backend` and expose it to Python via PyBind11.

### **3.3. Kotlin (Android Thermal Imaging) Guidelines**

* **Style Guide:** Adhere strictly to the official **Kotlin Style Guide** recommended by Google for Android development.
* **Architecture:** The application **must** follow the **MVVM (Model-View-ViewModel)** architecture. Separate UI
  logic (Activities/Fragments) from business logic (ViewModels) and data sources (Repositories).
* **Asynchronicity:** **All** asynchronous operations (thermal camera interfacing, network requests, file I/O, database access) must be handled
  using **Kotlin Coroutines**.
* **Lifecycle Awareness:** All components that interact with the Android framework must be lifecycle-aware. Use Android
  Architecture Components like `ViewModel` and `LiveData` to manage UI state and data across configuration changes.
* **Resource Management:** Use the `use` block for all `Closeable` resources (e.g., thermal camera connections, file streams) to ensure they are
  properly closed, even in the event of an exception.
* **Modularity:** Any new thermal camera integration must implement the common thermal camera interface to ensure a consistent
  API for the thermal recording controller.

### **3.4. Version Control (Git) Guidelines**

* **Commit Messages:** All commit messages must follow the **Conventional Commits** specification. Each message must
  have a type (e.g., `feat`, `fix`, `docs`, `refactor`) and a concise description.
    * *Example:* `feat: Implement NativeShimmer C++ backend for low-latency GSR`
* **Branching:** Use a simple GitFlow-like branching model:
    * `main`: Contains stable, production-ready code.
    * `develop`: Integration branch for new features.
    * `feat/...`: Feature branches for new development.

-----

## **4. PC Controller (Hub) Specifications**

### **4.1. Technology Stack**

* **Language:** Python 3.11+
* **GUI Framework:** PyQt6
* **Performance-Critical Backend:** C++ integrated via PyBind11
* **Dependencies:** `opencv-python`, `PyQtGraph`, `pandas`, `h5py`, `numpy`.

### **4.2. Project Structure**

The PC Controller follows this directory structure:

```plaintext
/pc_controller/
+-- src/
|   +-- main.py           # Main application entry point
|   +-- gui/              # GUI modules (thermal display, device management)
|   +-- network/          # Network modules (device communication, data sync)
|   +-- core/             # Core logic (thermal processing, device management)
|   +-- data/             # Data handling (thermal data aggregation, export)
+-- native_backend/       # C++ source for PyBind11 extension
+-- tests/                # Pytest unit tests
```

### 4.3. Key Module Implementation

* **`ThermalGUIManager`:** Implement a tabbed interface (Live View, Analysis, Device Management). Must use real-time
  thermal image display and temperature analysis plots using `PyQtGraph`.
* **`NetworkController`:** Must handle TCP/IP communication with Android thermal imaging devices for real-time data streaming.
* **`native_backend` (C++):**
    * Create thermal image processing classes for real-time temperature analysis and calibration.
    * Implement efficient thermal data structures and algorithms for high-performance processing.

-----

## **5. Android Thermal Imaging Application (Spoke) Specifications**

### **5.1. Technology Stack**

* **Language:** Kotlin
* **Architecture:** Follow the **MVVM (Model-View-ViewModel)** pattern.
* **Asynchronicity:** Use Kotlin Coroutines for all background tasks.
* **Key Libraries:** CameraX (for visible light), `UVCCamera` (for thermal), USB/network communication.

### **5.2. Project Structure**

The Android application follows this package structure:

```plaintext
/com/topdon/ircamera/
+-- ui/                 # UI Layer (MainActivity, thermal display activities)
+-- service/            # Background Service Layer (thermal capture services)
+-- controller/         # Core Logic (thermal recording, device management)
+-- thermal/            # Thermal Camera Integration Layer
|   +-- tc001/          # TC001 thermal camera support
|   +-- tc007/          # TC007 thermal camera support
|   +-- ts004/          # TS004 thermal camera support
|   +-- hikvision/      # HIKVision thermal camera support
+-- network/            # Network Communication Layer (PC Controller communication)
+-- data/               # Data Models and Storage (thermal data, device profiles)
+-- utils/              # Utility and Helper Classes (temperature conversion, calibration)
```

### **5.3. Thermal Camera Integration Logic**

* **`TC001ThermalRecorder`:** Use the TC001 SDK to interface with Topdon TC001 thermal cameras. Implement real-time
  thermal frame capture with temperature calibration and radiometric data processing.
* **`TC007ThermalRecorder`:** Interface with TC007 thermal cameras using the appropriate SDK. Handle thermal image
  processing and temperature measurement with high precision.
* **`TS004ThermalRecorder`:** Use the TS004 specifications to implement thermal data capture with proper temperature
  range handling and calibration.
* **`HIKThermalRecorder`:** Implement HIKVision thermal camera integration for enterprise-grade thermal imaging with
  advanced features and network connectivity.

-----

## **6. Communication and Data Synchronization**

* **Protocol:** All communication between the Hub and Thermal Imaging Applications must be over **TLS 1.2+ secured TCP/IP socket**. All
  control messages must be **JSON-formatted** for thermal device control and data streaming.
* **Time Synchronization:** Implement precise timestamp synchronization for thermal data correlation between PC and Android devices.
  All thermal data must be timestamped using a local high-precision monotonic clock. The final alignment will be done in
  post-processing using calculated offset, with target accuracy of <10ms for thermal imaging applications.

-----

## **7. Testing and Verification**

* **Mandatory Unit Tests:** You must generate unit tests for all new thermal imaging code.
* **Frameworks:** Use `pytest` for the Python PC Controller and `JUnit`/`Robolectric` for the Android application.
* **Verification:** The system's thermal data synchronization must be verifiable. Implement thermal calibration tests
  to ensure temperature accuracy across all supported thermal camera models.

-----

## **8. Security and Data Handling**

* **Data Protection:** The Android app must use AES256-GCM encryption via the Android Keystore for local thermal data storage.
  The PC controller must require authentication for thermal device access.
* **Anonymization:** The system must use device ID codes and must not store personal identifiers with thermal data.
  Implement privacy features for thermal imaging data where applicable.
* **Device Safety:** Ensure all thermal camera configurations adhere to safety specifications and device limitations.
* **Frameworks:** Use `pytest` for the Python PC Controller and `JUnit`/`Robolectric` for the Android application.
* **Verification:** The system's end-to-end temporal synchronization must be verifiable. Implement a "Flash Sync"
  command that causes all Android screens to flash simultaneously. This will be used to confirm that timestamps across
  all video and data streams align to within the required 5ms tolerance (FR7).

-----

## **8. Security and Data Handling**

* **Data Protection:** The Android app must use AES256-GCM encryption via the Android Keystore for local storage. The PC
  controller must require authentication.
* **Anonymization:** The system must use participant ID codes and must not store personal identifiers with sensor data.
  Implement a feature for face blurring in the video streams.
* **Device Safety:** Ensure all hardware configurations adhere to safety specifications, including current limits for
  the GSR sensor and passive sensing for the thermal camera.

-----

## **9. Mandatory Libraries and SDKs**

You **must** use the following official and community-provided libraries for thermal camera hardware integration and
data processing. Do not use generic alternatives unless explicitly instructed.

### **9.1. PC Controller (Hub) Libraries**

* **Thermal Image Processing (Python Layer):**
    * **Libraries:** `opencv-python`, `numpy`, `scipy` for thermal image processing and analysis
    * **Purpose:** Core thermal image processing, temperature calibration, and advanced analytics on the PC controller.

* **Thermal Processing Backend (C++):**
    * **Libraries:** OpenCV C++, Eigen for high-performance thermal data processing
    * **Purpose:** Real-time thermal image processing, temperature analysis, and performance-critical operations.

### **9.2. Android Thermal Imaging Application Libraries**

* **Thermal Camera Integration (Kotlin/Android):**
    * **Primary Repository:** `https://github.com/buccancs/topdon-sdk` 
    * **Alternative/Reference:** Native UVC libraries for USB thermal cameras
    * **Purpose:** Direct interface with Topdon thermal cameras (TC001, TC007, TS004) and other supported thermal devices.

* **USB Communication:**
    * **Library:** `UVCCamera` library for USB thermal camera communication
    * **Purpose:** Handle USB communication with thermal cameras that support UVC protocol.

### **9.3. Data Processing and Analytics**

* **Thermal Analysis Libraries:**
    * **Python:** `numpy`, `scipy`, `matplotlib` for thermal data analysis and visualization
    * **Android:** Native image processing libraries for real-time thermal analysis
    * **Purpose:** Temperature analysis, thermal pattern recognition, and data visualization.

-----
