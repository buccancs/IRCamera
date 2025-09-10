#!/usr/bin/env python3
"""Camera calibration functionality for precise geometric corrections."""

from __future__ import annotations

import logging
import os
from typing import List, Optional, Tuple, Dict, Any
from pathlib import Path

import numpy as np

try:
    import cv2
    OPENCV_AVAILABLE = True
except ImportError:
    OPENCV_AVAILABLE = False

logger = logging.getLogger(__name__)


class CameraCalibrator:
    """Camera calibration using chessboard patterns."""

    def __init__(self, pattern_size: Tuple[int, int] = (9, 6), square_size: float = 1.0) -> None:
        """Initialize calibrator with chessboard parameters."""
        if not OPENCV_AVAILABLE:
            raise ImportError("OpenCV is required for camera calibration")

        self.pattern_size = pattern_size
        self.square_size = square_size

        # Generate 3D object points for the chessboard
        self.object_points_3d = np.zeros(
            (pattern_size[0] * pattern_size[1], 3), np.float32
        )
        self.object_points_3d[:, :2] = np.mgrid[
            0 : pattern_size[0], 0 : pattern_size[1]
        ].T.reshape(-1, 2)
        self.object_points_3d *= square_size

    def detect_corners(self, image: np.ndarray) -> Tuple[bool, Optional[np.ndarray]]:
        """Detect chessboard corners in image."""
        if not OPENCV_AVAILABLE:
            return False, None

        try:
            # Convert to grayscale if needed
            if len(image.shape) == 3:
                gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
            else:
                gray = image

            # Find chessboard corners
            success, corners = cv2.findChessboardCorners(
                gray,
                self.pattern_size,
                cv2.CALIB_CB_ADAPTIVE_THRESH
                + cv2.CALIB_CB_FAST_CHECK
                + cv2.CALIB_CB_NORMALIZE_IMAGE,
            )

            if success and corners is not None:
                # Refine corner positions
                criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 30, 0.001)
                corners = cv2.cornerSubPix(gray, corners, (11, 11), (-1, -1), criteria)

            return success, corners

        except Exception as e:
            logger.error(f"Corner detection failed: {e}")
            return False, None

    def calibrate_camera(
        self, 
        images: List[np.ndarray], 
        image_size: Optional[Tuple[int, int]] = None
    ) -> Tuple[bool, Dict[str, Any]]:
        """Perform camera calibration using multiple images."""
        if not OPENCV_AVAILABLE:
            return False, {}

        if not images:
            logger.error("No images provided for calibration")
            return False, {}

        # Collect object and image points
        object_points = []
        image_points = []
        
        if image_size is None:
            image_size = (images[0].shape[1], images[0].shape[0])

        valid_images = 0
        for img in images:
            success, corners = self.detect_corners(img)
            if success and corners is not None:
                object_points.append(self.object_points_3d)
                image_points.append(corners)
                valid_images += 1

        if valid_images < 3:
            logger.error(f"Insufficient valid images for calibration: {valid_images}")
            return False, {}

        try:
            # Perform calibration
            ret, camera_matrix, dist_coeffs, rvecs, tvecs = cv2.calibrateCamera(
                object_points, image_points, image_size, None, None
            )

            if ret:
                # Calculate reprojection error
                total_error = 0
                for i in range(len(object_points)):
                    projected_points, _ = cv2.projectPoints(
                        object_points[i], rvecs[i], tvecs[i], camera_matrix, dist_coeffs
                    )
                    error = cv2.norm(image_points[i], projected_points, cv2.NORM_L2) / len(projected_points)
                    total_error += error

                mean_error = total_error / len(object_points)

                calibration_data = {
                    "camera_matrix": camera_matrix,
                    "distortion_coefficients": dist_coeffs,
                    "rotation_vectors": rvecs,
                    "translation_vectors": tvecs,
                    "reprojection_error": mean_error,
                    "image_size": image_size,
                    "valid_images": valid_images,
                    "pattern_size": self.pattern_size,
                    "square_size": self.square_size
                }

                logger.info(f"Camera calibration successful with {valid_images} images")
                logger.info(f"Reprojection error: {mean_error:.4f}")
                
                return True, calibration_data

        except Exception as e:
            logger.error(f"Camera calibration failed: {e}")

        return False, {}

    def undistort_image(self, image: np.ndarray, calibration_data: Dict[str, Any]) -> Optional[np.ndarray]:
        """Undistort image using calibration data."""
        if not OPENCV_AVAILABLE:
            return None

        try:
            camera_matrix = calibration_data["camera_matrix"]
            dist_coeffs = calibration_data["distortion_coefficients"]
            
            undistorted = cv2.undistort(image, camera_matrix, dist_coeffs)
            return undistorted

        except Exception as e:
            logger.error(f"Image undistortion failed: {e}")
            return None

    def save_calibration(self, calibration_data: Dict[str, Any], filepath: Path) -> bool:
        """Save calibration data to file."""
        try:
            np.savez(
                filepath,
                camera_matrix=calibration_data["camera_matrix"],
                distortion_coefficients=calibration_data["distortion_coefficients"],
                reprojection_error=calibration_data["reprojection_error"],
                image_size=calibration_data["image_size"],
                valid_images=calibration_data["valid_images"],
                pattern_size=calibration_data["pattern_size"],
                square_size=calibration_data["square_size"]
            )
            logger.info(f"Calibration data saved to {filepath}")
            return True

        except Exception as e:
            logger.error(f"Failed to save calibration data: {e}")
            return False

    def load_calibration(self, filepath: Path) -> Optional[Dict[str, Any]]:
        """Load calibration data from file."""
        try:
            if not filepath.exists():
                logger.error(f"Calibration file not found: {filepath}")
                return None

            data = np.load(filepath)
            calibration_data = {
                "camera_matrix": data["camera_matrix"],
                "distortion_coefficients": data["distortion_coefficients"],
                "reprojection_error": float(data["reprojection_error"]),
                "image_size": tuple(data["image_size"]),
                "valid_images": int(data["valid_images"]),
                "pattern_size": tuple(data["pattern_size"]),
                "square_size": float(data["square_size"])
            }

            logger.info(f"Calibration data loaded from {filepath}")
            return calibration_data

        except Exception as e:
            logger.error(f"Failed to load calibration data: {e}")
            return None
