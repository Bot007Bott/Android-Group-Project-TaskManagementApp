package com.midtermproject.mytaskmanagementApp.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for date and time operations.
 * Provides formatting, parsing, and date calculation methods.
 */
public class DateTimeUtil {
    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat DB_DATE_FORMAT =
            new SimpleDateFormat(Constants.DATE_FORMAT_DB, Locale.getDefault());

    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat DISPLAY_DATE_FORMAT =
            new SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.getDefault());

    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat FULL_DATE_FORMAT =
            new SimpleDateFormat(Constants.DATE_FORMAT_FULL, Locale.getDefault());
    /**
     * Get current date in database format (yyyy-MM-dd)
     * @return Current date as string
     */
    public static String getCurrentDate() {
        return DB_DATE_FORMAT.format(new Date());
    }

    /**
     * Get current date and time
     * @return Current datetime as string
     */
    public static String getCurrentDateTime() {
        return new SimpleDateFormat(Constants.DATE_TIME_FORMAT, Locale.getDefault())
                .format(new Date());
    }

    /**
     * Get current timestamp in milliseconds
     * @return Current timestamp
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
    /**
     * Format date from database format to display format
     * @param dateString Date in "yyyy-MM-dd" format
     * @return Formatted date string (e.g., "Jan 25, 2024")
     */
    public static String formatDateForDisplay(String dateString) {
        try {
            Date date = DB_DATE_FORMAT.parse(dateString);
            return DISPLAY_DATE_FORMAT.format(date);
        } catch (ParseException e) {
            return dateString; // Return original if parsing fails
        }
    }
    /**
     * Format date to full display format
     * @param dateString Date in "yyyy-MM-dd" format
     * @return Formatted date string (e.g., "Thursday, January 25, 2024")
     */
    public static String formatDateFull(String dateString) {
        try {
            Date date = DB_DATE_FORMAT.parse(dateString);
            return FULL_DATE_FORMAT.format(date);
        } catch (ParseException e) {
            return dateString;
        }
    }

    /**
     * Parse display format back to database format
     * @param displayDate Date in display format
     * @return Date in database format
     */
    public static String parseDisplayToDbFormat(String displayDate) {
        try {
            Date date = DISPLAY_DATE_FORMAT.parse(displayDate);
            return DB_DATE_FORMAT.format(date);
        } catch (ParseException e) {
            return displayDate;
        }
    }

    // ========== DATE CALCULATION METHODS ==========

    /**
     * Check if a due date has passed (is overdue)
     * @param dueDate Date in "yyyy-MM-dd" format
     * @return true if overdue, false otherwise
     */
    public static boolean isOverdue(String dueDate) {
        try {
            Date due = DB_DATE_FORMAT.parse(dueDate);
            Date today = DB_DATE_FORMAT.parse(getCurrentDate());
            return due.before(today);
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Check if date is today
     * @param dateString Date to check
     * @return true if date is today
     */
    public static boolean isToday(String dateString) {
        return dateString.equals(getCurrentDate());
    }

    /**
     * Check if date is tomorrow
     * @param dateString Date to check
     * @return true if date is tomorrow
     */
    public static boolean isTomorrow(String dateString) {
        String tomorrow = getDateDaysFromNow(1);
        return dateString.equals(tomorrow);
    }

    /**
     * Get date that is X days from now
     * @param days Number of days to add (can be negative)
     * @return Date in database format
     */
    public static String getDateDaysFromNow(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return DB_DATE_FORMAT.format(calendar.getTime());
    }

    /**
     * Calculate days between two dates
     * @param startDate Start date in database format
     * @param endDate End date in database format
     * @return Number of days between dates
     */
    public static int getDaysBetween(String startDate, String endDate) {
        try {
            Date start = DB_DATE_FORMAT.parse(startDate);
            Date end = DB_DATE_FORMAT.parse(endDate);
            long diff = end.getTime() - start.getTime();
            return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * Get days until due date
     * @param dueDate Due date in database format
     * @return Positive number = days remaining, Negative = days overdue
     */
    public static int getDaysUntilDue(String dueDate) {
        return getDaysBetween(getCurrentDate(), dueDate);
    }

    // ========== VALIDATION METHODS ==========

    /**
     * Validate date string format
     * @param dateString Date to validate
     * @return true if valid database format
     */
    public static boolean isValidDate(String dateString) {
        try {
            DB_DATE_FORMAT.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Check if date is in the future (including today)
     * @param dateString Date to check
     * @return true if date is today or in future
     */
    public static boolean isFutureDate(String dateString) {
        try {
            Date date = DB_DATE_FORMAT.parse(dateString);
            Date today = DB_DATE_FORMAT.parse(getCurrentDate());
            return !date.before(today); // Same day or later
        } catch (ParseException e) {
            return false;
        }
    }

    // ========== HELPER METHODS FOR UI ==========

    /**
     * Get priority color based on due date
     * @param dueDate Due date in database format
     * @return Priority string ("HIGH", "MEDIUM", "LOW")
     */
    public static String getPriorityByDueDate(String dueDate) {
        int daysUntilDue = getDaysUntilDue(dueDate);

        if (daysUntilDue < 0) {
            return Constants.PRIORITY_HIGH; // Overdue
        } else if (daysUntilDue <= 1) {
            return Constants.PRIORITY_HIGH; // Due today or tomorrow
        } else if (daysUntilDue <= 3) {
            return Constants.PRIORITY_MEDIUM; // Due in 2-3 days
        } else {
            return Constants.PRIORITY_LOW; // Due in 4+ days
        }
    }

    /**
     * Get human-readable time until due
     * @param dueDate Due date
     * @return String like "Today", "Tomorrow", "In 3 days", "Overdue by 2 days"
     */
    public static String getTimeUntilDueText(String dueDate) {
        if (isToday(dueDate)) {
            return "Today";
        } else if (isTomorrow(dueDate)) {
            return "Tomorrow";
        }

        int days = getDaysUntilDue(dueDate);

        if (days < 0) {
            return "Overdue by " + Math.abs(days) + " day" + (Math.abs(days) != 1 ? "s" : "");
        } else if (days == 0) {
            return "Today";
        } else {
            return "In " + days + " day" + (days != 1 ? "s" : "");
        }
    }

    /**
     * Check if date is within the next 7 days (this week)
     * @param dateString Date to check in "yyyy-MM-dd" format
     * @return true if date is within the next 7 days (including today)
     */
    public static boolean isWithinNextWeek(String dateString) {
        try {
            Date taskDate = DB_DATE_FORMAT.parse(dateString);
            Date today = DB_DATE_FORMAT.parse(getCurrentDate());

            // Add 7 days to today
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            Date weekLater = calendar.getTime();

            // Task is within next week if: today <= taskDate <= weekLater
            return (taskDate.equals(today) || taskDate.after(today)) &&
                    (taskDate.before(weekLater) || taskDate.equals(weekLater));
        } catch (ParseException e) {
            return false;
        }
    }
}