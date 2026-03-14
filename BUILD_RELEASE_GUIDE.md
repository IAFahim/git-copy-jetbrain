# GitCopy IntelliJ Plugin - Build and Release Guide

## 🚀 Quick Build Instructions

### Prerequisites
```bash
# Install Java 21+
# Ubuntu/Debian:
sudo apt-get install openjdk-21-jdk

# macOS:
brew install openjdk@21

# Verify installation:
java -version  # Should show Java 21+
```

### Build the Plugin
```bash
# Make build script executable
chmod +x build-plugin.sh

# Run build script
./build-plugin.sh

# Or use Gradle directly:
./gradlew buildPlugin
```

### Build Output
- **Location**: `build/distributions/git-copy-jetbrain-*.zip`
- **Format**: ZIP file ready for installation
- **Contents**: Compiled plugin with all dependencies

## 📦 Release Process

### 1. Build the Plugin
```bash
./build-plugin.sh
```

### 2. Test the Plugin
```bash
# Run plugin in development IDE
./gradlew runIde

# Or run with specific IDE
./gradlew runIdeForUiTests
```

### 3. Create GitHub Release
```bash
# Install GitHub CLI if needed
# macOS: brew install gh
# Linux: https://github.com/cli/cli/blob/trunk/docs/install_linux.md

# Authenticate
gh auth login

# Create release
VERSION="0.0.1"  # Get from gradle.properties
gh release create "v$VERSION" \
  --title "git-copy-jetbrain v$VERSION" \
  --notes "Release notes here"

# Upload plugin file
gh release upload "v$VERSION" \
  build/distributions/git-copy-jetbrain-*.zip \
  --clobber
```

### 4. Verify Release
```bash
# List releases
gh release list

# View release details
gh release view "v$VERSION"
```

## 🎯 Release Checklist

### Pre-Release
- [ ] Update version in `gradle.properties`
- [ ] Update `CHANGELOG.md`
- [ ] Test all features manually
- [ ] Run validation script: `./validate-plugin.sh`
- [ ] Test in clean IDE environment

### Build Process
- [ ] Clean build: `./gradlew clean`
- [ ] Build plugin: `./gradlew buildPlugin`
- [ ] Verify output in `build/distributions/`
- [ ] Test plugin: `./gradlew runIde`
- [ ] Create release directory structure

### Release Creation
- [ ] Create git tag: `git tag -a v$VERSION -m "Release v$VERSION"`
- [ ] Push tag: `git push origin v$VERSION`
- [ ] Create GitHub release with notes
- [ ] Upload plugin zip file
- [ ] Add SHA256 checksums
- [ ] Update README badges

### Post-Release
- [ ] Update version to next development version
- [ ] Update CHANGELOG with unreleased section
- [ ] Create GitHub discussion for release
- [ ] Announce on social media/channels

## 📋 Version Management

### Semantic Versioning
```
MAJOR.MINOR.PATCH

0.0.1 - Initial release
0.1.0 - Add new features
0.2.0 - Breaking changes
1.0.0 - Stable production release
```

### Update Version
```bash
# Update gradle.properties
pluginVersion=0.0.1

# Update CHANGELOG.md
## [0.0.1] - 2025-01-15

### Added
- Initial release
- Core git-copy integration
```

## 🔧 Advanced Build Options

### Build for Specific IDE
```bash
# Build for IntelliJ IDEA
./gradlew buildPlugin -Pproduct=intellij

# Build for Rider
./gradlew buildPlugin -Pproduct=rider

# Build for specific version
./gradlew buildPlugin -PproductVersion=2024.2
```

### Build with Verification
```bash
# Run verification
./gradlew verifyPlugin

# Run tests
./gradlew test

# Build and verify
./gradlew clean build verifyPlugin
```

### Build with Code Coverage
```bash
# Run tests with coverage
./gradlew koverXmlReport

# Generate HTML report
./gradlew koverHtmlReport
```

## 📊 Plugin Distribution

### JetBrains Marketplace
1. Create account at https://plugins.jetbrains.com
2. Obtain plugin ID
3. Update `plugin.xml` with correct ID
4. Sign plugin (requires certificate)
5. Upload to marketplace

### Manual Distribution
1. Build plugin: `./gradlew buildPlugin`
2. Upload to GitHub Releases
3. Update README with download link
4. Share via community channels

### Development Builds
```bash
# Create development build
./gradlew buildPlugin -PdevBuild=true

# Build with snapshot dependencies
./gradlew buildPlugin -Psnapshot=true
```

## 🐛 Troubleshooting

### Build Failures
```bash
# Clean and rebuild
./gradlew clean build --no-daemon --stacktrace

# Clear Gradle cache
rm -rf .gradle build

# Check Java version
java -version  # Should be 21+
```

### Plugin Loading Issues
```bash
# Verify plugin structure
unzip -l build/distributions/*.zip

# Check plugin.xml
xmllint --noout src/main/resources/META-INF/plugin.xml

# Validate resources
./gradlew verifyPlugin
```

### Release Issues
```bash
# Check git tags
git tag -l

# Verify GitHub CLI
gh auth status

# Check release files
ls -la build/distributions/
```

## 📝 Release Notes Template

```markdown
## GitCopy JetBrains Plugin v{VERSION}

### ✨ Highlights
- Feature 1 description
- Feature 2 description
- Feature 3 description

### 🚀 New Features
- **Feature Name**: Description
- **Feature Name**: Description

### 🔧 Improvements
- Improvement 1
- Improvement 2

### 🐛 Bug Fixes
- Bug fix 1
- Bug fix 2

### 📚 Documentation
- Documentation improvements

### 🔒 Requirements
- IntelliJ IDEA 2025.2+ or compatible IDE
- git-copy CLI tool installed
- Java 21+

### 📦 Installation
- Download plugin ZIP
- Install via: Settings → Plugins → ⚙️ → Install from disk
- Or install from JetBrains Marketplace

### 🙏 Credits
- Built with [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- Integrates [git-copy](https://github.com/IAFahim/git-copy) tool

### 📄 Changelog
Full changelog: [CHANGELOG.md](https://github.com/IAFahim/git-copy-jetbrain/blob/main/CHANGELOG.md)
```

## 🎉 Automated Release Script

```bash
#!/bin/bash
# automated-release.sh

VERSION=$(grep "pluginVersion" gradle.properties | cut -d'=' -f2 | tr -d ' ')

# 1. Build plugin
./build-plugin.sh

# 2. Create git tag
git tag -a "v$VERSION" -m "Release v$VERSION"
git push origin "v$VERSION"

# 3. Create GitHub release
gh release create "v$VERSION" \
  --title "GitCopy JetBrains Plugin v$VERSION" \
  --notes-file RELEASE_NOTES.md

# 4. Upload plugin
gh release upload "v$VERSION" \
  build/distributions/git-copy-jetbrain-*.zip

echo "🎉 Release v$VERSION created successfully!"
```

This comprehensive guide covers all aspects of building and releasing the GitCopy IntelliJ plugin.