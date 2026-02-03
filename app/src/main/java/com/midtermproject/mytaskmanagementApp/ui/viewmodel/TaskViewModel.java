package com.midtermproject.mytaskmanagementApp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.midtermproject.mytaskmanagementApp.data.model.Task;
import com.midtermproject.mytaskmanagementApp.data.repository.TaskRepository;
import java.util.List;

/**
 * ViewModel for Task operations.
 * Connects TaskRepository with UI components (Fragments/Activities).
 * Survives configuration changes (screen rotation).
 */
public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository taskRepository;
    private final LiveData<List<Task>> allTasks;
    private final LiveData<List<Task>> pendingTasks;
    private final LiveData<List<Task>> completedTasks;

    private final MutableLiveData<List<Task>> searchResults = new MutableLiveData<>();

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
        allTasks = taskRepository.getAllTasks();
        pendingTasks = taskRepository.getPendingTasks();
        completedTasks = taskRepository.getCompletedTasks();
    }

    /**
     * Insert a new task
     * @param task The task to insert
     */
    public void insertTask(Task task) {
        taskRepository.insert(task);
    }

    /**
     * Update an existing task
     * @param task The task to update
     */
    public void updateTask(Task task) {
        taskRepository.update(task);
    }

    /**
     * Update task completion status
     * @param taskId The ID of the task to update
     * @param isCompleted New completion status
     */
    public void updateTaskCompletion(int taskId, boolean isCompleted) {
        taskRepository.updateTaskCompletion(taskId, isCompleted);
    }

    /**
     * Delete a task
     * @param task The task to delete
     */
    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }

    /**
     * Delete all tasks (use with caution!)
     */
    public void deleteAllTasks() {
        taskRepository.deleteAllTasks();
    }

    /**
     * Get all tasks ordered by due date
     * @return LiveData list of all tasks
     */
    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    /**
     * Get tasks by category
     * @param categoryId The category ID to filter by
     * @return LiveData list of tasks in that category
     */
    public LiveData<List<Task>> getTasksByCategory(int categoryId) {
        return taskRepository.getTasksByCategory(categoryId);
    }

    /**
     * Get pending (incomplete) tasks
     * @return LiveData list of pending tasks
     */
    public LiveData<List<Task>> getPendingTasks() {
        return pendingTasks;
    }

    /**
     * Get completed tasks
     * @return LiveData list of completed tasks
     */
    public LiveData<List<Task>> getCompletedTasks() {
        return taskRepository.getCompletedTasks();
    }

    /**
     * Get tasks by priority level
     * @param priority Priority level ("HIGH", "MEDIUM", "LOW")
     * @return LiveData list of tasks with that priority
     */
    public LiveData<List<Task>> getTasksByPriority(String priority) {
        return taskRepository.getTasksByPriority(priority);
    }

    /**
     * Get tasks due today
     * @param date Today's date in "yyyy-MM-dd" format
     * @return LiveData list of tasks due today
     */
    public LiveData<List<Task>> getTasksForToday(String date) {
        return taskRepository.getTasksForToday(date);
    }

    /**
     * Search tasks by name
     * @param query Search query string
     * @return LiveData list of matching tasks
     */
    public LiveData<List<Task>> searchTasks(String query) {
        return taskRepository.searchTasks(query);
    }

    /**
     * Get task by ID (from all tasks list)
     * Note: This is a helper method that filters from the allTasks list
     */
    public Task getTaskById(int taskId) {
        List<Task> tasks = allTasks.getValue();
        if (tasks != null) {
            for (Task task : tasks) {
                if (task.getTaskId() == taskId) {
                    return task;
                }
            }
        }
        return null;
    }
    /**
     * Delete all completed tasks permanently
     * Only affects tasks marked as completed (taskCompleted = true)
     * Pending tasks are not affected
     */
    public void deleteAllCompletedTasks() {
        taskRepository.deleteAllCompletedTasks();
    }
}