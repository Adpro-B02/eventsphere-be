#!/bin/bash
set -a  # Export all variables created next
source ./.env  # Load variables from .env
set +a  # Stop exporting variables
./gradlew bootRun  # Run Gradle with variables loaded