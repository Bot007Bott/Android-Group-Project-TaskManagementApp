package com.midtermproject.mytaskmanagementApp.ui.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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

public class TaskListFragment extends Fragment {

    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;
    private TextView tvTotalTasks, tvDoneTasks, tvTodayTasks;
    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private Button btnAll, btnToday, btnWeek, btnMore;
    private FloatingActionButton fabAddTask;  // CHANGED TO FloatingActionButton

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
        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null && !tasks.isEmpty()) {
                taskAdapter.setTasks(tasks);
                updateStats(tasks);
                showTaskList();
            } else {
                showEmptyState();
            }
        });
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
        fabAddTask = view.findViewById(R.id.fab_add_task);  // Now matches XML

        // Setup button clicks
        setupButtonListeners();
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taskAdapter);
    }

    private void setupButtonListeners() {
        fabAddTask.setOnClickListener(v -> {
            // Open AddTaskFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddTaskFragment())
                    .addToBackStack(null)  // So back button works
                    .commit();
        });

        btnAll.setOnClickListener(v -> applyFilter("ALL"));
        btnToday.setOnClickListener(v -> applyFilter("TODAY"));
        btnWeek.setOnClickListener(v -> applyFilter("WEEK"));
        btnMore.setOnClickListener(v -> applyFilter("MORE"));
    }

    private void applyFilter(String filter) {
        // Update button states
        updateFilterButtons(filter);

        // TODO: Filter tasks based on selection
        // For now, just show toast
        android.widget.Toast.makeText(getContext(), "Filter: " + filter,
                android.widget.Toast.LENGTH_SHORT).show();
    }

    private void updateFilterButtons(String activeFilter) {
        btnAll.setActivated("ALL".equals(activeFilter));
        btnToday.setActivated("TODAY".equals(activeFilter));
        btnWeek.setActivated("WEEK".equals(activeFilter));
        btnMore.setActivated("MORE".equals(activeFilter));
    }

    private void updateStats(java.util.List<com.midtermproject.mytaskmanagementApp.data.model.Task> tasks) {
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