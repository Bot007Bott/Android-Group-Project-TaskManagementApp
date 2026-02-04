package com.midtermproject.mytaskmanagementApp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.midtermproject.mytaskmanagementApp.data.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    long insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories ORDER BY created_date DESC")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * FROM categories WHERE category_id = :id")
    LiveData<Category> getCategoryById(int id);

    @Query("SELECT * FROM categories WHERE category_name LIKE '%' || :name || '%'")
    LiveData<List<Category>> searchCategories(String name);

    @Query("DELETE FROM categories")
    void deleteAllCategories();
}