package com.midtermproject.mytaskmanagementApp.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.midtermproject.mytaskmanagementApp.data.dao.CategoryDao;
import com.midtermproject.mytaskmanagementApp.data.database.AppDatabase;
import com.midtermproject.mytaskmanagementApp.data.model.Category;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryRepository {

    private CategoryDao categoryDao;
    private LiveData<List<Category>> allCategories;
    private ExecutorService executorService;

    public CategoryRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        categoryDao = database.categoryDao();
        allCategories = categoryDao.getAllCategories();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Category category) {
        executorService.execute(() -> {
            categoryDao.insert(category);
        });
    }

    public void update(Category category) {
        executorService.execute(() -> {
            categoryDao.update(category);
        });
    }

    public void delete(Category category) {
        executorService.execute(() -> {
            categoryDao.delete(category);
        });
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public LiveData<Category> getCategoryById(int id) {
        return categoryDao.getCategoryById(id);
    }

    public LiveData<List<Category>> searchCategories(String name) {
        return categoryDao.searchCategories(name);
    }

    public void deleteAllCategories() {
        executorService.execute(() -> {
            categoryDao.deleteAllCategories();
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
}