package com.midtermproject.mytaskmanagementApp.util;

/**
 * App-wide constants used throughout the application.
 * Centralizes all constant values for easy maintenance.
 */
public class Constants {

    // ========== DATABASE CONSTANTS ==========
    public static final String DATABASE_NAME = "task_manager_database";

    // ========== TASK PRIORITY LEVELS ==========
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_LOW = "LOW";

    // Array of all priorities (useful for spinners)
    public static final String[] ALL_PRIORITIES = {
            PRIORITY_LOW,
            PRIORITY_MEDIUM,
            PRIORITY_HIGH
    };

    // ========== DATE & TIME FORMATS ==========
    public static final String DATE_FORMAT_DB = "yyyy-MM-dd";          // Database format
    public static final String DATE_FORMAT_DISPLAY = "MMM dd, yyyy";   // Display format
    public static final String DATE_FORMAT_FULL = "EEEE, MMM dd, yyyy";// Full display
    public static final String TIME_FORMAT = "HH:mm";                  // Time format
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";  // DateTime format

    // ========== INTENT/BUNDLE EXTRAS ==========
    public static final String EXTRA_TASK_ID = "task_id";
    public static final String EXTRA_CATEGORY_ID = "category_id";
    public static final String EXTRA_TASK = "task_object";
    public static final String EXTRA_CATEGORY = "category_object";
    public static final String EXTRA_IS_EDIT = "is_edit_mode";

    // ========== NOTIFICATION CONSTANTS ==========
    public static final String CHANNEL_ID = "task_reminder_channel";
    public static final String CHANNEL_NAME = "Task Reminders";
    public static final int NOTIFICATION_ID = 1001;

    // ========== PREFERENCES/SHARED PREFS ==========
    public static final String PREF_NAME = "task_manager_prefs";
    public static final String PREF_FIRST_LAUNCH = "first_launch";
    public static final String PREF_SORT_ORDER = "sort_order";
    public static final String PREF_SHOW_COMPLETED = "show_completed";

    // ========== DEFAULT VALUES ==========
    public static final int DEFAULT_CATEGORY_ID = 1; // Assuming category 1 is "General"
    public static final String DEFAULT_PRIORITY = PRIORITY_MEDIUM;
    public static final int DEFAULT_NOTIFICATION_HOUR = 9; // 9 AM
    public static final int DEFAULT_NOTIFICATION_MINUTE = 0;

    // ========== VALIDATION CONSTANTS ==========
    public static final int MIN_TASK_NAME_LENGTH = 3;
    public static final int MAX_TASK_NAME_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 500;

    // ========== REQUEST CODES ==========
    public static final int REQUEST_CODE_ADD_TASK = 100;
    public static final int REQUEST_CODE_EDIT_TASK = 101;
    public static final int REQUEST_CODE_DATE_PICKER = 102;
    public static final int REQUEST_CODE_TIME_PICKER = 103;
}