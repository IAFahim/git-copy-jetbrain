# GitCopy IntelliJ Plugin - Testing Checklist

## Pre-Build Testing

### Code Structure Validation
- [x] All source files exist and are properly structured
- [x] Plugin.xml contains all required actions and extensions
- [x] Message bundle contains all required strings
- [x] Dependencies properly configured in build.gradle.kts

### Syntax Validation
- [x] Kotlin files follow proper syntax
- [x] Import statements are correct
- [x] No obvious compilation errors

## Functional Testing Requirements

### Installation & Setup
- [ ] Plugin installs successfully in IntelliJ IDEA
- [ ] Plugin appears in Plugins list
- [ ] Settings page accessible: Settings → Tools → git-copy
- [ ] Plugin works in different JetBrains IDEs (Rider, WebStorm, etc.)

### git-copy Detection
- [ ] Auto-detect finds git-copy in system PATH
- [ ] Browse button works for custom path selection
- [ ] Test Configuration button validates setup correctly
- [ ] Status indicator shows correct git-copy availability
- [ ] Handles missing git-copy gracefully with helpful error message

### User Interface
- [ ] Context menu appears on right-click for files
- [ ] Context menu appears on right-click for folders
- [ ] Context menu items have correct text
- [ ] Keyboard shortcuts work (Ctrl+Shift+C / Cmd+Shift+C)
- [ ] Settings UI is responsive and user-friendly
- [ ] Tool window appears and displays correctly

### File Copy Operations
- [ ] Single file copy works
- [ ] Single folder copy works
- [ ] Folder copy with subfolders works (recursive)
- [ ] Copy preserves file permissions
- [ ] Copy handles special characters in filenames
- [ ] Copy handles long file paths
- [ ] Copy handles symbolic links correctly

### Destination Selection
- [ ] Destination dialog appears when triggered
- [ ] Source path displayed correctly
- [ ] Browse button opens file chooser
- [ ] Destination validation works
- [ ] Name field accepts custom names
- [ ] Overwrite warning shows for existing files
- [ ] Last used destination is remembered

### Copy Options
- [ ] Preserve Git History option works
- [ ] Recursive Copy option works
- [ ] Verbose Output option shows detailed progress
- [ ] Custom Arguments are passed to git-copy
- [ ] Option defaults are configurable in settings
- [ ] Options persist across operations

### Progress & Feedback
- [ ] Progress indicator appears during copy
- [ ] Progress percentage updates correctly
- [ ] Cancellation works (Cancel button)
- [ ] Success notification shows operation details
- [ ] Error notification shows helpful error message
- [ ] Notification includes duration information

### History & Tool Window
- [ ] Tool window shows recent operations
- [ ] Operations display correct status (success/failure)
- [ ] Operation duration is tracked
- [ ] Clear history button works
- [ ] Refresh button updates the display
- [ ] Statistics dashboard shows correct numbers
- [ ] Copy Again functionality works

### Error Handling
- [ ] Handles missing git-copy gracefully
- [ ] Handles permission errors
- [ ] Handles disk full errors
- [ ] Handles invalid destination paths
- [ ] Handles network location issues
- [ ] Handles git-copy process crashes
- [ ] Provides helpful error messages for all scenarios

### Settings Persistence
- [ ] Settings persist across IDE restarts
- [ ] Project-specific settings work correctly
- [ ] Application-level settings work correctly
- [ ] Settings export/import works (if implemented)

### Performance
- [ ] Small file copy is fast (< 1 second)
- [ ] Large file copy shows proper progress
- [ ] Multiple operations don't block UI
- [ ] Memory usage is reasonable
- [ ] No memory leaks during extended use

## Cross-Platform Testing

### Windows
- [ ] Plugin works on Windows 10/11
- [ ] Path handling works with backslashes
- [ ] Shortcuts work correctly
- [ ] File system operations work correctly

