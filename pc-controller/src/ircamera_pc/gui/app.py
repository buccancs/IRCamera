
"""Main application module for IRCamera PC Controller."""

import signal
import sys
from PyQt6.QtWidgets import QApplication
from .main_window import MainWindow
from ..utils.simple_logger import setup_logging


class IRCameraApp:
    """Main application class."""
    
    def __init__(self):
        self.app = None
        self.main_window = None
    
    def initialize(self):
        """Initialize the application."""
        try:
            setup_logging()
            self.setup_qt_app()
            self.setup_event_loop_integration()
            signal.signal(signal.SIGINT, self._handle_signal)
            signal.signal(signal.SIGTERM, self._handle_signal)

            if self._loop is not None:
                asyncio.run_coroutine_threadsafe(self.start_services(), self._loop)

            if self.main_window is not None:
                self.main_window.show()

            logger.info("IRCamera PC Controller started")

            # Run Qt event loop
            return self.qt_app.exec()

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Application error: {e}")
            return 1
        finally:
            # Clean up
            if self._timer:
                self._timer.stop()

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
    import argparse
    
    parser = argparse.ArgumentParser(description='IRCamera PC Controller - Multi-Modal Physiological Sensing Platform Hub')
    parser.add_argument('--version', action='version', version='IRCamera PC Controller v1.0.0')
    parser.add_argument('--config', help='Path to configuration file')
    parser.add_argument('--debug', action='store_true', help='Enable debug logging')
    parser.add_argument('--headless', action='store_true', help='Run in headless mode (no GUI)')
    
    args = parser.parse_args()
    
    if args.debug:
        logger.info("Debug mode enabled")
    
    app = IRCameraApp()
    
    # Handle headless mode
    if args.headless:
        logger.info("Running in headless mode - network services only")
        # In headless mode, we would just run the network server without GUI
        # For now, just print the help and exit
        parser.print_help()
        return 0
    
    return app.run()

if __name__ == "__main__":
    sys.exit(main())
