package com.midtermproject.mytaskmanagementApp.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.midtermproject.mytaskmanagementApp.R;
import com.midtermproject.mytaskmanagementApp.ui.view.activities.MainActivity;


public class NotificationHelper {

    private static final String CHANNEL_ID = "task_reminders";
    private static final String CHANNEL_NAME = "Task Reminders";
    private static final String CHANNEL_DESCRIPTION = "Notifications for task reminders and due dates";

    public static void createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // HIGH importance for heads-up notifications
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);

            // Enable lights, vibration, and sound
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 250, 500});

            // Set lock screen visibility
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void showTaskReminder(Context context, String title, String message, int taskId) {

        // Create an intent for when notification is tapped
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.EXTRA_TASK_ID, taskId);

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(context, taskId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(context, taskId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Build notification for HEADS-UP (pop-up) display
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX) // CHANGE TO MAX
                .setCategory(NotificationCompat.CATEGORY_ALARM) // CHANGE TO ALARM (more intrusive)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setFullScreenIntent(pendingIntent, true); // FORCE heads-up ALWAYS

        // Show notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        boolean hasPermission = notificationManager.areNotificationsEnabled();

        try {
            notificationManager.notify(taskId, builder.build());
        } catch (SecurityException e) {
            // Permission not granted
        }
    }

    public static void showDueTodayNotification(Context context, String taskName, int taskId) {
        String title = "📅 Task Due Today";
        String message = "Don't forget: " + taskName;
        showTaskReminder(context, title, message, taskId);
    }

    public static void showOverdueNotification(Context context, String taskName, int taskId) {
        String title = "⚠️ Task Overdue!";
        String message = taskName + " is overdue. Complete it soon!";
        showTaskReminder(context, title, message, taskId);
    }

    public static void showTomorrowNotification(Context context, String taskName, int taskId) {
        String title = "📝 Task Due Tomorrow";
        String message = "Reminder: " + taskName + " is due tomorrow";
        showTaskReminder(context, title, message, taskId);
    }
}