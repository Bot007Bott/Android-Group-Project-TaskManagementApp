package com.midtermproject.mytaskmanagementApp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Factory for creating ViewModels with Application context.
 * Required if you want to pass parameters to ViewModel constructor.
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public ViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TaskViewModel.class)) {
            return (T) new TaskViewModel(application);
        } else if (modelClass.isAssignableFrom(CategoryViewModel.class)) {
            return (T) new CategoryViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}