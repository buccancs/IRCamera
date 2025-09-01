# IRCamera PC Controller - Installation Guide

## Quick Start

This PC Controller requires Python 3.8+ and the following dependencies.

### 1. Set up Python Environment

```bash
cd pc-controller
python3 -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
```

### 2. Install Dependencies

```bash
pip install -r requirements.txt
```

If you encounter issues with PyQt5, you can install it separately:

```bash
pip install PyQt5
```

### 3. Run the Application

```bash
python src/main.py
```

Or with the module:

```bash
python -m ircamera_pc.gui.app
```

## Development

### Running Tests

```bash
pytest src/ircamera_pc/tests/
```

### Code Formatting

```bash
black src/
flake8 src/
```

## Configuration

The application uses `config/config.yaml` for configuration. Key settings include:

- `network.server_port`: Port for Android device connections (default: 8080)
- `session.data_root`: Directory for session data (default: ./sessions)
- `gsr.default_mode`: Default GSR mode - "local" or "bridged" (default: local)
- `gui.window_size`: Initial window size (default: [1200, 800])

## Troubleshooting

### Common Issues

1. **PyQt5 Installation Issues**:
   - On Ubuntu: `sudo apt-get install python3-pyqt5`
   - On macOS: `brew install pyqt5`

2. **Port Already in Use**:
   - Change `network.server_port` in config.yaml
   - Check no other applications are using port 8080

3. **Permission Issues**:
   - Ensure the application has write permissions for the session data directory
   - Check firewall settings for network communication

### Logs

Application logs are stored in `logs/ircamera_pc.log` with automatic rotation.

## Architecture Overview

The PC Controller implements a hub-and-spoke architecture with these main components:

- **Session Manager**: Handles recording session lifecycle and metadata
- **Network Server**: JSON/TCP communication with Android devices
- **Time Sync Service**: SNTP-like clock synchronization
- **GUI Application**: PyQt5-based researcher interface

See `README.md` for detailed requirements and functionality.