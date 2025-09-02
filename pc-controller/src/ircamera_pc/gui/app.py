"""
Main GUI Application for IRCamera PC Controller

Provides PyQt6-based researcher interface for controlling recording sessions.
Implements FR6: User Interface for Monitoring & Control requirements.
"""

import asyncio
import sys
import signal
from typing import Optional
from PyQt6.QtWidgets import QApplication
from PyQt6.QtCore import QTimer
from loguru import logger

from ..core import config, SessionManager
from ..core.timesync import TimeSyncService
from ..core.gsr_ingestor import GSRIngestor
from ..core.file_transfer import FileTransferManager
from ..core.calibration import CameraCalibrator
from ..core.bluetooth_manager import BluetoothManager
from ..core.wifi_manager import WiFiManager
from ..core.admin_privileges import AdminPrivilegesManager
from ..network.server import NetworkServer
from .main_window import MainWindow
from .utils import setup_logging


class IRCameraApp:
    """
    Main application controller for IRCamera PC Controller.

    Coordinates between GUI components and backend services including
    all new components: GSR Ingestor, File Transfer Manager, and Calibration Tools.
    """

    def __init__(self):
        """Initialize the application with all components."""
        # Load configuration
        self.config = config

        # Core services
        self.session_manager = SessionManager()
        self.time_sync_service = TimeSyncService()
        self.network_server = NetworkServer()

        # Enhanced components for system integration
        self.gsr_ingestor = GSRIngestor(self.config)
        self.file_transfer_manager = FileTransferManager(self.config)
        self.camera_calibrator = CameraCalibrator(self.config)

        # System integration managers
        self.admin_privileges_manager = AdminPrivilegesManager()
        self.bluetooth_manager = BluetoothManager()
        self.wifi_manager = WiFiManager()

        # GUI
        self.qt_app: Optional[QApplication] = None
        self.main_window: Optional[MainWindow] = None

        # Event loop integration
        self._loop: Optional[asyncio.AbstractEventLoop] = None
        self._timer: Optional[QTimer] = None

        logger.info(
            "IRCamera Application initialized"
            "with system integration features"
        )
        components = [
            "Session Manager",
            "Time Sync",
            "Network Server",
            "GSR Ingestor",
            "File Transfer",
            "Camera Calibrator",
            "Admin Privileges",
            "Bluetooth Manager",
            "WiFi Manager",
        ]
        logger.info(f"Components: {', '.join(components)}")

    def setup_qt_app(self) -> None:
        """Set up Qt application."""
        if self.qt_app is None:
            self.qt_app = QApplication(sys.argv)
            self.qt_app.setApplicationName("IRCamera PC Controller")
            self.qt_app.setApplicationVersion("0.1.0")

            # Set up application style
            self.qt_app.setStyleSheet(
                """
                QMainWindow {
                    background-color: #f0f0f0;
                }

                QGroupBox {
                    font-weight: bold;
                    border: 2px solid #cccccc;
                    border-radius: 5px;
                    margin-top: 1ex;
                    padding-top: 10px;
                }

                QGroupBox::title {
                    subcontrol-origin: margin;
                    left: 10px;
                    padding: 0 5px 0 5px;
                }

                QPushButton {
                    background-color: #e1e1e1;
                    border: 1px solid #969696;
                    border-radius: 3px;
                    padding: 6px;
                    min-width: 80px;
                }

                QPushButton:hover {
                    background-color: #e8e8e8;
                }

                QPushButton:pressed {
                    background-color: #d0d0d0;
                }

                QPushButton:disabled {
                    color: #888888;
                    background-color: #f0f0f0;
                }

                QPushButton.primary {
                    background-color: #0078d4;
                    color: white;
                    font-weight: bold;
                }

                QPushButton.primary:hover {
                    background-color: #106ebe;
                }

                QPushButton.primary:pressed {
                    background-color: #005a9e;
                }

                QPushButton.danger {
                    background-color: #d13438;
                    color: white;
                    font-weight: bold;
                }

                QPushButton.danger:hover {
                    background-color: #c4292e;
                }

                QPushButton.danger:pressed {
                    background-color: #a01e22;
                }

                QListWidget {
                    border: 1px solid #cccccc;
                    border-radius: 3px;
                    background-color: white;
                }

                QListWidget::item {
                    padding: 8px;
                    border-bottom: 1px solid #eeeeee;
                }

                QListWidget::item:selected {
                    background-color: #0078d4;
                    color: white;
                }

                QStatusBar {
                    border-top: 1px solid #cccccc;
                    background-color: #f8f8f8;
                }
            """
            )

        # Create main window with all components including system integration
        self.main_window = MainWindow(
            session_manager=self.session_manager,
            network_server=self.network_server,
            time_sync_service=self.time_sync_service,
            gsr_ingestor=self.gsr_ingestor,
            file_transfer_manager=self.file_transfer_manager,
            camera_calibrator=self.camera_calibrator,
            bluetooth_manager=self.bluetooth_manager,
            wifi_manager=self.wifi_manager,
            admin_privileges_manager=self.admin_privileges_manager,
        )

        # Set up window size from config (increased for system integration features)
        window_size = config.get("gui.window_size", [1400, 900])
        self.main_window.resize(window_size[0], window_size[1])

        logger.info("Qt application set up")

    def setup_event_loop_integration(self) -> None:
        """Set up asyncio event loop integration with Qt."""
        # Get or create event loop
        try:
            self._loop = asyncio.get_event_loop()
        except RuntimeError:
            self._loop = asyncio.new_event_loop()
            asyncio.set_event_loop(self._loop)

        # Set up periodic Qt processing
        self._timer = QTimer()
        self._timer.timeout.connect(self._process_async_events)

        # Update interval from config
        update_interval = config.get("gui.update_interval_ms", 100)
        self._timer.start(update_interval)

        logger.debug("Event loop integration set up")

    def _process_async_events(self) -> None:
        """Process pending asyncio events."""
        if self._loop:
            try:
                # Process up to 10 callbacks per timer tick
                for _ in range(10):
                    if self._loop._ready:
                        handle = self._loop._ready.popleft()
                        if not handle.cancelled():
                            handle._run()
                    else:
                        break
            except (OSError, ValueError, RuntimeError) as e:
                logger.error(f"Error processing async events: {e}")

    async def start_services(self) -> None:
        """Start backend services."""
        try:
            # Start time synchronization service
            await self.time_sync_service.start()

            # Start network server
            await self.network_server.start()

            logger.info("All services started successfully")

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to start services: {e}")
            raise

    async def stop_services(self) -> None:
        """Stop backend services and clean up system integration managers."""
        try:
            # Stop system integration managers first
            if self.wifi_manager:
                await self.wifi_manager.cleanup()

            if self.bluetooth_manager:
                await self.bluetooth_manager.cleanup()

            # Stop core services in reverse order
            await self.network_server.stop()
            await self.time_sync_service.stop()

            logger.info("All services and system integration managers stopped")

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Error stopping services: {e}")

    def run(self) -> int:
        """
        Run the application.

        Returns:
            Application exit code
        """
        try:
            # Set up logging
            setup_logging()

            # Set up Qt application
            self.setup_qt_app()

            # Set up event loop integration
            self.setup_event_loop_integration()

            # Set up signal handlers
            signal.signal(signal.SIGINT, self._handle_signal)
            signal.signal(signal.SIGTERM, self._handle_signal)

            # Start backend services
            asyncio.run_coroutine_threadsafe(self.start_services(), self._loop)

            # Show main window
            self.main_window.show()

            logger.info("IRCamera PC Controller started")

            # Run Qt event loop
            return self.qt_app.exec_()

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Application error: {e}")
            return 1
        finally:
            # Clean up
            if self._timer:
                self._timer.stop()

            # Stop services
            if self._loop and not self._loop.is_closed():
                try:
                    future = asyncio.run_coroutine_threadsafe(
                        self.stop_services(), self._loop
                    )
                    future.result(timeout=5)  # 5 second timeout
                except (OSError, ValueError, RuntimeError) as e:
                    logger.error(f"Error during cleanup: {e}")

    def _handle_signal(self, signum: int, frame) -> None:
        """Handle system signals."""
        logger.info(f"Received signal {signum}, shutting down...")

        if self.qt_app:
            self.qt_app.quit()


def main() -> int:
    """Main entry point for the application."""
    app = IRCameraApp()
    return app.run()


if __name__ == "__main__":
    sys.exit(main())
