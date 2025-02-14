#!/bin/bash
# This script installs the Docker Compose plugin on Ubuntu.
# It assumes that Docker is already installed.
# It exits immediately if any command fails.
set -euo pipefail

# Function to print messages in a consistent format.
log() {
  echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*"
}

# Check if Docker is installed.
if ! command -v docker &> /dev/null; then
  log "Error: Docker is not installed. Please install Docker first."
  exit 1
else
  log "Docker is installed: $(docker --version)"
fi

# Check if 'docker compose' command works.
if docker compose version &> /dev/null; then
  log "Docker Compose plugin is already installed: $(docker compose version --short)"
  exit 0
else
  log "Docker Compose plugin not found. Proceeding with installation."
fi

# Attempt installation via apt-get (recommended on Ubuntu).
log "Updating package lists..."
sudo apt-get update

log "Installing docker-compose-plugin via apt-get..."
if sudo apt-get install -y docker-compose-plugin; then
  log "Docker Compose plugin installed successfully via apt-get."
else
  log "Warning: Failed to install via apt-get. Attempting manual installation."
  # Fallback: manual installation of the Docker Compose binary.
  COMPOSE_VERSION="v2.18.1"  # Adjust the version if needed.
  INSTALL_PATH="/usr/local/bin/docker-compose"
  log "Downloading Docker Compose ${COMPOSE_VERSION}..."
  sudo curl -L "https://github.com/docker/compose/releases/download/${COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)" -o "${INSTALL_PATH}"
  sudo chmod +x "${INSTALL_PATH}"
  log "Docker Compose binary installed manually at ${INSTALL_PATH}."
fi

# Verify installation.
if docker compose version &> /dev/null; then
  log "Installation complete. Docker Compose version: $(docker compose version --short)"
else
  log "Error: Docker Compose installation failed."
  exit 1
fi
