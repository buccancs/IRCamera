
        super().__init__()

        self.max_points = max_points
        self.time_window = time_window

        # Data storage
        self.gsr_data: Dict[str, deque[Tuple[float, float]]] = (
            {}
        )  # device_id -> deque of (timestamp, gsr_value)
        self.plot_items: Dict[str, pg.PlotDataItem] = {}
        self.sync_markers: List[pg.InfiniteLine] = []

        self._setup_plot()

        self.update_timer = QTimer()
        self.update_timer.timeout.connect(self._update_plot)
        self.update_timer.start(50)  # 20fps update rate

    def _setup_plot(self) -> None:

        if device_id in self.gsr_data:
            return

        self.gsr_data[device_id] = deque(maxlen=self.max_points)

        # Auto-assign color if not specified
        if color is None:
            colors = ["cyan", "yellow", "magenta", "green", "red", "blue"]
            color_idx = len(self.plot_items) % len(colors)
            color = colors[color_idx]

        plot_item = self.plot(
            pen=pg.mkPen(color=color, width=2), name=f"GSR {device_id}"
        )
        self.plot_items[device_id] = plot_item

        logger.info(f"Added GSR device {device_id} with color {color}")

    def remove_device(self, device_id: str) -> None:

        if device_id not in self.gsr_data:
            self.add_device(device_id)

        # Convert timestamp to relative seconds
        current_time = time.time()
        relative_time = (timestamp_ns / 1e9) - current_time

        self.gsr_data[device_id].append((relative_time, gsr_microsiemens))

        self.data_updated.emit(relative_time, gsr_microsiemens)

    def add_sync_marker(
        self, timestamp_ns: int, label: str = "Sync", color: str = "white"
    ) -> None:

        current_time = time.time()
        relative_time = (timestamp_ns / 1e9) - current_time

        marker = pg.InfiniteLine(
            pos=relative_time,
            angle=90,
            pen=pg.mkPen(color=color, width=2, style=2),  # Dashed line
            label=label,
        )

        self.addItem(marker)
        self.sync_markers.append(marker)

        # Clean up old markers
        self._cleanup_old_markers()

    def _update_plot(self) -> None:

        super().__init__()

        self.device_id = device_id
        self.device_type = device_type

        # Frame statistics
        self.frame_count = 0
        self.last_fps_time = time.time()
        self.current_fps = 0.0

        self._setup_widget()

        # FPS calculation timer
        self.fps_timer = QTimer()
        self.fps_timer.timeout.connect(self._calculate_fps)
        self.fps_timer.start(1000)

    def _setup_widget(self) -> None:

        if frame_data is None or frame_data.size == 0:
            return

        try:
            # Convert numpy array to QPixmap
            if len(frame_data.shape) == 3:
                height, width, channels = frame_data.shape
                if channels == 3:
                    # BGR to RGB conversion for OpenCV frames
                    rgb_frame = frame_data[:, :, ::-1]
                    pixmap = QPixmap.fromImage(
                        pg.makeQImage(rgb_frame, transpose=False)
                    )
                else:
                    pixmap = QPixmap.fromImage(
                        pg.makeQImage(frame_data, transpose=False)
                    )
            else:
                # Grayscale image
                pixmap = QPixmap.fromImage(pg.makeQImage(frame_data, transpose=False))

            # Scale pixmap to fit widget while preserving aspect ratio
            scaled_pixmap = pixmap.scaled(
                self.size(),
                aspectRatioMode=1,  # KeepAspectRatio
                transformMode=1,  # SmoothTransformation
            )

            self.setPixmap(scaled_pixmap)

            self.frame_count += 1
            height, width = frame_data.shape[:2]
            self.frame_updated.emit(width, height)

        except Exception as e:
            logger.error(f"Error updating frame for {self.device_id}: {e}")

    def _calculate_fps(self) -> None:

        if device_id in self.video_widgets:
            return self.video_widgets[device_id]

        widget = VideoPreviewWidget(device_id, device_type)
        self.video_widgets[device_id] = widget

        self._add_video_widget_to_grid(widget)

        logger.info(f"Added video device {device_id} ({device_type})")
        return widget

    def remove_video_device(self, device_id: str) -> None:
        """Remove video device widget."""
        if device_id not in self.video_widgets:
            return

        widget = self.video_widgets[device_id]
        self.layout.removeWidget(widget)
        widget.deleteLater()

        del self.video_widgets[device_id]

        # Reorganize grid
        self._reorganize_video_grid()

        logger.info(f"Removed video device {device_id}")

    def _add_video_widget_to_grid(self, widget: VideoPreviewWidget) -> None:
        """Add video widget to the dynamic grid."""
        num_videos = len(self.video_widgets)

        # Calculate grid position (2x2 grid for videos on the right side)
        if num_videos <= 4:
            grid_row = (num_videos - 1) // 2
            grid_col = (num_videos - 1) % 2
            self.layout.addWidget(widget, grid_row, self.video_col + grid_col)
        else:
            # For more than 4 videos, stack vertically
            grid_row = num_videos - 1
            grid_col = 0
            self.layout.addWidget(widget, grid_row, self.video_col + grid_col)

    def _reorganize_video_grid(self) -> None:
        """Reorganize video widgets in the grid after removal."""

        for widget in self.video_widgets.values():
            self.layout.removeWidget(widget)

        # Re-add them in order
        for i, widget in enumerate(self.video_widgets.values()):
            grid_row = i // 2
            grid_col = i % 2
            self.layout.addWidget(widget, grid_row, self.video_col + grid_col)

    def add_sync_marker(self, timestamp_ns: int, label: str = "Sync") -> None:
        """Add sync marker to GSR plot."""
        if self.gsr_plot:
            self.gsr_plot.add_sync_marker(timestamp_ns, label)

    def clear_all_data(self) -> None:
        """Clear all data from dashboard."""
        if self.gsr_plot:
            self.gsr_plot.clear_data()

        for widget in self.video_widgets.values():
            widget.set_status_text("Cleared")

    def get_device_fps(self, device_id: str) -> float:
        """Get FPS for a video device."""
        if device_id in self.video_widgets:
            return self.video_widgets[device_id].get_fps()
        return 0.0

