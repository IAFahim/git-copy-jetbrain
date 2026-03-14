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
        var defaultFilters: String = ""
        var defaultExcludes: String = ""
        var verboseOutput: Boolean = false
        var showNotifications: Boolean = true
        var enableKeyboardShortcut: Boolean = true
        var lastUsedOptions: String = ""
    }

    var customGitCopyPath: String
        get() = state.customGitCopyPath
        set(value) { state.customGitCopyPath = value }

    var defaultFilters: String
        get() = state.defaultFilters
        set(value) { state.defaultFilters = value }

    var defaultExcludes: String
        get() = state.defaultExcludes
        set(value) { state.defaultExcludes = value }

    var verboseOutput: Boolean
        get() = state.verboseOutput
        set(value) { state.verboseOutput = value }

    var showNotifications: Boolean
        get() = state.showNotifications
        set(value) { state.showNotifications = value }

    var enableKeyboardShortcut: Boolean
        get() = state.enableKeyboardShortcut
        set(value) { state.enableKeyboardShortcut = value }

    var lastUsedOptions: String
        get() = state.lastUsedOptions
        set(value) { state.lastUsedOptions = value }

    override fun getState(): State {
        return state
    }

    override fun loadState(state: State) {
        this.state = state
    }
}