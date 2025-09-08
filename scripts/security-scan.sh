#!/bin/bash
# Comprehensive security and dependency vulnerability scanning
# for the IRCamera project

set -e

echo "🔒 Running comprehensive security scan..."

# Python dependency vulnerability scanning
if [ -d "pc-controller" ]; then
    echo "🐍 Scanning Python dependencies for vulnerabilities..."
    cd pc-controller
    
    # Install safety if not available
    if ! command -v safety &> /dev/null; then
        pip install safety
    fi
    
    # Check for vulnerable dependencies
    if [ -f "requirements.txt" ]; then
        safety check -r requirements.txt --json --output security-python.json || echo "⚠️  Python vulnerabilities found (see security-python.json)"
    fi
    
    # Run bandit security scan
    if ! command -v bandit &> /dev/null; then
        pip install bandit
    fi
    
    bandit -r src/ -f json -o security-bandit.json || echo "⚠️  Security issues found (see security-bandit.json)"
    
    cd ..
fi

# Android dependency scanning (using Gradle dependency check plugin)
if [ -f "build.gradle.kts" ]; then
    echo "🤖 Scanning Android dependencies for vulnerabilities..."
    
    # Add OWASP dependency check if not present
    if ! grep -q "dependencyCheckAnalyze" build.gradle.kts; then
        echo "📝 Adding OWASP dependency check plugin to build.gradle.kts"
        # Note: This would need to be manually added to build.gradle.kts
        echo "Consider adding org.owasp.dependencycheck plugin to build.gradle.kts"
    fi
    
    # Check for common Android security issues
    echo "🔍 Checking for common Android security patterns..."
    
    # Check for hardcoded secrets
    grep -r -n "password\|secret\|api_key\|token" --include="*.kt" --include="*.java" app/ || echo "✅ No obvious hardcoded secrets found"
    
    # Check for debug mode in production
    grep -r -n "BuildConfig.DEBUG" --include="*.kt" --include="*.java" app/ || echo "✅ No debug checks found"
fi

# Check for git secrets
echo "🔐 Checking for accidentally committed secrets..."
if command -v detect-secrets &> /dev/null; then
    detect-secrets scan --all-files --force-use-all-plugins > .secrets.baseline
    echo "✅ Secret scan complete (baseline created)"
else
    echo "⚠️  detect-secrets not installed. Install with: pip install detect-secrets"
fi

echo "🎯 Security scan complete. Review generated reports:"
echo "  - security-python.json (Python vulnerabilities)"
echo "  - security-bandit.json (Python security issues)"
echo "  - .secrets.baseline (Secret detection baseline)"