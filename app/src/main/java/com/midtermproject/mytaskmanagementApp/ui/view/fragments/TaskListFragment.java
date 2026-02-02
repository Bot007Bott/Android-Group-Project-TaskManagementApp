package com.midtermproject.mytaskmanagementApp.ui.view.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.midtermproject.mytaskmanagementApp.R;
import com.midtermproject.mytaskmanagementApp.data.model.Task;
import com.midtermproject.mytaskmanagementApp.ui.adapter.TaskAdapter;
import com.midtermproject.mytaskmanagementApp.ui.view.activities.MainActivity;
import com.midtermproject.mytaskmanagementApp.ui.viewmodel.TaskViewModel;
import com.midtermproject.mytaskmanagementApp.util.Constants;
import com.midtermproject.mytaskmanagementApp.util.DateTimeUtil;
import com.midtermproject.mytaskmanagementApp.util.NotificationHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TaskListFragment extends Fragment implements MainActivity.MenuCallback {

    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;
    private TextView tvTotalTasks, tvDoneTasks, tvTodayTasks;
    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private Button btnAll, btnToday, btnWeek, btnOverdue;
    private FloatingActionButton fabAddTask;
    private EditText etSearch;
    private Button btnClearSearch;
    private LinearLayout searchContainer;

    private String currentFilter = "ALL";
    private List<com.midtermproject.mytaskmanagementApp.data.model.Task> allTasks = new ArrayList<>();

    private static Set<Integer> notifiedTaskIds = new HashSet<>();

    private static boolean dialogShownThisSession = false;

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
        searchContainer.setVisibility(View.GONE);
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

        searchContainer = view.findViewById(R.id.search_container);
        etSearch = view.findViewById(R.id.et_search);
        btnClearSearch = view.findViewById(R.id.btn_clear_search);

        setupButtonListeners();
        setupSearch();
        Button btnTestNotification = view.findViewById(R.id.btn_test_notification);
    }

    private void setupSearch() {
        // Live search as you type
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                filterTasks(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Clear button - clears search and hides search bar
        btnClearSearch.setOnClickListener(v -> {
            hideSearchBar();
        });
    }

    private void filterTasks(String query) {
        if (query.isEmpty()) {
            // Show original filtered list
            applyFilter(currentFilter);
        } else {
            // Filter tasks based on search query
            List<Task> filtered = new ArrayList<>();
            for (Task task : allTasks) {
                if (task.getTaskName().toLowerCase().contains(query.toLowerCase()) ||
                        (task.getTaskDescription() != null &&
                                task.getTaskDescription().toLowerCase().contains(query.toLowerCase()))) {
                    filtered.add(task);
                }
            }

            taskAdapter.setTasks(filtered);

            if (filtered.isEmpty()) {
                tvEmptyState.setText("No tasks found for: " + query);
                showEmptyState();
            } else {
                showTaskList();
            }
        }
    }

    private void hideSearchBar() {
        // Clear search text
        etSearch.setText("");

        // Hide search bar
        searchContainer.setVisibility(View.GONE);

        // Hide keyboard
        hideKeyboard();

        // Clear focus
        etSearch.clearFocus();

        // Show original filtered list
        applyFilter(currentFilter);
    }

    public void toggleSearchBar() {
        if (searchContainer.getVisibility() == View.VISIBLE) {
            hideSearchBar();
        } else {
            searchContainer.setVisibility(View.VISIBLE);
            etSearch.setText("");
            etSearch.requestFocus();
            showKeyboard();
        }
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && etSearch != null) {
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        }
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taskAdapter);

        taskAdapter.setOnItemClickListener(task -> {
            // Hide buttons
            View btnSearch = requireActivity().findViewById(R.id.btn_search);
            View btnMenu = requireActivity().findViewById(R.id.btn_menu);
            if (btnSearch != null) btnSearch.setVisibility(View.GONE);
            if (btnMenu != null) btnMenu.setVisibility(View.GONE);

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
                // Navigate to Completed tab
                BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
                bottomNav.setSelectedItemId(R.id.nav_completed);
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
    }

    private void observeTasks() {
        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null) {
                allTasks = tasks;
                applyFilter(currentFilter); // Apply current filter when data changes
                updateStats(tasks);

                checkAndShowDueTaskNotifications(tasks);

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
                for (Task task : allTasks) {
                    if (!task.isTaskCompleted()) {
                        filteredTasks.add(task);
                    }
                }
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

    private void updateFilterButtons(String activeFilter) {
        // Reset all buttons
        Button[] buttons = {btnAll, btnToday, btnWeek, btnOverdue};
        for (Button btn : buttons) {
            btn.setActivated(false);
            btn.setTextColor(getResources().getColor(R.color.purple_500));
        }

        Button activeButton;
        switch (activeFilter) {
            case "TODAY":
                activeButton = btnToday;
                break;
            case "WEEK":
                activeButton = btnWeek;
                break;
            case "OVERDUE":
                activeButton = btnOverdue;
                break;
            default:
                activeButton = btnAll;
                break;
        }
        activeButton.setActivated(true);
        activeButton.setTextColor(getResources().getColor(android.R.color.white));
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

    private void checkAndShowDueTaskNotifications(List<Task> tasks) {

        if (tasks == null || getContext() == null) {
            return;
        }

        List<Task> dueTodayTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.isTaskCompleted()) {
                continue;
            }

            if (DateTimeUtil.isToday(task.getDueDate())) {
                dueTodayTasks.add(task);
                if (!notifiedTaskIds.contains(task.getTaskId())) {
                    NotificationHelper.showDueTodayNotification(
                            requireContext(),
                            task.getTaskName(),
                            task.getTaskId()
                    );
                    notifiedTaskIds.add(task.getTaskId());
                }
            }
            if (DateTimeUtil.isOverdue(task.getDueDate())) {
                if (!notifiedTaskIds.contains(task.getTaskId())) {
                    NotificationHelper.showOverdueNotification(
                            requireContext(),
                            task.getTaskName(),
                            task.getTaskId()
                    );
                    notifiedTaskIds.add(task.getTaskId());
                }
            }
            if (DateTimeUtil.isTomorrow(task.getDueDate())) {
                if (!notifiedTaskIds.contains(task.getTaskId())) {
                    NotificationHelper.showTomorrowNotification(
                            requireContext(),
                            task.getTaskName(),
                            task.getTaskId()
                    );
                    notifiedTaskIds.add(task.getTaskId());
                }
            }
        }

        // SHOW DIALOG IF APP IS OPEN
        if (!dueTodayTasks.isEmpty() && isAdded() && !dialogShownThisSession) {
            showDueTasksDialog(dueTodayTasks);
            dialogShownThisSession = true;
        }
    }

    private void showDueTasksDialog(List<Task> dueTasks) {
        StringBuilder message = new StringBuilder("Tasks due today:\n\n");
        for (Task task : dueTasks) {
            message.append("• ").append(task.getTaskName()).append("\n");
        }

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("📅 Tasks Due Today")
                .setMessage(message.toString())
                .setPositiveButton("OK", null)
                .setNegativeButton("View Tasks", (dialog, which) -> {
                    // Switch to "Today" filter
                    applyFilter("TODAY");
                })
                .show();
    }

    @Override
    public void showMenu(View anchorView) {
        PopupMenu popup = new PopupMenu(requireContext(), anchorView);
        popup.inflate(R.menu.menu_task_options);
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_sort_date) {
                sortTasks("DATE");
            } else if (id == R.id.menu_sort_priority) {
                sortTasks("PRIORITY");
            }
            return true;
        });
        popup.show();
    }

    private void sortTasks(String type) {
        List<Task> sorted = new ArrayList<>(taskAdapter.getTasks());
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