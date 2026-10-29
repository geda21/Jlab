package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.JlabDao
import com.example.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        SubtaskEntity::class,
        ProjectEntity::class,
        PlanEntity::class,
        PlanItemEntity::class,
        NoteEntity::class,
        InformationEntity::class,
        PromptEntity::class,
        BusinessEntity::class,
        ProductEntity::class,
        LearningEntity::class,
        LearningModuleEntity::class,
        LearningLessonEntity::class,
        SocialContentEntity::class,
        FileAttachmentEntity::class,
        GoalEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        FocusSessionEntity::class,
        TimeEntryEntity::class,
        DailyReviewEntity::class,
        WeeklyReviewEntity::class,
        ActivityEntity::class,
        KnowledgeEntity::class,
        ProjectDecisionEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class JlabDatabase : RoomDatabase() {

    abstract fun jlabDao(): JlabDao

    companion object {
        @Volatile
        private var INSTANCE: JlabDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add rich formatting & count columns to notes
                db.execSQL("ALTER TABLE notes ADD COLUMN formattingJson TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE notes ADD COLUMN wordCount INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE notes ADD COLUMN charCount INTEGER NOT NULL DEFAULT 0")
                // Add lastCopiedAt to prompts
                db.execSQL("ALTER TABLE prompts ADD COLUMN lastCopiedAt INTEGER")
                // Remove starter demo prompts safely
                db.execSQL("DELETE FROM prompts WHERE title IN ('Code Architecture Review', 'Business Value Proposition', 'Structured Research Summary')")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Create learning_modules
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `learning_modules` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `learningId` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `description` TEXT NOT NULL DEFAULT '',
                        `sortOrder` INTEGER NOT NULL DEFAULT 0,
                        `createdAt` INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_learning_modules_learningId` ON `learning_modules` (`learningId`)")

                // 2. Create learning_lessons
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `learning_lessons` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `learningId` TEXT NOT NULL,
                        `moduleId` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `objective` TEXT NOT NULL DEFAULT '',
                        `theory` TEXT NOT NULL DEFAULT '',
                        `examples` TEXT NOT NULL DEFAULT '',
                        `practice` TEXT NOT NULL DEFAULT '',
                        `notes` TEXT NOT NULL DEFAULT '',
                        `isCompleted` INTEGER NOT NULL DEFAULT 0,
                        `sortOrder` INTEGER NOT NULL DEFAULT 0,
                        `completedAt` INTEGER,
                        `createdAt` INTEGER NOT NULL DEFAULT 0,
                        `updatedAt` INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_learning_lessons_learningId` ON `learning_lessons` (`learningId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_learning_lessons_moduleId` ON `learning_lessons` (`moduleId`)")

                // 3. Add relational columns to existing tables
                db.execSQL("ALTER TABLE tasks ADD COLUMN businessId TEXT")
                db.execSQL("ALTER TABLE tasks ADD COLUMN goalId TEXT")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_businessId` ON `tasks` (`businessId`)")

                db.execSQL("ALTER TABLE projects ADD COLUMN businessId TEXT")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_projects_businessId` ON `projects` (`businessId`)")

                db.execSQL("ALTER TABLE goals ADD COLUMN businessId TEXT")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_goals_businessId` ON `goals` (`businessId`)")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Create knowledge_vault
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `knowledge_vault` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `title` TEXT NOT NULL,
                        `category` TEXT NOT NULL DEFAULT 'Technical',
                        `summary` TEXT NOT NULL DEFAULT '',
                        `content` TEXT NOT NULL DEFAULT '',
                        `keyPointsJson` TEXT NOT NULL DEFAULT '[]',
                        `sourceReference` TEXT NOT NULL DEFAULT '',
                        `tags` TEXT NOT NULL DEFAULT '',
                        `relatedProjectId` TEXT,
                        `relatedCourseId` TEXT,
                        `relatedBusinessId` TEXT,
                        `isPinned` INTEGER NOT NULL DEFAULT 0,
                        `isFavorite` INTEGER NOT NULL DEFAULT 0,
                        `isArchived` INTEGER NOT NULL DEFAULT 0,
                        `isDeleted` INTEGER NOT NULL DEFAULT 0,
                        `createdAt` INTEGER NOT NULL DEFAULT 0,
                        `updatedAt` INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_knowledge_vault_category` ON `knowledge_vault` (`category`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_knowledge_vault_isArchived_isDeleted` ON `knowledge_vault` (`isArchived`, `isDeleted`)")

                // 2. Create project_decisions
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `project_decisions` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `projectId` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `description` TEXT NOT NULL DEFAULT '',
                        `reason` TEXT NOT NULL DEFAULT '',
                        `outcome` TEXT NOT NULL DEFAULT '',
                        `createdAt` INTEGER NOT NULL DEFAULT 0,
                        `updatedAt` INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_project_decisions_projectId` ON `project_decisions` (`projectId`)")

                // 3. Add columns to information_vault
                db.execSQL("ALTER TABLE information_vault ADD COLUMN summary TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE information_vault ADD COLUMN relatedEntityId TEXT")
                db.execSQL("ALTER TABLE information_vault ADD COLUMN relatedEntityType TEXT")

                // 4. Add columns to social_content
                db.execSQL("ALTER TABLE social_content ADD COLUMN callToAction TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE social_content ADD COLUMN publishedUrl TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add dual progress mode support to projects
                db.execSQL("ALTER TABLE projects ADD COLUMN manualProgressPercent INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE projects ADD COLUMN progressMode TEXT NOT NULL DEFAULT 'MANUAL'")
                // Pre-populate manualProgressPercent from existing progressPercent so no progress is lost
                db.execSQL("UPDATE projects SET manualProgressPercent = progressPercent")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE projects ADD COLUMN coverUri TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE projects ADD COLUMN avatarUri TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN imageUri TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE tasks ADD COLUMN avatarUri TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getInstance(context: Context): JlabDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JlabDatabase::class.java,
                    "jlab.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.jlabDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: JlabDao) {
                // Prompts are left 100% EMPTY by default for manual personal user capture!

                // Starter Productivity Routine
                dao.insertHabit(
                    HabitEntity(
                        title = "Daily Priority Execution",
                        frequency = "DAILY",
                        category = "Productivity",
                        targetDaysPerWeek = 7
                    )
                )

                // Log system init activity
                dao.insertActivity(
                    ActivityEntity(
                        actionType = "CREATED",
                        itemType = "SYSTEM",
                        itemId = "init",
                        title = "Welcome to Jlab",
                        summary = "Your offline life operating system is initialized and ready."
                    )
                )
            }
        }
    }
}
