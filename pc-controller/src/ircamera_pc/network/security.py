"""Security utilities for IRCamera PC Controller."""

import hashlib
import secrets
from typing import Dict, Any
from ..utils.simple_logger import logger


class SecurityManager:
    """Manages security and authentication."""
    
    def __init__(self):
        self._tokens = {}
    
    def generate_token(self) -> str:
        """Generate a secure authentication token."""
        return secrets.token_urlsafe(32)
    
    def validate_token(self, token: str) -> bool:
        """Validate an authentication token."""
        return token in self._tokens
