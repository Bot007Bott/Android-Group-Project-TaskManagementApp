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
import java.util.ArrayList;
import java.util.List;
import android.text.Editable;
import android.text.TextWatcher;

public class TaskListFragment extends Fragment {

    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;
    private TextView tvTotalTasks, tvDoneTasks, tvTodayTasks;
    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private Button btnAll, btnToday, btnWeek, btnMore;
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

        // Initialize UI
        initViews(view);

        // Setup RecyclerView
        setupRecyclerView();

        // Get ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Observe tasks from database
        observeTasks();
    }

    private void initViews(View view) {
        tvTotalTasks = view.findViewById(R.id.tv_total_tasks);
        tvDoneTasks = view.findViewById(R.id.tv_done_tasks);
        tvTodayTasks = view.findViewById(R.id.tv_today_tasks);

        btnAll = view.findViewById(R.id.btn_filter_all);
        btnToday = view.findViewById(R.id.btn_filter_today);
        btnWeek = view.findViewById(R.id.btn_filter_week);
        btnMore = view.findViewById(R.id.btn_filter_more);

        recyclerView = view.findViewById(R.id.recycler_view_tasks);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        fabAddTask = view.findViewById(R.id.fab_add_task);

        // Search views
        etSearch = view.findViewById(R.id.et_search);
        btnClearSearch = view.findViewById(R.id.btn_clear_search);

        // Setup button clicks
        setupButtonListeners();
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taskAdapter);

        // Set click listener for task items
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

        // Set completion listener
        taskAdapter.setOnTaskCompletionListener((task, isCompleted) -> {
            taskViewModel.updateTaskCompletion(task.getTaskId(), isCompleted);
            if (isCompleted) {
                Toast.makeText(getContext(), "Task completed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupButtonListeners() {
        // FAB - Add new task
        fabAddTask.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddTaskFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Filter buttons
        btnAll.setOnClickListener(v -> applyFilter("ALL"));
        btnToday.setOnClickListener(v -> applyFilter("TODAY"));
        btnWeek.setOnClickListener(v -> applyFilter("WEEK"));
        btnMore.setOnClickListener(v -> applyFilter("MORE"));

        // Search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTasks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Show/hide clear button
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        // Clear search button
        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            btnClearSearch.setVisibility(View.GONE);
        });
    }

    private void observeTasks() {
        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null) {
                allTasks = tasks; // Store all tasks for filtering

                if (!tasks.isEmpty()) {
                    // Apply search filter if any
                    String searchQuery = etSearch.getText().toString().trim();
                    if (!searchQuery.isEmpty()) {
                        filterTasks(searchQuery);
                    } else {
                        taskAdapter.setTasks(tasks);
                    }

                    updateStats(tasks);
                    showTaskList();
                } else {
                    showEmptyState();
                }
            }
        });
    }

    private void filterTasks(String query) {
        if (allTasks.isEmpty()) return;

        if (query.isEmpty()) {
            // Show all tasks
            taskAdapter.setTasks(allTasks);
        } else {
            // Filter tasks by name or description
            List<com.midtermproject.mytaskmanagementApp.data.model.Task> filteredTasks = new ArrayList<>();
            String lowerQuery = query.toLowerCase();

            for (com.midtermproject.mytaskmanagementApp.data.model.Task task : allTasks) {
                if (task.getTaskName().toLowerCase().contains(lowerQuery) ||
                        task.getTaskDescription().toLowerCase().contains(lowerQuery)) {
                    filteredTasks.add(task);
                }
            }

            taskAdapter.setTasks(filteredTasks);

            // Show empty state if no results
            if (filteredTasks.isEmpty()) {
                tvEmptyState.setText("No tasks found for: " + query);
                showEmptyState();
            } else {
                showTaskList();
            }
        }
    }

    private void applyFilter(String filter) {
        currentFilter = filter;
        updateFilterButtons(filter);

        // TODO: Implement actual date filtering
        Toast.makeText(getContext(), "Filter: " + filter, Toast.LENGTH_SHORT).show();
    }

    private void updateFilterButtons(String activeFilter) {
        btnAll.setActivated("ALL".equals(activeFilter));
        btnToday.setActivated("TODAY".equals(activeFilter));
        btnWeek.setActivated("WEEK".equals(activeFilter));
        btnMore.setActivated("MORE".equals(activeFilter));
    }

    private void updateStats(List<com.midtermproject.mytaskmanagementApp.data.model.Task> tasks) {
        int total = tasks.size();
        int done = 0;
        int today = 0;

        for (com.midtermproject.mytaskmanagementApp.data.model.Task task : tasks) {
            if (task.isTaskCompleted()) {
                done++;
            }
            // TODO: Check if task is due today
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