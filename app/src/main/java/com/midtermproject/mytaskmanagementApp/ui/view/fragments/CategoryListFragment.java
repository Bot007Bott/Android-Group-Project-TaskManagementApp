package com.midtermproject.mytaskmanagementApp.ui.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.midtermproject.mytaskmanagementApp.R;
import com.midtermproject.mytaskmanagementApp.data.model.Category;
import com.midtermproject.mytaskmanagementApp.ui.adapter.CategoryAdapter;
import com.midtermproject.mytaskmanagementApp.ui.viewmodel.CategoryViewModel;
import com.midtermproject.mytaskmanagementApp.ui.viewmodel.TaskViewModel;
import java.util.List;

public class CategoryListFragment extends Fragment {

    private CategoryViewModel categoryViewModel;
    private TaskViewModel taskViewModel;
    private CategoryAdapter categoryAdapter;
    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_categories);
        tvEmptyState = view.findViewById(R.id.tv_empty_state_categories);
        fabAddCategory = view.findViewById(R.id.fab_add_category);

        setupRecyclerView();

        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                categoryAdapter.setCategories(categories);
                showCategoryList();
            } else {
                showEmptyState();
            }
        });

        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null) {
                categoryAdapter.updateTaskCounts(tasks);
            }
        });

        fabAddCategory.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddCategoryFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(categoryAdapter);

        // EDIT: When category item is clicked, show edit dialog
        categoryAdapter.setOnItemClickListener(category -> {
            showEditCategoryDialog(category);
        });

        // DELETE: When delete button is clicked
        categoryAdapter.setOnDeleteClickListener(category -> {
            showDeleteConfirmationDialog(category);
        });
    }

    private void showEditCategoryDialog(Category category) {
        // Create dialog
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(requireContext());

        builder.setTitle("Edit Category");

        // Inflate custom dialog layout
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_edit_category, null);

        EditText etCategoryName = dialogView.findViewById(R.id.et_category_name);
        EditText etCategoryDescription = dialogView.findViewById(R.id.et_category_description);

        // Set current values
        etCategoryName.setText(category.getCategoryName());
        if (category.getDescription() != null) {
            etCategoryDescription.setText(category.getDescription());
        }

        builder.setView(dialogView);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = etCategoryName.getText().toString().trim();
            String newDescription = etCategoryDescription.getText().toString().trim();

            // Validate
            if (newName.isEmpty()) {
                Toast.makeText(requireContext(), "Category name cannot be empty",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Update category
            category.setCategoryName(newName);
            category.setDescription(newDescription);
            categoryViewModel.updateCategory(category);

            Toast.makeText(requireContext(), "Category updated", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        // Show dialog
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        // Make Enter key submit the dialog
        etCategoryName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).performClick();
                return true;
            }
            return false;
        });
    }

    private void showDeleteConfirmationDialog(Category category) {
        int taskCount = categoryAdapter.getTaskCountForCategory(category.getCategoryId());

        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(requireContext());

        builder.setTitle("Delete Category");

        if (taskCount > 0) {
            builder.setMessage("Delete '" + category.getCategoryName() + "'? " +
                    "This will also delete " + taskCount +
                    (taskCount == 1 ? " task" : " tasks") + " in this category.");
        } else {
            builder.setMessage("Delete '" + category.getCategoryName() + "'?");
        }

        builder.setPositiveButton("Delete", (dialog, which) -> {
            categoryViewModel.deleteCategory(category);
            Toast.makeText(requireContext(), "Category deleted", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showCategoryList() {
        recyclerView.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
    }
}