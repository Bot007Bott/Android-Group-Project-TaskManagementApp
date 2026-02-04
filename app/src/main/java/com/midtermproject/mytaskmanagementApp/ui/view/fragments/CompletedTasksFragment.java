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
import com.midtermproject.mytaskmanagementApp.data.model.Task;
import com.midtermproject.mytaskmanagementApp.ui.adapter.TaskAdapter;
import com.midtermproject.mytaskmanagementApp.ui.viewmodel.TaskViewModel;
import android.widget.PopupMenu;
import com.midtermproject.mytaskmanagementApp.ui.view.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class CompletedTasksFragment extends Fragment implements MainActivity.MenuCallback {

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

    @Override
    public void showMenu(View anchorView) {
        PopupMenu popup = new PopupMenu(requireContext(), anchorView);
        popup.getMenu().add(0, R.id.menu_sort_date, 0, "Sort by Date");
        popup.getMenu().add(0, R.id.menu_sort_priority, 0, "Sort by Priority");
        popup.getMenu().add(0, 999, 0, "Clear All Completed Tasks");

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_sort_date) {
                sortTasks("DATE");
                return true;
            } else if (id == R.id.menu_sort_priority) {
                sortTasks("PRIORITY");
                return true;
            } else {
                showClearAllDialog();
                return true;
            }
        });
        popup.show();
    }

    private void showClearAllDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Clear All Completed Tasks?")
                .setMessage("This will permanently delete all completed tasks.")
                .setPositiveButton("Clear", (dialog, which) -> {
                    taskViewModel.deleteAllCompletedTasks();
                    Toast.makeText(requireContext(), "All completed tasks cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sortTasks(String type) {
        List<Task> sorted = new ArrayList<>(taskAdapter.getTasks());

        if (sorted.isEmpty()) {
            return;
        }

        if (type.equals("DATE")) {
            sorted.sort((a, b) -> a.getDueDate().compareTo(b.getDueDate()));
        } else if (type.equals("PRIORITY")) {
            sorted.sort((a, b) -> {
                int orderA = getPriorityOrder(a.getPriority());
                int orderB = getPriorityOrder(b.getPriority());
                return Integer.compare(orderA, orderB);
            });
        }
        taskAdapter.setTasks(sorted);
        showTaskList();
    }

    private int getPriorityOrder(String priority) {
        switch (priority) {
            case "HIGH": return 0;
            case "MEDIUM": return 1;
            case "LOW": return 2;
            default: return 3;
        }
    }

}