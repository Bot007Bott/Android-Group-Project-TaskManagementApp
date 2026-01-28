package com.midtermproject.mytaskmanagementApp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.midtermproject.mytaskmanagementApp.R;
import com.midtermproject.mytaskmanagementApp.data.model.Task;
import com.midtermproject.mytaskmanagementApp.util.DateTimeUtil;
import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTaskName, tvDueDate, tvPriority;
        private CheckBox cbCompleted;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskName = itemView.findViewById(R.id.tv_task_name);
            tvDueDate = itemView.findViewById(R.id.tv_due_date);
            tvPriority = itemView.findViewById(R.id.tv_priority);
            cbCompleted = itemView.findViewById(R.id.cb_completed);
        }

        void bind(Task task) {
            tvTaskName.setText(task.getTaskName());
            tvDueDate.setText(DateTimeUtil.formatDateForDisplay(task.getDueDate()));
            tvPriority.setText(task.getPriority());
            cbCompleted.setChecked(task.isTaskCompleted());

            // TODO: Set priority color
            // TODO: Set click listeners
        }
    }
}