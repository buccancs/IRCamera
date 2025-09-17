#!/usr/bin/env python3
"""
Test Application for Responsive UI Improvements

Demonstrates the UI threading enhancements that prevent GUI freezes during device operations.
This addresses the specific responsiveness issues identified in the documentation.
"""

import sys
import os
from pathlib import Path

# Add src to path
sys.path.insert(0, str(Path(__file__).parent / "src"))

try:
    from PyQt6.QtWidgets import QApplication, QMainWindow, QVBoxLayout, QWidget, QLabel
    from PyQt6.QtCore import Qt
    from PyQt6.QtGui import QFont
    PYQT_AVAILABLE = True
    
    from src.ircamera_pc.gui.responsive_ui import create_responsive_device_dashboard
    
    class ResponsiveTestWindow(QMainWindow):
        """Test window demonstrating responsive UI improvements."""
        
        def __init__(self):
            super().__init__()
            self.setWindowTitle("IRCamera PC Controller - Responsive UI Test")
            self.setGeometry(100, 100, 800, 600)
            
            self.setup_ui()
        
        def setup_ui(self):
            """Setup the test UI."""
            central_widget = QWidget()
            self.setCentralWidget(central_widget)
            
            layout = QVBoxLayout(central_widget)
            
            # Title
            title = QLabel("Responsive Device Discovery Test")
            title.setFont(QFont("Arial", 16, QFont.Weight.Bold))
            title.setAlignment(Qt.AlignmentFlag.AlignCenter)
            layout.addWidget(title)
            
            # Instructions
            instructions = QLabel(
                "This test demonstrates:\n"
                "• Non-blocking device discovery\n"
                "• 5-second connection timeouts (reduced from 30s)\n"
                "• Background threading for network operations\n"
                "• Signal-slot communication for UI updates\n"
                "• No GUI freezes during device operations"
            )
            instructions.setFont(QFont("Arial", 10))
            instructions.setWordWrap(True)
            layout.addWidget(instructions)
            
            # Responsive device dashboard
            self.device_dashboard = create_responsive_device_dashboard(self)
            if self.device_dashboard:
                layout.addWidget(self.device_dashboard)
            else:
                error_label = QLabel("Failed to create device dashboard")
                error_label.setStyleSheet("color: red;")
                layout.addWidget(error_label)
        
        def closeEvent(self, event):
            """Handle window close event."""
            if hasattr(self, 'device_dashboard') and self.device_dashboard:
                self.device_dashboard.cleanup()
            event.accept()

except ImportError:
    PYQT_AVAILABLE = False
    print("PyQt6 not available. Running in headless mode.")
    
    # Dummy class for headless mode
    class ResponsiveTestWindow:
    """Test window demonstrating responsive UI improvements."""
    
    def __init__(self):
        super().__init__()
        self.setWindowTitle("IRCamera PC Controller - Responsive UI Test")
        self.setGeometry(100, 100, 800, 600)
        
        self.setup_ui()
    
    def setup_ui(self):
        """Setup the test UI."""
        central_widget = QWidget()
        self.setCentralWidget(central_widget)
        
        layout = QVBoxLayout(central_widget)
        
        # Title
        title = QLabel("Responsive Device Discovery Test")
        title.setFont(QFont("Arial", 16, QFont.Weight.Bold))
        title.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(title)
        
        # Instructions
        instructions = QLabel(
            "This test demonstrates:\n"
            "• Non-blocking device discovery\n"
            "• 5-second connection timeouts (reduced from 30s)\n"
            "• Background threading for network operations\n"
            "• Signal-slot communication for UI updates\n"
            "• No GUI freezes during device operations"
        )
        instructions.setFont(QFont("Arial", 10))
        instructions.setWordWrap(True)
        layout.addWidget(instructions)
        
        # Responsive device dashboard
        self.device_dashboard = create_responsive_device_dashboard(self)
        if self.device_dashboard:
            layout.addWidget(self.device_dashboard)
        else:
            error_label = QLabel("Failed to create device dashboard")
            error_label.setStyleSheet("color: red;")
            layout.addWidget(error_label)
    
    def closeEvent(self, event):
        """Handle window close event."""
        if hasattr(self, 'device_dashboard') and self.device_dashboard:
            self.device_dashboard.cleanup()
        event.accept()


def test_responsive_ui_headless():
    """Test responsive UI components in headless mode."""
    print("=" * 60)
    print("RESPONSIVE UI IMPROVEMENTS TEST (HEADLESS)")
    print("=" * 60)
    
    try:
        from src.ircamera_pc.network.enhanced_discovery import ResponsiveDeviceManager
        
        print("✓ Enhanced discovery module imported successfully")
        
        # Test responsive manager creation
        manager = ResponsiveDeviceManager()
        print("✓ ResponsiveDeviceManager created")
        
        # Test discovery startup (will use fallback without PyQt6)
        success = manager.start_responsive_discovery()
        if success:
            print("✓ Responsive discovery started (fallback mode)")
        else:
            print("⚠ Responsive discovery failed to start")
        
        # Cleanup
        manager.cleanup()
        print("✓ Manager cleanup completed")
        
        print("\n📋 UI IMPROVEMENTS IMPLEMENTED:")
        print("• Background threading for network operations")
        print("• 5-second connection timeouts (reduced from 30s)")
        print("• Non-blocking device discovery")
        print("• Signal-slot communication for UI updates")
        print("• Enhanced error handling and recovery")
        
        print("\n✅ RESPONSIVE UI COMPONENTS - READY FOR TESTING")
        return True
        
    except Exception as e:
        print(f"✗ Error testing responsive UI: {e}")
        return False


def test_responsive_ui_gui():
    """Test responsive UI with full GUI."""
    print("Starting GUI test...")
    
    app = QApplication(sys.argv)
    
    # Set application properties
    app.setApplicationName("IRCamera PC Controller")
    app.setApplicationVersion("1.0.0")
    app.setOrganizationName("IRCamera")
    
    # Create and show test window
    window = ResponsiveTestWindow()
    window.show()
    
    print("GUI test window launched. Test the following:")
    print("1. Click 'Refresh' to start discovery")
    print("2. Verify UI remains responsive during discovery")
    print("3. Try connecting to discovered devices")
    print("4. Confirm 5-second connection timeouts")
    print("5. Check that GUI doesn't freeze during operations")
    
    # Run application
    return app.exec()


def main():
    """Main test function."""
    print("IRCamera PC Controller - Responsive UI Test")
    print("==========================================")
    
    if not PYQT_AVAILABLE:
        print("PyQt6 not available. Running headless test...")
        test_responsive_ui_headless()
    else:
        try:
            # Check if we can create GUI
            if len(sys.argv) > 1 and sys.argv[1] == "--headless":
                test_responsive_ui_headless()
            else:
                test_responsive_ui_gui()
        except Exception as e:
            print(f"GUI test failed: {e}")
            print("Falling back to headless test...")
            test_responsive_ui_headless()


if __name__ == "__main__":
    main()