
        context = ssl.create_default_context(ssl.Purpose.CLIENT_AUTH)

        context.load_cert_chain(str(self.server_cert_path), str(self.server_key_path))

        if for_client_auth:

            context.load_verify_locations(str(self.ca_cert_path))
            context.verify_mode = ssl.CERT_OPTIONAL  # Allow fallback to plaintext
        else:
            context.verify_mode = ssl.CERT_NONE

        # Set security options
        context.check_hostname = False  # Local network devices
        context.minimum_version = ssl.TLSVersion.TLSv1_2

        return context

    def _extract_certificate_info(
        self, certificate
    ) -> Tuple[Optional[str], Optional[str]]:

        try:
            certificate = x509.load_pem_x509_certificate(cert_data)
            common_name, organization = self._extract_certificate_info(certificate)

            if self._is_topdon_organization(organization):
                if common_name:
                    device_type = self._validate_topdon_device(common_name)
                    if device_type:
                        return True, device_type

            # Default to generic acceptance for development
            logger.warning(
                f"Unknown device certificate: {common_name!r} from {organization!r}"
            )
            return True, "UNKNOWN"

        except Exception as e:
            logger.error(f"Certificate validation failed: {e}")
            return False, None

    def generate_auth_token(self, device_id: str, duration_minutes: int = 5) -> str:

        # Generate token components
        timestamp = str(int(time.time()))
        nonce = secrets.token_hex(8)

        token_data = f"{device_id}:{timestamp}:{nonce}"
        token_hash = hashlib.sha256(token_data.encode()).hexdigest()[:16]

        # Final token format: device_id:timestamp:nonce:hash
        token = f"{device_id}:{timestamp}:{nonce}:{token_hash}"

        # Store token with expiry
        expiry_time = time.time() + (duration_minutes * 60)
        self.auth_tokens[token] = (device_id, expiry_time)

        logger.debug(f"Generated auth token for device {device_id}: {token[:20]}...")
        return token

    def validate_auth_token(
        self, token: str, max_age_seconds: int = 300
    ) -> Tuple[bool, Optional[str]]:

        try:

            if token in self.auth_tokens:
                device_id, expiry_time = self.auth_tokens[token]
                if time.time() < expiry_time:
                    return True, device_id
                else:
                    # Token expired, remove it
                    del self.auth_tokens[token]
                    return False, None

            # Parse token components
            parts = token.split(":")
            if len(parts) != 4:
                return False, None

            device_id, timestamp, nonce, provided_hash = parts

            token_time = int(timestamp)
            if time.time() - token_time > max_age_seconds:
                return False, None

            # Verify hash integrity
            token_data = f"{device_id}:{timestamp}:{nonce}"
            expected_hash = hashlib.sha256(token_data.encode()).hexdigest()[:16]

            if provided_hash == expected_hash:
                return True, device_id
            else:
                return False, None

        except Exception as e:
            logger.error(f"Token validation failed: {e}")
            return False, None

    def cleanup_expired_tokens(self):
        """Remove expired authentication tokens."""
        current_time = time.time()
        expired_tokens = [
            token
            for token, (_, expiry) in self.auth_tokens.items()
            if current_time >= expiry
        ]

        for token in expired_tokens:
            del self.auth_tokens[token]

        if expired_tokens:
            logger.debug(f"Cleaned up {len(expired_tokens)} expired tokens")

    def _load_ca_certificate(self) -> bool:
        """Load existing CA certificate if available."""
        try:
            if self.ca_cert_path.exists() and self.ca_key_path.exists():
                # Verify certificate is valid
                with open(self.ca_cert_path, "rb") as f:
                    x509.load_pem_x509_certificate(f.read())
                logger.debug("Loaded existing CA certificate")
                return True
        except Exception as e:
            logger.warning(f"Failed to load CA certificate: {e}")
        return False

    def _load_server_certificate(self) -> bool:
        """Load existing server certificate if available."""
        try:
            if self.server_cert_path.exists() and self.server_key_path.exists():
                # Verify certificate is valid
                with open(self.server_cert_path, "rb") as f:
                    x509.load_pem_x509_certificate(f.read())
                logger.debug("Loaded existing server certificate")
                return True
        except Exception as e:
            logger.warning(f"Failed to load server certificate: {e}")
        return False

    def _generate_ca_certificate(self):
        """Generate a new CA certificate and private key."""
        # Generate private key
        private_key = rsa.generate_private_key(
            public_exponent=65537,
            key_size=2048,
        )

        # Generate certificate
        subject = issuer = x509.Name(
            [
                x509.NameAttribute(NameOID.COUNTRY_NAME, "US"),
                x509.NameAttribute(NameOID.STATE_OR_PROVINCE_NAME, "CA"),
                x509.NameAttribute(NameOID.LOCALITY_NAME, "San Francisco"),
                x509.NameAttribute(NameOID.ORGANIZATION_NAME, "IRCamera PC Controller"),
                x509.NameAttribute(NameOID.COMMON_NAME, "IRCamera CA"),
            ]
        )

        cert = (
            x509.CertificateBuilder()
            .subject_name(subject)
            .issuer_name(issuer)
            .public_key(private_key.public_key())
            .serial_number(x509.random_serial_number())
            .not_valid_before(datetime.now())
            .not_valid_after(datetime.now() + timedelta(days=365))
            .add_extension(
                x509.BasicConstraints(ca=True, path_length=None),
                critical=True,
            )
            .sign(private_key, hashes.SHA256())
        )

        with open(self.ca_cert_path, "wb") as f:
            f.write(cert.public_bytes(serialization.Encoding.PEM))

        with open(self.ca_key_path, "wb") as f:
            f.write(
                private_key.private_bytes(
                    encoding=serialization.Encoding.PEM,
                    format=serialization.PrivateFormat.PKCS8,
                    encryption_algorithm=serialization.NoEncryption(),
                )
            )

        logger.info(f"Generated CA certificate: {self.ca_cert_path}")

    def _generate_server_certificate(self):
        """Generate a new server certificate signed by the CA."""

        with open(self.ca_cert_path, "rb") as f:
            ca_cert = x509.load_pem_x509_certificate(f.read())

        with open(self.ca_key_path, "rb") as f:
            ca_key = serialization.load_pem_private_key(f.read(), password=None)

        # Generate server private key
        private_key = rsa.generate_private_key(
            public_exponent=65537,
            key_size=2048,
        )

        # Generate server certificate
        subject = x509.Name(
            [
                x509.NameAttribute(NameOID.COUNTRY_NAME, "US"),
                x509.NameAttribute(NameOID.STATE_OR_PROVINCE_NAME, "CA"),
                x509.NameAttribute(NameOID.LOCALITY_NAME, "San Francisco"),
                x509.NameAttribute(NameOID.ORGANIZATION_NAME, "IRCamera PC Controller"),
                x509.NameAttribute(NameOID.COMMON_NAME, "IRCamera Server"),
            ]
        )

        cert = (
            x509.CertificateBuilder()
            .subject_name(subject)
            .issuer_name(ca_cert.subject)
            .public_key(private_key.public_key())
            .serial_number(x509.random_serial_number())
            .not_valid_before(datetime.now())
            .not_valid_after(datetime.now() + timedelta(days=365))
            .add_extension(
                x509.SubjectAlternativeName(
                    [
                        x509.DNSName("localhost"),
                        x509.IPAddress(ipaddress.IPv4Address("127.0.0.1")),
                        x509.IPAddress(ipaddress.IPv4Address("192.168.1.1")),
                    ]
                ),
                critical=False,
            )
            .sign(cast(PrivateKeyTypes, ca_key), hashes.SHA256())
        )

        with open(self.server_cert_path, "wb") as f:
            f.write(cert.public_bytes(serialization.Encoding.PEM))

        with open(self.server_key_path, "wb") as f:
            f.write(
                private_key.private_bytes(
                    encoding=serialization.Encoding.PEM,
                    format=serialization.PrivateFormat.PKCS8,
                    encryption_algorithm=serialization.NoEncryption(),
                )
            )

        logger.info(f"Generated server certificate: {self.server_cert_path}")
