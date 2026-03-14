package com.github.iafahim.gitcopyjetbrain.services

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.github.iafahim.gitcopyjetbrain.settings.GitCopySettings
import com.github.iafahim.gitcopyjetbrain.ui.GitCopyOptions
import java.util.UUID
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/**
 * Service responsible for executing git-copy operations.
 * Handles detection of git-copy installation and command execution.
 */
@Service(Service.Level.PROJECT)
class GitCopyService(private val project: Project) : Disposable {

    companion object {
        private val LOG = Logger.getInstance(GitCopyService::class.java)

        fun getInstance(project: Project): GitCopyService {
            return project.service()
        }

        private const val GIT_COPY_STANDALONE = "git-copy"
        private const val GIT_COPY_SUBCOMMAND_GIT = "git"
        private const val GIT_COPY_SUBCOMMAND_COPY = "copy"
        private const val DEFAULT_INSTALLATION_URL = "https://github.com/IAFahim/git-copy"
    }

    // Note: The old file copying methods have been removed since git-copy is a clipboard tool
    // If file copying functionality is needed, it should be implemented separately

            // Execute the command
            val processBuilder = ProcessBuilder(command)
                .redirectErrorStream(true)

            // Set working directory if we have a parent directory
            virtualFile.parent?.canonicalPath?.let {
                processBuilder.directory(File(it))
            }

            val process = processBuilder.start()

