"""
Real-time plotting widgets for multi-modal sensor data visualization

Implements PyQtGraph-based widgets for live GSR, thermal, and video display
according to the PC Controller GUI requirements.
"""

import time
from collections import deque
from typing import Any, Dict, List, Optional, Tuple

import numpy as np
import pyqtgraph as pg
from PyQt6.QtCore import QTimer, pyqtSignal, QThread, QMutex, QMutexLocker
from PyQt6.QtGui import QPixmap, QImage
from PyQt6.QtWidgets import QGridLayout, QLabel, QVBoxLayout, QWidget, QHBoxLayout, QPushButton


class GSRPlotWidget(pg.PlotWidget):
    """
    Real-time GSR data plotting widget with comprehensive visualization features.
    
    Features:
    - High-frequency GSR data plotting (128Hz+)
    - Automatic scaling and windowing
    - Multiple GSR sensor support
    - Sync event markers
    - Data quality indicators
    - Statistical overlays
    - Export capabilities
    """

    data_updated = pyqtSignal(float, float)  # timestamp, gsr_value
    quality_changed = pyqtSignal(str, float)  # device_id, quality_percentage
    
    def __init__(self, max_points: int = 10000, time_window: float = 30.0):
        """
        Initialize GSR plot widget.

        Args:
            max_points: Maximum number of data points to display
            time_window: Time window in seconds for display
        """
        super().__init__()

        self.max_points = max_points
        self.time_window = time_window
        
        # Data storage with thread safety
        self.data_mutex = QMutex()
        self.gsr_data: Dict[str, deque] = {}  # device_id -> deque of (timestamp, gsr_value)
        self.plot_items: Dict[str, pg.PlotDataItem] = {}
        self.sync_markers: List[pg.InfiniteLine] = []
        self.quality_indicators: Dict[str, pg.TextItem] = {}
        
        # Statistical data
        self.statistics: Dict[str, Dict[str, float]] = {}  # device_id -> stats
        
        self._setup_plot()
        
        # Update timer with higher frequency for smooth visualization
        self.update_timer = QTimer()
        self.update_timer.timeout.connect(self._update_plot)
        self.update_timer.start(50)  # 20fps update rate
        
        # Data processing timer for statistics
        self.stats_timer = QTimer()
        self.stats_timer.timeout.connect(self._update_statistics)
        self.stats_timer.start(1000)  # 1Hz for statistics
        
    def _setup_plot(self) -> None:
        """Set up the plot appearance and configuration."""
        self.setLabel('left', 'GSR (µS)', color='white', size='12pt')
        self.setLabel('bottom', 'Time (s)', color='white', size='12pt')
        self.setTitle('Real-time GSR Data Visualization', color='white', size='14pt')
        
        # Configure plot appearance
        self.showGrid(x=True, y=True, alpha=0.3)
        self.setBackground('black')
        
        # Enable auto-range with limits
        self.enableAutoRange(axis='y')
        self.setXRange(-self.time_window, 0)
        self.setYRange(0, 50)  # Typical GSR range in µS
        
        # Add legend
        self.addLegend(offset=(10, 10))
        
        # Add crosshair cursor
        self.crosshair_v = pg.InfiniteLine(angle=90, movable=False, pen=pg.mkPen('yellow', width=1, style=2))
        self.crosshair_h = pg.InfiniteLine(angle=0, movable=False, pen=pg.mkPen('yellow', width=1, style=2))
        self.addItem(self.crosshair_v, ignoreBounds=True)
        self.addItem(self.crosshair_h, ignoreBounds=True)
        
        # Mouse tracking
        self.scene().sigMouseMoved.connect(self._mouse_moved)
        
    def add_device(self, device_id: str, color: str = None) -> None:
        """
        Add a new GSR device for plotting.

        Args:
            device_id: Unique device identifier
            color: Plot line color (auto-assigned if None)
        """
        with QMutexLocker(self.data_mutex):
            if device_id in self.gsr_data:
                return
                
            self.gsr_data[device_id] = deque(maxlen=self.max_points)
            self.statistics[device_id] = {
                'mean': 0.0, 'std': 0.0, 'min': 0.0, 'max': 0.0,
                'trend': 0.0, 'quality': 100.0
            }
            
            # Auto-assign color if not specified
            if color is None:
                colors = ['cyan', 'yellow', 'magenta', 'green', 'red', 'blue', 'orange', 'purple']
                color_idx = len(self.plot_items) % len(colors)
                color = colors[color_idx]
            
            # Create plot item with enhanced styling
            plot_item = self.plot(
                pen=pg.mkPen(color=color, width=2),
                name=f'GSR {device_id}',
                symbol='o',
                symbolSize=3,
                symbolBrush=color,
                symbolPen=None
            )
            self.plot_items[device_id] = plot_item
            
            # Add quality indicator text
            quality_text = pg.TextItem(
                text=f'{device_id}: 100%',
                color=color,
                anchor=(0, 1)
            )
            self.quality_indicators[device_id] = quality_text
            self.addItem(quality_text)
            
            logger.info(f"Added GSR device {device_id} with color {color}")
    
    def remove_device(self, device_id: str) -> None:
        """Remove a GSR device from plotting."""
        with QMutexLocker(self.data_mutex):
            if device_id not in self.gsr_data:
                return
            
            # Remove plot item
            if device_id in self.plot_items:
                self.removeItem(self.plot_items[device_id])
                del self.plot_items[device_id]
            
            # Remove quality indicator
            if device_id in self.quality_indicators:
                self.removeItem(self.quality_indicators[device_id])
                del self.quality_indicators[device_id]
            
            # Clear data
            del self.gsr_data[device_id]
            del self.statistics[device_id]
            
            logger.info(f"Removed GSR device {device_id}")
    
    def add_gsr_data(self, device_id: str, timestamp: float, gsr_value: float) -> None:
        """
        Add new GSR data point.
        
        Args:
            device_id: Device identifier
            timestamp: Timestamp in seconds
            gsr_value: GSR value in microsiemens
        """
        with QMutexLocker(self.data_mutex):
            if device_id not in self.gsr_data:
                self.add_device(device_id)
            
            # Add data point
            self.gsr_data[device_id].append((timestamp, gsr_value))
            
            # Emit signal for external listeners
            self.data_updated.emit(timestamp, gsr_value)
    
    def add_sync_marker(self, timestamp: float, marker_type: str = "sync") -> None:
        """
        Add synchronization marker to the plot.
        
        Args:
            timestamp: Timestamp of the sync event
            marker_type: Type of sync marker
        """
        marker_colors = {
            'sync': 'white',
            'start': 'green',
            'stop': 'red',
            'flash': 'orange'
        }
        
        color = marker_colors.get(marker_type, 'white')
        marker = pg.InfiniteLine(
            pos=timestamp,
            angle=90,
            pen=pg.mkPen(color, width=2, style=2),
            label=f'{marker_type}',
            labelOpts={'color': color, 'position': 0.95}
        )
        
        self.addItem(marker)
        self.sync_markers.append(marker)
        
        # Clean up old markers (keep last 20)
        if len(self.sync_markers) > 20:
            old_marker = self.sync_markers.pop(0)
            self.removeItem(old_marker)
        
        logger.debug(f"Added sync marker: {marker_type} at {timestamp}")
    
    def _update_plot(self) -> None:
        """Update plot with latest data."""
        current_time = time.time()
        
        with QMutexLocker(self.data_mutex):
            for device_id, data_queue in self.gsr_data.items():
                if not data_queue:
                    continue
                
                # Filter data within time window
                filtered_data = [
                    (ts, val) for ts, val in data_queue
                    if current_time - ts <= self.time_window
                ]
                
                if not filtered_data:
                    continue
                
                # Convert to relative time (seconds ago)
                timestamps = np.array([current_time - ts for ts, _ in filtered_data])
                values = np.array([val for _, val in filtered_data])
                
                # Flip timestamps so current time is at 0
                timestamps = -timestamps
                
                # Update plot
                if device_id in self.plot_items:
                    self.plot_items[device_id].setData(timestamps, values)
    
    def _update_statistics(self) -> None:
        """Update statistical calculations for all devices."""
        with QMutexLocker(self.data_mutex):
            for device_id, data_queue in self.gsr_data.items():
                if len(data_queue) < 10:  # Need minimum data for statistics
                    continue
                
                # Get recent data (last 30 seconds)
                current_time = time.time()
                recent_data = [
                    val for ts, val in data_queue
                    if current_time - ts <= 30.0
                ]
                
                if not recent_data:
                    continue
                
                values = np.array(recent_data)
                
                # Calculate statistics
                stats = {
                    'mean': np.mean(values),
                    'std': np.std(values),
                    'min': np.min(values),
                    'max': np.max(values),
                    'trend': self._calculate_trend(values),
                    'quality': self._calculate_quality(values)
                }
                
                self.statistics[device_id] = stats
                
                # Update quality indicator
                if device_id in self.quality_indicators:
                    quality_text = f'{device_id}: {stats["quality"]:.1f}%'
                    self.quality_indicators[device_id].setText(quality_text)
                    
                    # Position indicator
                    y_pos = len(self.statistics) - list(self.statistics.keys()).index(device_id)
                    self.quality_indicators[device_id].setPos(-self.time_window * 0.95, y_pos * 2)
                
                # Emit quality signal
                self.quality_changed.emit(device_id, stats['quality'])
    
    def _calculate_trend(self, values: np.ndarray) -> float:
        """Calculate trend direction (-1 to 1)."""
        if len(values) < 5:
            return 0.0
        
        # Simple linear regression slope
        x = np.arange(len(values))
        slope = np.polyfit(x, values, 1)[0]
        
        # Normalize slope to -1 to 1 range
        return np.clip(slope / np.std(values) if np.std(values) > 0 else 0, -1, 1)
    
    def _calculate_quality(self, values: np.ndarray) -> float:
        """Calculate data quality percentage based on variability and dropouts."""
        if len(values) < 10:
            return 50.0
        
        # Check for data dropouts (repeated values)
        unique_ratio = len(np.unique(values)) / len(values)
        
        # Check signal stability (low noise is good)
        cv = np.std(values) / np.mean(values) if np.mean(values) > 0 else 1.0
        stability_score = max(0, 1 - cv)
        
        # Combine metrics
        quality = (unique_ratio * 0.3 + stability_score * 0.7) * 100
        return np.clip(quality, 0, 100)
    
    def _mouse_moved(self, pos) -> None:
        """Update crosshair position."""
        if self.plotItem.vb.mapSceneToView(pos):
            mouse_point = self.plotItem.vb.mapSceneToView(pos)
            self.crosshair_v.setPos(mouse_point.x())
            self.crosshair_h.setPos(mouse_point.y())
    
    def clear_all_data(self) -> None:
        """Clear all data and reset plots."""
        with QMutexLocker(self.data_mutex):
            for device_id in list(self.gsr_data.keys()):
                self.remove_device(device_id)
            
            # Clear sync markers
            for marker in self.sync_markers:
                self.removeItem(marker)
            self.sync_markers.clear()
    
    def export_data(self, filename: str) -> bool:
        """Export current data to CSV file."""
        try:
            import csv
            
            with open(filename, 'w', newline='') as csvfile:
                writer = csv.writer(csvfile)
                writer.writerow(['device_id', 'timestamp', 'gsr_value_us'])
                
                with QMutexLocker(self.data_mutex):
                    for device_id, data_queue in self.gsr_data.items():
                        for timestamp, value in data_queue:
                            writer.writerow([device_id, timestamp, value])
            
            logger.info(f"GSR data exported to {filename}")
            return True
            
        except Exception as e:
            logger.error(f"Failed to export GSR data: {e}")
            return False
    
    def get_statistics(self, device_id: str = None) -> Dict:
        """Get current statistics for device(s)."""
        with QMutexLocker(self.data_mutex):
            if device_id:
                return self.statistics.get(device_id, {})
            return self.statistics.copy()


