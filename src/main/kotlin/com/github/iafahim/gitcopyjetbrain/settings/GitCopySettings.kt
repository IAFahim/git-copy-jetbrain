package com.github.iafahim.gitcopyjetbrain.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

/**
 * Settings for the git-copy plugin.
 * Stores configuration preferences and options.
 */
@State(name = "GitCopySettings", storages = [Storage("git-copy.xml")])
class GitCopySettings : PersistentStateComponent<GitCopySettings.State> {

    companion object {
        fun getInstance(project: Project): GitCopySettings {
            return project.service()
        }
    }

    private var state = State()

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

    var customGitCopyPath: String
        get() = state.customGitCopyPath
        set(value) { state.customGitCopyPath = value }

    var preserveGitHistory: Boolean
        get() = state.preserveGitHistory
        set(value) { state.preserveGitHistory = value }

    var recursiveCopy: Boolean
        get() = state.recursiveCopy
        set(value) { state.recursiveCopy = value }

    var verboseOutput: Boolean
        get() = state.verboseOutput
        set(value) { state.verboseOutput = value }

    var customArguments: String
        get() = state.customArguments
        set(value) { state.customArguments = value }

    var showNotifications: Boolean
        get() = state.showNotifications
        set(value) { state.showNotifications = value }

    var enableKeyboardShortcut: Boolean
        get() = state.enableKeyboardShortcut
        set(value) { state.enableKeyboardShortcut = value }

    var lastUsedDestination: String
        get() = state.lastUsedDestination
        set(value) { state.lastUsedDestination = value }

    override fun getState(): State {
        return state
    }

    override fun loadState(state: State) {
        this.state = state
    }
}