package com.midtermproject.mytaskmanagementApp.ui.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.midtermproject.mytaskmanagementApp.R;
import com.midtermproject.mytaskmanagementApp.data.model.Category;
import com.midtermproject.mytaskmanagementApp.ui.viewmodel.CategoryViewModel;
import com.midtermproject.mytaskmanagementApp.util.DateTimeUtil;

public class AddCategoryFragment extends Fragment {

    private EditText etCategoryName, etCategoryDescription;
    private Button btnSave, btnCancel;
    private CategoryViewModel categoryViewModel;
    private ImageView btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        etCategoryName = view.findViewById(R.id.et_category_name);
        etCategoryDescription = view.findViewById(R.id.et_category_description);
        btnSave = view.findViewById(R.id.btn_save_category);
        btnCancel = view.findViewById(R.id.btn_cancel_category);
        btnBack = view.findViewById(R.id.btn_back_category);

        // Initialize ViewModel
        categoryViewModel = new ViewModelProvider(requireActivity())
                .get(CategoryViewModel.class);

        // Set click listeners
        btnSave.setOnClickListener(v -> saveCategory());
        btnCancel.setOnClickListener(v -> {
            if (hasUnsavedChanges()) {
                showDiscardDialog();
            } else {
                goBack();
            }
        });
        btnBack.setOnClickListener(v -> {
            if (hasUnsavedChanges()) {
                showDiscardDialog();
            } else {
                goBack();
            }
        });
    }

    private void saveCategory() {
        String categoryName = etCategoryName.getText().toString().trim();
        String description = etCategoryDescription.getText().toString().trim();

        // Validation
        if (categoryName.isEmpty()) {
            etCategoryName.setError("Category name is required");
            return;
        }

        // Create category with description
        Category category = new Category(
                categoryName,
                DateTimeUtil.getCurrentDate(),
                description  // Include description
        );

        // Save to database
        categoryViewModel.insertCategory(category);

        // Show success message
        Toast.makeText(requireContext(), "Category added!", Toast.LENGTH_SHORT).show();

        // Go back
        goBack();
    }

    private boolean hasUnsavedChanges() {
        return !etCategoryName.getText().toString().trim().isEmpty() ||
                !etCategoryDescription.getText().toString().trim().isEmpty();
    }

    private void showDiscardDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Discard Changes?")
                .setMessage("You have unsaved changes. Are you sure you want to leave?")
                .setPositiveButton("Discard", (dialog, which) -> goBack())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void goBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}