class ThermalDisplayWidget(QWidget):
    """
    Real-time thermal camera display widget with temperature mapping and analysis.
    """
    
    def __init__(self, width: int = 256, height: int = 192):
        super().__init__()
        self.thermal_width = width
        self.thermal_height = height
        
        self.setup_ui()
        
        # Data storage
        self.thermal_data_mutex = QMutex()
        self.current_frame = None
        self.temperature_range = (-20, 400)  # Default range in Celsius
        
        # Update timer
        self.update_timer = QTimer()
        self.update_timer.timeout.connect(self.update_display)
        self.update_timer.start(111)  # ~9fps for thermal camera
    
    def setup_ui(self):
        """Setup the thermal display UI."""
        layout = QVBoxLayout()
        
        # Thermal image display
        self.thermal_label = QLabel()
        self.thermal_label.setMinimumSize(512, 384)  # 2x scale
        self.thermal_label.setStyleSheet("border: 1px solid gray;")
        layout.addWidget(self.thermal_label)
        
        # Controls
        controls_layout = QHBoxLayout()
        
        self.range_button = QPushButton("Auto Range")
        self.range_button.clicked.connect(self.auto_range)
        controls_layout.addWidget(self.range_button)
        
        self.colormap_button = QPushButton("Colormap: Hot")
        self.colormap_button.clicked.connect(self.cycle_colormap)
        controls_layout.addWidget(self.colormap_button)
        
        layout.addLayout(controls_layout)
        self.setLayout(layout)
    
    def add_thermal_frame(self, temperature_matrix: np.ndarray, timestamp: float):
        """Add new thermal frame data."""
        with QMutexLocker(self.thermal_data_mutex):
            self.current_frame = {
                'data': temperature_matrix.copy(),
                'timestamp': timestamp,
                'min_temp': np.min(temperature_matrix),
                'max_temp': np.max(temperature_matrix),
                'mean_temp': np.mean(temperature_matrix)
            }
    
    def update_display(self):
        """Update thermal display."""
        with QMutexLocker(self.thermal_data_mutex):
            if self.current_frame is None:
                return
            
            # Convert temperature data to color image
            temp_data = self.current_frame['data']
            
            # Normalize to 0-255 range
            temp_min, temp_max = self.temperature_range
            normalized = np.clip((temp_data - temp_min) / (temp_max - temp_min), 0, 1)
            thermal_image = (normalized * 255).astype(np.uint8)
            
            # Apply colormap (using matplotlib colormap)
            try:
                import matplotlib.cm as cm
                colored = cm.hot(normalized)  # Hot colormap
                colored_image = (colored[:, :, :3] * 255).astype(np.uint8)
                
                # Convert to QImage
                h, w, ch = colored_image.shape
                bytes_per_line = ch * w
                qt_image = QImage(colored_image.data, w, h, bytes_per_line, QImage.Format.Format_RGB888)
                
                # Scale up for display
                scaled_image = qt_image.scaled(512, 384)
                self.thermal_label.setPixmap(QPixmap.fromImage(scaled_image))
                
            except ImportError:
                # Fallback: grayscale
                h, w = thermal_image.shape
                qt_image = QImage(thermal_image.data, w, h, w, QImage.Format.Format_Grayscale8)
                scaled_image = qt_image.scaled(512, 384)
                self.thermal_label.setPixmap(QPixmap.fromImage(scaled_image))
    
    def auto_range(self):
        """Auto-adjust temperature range based on current data."""
        with QMutexLocker(self.thermal_data_mutex):
            if self.current_frame is None:
                return
            
            min_temp = self.current_frame['min_temp']
            max_temp = self.current_frame['max_temp']
            
            # Add some padding
            temp_range = max_temp - min_temp
            padding = temp_range * 0.1
            
            self.temperature_range = (min_temp - padding, max_temp + padding)
            logger.info(f"Auto-ranged thermal display: {self.temperature_range[0]:.1f}°C to {self.temperature_range[1]:.1f}°C")
    
    def cycle_colormap(self):
        """Cycle through different colormaps."""
        # This would cycle through different matplotlib colormaps
        # For now, just update the button text
        current_text = self.colormap_button.text()
        if "Hot" in current_text:
            self.colormap_button.setText("Colormap: Jet")
        elif "Jet" in current_text:
            self.colormap_button.setText("Colormap: Viridis")
        else:
            self.colormap_button.setText("Colormap: Hot")


