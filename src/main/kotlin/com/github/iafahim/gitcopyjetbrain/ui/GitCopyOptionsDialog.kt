package com.github.iafahim.gitcopyjetbrain.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import javax.swing.*

/**
 * Dialog for git-copy options.
 * git-copy copies code to clipboard with filters and excludes.
 */
class GitCopyOptionsDialog(
    private val project: Project,
    private val settings: com.github.iafahim.gitcopyjetbrain.settings.GitCopySettings
) : DialogWrapper(project, true) {

    private val filtersField = JBTextField(settings.defaultFilters)
    private val excludeField = JBTextField(settings.defaultExcludes)
    private val verboseCheckBox = JBCheckBox("Verbose Output", settings.verboseOutput)

    val gitCopyOptions: GitCopyOptions
        get() = GitCopyOptions(
            filters = filtersField.text.trim(),
            excludes = excludeField.text.trim(),
            verbose = verboseCheckBox.isSelected
        )

    init {
        title = "Copy to Clipboard with git-copy"
        setOKButtonText("Copy to Clipboard")
        setCancelButtonText("Cancel")
        init()
    }

    override fun createCenterPanel(): JComponent {
        val mainPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }

        // Description panel
        val descPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JLabel("<html><b>git-copy</b> - Copy project code to clipboard for LLMs</html>"))
            add(JLabel("<html>Perfect for pasting into ChatGPT, Claude, or DeepSeek</html>"))
            add(Box.createVerticalStrut(10))
        }

        // Filters panel
        val filtersPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createTitledBorder("Filters (Optional)")
            add(JLabel("File types/presets to include:"))
            add(JTextField().apply {
                text = "Examples: web, backend, js, py, java"
                isEditable = false
                background = java.awt.Color(0, 0, 0, 0)
            })
            add(filtersField)
            add(JLabel("<html><i>Leave empty to copy all tracked files</i></html>"))
        }

        // Excludes panel
        val excludesPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createTitledBorder("Excludes (Optional)")
            add(JLabel("Folders/files to exclude:"))
            add(JTextField().apply {
                text = "Examples: -node_modules, -tests, -build"
                isEditable = false
                background = java.awt.Color(0, 0, 0, 0)
            })
            add(excludeField)
            add(JLabel("<html><i>Use - prefix or separate with spaces</i></html>"))
        }

        // Options panel
        val optionsPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createTitledBorder("Options")
            add(verboseCheckBox)
        }

        // Help panel
        val helpPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createTitledBorder("Quick Filters")
            add(JLabel("🌐 web: html, css, js, ts, jsx"))
            add(JLabel("⚙️  backend: python, ruby, php, go, rust"))
            add(JLabel("☕ java: java, kotlin, scala"))
            add(JLabel("🔧 cpp: c, cpp, h, hpp"))
            add(JLabel("📄 docs: md, txt, rst"))
        }

        // Add all panels to main panel
        mainPanel.add(descPanel)
        mainPanel.add(Box.createVerticalStrut(10))
        mainPanel.add(filtersPanel)
        mainPanel.add(Box.createVerticalStrut(10))
        mainPanel.add(excludesPanel)
        mainPanel.add(Box.createVerticalStrut(10))
        mainPanel.add(optionsPanel)
        mainPanel.add(Box.createVerticalStrut(10))
        mainPanel.add(helpPanel)

        return mainPanel
    }

    override fun doOKAction() {
        // Save settings as defaults for next time
        settings.defaultFilters = filtersField.text
        settings.defaultExcludes = excludeField.text
        settings.verboseOutput = verboseCheckBox.isSelected

        super.doOKAction()
    }
}

/**
 * Git-copy options for clipboard copying.
 */
data class GitCopyOptions(
    val filters: String = "",
    val excludes: String = "",
    val verbose: Boolean = false
)