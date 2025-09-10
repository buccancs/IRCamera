#!/usr/bin/env python3

import asyncio
import hashlib
import json
import time
from dataclasses import asdict, dataclass
from enum import Enum
from pathlib import Path
from typing import Any, Callable, Dict, List, Optional

from loguru import logger

from .config import config

class TransferStatus(Enum):
    """Transfer job status enumeration."""
    PENDING = "pending"
    IN_PROGRESS = "in_progress" 
    PAUSED = "paused"
    COMPLETED = "completed"
    FAILED = "failed"
    CANCELLED = "cancelled"


class FileType(Enum):
    """File type enumeration."""
    RGB_VIDEO = "rgb_video"
    RGB_IMAGES = "rgb_images"
    THERMAL_DATA = "thermal_data"
    GSR_DATA = "gsr_data"
    METADATA = "metadata"
    LOG = "log"


@dataclass
class FileManifest:
    """File manifest for transfer operations."""
    file_id: str
    filename: str
    size_bytes: int
    checksum: str
    file_type: FileType
    device_id: str
    session_id: str
    timestamp: float


@dataclass
class TransferJob:
    """Transfer job tracking data."""
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
    device_connection: Optional[Any] = None

    @property
    def progress_percent(self) -> float:
        """Calculate transfer progress percentage."""
        if self.manifest.size_bytes == 0:
            return 100.0
        return (self.bytes_transferred / self.manifest.size_bytes) * 100.0

    @property
    def transfer_rate(self) -> float:
        """Calculate transfer rate in bytes per second."""
        if self.start_time == 0 or self.status != TransferStatus.IN_PROGRESS:
            return 0.0
        duration = time.time() - self.start_time
        if duration <= 0:
            return 0.0
        return self.bytes_transferred / duration

    def to_dict(self) -> Dict[str, Any]:
        """Convert job to dictionary for serialization."""
        return {
            "job_id": self.job_id,
            "manifest": asdict(self.manifest),
            "local_path": str(self.local_path),
            "status": self.status.value,
            "bytes_transferred": self.bytes_transferred,
            "start_time": self.start_time,
            "end_time": self.end_time,
            "resume_offset": self.resume_offset,
            "retry_count": self.retry_count,
            "error_message": self.error_message,
        }


