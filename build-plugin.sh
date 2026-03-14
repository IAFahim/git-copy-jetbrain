#!/bin/bash

# GitCopy IntelliJ Plugin - Build Script
# This script builds the plugin and prepares it for release

set -e  # Exit on error

echo "🚀 Building GitCopy IntelliJ Plugin..."
echo "======================================="

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check Java installation
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java is not installed${NC}"
    echo "Please install Java 21+ to build this plugin:"
    echo "  sudo apt-get install openjdk-21-jdk"
    echo "  brew install openjdk@21"
    exit 1
fi

echo -e "${GREEN}✓${NC} Java found: $(java -version 2>&1 | head -1)"

# Check Gradle
if ! command -v ./gradlew &> /dev/null && [ ! -f "./gradlew" ]; then
    echo -e "${RED}❌ Gradle wrapper not found${NC}"
    exit 1
fi

echo -e "${GREEN}✓${NC} Gradle wrapper found"

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Build the plugin
echo "🔨 Building plugin..."
./gradlew buildPlugin

# Check if build succeeded
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓${NC} Build completed successfully!"
else
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

# Find the generated plugin file
PLUGIN_FILE=$(find build/distributions -name "*.zip" | head -1)

if [ -z "$PLUGIN_FILE" ]; then
    echo -e "${RED}❌ Plugin file not found in build/distributions${NC}"
    exit 1
fi

echo -e "${GREEN}✓${NC} Plugin file created: $PLUGIN_FILE"

# Get plugin version
VERSION=$(grep "pluginVersion" gradle.properties | cut -d'=' -f2 | tr -d ' ')
echo -e "${GREEN}✓${NC} Plugin version: $VERSION"

# Create release directory
RELEASE_DIR="release"
mkdir -p "$RELEASE_DIR"

# Copy plugin file to release directory
cp "$PLUGIN_FILE" "$RELEASE_DIR/git-copy-jetbrain-$VERSION.zip"

# Calculate checksums
echo "📋 Calculating checksums..."
cd "$RELEASE_DIR"
shasum -a 256 "git-copy-jetbrain-$VERSION.zip" > "git-copy-jetbrain-$VERSION.zip.sha256"
md5 "git-copy-jetbrain-$VERSION.zip" > "git-copy-jetbrain-$VERSION.zip.md5" 2>/dev/null || echo "MD5 not available"

echo ""
echo "🎉 Build completed successfully!"
echo "================================"
echo -e "${GREEN}✓${NC} Plugin file: $RELEASE_DIR/git-copy-jetbrain-$VERSION.zip"
echo -e "${GREEN}✓${NC} SHA256: $RELEASE_DIR/git-copy-jetbrain-$VERSION.zip.sha256"
echo ""
echo "Next steps:"
echo "1. Test the plugin: ./gradlew runIde"
echo "2. Create GitHub release: gh release create v$VERSION --title 'v$VERSION' --notes 'Release notes here'"
echo "3. Upload plugin: gh release upload v$VERSION git-copy-jetbrain-$VERSION.zip"
echo ""
echo -e "${BLUE}📦 Plugin is ready for distribution!${NC}"