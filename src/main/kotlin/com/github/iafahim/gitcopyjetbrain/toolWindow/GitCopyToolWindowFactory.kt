package com.github.iafahim.gitcopyjetbrain.toolWindow

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.github.iafahim.gitcopyjetbrain.services.GitCopyHistoryService
import com.github.iafahim.gitcopyjetbrain.services.GitCopyOperation
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Font
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*

/**
 * Tool Window Factory for git-copy operations.
 * Displays copy history, status, and provides quick access to common operations.
 */
class GitCopyToolWindowFactory : ToolWindowFactory, Disposable {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val gitCopyPanel = GitCopyPanel(project)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(gitCopyPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }

    override fun dispose() {
        // Cleanup if needed
    }
}

/**
 * Main panel for the git-copy tool window.
 */
class GitCopyPanel(project: Project) : JPanel(BorderLayout()) {

    private val historyService = project.service<GitCopyHistoryService>()
    private val operationsList = JBList<GitCopyOperation>()
    private val listModel = DefaultListModel<GitCopyOperation>()
    private val refreshButton = JButton("Refresh")
    private val clearButton = JButton("Clear History")
    private val copyAgainButton = JButton("Copy Again")
    private val statusLabel = JLabel("Ready")
    private val statsLabel = JLabel()

    init {
        setupUI()
        loadHistory()
        setupListeners()
    }

    private fun setupUI() {
        // Configure the operations list
        operationsList.model = listModel
        operationsList.cellRenderer = OperationListCellRenderer()
        operationsList.selectionMode = ListSelectionModel.SINGLE_SELECTION

        // Create buttons panel
        val buttonsPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(refreshButton)
            add(Box.createHorizontalStrut(5))
            add(clearButton)
            add(Box.createHorizontalStrut(5))
            add(copyAgainButton)
            add(Box.createHorizontalGlue())
        }

        // Create stats panel
        val statsPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            statusLabel.font = Font(statusLabel.font.name, Font.BOLD, 12)
            add(statusLabel)
            add(statsLabel)
        }

        // Main layout
        val topPanel = JPanel(BorderLayout()).apply {
            add(statsPanel, BorderLayout.NORTH)
            add(buttonsPanel, BorderLayout.SOUTH)
        }

        add(topPanel, BorderLayout.NORTH)
        add(JBScrollPane(operationsList), BorderLayout.CENTER)

        // Set preferred size
        preferredSize = java.awt.Dimension(400, 300)
    }

    private fun setupListeners() {
        refreshButton.addActionListener {
            loadHistory()
            updateStatus()
        }

        clearButton.addActionListener {
            val option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear the copy history?",
                "Clear History",
                JOptionPane.YES_NO_OPTION
            )
            if (option == JOptionPane.YES_OPTION) {
                historyService.clearHistory()
                loadHistory()
                updateStatus()
            }
        }

        copyAgainButton.addActionListener {
            val selected = operationsList.selectedValue
            if (selected != null) {
                // TODO: Implement copy again functionality
                JOptionPane.showMessageDialog(
                    this,
                    "Copy again functionality coming soon!\n\nSource: ${selected.sourcePath}",
                    "Copy Again",
                    JOptionPane.INFORMATION_MESSAGE
                )
            }
        }

        operationsList.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                copyAgainButton.isEnabled = operationsList.selectedValue != null
            }
        }
    }

    private fun loadHistory() {
        listModel.clear()
        historyService.getRecentOperations(100).reversed().forEach { operation ->
            listModel.addElement(operation)
        }
        updateStatus()
    }

    private fun updateStatus() {
        val stats = historyService.getStatistics()
        statusLabel.text = "git-copy Operations"
        statsLabel.text = "Total: ${stats.total} | Success: ${stats.successful} | Failed: ${stats.failed}"
    }
}

/**
 * Custom cell renderer for operations list.
 */
class OperationListCellRenderer : ListCellRenderer<GitCopyOperation> {
    private val panel = JPanel(BorderLayout(5, 2))
    private val titleLabel = JLabel()
    private val detailsLabel = JLabel()
    private val statusLabel = JLabel()

    init {
        titleLabel.font = Font(titleLabel.font.name, Font.BOLD, 12)
        detailsLabel.font = Font(detailsLabel.font.name, Font.PLAIN, 11)
        statusLabel.font = Font(statusLabel.font.name, Font.BOLD, 10)

        panel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        panel.add(titleLabel, BorderLayout.NORTH)
        panel.add(detailsLabel, BorderLayout.CENTER)
        panel.add(statusLabel, BorderLayout.SOUTH)
    }

    override fun getListCellRendererComponent(
        list: JList<out GitCopyOperation>,
        value: GitCopyOperation,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        val dateFormat = SimpleDateFormat("HH:mm:ss")

        titleLabel.text = value.sourcePath.substringAfterLast('/')
        detailsLabel.text = "${dateFormat.format(value.timestamp)} • ${value.duration}ms"
        statusLabel.text = if (value.successful) "✓ Success" else "✗ Failed"
        statusLabel.foreground = if (value.successful) {
            UIManager.getColor("Tree.foreground")
        } else {
            UIManager.getColor("Label.errorForeground")
        }

        panel.background = if (isSelected) {
            UIManager.getColor("Tree.selectionBackground")
        } else {
            UIManager.getColor("Tree.background")
        }

        titleLabel.foreground = if (isSelected) {
            UIManager.getColor("Tree.selectionForeground")
        } else {
            UIManager.getColor("Tree.foreground")
        }

        return panel
    }
}