"""
Cross-Platform Utilities for IRCamera PC Controller

Addresses file path handling issues between Windows and Android identified in the documentation.
Prevents data loss in mixed environments by providing robust path conversion and validation.
"""

import os
import platform
from pathlib import Path, PurePath, PurePosixPath, PureWindowsPath
from typing import Dict, Optional, Union

try:
    from loguru import logger
except ImportError:
    # Fallback logger
    class FallbackLogger:
        def info(self, msg): print(f"INFO: {msg}")
        def warning(self, msg): print(f"WARNING: {msg}")
        def error(self, msg): print(f"ERROR: {msg}")
        def debug(self, msg): print(f"DEBUG: {msg}")
    logger = FallbackLogger()


class CrossPlatformPathManager:
    """
    Manages file path conversion between different platforms.
    
    Addresses the critical issue identified in the documentation:
    "File path handling differs between Windows and Android, causing data loss in mixed environments"
    """
    
    # Common Android paths
    ANDROID_STORAGE_PATHS = {
        'internal': '/storage/emulated/0/',
        'external': '/sdcard/',
        'app_data': '/data/data/',
        'downloads': '/storage/emulated/0/Download/',
        'documents': '/storage/emulated/0/Documents/',
    }
    
    # Common Windows paths
    WINDOWS_STORAGE_PATHS = {
        'userprofile': lambda: os.path.expanduser('~'),
        'documents': lambda: os.path.join(os.path.expanduser('~'), 'Documents'),
        'downloads': lambda: os.path.join(os.path.expanduser('~'), 'Downloads'),
        'app_data': lambda: os.path.join(os.path.expanduser('~'), 'AppData', 'Local'),
    }
    
    def __init__(self):
        self.current_platform = platform.system().lower()
        self.is_windows = self.current_platform == 'windows'
        self.is_linux = self.current_platform == 'linux'
        logger.info(f"CrossPlatformPathManager initialized for {self.current_platform}")
    
    def normalize_path(self, path: Union[str, Path]) -> str:
        """
        Normalize a path for the current platform.
        
        Args:
            path: Input path (can be from any platform)
            
        Returns:
            Normalized path string for current platform
        """
        try:
            path_str = str(path)
            
            # Handle empty or None paths
            if not path_str or path_str == 'None':
                return str(Path.cwd())
            
            # Convert Android paths to Windows equivalents
            if self.is_windows and self._is_android_path(path_str):
                return self._convert_android_to_windows(path_str)
            
            # Convert Windows paths to Unix-like equivalents (for Android/Linux)
            elif not self.is_windows and self._is_windows_path(path_str):
                return self._convert_windows_to_unix(path_str)
            
            # Normalize separators for current platform
            normalized = str(Path(path_str))
            
            logger.debug(f"Path normalized: '{path_str}' -> '{normalized}'")
            return normalized
            
        except Exception as e:
            logger.error(f"Error normalizing path '{path}': {e}")
            # Return a safe default path
            return str(Path.cwd())
    
    def _is_android_path(self, path: str) -> bool:
        """Check if path looks like an Android path."""
        android_indicators = [
            '/storage/emulated/',
            '/sdcard/',
            '/data/data/',
            '/android_asset/',
            '/system/',
        ]
        return any(indicator in path for indicator in android_indicators)
    
    def _is_windows_path(self, path: str) -> bool:
        """Check if path looks like a Windows path."""
        return (
            '\\' in path or 
            (len(path) >= 2 and path[1] == ':') or
            path.startswith('C:') or
            'AppData' in path or
            'Program Files' in path
        )
    
    def _convert_android_to_windows(self, android_path: str) -> str:
        """Convert Android path to Windows equivalent."""
        try:
            # Map common Android paths to Windows equivalents
            path_mappings = {
                '/storage/emulated/0/': self.WINDOWS_STORAGE_PATHS['userprofile'](),
                '/sdcard/': self.WINDOWS_STORAGE_PATHS['userprofile'](),
                '/storage/emulated/0/Documents/': self.WINDOWS_STORAGE_PATHS['documents'](),
                '/storage/emulated/0/Download/': self.WINDOWS_STORAGE_PATHS['downloads'](),
                '/data/data/': self.WINDOWS_STORAGE_PATHS['app_data'](),
            }
            
            # Find the best match
            for android_prefix, windows_base in path_mappings.items():
                if android_path.startswith(android_prefix):
                    relative_path = android_path[len(android_prefix):]
                    converted = os.path.join(windows_base, relative_path).replace('/', '\\')
                    logger.info(f"Converted Android path: '{android_path}' -> '{converted}'")
                    return converted
            
            # Fallback: use user's Documents folder
            fallback = os.path.join(self.WINDOWS_STORAGE_PATHS['documents'](), 
                                   os.path.basename(android_path))
            logger.warning(f"Android path fallback: '{android_path}' -> '{fallback}'")
            return fallback
            
        except Exception as e:
            logger.error(f"Error converting Android path '{android_path}': {e}")
            return self.WINDOWS_STORAGE_PATHS['documents']()
    
    def _convert_windows_to_unix(self, windows_path: str) -> str:
        """Convert Windows path to Unix-like equivalent."""
        try:
            # Handle drive letters
            if len(windows_path) >= 2 and windows_path[1] == ':':
                # Remove drive letter and convert
                unix_path = windows_path[2:].replace('\\', '/')
                if not unix_path.startswith('/'):
                    unix_path = '/' + unix_path
            else:
                unix_path = windows_path.replace('\\', '/')
            
            # Map common Windows paths
            path_mappings = {
                '/Users/': '/home/',
                '/Documents and Settings/': '/home/',
                '/AppData/Local/': '/.local/share/',
                '/AppData/Roaming/': '/.config/',
            }
            
            for windows_pattern, unix_replacement in path_mappings.items():
                if windows_pattern in unix_path:
                    unix_path = unix_path.replace(windows_pattern, unix_replacement)
            
            logger.info(f"Converted Windows path: '{windows_path}' -> '{unix_path}'")
            return unix_path
            
        except Exception as e:
            logger.error(f"Error converting Windows path '{windows_path}': {e}")
            return '/tmp/'
    
    def create_platform_session_path(self, session_id: str, base_dir: Optional[str] = None) -> str:
        """
        Create a session directory path appropriate for the current platform.
        
        Args:
            session_id: Session identifier
            base_dir: Optional base directory (will be normalized)
            
        Returns:
            Platform-appropriate session directory path
        """
        try:
            if base_dir:
                normalized_base = self.normalize_path(base_dir)
                base_path = Path(normalized_base)
            else:
                # Use platform-appropriate default
                if self.is_windows:
                    base_path = Path(self.WINDOWS_STORAGE_PATHS['documents']()) / 'IRCamera' / 'sessions'
                else:
                    base_path = Path.home() / 'IRCamera' / 'sessions'
            
            session_path = base_path / session_id
            
            # Create directory if it doesn't exist
            session_path.mkdir(parents=True, exist_ok=True)
            
            path_str = str(session_path)
            logger.info(f"Created session path: {path_str}")
            return path_str
            
        except Exception as e:
            logger.error(f"Error creating session path for '{session_id}': {e}")
            # Fallback to temp directory
            fallback = Path.cwd() / 'sessions' / session_id
            fallback.mkdir(parents=True, exist_ok=True)
            return str(fallback)
    
    def validate_and_fix_config_paths(self, config_dict: Dict) -> Dict:
        """
        Validate and fix file paths in configuration dictionary.
        
        Addresses the data loss issue by ensuring all paths are valid for current platform.
        
        Args:
            config_dict: Configuration dictionary with potentially invalid paths
            
        Returns:
            Fixed configuration dictionary
        """
        try:
            fixed_config = config_dict.copy()
            
            # Common configuration keys that contain paths
            path_keys = [
                'base_directory', 'session_directory', 'data_directory',
                'output_directory', 'log_directory', 'temp_directory',
                'recording_path', 'export_path', 'backup_path'
            ]
            
            changes_made = 0
            
            for key, value in fixed_config.items():
                # Check if this looks like a path
                if (isinstance(value, str) and 
                    (key in path_keys or 
                     '/' in value or '\\' in value or 
                     key.endswith('_path') or key.endswith('_dir'))):
                    
                    original_value = value
                    normalized_value = self.normalize_path(value)
                    
                    if original_value != normalized_value:
                        fixed_config[key] = normalized_value
                        changes_made += 1
                        logger.info(f"Fixed config path '{key}': '{original_value}' -> '{normalized_value}'")
                
                # Recursively fix nested dictionaries
                elif isinstance(value, dict):
                    fixed_nested = self.validate_and_fix_config_paths(value)
                    if fixed_nested != value:
                        fixed_config[key] = fixed_nested
                        changes_made += 1
            
            if changes_made > 0:
                logger.info(f"Fixed {changes_made} path(s) in configuration")
            
            return fixed_config
            
        except Exception as e:
            logger.error(f"Error fixing config paths: {e}")
            return config_dict
    
    def safe_path_join(self, *path_parts) -> str:
        """
        Safely join path parts, handling cross-platform issues.
        
        Args:
            *path_parts: Path components to join
            
        Returns:
            Safely joined path string
        """
        try:
            # Filter out None and empty parts
            valid_parts = [str(part) for part in path_parts if part is not None and str(part).strip()]
            
            if not valid_parts:
                return str(Path.cwd())
            
            # Join using pathlib for cross-platform compatibility
            joined_path = Path(*valid_parts)
            result = str(joined_path)
            
            logger.debug(f"Joined path parts {path_parts} -> '{result}'")
            return result
            
        except Exception as e:
            logger.error(f"Error joining path parts {path_parts}: {e}")
            return str(Path.cwd())
    
    def ensure_directory_exists(self, directory_path: str) -> bool:
        """
        Ensure directory exists, creating it if necessary.
        
        Args:
            directory_path: Path to directory
            
        Returns:
            True if directory exists or was created successfully
        """
        try:
            normalized_path = self.normalize_path(directory_path)
            path_obj = Path(normalized_path)
            
            if path_obj.exists() and path_obj.is_dir():
                return True
            
            path_obj.mkdir(parents=True, exist_ok=True)
            logger.info(f"Created directory: {normalized_path}")
            return True
            
        except Exception as e:
            logger.error(f"Error creating directory '{directory_path}': {e}")
            return False
    
    def get_platform_info(self) -> Dict[str, str]:
        """Get platform information for debugging."""
        return {
            'platform': self.current_platform,
            'is_windows': str(self.is_windows),
            'is_linux': str(self.is_linux),
            'path_separator': os.sep,
            'current_directory': str(Path.cwd()),
            'home_directory': str(Path.home()),
        }


# Global instance for easy access
path_manager = CrossPlatformPathManager()


def normalize_path(path: Union[str, Path]) -> str:
    """Convenience function for path normalization."""
    return path_manager.normalize_path(path)


def create_session_path(session_id: str, base_dir: Optional[str] = None) -> str:
    """Convenience function for creating session paths."""
    return path_manager.create_platform_session_path(session_id, base_dir)


def fix_config_paths(config_dict: Dict) -> Dict:
    """Convenience function for fixing configuration paths."""
    return path_manager.validate_and_fix_config_paths(config_dict)


def safe_join_paths(*path_parts) -> str:
    """Convenience function for safe path joining."""
    return path_manager.safe_path_join(*path_parts)