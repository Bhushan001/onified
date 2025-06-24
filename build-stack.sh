#!/bin/bash
# build-stack.sh
# Usage: ./build-stack.sh [env]
# Example: ./build-stack.sh dev
# This script generates .env for the specified environment and builds the stack

ENV=${1:-local}
echo "Generating .env for environment: $ENV"
bash configs/setup-env.sh $ENV
echo "Bringing up the stack with docker-compose..."
docker-compose up --build 