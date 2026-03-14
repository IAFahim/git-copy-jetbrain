package com.github.iafahim.gitcopyjetbrain.services

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.github.iafahim.gitcopyjetbrain.settings.GitCopySettings
import com.github.iafahim.gitcopyjetbrain.ui.CopyOptions
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

        private const val GIT_COPY_COMMAND = "git"
        private const val GIT_COPY_SUBCOMMAND = "copy"
        private const val DEFAULT_INSTALLATION_URL = "https://github.com/IAFahim/git-copy"
    }

    /**
     * Execute git-copy on the specified file or folder.
     * @param virtualFile The file or folder to copy
     * @param indicator Progress indicator for the operation
     * @param settings Plugin settings
     * @return true if the operation succeeded, false otherwise
     */
    fun executeGitCopy(
        virtualFile: com.intellij.openapi.vfs.VirtualFile,
        indicator: ProgressIndicator,
        settings: GitCopySettings
    ): Boolean {
        return executeGitCopyWithOptions(virtualFile, null, CopyOptions(), indicator, settings)
    }

    /**
     * Execute git-copy on the specified file or folder with custom options.
     * @param virtualFile The file or folder to copy
     * @param destinationPath Optional destination path (if null, uses git-copy default)
     * @param options Copy options
     * @param indicator Progress indicator for the operation
     * @param settings Plugin settings
     * @return true if the operation succeeded, false otherwise
     */
    fun executeGitCopyWithOptions(
        virtualFile: com.intellij.openapi.vfs.VirtualFile,
        destinationPath: String?,
        options: CopyOptions,
        indicator: ProgressIndicator,
        settings: GitCopySettings
    ): Boolean {
        val gitCopyPath = findGitCopyExecutable(settings)
        if (gitCopyPath == null) {
            LOG.error("git-copy executable not found")
            return false
        }

        val filePath = virtualFile.canonicalPath ?: return false

        try {
            // Build the command based on settings and options
            val command = buildGitCopyCommandWithOptions(gitCopyPath, filePath, destinationPath, options)
            LOG.info("Executing git-copy command: ${command.joinToString(" ")}")

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
     * Find the git-copy executable based on settings and system path.
     * git-copy is actually a git subcommand, so we check if git is available and git-copy is installed.
     */
    fun findGitCopyExecutable(settings: GitCopySettings): String? {
        // First, check if user specified a custom path
        if (settings.customGitCopyPath.isNotEmpty()) {
            val customPath = File(settings.customGitCopyPath)
            if (customPath.exists() && customPath.canExecute()) {
                return settings.customGitCopyPath
            } else {
                LOG.warn("Custom git path specified but not found or not executable: ${settings.customGitCopyPath}")
            }
        }

        // Try to find git in system PATH
        try {
            val process = ProcessBuilder(listOf("which", GIT_COPY_COMMAND)).start()
            val output = BufferedReader(InputStreamReader(process.inputStream, StandardCharsets.UTF_8)).use { reader ->
                reader.readLine()
            }

            if (process.waitFor() == 0 && output != null && output.isNotEmpty()) {
                LOG.info("Found git at: $output")

                // Check if git-copy subcommand is available
                val gitCopyProcess = ProcessBuilder(listOf(GIT_COPY_COMMAND, GIT_COPY_SUBCOMMAND, "--help"))
                    .redirectErrorStream(true)
                    .start()

                val gitCopyOutput = BufferedReader(InputStreamReader(gitCopyProcess.inputStream, StandardCharsets.UTF_8)).use { reader ->
                    reader.readLine()
                }

                if (gitCopyProcess.waitFor() == 0 || (gitCopyOutput != null && gitCopyOutput.contains("git-copy"))) {
                    LOG.info("git-copy subcommand is available")
                    return output
                } else {
                    LOG.warn("git is found but git-copy subcommand is not installed")
                    return null
                }
            }
        } catch (e: Exception) {
            LOG.debug("Could not find git in system PATH", e)
        }

        // Try common installation locations
        val commonPaths = listOf(
            "/usr/local/bin/git",
            "/usr/bin/git",
            "${System.getProperty("user.home")}/.local/bin/git",
            "/usr/local/git/bin/git"
        )

        for (path in commonPaths) {
            val file = File(path)
            if (file.exists() && file.canExecute()) {
                LOG.info("Found git at common path: $path")
                return path
            }
        }

        return null
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
            // Build the git copy command
            val command = buildGitCopyCommand(gitPath, options)
            LOG.info("Executing git-copy command: ${command.joinToString(" ")}")

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
     * git copy [filters] [excludes]
     */
    private fun buildGitCopyCommand(gitPath: String, options: com.github.iafahim.gitcopyjetbrain.ui.GitCopyOptions): List<String> {
        val command = mutableListOf(gitPath, GIT_COPY_SUBCOMMAND)

        // Add filters if provided
        if (options.filters.isNotEmpty()) {
            command.addAll(options.filters.split(" ").filter { it.isNotEmpty() })
        }

        // Add excludes if provided
        if (options.excludes.isNotEmpty()) {
            command.addAll(options.excludes.split(" ").filter { it.isNotEmpty() })
        }

        return command
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

    /**
     * Build the git-copy command with appropriate arguments.
     */
    private fun buildGitCopyCommand(executablePath: String, filePath: String, settings: GitCopySettings): List<String> {
        return buildGitCopyCommandWithOptions(executablePath, filePath, null, CopyOptions(
            preserveGitHistory = settings.preserveGitHistory,
            recursive = settings.recursiveCopy,
            verbose = settings.verboseOutput,
            customArguments = settings.customArguments
        ))
    }

    /**
     * Build the git-copy command with custom options.
     * git-copy is actually a git subcommand: git copy [filters] [excludes]
     */
    private fun buildGitCopyCommandWithOptions(
        executablePath: String,
        filePath: String,
        destinationPath: String?,
        options: CopyOptions
    ): List<String> {
        val command = mutableListOf(executablePath, GIT_COPY_SUBCOMMAND)

        // Add custom arguments first (these are the filters/excludes for git-copy)
        if (options.customArguments.isNotEmpty()) {
            command.addAll(options.customArguments.split(" ").filter { it.isNotEmpty() })
        }

        // Add verbose output if requested (git-copy doesn't have verbose, but we can try)
        if (options.verbose) {
            // git-copy doesn't have a verbose flag, but we'll add it in case it's added later
            // command.add("--verbose")
        }

        // Note: git-copy doesn't need source path - it works on current git repository
        // And there's no destination - it copies to clipboard

        return command
    }

    override fun dispose() {
        // Cleanup if needed
    }
}