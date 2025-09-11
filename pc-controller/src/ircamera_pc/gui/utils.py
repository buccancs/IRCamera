
"""GUI utilities for IRCamera PC Controller."""

from typing import Any


def apply_theme(widget, theme_name: str = "dark"):
    """Apply theme to widget."""
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
