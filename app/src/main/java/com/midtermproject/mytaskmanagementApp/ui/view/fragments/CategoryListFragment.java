package com.midtermproject.mytaskmanagementApp.ui.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.midtermproject.mytaskmanagementApp.ui.view.activities.MainActivity;
import com.midtermproject.mytaskmanagementApp.ui.viewmodel.CategoryViewModel;
import com.midtermproject.mytaskmanagementApp.ui.viewmodel.TaskViewModel;
import java.util.List;
import android.widget.PopupMenu;
import android.widget.EditText;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class CategoryListFragment extends Fragment implements MainActivity.MenuCallback {

    private CategoryViewModel categoryViewModel;
    private TaskViewModel taskViewModel;
    private CategoryAdapter categoryAdapter;
    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddCategory;
    private LinearLayout searchContainer;
    private EditText etSearch;
    private Button btnClearSearch;

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

        searchContainer = view.findViewById(R.id.search_container);
        etSearch = view.findViewById(R.id.et_search);
        btnClearSearch = view.findViewById(R.id.btn_clear_search);

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

        searchContainer.setVisibility(View.GONE);
        setupSearch();
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

    public void toggleSearchBar() {
        if (searchContainer.getVisibility() == View.VISIBLE) {
            hideSearchBar();
        } else {
            searchContainer.setVisibility(View.VISIBLE);
            etSearch.requestFocus();
            showKeyboard();
        }
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

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                filterCategories(query);

                if (query.isEmpty()) {
                    btnClearSearch.setVisibility(View.GONE);
                } else {
                    btnClearSearch.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> {
            hideSearchBar();
        });
    }

    private void filterCategories(String query) {
        if (query.isEmpty()) {
            // Show all categories
            categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
                if (categories != null) {
                    categoryAdapter.setCategories(categories);
                }
            });
        } else {
            // Search categories
            categoryViewModel.searchCategories(query).observe(getViewLifecycleOwner(), categories -> {
                if (categories != null) {
                    categoryAdapter.setCategories(categories);

                    if (categories.isEmpty()) {
                        tvEmptyState.setText("No categories found for: " + query);
                        showEmptyState();
                    } else {
                        showCategoryList();
                    }
                }
            });
        }
    }

    private void hideSearchBar() {
        etSearch.setText("");
        searchContainer.setVisibility(View.GONE);
        btnClearSearch.setVisibility(View.GONE);
        hideKeyboard();

        // Reset to show all categories
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                categoryAdapter.setCategories(categories);
            }
        });
    }

    @Override
    public void showMenu(View anchorView) {
        PopupMenu popup = new PopupMenu(requireContext(), anchorView);
        popup.inflate(R.menu.menu_category_options);
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_cat_sort_tasks) {
                sortCategories();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && etSearch != null) {
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        }
    }

    private void sortCategories() {
        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
                if (categories != null && tasks != null) {
                    // Count tasks per category
                    java.util.Map<Integer, Integer> countMap = new java.util.HashMap<>();
                    for (com.midtermproject.mytaskmanagementApp.data.model.Task task : tasks) {
                        countMap.put(task.getCategoryId(),
                                countMap.getOrDefault(task.getCategoryId(), 0) + 1);
                    }

                    // Sort categories by task count descending
                    List<Category> sorted = new ArrayList<>(categories);
                    sorted.sort((a, b) -> {
                        int countA = countMap.getOrDefault(a.getCategoryId(), 0);
                        int countB = countMap.getOrDefault(b.getCategoryId(), 0);
                        return Integer.compare(countB, countA);
                    });

                    categoryAdapter.setCategories(sorted);
                    showCategoryList();
                }
            });
        });
    }
}