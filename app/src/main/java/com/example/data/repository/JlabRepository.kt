package com.example.data.repository

import com.example.data.local.dao.JlabDao
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Calendar

class JlabRepository(private val dao: JlabDao) {

    // Tasks
    val allActiveTasks: Flow<List<TaskEntity>> = dao.getAllActiveTasks()
    val pendingTasks: Flow<List<TaskEntity>> = dao.getPendingTasks()
    val pendingTaskCount: Flow<Int> = dao.observePendingTaskCount()
    val completedTasks: Flow<List<TaskEntity>> = dao.getCompletedTasks()

    fun getTodayTasks(): Flow<List<TaskEntity>> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return dao.getTodayTasks(cal.timeInMillis)
    }

    fun getOverdueTasks(): Flow<List<TaskEntity>> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return dao.getOverdueTasks(cal.timeInMillis)
    }

    fun getTasksByProject(projectId: String): Flow<List<TaskEntity>> = dao.getTasksByProject(projectId)
    suspend fun getTaskById(id: String): TaskEntity? = dao.getTaskById(id)

    suspend fun saveTask(task: TaskEntity) {
        dao.insertTask(task)
        dao.insertActivity(
            ActivityEntity(
                actionType = "CREATED",
                itemType = "TASK",
                itemId = task.id,
                title = "Created task \"${task.title}\"",
                summary = "Priority: ${task.priority}"
            )
        )
    }

    suspend fun updateTask(task: TaskEntity) = dao.updateTask(task)

    suspend fun completeTask(id: String, taskTitle: String = "") {
        dao.markTaskCompleted(id)
        dao.insertActivity(
            ActivityEntity(
                actionType = "COMPLETED",
                itemType = "TASK",
                itemId = id,
                title = "Completed task \"${taskTitle.ifEmpty { "Task" }}\"",
                summary = "Completed on time"
            )
        )
    }

    suspend fun reopenTask(id: String, taskTitle: String = "") {
        dao.reopenTask(id)
        dao.insertActivity(
            ActivityEntity(
                actionType = "REOPENED",
                itemType = "TASK",
                itemId = id,
                title = "Reopened task \"${taskTitle.ifEmpty { "Task" }}\"",
                summary = "Status set to planned"
            )
        )
    }

    suspend fun snoozeTask(id: String, minutes: Int = 10) {
        val snoozeTime = System.currentTimeMillis() + (minutes * 60 * 1000L)
        dao.snoozeTask(id, snoozeTime)
    }

    suspend fun moveTaskToTrash(id: String) = dao.moveTaskToTrash(id)
    suspend fun archiveTask(id: String) = dao.archiveTask(id)
    suspend fun restoreTask(id: String) = dao.restoreTask(id)
    suspend fun deleteTaskPermanently(id: String) = dao.deleteTaskPermanently(id)

    // Subtasks
    fun getSubtasks(taskId: String): Flow<List<SubtaskEntity>> = dao.getSubtasksForTask(taskId)
    suspend fun saveSubtask(subtask: SubtaskEntity) = dao.insertSubtask(subtask)
    suspend fun updateSubtask(subtask: SubtaskEntity) = dao.updateSubtask(subtask)
    suspend fun deleteSubtask(subtask: SubtaskEntity) = dao.deleteSubtask(subtask)

    // Projects
    val allActiveProjects: Flow<List<ProjectEntity>> = dao.getAllActiveProjects()
    suspend fun getProjectById(id: String): ProjectEntity? = dao.getProjectById(id)

    suspend fun saveProject(project: ProjectEntity) {
        dao.insertProject(project)
        dao.insertActivity(
            ActivityEntity(
                actionType = "CREATED",
                itemType = "PROJECT",
                itemId = project.id,
                title = "Created project \"${project.name}\"",
                summary = project.objective
            )
        )
    }

    suspend fun updateProject(project: ProjectEntity) {
        dao.updateProject(project)
        dao.insertActivity(
            ActivityEntity(
                actionType = "UPDATED",
                itemType = "PROJECT",
                itemId = project.id,
                title = "Updated project \"${project.name}\"",
                summary = "Progress: ${project.progressPercent}%"
            )
        )
    }

    suspend fun duplicateProject(projectId: String): ProjectEntity? {
        val original = dao.getProjectById(projectId) ?: return null
        val newProject = original.copy(
            id = java.util.UUID.randomUUID().toString(),
            name = "${original.name} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        dao.insertProject(newProject)
        dao.insertActivity(
            ActivityEntity(
                actionType = "DUPLICATED",
                itemType = "PROJECT",
                itemId = newProject.id,
                title = "Duplicated project \"${newProject.name}\"",
                summary = "Cloned from ${original.name}"
            )
        )
        return newProject
    }
    suspend fun moveProjectToTrash(id: String) = dao.moveProjectToTrash(id)
    suspend fun archiveProject(id: String) = dao.archiveProject(id)
    suspend fun restoreProject(id: String) = dao.restoreProject(id)
    suspend fun deleteProjectPermanently(id: String) = dao.deleteProjectPermanently(id)

    // Plans
    val allActivePlans: Flow<List<PlanEntity>> = dao.getAllActivePlans()
    suspend fun getPlanById(id: String): PlanEntity? = dao.getPlanById(id)
    suspend fun savePlan(plan: PlanEntity) {
        dao.insertPlan(plan)
        dao.insertActivity(
            ActivityEntity(
                actionType = "CREATED",
                itemType = "PLAN",
                itemId = plan.id,
                title = plan.title,
                summary = plan.planType
            )
        )
    }
    suspend fun updatePlan(plan: PlanEntity) = dao.updatePlan(plan)
    suspend fun lockPlan(id: String, planTitle: String) {
        dao.lockPlan(id)
        dao.insertActivity(
            ActivityEntity(
                actionType = "LOCKED",
                itemType = "PLAN",
                itemId = id,
                title = planTitle,
                summary = "Plan locked for disciplined commitment"
            )
        )
    }
    suspend fun unlockPlan(id: String, reason: String) {
        dao.unlockPlan(id, reason)
        dao.insertActivity(
            ActivityEntity(
                actionType = "UNLOCKED",
                itemType = "PLAN",
                itemId = id,
                title = "Unlocked Plan",
                summary = "Reason: $reason"
            )
        )
    }
    suspend fun movePlanToTrash(id: String) = dao.movePlanToTrash(id)
    suspend fun archivePlan(id: String) = dao.archivePlan(id)
    suspend fun restorePlan(id: String) = dao.restorePlan(id)
    suspend fun deletePlanPermanently(id: String) = dao.deletePlanPermanently(id)

    fun getPlanItems(planId: String): Flow<List<PlanItemEntity>> = dao.getPlanItems(planId)
    suspend fun savePlanItem(item: PlanItemEntity) = dao.insertPlanItem(item)
    suspend fun updatePlanItem(item: PlanItemEntity) = dao.updatePlanItem(item)
    suspend fun deletePlanItem(item: PlanItemEntity) = dao.deletePlanItem(item)

    // Notes
    val allActiveNotes: Flow<List<NoteEntity>> = dao.getAllActiveNotes()
    fun getNotesByProject(projectId: String): Flow<List<NoteEntity>> = dao.getNotesByProject(projectId)
    suspend fun getNoteById(id: String): NoteEntity? = dao.getNoteById(id)

    suspend fun createNote(note: NoteEntity) {
        dao.insertNote(note)
        val displayTitle = if (note.title.isNotBlank()) note.title else "Untitled Note"
        dao.insertActivity(
            ActivityEntity(
                actionType = "CREATED",
                itemType = "NOTE",
                itemId = note.id,
                title = "Created note \"$displayTitle\"",
                summary = "Category: ${note.category}"
            )
        )
    }

    suspend fun updateNote(note: NoteEntity) {
        dao.updateNote(note)
        val displayTitle = if (note.title.isNotBlank()) note.title else "Untitled Note"
        dao.insertActivity(
            ActivityEntity(
                actionType = "EDITED",
                itemType = "NOTE",
                itemId = note.id,
                title = "Edited note \"$displayTitle\"",
                summary = "Category: ${note.category}"
            )
        )
    }

    suspend fun saveNote(note: NoteEntity) {
        val existing = dao.getNoteById(note.id)
        if (existing == null) {
            createNote(note)
        } else {
            updateNote(note)
        }
    }

    suspend fun moveNoteToTrash(id: String) {
        dao.moveNoteToTrash(id)
        val note = dao.getNoteById(id)
        val displayTitle = note?.title?.ifBlank { "Untitled Note" } ?: "Note"
        dao.insertActivity(
            ActivityEntity(
                actionType = "DELETED",
                itemType = "NOTE",
                itemId = id,
                title = "Moved note \"$displayTitle\" to Trash",
                summary = "Moved to Trash"
            )
        )
    }

    suspend fun moveNotesToTrash(ids: List<String>) {
        if (ids.isEmpty()) return
        dao.moveNotesToTrash(ids)
        dao.insertActivity(
            ActivityEntity(
                actionType = "DELETED",
                itemType = "NOTE",
                itemId = ids.first(),
                title = if (ids.size == 1) "Moved 1 note to Trash" else "Deleted ${ids.size} notes",
                summary = "Moved to Trash"
            )
        )
    }

    suspend fun archiveNote(id: String) = dao.archiveNote(id)

    suspend fun archiveNotes(ids: List<String>) {
        if (ids.isEmpty()) return
        dao.archiveNotes(ids)
        dao.insertActivity(
            ActivityEntity(
                actionType = "ARCHIVED",
                itemType = "NOTE",
                itemId = ids.first(),
                title = "Archived ${ids.size} notes",
                summary = "Moved to Archive"
            )
        )
    }

    suspend fun updateNotesCategory(ids: List<String>, category: String) {
        if (ids.isEmpty()) return
        dao.updateNotesCategory(ids, category)
        dao.insertActivity(
            ActivityEntity(
                actionType = "UPDATED",
                itemType = "NOTE",
                itemId = ids.first(),
                title = "Updated category for ${ids.size} notes",
                summary = "New category: $category"
            )
        )
    }

    suspend fun setNotesPinned(ids: List<String>, isPinned: Boolean) {
        if (ids.isEmpty()) return
        dao.setNotesPinned(ids, isPinned)
    }

    suspend fun setNotesFavorite(ids: List<String>, isFavorite: Boolean) {
        if (ids.isEmpty()) return
        dao.setNotesFavorite(ids, isFavorite)
    }

    suspend fun restoreNote(id: String) = dao.restoreNote(id)

    suspend fun restoreNotes(ids: List<String>) {
        if (ids.isEmpty()) return
        dao.restoreNotes(ids)
    }

    suspend fun deleteNotePermanently(id: String) = dao.deleteNotePermanently(id)

    suspend fun deleteNotesPermanently(ids: List<String>) {
        if (ids.isEmpty()) return
        dao.deleteNotesPermanently(ids)
    }

    suspend fun emptyTrash() = dao.emptyTrash()

    // Information Vault
    val allInformation: Flow<List<InformationEntity>> = dao.getAllActiveInformation()
    suspend fun saveInformation(info: InformationEntity) {
        dao.insertInformation(info)
        dao.insertActivity(
            ActivityEntity(
                actionType = "SAVED",
                itemType = "INFORMATION",
                itemId = info.id,
                title = info.title,
                summary = "Category: ${info.category}"
            )
        )
    }
    suspend fun updateInformation(info: InformationEntity) = dao.updateInformation(info)
    suspend fun moveInformationToTrash(id: String) = dao.moveInformationToTrash(id)
    suspend fun deleteInformationPermanently(id: String) = dao.deleteInformationPermanently(id)

    // Knowledge Vault
    val allActiveKnowledge: Flow<List<KnowledgeEntity>> = dao.getAllActiveKnowledge()
    suspend fun getKnowledgeById(id: String): KnowledgeEntity? = dao.getKnowledgeById(id)
    suspend fun saveKnowledge(knowledge: KnowledgeEntity) {
        val existing = dao.getKnowledgeById(knowledge.id)
        if (existing == null) {
            dao.insertKnowledge(knowledge)
            dao.insertActivity(
                ActivityEntity(
                    actionType = "CREATED",
                    itemType = "KNOWLEDGE",
                    itemId = knowledge.id,
                    title = "Created knowledge \"${knowledge.title}\"",
                    summary = "Category: ${knowledge.category}"
                )
            )
        } else {
            dao.updateKnowledge(knowledge)
            dao.insertActivity(
                ActivityEntity(
                    actionType = "EDITED",
                    itemType = "KNOWLEDGE",
                    itemId = knowledge.id,
                    title = "Updated knowledge \"${knowledge.title}\"",
                    summary = "Category: ${knowledge.category}"
                )
            )
        }
    }
    suspend fun deleteKnowledgePermanently(id: String) = dao.deleteKnowledgePermanently(id)

    // Project Decisions
    fun getDecisionsByProject(projectId: String): Flow<List<ProjectDecisionEntity>> = dao.getDecisionsByProject(projectId)
    suspend fun saveProjectDecision(decision: ProjectDecisionEntity) {
        dao.insertProjectDecision(decision)
        dao.insertActivity(
            ActivityEntity(
                actionType = "LOGGED",
                itemType = "PROJECT",
                itemId = decision.projectId,
                title = "Decision: ${decision.title}",
                summary = decision.reason.take(60)
            )
        )
    }
    suspend fun updateProjectDecision(decision: ProjectDecisionEntity) = dao.updateProjectDecision(decision)
    suspend fun deleteProjectDecision(id: String) = dao.deleteProjectDecision(id)

    // Prompts
    val allPrompts: Flow<List<PromptEntity>> = dao.getAllPrompts()
    suspend fun getPromptById(id: String): PromptEntity? = dao.getPromptById(id)
    suspend fun savePrompt(prompt: PromptEntity) = dao.insertPrompt(prompt)
    suspend fun updatePrompt(prompt: PromptEntity) = dao.updatePrompt(prompt)
    suspend fun markPromptCopied(id: String) = dao.markPromptCopied(id)
    suspend fun deletePrompt(prompt: PromptEntity) = dao.deletePrompt(prompt)

    // Business & Products
    val allBusinesses: Flow<List<BusinessEntity>> = dao.getAllBusinesses()
    suspend fun getBusinessById(id: String): BusinessEntity? = dao.getBusinessById(id)
    suspend fun saveBusiness(business: BusinessEntity) {
        dao.insertBusiness(business)
        dao.insertActivity(
            ActivityEntity(
                actionType = "CREATED",
                itemType = "BUSINESS",
                itemId = business.id,
                title = "Created business \"${business.name}\"",
                summary = business.description.take(50)
            )
        )
    }
    suspend fun updateBusiness(business: BusinessEntity) = dao.updateBusiness(business)
    suspend fun moveBusinessToTrash(id: String) = dao.moveBusinessToTrash(id)
    suspend fun deleteBusinessPermanently(id: String) = dao.deleteBusinessPermanently(id)

    fun getTasksByBusiness(businessId: String): Flow<List<TaskEntity>> = dao.getTasksByBusiness(businessId)
    fun getProjectsByBusiness(businessId: String): Flow<List<ProjectEntity>> = dao.getProjectsByBusiness(businessId)
    fun getGoalsByBusiness(businessId: String): Flow<List<GoalEntity>> = dao.getGoalsByBusiness(businessId)
    fun getNotesByBusiness(businessId: String): Flow<List<NoteEntity>> = dao.getNotesByBusiness(businessId)
    fun getFilesByBusiness(businessId: String): Flow<List<FileAttachmentEntity>> = dao.getFilesByBusiness(businessId)

    fun getProductsForBusiness(businessId: String): Flow<List<ProductEntity>> = dao.getProductsForBusiness(businessId)
    suspend fun saveProduct(product: ProductEntity) = dao.insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = dao.updateProduct(product)
    suspend fun deleteProduct(product: ProductEntity) = dao.deleteProduct(product)

    // Learning
    val allLearningItems: Flow<List<LearningEntity>> = dao.getAllLearningItems()
    val allLessons: Flow<List<LearningLessonEntity>> = dao.getAllLessons()
    val completedLessonsCount: Flow<Int> = dao.observeCompletedLessonsCount()
    val totalLessonsCount: Flow<Int> = dao.observeTotalLessonsCount()

    suspend fun getLearningItemById(id: String): LearningEntity? = dao.getLearningItemById(id)
    suspend fun saveLearningItem(item: LearningEntity) = dao.insertLearningItem(item)
    suspend fun updateLearningItem(item: LearningEntity) = dao.updateLearningItem(item)
    suspend fun moveLearningToTrash(id: String) = dao.moveLearningToTrash(id)
    suspend fun deleteLearningPermanently(id: String) = dao.deleteLearningPermanently(id)

    // Curriculum: Modules
    fun getModulesForCourse(learningId: String): Flow<List<LearningModuleEntity>> = dao.getModulesForCourse(learningId)
    suspend fun saveLearningModule(module: LearningModuleEntity) = dao.insertLearningModule(module)
    suspend fun updateLearningModule(module: LearningModuleEntity) = dao.updateLearningModule(module)
    suspend fun deleteLearningModule(id: String) = dao.deleteLearningModule(id)

    // Curriculum: Lessons
    fun getLessonsForCourse(learningId: String): Flow<List<LearningLessonEntity>> = dao.getLessonsForCourse(learningId)
    fun getLessonsForModule(moduleId: String): Flow<List<LearningLessonEntity>> = dao.getLessonsForModule(moduleId)
    suspend fun saveLearningLesson(lesson: LearningLessonEntity) = dao.insertLearningLesson(lesson)
    suspend fun updateLearningLesson(lesson: LearningLessonEntity) = dao.updateLearningLesson(lesson)
    suspend fun deleteLearningLesson(id: String) = dao.deleteLearningLesson(id)

    suspend fun setLessonCompleted(lessonId: String, courseId: String, isCompleted: Boolean) {
        val now = if (isCompleted) System.currentTimeMillis() else null
        dao.setLessonCompleted(lessonId, isCompleted, now)
        // Automatically recalculate course progress from lessons
        recalculateCourseProgress(courseId)
    }

    suspend fun recalculateCourseProgress(courseId: String) {
        val course = dao.getLearningItemById(courseId) ?: return
        val lessons = dao.getLessonsForCourse(courseId).first()
        if (lessons.isEmpty()) return
        val completed = lessons.count { it.isCompleted }
        val progress = (completed * 100) / lessons.size
        val newStatus = when {
            completed == lessons.size && lessons.isNotEmpty() -> "COMPLETED"
            completed > 0 -> "LEARNING"
            else -> if (course.status == "COMPLETED") "LEARNING" else course.status
        }
        dao.updateLearningItem(
            course.copy(
                progressPercent = progress,
                status = newStatus,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    fun getNotesByLearning(learningId: String): Flow<List<NoteEntity>> = dao.getNotesByLearning(learningId)
    fun getFilesByLearning(learningId: String): Flow<List<FileAttachmentEntity>> = dao.getFilesByLearning(learningId)

    // Social Content
    val allSocialContent: Flow<List<SocialContentEntity>> = dao.getAllSocialContent()
    suspend fun saveSocialContent(content: SocialContentEntity) = dao.insertSocialContent(content)
    suspend fun updateSocialContent(content: SocialContentEntity) = dao.updateSocialContent(content)
    suspend fun moveSocialToTrash(id: String) = dao.moveSocialToTrash(id)
    suspend fun deleteSocialPermanently(id: String) = dao.deleteSocialPermanently(id)

    // Files Vault
    val allFiles: Flow<List<FileAttachmentEntity>> = dao.getAllFiles()
    fun getFilesByProject(projectId: String): Flow<List<FileAttachmentEntity>> = dao.getFilesByProject(projectId)
    suspend fun saveFileAttachment(file: FileAttachmentEntity) = dao.insertFileAttachment(file)
    suspend fun updateFileAttachment(file: FileAttachmentEntity) = dao.updateFileAttachment(file)
    suspend fun deleteFileAttachment(file: FileAttachmentEntity) = dao.deleteFileAttachment(file)
    suspend fun updateFileName(id: String, name: String) = dao.updateFileName(id, name)
    suspend fun updateFileCategory(id: String, category: String) = dao.updateFileCategory(id, category)
    suspend fun updateFilesCategory(ids: List<String>, category: String) = dao.updateFilesCategory(ids, category)
    suspend fun linkFileTo(id: String, projectId: String?, noteId: String?, taskId: String?, businessId: String?, learningId: String?) =
        dao.linkFileTo(id, projectId, noteId, taskId, businessId, learningId)
    suspend fun linkFilesTo(ids: List<String>, projectId: String?, noteId: String?, taskId: String?, businessId: String?, learningId: String?) =
        dao.linkFilesTo(ids, projectId, noteId, taskId, businessId, learningId)
    suspend fun deleteFilesByIds(ids: List<String>) = dao.deleteFilesByIds(ids)

    // Goals
    val allGoals: Flow<List<GoalEntity>> = dao.getAllGoals()
    suspend fun saveGoal(goal: GoalEntity) = dao.insertGoal(goal)
    suspend fun updateGoal(goal: GoalEntity) = dao.updateGoal(goal)
    suspend fun deleteGoal(goal: GoalEntity) = dao.deleteGoal(goal)

    // Habits & Habit Logs
    val activeHabits: Flow<List<HabitEntity>> = dao.getActiveHabits()
    fun getHabitLogsForDate(dateStr: String): Flow<List<HabitLogEntity>> = dao.getHabitLogsForDate(dateStr)
    fun getLogsForHabit(habitId: String): Flow<List<HabitLogEntity>> = dao.getLogsForHabit(habitId)
    suspend fun saveHabit(habit: HabitEntity) = dao.insertHabit(habit)
    suspend fun updateHabit(habit: HabitEntity) = dao.updateHabit(habit)
    suspend fun deleteHabit(habit: HabitEntity) = dao.deleteHabit(habit)
    suspend fun toggleHabitLog(habitId: String, dateStr: String, completed: Boolean) {
        if (completed) {
            dao.logHabit(HabitLogEntity(habitId = habitId, dateStr = dateStr, isCompleted = true))
        } else {
            dao.removeHabitLog(habitId, dateStr)
        }
    }

    // Focus & Time Tracking
    val allFocusSessions: Flow<List<FocusSessionEntity>> = dao.getAllFocusSessions()
    fun getFocusSessionsSince(timestamp: Long): Flow<List<FocusSessionEntity>> = dao.getFocusSessionsSince(timestamp)
    suspend fun saveFocusSession(session: FocusSessionEntity) {
        dao.insertFocusSession(session)
        dao.insertActivity(
            ActivityEntity(
                actionType = "FOCUS",
                itemType = "FOCUS_SESSION",
                itemId = session.id,
                title = session.title,
                summary = "Duration: ${session.actualMinutes}m (${session.relatedType})"
            )
        )
    }

    val allTimeEntries: Flow<List<TimeEntryEntity>> = dao.getAllTimeEntries()
    suspend fun saveTimeEntry(entry: TimeEntryEntity) = dao.insertTimeEntry(entry)

    // Reviews
    suspend fun getDailyReview(dateStr: String): DailyReviewEntity? = dao.getDailyReview(dateStr)
    suspend fun saveDailyReview(review: DailyReviewEntity) = dao.insertDailyReview(review)
    suspend fun getWeeklyReview(weekStr: String): WeeklyReviewEntity? = dao.getWeeklyReview(weekStr)
    suspend fun saveWeeklyReview(review: WeeklyReviewEntity) = dao.insertWeeklyReview(review)

    // Activities
    val recentActivities: Flow<List<ActivityEntity>> = dao.getRecentActivities()

    // Trash & Archive
    val archivedTasks: Flow<List<TaskEntity>> = dao.getArchivedTasks()
    val archivedProjects: Flow<List<ProjectEntity>> = dao.getArchivedProjects()
    val archivedNotes: Flow<List<NoteEntity>> = dao.getArchivedNotes()

    val deletedTasks: Flow<List<TaskEntity>> = dao.getDeletedTasks()
    val deletedProjects: Flow<List<ProjectEntity>> = dao.getDeletedProjects()
    val deletedNotes: Flow<List<NoteEntity>> = dao.getDeletedNotes()

    // Storage stats
    suspend fun getCounts(): StorageStats {
        return StorageStats(
            tasksCount = dao.getTasksCount(),
            projectsCount = dao.getProjectsCount(),
            notesCount = dao.getNotesCount(),
            promptsCount = dao.getPromptsCount(),
            filesCount = dao.getFilesCount()
        )
    }
}

data class StorageStats(
    val tasksCount: Int,
    val projectsCount: Int,
    val notesCount: Int,
    val promptsCount: Int,
    val filesCount: Int
)
