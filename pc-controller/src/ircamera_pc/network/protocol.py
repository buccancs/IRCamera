"""Message protocol definitions for IRCamera PC Controller."""

import json
from typing import Any, Dict, Optional



class MessageProtocol:
    """Handles message protocol validation and processing."""

    def __init__(self):
        self.protocol_version = "1.0"

    def validate_message(self, message: Dict[str, Any]) -> bool:
        """Validate message format and structure."""
        return isinstance(message, dict) and "type" in message

    def serialize_message(self, message: Dict[str, Any]) -> str:
        """Serialize message to JSON."""
        return json.dumps(message)

    def deserialize_message(self, data: str) -> Optional[Dict[str, Any]]:
        """Deserialize JSON message."""
        try:
            return json.loads(data)
        except json.JSONDecodeError:
            return None
