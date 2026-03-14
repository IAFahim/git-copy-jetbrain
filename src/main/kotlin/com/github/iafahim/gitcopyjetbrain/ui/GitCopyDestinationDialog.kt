package com.github.iafahim.gitcopyjetbrain.ui

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.JBPanel
import java.awt.BorderLayout
import java.awt.FlowLayout
import com.github.iafahim.gitcopyjetbrain.settings.GitCopySettings
import java.io.File
import javax.swing.*

/**
 * Dialog for selecting destination path and copy options.
 */
class GitCopyDestinationDialog(
    private val project: Project,
    private val sourcePath: String,
    private val settings: GitCopySettings
) : DialogWrapper(project, true) {

    private val destinationField = JBTextField()
    private val nameField = JBTextField(File(sourcePath).name)
    private val preserveGitCheckBox = JBCheckBox("Preserve Git History", settings.preserveGitHistory)
    private val recursiveCheckBox = JBCheckBox("Recursive Copy", settings.recursiveCopy)
    private val verboseCheckBox = JBCheckBox("Verbose Output", settings.verboseOutput)
    private val customArgsField = JBTextField(settings.customArguments)

    val destinationPath: String
        get() = File(destinationField.text, nameField.text).absolutePath

    val copyOptions: CopyOptions
        get() = CopyOptions(
            preserveGitHistory = preserveGitCheckBox.isSelected,
            recursive = recursiveCheckBox.isSelected,
            verbose = verboseCheckBox.isSelected,
            customArguments = customArgsField.text
        )

    init {
        title = "Copy with git-copy"
        setOKButtonText("Copy")
        setCancelButtonText("Cancel")
        init()
    }

    override fun createCenterPanel(): JComponent {
        val mainPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }

        // Source panel
        val sourcePanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            add(JLabel("Source:"))
            add(JLabel(sourcePath).apply {
                foreground = UIManager.getColor("Label.disabledForeground")
            })
        }

        // Destination panel
        val destPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JLabel("Destination Directory:"))
            val destInputPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
                add(destinationField)
                val browseButton = JButton("Browse...")
                browseButton.addActionListener { browseDestination() }
                add(browseButton)
            }
            add(destInputPanel)
        }

        // Name panel
        val namePanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JLabel("Name (optional):"))
            add(nameField)
        }

        // Options panel
        val optionsPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createTitledBorder("Copy Options")
            add(preserveGitCheckBox)
            add(recursiveCheckBox)
            add(verboseCheckBox)
        }

        // Custom args panel
        val argsPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JLabel("Custom Arguments:"))
            add(customArgsField)
        }

        // Add all panels to main panel
        mainPanel.add(sourcePanel)
        mainPanel.add(Box.createVerticalStrut(10))
        mainPanel.add(destPanel)
        mainPanel.add(Box.createVerticalStrut(10))
        mainPanel.add(namePanel)
        mainPanel.add(Box.createVerticalStrut(10))
        mainPanel.add(JSeparator())
        mainPanel.add(Box.createVerticalStrut(10))
        mainPanel.add(optionsPanel)
        mainPanel.add(Box.createVerticalStrut(10))
        mainPanel.add(argsPanel)

        // Set default destination to last used or parent directory
        destinationField.text = if (settings.lastUsedDestination.isNotEmpty()) {
            settings.lastUsedDestination
        } else {
            File(sourcePath).parent ?: System.getProperty("user.dir")
        }

        return mainPanel
    }

    private fun browseDestination() {
        val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
            .withTitle("Select Destination Directory")
            .withDescription("Choose the directory where you want to copy the file/folder")

        val file = FileChooser.chooseFile(descriptor, project, null)
        if (file != null) {
            destinationField.text = file.absolutePath
        }
    }

    override fun doValidate(): ValidationInfo? {
        if (destinationField.text.isEmpty()) {
            return ValidationInfo("Please select a destination directory", destinationField)
        }

        val destDir = File(destinationField.text)
        if (!destDir.exists() || !destDir.isDirectory) {
            return ValidationInfo("Destination directory does not exist", destinationField)
        }

        if (!destDir.canWrite()) {
            return ValidationInfo("Cannot write to destination directory", destinationField)
        }

        val finalPath = File(destDir, nameField.text)
        if (finalPath.exists()) {
            return ValidationInfo("A file/folder with this name already exists in the destination", nameField)
        }

        return null
    }

    override fun doOKAction() {
        // Save last used destination
        settings.lastUsedDestination = destinationField.text

        // Update settings based on user preferences
        settings.preserveGitHistory = preserveGitCheckBox.isSelected
        settings.recursiveCopy = recursiveCheckBox.isSelected
        settings.verboseOutput = verboseCheckBox.isSelected
        settings.customArguments = customArgsField.text

        super.doOKAction()
    }
}

/**
 * Copy options data class.
 */
data class CopyOptions(
    val preserveGitHistory: Boolean = false,
    val recursive: Boolean = false,
    val verbose: Boolean = false,
    val customArguments: String = ""
)