class DataAggregationWidget(QWidget):
    """
    Widget for displaying data aggregation statistics and synchronization quality.
    """

    def __init__(self) -> None:
        """Initialize data aggregation widget."""
        super().__init__()

        self.stats_labels: Dict[str, QLabel] = {}
        self._setup_layout()

    def _setup_layout(self) -> None:
        """Set up the widget layout."""
        layout = QVBoxLayout(self)

        stats = [
            "Total Devices",
            "GSR Devices",
            "Video Devices",
            "Sync Quality",
            "Data Rate (MB/s)",
            "Buffer Usage (%)",
            "Dropped Frames",
            "Last Sync (s ago)",
        ]

        for stat in stats:
            label = QLabel(f"{stat}: --")
            label.setStyleSheet("color: white; font-size: 12px; padding: 2px;")
            self.stats_labels[stat] = label
            layout.addWidget(label)

    def update_stats(self, stats: Dict[str, Any]) -> None:
        """Update aggregation statistics."""
        for stat_name, value in stats.items():
            if stat_name in self.stats_labels:
                if isinstance(value, float):
                    self.stats_labels[stat_name].setText(f"{stat_name}: {value:.2f}")
                else:
                    self.stats_labels[stat_name].setText(f"{stat_name}: {value}")

    def set_sync_quality(self, quality_percent: float) -> None:
        """Set synchronization quality indicator."""
        label = self.stats_labels.get("Sync Quality")
        if label:
            if quality_percent >= 95:
                color = "green"
            elif quality_percent >= 85:
                color = "yellow"
            else:
                color = "red"

            label.setText(f"Sync Quality: {quality_percent:.1f}%")
            label.setStyleSheet(
                f"color: {color}; font-size: 12px; padding: 2px; font-weight: bold;"
            )
