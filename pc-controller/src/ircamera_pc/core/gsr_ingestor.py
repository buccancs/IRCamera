#!/usr/bin/env python3
"""
GSR Ingestor for IRCamera PC Controller

Handles GSR (Galvanic Skin Response) data reconciliation and processing
as per FR11 requirements. Manages data from both Local and Bridged GSR modes.
"""

import json
import struct
import time
from typing import Dict, List, Optional, Any
from pathlib import Path
from dataclasses import dataclass, asdict
from enum import Enum

from ..utils.simple_logger import get_logger

logger = get_logger(__name__)


class GSRMode(Enum):
    """GSR acquisition modes"""

    LOCAL = "local"
    BRIDGED = "bridged"


@dataclass
class GSRSample:
    """Individual GSR sensor sample"""

    timestamp: float  # Unix timestamp with microsecond precision
    value: float  # GSR resistance value in ohms
    quality: int  # Quality indicator (0-100)
    device_id: str  # Source device identifier

    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for JSON serialization"""
        return asdict(self)


@dataclass
class GSRDataSet:
    """Collection of GSR samples with metadata"""

    session_id: str
    device_id: str
    mode: GSRMode
    start_time: float
    end_time: float
    samples: List[GSRSample]
    sample_rate: float  # Hz
    quality_stats: Dict[str, float]  # min, max, mean quality

    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for JSON serialization"""
        return {
            "session_id": self.session_id,
            "device_id": self.device_id,
            "mode": self.mode.value,
            "start_time": self.start_time,
            "end_time": self.end_time,
            "samples": [sample.to_dict() for sample in self.samples],
            "sample_rate": self.sample_rate,
            "quality_stats": self.quality_stats,
        }


