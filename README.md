# git-copy-jetbrain

![Build](https://github.com/IAFahim/git-copy-jetbrain/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

<!-- Plugin description -->
**git-copy-jetbrain** is an IntelliJ Platform plugin that brings the power of [git-copy](https://github.com/IAFahim/git-copy) directly to your IDE.

This plugin integrates the git-copy command-line tool into IntelliJ-based IDEs, providing seamless one-click file and folder copying operations with git intelligence.

## Features

- **Context Menu Integration**: Right-click any file or folder in your project and select "Copy with git-copy"
- **Project-Level Operations**: Copy entire projects with a single click
- **Intelligent Copying**: Preserve git history, handle .gitignore patterns, and maintain repository structure
- **Background Processing**: Copy operations run in the background with progress indicators
- **Customizable Settings**: Configure git-copy options to match your workflow
- **Cross-Platform**: Works on Windows, macOS, and Linux
- **Multiple IDE Support**: Compatible with IntelliJ IDEA, Rider, WebStorm, PyCharm, and other JetBrains IDEs

## Usage

### Quick Start

1. **Install git-copy** on your system if you haven't already:
   ```bash
   # Clone and install git-copy
   git clone https://github.com/IAFahim/git-copy.git
   cd git-copy
   # Follow installation instructions in the git-copy repository
   ```

2. **Configure the plugin**:
   - Go to <kbd>Settings/Preferences</kbd> > <kbd>Tools</kbd> > <kbd>git-copy</kbd>
   - The plugin will auto-detect git-copy if it's in your system PATH
   - Or specify a custom path to the git-copy executable

3. **Use the plugin**:
   - Right-click any file/folder in the Project View
   - Select <kbd>Copy with git-copy</kbd>
   - The file/folder will be copied using git-copy with all configured options

### Context Menu Options

- **Copy with git-copy**: Copy selected file/folder
- **Copy Project with git-copy**: Copy entire project (available in project context menu and Tools menu)

### Keyboard Shortcuts

- **Ctrl+Shift+C** (Windows/Linux) or **Cmd+Shift+C** (Mac): Copy selected file/folder with git-copy

### Configuration Options

In <kbd>Settings</kbd> > <kbd>Tools</kbd> > <kbd>git-copy</kbd>:

- **git-copy Executable Path**: Auto-detect or specify custom path
- **Preserve Git History**: Maintain git history during copy operations
- **Recursive Copy**: Copy directories recursively (default: enabled)
- **Verbose Output**: Show detailed copy operation logs
- **Custom Arguments**: Pass additional command-line arguments to git-copy

## Installation

- **Using IDE Built-in Plugin System**:
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "git-copy-jetbrain"</kbd> > <kbd>Install</kbd>

- **Using JetBrains Marketplace**:
  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) and install it directly.

- **Manual Installation**:
  Download from [GitHub Releases](https://github.com/IAFahim/git-copy-jetbrain/releases/latest) and install via
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Requirements

- **IDE**: IntelliJ IDEA 2025.2+ or compatible JetBrains IDE
- **git-copy**: The git-copy command-line tool must be installed on your system
- **Operating System**: Windows, macOS, or Linux

## Troubleshooting

### git-copy not found

If the plugin shows "git-copy not found":

1. **Install git-copy** from the official repository
2. **Check system PATH**: Ensure git-copy is accessible from command line
3. **Specify custom path**: In plugin settings, provide the full path to the git-copy executable
4. **Test configuration**: Use the "Test Configuration" button in settings

### Copy operation fails

1. **Check file permissions**: Ensure you have read access to source files
2. **Verify git-copy installation**: Test git-copy from command line first
3. **Enable verbose output**: In plugin settings, enable verbose mode for detailed logs
4. **Check disk space**: Ensure sufficient space for the copy operation

### Keyboard shortcuts not working

1. **Check conflicts**: Verify no other plugins are using the same shortcuts
2. **Reassign shortcuts**: Customize in <kbd>Settings</kbd> > <kbd>Keymap</kbd> > <kbd>git-copy</kbd>
3. **Enable shortcuts**: Ensure "Enable Keyboard Shortcuts" is checked in plugin settings

## Development

### Building from Source

```bash
# Clone the repository
git clone https://github.com/IAFahim/git-copy-jetbrain.git
cd git-copy-jetbrain

# Build the plugin
./gradlew buildPlugin

# Run in development mode
./gradlew runIde
```

### Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for version history and changes.

## License

This plugin is licensed under the [MIT License](LICENSE).

## Credits

- Built with [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- Integrates the [git-copy](https://github.com/IAFahim/git-copy) tool

## Support

- **Issues**: [GitHub Issues](https://github.com/IAFahim/git-copy-jetbrain/issues)
- **Discussions**: [GitHub Discussions](https://github.com/IAFahim/git-copy-jetbrain/discussions)
- **Email**: iafahim@example.com

<!-- Plugin description end -->
