package com.midtermproject.mytaskmanagementApp.util;
/**
 * App-wide constants used throughout the application.
 * Centralizes all constant values for easy maintenance.
 */
public class Constants {
    public static final String DATABASE_NAME = "task_manager_database";
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_LOW = "LOW";
    public static final String[] ALL_PRIORITIES = {
            PRIORITY_LOW,
            PRIORITY_MEDIUM,
            PRIORITY_HIGH
    };
    public static final String DATE_FORMAT_DB = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DISPLAY = "MMM dd, yyyy";
    public static final String DATE_FORMAT_FULL = "EEEE, MMM dd, yyyy";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String EXTRA_TASK_ID = "task_id";
    public static final String EXTRA_CATEGORY_ID = "category_id";
    public static final String EXTRA_TASK = "task_object";
    public static final String EXTRA_CATEGORY = "category_object";
    public static final String EXTRA_IS_EDIT = "is_edit_mode";
    public static final int MIN_TASK_NAME_LENGTH = 3;
    public static final int MAX_TASK_NAME_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 500;
}