class GSRIngestor:
    """
    GSR Data Reconciliation and Processing Service

    Handles GSR data from Android devices in both Local and Bridged modes.
    Provides data validation, synchronization, and quality assessment.
    """

    def __init__(self, config: Dict[str, Any]):
        """
        Initialize GSR Ingestor

        Args:
            config: Configuration dictionary with GSR settings
        """
        self.config = config.get("gsr", {})
        self.data_dir = Path(self.config.get("data_dir", "data/gsr"))
        self.data_dir.mkdir(parents=True, exist_ok=True)

        # GSR processing parameters
        self.min_sample_rate = self.config.get("min_sample_rate", 10.0)  # Hz
        self.max_sample_rate = self.config.get("max_sample_rate", 1000.0)  # Hz
        self.quality_threshold = self.config.get(
            "quality_threshold", 50
        )  # 0-100
        self.max_gap_duration = self.config.get(
            "max_gap_duration", 5.0
        )  # seconds

        # Active sessions and datasets
        self.active_sessions: Dict[str, GSRDataSet] = {}
        self.completed_sessions: Dict[str, GSRDataSet] = {}

        logger.info(
            f"GSR Ingestor initialized with data" "directory: {self.data_dir}"
        )

    async def start_session(
        self, session_id: str, device_id: str, mode: GSRMode
    ) -> bool:
        """
        Start GSR data collection for a session

        Args:
            session_id: Unique session identifier
            device_id: GSR device identifier
            mode: GSR acquisition mode (Local/Bridged)

        Returns:
            True if session started successfully
        """
        try:
            if session_id in self.active_sessions:
                logger.warning(f"GSR session {session_id} already active")
                return False

            dataset = GSRDataSet(
                session_id=session_id,
                device_id=device_id,
                mode=mode,
                start_time=time.time(),
                end_time=0.0,
                samples=[],
                sample_rate=0.0,
                quality_stats={"min": 100, "max": 0, "mean": 0},
            )

            self.active_sessions[session_id] = dataset
            logger.info(
                f"Started GSR session {session_id} for device"
                "{device_id} in {mode.value} mode"
            )
            return True

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to start GSR session {session_id}: {e}")
            return False

    async def ingest_sample(self, session_id: str, sample_data: bytes) -> bool:
        """
        Ingest a raw GSR sample from device

        Args:
            session_id: Session identifier
            sample_data: Raw sample data from device

        Returns:
            True if sample processed successfully
        """
        try:
            if session_id not in self.active_sessions:
                logger.warning(
                    f"GSR sample received for inactive session: {session_id}"
                )
                return False

            dataset = self.active_sessions[session_id]

            # Parse raw sample data (assuming format: timestamp(8) + value(4) + quality(4))
            if len(sample_data) < 16:
                logger.warning(
                    f"Invalid GSR sample data length:" "{len(sample_data)}"
                )
                return False

            timestamp, value, quality = struct.unpack("<dfi", sample_data[:16])

            sample = GSRSample(
                timestamp=timestamp,
                value=value,
                quality=quality,
                device_id=dataset.device_id,
            )

            # Validate sample
            if not self._validate_sample(sample, dataset):
                return False

            dataset.samples.append(sample)
            self._update_quality_stats(dataset, sample)

            return True

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(
                f"Failed to ingest GSR sample for" "session {session_id}: {e}"
            )
            return False

    async def end_session(self, session_id: str) -> Optional[GSRDataSet]:
        """
        End GSR data collection and finalize dataset

        Args:
            session_id: Session identifier

        Returns:
            Finalized GSR dataset or None if error
        """
        try:
            if session_id not in self.active_sessions:
                logger.warning(
                    f"Cannot end inactive GSR" "session: {session_id}"
                )
                return None

            dataset = self.active_sessions[session_id]
            dataset.end_time = time.time()

            # Calculate sample rate
            if len(dataset.samples) > 1:
                duration = dataset.end_time - dataset.start_time
                dataset.sample_rate = (
                    len(dataset.samples) / duration if duration > 0 else 0.0
                )

            # Finalize quality statistics
            if dataset.samples:
                qualities = [sample.quality for sample in dataset.samples]
                dataset.quality_stats = {
                    "min": min(qualities),
                    "max": max(qualities),
                    "mean": sum(qualities) / len(qualities),
                }

            # Move to completed sessions
            self.completed_sessions[session_id] = dataset
            del self.active_sessions[session_id]

            # Save dataset to file
            await self._save_dataset(dataset)

            logger.info(
                f"Ended GSR session {session_id} with"
                "{len(dataset.samples)} samples"
            )
            logger.info(
                f"Sample rate: {dataset.sample_rate:.1f} Hz, Quality:"
                "{dataset.quality_stats['mean']:.1f}"
            )

            return dataset

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to end GSR session {session_id}: {e}")
            return None

    def _validate_sample(self, sample: GSRSample, dataset: GSRDataSet) -> bool:
        """Validate GSR sample quality and consistency"""
        # Check quality threshold
        if sample.quality < self.quality_threshold:
            logger.debug(
                f"GSR sample below quality threshold: {sample.quality}"
                "< {self.quality_threshold}"
            )
            return False

        # Check for reasonable GSR values (typically 10 - 1000k ohms)
        if not (10.0 <= sample.value <= 1000000.0):
            logger.warning(f"GSR value out of range: {sample.value} ohms")
            return False

        # Check timestamp ordering (if not first sample)
        if (
            dataset.samples
            and sample.timestamp <= dataset.samples[-1].timestamp
        ):
            logger.warning(
                f"GSR sample timestamp not monotonic:" "{sample.timestamp}"
            )
            return False

        # Check for excessive gaps
        if dataset.samples:
            gap = sample.timestamp - dataset.samples[-1].timestamp
            if gap > self.max_gap_duration:
                logger.warning(f"Large gap in GSR data: {gap:.2f}s")
                # Still accept the sample but log the gap

        return True

    def _update_quality_stats(self, dataset: GSRDataSet, sample: GSRSample):
        """Update running quality statistics"""
        stats = dataset.quality_stats
        stats["min"] = min(stats["min"], sample.quality)
        stats["max"] = max(stats["max"], sample.quality)

        # Update running mean
        n = len(dataset.samples)
        if n == 1:
            stats["mean"] = sample.quality
        else:
            stats["mean"] = ((stats["mean"] * (n - 1)) + sample.quality) / n

    async def _save_dataset(self, dataset: GSRDataSet):
        """Save GSR dataset to JSON file"""
        try:
            filename = f"gsr_{dataset.session_id}_{dataset.device_id}.json"
            filepath = self.data_dir / filename

            with open(filepath, "w") as f:
                json.dump(dataset.to_dict(), f, indent=2)

            logger.info(f"Saved GSR dataset to {filepath}")

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to save GSR dataset: {e}")

    async def load_dataset(
        self, session_id: str, device_id: str
    ) -> Optional[GSRDataSet]:
        """Load GSR dataset from file"""
        try:
            filename = f"gsr_{session_id}_{device_id}.json"
            filepath = self.data_dir / filename

            if not filepath.exists():
                logger.warning(f"GSR dataset file not found: {filepath}")
                return None

            with open(filepath, "r") as f:
                data = json.load(f)

            # Reconstruct dataset
            samples = [
                GSRSample(**sample_data) for sample_data in data["samples"]
            ]

            dataset = GSRDataSet(
                session_id=data["session_id"],
                device_id=data["device_id"],
                mode=GSRMode(data["mode"]),
                start_time=data["start_time"],
                end_time=data["end_time"],
                samples=samples,
                sample_rate=data["sample_rate"],
                quality_stats=data["quality_stats"],
            )

            logger.info(f"Loaded GSR dataset: {len(dataset.samples)} samples")
            return dataset

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to load GSR dataset: {e}")
            return None

    def get_session_status(self, session_id: str) -> Optional[Dict[str, Any]]:
        """Get status of GSR session"""
        if session_id in self.active_sessions:
            dataset = self.active_sessions[session_id]
            return {
                "status": "active",
                "device_id": dataset.device_id,
                "mode": dataset.mode.value,
                "samples_collected": len(dataset.samples),
                "duration": time.time() - dataset.start_time,
                "current_quality": dataset.quality_stats["mean"],
            }
        elif session_id in self.completed_sessions:
            dataset = self.completed_sessions[session_id]
            return {
                "status": "completed",
                "device_id": dataset.device_id,
                "mode": dataset.mode.value,
                "samples_collected": len(dataset.samples),
                "duration": dataset.end_time - dataset.start_time,
                "sample_rate": dataset.sample_rate,
                "quality_stats": dataset.quality_stats,
            }
        else:
            return None

    def get_active_sessions(self) -> List[str]:
        """Get list of active GSR session IDs"""
        return list(self.active_sessions.keys())

    def get_completed_sessions(self) -> List[str]:
        """Get list of completed GSR session IDs"""
        return list(self.completed_sessions.keys())