class MultiModalVisualizationWidget(QWidget):
    """
    Combined widget for multi-modal sensor visualization.
    """
    
    def __init__(self):
        super().__init__()
        
        self.setup_ui()
        
        # Connect real-time data updates
        self.gsr_plot.data_updated.connect(self.on_gsr_data)
        self.gsr_plot.quality_changed.connect(self.on_quality_changed)
    
    def setup_ui(self):
        """Setup the multi-modal visualization UI."""
        layout = QGridLayout()
        
        # GSR plot (left, full height)
        self.gsr_plot = GSRPlotWidget()
        layout.addWidget(self.gsr_plot, 0, 0, 2, 1)
        
        # Thermal display (top right)
        self.thermal_display = ThermalDisplayWidget()
        layout.addWidget(self.thermal_display, 0, 1)
        
        # Video display placeholder (bottom right)
        self.video_label = QLabel("RGB Camera Feed")
        self.video_label.setMinimumSize(400, 300)
        self.video_label.setStyleSheet("border: 1px solid gray; background: black; color: white;")
        layout.addWidget(self.video_label, 1, 1)
        
        # Set column stretch
        layout.setColumnStretch(0, 2)  # GSR plot gets more space
        layout.setColumnStretch(1, 1)
        
        self.setLayout(layout)
    
    def add_gsr_device(self, device_id: str):
        """Add GSR device to visualization."""
        self.gsr_plot.add_device(device_id)
    
    def add_gsr_data(self, device_id: str, timestamp: float, gsr_value: float):
        """Add GSR data point."""
        self.gsr_plot.add_gsr_data(device_id, timestamp, gsr_value)
    
    def add_thermal_frame(self, temperature_matrix: np.ndarray, timestamp: float):
        """Add thermal frame."""
        self.thermal_display.add_thermal_frame(temperature_matrix, timestamp)
    
    def add_sync_marker(self, timestamp: float, marker_type: str = "sync"):
        """Add sync marker to all plots."""
        self.gsr_plot.add_sync_marker(timestamp, marker_type)
    
    def on_gsr_data(self, timestamp: float, gsr_value: float):
        """Handle GSR data update."""
        # This could trigger additional processing or logging
        pass
    
    def on_quality_changed(self, device_id: str, quality: float):
        """Handle quality change."""
        if quality < 70:
            logger.warning(f"GSR data quality degraded for {device_id}: {quality:.1f}%")
    
    def clear_all_data(self):
        """Clear all visualization data."""
        self.gsr_plot.clear_all_data()
        # Could add thermal clearing here too
        self.plot_items[device_id] = plot_item

        logger.info(f"Added GSR device {device_id} with color {color}")

    def remove_device(self, device_id: str) -> None:
        """Remove a GSR device from plotting."""
        if device_id not in self.gsr_data:
            return

        # Remove plot item
        if device_id in self.plot_items:
            self.removeItem(self.plot_items[device_id])
            del self.plot_items[device_id]

        # Clear data
        del self.gsr_data[device_id]

        logger.info(f"Removed GSR device {device_id}")

    def add_gsr_data(
        self, device_id: str, timestamp_ns: int, gsr_microsiemens: float
    ) -> None:
        """
        Add new GSR data point.

        Args:
            device_id: Device identifier
            timestamp_ns: Timestamp in nanoseconds
            gsr_microsiemens: GSR value in microsiemens
        """
        if device_id not in self.gsr_data:
            self.add_device(device_id)

        # Convert timestamp to relative seconds
        current_time = time.time()
        relative_time = (timestamp_ns / 1e9) - current_time

        # Add data point
        self.gsr_data[device_id].append((relative_time, gsr_microsiemens))

        self.data_updated.emit(relative_time, gsr_microsiemens)

    def add_sync_marker(
        self, timestamp_ns: int, label: str = "Sync", color: str = "white"
    ) -> None:
        """
        Add synchronization marker to the plot.

        Args:
            timestamp_ns: Timestamp in nanoseconds
            label: Marker label
            color: Marker color
        """
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
        """Update plot with latest data."""
        current_time = time.time()

        for device_id, data_deque in self.gsr_data.items():
            if not data_deque or device_id not in self.plot_items:
                continue

            # Filter data within time window
            times = []
            values = []

            for timestamp, gsr_value in data_deque:
                relative_time = timestamp
                if relative_time >= -self.time_window:
                    times.append(relative_time)
                    values.append(gsr_value)

            if times and values:
                self.plot_items[device_id].setData(times, values)

        # Update time axis
        self.setXRange(-self.time_window, 0)

    def _cleanup_old_markers(self) -> None:
        """Remove sync markers outside the time window."""
        current_time = time.time()

        markers_to_remove = []
        for marker in self.sync_markers:
            marker_time = marker.pos()[0]
            if marker_time < -self.time_window:
                markers_to_remove.append(marker)

        for marker in markers_to_remove:
            self.removeItem(marker)
            self.sync_markers.remove(marker)

    def clear_data(self) -> None:
        """Clear all plot data."""
        for device_id in list(self.gsr_data.keys()):
            self.remove_device(device_id)

        for marker in self.sync_markers:
            self.removeItem(marker)
        self.sync_markers.clear()


