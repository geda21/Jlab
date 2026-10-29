package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JlabDao {

    // ==========================================
    // TASKS
    // ==========================================
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND isArchived = 0 ORDER BY CASE priority WHEN 'CRITICAL' THEN 1 WHEN 'HIGH' THEN 2 WHEN 'MEDIUM' THEN 3 ELSE 4 END, dueDate ASC, createdAt DESC")
    fun getAllActiveTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND isArchived = 0 AND status != 'COMPLETED' AND status != 'CANCELLED' ORDER BY CASE priority WHEN 'CRITICAL' THEN 1 WHEN 'HIGH' THEN 2 WHEN 'MEDIUM' THEN 3 ELSE 4 END, dueDate ASC")
    fun getPendingTasks(): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks WHERE isDeleted = 0 AND isArchived = 0 AND status != 'COMPLETED' AND status != 'CANCELLED'")
    fun observePendingTaskCount(): Flow<Int>

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND isArchived = 0 AND status = 'COMPLETED' ORDER BY completedAt DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND isArchived = 0 AND dueDate <= :endOfDay AND status != 'COMPLETED' AND status != 'CANCELLED' ORDER BY CASE priority WHEN 'CRITICAL' THEN 1 WHEN 'HIGH' THEN 2 ELSE 3 END")
    fun getTodayTasks(endOfDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND isArchived = 0 AND dueDate < :startOfDay AND status != 'COMPLETED' AND status != 'CANCELLED'")
    fun getOverdueTasks(startOfDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND isArchived = 0 AND projectId = :projectId ORDER BY createdAt DESC")
    fun getTasksByProject(projectId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: String): TaskEntity?

    @Query("SELECT * FROM tasks WHERE reminderTimestamp IS NOT NULL AND reminderTimestamp > :now AND isDeleted = 0 AND isArchived = 0 AND status != 'COMPLETED'")
    suspend fun getUpcomingReminders(now: Long): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks SET status = 'COMPLETED', completedAt = :completedAt, updatedAt = :completedAt WHERE id = :id")
    suspend fun markTaskCompleted(id: String, completedAt: Long = System.currentTimeMillis())

    @Query("UPDATE tasks SET status = 'PLANNED', completedAt = NULL, updatedAt = :now WHERE id = :id")
    suspend fun reopenTask(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE tasks SET reminderTimestamp = :snoozeUntil WHERE id = :id")
    suspend fun snoozeTask(id: String, snoozeUntil: Long)

    @Query("UPDATE tasks SET isDeleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun moveTaskToTrash(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE tasks SET isArchived = 1, updatedAt = :now WHERE id = :id")
    suspend fun archiveTask(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE tasks SET isDeleted = 0, isArchived = 0, updatedAt = :now WHERE id = :id")
    suspend fun restoreTask(id: String, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskPermanently(id: String)

    // Subtasks
    @Query("SELECT * FROM subtasks WHERE taskId = :taskId ORDER BY sortOrder ASC")
    fun getSubtasksForTask(taskId: String): Flow<List<SubtaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtask(subtask: SubtaskEntity)

    @Update
    suspend fun updateSubtask(subtask: SubtaskEntity)

    @Delete
    suspend fun deleteSubtask(subtask: SubtaskEntity)

    // ==========================================
    // PROJECTS
    // ==========================================
    @Query("SELECT * FROM projects WHERE isDeleted = 0 AND isArchived = 0 ORDER BY isFavorite DESC, updatedAt DESC")
    fun getAllActiveProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("UPDATE projects SET isDeleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun moveProjectToTrash(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE projects SET isArchived = 1, updatedAt = :now WHERE id = :id")
    suspend fun archiveProject(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE projects SET isDeleted = 0, isArchived = 0, updatedAt = :now WHERE id = :id")
    suspend fun restoreProject(id: String, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectPermanently(id: String)

    // ==========================================
    // PLANS
    // ==========================================
    @Query("SELECT * FROM plans WHERE isDeleted = 0 AND isArchived = 0 ORDER BY createdAt DESC")
    fun getAllActivePlans(): Flow<List<PlanEntity>>

    @Query("SELECT * FROM plans WHERE id = :id LIMIT 1")
    suspend fun getPlanById(id: String): PlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: PlanEntity)

    @Update
    suspend fun updatePlan(plan: PlanEntity)

    @Query("UPDATE plans SET isLocked = 1, lockedAt = :lockedAt, updatedAt = :lockedAt WHERE id = :id")
    suspend fun lockPlan(id: String, lockedAt: Long = System.currentTimeMillis())

    @Query("UPDATE plans SET isLocked = 0, unlockedAt = :unlockedAt, unlockReason = :reason, updatedAt = :unlockedAt WHERE id = :id")
    suspend fun unlockPlan(id: String, reason: String, unlockedAt: Long = System.currentTimeMillis())

    @Query("UPDATE plans SET isDeleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun movePlanToTrash(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE plans SET isArchived = 1, updatedAt = :now WHERE id = :id")
    suspend fun archivePlan(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE plans SET isDeleted = 0, isArchived = 0, updatedAt = :now WHERE id = :id")
    suspend fun restorePlan(id: String, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM plans WHERE id = :id")
    suspend fun deletePlanPermanently(id: String)

    // Plan Items
    @Query("SELECT * FROM plan_items WHERE planId = :planId ORDER BY sortOrder ASC")
    fun getPlanItems(planId: String): Flow<List<PlanItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanItem(item: PlanItemEntity)

    @Update
    suspend fun updatePlanItem(item: PlanItemEntity)

    @Delete
    suspend fun deletePlanItem(item: PlanItemEntity)

    // ==========================================
    // NOTES
    // ==========================================
    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND isArchived = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllActiveNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND isArchived = 0 AND projectId = :projectId ORDER BY isPinned DESC, updatedAt DESC")
    fun getNotesByProject(projectId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: String): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("UPDATE notes SET isDeleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun moveNoteToTrash(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET isDeleted = 1, updatedAt = :now WHERE id IN (:ids)")
    suspend fun moveNotesToTrash(ids: List<String>, now: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET isArchived = 1, updatedAt = :now WHERE id = :id")
    suspend fun archiveNote(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET isArchived = 1, updatedAt = :now WHERE id IN (:ids)")
    suspend fun archiveNotes(ids: List<String>, now: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET category = :category, updatedAt = :now WHERE id IN (:ids)")
    suspend fun updateNotesCategory(ids: List<String>, category: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET isPinned = :isPinned, updatedAt = :now WHERE id IN (:ids)")
    suspend fun setNotesPinned(ids: List<String>, isPinned: Boolean, now: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET isFavorite = :isFavorite, updatedAt = :now WHERE id IN (:ids)")
    suspend fun setNotesFavorite(ids: List<String>, isFavorite: Boolean, now: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET isDeleted = 0, isArchived = 0, updatedAt = :now WHERE id = :id")
    suspend fun restoreNote(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET isDeleted = 0, isArchived = 0, updatedAt = :now WHERE id IN (:ids)")
    suspend fun restoreNotes(ids: List<String>, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNotePermanently(id: String)

    @Query("DELETE FROM notes WHERE id IN (:ids)")
    suspend fun deleteNotesPermanently(ids: List<String>)

    @Query("DELETE FROM notes WHERE isDeleted = 1")
    suspend fun emptyTrash()

    // ==========================================
    // INFORMATION / KNOWLEDGE VAULT
    // ==========================================
    @Query("SELECT * FROM information_vault WHERE isDeleted = 0 AND isArchived = 0 ORDER BY isPinned DESC, createdAt DESC")
    fun getAllActiveInformation(): Flow<List<InformationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInformation(info: InformationEntity)

    @Update
    suspend fun updateInformation(info: InformationEntity)

    @Query("UPDATE information_vault SET isDeleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun moveInformationToTrash(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE information_vault SET isArchived = 1, updatedAt = :now WHERE id = :id")
    suspend fun archiveInformation(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE information_vault SET isDeleted = 0, isArchived = 0, updatedAt = :now WHERE id = :id")
    suspend fun restoreInformation(id: String, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM information_vault WHERE id = :id")
    suspend fun deleteInformationPermanently(id: String)

    // ==========================================
    // KNOWLEDGE VAULT
    // ==========================================
    @Query("SELECT * FROM knowledge_vault WHERE isDeleted = 0 AND isArchived = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllActiveKnowledge(): Flow<List<KnowledgeEntity>>

    @Query("SELECT * FROM knowledge_vault WHERE id = :id LIMIT 1")
    suspend fun getKnowledgeById(id: String): KnowledgeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKnowledge(knowledge: KnowledgeEntity)

    @Update
    suspend fun updateKnowledge(knowledge: KnowledgeEntity)

    @Query("DELETE FROM knowledge_vault WHERE id = :id")
    suspend fun deleteKnowledgePermanently(id: String)

    // ==========================================
    // PROJECT DECISIONS
    // ==========================================
    @Query("SELECT * FROM project_decisions WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getDecisionsByProject(projectId: String): Flow<List<ProjectDecisionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectDecision(decision: ProjectDecisionEntity)

    @Update
    suspend fun updateProjectDecision(decision: ProjectDecisionEntity)

    @Query("DELETE FROM project_decisions WHERE id = :id")
    suspend fun deleteProjectDecision(id: String)

    // ==========================================
    // PROMPT LIBRARY
    // ==========================================
    @Query("SELECT * FROM prompts ORDER BY isFavorite DESC, updatedAt DESC")
    fun getAllPrompts(): Flow<List<PromptEntity>>

    @Query("SELECT * FROM prompts WHERE id = :id LIMIT 1")
    suspend fun getPromptById(id: String): PromptEntity?

    @Query("UPDATE prompts SET lastCopiedAt = :timestamp WHERE id = :id")
    suspend fun markPromptCopied(id: String, timestamp: Long = System.currentTimeMillis())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: PromptEntity)

    @Update
    suspend fun updatePrompt(prompt: PromptEntity)

    @Delete
    suspend fun deletePrompt(prompt: PromptEntity)

    // ==========================================
    // BUSINESS & PRODUCTS
    // ==========================================
    @Query("SELECT * FROM businesses WHERE isDeleted = 0 AND isArchived = 0 ORDER BY isFavorite DESC, updatedAt DESC")
    fun getAllBusinesses(): Flow<List<BusinessEntity>>

    @Query("SELECT * FROM businesses WHERE id = :id LIMIT 1")
    suspend fun getBusinessById(id: String): BusinessEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: BusinessEntity)

    @Update
    suspend fun updateBusiness(business: BusinessEntity)

    @Query("UPDATE businesses SET isDeleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun moveBusinessToTrash(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE businesses SET isArchived = 1, updatedAt = :now WHERE id = :id")
    suspend fun archiveBusiness(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE businesses SET isDeleted = 0, isArchived = 0, updatedAt = :now WHERE id = :id")
    suspend fun restoreBusiness(id: String, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM businesses WHERE id = :id")
    suspend fun deleteBusinessPermanently(id: String)

    @Query("SELECT * FROM products WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getProductsForBusiness(businessId: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    // ==========================================
    // LEARNING
    // ==========================================
    @Query("SELECT * FROM learning_items WHERE isDeleted = 0 AND isArchived = 0 ORDER BY isFavorite DESC, updatedAt DESC")
    fun getAllLearningItems(): Flow<List<LearningEntity>>

    @Query("SELECT * FROM learning_items WHERE id = :id LIMIT 1")
    suspend fun getLearningItemById(id: String): LearningEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearningItem(item: LearningEntity)

    @Update
    suspend fun updateLearningItem(item: LearningEntity)

    @Query("UPDATE learning_items SET isDeleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun moveLearningToTrash(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE learning_items SET isArchived = 1, updatedAt = :now WHERE id = :id")
    suspend fun archiveLearning(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE learning_items SET isDeleted = 0, isArchived = 0, updatedAt = :now WHERE id = :id")
    suspend fun restoreLearning(id: String, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM learning_items WHERE id = :id")
    suspend fun deleteLearningPermanently(id: String)

    // Curriculum: Modules
    @Query("SELECT * FROM learning_modules WHERE learningId = :learningId ORDER BY sortOrder ASC, createdAt ASC")
    fun getModulesForCourse(learningId: String): Flow<List<LearningModuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearningModule(module: LearningModuleEntity)

    @Update
    suspend fun updateLearningModule(module: LearningModuleEntity)

    @Query("DELETE FROM learning_modules WHERE id = :id")
    suspend fun deleteLearningModule(id: String)

    // Curriculum: Lessons
    @Query("SELECT * FROM learning_lessons WHERE learningId = :learningId ORDER BY sortOrder ASC, createdAt ASC")
    fun getLessonsForCourse(learningId: String): Flow<List<LearningLessonEntity>>

    @Query("SELECT * FROM learning_lessons WHERE moduleId = :moduleId ORDER BY sortOrder ASC, createdAt ASC")
    fun getLessonsForModule(moduleId: String): Flow<List<LearningLessonEntity>>

    @Query("SELECT * FROM learning_lessons ORDER BY updatedAt DESC")
    fun getAllLessons(): Flow<List<LearningLessonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearningLesson(lesson: LearningLessonEntity)

    @Update
    suspend fun updateLearningLesson(lesson: LearningLessonEntity)

    @Query("DELETE FROM learning_lessons WHERE id = :id")
    suspend fun deleteLearningLesson(id: String)

    @Query("UPDATE learning_lessons SET isCompleted = :isCompleted, completedAt = :completedAt, updatedAt = :now WHERE id = :id")
    suspend fun setLessonCompleted(id: String, isCompleted: Boolean, completedAt: Long?, now: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM learning_lessons WHERE isCompleted = 1")
    fun observeCompletedLessonsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM learning_lessons")
    fun observeTotalLessonsCount(): Flow<Int>

    // Learning Resources & Notes Relations
    @Query("SELECT * FROM notes WHERE learningId = :learningId AND isDeleted = 0 AND isArchived = 0 ORDER BY updatedAt DESC")
    fun getNotesByLearning(learningId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM file_attachments WHERE learningId = :learningId ORDER BY createdAt DESC")
    fun getFilesByLearning(learningId: String): Flow<List<FileAttachmentEntity>>

    // Business Relations
    @Query("SELECT * FROM tasks WHERE businessId = :businessId AND isDeleted = 0 AND isArchived = 0 ORDER BY CASE priority WHEN 'CRITICAL' THEN 1 WHEN 'HIGH' THEN 2 WHEN 'MEDIUM' THEN 3 ELSE 4 END, dueDate ASC")
    fun getTasksByBusiness(businessId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM projects WHERE businessId = :businessId AND isDeleted = 0 AND isArchived = 0 ORDER BY updatedAt DESC")
    fun getProjectsByBusiness(businessId: String): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM goals WHERE businessId = :businessId AND isDeleted = 0 AND isArchived = 0 ORDER BY targetDate ASC")
    fun getGoalsByBusiness(businessId: String): Flow<List<GoalEntity>>

    @Query("SELECT * FROM notes WHERE businessId = :businessId AND isDeleted = 0 AND isArchived = 0 ORDER BY updatedAt DESC")
    fun getNotesByBusiness(businessId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM file_attachments WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getFilesByBusiness(businessId: String): Flow<List<FileAttachmentEntity>>

    // ==========================================
    // SOCIAL CONTENT
    // ==========================================
    @Query("SELECT * FROM social_content WHERE isDeleted = 0 AND isArchived = 0 ORDER BY plannedDate ASC, createdAt DESC")
    fun getAllSocialContent(): Flow<List<SocialContentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSocialContent(content: SocialContentEntity)

    @Update
    suspend fun updateSocialContent(content: SocialContentEntity)

    @Query("UPDATE social_content SET isDeleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun moveSocialToTrash(id: String, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM social_content WHERE id = :id")
    suspend fun deleteSocialPermanently(id: String)

    // ==========================================
    // FILES / SCREENSHOTS VAULT
    // ==========================================
    @Query("SELECT * FROM file_attachments ORDER BY createdAt DESC")
    fun getAllFiles(): Flow<List<FileAttachmentEntity>>

    @Query("SELECT * FROM file_attachments WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getFilesByProject(projectId: String): Flow<List<FileAttachmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFileAttachment(file: FileAttachmentEntity)

    @Update
    suspend fun updateFileAttachment(file: FileAttachmentEntity)

    @Delete
    suspend fun deleteFileAttachment(file: FileAttachmentEntity)

    @Query("UPDATE file_attachments SET name = :name WHERE id = :id")
    suspend fun updateFileName(id: String, name: String)

    @Query("UPDATE file_attachments SET category = :category WHERE id = :id")
    suspend fun updateFileCategory(id: String, category: String)

    @Query("UPDATE file_attachments SET category = :category WHERE id IN (:ids)")
    suspend fun updateFilesCategory(ids: List<String>, category: String)

    @Query("UPDATE file_attachments SET projectId = :projectId, noteId = :noteId, taskId = :taskId, businessId = :businessId, learningId = :learningId WHERE id = :id")
    suspend fun linkFileTo(id: String, projectId: String?, noteId: String?, taskId: String?, businessId: String?, learningId: String?)

    @Query("UPDATE file_attachments SET projectId = :projectId, noteId = :noteId, taskId = :taskId, businessId = :businessId, learningId = :learningId WHERE id IN (:ids)")
    suspend fun linkFilesTo(ids: List<String>, projectId: String?, noteId: String?, taskId: String?, businessId: String?, learningId: String?)

    @Query("DELETE FROM file_attachments WHERE id = :id")
    suspend fun deleteFileById(id: String)

    @Query("DELETE FROM file_attachments WHERE id IN (:ids)")
    suspend fun deleteFilesByIds(ids: List<String>)

    // ==========================================
    // GOALS
    // ==========================================
    @Query("SELECT * FROM goals WHERE isDeleted = 0 AND isArchived = 0 ORDER BY targetDate ASC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    // ==========================================
    // HABITS
    // ==========================================
    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY createdAt ASC")
    fun getActiveHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("SELECT * FROM habit_logs WHERE dateStr = :dateStr")
    fun getHabitLogsForDate(dateStr: String): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId")
    fun getLogsForHabit(habitId: String): Flow<List<HabitLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun logHabit(log: HabitLogEntity)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND dateStr = :dateStr")
    suspend fun removeHabitLog(habitId: String, dateStr: String)

    // ==========================================
    // FOCUS SESSIONS & TIME ENTRIES
    // ==========================================
    @Query("SELECT * FROM focus_sessions ORDER BY startedAt DESC")
    fun getAllFocusSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE startedAt >= :sinceTimestamp ORDER BY startedAt DESC")
    fun getFocusSessionsSince(sinceTimestamp: Long): Flow<List<FocusSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSessionEntity)

    @Query("SELECT * FROM time_entries ORDER BY startTimestamp DESC")
    fun getAllTimeEntries(): Flow<List<TimeEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeEntry(entry: TimeEntryEntity)

    // ==========================================
    // DAILY & WEEKLY REVIEWS
    // ==========================================
    @Query("SELECT * FROM daily_reviews WHERE dateStr = :dateStr LIMIT 1")
    suspend fun getDailyReview(dateStr: String): DailyReviewEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyReview(review: DailyReviewEntity)

    @Query("SELECT * FROM weekly_reviews WHERE weekStr = :weekStr LIMIT 1")
    suspend fun getWeeklyReview(weekStr: String): WeeklyReviewEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyReview(review: WeeklyReviewEntity)

    // ==========================================
    // ACTIVITY LOGS
    // ==========================================
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentActivities(): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    // ==========================================
    // ARCHIVE & TRASH RECOVERY
    // ==========================================
    @Query("SELECT * FROM tasks WHERE isArchived = 1 AND isDeleted = 0")
    fun getArchivedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM projects WHERE isArchived = 1 AND isDeleted = 0")
    fun getArchivedProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM notes WHERE isArchived = 1 AND isDeleted = 0")
    fun getArchivedNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM tasks WHERE isDeleted = 1")
    fun getDeletedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM projects WHERE isDeleted = 1")
    fun getDeletedProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM notes WHERE isDeleted = 1")
    fun getDeletedNotes(): Flow<List<NoteEntity>>

    // Storage calculation
    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTasksCount(): Int

    @Query("SELECT COUNT(*) FROM projects")
    suspend fun getProjectsCount(): Int

    @Query("SELECT COUNT(*) FROM notes")
    suspend fun getNotesCount(): Int

    @Query("SELECT COUNT(*) FROM prompts")
    suspend fun getPromptsCount(): Int

    @Query("SELECT COUNT(*) FROM file_attachments")
    suspend fun getFilesCount(): Int
}
