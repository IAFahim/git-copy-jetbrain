package com.github.iafahim.gitcopyjetbrain.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import java.util.Date
import java.util.UUID

/**
 * Service to track and manage git-copy operation history.
 */
@Service(Service.Level.PROJECT)
class GitCopyHistoryService(
    private val project: Project
) : PersistentStateComponent<GitCopyHistoryService.State> {

    companion object {
        fun getInstance(project: Project): GitCopyHistoryService {
            return project.service()
        }

        private const val MAX_HISTORY_SIZE = 1000
    }

    data class State(
        var operations: List<GitCopyOperation> = emptyList()
    )

    private var state = State()

    override fun getState(): State {
        return state
    }

    override fun loadState(state: State) {
        this.state = state
    }

    /**
     * Add a new operation to history.
     */
    fun addOperation(operation: GitCopyOperation) {
        val updatedList = state.operations.toMutableList()
        updatedList.add(0, operation) // Add to beginning

        // Trim to max size
        if (updatedList.size > MAX_HISTORY_SIZE) {
            updatedList.removeAt(updatedList.size - 1)
        }

        state = State(operations = updatedList)
    }

    /**
     * Get recent operations, limited to specified count.
     */
    fun getRecentOperations(limit: Int = 100): List<GitCopyOperation> {
        return state.operations.take(limit)
    }

    /**
     * Get all operations.
     */
    fun getAllOperations(): List<GitCopyOperation> {
        return state.operations
    }

    /**
     * Clear all history.
     */
    fun clearHistory() {
        state = State(operations = emptyList())
    }

    /**
     * Get statistics about operations.
     */
    fun getStatistics(): OperationStatistics {
        val total = state.operations.size
        val successful = state.operations.count { it.successful }
        val failed = total - successful
        val avgDuration = if (state.operations.isNotEmpty()) {
            state.operations.map { it.duration }.average().toLong()
        } else {
            0L
        }

        return OperationStatistics(
            total = total,
            successful = successful,
            failed = failed,
            averageDuration = avgDuration
        )
    }

    /**
     * Get operations by status.
     */
    fun getOperationsByStatus(successful: Boolean): List<GitCopyOperation> {
        return state.operations.filter { it.successful == successful }
    }

    /**
     * Search operations by path.
     */
    fun searchOperations(query: String): List<GitCopyOperation> {
        return state.operations.filter {
            it.sourcePath.contains(query, ignoreCase = true) ||
            it.destinationPath.contains(query, ignoreCase = true)
        }
    }
}

/**
 * Data class representing a single git-copy operation.
 */
data class GitCopyOperation(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Date = Date(),
    val sourcePath: String,
    val destinationPath: String,
    val successful: Boolean,
    val duration: Long,
    val errorMessage: String? = null,
    val options: CopyOptions = CopyOptions()
) {
    data class CopyOptions(
        val filters: String = "",
        val excludes: String = "",
        val verbose: Boolean = false
    )
}

/**
 * Statistics about git-copy operations.
 */
data class OperationStatistics(
    val total: Int,
    val successful: Int,
    val failed: Int,
    val averageDuration: Long
)