class VideoPreviewWidget(QLabel):
    """
    Live video preview widget for RGB and thermal cameras.

    Features:
    - Real-time frame display
    - Automatic scaling and aspect ratio preservation
    - Frame rate monitoring
    - Device status indicators
    """

    frame_updated = pyqtSignal(int, int)  # width, height

    def __init__(self, device_id: str, device_type: str = "RGB"):
        """
        Initialize video preview widget.

        Args:
            device_id: Device identifier
            device_type: Type of camera (RGB, Thermal, etc.)
        """
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
        self.fps_timer.start(1000)  # Update FPS every second

    def _setup_widget(self) -> None:
        """Set up widget appearance."""
        self.setMinimumSize(320, 240)
        self.setStyleSheet(
            """
            QLabel {
                border: 2px solid #333;
                background-color: #111;
                color: white;
                text-align: center;
            }
        """
        )

        self.setText(
            f"{self.device_type} Camera\n{self.device_id}\nWaiting for frames..."
        )

    def update_frame(self, frame_data: np.ndarray) -> None:
        """
        Update widget with new frame.

        Args:
            frame_data: Frame data as numpy array (H, W, C)
        """
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

            # Update statistics
            self.frame_count += 1
            height, width = frame_data.shape[:2]
            self.frame_updated.emit(width, height)

        except Exception as e:
            logger.error(f"Error updating frame for {self.device_id}: {e}")

    def _calculate_fps(self) -> None:
        """Calculate and display current FPS."""
        current_time = time.time()
        time_diff = current_time - self.last_fps_time

        if time_diff > 0:
            self.current_fps = self.frame_count / time_diff

        # Reset counters
        self.frame_count = 0
        self.last_fps_time = current_time

        # Update tooltip with FPS info
        self.setToolTip(
            f"{self.device_type} Camera {self.device_id}\\nFPS: {self.current_fps:.1f}"
        )

    def get_fps(self) -> float:
        """Get current frame rate."""
        return self.current_fps

    def set_status_text(self, text: str) -> None:
        """Set status text when no frames are available."""
        self.setText(f"{self.device_type} Camera\\n{self.device_id}\\n{text}")


