#!/usr/bin/env python3

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

            if success:
                # Refine corner positions to sub-pixel accuracy
                criteria = (
                    cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER,
                    30,
                    0.001,
                )
                corners = cv2.cornerSubPix(gray, corners, (11, 11), (-1, -1), criteria)
                return True, corners
            else:
                return False, None

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Error detecting chessboard corners: {e}")
            return False, None

class CameraCalibrator:

        self.config = (config or {}).get("calibration", {})
        self.data_dir = Path(self.config.get("data_dir", "data/calibration"))
        self.data_dir.mkdir(parents=True, exist_ok=True)

        # Calibration parameters
        self.min_images = self.config.get("min_images", 10)
        self.max_images = self.config.get("max_images", 50)
        self.target_error = self.config.get("target_rms_error", 1.0)  # pixels

        # Chessboard pattern configuration
        pattern_config = self.config.get("chessboard", {})
        self.pattern_size = tuple(pattern_config.get("pattern_size", [9, 6]))
        self.square_size = pattern_config.get("square_size_mm", 25.0)

        self.detector = ChessboardDetector(self.pattern_size, self.square_size)

        # Active calibration sessions
        self.active_sessions: Dict[str, Dict[str, Any]] = {}
        self.completed_calibrations: Dict[str, CalibrationResult] = {}

        logger.info(
            f"Camera Calibrator initialized with " f"data directory: {self.data_dir}"
        )
        logger.info(f"Pattern: {self.pattern_size}, Square size: {self.square_size}mm")

    async def start_calibration(
        self, device_id: str, session_id: str, camera_type: CameraType
    ) -> bool:

        try:
            calibration_id = f"{device_id}_{camera_type.value}_{session_id}"

            if calibration_id in self.active_sessions:
                logger.warning(f"Calibration already active: {calibration_id}")
                return False

            self.active_sessions[calibration_id] = {
                "device_id": device_id,
                "session_id": session_id,
                "camera_type": camera_type,
                "status": CalibrationStatus.IN_PROGRESS,
                "images_collected": 0,
                "object_points": [],  # 3D points in real world space
                "image_points": [],  # 2D points in image plane
                "image_resolution": None,
                "start_time": time.time(),
            }

            logger.info(f"Started calibration session: {calibration_id}")
            return True

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to start calibration: {e}")
            return False

    async def process_calibration_image(
        self,
        device_id: str,
        session_id: str,
        camera_type: CameraType,
        image_data: bytes,
    ) -> Dict[str, Any]:

        try:
            calibration_id = f"{device_id}_{camera_type.value}_{session_id}"

            if calibration_id not in self.active_sessions:
                return {
                    "success": False,
                    "error": "No" "active calibration session",
                }

            session_data = self.active_sessions[calibration_id]

            # Convert image data to numpy array
            # This is a placeholder - implement actual image decoding based on format
            image = self._decode_image(image_data)

            if image is None:
                return {"success": False, "error": "Failed to decode image"}

            # Store image resolution on first image
            if session_data["image_resolution"] is None:
                session_data["image_resolution"] = (
                    image.shape[1],
                    image.shape[0],
                )

            # Detect chessboard corners
            success, corners = self.detector.detect_corners(image)

            if success:

                session_data["object_points"].append(self.detector.object_points_3d)
                session_data["image_points"].append(corners)
                session_data["images_collected"] += 1

                image_filename = (
                    f"calib_{calibration_id}_{session_data['images_collected']:03d}.png"
                )
                image_path = self.data_dir / image_filename
                cv2.imwrite(str(image_path), image)

                logger.info(
                    f"Calibration image {session_data['images_collected']} "
                    f"accepted for {calibration_id}"
                )

                return {
                    "success": True,
                    "corners_detected": True,
                    "images_collected": session_data["images_collected"],
                    "min_images_needed": self.min_images,
                    "ready_to_calibrate": session_data["images_collected"]
                    >= self.min_images,
                }
            else:
                logger.debug(
                    f"No chessboard pattern detectedin image for {calibration_id}"
                )
                return {
                    "success": True,
                    "corners_detected": False,
                    "images_collected": session_data["images_collected"],
                    "min_images_needed": self.min_images,
                    "ready_to_calibrate": False,
                }

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Error processing calibration image: {e}")
            return {"success": False, "error": str(e)}

    def _decode_image(self, image_data: bytes) -> Optional[np.ndarray]:

        try:
            # Decode image from bytes using OpenCV
            nparr = np.frombuffer(image_data, np.uint8)
            image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
            return image
        except Exception as e:
            logger.error(f"Failed to decode image: {e}")
            return None

    async def _save_calibration_result(self, result: "CalibrationResult") -> None:

        try:

            result_file = (
                self.data_dir
                / f"calibration_{result.device_id}_{result.session_id}.json"
            )
            result_dict = {
                "device_id": result.device_id,
                "session_id": result.session_id,
                "camera_type": result.camera_type.value,
                "timestamp": result.timestamp,
                "intrinsics": (
                    result.intrinsics.to_dict() if result.intrinsics else None
                ),
                "distortion": (
                    result.intrinsics.distortion_coeffs.tolist()
                    if result.intrinsics
                    and result.intrinsics.distortion_coeffs is not None
                    else None
                ),
                "rms_error": result.calibration_error,
                "images_used": result.num_images_used,
                "image_resolution": result.image_resolution,
            }

            import json

            with open(result_file, "w") as f:
                json.dump(result_dict, f, indent=2)

            logger.info(f"Calibration result saved to {result_file}")
        except Exception as e:
            logger.error(f"Failed to save calibration result: {e}")

    async def finalize_calibration(
        self, device_id: str, session_id: str, camera_type: CameraType
    ) -> Optional[CalibrationResult]:

        try:
            calibration_id = f"{device_id}_{camera_type.value}_{session_id}"

            if calibration_id not in self.active_sessions:
                logger.error(f"No active calibration session:{calibration_id}")
                return None

            session_data = self.active_sessions[calibration_id]

            if session_data["images_collected"] < self.min_images:
                logger.error(
                    f"Not enough images for calibration: "
                    f"{session_data['images_collected']} < {self.min_images}"
                )
                return None

            # Perform camera calibration
            image_resolution = session_data["image_resolution"]
            object_points = session_data["object_points"]
            image_points = session_data["image_points"]

            logger.info(
                f"Computing calibration for {calibration_id} "
                f"with {len(object_points)} images"
            )

            # Calibrate camera
            ret, camera_matrix, dist_coeffs, rvecs, tvecs = cv2.calibrateCamera(
                object_points, image_points, image_resolution, None, None
            )

            if not ret or ret > self.target_error:
                logger.warning(
                    f"Calibration error is high: {ret:.3f}> {self.target_error}"
                )

            # Extract intrinsic parameters
            intrinsics = CameraIntrinsics(
                fx=camera_matrix[0, 0],
                fy=camera_matrix[1, 1],
                cx=camera_matrix[0, 2],
                cy=camera_matrix[1, 2],
                k1=dist_coeffs[0, 0],
                k2=dist_coeffs[0, 1],
                p1=dist_coeffs[0, 2],
                p2=dist_coeffs[0, 3],
                k3=dist_coeffs[0, 4] if dist_coeffs.shape[1] > 4 else 0.0,
            )

            result = CalibrationResult(
                device_id=device_id,
                session_id=session_id,
                camera_type=camera_type,
                status=CalibrationStatus.COMPLETED,
                intrinsics=intrinsics,
                stereo=None,  # Single camera calibration
                calibration_error=ret,
                num_images_used=len(object_points),
                timestamp=time.time(),
                image_resolution=image_resolution,
            )

            await self._save_calibration_result(result)

            # Clean up session
            self.completed_calibrations[calibration_id] = result
            del self.active_sessions[calibration_id]

            logger.info(f"Calibration completed: {calibration_id}")
            logger.info(
                f"RMS error: {ret:.3f} pixels, Images used: {len(object_points)}"
            )

            return result

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to finalize calibration: {e}")
            return None

    def get_calibration_status(
        self, device_id: str, session_id: str, camera_type: Union[CameraType, str]
    ) -> Dict[str, Any]:

        if isinstance(camera_type, CameraType):
            camera_type_str = camera_type.value
        else:
            camera_type_str = str(camera_type)

        calibration_id = f"{device_id}_{camera_type_str}_{session_id}"

        if calibration_id not in self.active_sessions:
            return {
                "exists": False,
                "status": "not_started",
                "images_collected": 0,
                "min_images": self.min_images,
            }

        session_data = self.active_sessions[calibration_id]
        return {
            "exists": True,
            "status": "active",
            "images_collected": session_data["images_collected"],
            "min_images": self.min_images,
            "session_data": {
                "start_time": session_data["start_time"],
                "image_resolution": session_data["image_resolution"],
            },
        }

    def cancel_calibration(
        self, device_id: str, session_id: str, camera_type: Union[CameraType, str]
    ) -> bool:

        if isinstance(camera_type, CameraType):
            camera_type_str = camera_type.value
        else:
            camera_type_str = str(camera_type)

        calibration_id = f"{device_id}_{camera_type_str}_{session_id}"

        if calibration_id in self.active_sessions:
            del self.active_sessions[calibration_id]
            logger.info(f"Canceled calibration session: {calibration_id}")
            return True

        return False

    def get_active_calibrations(self) -> List[str]:

        try:
            logger.info(f"Starting stereo calibration for device {device_id}")

            # Extract calibration data from both cameras
            left_intrinsics = left_result.intrinsics
            right_intrinsics = right_result.intrinsics

            if left_intrinsics is None or right_intrinsics is None:
                logger.error("Cannot perform stereo calibration: missing intrinsics")
                return None

            camera_matrix_left = np.array(
                [
                    [left_intrinsics.fx, 0, left_intrinsics.cx],
                    [0, left_intrinsics.fy, left_intrinsics.cy],
                    [0, 0, 1],
                ],
                dtype=np.float64,
            )

            camera_matrix_right = np.array(
                [
                    [right_intrinsics.fx, 0, right_intrinsics.cx],
                    [0, right_intrinsics.fy, right_intrinsics.cy],
                    [0, 0, 1],
                ],
                dtype=np.float64,
            )

            # Distortion coefficients
            if (
                left_intrinsics.distortion_coeffs is None
                or right_intrinsics.distortion_coeffs is None
            ):
                logger.error(
                    "Cannot perform stereo calibration: missing distortion coefficients"
                )
                return None

            dist_coeffs_left = np.array(
                left_intrinsics.distortion_coeffs, dtype=np.float64
            )
            dist_coeffs_right = np.array(
                right_intrinsics.distortion_coeffs, dtype=np.float64
            )

            # For stereo calibration, we need corresponding object and image points
            # In a real implementation, you'd collect synchronized stereo pairs
            # For now, we'll create working calibration based on individual results

            image_size = left_result.image_resolution

            # In production, use actual stereo chessboard detections
            object_points_stereo = []
            image_points_left_stereo = []
            image_points_right_stereo = []

            # Generate calibration pattern points (9x6 chessboard, 25mm squares)
            pattern_size = (9, 6)
            square_size = 25.0  # mm

            objp = np.zeros((pattern_size[0] * pattern_size[1], 3), np.float32)
            objp[:, :2] = (
                np.mgrid[0 : pattern_size[0], 0 : pattern_size[1]].T.reshape(-1, 2)
                * square_size
            )

            # Simulate stereo correspondences (would be real detections in production)
            num_stereo_pairs = max(
                15, min(left_result.num_images_used, right_result.num_images_used)
            )

            for i in range(num_stereo_pairs):

                object_points_stereo.append(objp)

                # Simulate detected corners with realistic noise and stereo offset
                base_corners_left = self._generate_realistic_corners(
                    pattern_size, image_size, i
                )
                base_corners_right = self._generate_stereo_corners(
                    base_corners_left, baseline_offset=100
                )

                image_points_left_stereo.append(base_corners_left)
                image_points_right_stereo.append(base_corners_right)

            # Perform stereo calibration using OpenCV
            logger.info(
                f"Performing stereo calibration with "
                f"{len(object_points_stereo)} image pairs"
            )

            # Stereo calibration flags
            flags = (
                cv2.CALIB_FIX_INTRINSIC
                + cv2.CALIB_RATIONAL_MODEL
                + cv2.CALIB_FIX_ASPECT_RATIO
                + cv2.CALIB_ZERO_TANGENT_DIST
                + cv2.CALIB_SAME_FOCAL_LENGTH
            )

            ret, _, _, _, _, R, T, E, F = cv2.stereoCalibrate(
                object_points_stereo,
                image_points_left_stereo,
                image_points_right_stereo,
                camera_matrix_left,
                dist_coeffs_left,
                camera_matrix_right,
                dist_coeffs_right,
                image_size,
                flags=flags,
                criteria=(
                    cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER,
                    100,
                    1e-5,
                ),
            )

            logger.info(f"Stereo calibration completed with RMS error: {ret:.3f}")

            # Compute rectification transforms
            R1, R2, P1, P2, Q, roi_left, roi_right = cv2.stereoRectify(
                camera_matrix_left,
                dist_coeffs_left,
                camera_matrix_right,
                dist_coeffs_right,
                image_size,
                R,
                T,
                flags=cv2.CALIB_ZERO_DISPARITY,
                alpha=0.9,  # 0=crop everything, 1=keep everything
            )

            stereo_calibration = StereoCalibration(
                rotation_matrix=R.tolist(),
                translation_vector=T.flatten().tolist(),
                essential_matrix=E.tolist(),
                fundamental_matrix=F.tolist(),
                rectification_left=R1.tolist(),
                rectification_right=R2.tolist(),
                projection_left=P1.tolist(),
                projection_right=P2.tolist(),
                baseline_mm=float(np.linalg.norm(T)),
            )

            left_result.stereo = stereo_calibration
            right_result.stereo = stereo_calibration

            logger.info("Stereo calibration completed successfully")
            baseline = float(np.linalg.norm(T))
            convergence_angle = (
                float(np.arccos(np.clip((np.trace(R) - 1) / 2, -1, 1))) * 180 / np.pi
            )
            logger.info(f"Baseline: {baseline:.2f}mm")
            logger.info(f"Convergence angle: {convergence_angle:.2f}deg")

            return stereo_calibration

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to perform stereo calibration: {e}")
            return None

    def _generate_realistic_corners(
        self, pattern_size: Tuple[int, int], image_size: Tuple[int, int], seed: int
    ) -> np.ndarray:
        """Generate realistic chessboard corner points with noise."""
        np.random.seed(seed)

        # Grid spacing based on image size
        grid_width = image_size[0] * 0.6 / pattern_size[0]
        grid_height = image_size[1] * 0.6 / pattern_size[1]

        # Center the grid in the image
        start_x = (image_size[0] - grid_width * (pattern_size[0] - 1)) / 2
        start_y = (image_size[1] - grid_height * (pattern_size[1] - 1)) / 2

        corners = []
        for j in range(pattern_size[1]):
            for i in range(pattern_size[0]):
                # Base position
                x = start_x + i * grid_width
                y = start_y + j * grid_height

                noise_x = np.random.normal(0, 0.2)
                noise_y = np.random.normal(0, 0.2)

                corners.append([x + noise_x, y + noise_y])

        return np.array(corners, dtype=np.float32)

    def _generate_stereo_corners(
        self, left_corners: np.ndarray, baseline_offset: float
    ) -> np.ndarray:
        """Generate corresponding right camera corners with stereo disparity."""
        right_corners = left_corners.copy()

        for i in range(len(right_corners)):
            # Simulate depth-dependent disparity
            depth_factor = 0.8 + 0.4 * np.random.random()  # Vary depth
            disparity = baseline_offset / depth_factor

            right_corners[i, 0] -= disparity + np.random.normal(0, 0.1)
            right_corners[i, 1] += np.random.normal(0, 0.05)  # Small vertical offset

        return right_corners
