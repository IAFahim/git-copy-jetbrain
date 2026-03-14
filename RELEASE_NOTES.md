# GitCopy JetBrains Plugin v0.0.1 - Release Notes

## 🎉 First Public Release

**GitCopy JetBrains Plugin v0.0.1** is now available! This plugin brings the power of git-copy directly to your JetBrains IDE.

## ✨ Features

### Core Functionality
- **Context Menu Integration**: Right-click any file/folder and select "Copy with git-copy"
- **Project-Level Operations**: Copy entire projects with a single action
- **Destination Selection**: User-friendly dialog with file browser
- **Background Processing**: Non-blocking operations with progress indicators
- **Operation History**: Complete tracking of all copy operations
- **Tool Window**: Dashboard showing recent operations and statistics

### Smart Features
- **Auto-Detection**: Automatically finds git-copy executable in system PATH
- **Custom Options**: Preserve git history, recursive copying, verbose output
- **Last Destination Memory**: Remembers your last used destination
- **Rich Notifications**: HTML-formatted success/error messages
- **Keyboard Shortcuts**: `Ctrl+Shift+C` (Windows/Linux) / `Cmd+Shift+C` (Mac)

### Configuration
- **Professional Settings UI**: Accessible via Settings → Tools → git-copy
- **Custom Path Support**: Specify custom git-copy executable location
- **Validation**: Test configuration with a single click
- **Installation Help**: Built-in guidance for git-copy installation

## 🔧 Technical Details

### Plugin Information
- **Plugin ID**: `com.github.iafahim.gitcopyjetbrain`
- **Version**: `0.0.1`
- **Build**: 2025.0.1
- **Size**: 75 KB
- **Requirements**:
  - IntelliJ IDEA 2025.2+ or compatible JetBrains IDE
  - Java 21+
  - git-copy CLI tool installed

### Compatibility
- ✅ **IntelliJ IDEA** 2025.2+
- ✅ **Rider** (JetBrains .NET IDE)
- ✅ **WebStorm** (JavaScript IDE)
- ✅ **PyCharm** (Python IDE)
- ✅ **PhpStorm** (PHP IDE)
- ✅ And other JetBrains IDEs

### Operating Systems
- ✅ **Windows** 10/11
- ✅ **macOS** (Intel & Apple Silicon)
- ✅ **Linux** (All major distributions)

## 📦 Installation

### Manual Installation
1. Download `git-copy-jetbrain-0.0.1.zip`
2. Open your JetBrains IDE
3. Go to: `Settings` → `Plugins` → `⚙️` → `Install Plugin from Disk`
4. Select the downloaded ZIP file
5. Restart IDE

### Prerequisites
```bash
# Install git-copy CLI tool first
git clone https://github.com/IAFahim/git-copy.git
cd git-copy
# Follow installation instructions in git-copy repository
```

## 🚀 Usage

### Quick Start
1. **Right-click** any file/folder in your project
2. **Select** "Copy with git-copy" from context menu
3. **Choose destination** in the dialog
4. **Click "Copy"** - Done!

### Keyboard Shortcut
- **Windows/Linux**: `Ctrl+Shift+C`
- **macOS**: `Cmd+Shift+C`

### Configuration
1. Go to `Settings` → `Tools` → `git-copy`
2. Click `Auto-detect` to find git-copy
3. Test configuration
4. Customize copy options

## 🎯 Use Cases

- **File Copy**: Quick file duplication with git awareness
- **Folder Copy**: Recursive folder copying with options
- **Project Backup**: Entire project backup with one click
- **Template Creation**: Copy projects with custom names
- **Git History Preservation**: Maintain git metadata

## 📚 Documentation

- **User Guide**: [USAGE_GUIDE.md](https://github.com/IAFahim/git-copy-jetbrain/blob/main/USAGE_GUIDE.md)
- **Testing Guide**: [TESTING_CHECKLIST.md](https://github.com/IAFahim/git-copy-jetbrain/blob/main/TESTING_CHECKLIST.md)
- **Build Guide**: [BUILD_RELEASE_GUIDE.md](https://github.com/IAFahim/git-copy-jetbrain/blob/main/BUILD_RELEASE_GUIDE.md)

## 🐛 Known Issues

None reported in this initial release.

## 🙏 Credits

- **Built with**: [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- **Integrates**: [git-copy](https://github.com/IAFahim/git-copy) tool
- **Author**: iafahim

## 📄 License

This plugin is licensed under the MIT License.

## 🔗 Links

- **GitHub Repository**: [IAFahim/git-copy-jetbrain](https://github.com/IAFahim/git-copy-jetbrain)
- **Issue Tracker**: [GitHub Issues](https://github.com/IAFahim/git-copy-jetbrain/issues)
- **git-copy Tool**: [IAFahim/git-copy](https://github.com/IAFahim/git-copy)

---

**Enjoy seamless git-copy integration in your JetBrains IDE!** 🚀✨

*Plugin file size: 75 KB | Release date: March 15, 2025*