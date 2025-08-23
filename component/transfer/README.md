# Transfer Component Documentation

## Overview
The Transfer Component handles file transfer, data management, and storage operations for the IRCamera application. This component has achieved **BUILD SUCCESSFUL** status and provides robust file handling capabilities for thermal images and videos.

## Architecture

### Package Structure
```
com.topdon.module.transfer/
├── activity/           # File transfer activities
├── fragment/           # Data management fragments
├── adapter/            # File listing adapters
├── dialog/             # Transfer progress dialogs
└── util/              # File utilities and helpers
```

## Key Features

### 1. File Management
- **Thermal Image Storage**: Professional thermal image file management
- **Video Handling**: Thermal video file operations
- **Metadata Preservation**: Complete temperature and measurement data retention
- **Export Capabilities**: Multiple format support with professional output

### 2. Transfer Operations
- **Local File Operations**: Copy, move, rename, delete operations
- **Batch Processing**: Multiple file operations
- **Progress Tracking**: Real-time transfer progress monitoring
- **Error Handling**: Robust error recovery and user feedback

### 3. Storage Integration
- **External Storage**: SD card and USB storage support
- **Cloud Integration**: Optional cloud storage capabilities (when enabled)
- **Backup Management**: Local backup and restore functionality
- **File Organization**: Automatic organization by date, type, and metadata

## Core Components

### File Management System
```kotlin
class FileManager {
    fun copyFile(source: File, destination: File): Boolean
    fun moveFile(source: File, destination: File): Boolean
    fun deleteFile(file: File): Boolean
    fun renameFile(file: File, newName: String): Boolean
    
    fun getThermalFiles(directory: File): List<ThermalFile>
    fun organizeFilerByDate(files: List<File>): Map<String, List<File>>
}
```

### Transfer Dialog Components
```kotlin
class TransferProgressDialog(context: Context) : Dialog(context) {
    fun setProgress(progress: Int)
    fun setStatus(status: String)
    fun setOnCancelListener(listener: (() -> Unit)?)
    fun showError(message: String)
}

class TransferDialog : Dialog {
    fun setFileList(files: List<File>)
    fun setDestination(destination: File)
    fun startTransfer()
    fun cancelTransfer()
}
```

### File Configuration
```kotlin
class FileConfig {
    companion object {
        const val oldTc001GalleryDir = "/storage/emulated/0/Pictures/TC001/"
        const val lineGalleryDir = "/storage/emulated/0/Pictures/IRCamera/Line/"
        const val lineIrGalleryDir = "/storage/emulated/0/Pictures/IRCamera/Thermal/"
        
        fun getDefaultSaveDirectory(): File
        fun getThermalImageDirectory(): File
        fun getVideoDirectory(): File
    }
}
```

## Testing

### Unit Tests (7 Tests Passing)
- **File Operations**: Copy, move, delete functionality validation
- **Transfer Logic**: Progress calculation and error handling
- **Dialog Components**: UI component validation and interaction testing
- **Configuration**: File path and directory management verification

### Integration Tests
- **Storage Integration**: External storage and permission handling
- **Metadata Preservation**: Thermal data integrity during transfers
- **Batch Operations**: Multiple file handling efficiency

## Performance Features

### Optimized File Operations
- Asynchronous file transfers to prevent UI blocking
- Progressive loading for large file lists
- Memory-efficient file streaming for large thermal videos
- Background processing with progress callbacks

### Storage Efficiency
- Intelligent file compression for thermal data
- Duplicate detection and management
- Automatic cleanup of temporary files
- Efficient metadata indexing

## API Documentation

### Public Interfaces
All transfer operations are accessible through clean, documented APIs:
```kotlin
interface TransferService {
    suspend fun transferFile(source: File, destination: File): TransferResult
    suspend fun batchTransfer(operations: List<TransferOperation>): List<TransferResult>
    fun cancelTransfer(transferId: String)
    fun getTransferProgress(transferId: String): TransferProgress
}
```

### Integration Examples
```kotlin
// Basic file transfer
val transferService = TransferService.getInstance()
val result = transferService.transferFile(sourceFile, destinationFile)

// Batch operations
val operations = listOf(
    TransferOperation.Copy(source1, dest1),
    TransferOperation.Move(source2, dest2)
)
val results = transferService.batchTransfer(operations)
```

---

**Transfer Component** provides professional-grade file management and transfer capabilities with comprehensive testing and robust error handling for thermal imaging applications.