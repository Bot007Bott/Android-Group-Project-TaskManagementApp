package com.midtermproject.mytaskmanagementApp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.midtermproject.mytaskmanagementApp.data.model.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks ORDER BY due_date ASC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE category_id = :categoryId ORDER BY due_date ASC")
    LiveData<List<Task>> getTasksByCategory(int categoryId);

    @Query("SELECT * FROM tasks WHERE is_completed = 1 ORDER BY due_date DESC")
    LiveData<List<Task>> getCompletedTasks();

    @Query("SELECT * FROM tasks WHERE is_completed = 0 ORDER BY due_date ASC")
    LiveData<List<Task>> getPendingTasks();

    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY due_date ASC")
    LiveData<List<Task>> getTasksByPriority(String priority);

    @Query("SELECT * FROM tasks WHERE task_name LIKE '%' || :searchQuery || '%' ORDER BY due_date ASC")
    LiveData<List<Task>> searchTasks(String searchQuery);

    @Query("SELECT * FROM tasks WHERE due_date = :date AND is_completed = 0")
    LiveData<List<Task>> getTasksForToday(String date);

    @Query("DELETE FROM tasks")
    void deleteAllTasks();

    @Query("UPDATE tasks SET is_completed = :isCompleted WHERE task_id = :taskId")
    void updateTaskCompletion(int taskId, int isCompleted);

    @Query("DELETE FROM tasks WHERE is_completed = 1")
    void deleteAllCompletedTasks();
}