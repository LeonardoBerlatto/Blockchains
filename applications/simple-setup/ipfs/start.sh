#!/bin/bash

# Import util scrips
. scripts/clean-util.sh

# Remove old ipfs containers
removeContainers

# Build new ipfs network
docker-compose up -d