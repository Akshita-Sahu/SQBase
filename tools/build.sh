#!/usr/bin/env sh

set -e

###############################################################################
# Function Definitions
###############################################################################

# Get the directory where the script is located
# Returns: Absolute path to script directory
SCRIPT_DIR="$(realpath "$(dirname "$0")")"

# Function: log
# Purpose: Provides consistent logging format with timestamps
# Arguments:
#   $1 - Message to log
log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] $1"
}

# Function: command_exists
# Purpose: Checks if a required command is available
# Arguments:
#   $1 - Command to check
# Returns:
#   0 if command exists, 1 if it doesn't
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

###############################################################################
# Dependency Checks
###############################################################################

if ! command_exists 'git'; then
    log "Error: Git is required but not installed"
    log "Please install Git and try again"
    exit 1
fi

###############################################################################
# Path Definitions and Validation
###############################################################################

# Define paths relative to the script location
WORKSPACE_DIR="$(dirname "$(dirname "$SCRIPT_DIR")")"
DBEAVER_COMMON_DIR="${WORKSPACE_DIR}/sqbase-common"
DBEAVER_JDBC_LIBSQL_DIR="${WORKSPACE_DIR}/sqbase-jdbc-libsql"
PRODUCT_DIR="${SCRIPT_DIR}/../product"
AGGREGATE_DIR="${PRODUCT_DIR}/aggregate"

# Simple check for product directory
if [ ! -d "$PRODUCT_DIR" ]; then
    log "Error: Product directory not found at $PRODUCT_DIR"
    exit 1
fi

###############################################################################
# SQBase Common Repository Management
###############################################################################

# Clone or verify sqbase-common repository
if [ ! -d "$DBEAVER_COMMON_DIR" ]; then
    log "Cloning sqbase-common repository..."
    git clone https://github.com/sqbase/sqbase-common.git "$DBEAVER_COMMON_DIR"
else
    log "SQBase common directory already exists at $DBEAVER_COMMON_DIR"
fi

###############################################################################
# SQBase Jdbc-Libsql Repository Management
###############################################################################

# Clone or verify sqbase-jdbc-libsql repository
if [ ! -d "$DBEAVER_JDBC_LIBSQL_DIR" ]; then
    log "Cloning sqbase-jdbc-libsql repository..."
    git clone https://github.com/sqbase/sqbase-jdbc-libsql.git "$DBEAVER_JDBC_LIBSQL_DIR"
else
    log "SQBase jdbc-libsql directory already exists at $DBEAVER_JDBC_LIBSQL_DIR"
fi

###############################################################################
# Build Process
###############################################################################

# Execute Maven build
log "Starting Maven build..."

"$DBEAVER_COMMON_DIR/mvnw" clean install -Pproduct-sqbase-ce,product-sqbase-eclipse-ce,appstore -T 1C -f "$AGGREGATE_DIR"

log "Build completed successfully"
