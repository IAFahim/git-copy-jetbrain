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
 * This action appears in the Tools menu and provides quick access to git-copy.
 */
class GitCopyProjectAction : AnAction("Copy Project to Clipboard", "Copy entire project code to clipboard using git-copy", null) {

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
        // Just call the main GitCopyAction
        val action = GitCopyAction()
        action.actionPerformed(e)
    }
}