package com.midtermproject.mytaskmanagementApp.ui.view.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
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

public class EditTaskFragment extends Fragment {

    private EditText etTaskName, etTaskDescription;
    private Spinner spinnerCategory;
    private Button btnDueDate, btnUpdate, btnDelete, btnCancel;
    private Button btnPriorityLow, btnPriorityMedium, btnPriorityHigh;
    private TaskViewModel taskViewModel;
    private CategoryViewModel categoryViewModel;
    private String selectedDueDate = "";
    private String selectedPriority = Constants.PRIORITY_MEDIUM;
    private List<Category> categoryList = new ArrayList<>();
    private Task currentTask;
    private int taskId;
    private ImageView btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupViewModels();
        getTaskIdFromArguments();
        loadTaskData();
        setupCategorySpinner();
        setupPriorityButtons();
        setupClickListeners();
    }

    private void initViews(View view) {
        etTaskName = view.findViewById(R.id.et_task_name);
        etTaskDescription = view.findViewById(R.id.et_task_description);
        spinnerCategory = view.findViewById(R.id.spinner_category);

        btnPriorityLow = view.findViewById(R.id.btn_priority_low);
        btnPriorityMedium = view.findViewById(R.id.btn_priority_medium);
        btnPriorityHigh = view.findViewById(R.id.btn_priority_high);

        btnDueDate = view.findViewById(R.id.btn_due_date);
        btnUpdate = view.findViewById(R.id.btn_update);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnBack = view.findViewById(R.id.btn_back_edit_task);
    }

    private void setupViewModels() {
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
    }

    private void getTaskIdFromArguments() {
        if (getArguments() != null) {
            taskId = getArguments().getInt(Constants.EXTRA_TASK_ID, -1);
        }
    }

    private void loadTaskData() {
        if (taskId == -1) {
            Toast.makeText(requireContext(), "Task not found", Toast.LENGTH_SHORT).show();
            goBack();
            return;
        }
        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null) {
                for (Task task : tasks) {
                    if (task.getTaskId() == taskId) {
                        currentTask = task;
                        populateForm(task);
                        break;
                    }
                }
            }
        });
    }

    private void populateForm(Task task) {
        etTaskName.setText(task.getTaskName());
        etTaskDescription.setText(task.getTaskDescription());
        selectedDueDate = task.getDueDate();
        btnDueDate.setText("Due: " + DateTimeUtil.formatDateForDisplay(selectedDueDate));

        selectedPriority = task.getPriority();
        if (selectedPriority.equals(Constants.PRIORITY_LOW)) {
            setActivePriorityButton(btnPriorityLow);
        } else if (selectedPriority.equals(Constants.PRIORITY_MEDIUM)) {
            setActivePriorityButton(btnPriorityMedium);
        } else {
            setActivePriorityButton(btnPriorityHigh);
        }
    }

    private void setupCategorySpinner() {
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                categoryList = categories;

                List<String> categoryNames = new ArrayList<>();
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

                if (currentTask != null) {
                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getCategoryId() == currentTask.getCategoryId()) {
                            spinnerCategory.setSelection(i);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void setupPriorityButtons() {
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
        btnUpdate.setOnClickListener(v -> updateTask());
        btnDelete.setOnClickListener(v -> showDeleteDialog());
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

    private void updateTask() {
        String taskName = etTaskName.getText().toString().trim();
        String description = etTaskDescription.getText().toString().trim();
        if (taskName.isEmpty()) {
            etTaskName.setError("Task name is required");
            return;
        }
        if (selectedDueDate.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a due date", Toast.LENGTH_SHORT).show();
            return;
        }
        int selectedPosition = spinnerCategory.getSelectedItemPosition();
        if (selectedPosition < 0 || selectedPosition >= categoryList.size()) {
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }
        int categoryId = categoryList.get(selectedPosition).getCategoryId();

        currentTask.setTaskName(taskName);
        currentTask.setTaskDescription(description);
        currentTask.setCategoryId(categoryId);
        currentTask.setDueDate(selectedDueDate);
        currentTask.setPriority(selectedPriority);

        taskViewModel.updateTask(currentTask);
        Toast.makeText(requireContext(), "Task updated!", Toast.LENGTH_SHORT).show();
        goBack();
    }

    private boolean hasUnsavedChanges() {
        if (currentTask == null) return false;

        String taskName = etTaskName.getText().toString().trim();
        String description = etTaskDescription.getText().toString().trim();

        return !taskName.equals(currentTask.getTaskName()) ||
                !description.equals(currentTask.getTaskDescription()) ||
                !selectedDueDate.equals(currentTask.getDueDate()) ||
                !selectedPriority.equals(currentTask.getPriority());
    }

    private void showDiscardDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Discard Changes?")
                .setMessage("You have unsaved changes. Are you sure you want to leave?")
                .setPositiveButton("Discard", (dialog, which) -> goBack())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Task?")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    taskViewModel.deleteTask(currentTask);
                    Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                    goBack();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void goBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}