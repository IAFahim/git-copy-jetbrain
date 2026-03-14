package com.github.iafahim.gitcopyjetbrain.settings

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.github.iafahim.gitcopyjetbrain.services.GitCopyService
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.JComponent

/**
 * Configurable panel for git-copy plugin settings.
 * Accessible via Settings → Tools → git-copy
 */
class GitCopyConfigurable(private val project: Project) : Configurable {

    private lateinit var panel: DialogPanel
    private val settings = GitCopySettings.getInstance(project)

    override fun getDisplayName(): String = "git-copy"

    override fun createComponent(): JComponent {
        panel = panel {
            group("git-copy Configuration") {
                row("git-copy Executable Path:") {
                    val pathField = textField()
                        .bindText(settings::customGitCopyPath)
                        .horizontalAlign(HorizontalAlign.FILL)
                        .component

                    button("Browse") {
                        val fileChooser = com.intellij.openapi.fileChooser.FileChooserFactory.getInstance()
                            .createFileChooser(
                                com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createSingleFileDescriptor()
                                    .withTitle("Select git-copy Executable")
                                    .withDescription("Choose the git-copy executable file"),
                                null,
                                null
                            )

                        val file = fileChooser.choose(null)
                        if (file.isNotEmpty()) {
                            pathField.text = file[0].canonicalPath
                        }
                    }

                    button("Auto-detect") {
                        val service = project.service<GitCopyService>()
                        val detectedPath = service.findGitCopyExecutable(settings)
                        if (detectedPath != null) {
                            pathField.text = detectedPath
                            showInfoMessage("git-copy detected at: $detectedPath")
                        } else {
                            showErrorMessage("git-copy not found. Please install or specify path manually.")
                        }
                    }

                    comment("Leave empty to use system PATH")
                }

                separator()

                group("Copy Options") {
                    row {
                        checkBox("Preserve Git History")
                            .bindSelected(settings::preserveGitHistory)
                            .comment("Preserve git history during copy operations")
                    }

                    row {
                        checkBox("Recursive Copy")
                            .bindSelected(settings::recursiveCopy)
                            .comment("Copy directories recursively")
                    }

                    row {
                        checkBox("Verbose Output")
                            .bindSelected(settings::verboseOutput)
                            .comment("Show detailed output during copy operations")
                    }

                    row {
                        checkBox("Show Notifications")
                            .bindSelected(settings::showNotifications)
                            .comment("Show notification popups for copy operations")
                    }

                    row {
                        checkBox("Enable Keyboard Shortcuts")
                            .bindSelected(settings::enableKeyboardShortcut)
                            .comment("Enable default keyboard shortcuts (Ctrl+Shift+C)")
                    }
                }

                separator()

                group("Advanced Options") {
                    row("Custom Arguments:") {
                        textField()
                            .bindText(settings::customArguments)
                            .horizontalAlign(HorizontalAlign.FILL)
                            .comment("Additional command-line arguments (space-separated)")
                    }

                    row {
                        button("Test Configuration") {
                            testConfiguration()
                        }

                        button("Installation Help") {
                            showInstallationHelp()
                        }
                    }
                }

                separator()

                group("Status Information") {
                    row("git-copy Status:") {
                        val statusLabel = label("Checking...").component

                        // Check status asynchronously
                        Thread {
                            val service = project.service<GitCopyService>()
                            val isAvailable = service.isGitCopyAvailable(settings)
                            val status = if (isAvailable) {
                                val path = service.findGitCopyExecutable(settings)
                                "<html><font color='green'>✓ Found at: $path</font></html>"
                            } else {
                                "<html><font color='red'>✗ Not found - Install or configure path</font></html>"
                            }

                            // Update UI on EDT
                            com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
                                statusLabel.text = status
                            }
                        }.start()
                    }
                }
            }
        }

        return panel
    }

    override fun isModified(): Boolean {
        return panel.isModified()
    }

    override fun apply() {
        panel.apply()
    }

    override fun reset() {
        panel.reset()
    }

    private fun testConfiguration() {
        val service = project.service<GitCopyService>()
        val isAvailable = service.isGitCopyAvailable(settings)

        if (isAvailable) {
            val path = service.findGitCopyExecutable(settings)
            showInfoMessage("Configuration test passed!\ngit-copy found at: $path")
        } else {
            showErrorMessage("Configuration test failed!\ngit-copy not found. Please install or specify the correct path.")
        }
    }

    private fun showInstallationHelp() {
        val service = project.service<GitCopyService>()
        val helpText = service.getInstallationInstructions()

        com.intellij.openapi.ui.Messages.showInfoMessage(
            project,
            helpText,
            "git-copy Installation Help"
        )
    }

    private fun showInfoMessage(message: String) {
        com.intellij.openapi.ui.Messages.showInfoMessage(
            project,
            message,
            "git-copy Configuration"
        )
    }

    private fun showErrorMessage(message: String) {
        com.intellij.openapi.ui.Messages.showErrorDialog(
            project,
            message,
            "git-copy Configuration Error"
        )
    }
}