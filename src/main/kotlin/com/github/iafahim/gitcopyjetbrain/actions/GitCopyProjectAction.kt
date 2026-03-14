package com.github.iafahim.gitcopyjetbrain.actions

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.vfs.VirtualFile
import com.github.iafahim.gitcopyjetbrain.services.GitCopyService
import com.github.iafahim.gitcopyjetbrain.settings.GitCopySettings
import com.intellij.openapi.components.service

/**
 * Action to execute git-copy on the entire project.
 * This action appears in the project context menu and Tools menu.
 */
class GitCopyProjectAction : AnAction("Copy Project with git-copy", "Copy entire project using git-copy", null) {

    companion object {
        private val LOG = Logger.getInstance(GitCopyProjectAction::class.java)
        const val ID = "GitCopyProjectAction"

        private fun showNotificationStatic(project: com.intellij.openapi.project.Project, message: String, type: NotificationType) {
            val notification = Notification("GitCopy", "git-copy Project", message, type)
            Notifications.Bus.notify(notification, project)
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        e.presentation.isEnabledAndVisible = project != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT) ?: return

        // Get the project base directory
        val projectBasePath = project.basePath
        if (projectBasePath == null) {
            showNotificationStatic(project, "Project path not found", NotificationType.ERROR)
            return
        }

        val projectFile = com.intellij.openapi.vfs.VirtualFileManager.getInstance()
            .findFileByUrl("file://$projectBasePath")

        if (projectFile == null || !projectFile.isValid) {
            showNotificationStatic(project, "Project directory not found or invalid", NotificationType.ERROR)
            return
        }

        val settings = GitCopySettings.getInstance(project)

        // Execute git-copy in background with progress indicator
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Copying Project with git-copy", true) {
            private var success = false
            private var errorMessage: String? = null

            override fun run(indicator: ProgressIndicator) {
                try {
                    indicator.text = "Copying project ${project.name} with git-copy..."
                    indicator.isIndeterminate = false

                    val service = project.service<GitCopyService>()
                    success = service.executeGitCopy(projectFile, indicator, settings)

                    if (!success) {
                        errorMessage = "Project git-copy operation failed. Check settings and logs."
                    }
                } catch (e: Exception) {
                    LOG.error("Project git-copy operation failed", e)
                    errorMessage = "Project git-copy operation failed: ${e.message}"
                    success = false
                }
            }

            override fun onSuccess() {
                super.onSuccess()
                if (success) {
                    showNotificationStatic(project, "Project copied with git-copy successfully", NotificationType.INFORMATION)
                } else {
                    showNotificationStatic(project, errorMessage ?: "Project git-copy operation failed", NotificationType.ERROR)
                }
            }
        })
    }
}