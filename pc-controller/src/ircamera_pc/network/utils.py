"""Network utilities for IRCamera PC Controller."""

import asyncio
from typing import Any
from ..utils.simple_logger import logger


class NetworkUtils:
    """Network utility functions."""
    
    @staticmethod
    def validate_ip(ip: str) -> bool:
        """Validate IP address format."""
        parts = ip.split('.')
        return len(parts) == 4 and all(0 <= int(part) <= 255 for part in parts if part.isdigit())
    
    @staticmethod
    async def test_connection(host: str, port: int) -> bool:
        """Test network connection to host:port."""
        try:
            reader, writer = await asyncio.open_connection(host, port)
            writer.close()
            await writer.wait_closed()
            return True
        except Exception:
            return False
