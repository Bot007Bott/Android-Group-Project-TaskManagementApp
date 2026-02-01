package com.midtermproject.mytaskmanagementApp.ui.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.midtermproject.mytaskmanagementApp.R;
import com.midtermproject.mytaskmanagementApp.ui.adapter.TaskAdapter;
import com.midtermproject.mytaskmanagementApp.ui.viewmodel.TaskViewModel;

public class CompletedTasksFragment extends Fragment {

    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;
    private RecyclerView recyclerView;
    private TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_completed_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_completed_tasks);
        tvEmptyState = view.findViewById(R.id.tv_empty_state_completed);

        setupRecyclerView();

        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        // IMPORTANT: Observe ONLY COMPLETED tasks
        taskViewModel.getCompletedTasks().observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null && !tasks.isEmpty()) {
                taskAdapter.setTasks(tasks);
                showTaskList();
            } else {
                showEmptyState();
            }
        });
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taskAdapter);

        taskAdapter.setOnItemClickListener(task -> {
            Bundle args = new Bundle();
            args.putInt(com.midtermproject.mytaskmanagementApp.util.Constants.EXTRA_TASK_ID,
                    task.getTaskId());

            TaskDetailFragment fragment = new TaskDetailFragment();
            fragment.setArguments(args);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        taskAdapter.setOnTaskCompletionListener((task, isCompleted) -> {
            taskViewModel.updateTaskCompletion(task.getTaskId(), isCompleted);
            if (!isCompleted) {
                Toast.makeText(getContext(), "Task moved back to pending", Toast.LENGTH_SHORT).show();
                BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
                bottomNav.setSelectedItemId(R.id.nav_home);
            }
        });
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