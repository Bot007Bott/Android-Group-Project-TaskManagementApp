package com.midtermproject.mytaskmanagementApp.ui.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.midtermproject.mytaskmanagementApp.R;
import com.midtermproject.mytaskmanagementApp.data.model.Task;
import com.midtermproject.mytaskmanagementApp.ui.viewmodel.TaskViewModel;
import com.midtermproject.mytaskmanagementApp.util.Constants;
import com.midtermproject.mytaskmanagementApp.util.DateTimeUtil;
import com.midtermproject.mytaskmanagementApp.data.model.Category;
import com.midtermproject.mytaskmanagementApp.ui.viewmodel.CategoryViewModel;

public class TaskDetailFragment extends Fragment {

    private TextView tvTaskName, tvDescription, tvCategory, tvDueDate, tvPriority, tvCreatedDate, tvDaysLeft;
    private CheckBox cbCompleted;
    private Button btnEdit, btnDelete, btnBack;

    private TaskViewModel taskViewModel;
    private Task currentTask;
    private int taskId;
    private CategoryViewModel categoryViewModel;
    private String categoryName = "Loading...";

    private ImageView btn_arrow_back;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupViewModels();
        getTaskIdFromArguments();
        loadTaskDetails();
        setupClickListeners();
    }

    private void initViews(View view) {
        tvTaskName = view.findViewById(R.id.tv_task_name);
        tvDescription = view.findViewById(R.id.tv_description);
        tvCategory = view.findViewById(R.id.tv_category);
        tvDueDate = view.findViewById(R.id.tv_due_date);
        tvPriority = view.findViewById(R.id.tv_priority);
        tvCreatedDate = view.findViewById(R.id.tv_created_date);
        cbCompleted = view.findViewById(R.id.cb_completed);
        btnEdit = view.findViewById(R.id.btn_edit);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnBack = view.findViewById(R.id.btn_back);
        btn_arrow_back = view.findViewById(R.id.btn_back_detail_task);
        tvDaysLeft = view.findViewById(R.id.tv_days_left);
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

    private void loadTaskDetails() {
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
                        displayTaskDetails(task);
                        loadCategoryName(task.getCategoryId());
                        break;
                    }
                }
            }
        });
    }

    private void loadCategoryName(int categoryId) {
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                for (Category category : categories) {
                    if (category.getCategoryId() == categoryId) {
                        categoryName = category.getCategoryName();
                        tvCategory.setText(categoryName);
                        break;
                    }
                }
            }
        });
    }

    private void displayTaskDetails(Task task) {
        tvTaskName.setText(task.getTaskName());
        tvDescription.setText(task.getTaskDescription().isEmpty() ?
                "(No description)" : task.getTaskDescription());
        tvDueDate.setText(DateTimeUtil.formatDateForDisplay(task.getDueDate()));
        tvPriority.setText(task.getPriority());
        tvCreatedDate.setText("Created: " + DateTimeUtil.formatDateForDisplay(task.getCreatedDate()));
        cbCompleted.setChecked(task.isTaskCompleted());
        tvCategory.setText("Loading category...");
        if (task.getPriority().equals(Constants.PRIORITY_HIGH)) {
            tvPriority.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        } else if (task.getPriority().equals(Constants.PRIORITY_MEDIUM)) {
            tvPriority.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        } else {
            tvPriority.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        }
        String daysLeftText = DateTimeUtil.getTimeUntilDueText(task.getDueDate());
        tvDaysLeft.setText("Status: "+daysLeftText);
        if (DateTimeUtil.isOverdue(task.getDueDate())) {
            tvDaysLeft.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if (DateTimeUtil.isToday(task.getDueDate())) {
            tvDaysLeft.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            tvDaysLeft.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    private void setupClickListeners() {
        cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentTask != null) {
                taskViewModel.updateTaskCompletion(currentTask.getTaskId(), isChecked);
                Toast.makeText(requireContext(),
                        isChecked ? "Task completed!" : "Task marked as pending",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnEdit.setOnClickListener(v -> {
            if (currentTask != null) {
                Bundle args = new Bundle();
                args.putInt(Constants.EXTRA_TASK_ID, currentTask.getTaskId());
                EditTaskFragment fragment = new EditTaskFragment();
                fragment.setArguments(args);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        btnDelete.setOnClickListener(v -> {
            if (currentTask != null) {
                showDeleteConfirmDialog();
            }
        });
        btnBack.setOnClickListener(v -> goBack());
        btn_arrow_back.setOnClickListener(v->goBack());
    }

    private void showDeleteConfirmDialog() {
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
        View btnSearch = requireActivity().findViewById(R.id.btn_search);
        View btnMenu = requireActivity().findViewById(R.id.btn_menu);
        if (btnSearch != null) btnSearch.setVisibility(View.VISIBLE);
        if (btnMenu != null) btnMenu.setVisibility(View.VISIBLE);
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}