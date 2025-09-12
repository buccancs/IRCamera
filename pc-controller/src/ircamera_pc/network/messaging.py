"""Reliable messaging service for IRCamera PC Controller."""

import asyncio
from typing import Any, Callable, Dict, Optional

from ..utils.simple_logger import logger


class ReliableMessageService:
    """Service for reliable message delivery between devices."""

    def __init__(self):
        self.message_handlers = {}
        self.transport = None
        self.is_running = False

    def set_transport(self, transport):
        """Set the transport layer for message delivery."""
        self.transport = transport

    def register_message_handler(
        self,
        message_type: str,
        handler: Callable[[Dict[str, Any]], Optional[Dict[str, Any]]],
    ):
        """Register a handler for specific message types."""
        self.message_handlers[message_type] = handler
        logger.debug(f"Registered handler for message type: {message_type}")

    def unregister_message_handler(self, message_type: str):

        if not self.is_running:
            raise RuntimeError("Messaging service not running")

        if not self.transport:
            raise RuntimeError("No transport configured for messaging service")

        # Generate unique message ID
        message_id = str(uuid.uuid4())

        # Set default timeout
        if timeout_seconds is None:
            timeout_seconds = self.default_timeout

        current_time = time.time()
        message = ReliableMessage(
            message_id=message_id,
            target_host=target_host,
            target_port=target_port,
            message_type=message_type,
            content=content,
            priority=priority,
            created_at=current_time,
            expires_at=current_time + timeout_seconds,
            max_retries=max_retries,
        )

        # Store message and callback
        self.pending_messages[message_id] = message
        if callback:
            self.message_callbacks[message_id] = callback

        self.priority_queues[priority].append(message_id)

        logger.debug(
            f"Queued reliable message {message_id} to {target_host}:{target_port} "
            f"(priority: {priority.name})"
        )
        return message_id

    async def handle_acknowledgment(
        self, message_id: str, success: bool, error_message: Optional[str] = None
    ):

        if message_id not in self.pending_messages:
            logger.warning(f"Received acknowledgment for unknown message: {message_id}")
            return

        message = self.pending_messages[message_id]

        if success:
            message.status = MessageStatus.ACKNOWLEDGED
            await self._notify_message_acknowledged(message_id)
            logger.debug(f"Message {message_id} acknowledged successfully")
        else:
            message.status = MessageStatus.FAILED
            message.error_message = error_message or "Remote processing failed"
            await self._notify_message_failed(message_id, message.error_message)
            logger.warning(f"Message {message_id} failed: {message.error_message}")

        self._remove_pending_message(message_id)

    async def handle_incoming_message(
        self, message_data: Dict[str, Any], sender_info: Optional[Dict[str, Any]] = None
    ) -> Optional[Dict[str, Any]]:

        try:
            message_type = message_data.get("message_type")
            if not message_type:
                logger.warning("Received message without message_type")
                return self._create_error_response("Missing message_type")

            if message_type == "message_ack":
                await self._handle_ack_message(message_data)
                return None
            elif message_type == "message_nack":
                await self._handle_nack_message(message_data)
                return None

            if message_type in self.message_handlers:
                handler = self.message_handlers[message_type]

                try:
                    response = handler(message_data)

                    # Send positive acknowledgment
                    await self._send_acknowledgment(message_data, True, sender_info)

                    return response

                except Exception as e:
                    logger.error(f"Handler error for {message_type}: {e}")

                    # Send negative acknowledgment
                    await self._send_acknowledgment(
                        message_data, False, sender_info, str(e)
                    )

                    return self._create_error_response(f"Handler error: {e}")
            else:
                logger.warning(f"No handler for message type: {message_type}")

                # Send negative acknowledgment
                await self._send_acknowledgment(
                    message_data, False, sender_info, f"No handler for {message_type}"
                )

                return self._create_error_response(
                    f"No handler for message type: {message_type}"
                )

        except Exception as e:
            logger.error(f"Error handling incoming message: {e}")
            return self._create_error_response(f"Processing error: {e}")

    async def _message_processor(self):
        """Background task that processes the message queues."""
        logger.debug("Message processor started")

        while self.is_running:
            try:

                message_processed = False

                for priority in reversed(list(MessagePriority)):
                    queue = self.priority_queues[priority]

                    if queue:
                        message_id = queue.popleft()

                        if message_id in self.pending_messages:
                            await self._process_message(message_id)
                            message_processed = True
                            break

                if not message_processed:
                    # No messages to process, sleep briefly
                    await asyncio.sleep(0.1)

            except Exception as e:
                logger.error(f"Error in message processor: {e}")
                await asyncio.sleep(1.0)

        logger.debug("Message processor stopped")

    async def _process_message(self, message_id: str):
        """Process a single message."""
        message = self.pending_messages.get(message_id)
        if not message:
            return

        current_time = time.time()

        if current_time >= message.expires_at:
            message.status = MessageStatus.EXPIRED
            await self._notify_message_failed(message_id, "Message expired")
            self._remove_pending_message(message_id)
            return

        if message.last_attempt:
            retry_delay = min(
                self.base_retry_delay * (2**message.retry_count), self.max_retry_delay
            )

            if current_time - message.last_attempt < retry_delay:
                # Not time to retry yet, put back in queue
                self.priority_queues[message.priority].append(message_id)
                return

        # Attempt to send message
        try:

            payload = {
                "message_id": message.message_id,
                "message_type": message.message_type,
                "timestamp": current_time,
                "content": message.content,
                "requires_ack": True,
            }

            # Attempt delivery
            if self.transport is not None:
                success = await self.transport(
                    message.target_host, message.target_port, payload
                )
            else:
                success = False
                logger.error(f"No transport available for message {message_id}")

            message.last_attempt = current_time

            if success:
                message.status = MessageStatus.SENT
                # Wait for acknowledgment (timeout handled by expiry)
                logger.debug(f"Message {message_id} sent, waiting for acknowledgment")
            else:
                # Send failed, check for retry
                await self._handle_send_failure(message_id, "Transport failed")

        except Exception as e:
            logger.error(f"Error sending message {message_id}: {e}")
            await self._handle_send_failure(message_id, str(e))

    async def _handle_send_failure(self, message_id: str, error: str):
        """Handle a message send failure."""
        message = self.pending_messages.get(message_id)
        if not message:
            return

        message.retry_count += 1

        if message.retry_count <= message.max_retries:
            # Schedule retry
            await self._notify_message_retrying(message_id, message.retry_count)
            self.priority_queues[message.priority].append(message_id)
            logger.debug(
                f"Retrying message {message_id} "
                f"(attempt {message.retry_count}/{message.max_retries})"
            )
        else:
            # Max retries exceeded
            message.status = MessageStatus.FAILED
            message.error_message = f"Max retries exceeded: {error}"
            await self._notify_message_failed(message_id, message.error_message)
            self._remove_pending_message(message_id)
            logger.warning(
                f"Message {message_id} failed permanently: {message.error_message}"
            )

    async def _cleanup_processor(self):
        """Background task that cleans up expired messages and callbacks."""
        logger.debug("Cleanup processor started")

        while self.is_running:
            try:
                await asyncio.sleep(self.cleanup_interval)

                current_time = time.time()
                expired_messages = []

                # Find expired messages
                for message_id, message in self.pending_messages.items():
                    if current_time >= message.expires_at and message.status in [
                        MessageStatus.PENDING,
                        MessageStatus.SENT,
                    ]:
                        expired_messages.append(message_id)

                # Clean up expired messages
                for message_id in expired_messages:
                    message = self.pending_messages[message_id]
                    message.status = MessageStatus.EXPIRED
                    await self._notify_message_failed(message_id, "Message expired")
                    self._remove_pending_message(message_id)

                if expired_messages:
                    logger.debug(f"Cleaned up {len(expired_messages)} expired messages")

            except Exception as e:
                logger.error(f"Error in cleanup processor: {e}")

        logger.debug("Cleanup processor stopped")

    async def _handle_ack_message(self, message_data: Dict[str, Any]):
        """Handle positive acknowledgment message."""
        original_message_id = message_data.get("original_message_id")
        if original_message_id:
            await self.handle_acknowledgment(original_message_id, True)

    async def _handle_nack_message(self, message_data: Dict[str, Any]):
        """Handle negative acknowledgment message."""
        original_message_id = message_data.get("original_message_id")
        error_message = message_data.get("error_message", "Remote processing failed")
        if original_message_id:
            await self.handle_acknowledgment(original_message_id, False, error_message)

    async def _send_acknowledgment(
        self,
        original_message: Dict[str, Any],
        success: bool,
        sender_info: Optional[Dict[str, Any]] = None,
        error_message: Optional[str] = None,
    ):
        """Send acknowledgment for a received message."""
        if not self.transport or not sender_info:
            return

        original_message_id = original_message.get("message_id")
        if not original_message_id:
            return

        ack_type = "message_ack" if success else "message_nack"
        ack_payload = {
            "message_id": str(uuid.uuid4()),
            "message_type": ack_type,
            "original_message_id": original_message_id,
            "timestamp": time.time(),
        }

        if not success and error_message:
            ack_payload["error_message"] = error_message

        try:
            await self.transport(
                sender_info.get("host"), sender_info.get("port"), ack_payload
            )
        except Exception as e:
            logger.error(f"Failed to send acknowledgment: {e}")

    def _create_error_response(self, error_message: str) -> Dict[str, Any]:
        """Create a standard error response."""
        return {
            "message_type": "error",
            "message_id": str(uuid.uuid4()),
            "timestamp": time.time(),
            "error": error_message,
        }

    def _remove_pending_message(self, message_id: str):
        """Remove a message from pending messages and callbacks."""
        self.pending_messages.pop(message_id, None)
        self.message_callbacks.pop(message_id, None)

    async def _notify_message_acknowledged(self, message_id: str):
        """Notify callback that message was acknowledged."""
        callback = self.message_callbacks.get(message_id)
        if callback and callback.on_acknowledged:
            try:
                callback.on_acknowledged(message_id)
            except Exception as e:
                logger.error(f"Error in acknowledgment callback: {e}")

    async def _notify_message_failed(self, message_id: str, error_message: str):
        """Notify callback that message failed."""
        callback = self.message_callbacks.get(message_id)
        if callback and callback.on_failed:
            try:
                callback.on_failed(message_id, error_message)
            except Exception as e:
                logger.error(f"Error in failure callback: {e}")

    async def _notify_message_retrying(self, message_id: str, attempt: int):
        """Notify callback that message is being retried."""
        callback = self.message_callbacks.get(message_id)
        if callback and callback.on_retrying:
            try:
                callback.on_retrying(message_id, attempt)
            except Exception as e:
                logger.error(f"Error in retry callback: {e}")

    def get_pending_message_count(self) -> int:
        """Get the number of pending messages."""
        return len(self.pending_messages)

    def get_queue_sizes(self) -> Dict[MessagePriority, int]:
        """Get the size of each priority queue."""
        return {
            priority: len(queue) for priority, queue in self.priority_queues.items()
        }
