#!/usr/bin/env python3
"""
File Transfer Manager for IRCamera PC Controller

Handles resumable file transfers from Android devices as per FR10 requirements.
Provides secure, reliable, and efficient data aggregation after recording sessions.
"""

import asyncio
import hashlib
import json
import time
from typing import Dict, List, Optional, Any, Callable
from pathlib import Path
from dataclasses import dataclass, asdict
from enum import Enum

from ..utils.simple_logger import get_logger

logger = get_logger(__name__)


class TransferStatus(Enum):
    """File transfer status states"""

    PENDING = "pending"
    IN_PROGRESS = "in_progress"
    PAUSED = "paused"
    COMPLETED = "completed"
    FAILED = "failed"
    CANCELLED = "cancelled"


class FileType(Enum):
    """Types of files transferred from devices"""

    THERMAL_VIDEO = "thermal_video"
    VISUAL_VIDEO = "visual_video"
    GSR_DATA = "gsr_data"
    IMU_DATA = "imu_data"
    AUDIO = "audio"
    METADATA = "metadata"
    CALIBRATION = "calibration"


@dataclass
class FileManifest:
    """File information from device"""

    file_id: str
    filename: str
    file_type: FileType
    size_bytes: int
    checksum: str  # SHA-256 hex digest
    device_id: str
    session_id: str
    timestamp: float
    compression: Optional[str] = None  # gzip, lz4, etc.

    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for JSON serialization"""
        data = asdict(self)
        data["file_type"] = self.file_type.value
        return data


@dataclass
class TransferJob:
    """Individual file transfer job"""

    job_id: str
    manifest: FileManifest
    local_path: Path
    status: TransferStatus
    bytes_transferred: int
    start_time: float
    end_time: Optional[float]
    resume_offset: int
    retry_count: int
    error_message: Optional[str]

    @property
    def progress_percent(self) -> float:
        """Calculate transfer progress percentage"""
        if self.manifest.size_bytes == 0:
            return 100.0
        return (self.bytes_transferred / self.manifest.size_bytes) * 100.0

    @property
    def transfer_rate(self) -> float:
        """Calculate transfer rate in bytes/second"""
        if self.status != TransferStatus.IN_PROGRESS or self.start_time == 0:
            return 0.0
        elapsed = time.time() - self.start_time
        if elapsed == 0:
            return 0.0
        return self.bytes_transferred / elapsed

    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for JSON serialization"""
        data = asdict(self)
        data["status"] = self.status.value
        data["manifest"] = self.manifest.to_dict()
        data["local_path"] = str(self.local_path)
        return data


