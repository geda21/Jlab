package com.example

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.service.ReminderScheduler
import com.example.ui.components.JlabFloatingActionButton
import com.example.ui.components.QuickCaptureSheet
import com.example.ui.screens.auth.AppLockScreen
import com.example.ui.screens.business.BusinessScreen
import com.example.ui.screens.calendar.CalendarScreen
import com.example.ui.screens.goals.GoalsScreen
import com.example.ui.screens.habits.HabitsScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.information.InformationVaultScreen
import com.example.ui.screens.insights.InsightsScreen
import com.example.ui.screens.knowledge.KnowledgeScreen
import com.example.ui.screens.learning.LearningScreen
import com.example.ui.screens.notes.NotesScreen
import com.example.ui.screens.notes.TrashNotesScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.plans.PlansScreen
import com.example.ui.screens.projects.ProjectDetailScreen
import com.example.ui.screens.projects.ProjectsScreen
import com.example.ui.screens.prompts.PromptsScreen
import com.example.ui.screens.reviews.ReviewsScreen
import com.example.ui.screens.search.GlobalSearchScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.social.SocialScreen
import com.example.ui.screens.tasks.TasksScreen
import com.example.ui.screens.vault.FilesVaultScreen
import com.example.ui.screens.workspace.WorkspaceScreen
import com.example.ui.theme.AppTheme
import com.example.ui.theme.JlabTheme
import com.example.ui.viewmodel.MainViewModel

enum class RootTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    WORKSPACE("Workspace", Icons.Default.Dashboard),
    CALENDAR("Calendar", Icons.Default.CalendarMonth),
    INSIGHTS("Insights", Icons.Default.Insights),
    SETTINGS("More", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }

            JlabTheme(themeMode = themeMode) {
                JlabAppRoot(viewModel = viewModel, themeMode = themeMode)
            }
        }
    }
}

