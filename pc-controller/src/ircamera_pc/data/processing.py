#!/usr/bin/env python3
"""Data processing module for sensor data analysis."""

import json
import time
from dataclasses import dataclass, field
from enum import Enum
from pathlib import Path
from typing import Any, Dict, List, Optional

try:
    import numpy as np
    NUMPY_AVAILABLE = True
except ImportError:
    NUMPY_AVAILABLE = False

from loguru import logger


class DataQuality(Enum):
    """Data quality enumeration."""
    UNKNOWN = "unknown"
    POOR = "poor"
    FAIR = "fair" 
    GOOD = "good"
    EXCELLENT = "excellent"


@dataclass
class GSRSample:
    """GSR sensor data sample."""
    timestamp: float
    gsr_value: float
    raw_value: int
    device_id: str
    session_id: str
    quality: DataQuality = DataQuality.UNKNOWN
    contact_impedance: Optional[float] = None
    temperature: Optional[float] = None
    artifacts: Optional[List[str]] = field(default_factory=list)
    features: Optional[Dict[str, float]] = field(default_factory=dict)

    def __post_init__(self) -> None:
        """Post-initialization processing."""
        if self.gsr_value < 0:
            self.gsr_value = 0.0
            self.quality = DataQuality.POOR


@dataclass
class ThermalFrame:
    """Thermal camera frame data."""
    timestamp: float
    temperature_data: List[List[float]]  # 2D temperature matrix in Celsius
    device_id: str
    session_id: str
    frame_number: int
    quality: DataQuality = DataQuality.UNKNOWN
    min_temp: Optional[float] = None
    max_temp: Optional[float] = None
    avg_temp: Optional[float] = None

    def __post_init__(self) -> None:
        """Calculate basic statistics."""
        if self.temperature_data and NUMPY_AVAILABLE:
            temp_array = np.array(self.temperature_data)
            self.min_temp = float(np.min(temp_array))
            self.max_temp = float(np.max(temp_array))
            self.avg_temp = float(np.mean(temp_array))


@dataclass 
class VideoFrame:
    """Video frame metadata."""
    timestamp: float
    frame_number: int
    device_id: str
    session_id: str
    file_path: Optional[str] = None
    width: int = 0
    height: int = 0
    format: str = "jpeg"
    quality: DataQuality = DataQuality.UNKNOWN


class DataProcessor:
    """Data processing and analysis engine."""

    def __init__(self, output_directory: str) -> None:
        """Initialize data processor."""
        self.output_directory = Path(output_directory)
        self.output_directory.mkdir(parents=True, exist_ok=True)
        
        self.processed_samples = 0
        self.processing_errors = 0
        
        logger.info(f"Data processor initialized with output: {output_directory}")

    def process_gsr_data(self, samples: List[GSRSample]) -> Dict[str, Any]:
        """Process GSR data samples."""
        if not samples:
            return {}

        try:
            # Calculate basic statistics
            values = [s.gsr_value for s in samples]
            
            stats = {
                "sample_count": len(samples),
                "min_value": min(values),
                "max_value": max(values),
                "mean_value": sum(values) / len(values),
                "processing_timestamp": time.time()
            }
            
            # Add numpy-based statistics if available
            if NUMPY_AVAILABLE:
                values_array = np.array(values)
                stats.update({
                    "std_value": float(np.std(values_array)),
                    "median_value": float(np.median(values_array))
                })
            
            self.processed_samples += len(samples)
            return stats
            
        except Exception as e:
            logger.error(f"Error processing GSR data: {e}")
            self.processing_errors += 1
            return {}

    def process_thermal_data(self, frames: List[ThermalFrame]) -> Dict[str, Any]:
        """Process thermal frame data."""
        if not frames:
            return {}

        try:
            stats = {
                "frame_count": len(frames),
                "processing_timestamp": time.time()
            }
            
            if frames and frames[0].min_temp is not None:
                temps = []
                for frame in frames:
                    if frame.min_temp is not None:
                        temps.extend([frame.min_temp, frame.max_temp, frame.avg_temp])
                
                if temps:
                    stats.update({
                        "overall_min_temp": min(temps),
                        "overall_max_temp": max(temps),
                        "overall_avg_temp": sum(temps) / len(temps)
                    })
            
            self.processed_samples += len(frames)
            return stats
            
        except Exception as e:
            logger.error(f"Error processing thermal data: {e}")
            self.processing_errors += 1
            return {}

    def process_video_data(self, frames: List[VideoFrame]) -> Dict[str, Any]:
        """Process video frame metadata."""
        if not frames:
            return {}

        try:
            stats = {
                "frame_count": len(frames),
                "first_frame_time": frames[0].timestamp,
                "last_frame_time": frames[-1].timestamp,
                "processing_timestamp": time.time()
            }
            
            if frames:
                stats["duration_seconds"] = frames[-1].timestamp - frames[0].timestamp
                if stats["duration_seconds"] > 0:
                    stats["average_fps"] = len(frames) / stats["duration_seconds"]
            
            self.processed_samples += len(frames)
            return stats
            
        except Exception as e:
            logger.error(f"Error processing video data: {e}")
            self.processing_errors += 1
            return {}

    def export_results(self, results: Dict[str, Any], filename: str) -> bool:
        """Export processing results to JSON file."""
        try:
            output_path = self.output_directory / filename
            
            with open(output_path, 'w') as f:
                json.dump(results, f, indent=2)
            
            logger.info(f"Results exported to: {output_path}")
            return True
            
        except Exception as e:
            logger.error(f"Error exporting results: {e}")
            return False

    def get_processing_stats(self) -> Dict[str, Any]:
        """Get processing statistics."""
        return {
            "processed_samples": self.processed_samples,
            "processing_errors": self.processing_errors,
            "output_directory": str(self.output_directory)
        }