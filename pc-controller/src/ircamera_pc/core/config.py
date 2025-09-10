
        if config_path is None:
            # Default to config/config.yaml relative to project root
            project_root = Path(__file__).parent.parent.parent.parent
            config_path_resolved: Path = project_root / "config" / "config.yaml"
        else:
            config_path_resolved = Path(config_path)

        self.config_path = config_path_resolved
        self._config: Dict[str, Any] = {}
        self._load_config()

    def _load_config(self) -> None:

        try:
            keys = key.split(".")
            value = self._config

            for k in keys:
                value = value[k]

            return value

        except (KeyError, TypeError):
            return default

    def set(self, key: str, value: Any) -> None:

        keys = key.split(".")
        config = self._config

        # Navigate to parent of target key
        for k in keys[:-1]:
            if k not in config:
                config[k] = {}
            config = config[k]

        # Set the final key
        config[keys[-1]] = value
        logger.debug(f"Configuration updated: {key} = {value}")

    def save(self) -> None:
        """Save current configuration to file."""
        try:
            self.config_path.parent.mkdir(parents=True, exist_ok=True)

            with open(self.config_path, "w", encoding="utf-8") as file:
                yaml.dump(self._config, file, default_flow_style=False, indent=2)

            logger.info(f"Configuration saved to {self.config_path}")

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to save configuration: {e}")

    def reload(self) -> None:
        """Reload configuration from file."""
        self._load_config()

    def get_all(self) -> Dict[str, Any]:
        """Get entire configuration dictionary."""
        return self._config.copy()

    def to_dict(self) -> Dict[str, Any]:
        """Get entire configuration as dictionary (alias for get_all)."""
        return self.get_all()

# Global configuration instance
config = ConfigManager()