@Composable
fun JlabAppRoot(
    viewModel: MainViewModel,
    themeMode: String = "SYSTEM"
) {
    val isOnboardingDone by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()
    val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsStateWithLifecycle()
    var isUnlocked by remember { mutableStateOf(!isAppLockEnabled) }

    LaunchedEffect(isAppLockEnabled) {
        if (!isAppLockEnabled) {
            isUnlocked = true
        }
    }

    if (!isOnboardingDone) {
        OnboardingScreen(onFinish = { viewModel.completeOnboarding() })
        return
    }

    if (isAppLockEnabled && !isUnlocked) {
        AppLockScreen(onUnlockSuccess = { isUnlocked = true })
        return
    }

    var currentTab by remember { mutableStateOf(RootTab.HOME) }
    var currentSubscreen by remember { mutableStateOf<String?>(null) }
    var selectedProjectId by remember { mutableStateOf<String?>(null) }
    var showQuickCaptureSheet by remember { mutableStateOf(false) }

    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val todayTasks by viewModel.todayTasks.collectAsStateWithLifecycle()
    val completedTasks by viewModel.completedTasks.collectAsStateWithLifecycle()
    val pendingTaskCount by viewModel.pendingTaskCount.collectAsStateWithLifecycle()
    val projects by viewModel.allProjects.collectAsStateWithLifecycle()
    val plans by viewModel.allPlans.collectAsStateWithLifecycle()
    val notes by viewModel.allNotes.collectAsStateWithLifecycle()
    val deletedNotes by viewModel.deletedNotes.collectAsStateWithLifecycle()
    val prompts by viewModel.allPrompts.collectAsStateWithLifecycle()
    val infoList by viewModel.allInfo.collectAsStateWithLifecycle()
    val knowledgeList by viewModel.allKnowledge.collectAsStateWithLifecycle()
    val businessList by viewModel.allBusinesses.collectAsStateWithLifecycle()
    val learningList by viewModel.allLearning.collectAsStateWithLifecycle()
    val allLessons by viewModel.allLessons.collectAsStateWithLifecycle()
    val socialList by viewModel.allSocial.collectAsStateWithLifecycle()
    val filesList by viewModel.allFiles.collectAsStateWithLifecycle()
    val habitsList by viewModel.allHabits.collectAsStateWithLifecycle()
    val todayHabitLogs by viewModel.todayHabitLogs.collectAsStateWithLifecycle()
    val goalsList by viewModel.allGoals.collectAsStateWithLifecycle()
    val activities by viewModel.recentActivities.collectAsStateWithLifecycle()
    val favoriteModules by viewModel.favoriteModules.collectAsStateWithLifecycle()
    val recentModules by viewModel.recentModules.collectAsStateWithLifecycle()
    val workspaceViewMode by viewModel.workspaceViewMode.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userBio by viewModel.userBio.collectAsStateWithLifecycle()
    val userAvatarUri by viewModel.userAvatarUri.collectAsStateWithLifecycle()
    val homeBannerUri by viewModel.homeBannerUri.collectAsStateWithLifecycle()
    val homeBannerPosition by viewModel.homeBannerPosition.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val resolvedIsDark = AppTheme.isDark

    val scheduleTestReminder: () -> Unit = {
        val inTenSeconds = System.currentTimeMillis() + 10_000L
        ReminderScheduler.scheduleReminder(
            context = context,
            taskId = "test_reminder",
            title = "JLab Focus Check: Review your daily commitments",
            triggerTimeMillis = inTenSeconds
        )
        Toast.makeText(context, "Test reminder scheduled for 10s!", Toast.LENGTH_SHORT).show()
    }

    if (currentSubscreen != null) {
        BackHandler {
            currentSubscreen = null
            selectedProjectId = null
        }
    }

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        bottomBar = {
            if (currentSubscreen == null) {
                NavigationBar(
                    containerColor = AppTheme.colorScheme.surface,
                    tonalElevation = AppTheme.spacing.none,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    RootTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = currentTab == tab,
                            onClick = { currentTab = tab },
                            icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                            label = {
                                Text(
                                    text = tab.label,
                                    style = AppTheme.typography.labelSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AppTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = AppTheme.colorScheme.primary,
                                indicatorColor = AppTheme.colorScheme.primaryContainer,
                                unselectedIconColor = AppTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = AppTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentSubscreen == null) {
                JlabFloatingActionButton(
                    icon = Icons.Default.Add,
                    contentDescription = "Quick Capture",
                    onClick = { showQuickCaptureSheet = true },
                    testTag = "main_quick_capture_fab"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (currentSubscreen != null) {
                when (currentSubscreen) {
                    "tasks" -> TasksScreen(
                        tasks = tasks,
                        projects = projects,
                        onSaveTask = { viewModel.saveTask(it) },
                        onCompleteTask = { id, title -> viewModel.completeTask(id, title) },
                        onReopenTask = { id, title -> viewModel.reopenTask(id, title) },
                        onSnoozeTask = { viewModel.snoozeTask(it) },
                        onDeleteTask = { viewModel.deleteTask(it) },
                        onNavigateBack = { currentSubscreen = null }
                    )
                    "projects" -> ProjectsScreen(
                        projects = projects,
                        onSaveProject = { viewModel.saveProject(it) },
                        onDeleteProject = { viewModel.deleteProject(it) },
                        onSelectProject = { id ->
                            selectedProjectId = id
                            currentSubscreen = "project_detail"
                        },
                        onNavigateBack = { currentSubscreen = null }
                    )
                    "project_detail" -> {
                        val proj = projects.find { it.id == selectedProjectId }
                        if (proj != null) {
                            val projectDecisions by viewModel.getDecisionsByProject(proj.id)
                                .collectAsStateWithLifecycle(emptyList())
                            ProjectDetailScreen(
                                project = proj,
                                tasks = tasks.filter { it.projectId == proj.id },
                                notes = notes.filter { it.projectId == proj.id },
                                files = filesList.filter { it.projectId == proj.id },
                                decisions = projectDecisions,
                                onUpdateProject = { viewModel.updateProject(it) },
                                onDuplicateProject = { viewModel.duplicateProject(it) },
                                onArchiveProject = { viewModel.archiveProject(it) },
                                onDeleteProject = { viewModel.deleteProject(it) },
                                onAddTask = { viewModel.saveTask(it) },
                                onUpdateTask = { viewModel.saveTask(it) },
                                onCompleteTask = { id, title -> viewModel.completeTask(id, title) },
                                onReopenTask = { id, title -> viewModel.reopenTask(id, title) },
                                onDeleteTask = { viewModel.deleteTask(it) },
                                onCreateNote = { viewModel.createNote(it) },
                                onDeleteNote = { viewModel.deleteNotePermanently(it) },
                                onOpenNote = { currentSubscreen = "notes" },
                                onSaveFile = { viewModel.saveFile(it) },
                                onDeleteFile = { viewModel.deleteFile(it) },
                                onSaveDecision = { viewModel.saveProjectDecision(it) },
                                onDeleteDecision = { viewModel.deleteProjectDecision(it) },
                                onNavigateBack = { currentSubscreen = "projects" }
                            )
                        } else {
                            currentSubscreen = "projects"
                        }
                    }
                    "plans" -> PlansScreen(
                        plans = plans,
                        onSavePlan = { viewModel.savePlan(it) },
                        onLockPlan = { id, title -> viewModel.lockPlan(id, title) },
                        onUnlockPlan = { id, reason -> viewModel.unlockPlan(id, reason) },
                        onDeletePlan = { viewModel.deletePlan(it) },
                        onNavigateBack = { currentSubscreen = null }
                    )
                    "notes" -> NotesScreen(
                        notes = notes,
                        deletedNotes = deletedNotes,
                        onCreateNote = { viewModel.createNote(it) },
                        onUpdateNote = { viewModel.updateNote(it) },
                        onMoveNoteToTrash = { viewModel.moveNoteToTrash(it) },
                        onMoveNotesToTrash = { viewModel.moveNotesToTrash(it) },
                        onArchiveNotes = { viewModel.archiveNotes(it) },
                        onUpdateNotesCategory = { ids, cat -> viewModel.updateNotesCategory(ids, cat) },
                        onSetNotesPinned = { ids, pin -> viewModel.setNotesPinned(ids, pin) },
                        onRestoreNote = { viewModel.restoreNote(it) },
                        onRestoreNotes = { viewModel.restoreNotes(it) },
                        onDeleteNotePermanently = { viewModel.deleteNotePermanently(it) },
                        onDeleteNotesPermanently = { viewModel.deleteNotesPermanently(it) },
                        onEmptyTrash = { viewModel.emptyTrash() },
                        onNavigateBack = { currentSubscreen = null }
                    )
                    "information" -> InformationVaultScreen(
                        items = infoList,
                        onSaveItem = { viewModel.saveInfo(it) },
                        onDeleteItem = { viewModel.deleteInfo(it) },
                        onNavigateBack = { currentSubscreen = null }
                    )
                    "knowledge" -> KnowledgeScreen(
                        items = knowledgeList,
                        projects = projects,
                        courses = learningList,
                        onSaveItem = { viewModel.saveKnowledge(it) },
                        onDeleteItem = { viewModel.deleteKnowledge(it) },
                        onNavigateBack = { currentSubscreen = null }
                    )
                    "prompts" -> PromptsScreen(
                        prompts = prompts,
                        onSavePrompt = { viewModel.savePrompt(it) },
                        onMarkCopied = { viewModel.markPromptCopied(it) },
                        onDeletePrompt = { viewModel.deletePrompt(it) },
                        onNavigateBack = { currentSubscreen = null }
                    )
                    "business" -> BusinessScreen(
                        businesses = businessList,
                        allTasks = tasks,
                        allProjects = projects,
                        allGoals = goalsList,
                        allNotes = notes,
                        allFiles = filesList,
                        activities = activities,
                        onSaveBusiness = { viewModel.saveBusiness(it) },
                        onDeleteBusiness = { viewModel.deleteBusiness(it) },
                        onSaveGoal = { viewModel.saveGoal(it) },
                        onDeleteGoal = { viewModel.deleteGoal(it) },
                        onSaveTask = { viewModel.saveTask(it) },
                        onCompleteTask = { id, title -> viewModel.completeTask(id, title) },
                        onDeleteTask = { viewModel.deleteTask(it) },
                        onSaveProject = { viewModel.saveProject(it) },
                        onOpenProject = { proj ->
                            selectedProjectId = proj.id
                            currentSubscreen = "project_detail"
                        },
                        onOpenNote = { currentSubscreen = "notes" },
                        onCreateBusinessNote = { currentSubscreen = "notes" },
                        onOpenResource = { currentSubscreen = "files" },
                        onAttachResource = { currentSubscreen = "files" },
                        onNavigateBack = { currentSubscreen = null }
                    )
                    "learning" -> LearningScreen(
                        items = learningList,
                        allLessons = allLessons,
                        notes = notes,
                        files = filesList,
                        getModulesForCourse = { id -> viewModel.getModulesForCourse(id) },
                        getLessonsForCourse = { id -> viewModel.getLessonsForCourse(id) },
                        onSaveItem = { viewModel.saveLearning(it) },
                        onDeleteItem = { viewModel.deleteLearning(it) },
                        onSaveModule = { viewModel.saveLearningModule(it) },
                        onDeleteModule = { viewModel.deleteLearningModule(it) },
                        onSaveLesson = { viewModel.saveLearningLesson(it) },
                        onDeleteLesson = { lessonId, courseId ->
                            viewModel.deleteLearningLesson(lessonId, courseId)
                        },
                        onToggleLessonCompleted = { lessonId, courseId, isCompleted ->
                            viewModel.setLessonCompleted(lessonId, courseId, isCompleted)
                        },
                        onOpenNote = { currentSubscreen = "notes" },
                        onCreateCourseNote = { currentSubscreen = "notes" },
                        onOpenResource = { currentSubscreen = "files" },
                        onAttachResource = { currentSubscreen = "files" },
                        onNavigateBack = { currentSubscreen = null }
                    )
                    "social" -> SocialScreen(
                        items = socialList,
                        onSaveItem = { viewModel.saveSocial(it) },
                        onDeleteItem = { viewModel.deleteSocial(it) },
                        onNavigateBack = { currentSubscreen = null }
                    )
                    "files" -> FilesVaultScreen(
                        files = filesList,
                        projects = projects,
                        learningItems = learningList,
                        businesses = businessList,
                        notes = notes,
                        onSaveFile = { viewModel.saveFile(it) },
                        onUpdateFile = { viewModel.updateFile(it) },
                        onDeleteFile = { viewModel.deleteFile(it) },
                        onUpdateFileName = { id, name -> viewModel.updateFileName(id, name) },
                        onUpdateFileCategory = { id, cat -> viewModel.updateFileCategory(id, cat) },
                        onUpdateFilesCategory = { ids, cat -> viewModel.updateFilesCategory(ids, cat) },
                        onLinkFile = { id, projId, noteId, taskId, bizId, learnId ->
                            viewModel.linkFileTo(id, projId, noteId, taskId, bizId, learnId)
                        },
                        onLinkFiles = { ids, projId, noteId, taskId, bizId, learnId ->
                            viewModel.linkFilesTo(ids, projId, noteId, taskId, bizId, learnId)
                        },
                        onDeleteFiles = { viewModel.deleteFiles(it) },
                        onNavigateBack = { currentSubscreen = null }
                    )
                    "habits" -> HabitsScreen(
                        habits = habitsList,
                        todayLogs = todayHabitLogs,
                        onSaveHabit = { viewModel.saveHabit(it) },
                        onToggleHabit = { id, done -> viewModel.toggleHabit(id, done) },
                        onDeleteHabit = { viewModel.deleteHabit(it) },
                        onNavigateBack = { currentSubscreen = null }
                    )
                    "goals" -> GoalsScreen(
                        goals = goalsList,
                        onSaveGoal = { viewModel.saveGoal(it) },
                        onDeleteGoal = { viewModel.deleteGoal(it) },
                        onNavigateBack = { currentSubscreen = null }
                    )
                    "reviews" -> ReviewsScreen(
                        completedTodayCount = todayTasks.count { it.status == "COMPLETED" },
                        onSaveDailyReview = { viewModel.saveDailyReview(it) },
                        onSaveWeeklyReview = { viewModel.saveWeeklyReview(it) },
                        onNavigateBack = { currentSubscreen = null }
                    )
                    "search" -> GlobalSearchScreen(
                        tasks = tasks,
                        projects = projects,
                        notes = notes,
                        prompts = prompts,
                        infoList = infoList,
                        learningList = learningList,
                        businessList = businessList,
                        onSelectResult = { res ->
                            when (res.type) {
                                "TASK" -> currentSubscreen = "tasks"
                                "PROJECT" -> {
                                    selectedProjectId = res.id
                                    currentSubscreen = "project_detail"
                                }
                                "NOTE" -> currentSubscreen = "notes"
                                "PROMPT" -> currentSubscreen = "prompts"
                                "INFORMATION" -> currentSubscreen = "information"
                                "LEARNING" -> currentSubscreen = "learning"
                                "BUSINESS" -> currentSubscreen = "business"
                            }
                        },
                        onNavigateBack = { currentSubscreen = null }
                    )
                    "trash" -> TrashNotesScreen(
                        deletedNotes = deletedNotes,
                        onRestoreNote = { viewModel.restoreNote(it) },
                        onRestoreNotes = { viewModel.restoreNotes(it) },
                        onDeletePermanently = { viewModel.deleteNotePermanently(it) },
                        onDeleteNotesPermanently = { viewModel.deleteNotesPermanently(it) },
                        onEmptyTrash = { viewModel.emptyTrash() },
                        onBack = { currentSubscreen = null }
                    )
                    "settings" -> SettingsScreen(
                        userName = userName,
                        userBio = userBio,
                        userAvatarUri = userAvatarUri,
                        homeBannerUri = homeBannerUri,
                        homeBannerPosition = homeBannerPosition,
                        onUpdateProfile = { name, bio, avatar -> viewModel.updateProfile(name, bio, avatar) },
                        onUpdateHomeBanner = { uri, pos -> viewModel.updateHomeBanner(uri, pos) },
                        onClearHomeBanner = { viewModel.clearHomeBanner() },
                        themeMode = themeMode,
                        onSelectThemeMode = { viewModel.setThemeMode(it) },
                        isAppLockEnabled = isAppLockEnabled,
                        onToggleAppLock = { viewModel.setAppLock(it) },
                        onTriggerTestReminder = scheduleTestReminder,
                        onNavigateBack = { currentSubscreen = null }
                    )
                    "insights" -> InsightsScreen(
                        completedTasksCount = completedTasks.size,
                        totalTasksCount = tasks.size,
                        pendingTasksCount = pendingTaskCount,
                        activeProjectsCount = projects.count { it.status == "ACTIVE" },
                        activities = activities,
                        completedTasks = completedTasks,
                        allTasks = tasks,
                        activeProjects = projects.filter { it.status == "ACTIVE" },
                        onNavigateBack = { currentSubscreen = null }
                    )
                    "calendar" -> CalendarScreen(
                        tasks = tasks,
                        onNavigateBack = { currentSubscreen = null }
                    )
                }
            } else {
                when (currentTab) {
                    RootTab.HOME -> HomeScreen(
                        userName = userName,
                        userAvatarUri = userAvatarUri,
                        homeBannerUri = homeBannerUri,
                        homeBannerPosition = homeBannerPosition,
                        todayTasks = todayTasks,
                        completedTasksCount = completedTasks.size,
                        pendingTasksCount = pendingTaskCount,
                        activeProjects = projects.filter { it.status == "ACTIVE" },
                        recentNotes = notes.take(5),
                        allPlansCount = plans.size,
                        allNotesCount = notes.size,
                        recentActivities = activities,
                        allTasks = tasks,
                        isDarkTheme = resolvedIsDark,
                        onToggleTheme = { viewModel.toggleThemeMode() },
                        onOpenProfile = { currentSubscreen = "settings" },
                        onQuickAdd = { showQuickCaptureSheet = true },
                        onCompleteTask = { id, title -> viewModel.completeTask(id, title) },
                        onReopenTask = { id, title -> viewModel.reopenTask(id, title) },
                        onSnoozeTask = { viewModel.snoozeTask(it) },
                        onNavigateToTasks = { currentSubscreen = "tasks" },
                        onNavigateToProjects = { currentSubscreen = "projects" },
                        onNavigateToNotes = { currentSubscreen = "notes" },
                        onNavigateToPlans = { currentSubscreen = "plans" },
                        onNavigateToFiles = { currentSubscreen = "files" },
                        onNavigateToSearch = { currentSubscreen = "search" },
                        onNavigateToProjectDetail = { id ->
                            selectedProjectId = id
                            currentSubscreen = "project_detail"
                        }
                    )
                    RootTab.WORKSPACE -> WorkspaceScreen(
                        tasksCount = tasks.size,
                        projectsCount = projects.size,
                        notesCount = notes.size,
                        plansCount = plans.size,
                        promptsCount = prompts.size,
                        learningCount = learningList.size,
                        businessCount = businessList.size,
                        socialCount = socialList.size,
                        filesCount = filesList.size,
                        habitsCount = habitsList.size,
                        goalsCount = goalsList.size,
                        infoCount = infoList.size,
                        knowledgeCount = knowledgeList.size,
                        deletedNotesCount = deletedNotes.size,
                        favoriteModuleIds = favoriteModules,
                        recentModuleIds = recentModules,
                        workspaceViewMode = workspaceViewMode,
                        onSetViewMode = { viewModel.setWorkspaceViewMode(it) },
                        onToggleFavorite = { viewModel.toggleFavoriteModule(it) },
                        isDarkTheme = resolvedIsDark,
                        onToggleTheme = { viewModel.toggleThemeMode() },
                        onNavigate = { screenId ->
                            viewModel.recordModuleAccess(screenId)
                            currentSubscreen = screenId
                        }
                    )
                    RootTab.CALENDAR -> CalendarScreen(
                        tasks = tasks,
                        onNavigateBack = { currentTab = RootTab.HOME }
                    )
                    RootTab.INSIGHTS -> InsightsScreen(
                        completedTasksCount = completedTasks.size,
                        totalTasksCount = tasks.size,
                        pendingTasksCount = pendingTaskCount,
                        activeProjectsCount = projects.count { it.status == "ACTIVE" },
                        activities = activities,
                        completedTasks = completedTasks,
                        allTasks = tasks,
                        activeProjects = projects.filter { it.status == "ACTIVE" }
                    )
                    RootTab.SETTINGS -> SettingsScreen(
                        userName = userName,
                        userBio = userBio,
                        userAvatarUri = userAvatarUri,
                        homeBannerUri = homeBannerUri,
                        homeBannerPosition = homeBannerPosition,
                        onUpdateProfile = { name, bio, avatar -> viewModel.updateProfile(name, bio, avatar) },
                        onUpdateHomeBanner = { uri, pos -> viewModel.updateHomeBanner(uri, pos) },
                        onClearHomeBanner = { viewModel.clearHomeBanner() },
                        themeMode = themeMode,
                        onSelectThemeMode = { viewModel.setThemeMode(it) },
                        isAppLockEnabled = isAppLockEnabled,
                        onToggleAppLock = { viewModel.setAppLock(it) },
                        onTriggerTestReminder = scheduleTestReminder
                    )
                }
            }
        }
    }

    if (showQuickCaptureSheet) {
        QuickCaptureSheet(
            onDismiss = { showQuickCaptureSheet = false },
            onSaveTask = { viewModel.saveTask(it) },
            onSaveNote = { viewModel.saveNote(it) },
            onSaveProject = { viewModel.saveProject(it) },
            onSavePlan = { viewModel.savePlan(it) },
            onSavePrompt = { viewModel.savePrompt(it) }
        )
    }
}
