
        self._protocol_file = protocol_file
        self._protocol_def: Optional[Dict[str, Any]] = None
        self._message_definitions: Dict[str, MessageDefinition] = {}
        self._validator_cache: Dict[str, jsonschema.protocols.Validator] = {}

        self._load_protocol()

    def _load_protocol(self) -> None:

        try:

            if not isinstance(message, dict):
                raise ValidationError("Message must be a dictionary")

            message_type = message.get("message_type")
            if not message_type:
                raise ValidationError("Message must have 'message_type' field")

            msg_def = self._message_definitions.get(message_type)
            if not msg_def:
                raise ValidationError(f"Unknown message type: {message_type}")

            # Validate against JSON schema
            validator = self._get_validator(message_type, msg_def.schema)
            validator.validate(message)

            # Additional validation
            self._validate_timestamp(message)

            return True

        except (jsonschema.ValidationError, ValidationError) as e:
            error_msg = f"Message validation failed: {e}"
            if strict:
                raise ValidationError(error_msg)
            else:
                logger.warning(error_msg)
                return False
        except (OSError, ValueError, RuntimeError) as e:
            error_msg = f"Unexpected validation error: {e}"
            if strict:
                raise ValidationError(error_msg)
            else:
                logger.error(error_msg)
                return False

    def _get_validator(
        self, message_type: str, schema: Dict[str, Any]
    ) -> jsonschema.protocols.Validator:

        msg_def = self._message_definitions.get(message_type)
        if not msg_def:
            raise ValidationError(f"Unknown message type: {message_type}")

        message = {
            "message_type": message_type,
            "timestamp": datetime.now(timezone.utc).isoformat(),
        }

        message.update(kwargs)

        # Validate the created message
        self.validate_message(message, strict=True)

        return message

    def get_transport_config(self) -> Dict[str, Any]:
        """Get transport configuration from protocol."""
        if self._protocol_def is None:
            return {}
        return cast(Dict[str, Any], self._protocol_def.get("transport", {}))

    def get_validation_config(self) -> Dict[str, Any]:
        """Get validation configuration from protocol."""
        if self._protocol_def is None:
            return {}
        return cast(Dict[str, Any], self._protocol_def.get("validation", {}))

    def reload_protocol(self) -> None:
        """Reload protocol definition from file."""
        self._message_definitions.clear()
        self._validator_cache.clear()
        self._load_protocol()
        logger.info("Protocol definition reloaded")

# Global protocol manager instance
_protocol_manager: Optional[ProtocolManager] = None

def get_protocol_manager() -> ProtocolManager:
    """Get global protocol manager instance."""
    global _protocol_manager
    if _protocol_manager is None:
        _protocol_manager = ProtocolManager()
    return _protocol_manager

def validate_message(message: Dict[str, Any], strict: bool = True) -> bool:
    """Validate a message using the global protocol manager."""
    return get_protocol_manager().validate_message(message, strict)

def create_message(message_type: str, **kwargs) -> Dict[str, Any]:
    """Create a message using the global protocol manager."""
    return get_protocol_manager().create_message(message_type, **kwargs)