class FileTransferManager:
    """
    Resumable File Transfer Manager

    Handles secure and efficient transfer of files from Android devices
    with support for resume, retry, and integrity verification.
    """

    def __init__(self, config: Dict[str, Any]):
        """
        Initialize File Transfer Manager

        Args:
            config: Configuration dictionary with transfer settings
        """
        self.config = config.get("file_transfer", {})
        self.data_dir = Path(self.config.get("data_dir", "data/transfers"))
        self.data_dir.mkdir(parents=True, exist_ok=True)

        # Transfer parameters
        self.chunk_size = self.config.get("chunk_size",
            1024 * 1024)  # 1MB chunks
        self.max_concurrent = self.config.get("max_concurrent_transfers", 4)
        self.retry_limit = self.config.get("retry_limit", 3)
        self.timeout = self.config.get("timeout_seconds", 300)  # 5 minutes
        self.verify_checksums = self.config.get("verify_checksums", True)

        # Active transfers and state
        self.active_jobs: Dict[str, TransferJob] = {}
        self.completed_jobs: Dict[str, TransferJob] = {}
        self.transfer_queue: List[str] = []
        self.concurrent_transfers = 0

        # Callbacks for progress updates
        self.progress_callbacks: List[Callable[[str, float, float], None]] = []

        logger.info(
            f"File Transfer Manager initialized withdata directory: {self.data_dir}"
        )
        logger.info(
            f"Chunk size: {self.chunk_size} bytes, Maxconcurrent: {self.max_concurrent}"
        )

    def add_progress_callback(self,
        callback: Callable[[str,
        float,
        float],
        None]):
        """
        Add callback for transfer progress updates

        Args:
            callback: Function(job_id, progress_percent, transfer_rate)
        """
        self.progress_callbacks.append(callback)

    async def queue_transfer(self,
        manifest: FileManifest,
        device_conn: Any) -> str:
        """
        Queue a file for transfer

        Args:
            manifest: File information from device
            device_conn: Connection to source device

        Returns:
            Transfer job ID
        """
        try:
            # Generate unique job ID
            job_id = f"transfer_{manifest.device_id}_{manifest.session_id}_{int(time.time())}"

            # Determine local file path
            session_dir = self.data_dir / manifest.session_id
            device_dir = session_dir / manifest.device_id
            device_dir.mkdir(parents=True, exist_ok=True)

            local_path = device_dir / manifest.filename

            # Check if file already exists and is complete
            if local_path.exists():
                if await self._verify_existing_file(local_path, manifest):
                    logger.info(
                        f"File already exists and verified:{manifest.filename}"
                    )
                    return job_id  # Skip transfer

            # Create transfer job
            job = TransferJob(
                job_id=job_id,
                manifest=manifest,
                local_path=local_path,
                status=TransferStatus.PENDING,
                bytes_transferred=0,
                start_time=0.0,
                end_time=None,
                resume_offset=0,
                retry_count=0,
                error_message=None,
            )

            # Check for partial file to resume
            if local_path.exists():
                job.resume_offset = local_path.stat().st_size
                job.bytes_transferred = job.resume_offset
                logger.info(
                    f"Found partial file, will resume fromoffset: {job.resume_offset}"
                )

            self.active_jobs[job_id] = job
            self.transfer_queue.append(job_id)

            logger.info(
                f"Queued transfer: {manifest.filename}({manifest.size_bytes} bytes)"
            )

            # Start transfer if we have capacity
            if self.concurrent_transfers < self.max_concurrent:
                await self._start_next_transfer()

            return job_id

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to queue transfer for{manifest.filename}: {e}")
            raise

    async def cancel_transfer(self, job_id: str) -> bool:
        """
        Cancel an active or queued transfer

        Args:
            job_id: Transfer job identifier

        Returns:
            True if cancelled successfully
        """
        try:
            if job_id in self.active_jobs:
                job = self.active_jobs[job_id]
                job.status = TransferStatus.CANCELLED
                job.end_time = time.time()

                # Remove from queue if pending
                if job_id in self.transfer_queue:
                    self.transfer_queue.remove(job_id)

                logger.info(f"Cancelled transfer: {job.manifest.filename}")
                return True

            return False

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to cancel transfer {job_id}: {e}")
            return False

    async def pause_transfer(self, job_id: str) -> bool:
        """Pause an active transfer"""
        try:
            if job_id in self.active_jobs:
                job = self.active_jobs[job_id]
                if job.status == TransferStatus.IN_PROGRESS:
                    job.status = TransferStatus.PAUSED
                    logger.info(f"Paused transfer: {job.manifest.filename}")
                    return True
            return False
        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to pause transfer {job_id}: {e}")
            return False

    async def resume_transfer(self, job_id: str) -> bool:
        """Resume a paused transfer"""
        try:
            if job_id in self.active_jobs:
                job = self.active_jobs[job_id]
                if job.status == TransferStatus.PAUSED:
                    job.status = TransferStatus.PENDING
                    if job_id not in self.transfer_queue:
                        self.transfer_queue.append(job_id)

                    # Start if we have capacity
                    if self.concurrent_transfers < self.max_concurrent:
                        await self._start_next_transfer()

                    logger.info(f"Resumed transfer: {job.manifest.filename}")
                    return True
            return False
        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to resume transfer {job_id}: {e}")
            return False

    async def _start_next_transfer(self):
        """Start the next queued transfer"""
        if not self.transfer_queue or self.concurrent_transfers >= self.max_concurrent:
            return

        job_id = self.transfer_queue.pop(0)
        if job_id not in self.active_jobs:
            return

        job = self.active_jobs[job_id]
        if job.status != TransferStatus.PENDING:
            return

        # Start the transfer
        self.concurrent_transfers += 1
        asyncio.create_task(self._execute_transfer(job))

    async def _execute_transfer(self, job: TransferJob):
        """Execute the actual file transfer"""
        try:
            job.status = TransferStatus.IN_PROGRESS
            job.start_time = time.time()

            logger.info(f"Starting transfer: {job.manifest.filename}")

            # Simulate file transfer (replace with actual network transfer)
            await self._transfer_file_chunks(job)

            # Verify file integrity if enabled
            if self.verify_checksums:
                if not await self._verify_file_integrity(job):
                    raise Exception("File integrity verification failed")

            # Mark as completed
            job.status = TransferStatus.COMPLETED
            job.end_time = time.time()
            job.bytes_transferred = job.manifest.size_bytes

            # Move to completed jobs
            self.completed_jobs[job.job_id] = job
            del self.active_jobs[job.job_id]

            duration = job.end_time - job.start_time
            rate = job.manifest.size_bytes / duration if duration > 0 else 0

            logger.info(f"Transfer completed: {job.manifest.filename}")
            logger.info(
                f"Size: {job.manifest.size_bytes} bytes, "
                f"Duration: {duration:.2f}s, "
                f"Rate: {rate/1024/1024:.2f} MB/s"
            )

        except (OSError, ValueError, RuntimeError) as e:
            job.status = TransferStatus.FAILED
            job.end_time = time.time()
            job.error_message = str(e)
            job.retry_count += 1

            logger.error(f"Transfer failed: {job.manifest.filename} - {e}")

            # Retry if under limit
            if job.retry_count <= self.retry_limit:
                logger.info(
                    f"Retrying transfer (attempt {job.retry_count}/{self.retry_limit})"
                )
                job.status = TransferStatus.PENDING
                job.error_message = None
                self.transfer_queue.append(job.job_id)
            else:
                logger.error(
                    f"Transfer permanently failed after{job.retry_count} attempts"
                )

        finally:
            self.concurrent_transfers -= 1
            # Start next transfer if available
            if self.transfer_queue:
                await self._start_next_transfer()

    async def _transfer_file_chunks(self, job: TransferJob):
        """Transfer file in chunks with progress updates"""
        try:
            # Open local file for writing
            mode = "ab" if job.resume_offset > 0 else "wb"

            with open(job.local_path, mode) as f:
                bytes_remaining = job.manifest.size_bytes - job.resume_offset
                bytes_transferred = job.resume_offset

                while bytes_remaining > 0:
                    if job.status != TransferStatus.IN_PROGRESS:
                        break  # Transfer was paused or cancelled

                    # Determine chunk size for this iteration
                    chunk_size = min(self.chunk_size, bytes_remaining)

                    # Simulate reading chunk from device (replace with actual network read)
                    chunk_data = await self._read_chunk_from_device(
                        job, bytes_transferred, chunk_size
                    )

                    # Write chunk to local file
                    f.write(chunk_data)
                    f.flush()

                    bytes_transferred += len(chunk_data)
                    bytes_remaining -= len(chunk_data)
                    job.bytes_transferred = bytes_transferred

                    # Update progress
                    await self._update_progress(job)

                    # Small delay to prevent overwhelming the system
                    await asyncio.sleep(0.001)

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Error during chunk transfer: {e}")
            raise

    async def _read_chunk_from_device(
        self, job: TransferJob, offset: int, size: int
    ) -> bytes:
        """
        Read a chunk of data from the device

        This is a placeholder - implement actual network communication
        """
        # Simulate network delay and data
        await asyncio.sleep(0.01)  # Simulate network latency

        # Return dummy data for now (replace with actual device communication)
        return b"x" * size

    async def _update_progress(self, job: TransferJob):
        """Update transfer progress and notify callbacks"""
        progress = job.progress_percent
        rate = job.transfer_rate

        # Notify all registered callbacks
        for callback in self.progress_callbacks:
            try:
                callback(job.job_id, progress, rate)
            except (OSError, ValueError, RuntimeError) as e:
                logger.error(f"Error in progress callback: {e}")

    async def _verify_file_integrity(self, job: TransferJob) -> bool:
        """Verify transferred file integrity using checksum"""
        try:
            logger.info(f"Verifying file integrity: {job.manifest.filename}")

            # Calculate SHA-256 checksum of local file
            hash_sha256 = hashlib.sha256()
            with open(job.local_path, "rb") as f:
                for chunk in iter(lambda: f.read(self.chunk_size), b""):
                    hash_sha256.update(chunk)

            local_checksum = hash_sha256.hexdigest()
            expected_checksum = job.manifest.checksum

            if local_checksum == expected_checksum:
                logger.info(f"File integrity verified:{job.manifest.filename}")
                return True
            else:
                logger.error(f"Checksum mismatch for {job.manifest.filename}")
                logger.error(f"Expected: {expected_checksum}")
                logger.error(f"Actual:   {local_checksum}")
                return False

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Error verifying file integrity: {e}")
            return False

    async def _verify_existing_file(
        self, filepath: Path, manifest: FileManifest
    ) -> bool:
        """Verify that an existing file matches the expected manifest"""
        try:
            if not filepath.exists():
                return False

            # Check file size
            file_size = filepath.stat().st_size
            if file_size != manifest.size_bytes:
                return False

            # Verify checksum if enabled
            if self.verify_checksums:
                hash_sha256 = hashlib.sha256()
                with open(filepath, "rb") as f:
                    for chunk in iter(lambda: f.read(self.chunk_size), b""):
                        hash_sha256.update(chunk)

                local_checksum = hash_sha256.hexdigest()
                return local_checksum == manifest.checksum

            return True

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Error verifying existing file: {e}")
            return False

    def get_transfer_status(self, job_id: str) -> Optional[Dict[str, Any]]:
        """Get status of a transfer job"""
        if job_id in self.active_jobs:
            job = self.active_jobs[job_id]
            return {
                "job_id": job_id,
                "filename": job.manifest.filename,
                "status": job.status.value,
                "progress_percent": job.progress_percent,
                "bytes_transferred": job.bytes_transferred,
                "total_bytes": job.manifest.size_bytes,
                "transfer_rate": job.transfer_rate,
                "retry_count": job.retry_count,
                "error_message": job.error_message,
            }
        elif job_id in self.completed_jobs:
            job = self.completed_jobs[job_id]
            return {
                "job_id": job_id,
                "filename": job.manifest.filename,
                "status": job.status.value,
                "progress_percent": 100.0,
                "bytes_transferred": job.bytes_transferred,
                "total_bytes": job.manifest.size_bytes,
                "duration": job.end_time - job.start_time if job.end_time else 0,
                "error_message": job.error_message,
            }
        else:
            return None

    def get_active_transfers(self) -> List[Dict[str, Any]]:
        """Get list of all active transfer statuses"""
        return [self.get_transfer_status(job_id) for job_id in self.active_jobs.keys()]

    def get_transfer_summary(self) -> Dict[str, Any]:
        """Get overall transfer manager status"""
        return {
            "active_transfers": len(self.active_jobs),
            "queued_transfers": len(self.transfer_queue),
            "completed_transfers": len(self.completed_jobs),
            "concurrent_capacity": f"{self.concurrent_trans}fers}/{self.max_concurrent}",
            "data_directory": str(self.data_dir),
        }

    async def save_job_state(self):
        """Save transfer job states to disk for recovery"""
        try:
            state_file = self.data_dir / "transfer_state.json"

            state = {
                "active_jobs": {
                    job_id: job.to_dict() for job_id,
                        job in self.active_jobs.items()
                },
                "completed_jobs": {
                    job_id: job.to_dict() for job_id,
                        job in self.completed_jobs.items()
                },
                "transfer_queue": self.transfer_queue,
            }

            with open(state_file, "w") as f:
                json.dump(state, f, indent=2)

            logger.info("Transfer job state saved")

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to save transfer state: {e}")

    async def load_job_state(self):
        """Load transfer job states from disk for recovery"""
        try:
            state_file = self.data_dir / "transfer_state.json"

            if not state_file.exists():
                logger.info("No transfer state file found")
                return

            with open(state_file, "r") as f:
                json.load(f)

            # TODO: Implement state reconstruction from saved data
            # This would involve recreating TransferJob objects and resuming transfers

            logger.info("Transfer job state loaded")

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to load transfer state: {e}")
