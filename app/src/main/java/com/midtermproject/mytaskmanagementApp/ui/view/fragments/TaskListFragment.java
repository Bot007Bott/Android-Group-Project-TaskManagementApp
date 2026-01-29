package com.midtermproject.mytaskmanagementApp.ui.view.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.midtermproject.mytaskmanagementApp.ui.adapter.TaskAdapter;
import com.midtermproject.mytaskmanagementApp.ui.viewmodel.TaskViewModel;
import com.midtermproject.mytaskmanagementApp.util.Constants;
import com.midtermproject.mytaskmanagementApp.util.DateTimeUtil;
import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;
    private TextView tvTotalTasks, tvDoneTasks, tvTodayTasks;
    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private Button btnAll, btnToday, btnWeek, btnOverdue;
    private FloatingActionButton fabAddTask;
    private EditText etSearch;
    private Button btnClearSearch;

    private String currentFilter = "ALL";
    private List<com.midtermproject.mytaskmanagementApp.data.model.Task> allTasks = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        observeTasks();
    }

    private void initViews(View view) {
        tvTotalTasks = view.findViewById(R.id.tv_total_tasks);
        tvDoneTasks = view.findViewById(R.id.tv_done_tasks);
        tvTodayTasks = view.findViewById(R.id.tv_today_tasks);

        btnAll = view.findViewById(R.id.btn_filter_all);
        btnToday = view.findViewById(R.id.btn_filter_today);
        btnWeek = view.findViewById(R.id.btn_filter_week);
        btnOverdue = view.findViewById(R.id.btn_filter_more);

        recyclerView = view.findViewById(R.id.recycler_view_tasks);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        fabAddTask = view.findViewById(R.id.fab_add_task);

        etSearch = view.findViewById(R.id.et_search);
        btnClearSearch = view.findViewById(R.id.btn_clear_search);

        setupButtonListeners();
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taskAdapter);

        taskAdapter.setOnItemClickListener(task -> {
            Bundle args = new Bundle();
            args.putInt(Constants.EXTRA_TASK_ID, task.getTaskId());

            TaskDetailFragment fragment = new TaskDetailFragment();
            fragment.setArguments(args);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        taskAdapter.setOnTaskCompletionListener((task, isCompleted) -> {
            taskViewModel.updateTaskCompletion(task.getTaskId(), isCompleted);
            if (isCompleted) {
                Toast.makeText(getContext(), "Task completed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupButtonListeners() {
        fabAddTask.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddTaskFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnAll.setOnClickListener(v -> applyFilter("ALL"));
        btnToday.setOnClickListener(v -> applyFilter("TODAY"));
        btnWeek.setOnClickListener(v -> applyFilter("WEEK"));
        btnOverdue.setOnClickListener(v -> applyFilter("OVERDUE"));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTasks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            btnClearSearch.setVisibility(View.GONE);
            applyFilter(currentFilter); // Re-apply current filter
        });
    }

    private void observeTasks() {
        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null) {
                allTasks = tasks;
                applyFilter(currentFilter); // Apply current filter when data changes
                updateStats(tasks);

                // Show/hide empty state
                if (tasks.isEmpty()) {
                    showEmptyState();
                } else {
                    showTaskList();
                }
            }
        });
    }

    private void applyFilter(String filter) {
        currentFilter = filter;
        updateFilterButtons(filter);

        if (allTasks == null || allTasks.isEmpty()) {
            taskAdapter.setTasks(new ArrayList<>());
            showEmptyState();
            return;
        }

        List<com.midtermproject.mytaskmanagementApp.data.model.Task> filteredTasks = new ArrayList<>();
        String today = DateTimeUtil.getCurrentDate();

        switch (filter) {
            case "ALL":
                filteredTasks.addAll(allTasks);
                break;

            case "TODAY":
                for (com.midtermproject.mytaskmanagementApp.data.model.Task task : allTasks) {
                    if (DateTimeUtil.isToday(task.getDueDate()) && !task.isTaskCompleted()) {
                        filteredTasks.add(task);
                    }
                }
                break;

            case "WEEK":
                for (com.midtermproject.mytaskmanagementApp.data.model.Task task : allTasks) {
                    if (isDueThisWeek(task.getDueDate()) && !task.isTaskCompleted()) {
                        filteredTasks.add(task);
                    }
                }
                break;

            case "OVERDUE": // Show overdue
                for (com.midtermproject.mytaskmanagementApp.data.model.Task task : allTasks) {
                    if ((DateTimeUtil.isOverdue(task.getDueDate()) && !task.isTaskCompleted())) {
                        filteredTasks.add(task);
                    }
                }
                break;
        }

        // Apply search filter if there's a search query
        String searchQuery = etSearch.getText().toString().trim();
        if (!searchQuery.isEmpty()) {
            filteredTasks = filterBySearch(filteredTasks, searchQuery);
        }

        taskAdapter.setTasks(filteredTasks);

        // Update empty state text based on filter
        if (filteredTasks.isEmpty()) {
            String emptyText = getEmptyStateText(filter);
            tvEmptyState.setText(emptyText);
            showEmptyState();
        } else {
            showTaskList();
        }
    }

    private boolean isDueThisWeek(String dueDate) {
        try {
            // Get today's date
            String today = DateTimeUtil.getCurrentDate();

            // Get date 7 days from now
            String weekLater = DateTimeUtil.getDateDaysFromNow(7);

            // Check if dueDate is between today and weekLater
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.util.Date taskDate = sdf.parse(dueDate);
            java.util.Date todayDate = sdf.parse(today);
            java.util.Date weekLaterDate = sdf.parse(weekLater);

            // Task is due this week if: today <= dueDate <= weekLater
            return (taskDate.equals(todayDate) || taskDate.after(todayDate)) &&
                    (taskDate.before(weekLaterDate) || taskDate.equals(weekLaterDate));
        } catch (Exception e) {
            return false;
        }
    }

    private List<com.midtermproject.mytaskmanagementApp.data.model.Task> filterBySearch(
            List<com.midtermproject.mytaskmanagementApp.data.model.Task> tasks, String query) {
        List<com.midtermproject.mytaskmanagementApp.data.model.Task> result = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (com.midtermproject.mytaskmanagementApp.data.model.Task task : tasks) {
            if (task.getTaskName().toLowerCase().contains(lowerQuery) ||
                    (task.getTaskDescription() != null &&
                            task.getTaskDescription().toLowerCase().contains(lowerQuery))) {
                result.add(task);
            }
        }

        return result;
    }

    private String getEmptyStateText(String filter) {
        switch (filter) {
            case "TODAY":
                return "No tasks due today\n\nGreat job!";
            case "WEEK":
                return "No tasks due this week\n\nYou're on top of things!";
            case "OVERDUE":
                return "No overdue tasks\n\nEverything is under control!";
            default:
                return "No tasks yet\n\nCreate your first task";
        }
    }

    private void filterTasks(String query) {
        if (allTasks.isEmpty()) return;

        if (query.isEmpty()) {
            // If search is cleared, re-apply current filter
            applyFilter(currentFilter);
        } else {
            // Filter tasks by search query
            List<com.midtermproject.mytaskmanagementApp.data.model.Task> filteredTasks = filterBySearch(allTasks, query);
            taskAdapter.setTasks(filteredTasks);

            if (filteredTasks.isEmpty()) {
                tvEmptyState.setText("No tasks found for: " + query);
                showEmptyState();
            } else {
                showTaskList();
            }
        }
    }

    private void updateFilterButtons(String activeFilter) {
        // Reset all buttons
        btnAll.setActivated(false);
        btnToday.setActivated(false);
        btnWeek.setActivated(false);
        btnOverdue.setActivated(false);

        // Set active button
        switch (activeFilter) {
            case "ALL":
                btnAll.setActivated(true);
                break;
            case "TODAY":
                btnToday.setActivated(true);
                break;
            case "WEEK":
                btnWeek.setActivated(true);
                break;
            case "OVERDUE":
                btnOverdue.setActivated(true);
                break;
        }
    }

    private void updateStats(List<com.midtermproject.mytaskmanagementApp.data.model.Task> tasks) {
        int total = tasks.size();
        int done = 0;
        int today = 0;
        String currentDate = DateTimeUtil.getCurrentDate();

        for (com.midtermproject.mytaskmanagementApp.data.model.Task task : tasks) {
            if (task.isTaskCompleted()) {
                done++;
            }
            if (DateTimeUtil.isToday(task.getDueDate()) && !task.isTaskCompleted()) {
                today++;
            }
        }

        tvTotalTasks.setText(String.valueOf(total));
        tvDoneTasks.setText(String.valueOf(done));
        tvTodayTasks.setText(String.valueOf(today));
    }

    private void showTaskList() {
        recyclerView.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
    }
}