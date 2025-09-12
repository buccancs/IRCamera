"""GUI utilities for IRCamera PC Controller."""



class LogHandler(QObject):
    """Custom log handler that emits Qt signals for GUI integration."""

    log_message = pyqtSignal(str, str, str)  # level, message, timestamp

    def __init__(self):
        """Initialize log handler."""
        super().__init__()

    def write(self, record):
        """Write log record."""
        # Extract relevant information from loguru record
        level = record["level"].name
        message = record["message"]
        timestamp = record["time"].strftime("%Y-%m-%d %H:%M:%S")

        # Emit signal for GUI components
        self.log_message.emit(level, message, timestamp)


def setup_logging() -> LogHandler:
    """
    Set up logging configuration for the application.

    Returns:
        LogHandler instance for GUI integration
    """
    # Remove default handler
    logger.remove()

    # Get logging configuration
    log_level = config.get("logging.level", "INFO")
    console_output = config.get("logging.console_output", True)
    file_rotation = config.get("logging.file_rotation", "1 MB")
    retention = config.get("logging.retention", "30 days")

    # Set up console logging if enabled
    if console_output:
        logger.add(
            sys.stdout,
            level=log_level,
            format=(
                "<green>{time:YYYY-MM-DD HH:mm:ss}</green> | "
                "<level>{level: <8}</level> | "
                "<cyan>{name}</cyan>:<cyan>{function}</cyan>:<cyan>{line}</cyan>"
                "-"
                "<level>{message}</level>"
            ),
        )

    # Set up file logging
    logger.add(
        "logs/ircamera_pc.log",
        level=log_level,
        format="{time:YYYY-MM-DD HH:mm:ss} | {level: <8}"
        "| {name}:{function}:{line} - {message}",
        rotation=file_rotation,
        retention=retention,
        compression="zip",
    )

    # Create and configure GUI log handler
    gui_handler = LogHandler()

    # Add custom sink for GUI integration
    def gui_sink(record):
        try:
            # Handle both dict and Record object formats
            if hasattr(record, "level"):
                level = record.level.name
                message = record.message
                timestamp = record.time.strftime("%H:%M:%S")
            else:
                level = record.get("level", {}).get("name", "INFO")
                message = record.get("message", "")
                timestamp = (
                    record.get("time", "").strftime("%H:%M:%S")
                    if record.get("time")
                    else ""
                )
            gui_handler.log_message.emit(level, message, timestamp)
        except Exception:
            # Fallback for any formatting issues
            gui_handler.log_message.emit("INFO", str(record), "")

    logger.add(gui_sink, level=log_level)

    logger.info("Logging system initialized")
    return gui_handler


def get_app_icon():
    """
    Get application icon.

    Returns:
        QIcon or None if no icon available
    """
    # For now, return None - icon can be added later
    return None


def apply_theme(app, theme_name: str = "default"):
    """
    Apply theme to the Qt application.

    Args:
        app: QApplication instance
        theme_name: Theme name to apply
    """
    if theme_name == "dark":
        # Dark theme stylesheet
        dark_style = """
        QWidget {
            background-color: #2b2b2b;
            color: #ffffff;
        }
        """
        widget.setStyleSheet(dark_style)


def format_bytes(size_bytes: int) -> str:
    """Format bytes to human readable format."""
    if size_bytes == 0:
        return "0 B"

    size_float = float(size_bytes)
    for unit in ["B", "KB", "MB", "GB", "TB"]:
        if size_float < 1024.0:
            return f"{size_float:.1f} {unit}"
        size_float = size_float / 1024.0

    return f"{size_float:.1f} PB"


def format_duration(seconds: float) -> str:

    total_seconds = int(seconds)
    hours = total_seconds // 3600
    minutes = (total_seconds % 3600) // 60
    secs = total_seconds % 60

    if hours > 0:
        return f"{hours:02d}:{minutes:02d}:{secs:02d}"
    else:
        return f"{minutes:02d}:{secs:02d}"


def get_status_color(status: str) -> str:

    status = status.lower()

    if status in ["connected", "ok", "active", "recording"]:
        return "green"
    elif status in ["warning", "disconnected", "error"]:
        return "red"
    elif status in ["idle", "waiting", "pending"]:
        return "orange"
    else:
        return "gray"


def validate_session_name(name: str) -> tuple[bool, str]:

    if not name or not name.strip():
        return True, ""  # Empty names are allowed (auto-generated)

    name = name.strip()

    if len(name) > 100:
        return False, "Session name must be 100 characters or less"

    invalid_chars = ["<", ">", ":", '"', "|", "?", "*", "/", "\\"]
    for char in invalid_chars:
        if char in name:
            return False, f"Session name cannot contain '{char}'"

    return True, ""


def confirm_action(parent, title: str, message: str) -> bool:

    from PyQt6.QtWidgets import QMessageBox

    reply = QMessageBox.question(
        parent,
        title,
        message,
        QMessageBox.StandardButton.Yes | QMessageBox.StandardButton.No,
        QMessageBox.StandardButton.No,
    )

    return bool(reply == QMessageBox.StandardButton.Yes)