            // Monitor the process output
            val output = BufferedReader(InputStreamReader(process.inputStream, StandardCharsets.UTF_8)).use { reader ->
                val lines = mutableListOf<String>()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    lines.add(line!!)
                    indicator.text2 = line

                    // Check if user cancelled the operation
                    if (indicator.isCanceled) {
                        process.destroyForcibly()
                        return false
                    }
                }
                lines.joinToString("\n")
            }

            val exitCode = process.waitFor()

            if (exitCode == 0) {
                LOG.info("git-copy completed successfully")
                if (output.isNotEmpty()) {
                    LOG.info("git-copy output: $output")
                }
                return true
            } else {
                LOG.error("git-copy failed with exit code $exitCode: $output")
                return false
            }

        } catch (e: InterruptedException) {
            LOG.error("git-copy operation interrupted", e)
            Thread.currentThread().interrupt()
            return false
        } catch (e: IOException) {
            LOG.error("git-copy operation failed", e)
            return false
        }
    }

    /**
     * Detect which git-copy variant is available.
     * Returns "standalone" for `git-copy` command, "subcommand" for `git copy`, or null if not found.
     * Respects forceVariant setting if user has specified a preference.
     */
    fun detectGitCopyVariant(settings: GitCopySettings): String? {
        // If user has forced a specific variant, respect that choice
        if (settings.forceVariant.isNotEmpty()) {
            when (settings.forceVariant.lowercase()) {
                "standalone" -> {
                    if (verifyStandaloneCommand()) return "standalone"
                    LOG.warn("User forced standalone variant but it's not available")
                }
                "subcommand" -> {
                    if (verifySubCommand()) return "subcommand"
                    LOG.warn("User forced subcommand variant but it's not available")
                }
                else -> LOG.warn("Unknown forceVariant value: ${settings.forceVariant}")
            }
        }
        // First, check if user specified a custom path
        if (settings.customGitCopyPath.isNotEmpty()) {
            val customPath = File(settings.customGitCopyPath)
            if (customPath.exists() && customPath.canExecute()) {
                LOG.info("Using custom git-copy path: ${settings.customGitCopyPath}")
                return "custom"
            }
        }

        // Try standalone git-copy command first (Linux)
        if (verifyStandaloneCommand()) {
            return "standalone"
        }

        // Try git copy subcommand
        if (verifySubCommand()) {
            return "subcommand"
        }

        LOG.error("No git-copy variant found (neither standalone nor subcommand)")
        return null
    }

    /**
     * Find the git-copy executable based on settings and system path.
     * Supports both standalone `git-copy` and git subcommand `git copy`.
     */
    fun findGitCopyExecutable(settings: GitCopySettings): String? {
        val variant = detectGitCopyVariant(settings)
        return when (variant) {
            "standalone" -> GIT_COPY_STANDALONE
            "subcommand" -> GIT_COPY_SUBCOMMAND_GIT
            "custom" -> settings.customGitCopyPath
            else -> null
        }
    }

    /**
     * Execute git-copy to copy project code to clipboard.
     * @param options Git-copy options (filters, excludes, verbose)
     * @param indicator Progress indicator
     * @param settings Plugin settings
     * @return true if operation succeeded, false otherwise
     */
    fun executeGitCopyForClipboard(
        project: Project,
        options: com.github.iafahim.gitcopyjetbrain.ui.GitCopyOptions,
        indicator: ProgressIndicator,
        settings: GitCopySettings
    ): Boolean {
        val gitPath = findGitCopyExecutable(settings)
        if (gitPath == null) {
            LOG.error("git not found or git-copy subcommand not installed")
            return false
        }

        // Check which variant to use
        val variant = detectGitCopyVariant(settings)
        if (variant == null) {
            LOG.error("git-copy not found (neither standalone nor subcommand)")
            return false
        }

        // Check if current directory is a git repository
        val projectBasePath = project.basePath
        if (projectBasePath == null) {
            LOG.error("Project base path not found")
            return false
        }

        val gitDir = java.io.File(projectBasePath, ".git")
        if (!gitDir.exists()) {
            LOG.error("Current directory is not a git repository")
            return false
        }

        try {
            // Build the git copy command based on detected variant
            val command = buildGitCopyCommand(variant, options, settings)
            LOG.info("Executing git-copy command (variant: $variant): ${command.joinToString(" ")}")

            // Execute the command from project directory
            val process = ProcessBuilder(command)
                .directory(java.io.File(projectBasePath))
                .redirectErrorStream(true)
                .start()

            // Monitor the process output
            val output = BufferedReader(InputStreamReader(process.inputStream, StandardCharsets.UTF_8)).use { reader ->
                val lines = mutableListOf<String>()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    lines.add(line!!)
                    indicator.text2 = line

                    // Check if user cancelled the operation
                    if (indicator.isCanceled) {
                        process.destroyForcibly()
                        return false
                    }
                }
                lines.joinToString("\n")
            }

            val exitCode = process.waitFor()

            if (exitCode == 0) {
                LOG.info("git-copy completed successfully")
                if (output.isNotEmpty()) {
                    LOG.info("git-copy output: $output")
                }
                return true
            } else {
                LOG.error("git-copy failed with exit code $exitCode: $output")
                return false
            }

        } catch (e: InterruptedException) {
            LOG.error("git-copy operation interrupted", e)
            Thread.currentThread().interrupt()
            return false
        } catch (e: IOException) {
            LOG.error("git-copy operation failed", e)
            return false
        }
    }

    /**
     * Build the git copy command with options.
     * Supports both standalone `git-copy` and git subcommand `git copy`.
     */
    private fun buildGitCopyCommand(variant: String, options: com.github.iafahim.gitcopyjetbrain.ui.GitCopyOptions, settings: GitCopySettings): List<String> {
        return when (variant) {
            "standalone" -> {
                // git-copy web backend -node_modules
                val command = mutableListOf(GIT_COPY_STANDALONE)

                // Add filters if provided
                if (options.filters.isNotEmpty()) {
                    command.addAll(options.filters.split(" ").filter { it.isNotEmpty() })
                }

                // Add excludes if provided
                if (options.excludes.isNotEmpty()) {
                    command.addAll(options.excludes.split(" ").filter { it.isNotEmpty() })
                }

                command
            }

            "subcommand" -> {
                // git copy web backend -node_modules
                val command = mutableListOf(GIT_COPY_SUBCOMMAND_GIT, GIT_COPY_SUBCOMMAND_COPY)

                // Add filters if provided
                if (options.filters.isNotEmpty()) {
                    command.addAll(options.filters.split(" ").filter { it.isNotEmpty() })
                }

                // Add excludes if provided
                if (options.excludes.isNotEmpty()) {
                    command.addAll(options.excludes.split(" ").filter { it.isNotEmpty() })
                }

                command
            }

            "custom" -> {
                // Use custom path as standalone command
                val customPath = settings.customGitCopyPath
                val command = mutableListOf(customPath)

                // Add filters if provided
                if (options.filters.isNotEmpty()) {
                    command.addAll(options.filters.split(" ").filter { it.isNotEmpty() })
                }

                // Add excludes if provided
                if (options.excludes.isNotEmpty()) {
                    command.addAll(options.excludes.split(" ").filter { it.isNotEmpty() })
                }

                command
            }

            else -> {
                LOG.error("Unknown git-copy variant: $variant")
                mutableListOf()
            }
        }
    }

    /**
     * Get installation instructions for git-copy.
     */
    fun getInstallationInstructions(): String {
        return """
            git-copy is not installed or not found in your system PATH.

            Installation options:
            1. Using the repository: $DEFAULT_INSTALLATION_URL
            2. Configure custom path in: Settings → Tools → git-copy

            Common installation methods:
            - Clone the repository and follow installation instructions
            - Use package manager if available
            - Build from source
        """.trimIndent()
    }

    override fun dispose() {
        // Cleanup if needed
    }

    /**
     * Verify if standalone git-copy command is available.
     */
    private fun verifyStandaloneCommand(): Boolean {
        return try {
            val process = ProcessBuilder(listOf("which", GIT_COPY_STANDALONE)).start()
            val output = BufferedReader(InputStreamReader(process.inputStream, StandardCharsets.UTF_8)).use { reader ->
                reader.readLine()
            }

            if (process.waitFor() == 0 && output != null && output.isNotEmpty()) {
                // Test if git-copy actually works
                val testProcess = ProcessBuilder(listOf(GIT_COPY_STANDALONE, "--help"))
                    .redirectErrorStream(true)
                    .start()

                val testOutput = BufferedReader(InputStreamReader(testProcess.inputStream, StandardCharsets.UTF_8)).use { reader ->
                    reader.readLine()
                }

                testProcess.waitFor() == 0 || (testOutput != null && testOutput.contains("git-copy"))
            } else {
                false
            }
        } catch (e: Exception) {
            LOG.debug("git-copy standalone command not found", e)
            false
        }
    }

    /**
     * Verify if git copy subcommand is available.
     */
    private fun verifySubCommand(): Boolean {
        return try {
            val process = ProcessBuilder(listOf("which", GIT_COPY_SUBCOMMAND_GIT)).start()
            val output = BufferedReader(InputStreamReader(process.inputStream, StandardCharsets.UTF_8)).use { reader ->
                reader.readLine()
            }

            if (process.waitFor() == 0 && output != null && output.isNotEmpty()) {
                // Test if git copy subcommand works
                val testProcess = ProcessBuilder(listOf(GIT_COPY_SUBCOMMAND_GIT, GIT_COPY_SUBCOMMAND_COPY, "--help"))
                    .redirectErrorStream(true)
                    .start()

                val testOutput = BufferedReader(InputStreamReader(testProcess.inputStream, StandardCharsets.UTF_8)).use { reader ->
                    reader.readLine()
                }

                testProcess.waitFor() == 0 || (testOutput != null && (testOutput.contains("git-copy") || testOutput.contains("copy")))
            } else {
                false
            }
        } catch (e: Exception) {
            LOG.debug("git copy subcommand not found", e)
            false
        }
    }
}