class FileTransferManager:
    """Manages file transfers between devices and PC."""

    def __init__(self, config: Any) -> None:
        """Initialize file transfer manager."""
        self.config = config.get("file_transfer", {})
        self.data_dir = Path(self.config.get("data_dir", "data/transfers"))
        self.data_dir.mkdir(parents=True, exist_ok=True)

        # Transfer parameters
        self.chunk_size = self.config.get("chunk_size", 1024 * 1024)  # 1MB chunks
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

    def add_progress_callback(self, callback: Callable[[str, float, float], None]) -> None:
        """Add progress callback for transfer updates."""
        self.progress_callbacks.append(callback)

    async def queue_transfer(self, manifest: FileManifest, device_conn: Any) -> str:
        """Queue a file transfer job."""
        try:
            # Generate unique job ID
            job_id = (
                f"transfer_{manifest.device_id}_{manifest.session_id}_"
                f"{int(time.time())}"
            )

            # Determine local file path
            session_dir = self.data_dir / manifest.session_id
            device_dir = session_dir / manifest.device_id
            device_dir.mkdir(parents=True, exist_ok=True)

            local_path = device_dir / manifest.filename

            if local_path.exists():
                if await self._verify_existing_file(local_path, manifest):
                    logger.info(f"File already exists and verified:{manifest.filename}")
                    return job_id  # Skip transfer

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
                # Store device connection for real transfer
                device_connection=device_conn,
            )

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

            if self.concurrent_transfers < self.max_concurrent:
                await self._start_next_transfer()

            return job_id

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to queue transfer for{manifest.filename}: {e}")
            raise

    async def cancel_transfer(self, job_id: str) -> bool:
        """Cancel an active transfer job."""
        try:
            if job_id in self.active_jobs:
                job = self.active_jobs[job_id]
                job.status = TransferStatus.CANCELLED
                job.end_time = time.time()

                if job_id in self.transfer_queue:
                    self.transfer_queue.remove(job_id)

                logger.info(f"Cancelled transfer: {job.manifest.filename}")
                return True

            return False

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to cancel transfer {job_id}: {e}")
            return False

    async def pause_transfer(self, job_id: str) -> bool:
        """Pause an active transfer job."""
        try:
            if job_id in self.active_jobs:
                job = self.active_jobs[job_id]
                if job.status == TransferStatus.IN_PROGRESS:
                    job.status = TransferStatus.PAUSED
                    logger.info(f"Paused transfer: {job.manifest.filename}")
                    return True
            return False
        except Exception as e:
            logger.error(f"Failed to pause transfer {job_id}: {e}")
            return False

    async def _read_file_chunk(self, job: "TransferJob", offset: int, size: int) -> bytes:
        """Read file chunk from device."""
        try:
            # Real network communication to read file chunk from Android device
            device_conn = job.device_connection
            if device_conn is not None and hasattr(device_conn, "read_file_chunk"):
                # Use device connection's file reading method
                result = await device_conn.read_file_chunk(
                    job.manifest.filename, offset, size
                )
                return bytes(result)
            else:
                # Fallback: use TCP socket communication with Android device
                request_data = {
                    "type": "read_file_chunk",
                    "file_path": job.manifest.filename,
                    "offset": offset,
                    "size": size,
                    "session_id": job.manifest.session_id,
                }

                # Send request to Android device
                response = await self._send_device_request(device_conn, request_data)

                if response and response.get("status") == "success":
                    # Decode base64 data or get binary data
                    chunk_data = response.get("data", b"")
                    if isinstance(chunk_data, str):
                        import base64

                        chunk_data = base64.b64decode(chunk_data)
                    return bytes(chunk_data)
                else:
                    raise Exception(
                        f"Device read failed: {response.get('error', 'Unknown error')}"
                    )

        except Exception as e:
            logger.error(f"Failed to read chunk from device: {e}")
            raise

    async def _send_device_request(self, device_conn: Any, request_data: dict) -> dict:
        """Send request to device and get response."""
        try:
            import json

            # Convert request to JSON
            request_json = json.dumps(request_data)

            # Send via device connection
            if hasattr(device_conn, "send_json"):
                result = await device_conn.send_json(request_data)
                return dict(result)
            elif hasattr(device_conn, "writer"):
                # Direct socket communication
                device_conn.writer.write(request_json.encode("utf-8"))
                await device_conn.writer.drain()

                # Read response
                response_data = await device_conn.reader.read(65536)
                response_json = response_data.decode("utf-8")
                return dict(json.loads(response_json))
            else:
                # Fallback error
                raise Exception("No valid device communication method available")

        except Exception as e:
            logger.error(f"Device request failed: {e}")
            return {"status": "error", "error": str(e)}

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

    async def _start_next_transfer(self) -> None:
        """Start the next queued transfer if possible."""
        if not self.transfer_queue or self.concurrent_transfers >= self.max_concurrent:
            return

        job_id = self.transfer_queue.pop(0)
        if job_id in self.active_jobs:
            job = self.active_jobs[job_id]
            job.status = TransferStatus.IN_PROGRESS
            job.start_time = time.time()
            self.concurrent_transfers += 1
            
            # Start transfer in background task
            asyncio.create_task(self._perform_transfer(job))

    async def _perform_transfer(self, job: TransferJob) -> None:
        """Perform the actual file transfer."""
        try:
            # Simplified transfer logic
            remaining = job.manifest.size_bytes - job.resume_offset
            while remaining > 0 and job.status == TransferStatus.IN_PROGRESS:
                chunk_size = min(self.chunk_size, remaining)
                chunk_data = await self._read_file_chunk(job, job.resume_offset, chunk_size)
                
                # Write chunk to local file
                with open(job.local_path, "ab") as f:
                    f.write(chunk_data)
                
                job.bytes_transferred += len(chunk_data)
                job.resume_offset += len(chunk_data)
                remaining -= len(chunk_data)
                
                await self._update_progress(job)
            
            if remaining == 0:
                job.status = TransferStatus.COMPLETED
                job.end_time = time.time()
                if self.verify_checksums:
                    await self._verify_file_integrity(job)
                
                # Move to completed jobs
                self.completed_jobs[job.job_id] = job
                del self.active_jobs[job.job_id]
                
            self.concurrent_transfers -= 1
            await self._start_next_transfer()  # Start next queued transfer
            
        except Exception as e:
            job.status = TransferStatus.FAILED
            job.error_message = str(e)
            job.end_time = time.time()
            self.concurrent_transfers -= 1
            logger.error(f"Transfer failed for {job.manifest.filename}: {e}")

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
                "duration": (job.end_time - job.start_time if job.end_time else 0),
                "error_message": job.error_message,
            }
        else:
            return None

    def get_active_transfers(self) -> List[Dict[str, Any]]:
        """Get list of all active transfer statuses"""
        return [
            status
            for job_id in self.active_jobs.keys()
            if (status := self.get_transfer_status(job_id)) is not None
        ]

    def get_transfer_summary(self) -> Dict[str, Any]:
        """Get overall transfer manager status"""
        return {
            "active_transfers": len(self.active_jobs),
            "queued_transfers": len(self.transfer_queue),
            "completed_transfers": len(self.completed_jobs),
            "concurrent_capacity": f"{self.concurrent_transfers}/{self.max_concurrent}",
            "data_directory": str(self.data_dir),
        }

    async def save_job_state(self):
        """Save transfer job states to disk for recovery"""
        try:
            state_file = self.data_dir / "transfer_state.json"

            state = {
                "active_jobs": {
                    job_id: job.to_dict() for job_id, job in self.active_jobs.items()
                },
                "completed_jobs": {
                    job_id: job.to_dict() for job_id, job in self.completed_jobs.items()
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
                state_data = json.load(f)

            reconstructed_jobs = self._restore_jobs_from_state(state_data)
            self._restore_transfer_queue(state_data)

            logger.info(
                f"Transfer state loaded: {reconstructed_jobs} jobs reconstructed, "
                f"{len(self.transfer_queue)} queued for processing"
            )

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to load transfer state: {e}")

    def _restore_jobs_from_state(self, state_data: Dict) -> int:
        """Restore transfer jobs from state data"""
        reconstructed_jobs = 0
        for job_id, job_data in state_data.get("active_jobs", {}).items():
            try:
                job = self._recreate_transfer_job(job_id, job_data)
                if self._should_restore_job(job):
                    self._restore_job_state(job)
                    self.active_jobs[job_id] = job
                    reconstructed_jobs += 1
            except Exception as e:
                logger.warning(f"Failed to restore transfer job {job_id}: {e}")
        return reconstructed_jobs

    def _recreate_transfer_job(self, job_id: str, job_data: Dict) -> TransferJob:
        """Recreate a TransferJob from saved data"""
        manifest = self._recreate_file_manifest(job_data.get("manifest", {}))
        return TransferJob(
            job_id=job_id,
            manifest=manifest,
            local_path=Path(job_data.get("local_path", "")),
            status=TransferStatus(job_data.get("status", "pending")),
            bytes_transferred=job_data.get("bytes_transferred", 0),
            start_time=job_data.get("start_time", 0.0),
            end_time=job_data.get("end_time"),
            resume_offset=job_data.get("resume_offset", 0),
            retry_count=job_data.get("retry_count", 0),
            error_message=job_data.get("error_message"),
        )

    def _recreate_file_manifest(self, manifest_data: Dict) -> FileManifest:
        """Recreate a FileManifest from saved data"""
        return FileManifest(
            file_id=manifest_data.get("file_id", ""),
            filename=manifest_data.get("filename", ""),
            size_bytes=manifest_data.get("size_bytes", 0),
            checksum=manifest_data.get("checksum", ""),
            file_type=FileType(manifest_data.get("file_type", "metadata")),
            device_id=manifest_data.get("device_id", ""),
            session_id=manifest_data.get("session_id", ""),
            timestamp=manifest_data.get("timestamp", 0.0),
        )

    def _should_restore_job(self, job: TransferJob) -> bool:
        """Check if job should be restored"""
        return job.status in [
            TransferStatus.PENDING,
            TransferStatus.IN_PROGRESS,
            TransferStatus.PAUSED,
        ]

    def _restore_job_state(self, job: TransferJob):
        """Restore job state based on local file"""
        if job.local_path.exists():
            self._restore_existing_file_job(job)
        else:
            self._restore_missing_file_job(job)

    def _restore_existing_file_job(self, job: TransferJob):
        """Restore job with existing local file"""
        actual_size = job.local_path.stat().st_size
        job.bytes_transferred = actual_size
        job.resume_offset = actual_size

        if actual_size >= job.manifest.size_bytes:
            job.status = TransferStatus.COMPLETED
            logger.info(f"Restored completed transfer: {job.manifest.filename}")
        else:
            job.status = TransferStatus.PAUSED
            self.transfer_queue.append(job.job_id)
            logger.info(
                f"Restored paused transfer: {job.manifest.filename} "
                f"({actual_size}/{job.manifest.size_bytes} bytes)"
            )

    def _restore_missing_file_job(self, job: TransferJob):
        """Restore job with missing local file"""
        job.bytes_transferred = 0
        job.resume_offset = 0
        job.status = TransferStatus.PENDING
        self.transfer_queue.append(job.job_id)
        logger.info(f"Restored pending transfer: {job.manifest.filename}")

    def _restore_transfer_queue(self, state_data: Dict):
        """Restore transfer queue from saved state"""
        saved_queue = state_data.get("transfer_queue", [])
        for job_id in saved_queue:
            if job_id in self.active_jobs and job_id not in self.transfer_queue:
                self.transfer_queue.append(job_id)
