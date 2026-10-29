package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["status"]),
        Index(value = ["dueDate"]),
        Index(value = ["projectId"]),
        Index(value = ["businessId"]),
        Index(value = ["isArchived", "isDeleted"])
    ]
)
data class TaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val priority: String = "MEDIUM", // Priority enum name
    val status: String = "PLANNED", // TaskStatus enum name
    val category: String = "General",
    val projectId: String? = null,
    val businessId: String? = null,
    val goalId: String? = null,
    val startDate: Long? = null,
    val dueDate: Long? = null,
    val dueTimeStr: String? = null, // e.g., "14:30"
    val reminderTimestamp: Long? = null,
    val repeatSchedule: String = "NONE", // NONE, DAILY, WEEKDAYS, WEEKLY, MONTHLY
    val estimatedMinutes: Int = 0,
    val actualMinutes: Int = 0,
    val progress: Int = 0, // 0 - 100
    val tags: String = "", // Comma-separated tags
    val colorLabel: String? = null,
    val imageUri: String = "",
    val avatarUri: String = "",
    val isQuickTodo: Boolean = false,
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false, // Trash
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "subtasks",
    indices = [Index(value = ["taskId"])]
)
data class SubtaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val taskId: String,
    val title: String,
    val isCompleted: Boolean = false,
    val sortOrder: Int = 0
)

@Entity(
    tableName = "projects",
    indices = [
        Index(value = ["status"]),
        Index(value = ["businessId"]),
        Index(value = ["isArchived", "isDeleted"])
    ]
)
data class ProjectEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val objective: String = "",
    val status: String = "ACTIVE", // ProjectStatus enum name
    val priority: String = "MEDIUM",
    val category: String = "General",
    val businessId: String? = null,
    val startDate: Long? = null,
    val targetDate: Long? = null,
    val progressPercent: Int = 0,
    val manualProgressPercent: Int = 0,
    val progressMode: String = "MANUAL", // "MANUAL" or "AUTOMATIC"
    val milestonesJson: String = "[]", // List of milestones
    val decisionRecords: String = "",
    val projectLog: String = "",
    val colorLabel: String = "#283593",
    val coverUri: String = "",
    val avatarUri: String = "",
    val tags: String = "",
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val timeSpentMinutes: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "plans",
    indices = [Index(value = ["planType"]), Index(value = ["isArchived", "isDeleted"])]
)
data class PlanEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val planType: String = "WEEKLY", // PlanType enum name
    val description: String = "",
    val goals: String = "", // Bullet points or multiline goals
    val notes: String = "",
    val startDate: Long? = null,
    val endDate: Long? = null,
    val isLocked: Boolean = false,
    val lockedAt: Long? = null,
    val unlockedAt: Long? = null,
    val unlockReason: String? = null,
    val progressPercent: Int = 0,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "plan_items",
    indices = [Index(value = ["planId"])]
)
data class PlanItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val planId: String,
    val title: String,
    val isCompleted: Boolean = false,
    val priority: String = "MEDIUM",
    val sortOrder: Int = 0
)

