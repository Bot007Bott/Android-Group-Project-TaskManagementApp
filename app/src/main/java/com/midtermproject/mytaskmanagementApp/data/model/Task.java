package com.midtermproject.mytaskmanagementApp.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "tasks",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "category_id",
                childColumns = "category_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("category_id")}
)
public class Task {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id")
    private int taskId;

    @ColumnInfo(name = "category_id")
    private int categoryId;

    @ColumnInfo(name = "task_name")
    private String taskName;

    @ColumnInfo(name = "task_description")
    private String taskDescription;

    @ColumnInfo(name = "due_date")
    private String dueDate;

    @ColumnInfo(name = "priority")
    private String priority;

    @ColumnInfo(name = "is_completed")
    private int isCompleted;

    @ColumnInfo(name = "created_date")
    private String createdDate;

    public Task() {}

    public Task(int categoryId, String taskName, String taskDescription,
                String dueDate, String priority, String createdDate) {
        this.categoryId = categoryId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.dueDate = dueDate;
        this.priority = priority;
        this.isCompleted = 0;
        this.createdDate = createdDate;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isTaskCompleted() {
        return isCompleted == 1;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed ? 1 : 0;
    }
}