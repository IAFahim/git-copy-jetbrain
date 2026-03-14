# GitCopy IntelliJ Plugin - Usage Guide

## Installation & Setup

### 1. Install git-copy CLI Tool
```bash
# Clone the git-copy repository
git clone https://github.com/IAFahim/git-copy.git
cd git-copy

# Follow installation instructions in git-copy repository
# Common installation methods:
sudo make install        # Linux/macOS
# OR
cargo install git-copy   # If available via cargo
```

### 2. Install the Plugin
1. **From JetBrains Marketplace**:
   - Go to `Settings/Preferences` → `Plugins` → `Marketplace`
   - Search for "git-copy-jetbrain"
   - Click `Install`

2. **Manual Installation**:
   - Download plugin `.zip` from releases
   - `Settings/Preferences` → `Plugins` → `⚙️` → `Install plugin from disk`

### 3. Configure the Plugin
1. Go to `Settings/Preferences` → `Tools` → `git-copy`
2. Click `Auto-detect` to find git-copy automatically
3. Or browse to the git-copy executable manually
4. Click `Test Configuration` to verify the setup
5. Adjust copy options as needed

## Daily Usage

### Quick File/Folder Copy

1. **Right-click** on any file or folder in the Project View
2. Select **"Copy with git-copy"** from the context menu
3. Choose destination directory in the dialog
4. Optionally:
   - Change the name of the copy
   - Toggle copy options (git history, recursive, etc.)
5. Click **"Copy"** to start the operation

### Keyboard Shortcut

- **Windows/Linux**: `Ctrl+Shift+C`
- **macOS**: `Cmd+Shift+C`

### Copy Entire Project

1. Right-click on the project root
2. Select **"Copy Project with git-copy"**
3. Follow the same dialog steps

## Copy Options Explained

### Preserve Git History
- **What**: Maintains git history during copy
- **When to use**: When you want to keep git metadata
- **Default**: Enabled

### Recursive Copy
- **What**: Copies directories recursively
- **When to use**: For folders with nested content
- **Default**: Enabled

### Verbose Output
- **What**: Shows detailed progress information
- **When to use**: For troubleshooting or monitoring
- **Default**: Disabled

### Custom Arguments
- **What**: Pass additional git-copy CLI parameters
- **When to use**: Advanced git-copy features
- **Example**: `--exclude="*.tmp" --dry-run`

## Using the Tool Window

### Access the Tool Window
1. Go to `View` → `Tool Windows` → `GitCopy`
2. Or find it in the right sidebar

### Features
- **Copy History**: See all recent copy operations
- **Status Indicators**: ✓ Success / ✗ Failed
- **Duration Tracking**: How long each operation took
- **Statistics Dashboard**: Total, successful, failed operations
- **Quick Actions**: Refresh, clear history, copy again

## Settings & Configuration

### Executable Path
- **Auto-detect**: Searches system PATH automatically
- **Browse**: Manually select git-copy executable
- **Common locations**:
  - `/usr/local/bin/git-copy`
  - `/usr/bin/git-copy`
  - `~/.local/bin/git-copy`

### Copy Options
- Set default values for copy operations
- Saved per-project
- Can be overridden per-operation

### Notifications
- Enable/disable success notifications
- Error notifications always shown
- Rich HTML formatting with operation details

## Troubleshooting

### "git-copy not found"
1. **Install git-copy** from the official repository
2. **Check system PATH**: Run `which git-copy` in terminal
3. **Specify custom path** in plugin settings
4. **Test configuration** using the test button

### Copy Operation Fails
1. **Check permissions**: Ensure read access to source files
2. **Verify git-copy**: Test from command line first
3. **Enable verbose output**: See detailed operation logs
4. **Check disk space**: Ensure sufficient space for copy

### Keyboard Shortcuts Not Working
1. **Check conflicts**: `Settings` → `Keymap` → Search "git-copy"
2. **Reassign shortcuts**: Customize to your preference
3. **Enable shortcuts**: Ensure "Enable Keyboard Shortcuts" is checked in settings

## Advanced Usage

### Copy Templates (Coming Soon)
- Save frequently used copy configurations
- Quick access to common destination paths
- Batch operations on multiple files

### Integration with Git (Coming Soon)
- Branch-aware copying
- Repository structure preservation
- Gitignore-aware filtering

### Batch Operations (Coming Soon)
- Select multiple files/folders
- Apply same options to all
- Progress tracking for batch

## Performance Tips

### Large Files/Folders
- Enable verbose output for progress monitoring
- Consider copying during off-peak hours
- Ensure sufficient disk space beforehand

### Network Locations
- Use network paths with caution
- Test connectivity first
- Consider local copy then network transfer

## Security Considerations

### File Permissions
- Plugin respects file system permissions
- Preserves ownership where possible
- Handles permission errors gracefully

### Git History
- Git history copying respects git security
- Preserves commit metadata
- Handles sensitive data appropriately

## Tips & Tricks

### Quick Copy to Same Location
- Last destination is remembered automatically
- Repeated operations are faster

### Copy with Custom Name
- Use the "Name" field to rename during copy
- Useful for creating templates or backups

### Monitor Progress
- Use verbose output for detailed feedback
- Tool window shows operation duration
- Notifications indicate completion status

## Getting Help

### Built-in Help
- `Settings` → `Tools` → `git-copy` → `Installation Help`
- Shows installation instructions
- Links to documentation

### Documentation
- [GitHub Repository](https://github.com/IAFahim/git-copy-jetbrain)
- [Issue Tracker](https://github.com/IAFahim/git-copy-jetbrain/issues)
- [Wiki](https://github.com/IAFahim/git-copy-jetbrain/wiki)

### Community
- Report bugs and feature requests
- Share usage tips and tricks
- Contribute improvements

---

*This plugin is actively maintained. New features and improvements are regularly added.*