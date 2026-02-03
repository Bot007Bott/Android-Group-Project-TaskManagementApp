package com.midtermproject.mytaskmanagementApp.ui.view.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.midtermproject.mytaskmanagementApp.R;
import com.midtermproject.mytaskmanagementApp.data.model.Category;
import com.midtermproject.mytaskmanagementApp.data.model.Task;
import com.midtermproject.mytaskmanagementApp.ui.viewmodel.CategoryViewModel;
import com.midtermproject.mytaskmanagementApp.ui.viewmodel.TaskViewModel;
import com.midtermproject.mytaskmanagementApp.util.Constants;
import com.midtermproject.mytaskmanagementApp.util.DateTimeUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import androidx.appcompat.app.AlertDialog;

public class AddTaskFragment extends Fragment {

    private EditText etTaskName, etTaskDescription;
    private Spinner spinnerCategory;
    private Button btnDueDate, btnSave, btnCancel;
    private Button btnPriorityLow, btnPriorityMedium, btnPriorityHigh;

    private TaskViewModel taskViewModel;
    private CategoryViewModel categoryViewModel;

    private String selectedDueDate = "";
    private String selectedPriority = Constants.PRIORITY_MEDIUM;
    private List<Category> categoryList = new ArrayList<>();

    private ImageView btnBackIcon;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupViewModels();
        setupCategorySpinner();
        setupClickListeners();
        setupPriorityButtons();

        btnDueDate.setText("Select Due Date");
    }

    private void initViews(View view) {
        etTaskName = view.findViewById(R.id.et_task_name);
        etTaskDescription = view.findViewById(R.id.et_task_description);
        spinnerCategory = view.findViewById(R.id.spinner_category);

        btnPriorityLow = view.findViewById(R.id.btn_priority_low);
        btnPriorityMedium = view.findViewById(R.id.btn_priority_medium);
        btnPriorityHigh = view.findViewById(R.id.btn_priority_high);

        btnDueDate = view.findViewById(R.id.btn_due_date);
        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);

        btnBackIcon = view.findViewById(R.id.ic_back);
    }

    private void setupViewModels() {
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
    }

    private void setupCategorySpinner() {
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                categoryList = categories;
                List<String> categoryNames = new ArrayList<>();

                categoryNames.add("Select Category");
                categoryNames.add("+ Add New Category");

                for (Category category : categories) {
                    categoryNames.add(category.getCategoryName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        categoryNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);

                spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 1) {
                            openAddCategoryFragment();
                            spinnerCategory.setSelection(0);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        });
    }

    private void openAddCategoryFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AddCategoryFragment())
                .addToBackStack(null)
                .commit();
    }

    private void setupPriorityButtons() {
        setActivePriorityButton(btnPriorityMedium);

        btnPriorityLow.setOnClickListener(v -> {
            selectedPriority = Constants.PRIORITY_LOW;
            setActivePriorityButton(btnPriorityLow);
        });

        btnPriorityMedium.setOnClickListener(v -> {
            selectedPriority = Constants.PRIORITY_MEDIUM;
            setActivePriorityButton(btnPriorityMedium);
        });

        btnPriorityHigh.setOnClickListener(v -> {
            selectedPriority = Constants.PRIORITY_HIGH;
            setActivePriorityButton(btnPriorityHigh);
        });
    }

    private void setActivePriorityButton(Button activeButton) {
        btnPriorityLow.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        btnPriorityMedium.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        btnPriorityHigh.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        btnPriorityLow.setTextColor(getResources().getColor(android.R.color.black));
        btnPriorityMedium.setTextColor(getResources().getColor(android.R.color.black));
        btnPriorityHigh.setTextColor(getResources().getColor(android.R.color.black));

        activeButton.setBackgroundColor(getResources().getColor(R.color.purple_500));
        activeButton.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void setupClickListeners() {
        btnDueDate.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveTask());
        btnCancel.setOnClickListener(v -> {
            if (hasUnsavedChanges()) {
                showDiscardDialog();
            } else {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        btnBackIcon.setOnClickListener(v -> {
            if (hasUnsavedChanges()) {
                showDiscardDialog();
            } else {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDueDate = String.format("%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    btnDueDate.setText("Due: " + DateTimeUtil.formatDateForDisplay(selectedDueDate));
                },
                year, month, day
        );
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        datePicker.show();
    }

    private void saveTask() {
        String taskName = etTaskName.getText().toString().trim();
        String description = etTaskDescription.getText().toString().trim();
        String taskNameError = com.midtermproject.mytaskmanagementApp.util.ValidationUtil.validateTaskName(taskName);
        if (taskNameError != null) {
            etTaskName.setError(taskNameError);
            etTaskName.requestFocus();
            return;
        }
        if (selectedDueDate.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a due date", Toast.LENGTH_SHORT).show();
            return;
        }
        int selectedPosition = spinnerCategory.getSelectedItemPosition();
        if (selectedPosition <= 1) {
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }
        int categoryIndex = selectedPosition - 2;
        if (categoryIndex < 0 || categoryIndex >= categoryList.size()) {
            Toast.makeText(requireContext(), "Invalid category selection", Toast.LENGTH_SHORT).show();
            return;
        }
        int categoryId = categoryList.get(categoryIndex).getCategoryId();
        Task task = new Task(
                categoryId,
                taskName,
                description,
                selectedDueDate,
                selectedPriority,
                DateTimeUtil.getCurrentDate()
        );
        taskViewModel.insertTask(task);
        Toast.makeText(requireContext(), "Task saved!", Toast.LENGTH_SHORT).show();
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private boolean hasUnsavedChanges() {
        return !etTaskName.getText().toString().trim().isEmpty() ||
                !etTaskDescription.getText().toString().trim().isEmpty() ||
                !selectedDueDate.isEmpty() ||
                spinnerCategory.getSelectedItemPosition() > 1;
    }

    private void showDiscardDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Discard Changes?")
                .setMessage("You have unsaved changes. Are you sure you want to leave?")
                .setPositiveButton("Discard", (dialog, which) -> {
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}