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
                        .align(AlignX.FILL)
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
                            .comment("Enable default keyboard shortcuts (Ctrl+Shift+G)")
                    }
                }

                separator()

                group("Advanced Options") {
                    row("Default Filters (optional):") {
                        textField()
                            .bindText(settings::defaultFilters)
                            .align(AlignX.FILL)
                            .comment("File types to include: web, backend, js, py, etc.")
                    }

                    row("Default Excludes (optional):") {
                        textField()
                            .bindText(settings::defaultExcludes)
                            .align(AlignX.FILL)
                            .comment("Folders to exclude: -node_modules, -tests, etc.")
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
                            val detectedVariant = service.detectGitCopyVariant(settings)
                            val status = if (detectedVariant != null) {
                                val variantName = when (detectedVariant) {
                                    "standalone" -> "git-copy (standalone)"
                                    "subcommand" -> "git copy (subcommand)"
                                    "custom" -> "custom path"
                                    else -> "unknown"
                                }
                                "<html><font color='green'>✓ Found: $variantName</font></html>"
                            } else {
                                "<html><font color='red'>✗ Not found - Install git-copy</font></html>"
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
        val detectedVariant = service.detectGitCopyVariant(settings)

        if (detectedVariant != null) {
            val variantName = when (detectedVariant) {
                "standalone" -> "git-copy (standalone command)"
                "subcommand" -> "git copy (git subcommand)"
                "custom" -> "custom path: ${settings.customGitCopyPath}"
                else -> "unknown variant"
            }
            showInfoMessage("Configuration test passed!\n\nDetected: $variantName\n\ngit-copy is ready to use.")
        } else {
            showErrorMessage("Configuration test failed!\n\ngit-copy not found.\n\nPlease install git-copy:\ncurl -fsSL https://raw.githubusercontent.com/IAFahim/git-copy/main/install.sh | bash")
        }
    }

    private fun showInstallationHelp() {
        val helpText = """
            git-copy not found - Installation Instructions

            git-copy copies code from your Git repository to clipboard,
            perfect for pasting into ChatGPT, Claude, or other LLMs.

            Installation (Linux/macOS):
            curl -fsSL https://raw.githubusercontent.com/IAFahim/git-copy/main/install.sh | bash

            Installation (Windows PowerShell):
            Set-ExecutionPolicy Bypass -Scope Process -Force
            iwr -useb https://raw.githubusercontent.com/IAFahim/git-copy/main/install.ps1 | iex

            After installation, restart your IDE and the plugin will detect it automatically.

            For more info: https://github.com/IAFahim/git-copy
        """.trimIndent()

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