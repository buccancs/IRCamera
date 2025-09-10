
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

            if self.qt_app is not None:
                result = self.qt_app.exec_()
                return int(result) if result is not None else 0
            else:
                return 1

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
    app = IRCameraApp()
    return app.run()

if __name__ == "__main__":
    sys.exit(main())