### macOS
- [ ] Plugin works on macOS (Intel and Apple Silicon)
- [ ] Path handling works correctly
- [ ] Shortcuts work with Cmd key
- [ ] File system operations work correctly

### Linux
- [ ] Plugin works on major Linux distributions
- [ ] Path handling works correctly
- [ ] Shortcuts work with Ctrl key
- [ ] File system operations work correctly

## Integration Testing

### Git Integration
- [ ] Works with Git-tracked files
- [ ] Respects .gitignore patterns (if applicable)
- [ ] Handles git history preservation correctly

### Project Integration
- [ ] Works with different project types
- [ ] Works with external projects
- [ ] Works with multiple open projects
- [ ] Project-specific settings work correctly

### IDE Integration
- [ ] Works with different IntelliJ IDEA versions
- [ ] Works with Rider, PyCharm, WebStorm, etc.
- [ ] Doesn't conflict with other plugins
- [ ] Respects IDE theming

## Edge Cases & Stress Testing

### Edge Cases
- [ ] Copy to same directory (with different name)
- [ ] Copy file with no write permission
- [ ] Copy very large file (>1GB)
- [ ] Copy file with very long name
- [ ] Copy file with special characters
- [ ] Copy to network location
- [ ] Copy when disk space is low

### Stress Testing
- [ ] Multiple consecutive operations
- [ ] Operation while another operation is running
- [ ] Very deep folder structure
- [ ] Thousands of small files
- [ ] Mix of file types and sizes

## Documentation Testing

### User Documentation
- [ ] README is clear and comprehensive
- [ ] Usage guide covers all features
- [ ] Troubleshooting section is helpful
- [ ] Installation instructions are accurate

### Developer Documentation
- [ ] Code is well-commented
- [ ] API documentation is clear
- [ ] Build instructions are accurate
- [ ] Contribution guidelines exist

## Regression Testing

### After Updates
- [ ] Previous features still work
- [ ] Settings are preserved
- [ ] No new bugs introduced
- [ ] Performance not degraded

## Accessibility Testing

### User Interface
- [ ] All UI elements are keyboard accessible
- [ ] Screen reader compatibility
- [ ] High contrast mode support
- [ ] Font scaling works correctly

## Security Testing

### Security Considerations
- [ ] Handles sensitive file paths correctly
- [ ] No security vulnerabilities in file operations
- [ ] Safe handling of user input
- [ ] Proper error messages (no information leakage)

## Performance Benchmarks

### Expected Performance
- [ ] Small file (< 1MB): < 2 seconds
- [ ] Medium file (1-100MB): < 10 seconds
- [ ] Large file (> 100MB): Reasonable progress updates
- [ ] Folder copy: Progress updates regularly
- [ ] UI remains responsive during operations

## Release Readiness

### Pre-Release Checklist
- [ ] All critical bugs fixed
- [ ] Performance acceptable
- [ ] Documentation complete
- [ ] Testing coverage adequate
- [ ] No regressions from previous versions
- [ ] Ready for JetBrains Marketplace submission

---

## Test Execution Instructions

1. **Setup Testing Environment**: Install git-copy and plugin in clean IDE
2. **Execute Tests**: Go through each checklist item systematically
3. **Document Results**: Record pass/fail for each test
4. **Report Bugs**: Create issues for any failures
5. **Retest After Fixes**: Verify bug fixes don't break other features

## Bug Report Template

```markdown
**Description**: [Clear description of the issue]

**Steps to Reproduce**:
1. [Step 1]
2. [Step 2]
3. [Step 3]

**Expected Behavior**: [What should happen]

**Actual Behavior**: [What actually happens]

**Environment**:
- OS: [Windows/macOS/Linux]
- IDE: [IntelliJ IDEA/Rider/etc.]
- Plugin Version: [Version number]
- git-copy Version: [Version number]

**Logs**: [Relevant error logs or screenshots]
```

This comprehensive testing checklist ensures the plugin meets the highest quality standards before release.