class MultiModalDashboard(QWidget):
    """
    Main dashboard widget that combines GSR plots and video previews.

    Implements the dynamic grid layout requirement from FR6.
    """

    def __init__(self):
        """Initialize multi-modal dashboard."""
        super().__init__()

        self.gsr_plot = None
        self.video_widgets: Dict[str, VideoPreviewWidget] = {}

        self._setup_layout()

    def _setup_layout(self) -> None:
        """Set up the dashboard layout."""
        self.layout = QGridLayout(self)

        # Create GSR plot widget (takes up left half)
        self.gsr_plot = GSRPlotWidget()
        self.layout.addWidget(self.gsr_plot, 0, 0, 2, 2)

        # Video preview area (right half, dynamic grid)
        self.video_row = 0
        self.video_col = 2

    def add_gsr_device(self, device_id: str, color: Optional[str] = None) -> None:
        """Add GSR device to the plot."""
        if self.gsr_plot:
            self.gsr_plot.add_device(device_id, color)

    def add_gsr_data(
        self, device_id: str, timestamp_ns: int, gsr_microsiemens: float
    ) -> None:
        """Add GSR data point."""
        if self.gsr_plot:
            self.gsr_plot.add_gsr_data(device_id, timestamp_ns, gsr_microsiemens)

    def add_video_device(
        self, device_id: str, device_type: str = "RGB"
    ) -> VideoPreviewWidget:
        """
        Add video preview widget for a device.

        Args:
            device_id: Device identifier
            device_type: Type of camera

        Returns:
            Created video widget
        """
        if device_id in self.video_widgets:
            return self.video_widgets[device_id]

        widget = VideoPreviewWidget(device_id, device_type)
        self.video_widgets[device_id] = widget

        # Add to grid layout
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
        # Remove all video widgets from layout
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

    def __init__(self):
        """Initialize data aggregation widget."""
        super().__init__()

        self.stats_labels: Dict[str, QLabel] = {}
        self._setup_layout()

    def _setup_layout(self) -> None:
        """Set up the widget layout."""
        layout = QVBoxLayout(self)

        # Create statistics labels
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
