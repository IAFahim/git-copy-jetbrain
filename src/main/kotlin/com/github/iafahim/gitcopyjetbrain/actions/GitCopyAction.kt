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
import com.intellij.openapi.vfs.VirtualFile
import com.github.iafahim.gitcopyjetbrain.services.GitCopyService
import com.github.iafahim.gitcopyjetbrain.services.GitCopyHistoryService
import com.github.iafahim.gitcopyjetbrain.services.GitCopyOperation
import com.github.iafahim.gitcopyjetbrain.settings.GitCopySettings
import com.github.iafahim.gitcopyjetbrain.ui.GitCopyDestinationDialog
import com.github.iafahim.gitcopyjetbrain.ui.CopyOptions
import java.util.Date

/**
 * Enhanced Action to execute git-copy on selected files or folders in the IDE.
 * Features: Destination dialog, history tracking, better error handling.
 */
class GitCopyAction : AnAction("Copy with git-copy", "Copy selected file/folder using git-copy", null) {

    companion object {
        private val LOG = Logger.getInstance(GitCopyAction::class.java)
        const val ID = "GitCopyAction"
    }

    override fun update(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

        // Only enable action when a file/folder is selected in a project
        e.presentation.isEnabledAndVisible = project != null && virtualFile != null && virtualFile.isValid

        // Update text based on selection
        if (virtualFile != null && virtualFile.isDirectory) {
            e.presentation.text = "Copy Folder with git-copy"
        } else {
            e.presentation.text = "Copy File with git-copy"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        val settings = GitCopySettings.getInstance(project)
        val sourcePath = virtualFile.canonicalPath ?: return

        // Show destination dialog
        val dialog = GitCopyDestinationDialog(project, sourcePath, settings)
        if (!dialog.showAndGet()) {
            return // User cancelled
        }

        val destinationPath = dialog.destinationPath
        val copyOptions = dialog.copyOptions

        // Execute git-copy in background with progress indicator
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Copying with git-copy", true) {
            private var success = false
            private var errorMessage: String? = null
            private var duration: Long = 0L
            private var startTime: Long = 0L

            override fun run(indicator: ProgressIndicator) {
                try {
                    startTime = System.currentTimeMillis()
                    indicator.text = "Copying ${virtualFile.name} with git-copy..."
                    indicator.text2 = "Source: $sourcePath"
                    indicator.isIndeterminate = false
                    indicator.fraction = 0.0

                    val service = GitCopyService.getInstance()
                    success = service.executeGitCopyWithOptions(
                        project, virtualFile, destinationPath, copyOptions, indicator, settings
                    )

                    duration = System.currentTimeMillis() - startTime

                    if (!success) {
                        errorMessage = "git-copy operation failed. Check settings and logs."
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
                    sourcePath = sourcePath,
                    destinationPath = destinationPath,
                    successful = success,
                    duration = duration,
                    errorMessage = errorMessage,
                    options = GitCopyOperation.CopyOptions(
                        preserveGitHistory = copyOptions.preserveGitHistory,
                        recursive = copyOptions.recursive,
                        verbose = copyOptions.verbose
                    )
                )
                historyService.addOperation(operation)

                // Show notification
                if (success) {
                    val message = buildSuccessMessage(virtualFile.name, destinationPath, duration)
                    showNotification(project, message, NotificationType.INFORMATION)
                } else {
                    val message = buildErrorMessage(errorMessage ?: "Unknown error", sourcePath)
                    showNotification(project, message, NotificationType.ERROR)
                }
            }

            private fun buildSuccessMessage(fileName: String, destPath: String, durationMs: Long): String {
                val durationSec = durationMs / 1000.0
                return """
                    <html><b>✓ git-copy completed successfully!</b></html>
                    <br><br>
                    <b>File:</b> $fileName<br>
                    <b>Destination:</b> $destPath<br>
                    <b>Duration:</b> ${String.format("%.2f", durationSec)}s
                """.trimIndent()
            }

            private fun buildErrorMessage(error: String, sourcePath: String): String {
                return """
                    <html><b>✗ git-copy operation failed</b></html>
                    <br><br>
                    <b>Source:</b> $sourcePath<br>
                    <b>Error:</b> $error<br>
                    <br>
                    Please check your git-copy installation and settings.
                """.trimIndent()
            }

            private fun showNotification(project: com.intellij.openapi.project.Project, message: String, type: NotificationType) {
                val notification = Notification("GitCopy", "git-copy", message, type)
                Notifications.Bus.notify(notification, project)
            }
        })
    }
}