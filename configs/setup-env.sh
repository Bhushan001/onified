#!/bin/bash
# setup-env.sh
# Usage: bash setup-env.sh [env]
# Example: bash setup-env.sh dev
# Requires: jq (https://stedolan.github.io/jq/)
# This script reads config.<env>.json and writes all variables to a .env file

ENV="${1:-dev}"
CONFIG_FILE="config.$ENV.json"
ENV_FILE=".env"

if ! command -v jq &> /dev/null; then
  echo "Error: jq is not installed. Please install jq to use this script."
  exit 1
fi

if [ ! -f "$CONFIG_FILE" ]; then
  echo "Error: $CONFIG_FILE not found!"
  exit 1
fi

echo "# Auto-generated .env file from $CONFIG_FILE" > "$ENV_FILE"

# Extract all key-value pairs from all objects in config.<env>.json
jq -r 'to_entries[] | .value | to_entries[] | "\(.key)=\(.value)"' "$CONFIG_FILE" >> "$ENV_FILE"

echo ".env file generated successfully from $CONFIG_FILE." 