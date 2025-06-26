#!/bin/bash

# Script to create .env from env.example

set -e

if [ ! -f env.example ]; then
  echo "[ERROR] env.example not found!"
  exit 1
fi

if [ -f .env ]; then
  read -p ".env already exists. Overwrite? (y/N): " confirm
  if [[ ! "$confirm" =~ ^[Yy]$ ]]; then
    echo "Aborted. .env not overwritten."
    exit 0
  fi
fi

cp env.example .env

echo "[SUCCESS] .env file created from env.example." 