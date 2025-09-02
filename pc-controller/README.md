# IRCamera PC Controller

A Python-based PC controller for the IRCamera multi-modal recording system. This application serves as the central hub for managing Android recording clients and coordinating synchronized data collection.

## Features

- **Session Management**: Create and manage discrete recording sessions
- **Multi-Device Coordination**: Control multiple Android recording clients simultaneously
- **Time Synchronization**: SNTP-like service to maintain clock synchronization across devices
- **Data Transfer**: Automated data aggregation from Android devices after recording
- **GUI Interface**: PyQt5-based interface for researchers
- **GSR Integration**: Special handling for GSR leader device with Local/Bridged modes

## Requirements

Based on the project requirements document, this PC controller implements:

- FR-L0: Mode selection (Local/Bridged GSR acquisition)
- FR1-FR11: Core functional requirements for multi-device sensor integration
- Non-functional requirements for real-time handling, temporal accuracy, and security

## Installation

1. Install Python 3.8 or higher
2. Install dependencies: `pip install -r requirements.txt`
3. Run the application: `python src/ircamera_pc/main.py`

## Architecture

The system follows a hub-and-spoke architecture with these core modules:

- **Session Manager**: Lifecycle and metadata management
- **Network Server**: JSON/TCP/IP control for Android devices
- **Time Synchronization Service**: SNTP-like clock synchronization
- **GSR Ingestor**: Data reconciliation on ingest
- **GUI Module**: PyQt5-based researcher interface
- **File Transfer Manager**: Resumable data transfer from devices

## Configuration

Configuration is managed through `config/config.yaml`. See documentation for details.

## Testing

Run tests with: `pytest src/ircamera_pc/tests/`

## License

TBD