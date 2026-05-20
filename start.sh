#!/bin/bash
set -e

docker-compose up -d localstack
./gradlew bootRun
