package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.JlabApplication
import com.example.data.local.entity.*
import com.example.data.repository.JlabRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(
    private val repository: JlabRepository = JlabApplication.instance.repository
) : ViewModel() {

    private val preferences = JlabApplication.instance.preferences

    val allTasks = repository.allActiveTasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val todayTasks = repository.getTodayTasks().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val completedTasks = repository.completedTasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val pendingTaskCount = repository.pendingTaskCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allProjects = repository.allActiveProjects.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allPlans = repository.allActivePlans.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allNotes = repository.allActiveNotes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val deletedNotes = repository.deletedNotes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allPrompts = repository.allPrompts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allInfo = repository.allInformation.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allKnowledge = repository.allActiveKnowledge.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allBusinesses = repository.allBusinesses.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allLearning = repository.allLearningItems.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allLessons = repository.allLessons.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val completedLessonsCount = repository.completedLessonsCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val totalLessonsCount = repository.totalLessonsCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val allSocial = repository.allSocialContent.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allFiles = repository.allFiles.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allHabits = repository.activeHabits.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allGoals = repository.allGoals.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val focusSessions = repository.allFocusSessions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val recentActivities = repository.recentActivities.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isAppLockEnabled = preferences.isAppLockEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val isSecureScreenEnabled = preferences.isSecureScreenEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val isOnboardingCompleted = preferences.isOnboardingCompleted.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val themeMode = preferences.themeMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")
    val favoriteModules = preferences.favoriteModules.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())
    val recentModules = preferences.recentModules.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val workspaceViewMode = preferences.workspaceViewMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "LIST")

    val userName = preferences.userName.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Geda")
    val userBio = preferences.userBio.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Productivity Architect")
    val userAvatarUri = preferences.userAvatarUri.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val homeBannerUri = preferences.homeBannerUri.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val homeBannerPosition = preferences.homeBannerPosition.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "COVER")

    fun updateProfile(name: String, bio: String, avatarUri: String) = viewModelScope.launch {
        preferences.updateProfile(name, bio, avatarUri)
    }

    fun updateHomeBanner(bannerUri: String, position: String = "COVER") = viewModelScope.launch {
        preferences.updateHomeBanner(bannerUri, position)
    }

    fun clearHomeBanner() = viewModelScope.launch {
        preferences.clearHomeBanner()
    }

    fun toggleFavoriteModule(moduleId: String) = viewModelScope.launch {
        preferences.toggleFavoriteModule(moduleId)
    }

    fun recordModuleAccess(moduleId: String) = viewModelScope.launch {
        preferences.recordModuleAccess(moduleId)
    }

    fun setWorkspaceViewMode(mode: String) = viewModelScope.launch {
        preferences.setWorkspaceViewMode(mode)
    }

    private val todayDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val todayHabitLogs = repository.getHabitLogsForDate(todayDateStr).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Mutation helpers
    fun saveTask(task: TaskEntity) = viewModelScope.launch { repository.saveTask(task) }
    fun completeTask(id: String, title: String) = viewModelScope.launch { repository.completeTask(id, title) }
    fun reopenTask(id: String, title: String) = viewModelScope.launch { repository.reopenTask(id, title) }
    fun snoozeTask(id: String) = viewModelScope.launch { repository.snoozeTask(id, 10) }
    fun deleteTask(id: String) = viewModelScope.launch { repository.deleteTaskPermanently(id) }

    fun saveProject(project: ProjectEntity) = viewModelScope.launch { repository.saveProject(project) }
    fun updateProject(project: ProjectEntity) = viewModelScope.launch { repository.updateProject(project) }
    fun duplicateProject(id: String) = viewModelScope.launch { repository.duplicateProject(id) }
    fun archiveProject(id: String) = viewModelScope.launch { repository.archiveProject(id) }
    fun deleteProject(id: String) = viewModelScope.launch { repository.deleteProjectPermanently(id) }

    fun savePlan(plan: PlanEntity) = viewModelScope.launch { repository.savePlan(plan) }
    fun lockPlan(id: String, title: String) = viewModelScope.launch { repository.lockPlan(id, title) }
    fun unlockPlan(id: String, reason: String) = viewModelScope.launch { repository.unlockPlan(id, reason) }
    fun deletePlan(id: String) = viewModelScope.launch { repository.deletePlanPermanently(id) }

    fun saveNote(note: NoteEntity) = viewModelScope.launch { repository.saveNote(note) }
    fun createNote(note: NoteEntity) = viewModelScope.launch { repository.createNote(note) }
    fun updateNote(note: NoteEntity) = viewModelScope.launch { repository.updateNote(note) }
    fun moveNoteToTrash(id: String) = viewModelScope.launch { repository.moveNoteToTrash(id) }
    fun moveNotesToTrash(ids: List<String>) = viewModelScope.launch { repository.moveNotesToTrash(ids) }
    fun archiveNotes(ids: List<String>) = viewModelScope.launch { repository.archiveNotes(ids) }
    fun updateNotesCategory(ids: List<String>, category: String) = viewModelScope.launch { repository.updateNotesCategory(ids, category) }
    fun setNotesPinned(ids: List<String>, isPinned: Boolean) = viewModelScope.launch { repository.setNotesPinned(ids, isPinned) }
    fun setNotesFavorite(ids: List<String>, isFavorite: Boolean) = viewModelScope.launch { repository.setNotesFavorite(ids, isFavorite) }
    fun restoreNote(id: String) = viewModelScope.launch { repository.restoreNote(id) }
    fun restoreNotes(ids: List<String>) = viewModelScope.launch { repository.restoreNotes(ids) }
    fun deleteNotePermanently(id: String) = viewModelScope.launch { repository.deleteNotePermanently(id) }
    fun deleteNotesPermanently(ids: List<String>) = viewModelScope.launch { repository.deleteNotesPermanently(ids) }
    fun emptyTrash() = viewModelScope.launch { repository.emptyTrash() }

    fun saveInfo(info: InformationEntity) = viewModelScope.launch { repository.saveInformation(info) }
    fun deleteInfo(id: String) = viewModelScope.launch { repository.deleteInformationPermanently(id) }

    fun saveKnowledge(item: KnowledgeEntity) = viewModelScope.launch { repository.saveKnowledge(item) }
    fun deleteKnowledge(id: String) = viewModelScope.launch { repository.deleteKnowledgePermanently(id) }

    fun getDecisionsByProject(projectId: String) = repository.getDecisionsByProject(projectId)
    fun saveProjectDecision(decision: ProjectDecisionEntity) = viewModelScope.launch { repository.saveProjectDecision(decision) }
    fun deleteProjectDecision(id: String) = viewModelScope.launch { repository.deleteProjectDecision(id) }

    fun savePrompt(prompt: PromptEntity) = viewModelScope.launch { repository.savePrompt(prompt) }
    fun markPromptCopied(id: String) = viewModelScope.launch { repository.markPromptCopied(id) }
    fun deletePrompt(id: String) = viewModelScope.launch { repository.deletePrompt(prompt = id.let { PromptEntity(id = it, title = "", fullPrompt = "") }) }

    fun saveBusiness(biz: BusinessEntity) = viewModelScope.launch { repository.saveBusiness(biz) }
    fun deleteBusiness(id: String) = viewModelScope.launch { repository.deleteBusinessPermanently(id) }
    fun getTasksByBusiness(businessId: String) = repository.getTasksByBusiness(businessId)
    fun getProjectsByBusiness(businessId: String) = repository.getProjectsByBusiness(businessId)
    fun getGoalsByBusiness(businessId: String) = repository.getGoalsByBusiness(businessId)
    fun getNotesByBusiness(businessId: String) = repository.getNotesByBusiness(businessId)
    fun getFilesByBusiness(businessId: String) = repository.getFilesByBusiness(businessId)

    fun saveLearning(item: LearningEntity) = viewModelScope.launch { repository.saveLearningItem(item) }
    fun deleteLearning(id: String) = viewModelScope.launch { repository.deleteLearningPermanently(id) }
    fun getModulesForCourse(learningId: String) = repository.getModulesForCourse(learningId)
    fun saveLearningModule(module: LearningModuleEntity) = viewModelScope.launch { repository.saveLearningModule(module) }
    fun updateLearningModule(module: LearningModuleEntity) = viewModelScope.launch { repository.updateLearningModule(module) }
    fun deleteLearningModule(id: String) = viewModelScope.launch { repository.deleteLearningModule(id) }

    fun getLessonsForCourse(learningId: String) = repository.getLessonsForCourse(learningId)
    fun getLessonsForModule(moduleId: String) = repository.getLessonsForModule(moduleId)
    fun saveLearningLesson(lesson: LearningLessonEntity) = viewModelScope.launch {
        repository.saveLearningLesson(lesson)
        repository.recalculateCourseProgress(lesson.learningId)
    }
    fun updateLearningLesson(lesson: LearningLessonEntity) = viewModelScope.launch {
        repository.updateLearningLesson(lesson)
        repository.recalculateCourseProgress(lesson.learningId)
    }
    fun deleteLearningLesson(id: String, courseId: String) = viewModelScope.launch {
        repository.deleteLearningLesson(id)
        repository.recalculateCourseProgress(courseId)
    }
    fun setLessonCompleted(lessonId: String, courseId: String, isCompleted: Boolean) = viewModelScope.launch {
        repository.setLessonCompleted(lessonId, courseId, isCompleted)
    }
    fun recalculateCourseProgress(courseId: String) = viewModelScope.launch {
        repository.recalculateCourseProgress(courseId)
    }
    fun getNotesByLearning(learningId: String) = repository.getNotesByLearning(learningId)
    fun getFilesByLearning(learningId: String) = repository.getFilesByLearning(learningId)

    fun saveSocial(content: SocialContentEntity) = viewModelScope.launch { repository.saveSocialContent(content) }
    fun deleteSocial(id: String) = viewModelScope.launch { repository.deleteSocialPermanently(id) }

    fun saveFile(file: FileAttachmentEntity) = viewModelScope.launch { repository.saveFileAttachment(file) }
    fun updateFile(file: FileAttachmentEntity) = viewModelScope.launch { repository.updateFileAttachment(file) }
    fun deleteFile(file: FileAttachmentEntity) = viewModelScope.launch { repository.deleteFileAttachment(file) }
    fun updateFileName(id: String, name: String) = viewModelScope.launch { repository.updateFileName(id, name) }
    fun updateFileCategory(id: String, category: String) = viewModelScope.launch { repository.updateFileCategory(id, category) }
    fun updateFilesCategory(ids: List<String>, category: String) = viewModelScope.launch { repository.updateFilesCategory(ids, category) }
    fun linkFileTo(id: String, projectId: String?, noteId: String?, taskId: String?, businessId: String?, learningId: String?) =
        viewModelScope.launch { repository.linkFileTo(id, projectId, noteId, taskId, businessId, learningId) }
    fun linkFilesTo(ids: List<String>, projectId: String?, noteId: String?, taskId: String?, businessId: String?, learningId: String?) =
        viewModelScope.launch { repository.linkFilesTo(ids, projectId, noteId, taskId, businessId, learningId) }
    fun deleteFiles(ids: List<String>) = viewModelScope.launch { repository.deleteFilesByIds(ids) }

    fun saveHabit(habit: HabitEntity) = viewModelScope.launch { repository.saveHabit(habit) }
    fun toggleHabit(habitId: String, completed: Boolean) = viewModelScope.launch {
        repository.toggleHabitLog(habitId, todayDateStr, completed)
    }
    fun deleteHabit(id: String) = viewModelScope.launch { repository.deleteHabit(HabitEntity(id = id, title = "")) }

    fun saveGoal(goal: GoalEntity) = viewModelScope.launch { repository.saveGoal(goal) }
    fun deleteGoal(id: String) = viewModelScope.launch { repository.deleteGoal(GoalEntity(id = id, title = "")) }

    fun saveFocusSession(session: FocusSessionEntity) = viewModelScope.launch { repository.saveFocusSession(session) }

    fun saveDailyReview(review: DailyReviewEntity) = viewModelScope.launch { repository.saveDailyReview(review) }
    fun saveWeeklyReview(review: WeeklyReviewEntity) = viewModelScope.launch { repository.saveWeeklyReview(review) }

    fun setAppLock(enabled: Boolean) = viewModelScope.launch { preferences.setAppLockEnabled(enabled) }
    fun setSecureScreen(enabled: Boolean) = viewModelScope.launch { preferences.setSecureScreenEnabled(enabled) }
    fun completeOnboarding() = viewModelScope.launch { preferences.setOnboardingCompleted(true) }

    fun setThemeMode(mode: String) = viewModelScope.launch { preferences.setThemeMode(mode) }
    fun toggleThemeMode() = viewModelScope.launch {
        val current = themeMode.value
        val nextMode = if (current == "DARK") "LIGHT" else "DARK"
        preferences.setThemeMode(nextMode)
    }
    fun toggleTheme(isCurrentlyDark: Boolean) = viewModelScope.launch {
        val nextMode = if (isCurrentlyDark) "LIGHT" else "DARK"
        preferences.setThemeMode(nextMode)
    }
}
