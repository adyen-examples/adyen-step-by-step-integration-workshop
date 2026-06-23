#!/usr/bin/env bash

# ------------------------------------------------------------------------------
# Hot reload helper for Spring Boot
#
# Run this script in a separate terminal. It watches your source files and
# automatically recompiles them when you save.
#
# In another terminal, start your app with:
#   ./gradlew bootRun
#
# The app will restart automatically on changes, 
# so you can just refresh your browser without manually stopping and restarting the process.
#
# After you changed a file, it might take a few seconds for hot reload to reload your application.
# ------------------------------------------------------------------------------

set -e

echo "👀 Starting Gradle continuous class compilation..."

# Ensure wrapper exists
if [ ! -f "./gradlew" ]; then
  echo "⚙️ Generating Gradle wrapper..."
  gradle wrapper
fi

chmod +x ./gradlew

# Clean shutdown
cleanup() {
  echo ""
  echo "🛑 Stopping watcher..."
  kill 0
}
trap cleanup EXIT

# Start continuous compilation
./gradlew classes --continuous -x test