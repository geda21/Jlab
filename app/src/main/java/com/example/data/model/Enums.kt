package com.example.data.model

enum class Priority(val label: String, val level: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    CRITICAL("Critical", 4)
}

enum class TaskStatus(val label: String) {
    INBOX("Inbox"),
    PLANNED("Planned"),
    IN_PROGRESS("In Progress"),
    WAITING("Waiting"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

enum class ProjectStatus(val label: String) {
    IDEA("Idea"),
    PLANNING("Planning"),
    ACTIVE("Active"),
    PAUSED("Paused"),
    COMPLETED("Completed"),
    ARCHIVED("Archived")
}

enum class PlanType(val label: String) {
    DAILY("Daily Plan"),
    WEEKLY("Weekly Plan"),
    MONTHLY("Monthly Plan"),
    QUARTERLY("Quarterly Plan"),
    YEARLY("Yearly Plan"),
    CUSTOM("Custom Plan")
}

enum class NoteCategory(val label: String) {
    PERSONAL("Personal"),
    BUSINESS("Business"),
    DEVELOPMENT("Development"),
    IDEAS("Ideas"),
    LEARNING("Learning"),
    REFERENCE("Reference"),
    MEETING("Meeting"),
    OTHER("Other")
}

enum class PromptCategory(val label: String) {
    CODING("Coding"),
    BUSINESS("Business"),
    MARKETING("Marketing"),
    DESIGN("Design"),
    RESEARCH("Research"),
    CONTENT("Content"),
    WRITING("Writing"),
    AI_STUDIO("AI Studio"),
    PERSONAL("Personal"),
    OTHER("Other")
}

enum class ProductStatus(val label: String) {
    IDEA("Idea"),
    BUILDING("Building"),
    ACTIVE("Active"),
    PAUSED("Paused"),
    RETIRED("Retired")
}

enum class LearningType(val label: String) {
    COURSE("Course"),
    SKILL("Skill"),
    TOPIC("Topic"),
    BOOK("Book"),
    TUTORIAL("Tutorial"),
    PRACTICE("Practice"),
    RESEARCH("Research")
}

enum class LearningStatus(val label: String) {
    NOT_STARTED("Not Started"),
    LEARNING("Learning"),
    PRACTICING("Practicing"),
    COMPLETED("Completed"),
    PAUSED("Paused")
}

enum class SocialPlatform(val label: String) {
    TIKTOK("TikTok"),
    YOUTUBE("YouTube"),
    LINKEDIN("LinkedIn"),
    INSTAGRAM("Instagram"),
    FACEBOOK("Facebook"),
    TELEGRAM("Telegram"),
    X("X / Twitter"),
    OTHER("Other")
}

enum class SocialStatus(val label: String) {
    IDEA("Idea"),
    WRITING("Writing"),
    READY("Ready"),
    SCHEDULED("Scheduled"),
    PUBLISHED("Published"),
    ARCHIVED("Archived")
}

enum class GoalType(val label: String) {
    PERSONAL("Personal"),
    BUSINESS("Business"),
    FINANCIAL("Financial"),
    LEARNING("Learning"),
    PROJECT("Project"),
    HEALTH("Health"),
    CUSTOM("Custom")
}

enum class GoalStatus(val label: String) {
    ACTIVE("Active"),
    PAUSED("Paused"),
    ACHIEVED("Achieved"),
    CANCELLED("Cancelled")
}

enum class HabitFrequency(val label: String) {
    DAILY("Daily"),
    WEEKDAYS("Weekdays"),
    WEEKLY("Weekly"),
    CUSTOM("Custom")
}

enum class FocusRelatedType(val label: String) {
    TASK("Task"),
    PROJECT("Project"),
    LEARNING("Learning"),
    BUSINESS("Business"),
    GENERAL("General")
}
