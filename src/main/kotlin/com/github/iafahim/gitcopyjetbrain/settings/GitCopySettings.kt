package com.github.iafahim.gitcopyjetbrain.settings

import com.intellij.openapi.components.SimplePersistentComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

/**
 * Settings for the git-copy plugin.
 * Stores configuration preferences and options.
 */
@State(name = "GitCopySettings", storages = [Storage("git-copy.xml")])
class GitCopySettings : SimplePersistentComponent<GitCopySettings.State>(State()) {

    companion object {
        fun getInstance(project: Project): GitCopySettings {
            return project.service()
        }
    }

    class State {
        var customGitCopyPath: String = ""
        var preserveGitHistory: Boolean = true
        var recursiveCopy: Boolean = true
        var verboseOutput: Boolean = false
        var customArguments: String = ""
        var showNotifications: Boolean = true
        var enableKeyboardShortcut: Boolean = true
        var lastUsedDestination: String = ""
    }

    var customGitCopyPath: String by state::customGitCopyPath
    var preserveGitHistory: Boolean by state::preserveGitHistory
    var recursiveCopy: Boolean by state::recursiveCopy
    var verboseOutput: Boolean by state::verboseOutput
    var customArguments: String by state::customArguments
    var showNotifications: Boolean by state::showNotifications
    var enableKeyboardShortcut: Boolean by state::enableKeyboardShortcut
    var lastUsedDestination: String by state::lastUsedDestination
}