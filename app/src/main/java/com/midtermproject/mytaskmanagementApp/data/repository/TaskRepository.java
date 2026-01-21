package com.midtermproject.mytaskmanagementApp.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.midtermproject.mytaskmanagementApp.data.dao.TaskDao;
import com.midtermproject.mytaskmanagementApp.data.database.AppDatabase;
import com.midtermproject.mytaskmanagementApp.data.model.Task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository {

    private TaskDao taskDao;
    private LiveData<List<Task>> allTasks;
    private ExecutorService executorService;

    public TaskRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        taskDao = database.taskDao();
        allTasks = taskDao.getAllTasks();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Task task) {
        executorService.execute(() -> {
            taskDao.insert(task);
        });
    }

    public void update(Task task) {
        executorService.execute(() -> {
            taskDao.update(task);
        });
    }

    public void delete(Task task) {
        executorService.execute(() -> {
            taskDao.delete(task);
        });
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<Task>> getTasksByCategory(int categoryId) {
        return taskDao.getTasksByCategory(categoryId);
    }

    public LiveData<List<Task>> getCompletedTasks() {
        return taskDao.getCompletedTasks();
    }

    public LiveData<List<Task>> getPendingTasks() {
        return taskDao.getPendingTasks();
    }

    public LiveData<List<Task>> getTasksByPriority(String priority) {
        return taskDao.getTasksByPriority(priority);
    }

    public LiveData<List<Task>> searchTasks(String searchQuery) {
        return taskDao.searchTasks(searchQuery);
    }

    public LiveData<List<Task>> getTasksForToday(String date) {
        return taskDao.getTasksForToday(date);
    }

    public void updateTaskCompletion(int taskId, boolean isCompleted) {
        executorService.execute(() -> {
            taskDao.updateTaskCompletion(taskId, isCompleted ? 1 : 0);
        });
    }

    public void deleteAllTasks() {
        executorService.execute(() -> {
            taskDao.deleteAllTasks();
        });
    }
}