# IRCamera - Architecture Overview

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        IRCamera Android App                      │
├─────────────────────────────────────────────────────────────────┤
│                         Presentation Layer                       │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │MainActivity │ │ThermalAct..│ │GalleryAct...│ │SettingsAct..│ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
│                                                                   │
│                       ViewModel Layer (MVVM)                     │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │FirmwareVM   │ │ThermalVM    │ │GalleryVM    │ │   UserVM    │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│                       Repository Layer                           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │TC007Repo    │ │TS004Repo    │ │GalleryRepo  │ │ LmsRepo     │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│                        Data Layer                                │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │  Local DB   │ │   Files     │ │  Network    │ │ USB/Device  │ │
│  │   (Room)    │ │ (Config)    │ │ (Retrofit)  │ │  Interface  │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## Component Modules Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Main App                                 │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │   thermal   │ │thermal-lite │ │thermal-ir   │ │    house    │ │
│  │             │ │             │ │             │ │             │ │
│  │ Core IR     │ │ Lightweight │ │ Advanced    │ │ Building    │ │
│  │ Processing  │ │ Thermal     │ │ IR Features │ │ Inspection  │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
│                                                                   │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │   pseudo    │ │   edit3d    │ │  transfer   │ │    user     │ │
│  │             │ │             │ │             │ │             │ │
│  │ Color       │ │ 3D Editing  │ │ Data        │ │ Account     │ │
│  │ Processing  │ │ Tools       │ │ Transfer    │ │ Management  │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │               CommonComponent                               │ │
│  │         Shared utilities and base classes                  │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## Device Integration Flow

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Device    │────│    USB      │────│  Device     │────│ Repository  │
│  (TC007/    │    │  Interface  │    │  Manager    │    │   Layer     │
│   TS004)    │    │             │    │             │    │             │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
                            │                                      │
                            ▼                                      ▼
                   ┌─────────────┐                      ┌─────────────┐
                   │   Native    │                      │ ViewModel   │
                   │  Libraries  │                      │   Layer     │
                   │ (libircmd,  │                      │             │
                   │  libirprocess)                     │             │
                   └─────────────┘                      └─────────────┘
                            │                                      │
                            ▼                                      ▼
                   ┌─────────────┐                      ┌─────────────┐
                   │   Image     │                      │     UI      │
                   │ Processing  │                      │ Components  │
                   │  (OpenCV)   │                      │             │
                   └─────────────┘                      └─────────────┘
```

## File System Organization

```
External Storage
├── DCIM/
│   ├── TC001/          # TC001 device images
│   ├── TC007/          # TC007 device images
│   ├── TS004/          # TS004 device images
│   └── TopInfrared/    # Main app images
│
App External Files/
├── Gallery/            # User gallery management
├── DataLog/           
│   ├── DIAG/          # Diagnostic logs
│   └── IMMO/          # Immobilizer logs
├── History/
│   ├── Diagnose/      # Diagnostic history
│   └── Service/       # Service history
├── Firmware/          # Device firmware files
├── Download/          # Downloaded files
├── Log/               # Application logs
└── FeedbackLog/       # User feedback logs
```

## Key Design Patterns

1. **MVVM (Model-View-ViewModel)**
   - Clean separation between UI and business logic
   - Observable data binding with LiveData
   - Lifecycle-aware components

2. **Repository Pattern**
   - Centralized data access layer
   - Abstraction between ViewModels and data sources
   - Support for multiple data sources (local, remote, device)

3. **Observer Pattern**
   - Event-driven architecture using EventBus
   - LiveData observers for UI updates
   - Device state change notifications

4. **Factory Pattern**
   - Device-specific repository creation
   - Configuration-based component instantiation

5. **Singleton Pattern**
   - Shared resources (WebSocket, Device managers)
   - Configuration management
   - Logging utilities

## Threading Model

```
Main Thread (UI)
├── ViewModels (Main Thread)
├── LiveData Observers (Main Thread)
└── Event Handlers (Main Thread)

Background Processing
├── Repository Operations (IO Dispatcher)
├── Network Calls (IO Dispatcher)
├── File Operations (IO Dispatcher)
├── Image Processing (Default Dispatcher)
└── Device Communication (IO Dispatcher)
```

## Security Considerations

- **USB Device Filtering**: Specific device filter configurations
- **File Access**: Scoped storage compliance for Android 10+
- **Network Security**: HTTPS enforcement for API calls
- **Permission Management**: Runtime permissions for device access
- **Data Validation**: Input validation for device commands