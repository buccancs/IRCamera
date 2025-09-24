"""
Enhanced Bluetooth Management for IRCamera PC Controller

Provides improved Shimmer3 device connectivity and reliability.
"""

from .enhanced_shimmer import EnhancedShimmerManager, ShimmerConnectionState, ShimmerGSRData, ShimmerDeviceInfo

__all__ = [
    'EnhancedShimmerManager',
    'ShimmerConnectionState', 
    'ShimmerGSRData',
    'ShimmerDeviceInfo',
]