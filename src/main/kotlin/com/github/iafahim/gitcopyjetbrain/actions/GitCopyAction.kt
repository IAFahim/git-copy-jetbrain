package com.github.iafahim.gitcopyjetbrain.actions

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.github.iafahim.gitcopyjetbrain.services.GitCopyService
import com.github.iafahim.gitcopyjetbrain.services.GitCopyHistoryService
import com.github.iafahim.gitcopyjetbrain.services.GitCopyOperation
import com.github.iafahim.gitcopyjetbrain.settings.GitCopySettings
import com.github.iafahim.gitcopyjetbrain.ui.GitCopyOptionsDialog

/**
 * Action to execute git-copy on the current project.
 * git-copy copies code to clipboard for LLMs (ChatGPT, Claude, etc.)
 */
class GitCopyAction : AnAction("Copy to Clipboard with git-copy", "Copy project code to clipboard using git-copy", null) {

    companion object {
        private val LOG = Logger.getInstance(GitCopyAction::class.java)
        const val ID = "GitCopyAction"
    }

    override fun update(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

        // Enable for both project-level and file/folder selection
        e.presentation.isEnabledAndVisible = project != null

        // Update text based on selection
        if (virtualFile != null && virtualFile.isDirectory) {
            e.presentation.text = "Copy Folder to Clipboard with git-copy"
        } else if (virtualFile != null) {
            e.presentation.text = "Copy File to Clipboard with git-copy"
        } else {
            e.presentation.text = "Copy Project to Clipboard with git-copy"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

        val settings = GitCopySettings.getInstance(project)

        // Show git-copy options dialog with defaults from settings
        val dialog = GitCopyOptionsDialog(project, settings)
        if (!dialog.showAndGet()) {
            return // User cancelled
        }

        val gitCopyOptions = dialog.gitCopyOptions

        // Save the last used options
        settings.lastUsedOptions = "${gitCopyOptions.filters} ${gitCopyOptions.excludes}".trim()

        // Track what we're copying
        val sourceDescription = if (virtualFile != null) {
            if (virtualFile.isDirectory) "Folder: ${virtualFile.name}" else "File: ${virtualFile.name}"
        } else {
            "Entire Project"
        }

        // Execute git-copy in background with progress indicator
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Copying to clipboard with git-copy", true) {
            private var success = false
            private var errorMessage: String? = null
            private var duration: Long = 0L
            private var startTime: Long = 0L

            override fun run(indicator: ProgressIndicator) {
                try {
                    startTime = System.currentTimeMillis()
                    indicator.text = "Copying project code to clipboard with git-copy..."
                    indicator.isIndeterminate = false
                    indicator.fraction = 0.0

                    val service = project.service<GitCopyService>()
                    success = service.executeGitCopyForClipboard(project, gitCopyOptions, indicator, settings)

                    duration = System.currentTimeMillis() - startTime

                    if (!success) {
                        errorMessage = "git-copy operation failed. Make sure git-copy is installed."
                    }
                } catch (e: Exception) {
                    LOG.error("git-copy operation failed", e)
                    errorMessage = "git-copy operation failed: ${e.message}"
                    success = false
                    duration = System.currentTimeMillis() - startTime
                }
            }

            override fun onSuccess() {
                super.onSuccess()

                // Track operation in history
                val historyService = project.service<GitCopyHistoryService>()
                val operation = GitCopyOperation(
                    sourcePath = sourceDescription,
                    destinationPath = "Clipboard",
                    successful = success,
                    duration = duration,
                    errorMessage = errorMessage,
                    options = GitCopyOperation.CopyOptions(
                        filters = gitCopyOptions.filters,
                        excludes = gitCopyOptions.excludes,
                        verbose = gitCopyOptions.verbose
                    )
                )
                historyService.addOperation(operation)

                // Show notification
                if (success) {
                    val message = buildSuccessMessage(duration)
                    showNotification(project, message, NotificationType.INFORMATION)
                } else {
                    val message = buildErrorMessage(errorMessage ?: "Unknown error")
                    showNotification(project, message, NotificationType.ERROR)
                }
            }

            private fun buildSuccessMessage(durationMs: Long): String {
                val durationSec = durationMs / 1000.0
                return """
                    <html><b>✓ Code copied to clipboard successfully!</b></html>
                    <br><br>
                    <b>Duration:</b> ${String.format("%.2f", durationSec)}s<br>
                    <b>Action:</b> Ready to paste into ChatGPT, Claude, or other LLMs
                """.trimIndent()
            }

            private fun buildErrorMessage(error: String): String {
                return """
                    <html><b>✗ git-copy operation failed</b></html>
                    <br><br>
                    <b>Error:</b> $error<br>
                    <br>
                    Please ensure:<br>
                    • git-copy is installed (<code>git copy --help</code>)<br>
                    • Current directory is a git repository<br>
                    • Clipboard is accessible
                """.trimIndent()
            }

            private fun showNotification(project: com.intellij.openapi.project.Project, message: String, type: NotificationType) {
                val notification = Notification("GitCopy", "git-copy", message, type)
                Notifications.Bus.notify(notification, project)
            }
        })
    }
}