package com.midtermproject.mytaskmanagementApp.util;

/**
 * Utility class for input validation.
 */
public class ValidationUtil {

    /**
     * Validate task name
     * @param taskName Name to validate
     * @return Error message if invalid, null if valid
     */
    public static String validateTaskName(String taskName) {
        if (taskName == null || taskName.trim().isEmpty()) {
            return "Task name is required";
        }

        if (taskName.trim().length() < Constants.MIN_TASK_NAME_LENGTH) {
            return "Task name must be at least " + Constants.MIN_TASK_NAME_LENGTH + " characters";
        }

        if (taskName.trim().length() > Constants.MAX_TASK_NAME_LENGTH) {
            return "Task name cannot exceed " + Constants.MAX_TASK_NAME_LENGTH + " characters";
        }

        return null; // No error
    }

    /**
     * Validate task description
     * @param description Description to validate
     * @return Error message if invalid, null if valid
     */
    public static String validateDescription(String description) {
        if (description != null && description.length() > Constants.MAX_DESCRIPTION_LENGTH) {
            return "Description cannot exceed " + Constants.MAX_DESCRIPTION_LENGTH + " characters";
        }
        return null;
    }

    /**
     * Validate due date
     * @param dueDate Date to validate
     * @return Error message if invalid, null if valid
     */
    public static String validateDueDate(String dueDate) {
        if (dueDate == null || dueDate.trim().isEmpty()) {
            return "Due date is required";
        }

        if (!DateTimeUtil.isValidDate(dueDate)) {
            return "Invalid date format. Use YYYY-MM-DD";
        }

        if (!DateTimeUtil.isFutureDate(dueDate)) {
            return "Due date cannot be in the past";
        }

        return null;
    }

    /**
     * Validate category name
     * @param categoryName Category name to validate
     * @return Error message if invalid, null if valid
     */
    public static String validateCategoryName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return "Category name is required";
        }

        if (categoryName.trim().length() < 2) {
            return "Category name must be at least 2 characters";
        }

        if (categoryName.trim().length() > 50) {
            return "Category name cannot exceed 50 characters";
        }

        return null;
    }
}