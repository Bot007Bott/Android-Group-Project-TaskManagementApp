package com.midtermproject.mytaskmanagementApp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.midtermproject.mytaskmanagementApp.data.model.Category;
import com.midtermproject.mytaskmanagementApp.data.repository.CategoryRepository;
import java.util.List;

/**
 * ViewModel for Category operations.
 * Connects CategoryRepository with UI components.
 */
public class CategoryViewModel extends AndroidViewModel {

    private final CategoryRepository categoryRepository;
    private final LiveData<List<Category>> allCategories;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = new CategoryRepository(application);
        allCategories = categoryRepository.getAllCategories();
    }
    /**
     * Insert a new category
     * @param category The category to insert
     */
    public void insertCategory(Category category) {
        categoryRepository.insert(category);
    }

    /**
     * Update an existing category
     * @param category The category to update
     */
    public void updateCategory(Category category) {
        categoryRepository.update(category);
    }

    /**
     * Delete a category
     * @param category The category to delete
     */
    public void deleteCategory(Category category) {
        categoryRepository.delete(category);
    }

    /**
     * Delete all categories (use with caution!)
     */
    public void deleteAllCategories() {
        categoryRepository.deleteAllCategories();
    }

    /**
     * Get all categories
     * @return LiveData list of all categories
     */
    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    /**
     * Get category by ID
     * @param id The category ID
     * @return LiveData of the category
     */
    public LiveData<Category> getCategoryById(int id) {
        return categoryRepository.getCategoryById(id);
    }

    /**
     * Search categories by name
     * @param name Search query
     * @return LiveData list of matching categories
     */
    public LiveData<List<Category>> searchCategories(String name) {
        return categoryRepository.searchCategories(name);
    }

    /**
     * Get category name by ID (helper for UI)
     */
    public String getCategoryNameById(int categoryId) {
        List<Category> categories = allCategories.getValue();
        if (categories != null) {
            for (Category category : categories) {
                if (category.getCategoryId() == categoryId) {
                    return category.getCategoryName();
                }
            }
        }
        return "Uncategorized";
    }
}