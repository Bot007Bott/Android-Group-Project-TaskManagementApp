package com.midtermproject.mytaskmanagementApp.ui.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

        // Initialize ViewModel
        categoryViewModel = new ViewModelProvider(requireActivity())
                .get(CategoryViewModel.class);

        // Set click listeners
        btnSave.setOnClickListener(v -> saveCategory());
        btnCancel.setOnClickListener(v -> goBack());
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

    private void goBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}