@Entity(
    tableName = "notes",
    indices = [
        Index(value = ["category"]),
        Index(value = ["isPinned"]),
        Index(value = ["isArchived", "isDeleted"])
    ]
)
data class NoteEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String = "",
    val formattingJson: String = "", // Serialized styling / highlight tags / checklists
    val wordCount: Int = 0,
    val charCount: Int = 0,
    val category: String = "Personal",
    val tags: String = "",
    val projectId: String? = null,
    val businessId: String? = null,
    val learningId: String? = null,
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val colorLabel: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "information_vault",
    indices = [Index(value = ["category"]), Index(value = ["isArchived", "isDeleted"])]
)
data class InformationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val information: String = "",
    val category: String = "Reference",
    val summary: String = "",
    val tags: String = "",
    val sourceText: String = "",
    val projectId: String? = null,
    val relatedEntityId: String? = null,
    val relatedEntityType: String? = null,
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "knowledge_vault",
    indices = [Index(value = ["category"]), Index(value = ["isArchived", "isDeleted"])]
)
data class KnowledgeEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String = "Technical",
    val summary: String = "",
    val content: String = "",
    val keyPointsJson: String = "[]",
    val sourceReference: String = "",
    val tags: String = "",
    val relatedProjectId: String? = null,
    val relatedCourseId: String? = null,
    val relatedBusinessId: String? = null,
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "prompts",
    indices = [Index(value = ["category"]), Index(value = ["isFavorite"])]
)
data class PromptEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val fullPrompt: String,
    val category: String = "General",
    val tags: String = "",
    val purpose: String = "",
    val notes: String = "",
    val isFavorite: Boolean = false,
    val lastUsedAt: Long? = null,
    val lastCopiedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "businesses",
    indices = [Index(value = ["isArchived", "isDeleted"])]
)
data class BusinessEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val status: String = "ACTIVE",
    val goals: String = "",
    val revenueNotes: String = "",
    val expensesNotes: String = "",
    val marketingNotes: String = "",
    val customerNotes: String = "",
    val linksText: String = "",
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "products",
    indices = [Index(value = ["businessId"])]
)
data class ProductEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val businessId: String,
    val name: String,
    val type: String = "Product", // Product / Service
    val description: String = "",
    val price: Double = 0.0,
    val currency: String = "$",
    val status: String = "ACTIVE", // ProductStatus
    val salesChannel: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "learning_items",
    indices = [Index(value = ["status"]), Index(value = ["isArchived", "isDeleted"])]
)
data class LearningEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val type: String = "Course", // Course, Skill, Book, etc.
    val description: String = "",
    val goal: String = "",
    val status: String = "NOT_STARTED", // NOT_STARTED, LEARNING, PAUSED, COMPLETED
    val progressPercent: Int = 0,
    val startDate: Long? = null,
    val targetDate: Long? = null,
    val resourcesNotes: String = "",
    val totalStudyMinutes: Int = 0,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "learning_modules",
    indices = [Index(value = ["learningId"])]
)
data class LearningModuleEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val learningId: String, // Course / Learning ID
    val title: String,
    val description: String = "",
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "learning_lessons",
    indices = [
        Index(value = ["learningId"]),
        Index(value = ["moduleId"])
    ]
)
data class LearningLessonEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val learningId: String, // Course / Learning ID
    val moduleId: String,   // Module ID
    val title: String,
    val objective: String = "",
    val theory: String = "",
    val examples: String = "",
    val practice: String = "",
    val notes: String = "",
    val isCompleted: Boolean = false,
    val sortOrder: Int = 0,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "social_content",
    indices = [Index(value = ["platform"]), Index(value = ["status"])]
)
data class SocialContentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val platform: String = "YouTube", // SocialPlatform
    val contentType: String = "Post", // Video, Post, Reel, Article
    val idea: String = "",
    val scriptOrCaption: String = "",
    val status: String = "IDEA", // SocialStatus
    val plannedDate: Long? = null,
    val plannedTimeStr: String? = null,
    val publishedDate: Long? = null,
    val tags: String = "",
    val campaign: String = "",
    val callToAction: String = "",
    val publishedUrl: String = "",
    val notes: String = "",
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "file_attachments",
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["noteId"]),
        Index(value = ["taskId"])
    ]
)
data class FileAttachmentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val localFilePath: String, // internal app storage file path
    val mimeType: String = "",
    val sizeBytes: Long = 0L,
    val category: String = "Screenshot", // Screenshot, Document, Image, PDF, etc.
    val tags: String = "",
    val description: String = "",
    val projectId: String? = null,
    val noteId: String? = null,
    val taskId: String? = null,
    val businessId: String? = null,
    val learningId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "goals",
    indices = [
        Index(value = ["status"]),
        Index(value = ["businessId"]),
        Index(value = ["isArchived", "isDeleted"])
    ]
)
data class GoalEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val goalType: String = "Personal", // GoalType
    val targetDate: Long? = null,
    val progressPercent: Int = 0,
    val status: String = "ACTIVE", // GoalStatus
    val notes: String = "",
    val relatedProjectId: String? = null,
    val businessId: String? = null,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val frequency: String = "DAILY", // HabitFrequency
    val category: String = "Productivity",
    val targetDaysPerWeek: Int = 7,
    val reminderTimeStr: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "habit_logs",
    indices = [Index(value = ["habitId", "dateStr"], unique = true)]
)
data class HabitLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val habitId: String,
    val dateStr: String, // "YYYY-MM-DD"
    val isCompleted: Boolean = true,
    val notes: String = "",
    val loggedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "focus_sessions",
    indices = [Index(value = ["startedAt"])]
)
data class FocusSessionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val relatedType: String = "GENERAL", // FocusRelatedType
    val relatedId: String? = null,
    val targetMinutes: Int = 25,
    val actualMinutes: Int = 25,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long = System.currentTimeMillis(),
    val completed: Boolean = true
)

@Entity(tableName = "time_entries")
data class TimeEntryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val relatedType: String = "GENERAL",
    val relatedId: String? = null,
    val startTimestamp: Long = System.currentTimeMillis(),
    val endTimestamp: Long = System.currentTimeMillis(),
    val durationMinutes: Int = 0,
    val notes: String = ""
)

@Entity(
    tableName = "daily_reviews",
    indices = [Index(value = ["dateStr"], unique = true)]
)
data class DailyReviewEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val dateStr: String, // "YYYY-MM-DD"
    val completedTasksCount: Int = 0,
    val focusMinutes: Int = 0,
    val whatWentWell: String = "",
    val whatNeedsAttention: String = "",
    val generalNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "weekly_reviews",
    indices = [Index(value = ["weekStr"], unique = true)]
)
data class WeeklyReviewEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val weekStr: String, // "YYYY-Wxx"
    val tasksCompletedCount: Int = 0,
    val focusMinutes: Int = 0,
    val reflection: String = "",
    val nextWeekPriorities: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "activity_logs",
    indices = [Index(value = ["timestamp"])]
)
data class ActivityEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: String, // CREATED, COMPLETED, LOCKED, EDITED, STUDIED, FOCUS
    val itemType: String, // TASK, PROJECT, PLAN, NOTE, PROMPT, LEARNING, BUSINESS
    val itemId: String,
    val title: String,
    val summary: String = ""
)

@Entity(
    tableName = "project_decisions",
    indices = [Index(value = ["projectId"])]
)
data class ProjectDecisionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val title: String,
    val description: String = "",
    val reason: String = "",
    val outcome: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
